package com.surfs.storage.block.model;

import java.util.List;

public class ExportInfo {
	
	private List<Export> info;
	
	private String ip;
	
	private String hostname;

	public List<Export> getInfo() {
		return info;
	}

	public void setInfo(List<Export> info) {
		this.info = info;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

}
