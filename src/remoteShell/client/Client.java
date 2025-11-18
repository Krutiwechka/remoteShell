package remoteShell.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;

import remoteShell.*;

class Client {
    Client(Protocol protocol, String portStr, String address, String name) throws 
	IllegalArgumentException, RuntimeException, UnknownHostException {
		int port = parsePort(portStr);

		try (Socket sock = new Socket(address, port)) { 		
			System.err.println("Socket initialized");
			session(sock, name);
		} catch ( Exception e) {
			System.err.println("Unable to connect");
			throw new RuntimeException(e.getMessage());
		}
	}

	Client(Protocol protocol, String portStr, String name) throws 
	IllegalArgumentException, RuntimeException, UnknownHostException {
		this(protocol, portStr, InetAddress.getLocalHost().toString(), name);
	}

	
	static int parsePort(String portStr) throws IllegalArgumentException {
		try {
			return Integer.parseInt(portStr);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid port format " + portStr);
		}
	}

	// static void waitKeyToStop() {
	// 	System.err.println("Press a key to stop...");
	// 	try {
	// 		System.in.read();
	// 	} catch (IOException e) {
	// 	}
	// }
	
	static class Session {
		boolean connected = false;
		String username = null;
		Session(String name) {
			username = name;
		}
	}
	static void session(Socket s, String name) {
		try ( Scanner in = new Scanner(System.in);
			  ObjectInputStream is = new ObjectInputStream(s.getInputStream());
			  ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream())) {
			Session ses = new Session(name);
			if (openSession(ses, is, os, in)) { 
				try {
					while (true) {
						Message msg = getCommand(ses, in);
						if (!processCommand(ses, msg, is, os)) {
							break;
						}				
					}			
				} finally {
					closeSession(ses, os);
				}
			}
		} catch ( Exception e) {
			System.err.println(e);
		}
	}
	
	static boolean openSession(Session ses, ObjectInputStream is, ObjectOutputStream os, Scanner in) 
			throws IOException, ClassNotFoundException {
		os.writeObject( new ConnectMessage(ses.username));
		ConnectMessageResponse msg = (ConnectMessageResponse) is.readObject();
		if (msg.getErrorMessage() == "") {
			System.err.println("Connected");
			ses.connected = true;
			return true;
		}
		System.err.println("Unable to connect: "+ msg.getErrorMessage());
		System.err.println("Press <Enter> to continue...");
		if(in.hasNextLine())
			in.nextLine();
		return false;
	}
	
	static void printPrompt() {
		System.out.println("Not designed");
	}

	static void closeSession(Session ses, ObjectOutputStream os) throws IOException {
		if ( ses.connected ) {
			ses.connected = false;
			os.writeObject(new DisconnectMessage(ses.username));
		}
	}

	static MessageType strToMessage(String line) {
		if (line == "" || line == null)
			return null;
		StringTokenizer tokenizer = new StringTokenizer(line);
		String command = tokenizer.nextToken();

		if (commands.containsKey(command))
			return commands.get(command);
		return null;
	}

	static Message getCommand(Session ses, Scanner in) {	
		while (true) {
			printPrompt();
			if (in.hasNextLine()== false)
				break;
			String str = in.nextLine();
			MessageType action = strToMessage(str);
			switch (action) {
				case Connect: {
					return new ConnectMessage(ses.username);
				}
				case Disconnect:
					return new DisconnectMessage(ses.username);
				case Execute: {
					return new ExecuteMessage(str);
				}
				default: 
					System.err.println("Unknown command!");
					continue;
			}
		}
		return null;
	}
	
	
	static TreeMap<String,MessageType> commands = new TreeMap<String,MessageType>();
	static {
		commands.put("q", MessageType.Disconnect);
		commands.put("quit", MessageType.Disconnect);
		commands.put("c", MessageType.Connect);
		commands.put("connect", MessageType.Connect);
		commands.put("x", MessageType.Execute);
		commands.put("execute", MessageType.Execute);
	}
	
	
	static boolean processCommand(Session ses, Message msg, 
			                      ObjectInputStream is, ObjectOutputStream os) 
            throws IOException, ClassNotFoundException {
		if ( msg != null )
		{
			os.writeObject(msg);
			ResponseMessage response = (ResponseMessage) is.readObject();
			if (response.getErrorMessage() != "") {
				System.err.println(response.getErrorMessage());
			} else {
				switch (response.getType()) {
					case Connect:
						System.out.println("Server response: connected");
						break;
					case Disconnect:
						System.out.println("Server response: disconnected");
						break;
					case Execute:
						System.out.println("Execution result:");
						System.out.println(((ExecuteMessageResponse)msg).getTerminalResponse());
						break;
					default:
						assert(false);
						break;
				}
			}
			return true;
		}
		return false;
	}
}