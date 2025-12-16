package remoteShell.server;

import java.util.Scanner;

public class ServerStopThread extends Thread{
	Scanner in;
	ServerStopThread() {
		in = new Scanner(System.in);
		this.setDaemon(true);
		System.err.println("Enter 'quit' / 'q' to stop server");
	}
	public void run() {
		while(true) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				in.close();
				break;
			}
			if(in.hasNextLine() == false) {
				continue;
			}
			String input = in.nextLine();
			if(input.toLowerCase().equals("quit") || input.toLowerCase().equals("q")) {
				in.close();
				Server.serverLog("Stopping server...");
				Server.setStopFlag(true);
				break;
			}
		}
	}
}
