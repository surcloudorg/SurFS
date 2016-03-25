package com.surfs.storage.common.util;

public class CmdResponse {
	
	private int status;
	
	private String response;

	public CmdResponse() {
		super();
	}

	public CmdResponse(int status, String response) {
		this.status = status;
		this.response = response;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
