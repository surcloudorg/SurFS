package com.surfs.storage.block.service;

import java.util.List;
import java.util.Map;

import com.surfs.storage.block.model.BlockUser;
import com.surfs.storage.block.model.BlockUserTarget;

public interface BlockUserService {
	public List<BlockUserTarget> queryAllBlockUserTarget(String poolName)throws Exception;
	
	public List<BlockUser> queryAllBlockUser(String poolName) throws Exception;
	
	public int addBlockUser(String poolName,BlockUser blockUser) throws Exception;
	
	public int modifyBlockUser(String poolName,BlockUser blockUser) throws Exception;
	
	public int removeBlockUser(String poolName,int userId) throws Exception;
	
	public List<BlockUser> queryListBlockUserTarget(String poolName,String target)throws Exception;
	
	public List<BlockUser> queryListBlockUserNotTarget(String poolName,String target)throws Exception;
	
	public void addBlockUserTarget(String poolName, List<Map<String, String>> listMap) throws Exception;
	
	//public void editBlockUserTarget(String poolName, List<Map<String, String>> listMap) throws Exception;
	
	public int removeBlockUserTarget(String poolName,Map<String,String> args)throws Exception;
	
	public List<BlockUserTarget> queryBlockUser(String poolName,int userId)throws Exception;
	
	public String addRemoteBlockUserTarget(Map<String, Object> map) throws Exception;
	
}
