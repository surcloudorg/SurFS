/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.user.model;

import java.io.IOException;
import java.sql.Timestamp;

import com.surfs.nas.client.SurFileFactory;

public class Mount {
	
	private int mountId;
	
	private String path;
	
	private long quota;
	
	private long usedQuota;
	
	private Timestamp createTime;
	
	private String oldPath;	

	public int getMountId() {
		return mountId;
	}

	public void setMountId(int mountId) {
		this.mountId = mountId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getQuota() {
		return quota;
	}

	public void setQuota(long quota) {
		this.quota = quota;
	}

	public long getUsedQuota() {
		try {
			this.usedQuota = SurFileFactory.newInstance(path).length();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return usedQuota;
	}

	public void setUsedQuota(long usedQuota) {
		this.usedQuota = usedQuota;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getOldPath() {
		return oldPath;
	}

	public void setOldPath(String oldPath) {
		this.oldPath = oldPath;
	}
	
}
