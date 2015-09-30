package org.yws.doggie.worker;

import java.io.Serializable;

public class JobInfoRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long historyId;
	private String type;
	private String script;

	public JobInfoRequest() {

	}

	public JobInfoRequest(Long id, Long historyId, String type, String script) {
		super();
		this.id = id;
		this.historyId = historyId;
		this.type = type;
		this.script = script;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}

}
