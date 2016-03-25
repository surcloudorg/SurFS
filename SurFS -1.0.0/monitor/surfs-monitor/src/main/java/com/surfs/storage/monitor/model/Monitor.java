package com.surfs.storage.monitor.model;

import java.util.Map;

public class Monitor {
	
	private String ip;
	
	private Map<String, Map<String, String>> map;
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Map<String, Map<String, String>> getMap() {
		return map;
	}

	public void setMap(Map<String, Map<String, String>> map) {
		this.map = map;
	}

}
