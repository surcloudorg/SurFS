/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.monitor.model;

import java.io.Serializable;

public class Disk implements Serializable {
	
	private static final long serialVersionUID = 4894197214130468859L;

	private String id;
	
	private String jbodId;
	
	private String panel;
	
	private String disk;
	
	private String location;
	
	private String status;
	
	private String statusCss;
	
	private String devName;
	
	private String zpoolName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJbodId() {
		return jbodId;
	}

	public void setJbodId(String jbodId) {
		this.jbodId = jbodId;
	}

	public String getPanel() {
		return panel;
	}

	public void setPanel(String panel) {
		this.panel = panel;
	}
	
	public String getDisk() {
		return disk;
	}

	public void setDisk(String disk) {
		this.disk = disk;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusCss() {
		return statusCss;
	}

	public void setStatusCss(String statusCss) {
		this.statusCss = statusCss;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getZpoolName() {
		return zpoolName;
	}

	public void setZpoolName(String zpoolName) {
		this.zpoolName = zpoolName;
	}

}
