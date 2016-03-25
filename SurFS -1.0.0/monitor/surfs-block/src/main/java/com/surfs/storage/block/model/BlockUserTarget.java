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
