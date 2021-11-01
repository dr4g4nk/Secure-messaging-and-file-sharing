/**
 * AccountManager.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unibl.etf;

public interface AccountManager extends java.rmi.Remote {
    public boolean createUser(java.lang.String username, java.lang.String password, boolean admin) throws java.rmi.RemoteException;
    public boolean unblockUser(java.lang.String username) throws java.rmi.RemoteException;
    public boolean blockUser(java.lang.String username) throws java.rmi.RemoteException;
    public boolean deleteUser(java.lang.String username) throws java.rmi.RemoteException;
    public java.lang.String getAddress(java.lang.String username) throws java.rmi.RemoteException;
}
