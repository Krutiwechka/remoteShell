package remoteShell.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteShellInterface extends Remote {
     void connect(String name) throws RemoteException;
     String execute(String name, String command) throws RemoteException;
     void disconnect(String name) throws RemoteException;
}
