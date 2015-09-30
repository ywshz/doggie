package org.yws.doggie.scheduler;

import java.io.Serializable;

public class WorkerResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int SUCCESS_CODE=200;
	
	private int code;
	private String message;
	private Object data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
