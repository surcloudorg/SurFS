/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.block.model;

public class BlockUserTarget {
	private int userTargetId;
	private int userId;
	private String target;
	
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public int getUserTargetId() {
		return userTargetId;
	}
	public void setUserTargetId(int userTargetId) {
		this.userTargetId = userTargetId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
