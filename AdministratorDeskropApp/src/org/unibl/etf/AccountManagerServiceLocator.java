/**
 * AccountManagerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unibl.etf;

public class AccountManagerServiceLocator extends org.apache.axis.client.Service implements org.unibl.etf.AccountManagerService {

    public AccountManagerServiceLocator() {
    }


    public AccountManagerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AccountManagerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AccountManager
    private java.lang.String AccountManager_address = "http://localhost:8080/SOAPService/services/AccountManager";

    public java.lang.String getAccountManagerAddress() {
        return AccountManager_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AccountManagerWSDDServiceName = "AccountManager";

    public java.lang.String getAccountManagerWSDDServiceName() {
        return AccountManagerWSDDServiceName;
    }

    public void setAccountManagerWSDDServiceName(java.lang.String name) {
        AccountManagerWSDDServiceName = name;
    }

    public org.unibl.etf.AccountManager getAccountManager() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AccountManager_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAccountManager(endpoint);
    }

    public org.unibl.etf.AccountManager getAccountManager(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.unibl.etf.AccountManagerSoapBindingStub _stub = new org.unibl.etf.AccountManagerSoapBindingStub(portAddress, this);
            _stub.setPortName(getAccountManagerWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAccountManagerEndpointAddress(java.lang.String address) {
        AccountManager_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.unibl.etf.AccountManager.class.isAssignableFrom(serviceEndpointInterface)) {
                org.unibl.etf.AccountManagerSoapBindingStub _stub = new org.unibl.etf.AccountManagerSoapBindingStub(new java.net.URL(AccountManager_address), this);
                _stub.setPortName(getAccountManagerWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("AccountManager".equals(inputPortName)) {
            return getAccountManager();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://etf.unibl.org", "AccountManagerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://etf.unibl.org", "AccountManager"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AccountManager".equals(portName)) {
            setAccountManagerEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
