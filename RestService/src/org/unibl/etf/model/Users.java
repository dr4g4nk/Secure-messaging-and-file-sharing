package org.unibl.etf.model;

import java.util.ArrayList;
import java.util.Collection;

public class Users {

	private ArrayList<String> users = new ArrayList<String>();
	
	public Users(Collection<String> set) {
		
		users.addAll(set);
	}
	
	public ArrayList<String> getUsers(){
		return users;
	}
}
