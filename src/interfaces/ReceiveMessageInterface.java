package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import server.RmiServer;

public interface ReceiveMessageInterface extends Remote {
	int receiveMessage(String comandoSql) throws RemoteException;

	int getLider() throws RemoteException;
}
