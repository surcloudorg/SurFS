package com.surfs.storage.web.dto;

import java.net.URL;

public class User {
	
	private String userName;
	
	private String passWord;
	
	private String dataCenter;
	
	private String dataCenterName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getDataCenter() {
		return dataCenter;
	}

	public void setDataCenter(String dataCenter) {
		this.dataCenter = dataCenter;
	}

	public String getDataCenterName() {
		return dataCenterName;
	}

	public void setDataCenterName(String dataCenterName) {
		this.dataCenterName = dataCenterName;
	}
	
	
	public static void main(String[] args) {
		URL url = User.class.getClassLoader().getResource("rest.properties");
		System.out.println(url.getPath());
	}
}
