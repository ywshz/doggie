package org.yws.doggie.worker;

public class JobInfoResponse {
    private Long historyId;
	private boolean succeed;
	private String message;

	public JobInfoResponse(){
		
	}
	
	public JobInfoResponse(boolean succeed, String message) {
		this.succeed=succeed;
		this.message=message;
	}

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public boolean isSucceed() {
		return succeed;
	}

	public void setSucceed(boolean succeed) {
		this.succeed = succeed;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
