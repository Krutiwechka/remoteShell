package remoteShell.server;

import remoteShell.Protocol;

public class Server {
	static void listen(String portStr, Protocol protocol) throws IllegalArgumentException {
		try {
			int port = Integer.parseInt(portStr);
			if (port < 1 || port > 65535) {
				throw new IllegalArgumentException("Invalid port: " + portStr);
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid port format: " + portStr);
		}
		ServerStopThread stopper = new ServerStopThread();
	}
}
