package remoteShell.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import remoteShell.*;
public class ServerThread extends Thread{
	private Socket sock;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	String name;
	ServerThread(Socket sock) throws IOException{
		this.sock = sock;
		sock.setSoTimeout(1000);
		this.os = new ObjectOutputStream(sock.getOutputStream());
		this.is = new ObjectInputStream(sock.getInputStream());
		this.setDaemon(true);
	}
	public void run() {
		try {
			while(true) {
				Message msg = null;
				try {
					msg = (Message)this.is.readObject();
				} catch(IOException e) {}
				  catch(ClassNotFoundException e) {
					Server.serverLog(e.getMessage());
				}
				if(msg != null) {
					switch(msg.getType()) {
					case Connect: 
						connect(msg);
						break;
					case Execute:
						execute(msg);
						break;
					case Disconnect:
						disconnect();
						break;
					}
				}
			}
		}
		finally {
			if(!sock.isClosed())
				disconnect();
		}
	}
	private void connect(Message msg){
		ConnectMessage m = (ConnectMessage) msg;
		try {
			os.writeObject(new ConnectMessageResponse(""));
		} catch(IOException e) {
			Server.serverLog(e.getMessage());
		}
		this.name = m.username;
		Server.serverLog("User " + this.name + " connected");
	}
	private void disconnect(){
		try {
		os.close();
		is.close();
		sock.close();
		} catch(IOException e) {
			Server.serverLog(e.getMessage());
		}
		Server.serverLog("User " + this.name + " disconnected");
		this.interrupt();
	}
	private void execute(Message msg) {
		ExecuteMessage m = (ExecuteMessage)msg;
		String command = m.command;
		Server.serverLog("user " + this.name + " executed: " + command);
		String[] cmd = command.split(" ");
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);
		Process proc = null;
		StringBuilder output = new StringBuilder();
		try {
	        proc = pb.start();
	        
	        try(BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                output.append(line).append('\n');
	            }
	            
	            boolean finished = proc.waitFor(10, TimeUnit.SECONDS);
	            
	            if(!finished) {
	                proc.destroyForcibly();
	                os.writeObject(new ExecuteMessageResponse("Timed out after 10 seconds."));
	                return; 
	            }
	            
	            int exit = proc.exitValue();
	            if (exit != 0) {
	                os.writeObject(new ExecuteMessageResponse("Process exited with code " + exit + "\n" + output));
	                Server.serverLog("Process exited with code " + exit + "\n" + output);
	            } else {
	                String out = output.toString();
	                os.writeObject(new ExecuteMessageResponse(out));
	                if(!out.isEmpty())
	                    Server.serverLog(out);
	            }
	                
	        } catch (IOException | InterruptedException e) {
	            os.writeObject(new ExecuteMessageResponse(e.getMessage()));
	            Server.serverLog(e.getMessage());
	        }
	    } catch (IOException e) {
	        Server.serverLog("Startup Error: " + e.getMessage());
	        try {
	             os.writeObject(new ExecuteMessageResponse(e.getMessage()));
	        } catch (IOException ex) {
	            Server.serverLog(ex.getMessage());
	        }
	    } 
		finally {
	        if (proc != null && proc.isAlive()) {
	            proc.destroyForcibly();
	        }
	    }
	}
}
