package org.unibl.etf.model;

import java.net.InetAddress;

public class User {

	private String username;
	private String password;
	private String address;
	
	private String newPassword;
	
	
	public User() {
		super();
	}

	public User(String username) {
		super();
		this.username = username;
	}
	
	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	public User(String username, String password, InetAddress address) {
		super();
		this.username = username;
		this.password = password;
		this.address = address.getHostAddress();
	}
	
	public User(String username, String password, String newPassword) {
		super();
		this.username = username;
		this.password = password;
		this.newPassword = newPassword;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getNewPassword() {
		return newPassword;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	
}
