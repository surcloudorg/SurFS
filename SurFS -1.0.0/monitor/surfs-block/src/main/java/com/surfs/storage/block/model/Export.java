/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.block.model;

import java.util.List;

public class Export {
	
	private List<Device> device;
	
	private List<Login> login;
	
	private String target;
	
	private List<BlockUser> blockUser;
	
	private List<String> account;
	
	private List<String> acl;

	public void setBlockUser(List<BlockUser> blockUser) {
		this.blockUser = blockUser;
	}

	public List<Device> getDevice() {
		return device;
	}

	public void setDevice(List<Device> device) {
		this.device = device;
	}

	public List<Login> getLogin() {
		return login;
	}

	public void setLogin(List<Login> login) {
		this.login = login;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<String> getAccount() {
		return account;
	}

	public void setAccount(List<String> account) {
		this.account = account;
	}

	public List<String> getAcl() {
		return acl;
	}

	public void setAcl(List<String> acl) {
		this.acl = acl;
	} 
	
	public List<BlockUser> getBlockUser() {
		return blockUser;
	}

}
