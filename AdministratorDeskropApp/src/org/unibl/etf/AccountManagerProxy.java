package org.unibl.etf;

public class AccountManagerProxy implements org.unibl.etf.AccountManager {
  private String _endpoint = null;
  private org.unibl.etf.AccountManager accountManager = null;
  
  public AccountManagerProxy() {
    _initAccountManagerProxy();
  }
  
  public AccountManagerProxy(String endpoint) {
    _endpoint = endpoint;
    _initAccountManagerProxy();
  }
  
  private void _initAccountManagerProxy() {
    try {
      accountManager = (new org.unibl.etf.AccountManagerServiceLocator()).getAccountManager();
      if (accountManager != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)accountManager)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)accountManager)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (accountManager != null)
      ((javax.xml.rpc.Stub)accountManager)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.unibl.etf.AccountManager getAccountManager() {
    if (accountManager == null)
      _initAccountManagerProxy();
    return accountManager;
  }
  
  public boolean blockUser(java.lang.String username) throws java.rmi.RemoteException{
    if (accountManager == null)
      _initAccountManagerProxy();
    return accountManager.blockUser(username);
  }
  
  public boolean deleteUser(java.lang.String username) throws java.rmi.RemoteException{
    if (accountManager == null)
      _initAccountManagerProxy();
    return accountManager.deleteUser(username);
  }
  
  public boolean unblockUser(java.lang.String username) throws java.rmi.RemoteException{
    if (accountManager == null)
      _initAccountManagerProxy();
    return accountManager.unblockUser(username);
  }
  
  public boolean createUser(java.lang.String username, java.lang.String password, boolean admin) throws java.rmi.RemoteException{
    if (accountManager == null)
      _initAccountManagerProxy();
    return accountManager.createUser(username, password, admin);
  }
  
  public java.lang.String getAddress(java.lang.String username) throws java.rmi.RemoteException{
    if (accountManager == null)
      _initAccountManagerProxy();
    return accountManager.getAddress(username);
  }
  
  
}