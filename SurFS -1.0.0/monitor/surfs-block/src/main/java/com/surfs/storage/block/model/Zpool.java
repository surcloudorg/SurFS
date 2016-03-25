package com.surfs.storage.block.model;

import java.util.List;

public class Zpool {

	private String zpool;

	private String total;

	private String free;
	
	private String ctime;

	private List<Vol> vols;

	public String getZpool() {
		return zpool;
	}

	public void setZpool(String zpool) {
		this.zpool = zpool;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getFree() {
		return free;
	}

	public void setFree(String free) {
		this.free = free;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public List<Vol> getVols() {
		return vols;
	}

	public void setVols(List<Vol> vols) {
		this.vols = vols;
	}

}
