package com.surfs.storage.monitor.service;

import java.util.List;

import com.surfs.storage.monitor.model.Monitor;

public interface ClusterService {
	
	public List<Monitor> getClusterList();
	
	public Monitor getRemoteStatus();
	
	public Monitor getLocalStatus();

}
