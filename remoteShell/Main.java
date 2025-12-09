package remoteShell;

import remoteShell.server.*;

import java.util.Arrays;

import remoteShell.client.*;

public class Main {
	public static void main(String[] args) {
		try{
			String name = System.getProperty("user.name");
			for(int i = 0; i < args.length; i++) {
				if(args[i].equals("-n") || args[i].equals("--name")) {
					if(i == args.length - 1) { 
				 		throw new IllegalArgumentException("Name is empty");
				 	}
					else {
				 		name = args[i + 1];
				 	}
				}
			}
			if(args.length >= 1) {
				int i = 0;
				while(i < args.length) {
					if(args[i].equals("-h") || args[0].equals("--help")){
					System.out.println(
							"\t-l, --listen [PORT] 				Server starts listening chosen port\n" + 
							"\t-c, --connect [PORT] [IP]	 	Сonnect chosen server\n" +
							"\t-h, --help 						Сommand line syntax\n"+ 
							"\t-n, --name [NAME]      			Set username for client"+
							"(*) Uses TCP protocol as default");
					break;
					}
				else if(args[i].equals("-l") || args[i].equals("--listen")) {
					 	if(i == args.length - 1) { 
					 		throw new IllegalArgumentException("Port is empty");
					 	}
					 	else {
					 		Server.listen(args[i + 1]);
					 		break;
					 	}
					}
				else if(args[i].equals("-c") || args[i].equals("--connect")) {
					if(i == args.length - 1) { 
						throw new IllegalArgumentException("Port is empty");
					}
					else if(i != args.length - 2 && args[i + 2].charAt(0) != '-') {
						Client.connect(args[i + 1], args[i + 2], name);
					}
					else {
						Client.connect(args[i + 1], name);
					}
					break;
				}
				else {
					throw new IllegalArgumentException("Option is not supported");
				}
				}
			}
			else {
				throw new IllegalArgumentException("Nothing to do, add -h for help");
			}
		} catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
