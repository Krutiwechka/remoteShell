package remoteShell.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import java.rmi.registry.LocateRegistry;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;

import remoteShell.*;
import remoteShell.server.RemoteShellInterface;

public class Client {
    private static final String SERVERNAME = "RemoteShell";

    Client(String portStr, String address, String name) throws 
	IllegalArgumentException, RuntimeException, RemoteException, NotBoundException {
		connect(portStr, address, name);
	}

	static public void connect(String portStr, String address, String name) throws 
	IllegalArgumentException, RuntimeException, RemoteException, NotBoundException {
		int port = parsePort(portStr);
		Registry registry = LocateRegistry.getRegistry(address, port);
        RemoteShellInterface shell = (RemoteShellInterface) registry.lookup(SERVERNAME);
        session(shell, name);
	}
	
	static int parsePort(String portStr) throws IllegalArgumentException {
		try {
			return Integer.parseInt(portStr);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid port format " + portStr);
		}
	}

	static class Session {
		boolean connected = false;
		String username = null;
		Session(String name) {
			username = name;
		}
	}
	static void session(RemoteShellInterface shell, String name) throws RemoteException {
        shell.connect(name);
        System.out.println("Connected to server");

		try ( Scanner in = new Scanner(System.in)) {
			Session ses = new Session(name);
            ses.connected = true;
            try {
                printPrompt();
                while (true) {
                    String msg = getCommand(ses, in);
                    if (!processCommand(ses, msg, shell)) {
                        break;
                    }				
                }			
            } finally {
                closeSession(ses, shell);
            }
        
		} catch ( Exception e) {
			System.err.println(e);
		}
	}
	

	static void printPrompt() {
		System.out.println(
			"Client commands:\n" + 
			"\tq/quit - disconnect and exit application\n" +
			"\tc/connect [port] [address] - connect/reconnect to server\n" + 
			"\tx/execute [command] - execute command in linux terminal\n"
		);
	}

	static void closeSession(Session ses, RemoteShellInterface shell) throws IOException {
		if ( ses.connected ) {
			ses.connected = false;
			try {
                shell.disconnect(ses.username);
                System.out.println("Disconnected");
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
            }
		} else {
            System.out.println("Not connected");
        }
	}

	static String strToAction(String line) {
		if (line.equals("") || line.equals(null))
			return null;
		StringTokenizer tokenizer = new StringTokenizer(line);
		String command = tokenizer.nextToken();

		if (commands.containsKey(command))
			return commands.get(command);
		return null;
	}

	static String getArg(String executeCommand) throws IllegalArgumentException {
		if (executeCommand.startsWith("x "))
			return executeCommand.substring("x ".length());
		else if (executeCommand.startsWith("execute "))
			return executeCommand.substring("execute ".length());
		throw new IllegalArgumentException("Not an execution command: " + executeCommand);
	}


	static String[] getConnectionArgs(String connectCommand) throws IllegalArgumentException {
		String commandStr = connectCommand;
		if (commandStr.startsWith("c "))
			commandStr = commandStr.substring("c ".length());
		else if (commandStr.startsWith("connect "))
			commandStr = commandStr.substring("connect ".length());
		else
			throw new IllegalArgumentException("Not a connection command: " + connectCommand);
		String[] parts = commandStr.split(" ");

		if (parts.length == 3)
			return parts;
		return new String[0];
	}

	static String getCommand(Session ses, Scanner in) {	
		while (true) {
			if (in.hasNextLine()== false)
				break;
			String str = in.nextLine();
			String action = strToAction(str);
			if (action == null) {
				System.err.println("Unknown command");
				printPrompt();
				continue;
			}
			if (action.equals("connect")) {
                if (ses.connected) {
                    System.err.println("Already connected");
                    continue;
                }
                String[] args = getConnectionArgs(str);
                if (args.length == 0) {
                    System.err.println("Invalid arguments for connection: " + str);
                    printPrompt();
                    continue;
                }
                
            }
            else if (action.equals("execute")) {
                String commandString = getArg(str);
                if (commandString.trim().isEmpty()) {
                    System.err.println("Provide a command to execute");
                    continue;
                }
            }
			return str;
		}
		return null;
	}
	
	
	static TreeMap<String,String> commands = new TreeMap<String,String>();
	static {
		commands.put("q", "quit");
		commands.put("quit", "quit");
		commands.put("c", "connect");
		commands.put("connect", "connect");
		commands.put("x", "execute");
		commands.put("execute", "execute");
	}
	
	
	static boolean processCommand(Session ses, String msg, RemoteShellInterface shell) 
            throws IOException, ClassNotFoundException, RemoteException, NotBoundException {
		if (msg != null) {
            String action = strToAction(msg);
            if (action == null)
                return false;

            switch (action) {
                case "connect": {
                    String[] args = getConnectionArgs(msg);
                    closeSession(ses, shell);
                    connect(args[0], args[1], ses.username);
                    break;
                }
                case "execute": {
                    String commandString = getArg(msg);
                    String response = shell.execute(ses.username, commandString);
                    System.out.println(response);
                    break;
                }
                case "disconnect": {
                    closeSession(ses, shell);
                    break;
                }
                default:
                    return false;
            }
            return true;
		}
		return false;
	}
}