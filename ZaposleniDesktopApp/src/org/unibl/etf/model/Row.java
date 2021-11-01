package org.unibl.etf.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Row {

	private Long ID;
	private String loginTime;
	private String logoutTime;
	private Long duration;
	
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm d.M.yyyy");
	
	public Row(Long iD, Long loginTime, Long logoutTime) {
		super();
		ID = iD;
		this.loginTime = format.format(new Date(loginTime));
		this.logoutTime = format.format(new Date(logoutTime));
		this.duration = (logoutTime-loginTime)/60000;
	}

	public Long getID() {
		return ID;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public String getLogoutTime() {
		return logoutTime;
	}

	public Long getDuration() {
		return duration;
	}

	public SimpleDateFormat getFormat() {
		return format;
	}
	
}
