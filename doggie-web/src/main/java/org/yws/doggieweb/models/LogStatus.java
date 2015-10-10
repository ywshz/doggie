package org.yws.doggieweb.models;

import java.io.Serializable;

public class LogStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String status;
	private String log;

	public LogStatus(){
		
	}
	
	public LogStatus(String status, String log) {
		this.status=status;
		this.log=log;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

}
