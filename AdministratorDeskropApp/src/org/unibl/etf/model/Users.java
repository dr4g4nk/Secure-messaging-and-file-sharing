package org.unibl.etf.model;

import java.util.ArrayList;
import java.util.Set;

public class Users {

	private ArrayList<String> users = new ArrayList<String>();
	
	public Users(Set<String> set) {
		
		users.addAll(set);
	}
	
	public ArrayList<String> getUsers(){
		return users;
	}
}
