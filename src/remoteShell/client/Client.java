package remoteShell.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
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
						if (! processCommand(ses, msg, is, os)) {
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
		os.writeObject( new MessageConnect(ses.userNic, ses.userName));
		MessageConnectResult msg = (MessageConnectResult) is.readObject();
		if (msg.Error()== false ) {
			System.err.println("connected");
			ses.connected = true;
			return true;
		}
		System.err.println("Unable to connect: "+ msg.getErrorMessage());
		System.err.println("Press <Enter> to continue...");
		if( in.hasNextLine())
			in.nextLine();
		return false;
	}
	
	static void closeSession(Session ses, ObjectOutputStream os) throws IOException {
		if ( ses.connected ) {
			ses.connected = false;
			os.writeObject(new MessageDisconnect());
		}
	}

	// static Message getCommand(Session ses, Scanner in) {	
	// 	while (true) {
	// 		printPrompt();
	// 		if (in.hasNextLine()== false)
	// 			break;
	// 		String str = in.nextLine();
	// 		byte cmd = translateCmd(str);
	// 		switch ( cmd ) {
	// 			case -1:
	// 				return null;
	// 			case Protocol.CMD_CHECK_MAIL:
	// 				return new MessageCheckMail();
	// 			case Protocol.CMD_USER:
	// 				return new MessageUser();
	// 			case Protocol.CMD_LETTER:
	// 				return inputLetter(in);
	// 			case 0:
	// 				continue;
	// 			default: 
	// 				System.err.println("Unknow command!");
	// 				continue;
	// 		}
	// 	}
	// 	return null;
	// }
	
	
	// static TreeMap<String,Byte> commands = new TreeMap<String,Byte>();
	// static {
	// 	commands.put("q", new Byte((byte) -1));
	// 	commands.put("quit", new Byte((byte) -1));
	// 	commands.put("m", new Byte(Protocol.CMD_CHECK_MAIL));
	// 	commands.put("mail", new Byte(Protocol.CMD_CHECK_MAIL));
	// 	commands.put("u", new Byte(Protocol.CMD_USER));
	// 	commands.put("users", new Byte(Protocol.CMD_USER));
	// 	commands.put("l", new Byte(Protocol.CMD_LETTER));
	// 	commands.put("letter", new Byte(Protocol.CMD_LETTER));
	// }
	
	
	// static boolean processCommand(Session ses, Message msg, 
	// 		                      ObjectInputStream is, ObjectOutputStream os) 
    //         throws IOException, ClassNotFoundException {
	// 	if ( msg != null )
	// 	{
	// 		os.writeObject(msg);
	// 		ResponseMessage res = (ResponseMessage) is.readObject();
	// 		if ( res.Error()) {
	// 			System.err.println(res.getErrorMessage());
	// 		} else {
	// 			switch (res.getID()) {
	// 				case Protocol.CMD_CHECK_MAIL:
	// 					printMail(( MessageCheckMailResult ) res);
	// 					break;
	// 				case Protocol.CMD_USER:
	// 					printUsers(( MessageUserResult ) res);
	// 					break;
	// 				case Protocol.CMD_LETTER:
	// 					System.out.println("OK...");
	// 					break;
	// 				default:
	// 					assert(false);
	// 					break;
	// 			}
	// 		}
	// 		return true;
	// 	}
	// 	return false;
	// }
}