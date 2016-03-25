package com.surfs.storage.block.model;

import java.util.List;

public class ZpoolInfo {
	
	private List<Zpool> info;
	
	private String ip;
	
	private String hostname;

	public List<Zpool> getInfo() {
		return info;
	}

	public void setInfo(List<Zpool> info) {
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
