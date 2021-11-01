package org.unibl.etf.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Statistic {

	private HashMap<String, Long> loginTimes;
	private HashMap<String, Long> logoutTimes;

	private ArrayList<Row> rows;
	
	public Statistic() {

	}
	
	public Statistic(Statistic s) {

		loginTimes = s.getLoginTimes();
		logoutTimes = s.getLogoutTimes();
		rows = new ArrayList<Row>();
	}

	public HashMap<String, Long> getLoginTimes() {
		return loginTimes;
	}

	public HashMap<String, Long> getLogoutTimes() {
		return logoutTimes;
	}

	public ArrayList<Row> getRows() {
		
		for(long i =1; i<=loginTimes.size(); ++i) {
			
			rows.add(new Row(i, loginTimes.get("loginTime"+i), logoutTimes.get("logoutTime"+i)));
			
		}
		
		return rows;
	}
	
}
