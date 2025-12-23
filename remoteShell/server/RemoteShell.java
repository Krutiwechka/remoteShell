package remoteShell.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.TimeUnit;
public class RemoteShell extends UnicastRemoteObject implements RemoteShellInterface {
    protected RemoteShell() throws RemoteException{
        super();
    }
    @Override
    public void connect(String name) throws RemoteException {
        Server.serverLog("User " + name + " connected");
    }
    @Override
    public void disconnect(String name) throws RemoteException {
        Server.serverLog("User " + name + " disconnected");
    }
    @Override
    public String execute(String name, String command) throws RemoteException {
		Server.serverLog("User " + name + " executed: " + command);
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
	                return("Timed out after 10 seconds.");
	            }
	            
	            int exit = proc.exitValue();
	            if (exit != 0) {
                    Server.serverLog("Process exited with code " + exit + "\n" + output);
	                return("Process exited with code " + exit + "\n" + output);
	                
	            } else {
	                String out = output.toString();
	                if(!out.isEmpty())
	                    Server.serverLog(out);
                    return(out);
	            }
	                
	        } catch (IOException | InterruptedException e) {
	            Server.serverLog(e.getMessage());
	        	return(e.getMessage());
            }
	    } catch (IOException e) {

	        Server.serverLog("Startup Error: " + e.getMessage());
            return("Startup Error: " + e.getMessage());
	    } 
		finally {
	        if (proc != null && proc.isAlive()) {
	            proc.destroyForcibly();
	        }
	    }
    }
    
}
