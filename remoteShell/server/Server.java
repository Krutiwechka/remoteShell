package remoteShell.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Server {
	private static Registry registry;
	private static RemoteShell remoteShell;
	protected static void serverLog(String msg) {
		System.err.println("["+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "][SERVER]: " + msg);
	}
	public static void listen(String portStr) throws IllegalArgumentException{
		int port;
		try {
			port = Integer.parseInt(portStr);
			if (port < 1 || port > 65535) {
				throw new IllegalArgumentException("Invalid port: " + portStr);
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid port format: " + portStr);
		}
		try{
			remoteShell = new RemoteShell();
			registry = LocateRegistry.createRegistry(port);
			registry.rebind("RemoteShell", remoteShell);
			serverLog("Server started...");
			ServerStopThread stopper = new ServerStopThread();
			stopper.start();
			while (!getStopFlag()) {
					Thread.sleep(1000);
			}
			shutdown();
		} catch (Exception e){
			shutdown();
			serverLog("Error: " + e.getMessage());
		}
	}
	private static Object syncFlags = new Object();
	private static boolean stopFlag = false;
	public static boolean getStopFlag() {
		synchronized (syncFlags) {
			return stopFlag;
		}
	}
	public static void setStopFlag(boolean value) {
		synchronized (syncFlags) {
			stopFlag = value;
		}
	}
	private static void shutdown(){
		serverLog("Shutting down server...");
		try{
			if(registry != null){
				registry.unbind("RemoteShell");
			}
			if(remoteShell != null){
				UnicastRemoteObject.unexportObject(remoteShell, true);
			}
			serverLog("Server stopped successfully");
		} catch (Exception e){
			serverLog("Error during shutdown: " + e.getMessage());
		}
	}
}
