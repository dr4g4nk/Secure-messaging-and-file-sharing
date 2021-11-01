package org.unibl.etf.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface RMIInterface extends Remote {

	Set<String> getAllFiles(String username) throws RemoteException;
	byte[] getFile(String username, String fileName) throws RemoteException;
	boolean sendFile(String toUser, String fileName, byte[] content) throws RemoteException;
}
