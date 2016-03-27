/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
