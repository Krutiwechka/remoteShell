package remoteShell.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import remoteShell.Protocol;

public class Server {
	protected static void serverLog(String msg) {
		System.err.println("["+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " ][SERVER]: " + msg);
	}
	public static void listen(String portStr, Protocol protocol) throws IllegalArgumentException {
		int port;
		try {
			port = Integer.parseInt(portStr);
			if (port < 1 || port > 65535) {
				throw new IllegalArgumentException("Invalid port: " + portStr);
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid port format: " + portStr);
		}
		ServerStopThread stopper = new ServerStopThread();
		stopper.start();
		serverLog("Server started");
		try(ServerSocket serv = new ServerSocket(port)){
			serverLog("Listening port " + portStr);
			while (true) {
				Socket sock = accept(serv);
				if(sock != null) {
					serverLog(sock.getInetAddress().getHostName() + " connected");
					ServerThread server = new ServerThread(sock);
					server.start();
				}
				if(getStopFlag()) {
					break;
				}
			}
		} catch (IOException e) {
			serverLog(e.getMessage());
		} finally {
			serverLog("stopped");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {		
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
	private static Socket accept(ServerSocket serv) {
		try {
			serv.setSoTimeout(1000);
			return serv.accept();
		} catch(SocketTimeoutException e) {
			
		} catch(IOException e) {
			serverLog(e.getMessage());
		}
		return null;
	}
}
