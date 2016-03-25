package com.surfs.storage.monitor.service;

import java.util.List;
import java.util.Map;

import com.surfs.storage.monitor.model.Disk;

public interface DiskService {
	
	public List<Disk> getRemoteDiskInfos();
	
	public List<Disk> getLocalDiskInfos();
	
	public Map<String, Map<String, Map<String, Disk>>> getDiskInfos();
	
	public String readLocationLog(String date);
	
	public Map<String, Map<String, List<Disk>>> getPoolUsedDisks();
	
}
