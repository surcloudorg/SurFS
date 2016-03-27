/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.block.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.surfs.storage.block.model.BlockUser;
import com.surfs.storage.block.model.ExportInfo;

public interface ExportService {
	
	public List<ExportInfo> getExportInfos();
	
	public String getExportInfoJson();
	
	public String addTarget(String target);

	public String addRemoteTarget(Map<String, String> args);
	
	public String delTarget(String target);
	
	public String delRemoteTarget(Map<String, String> args);
	
	public void addDevice(Map<String, Object> args) throws IOException ;
	
	public String addRemoteDevice(Map<String, Object> args);
	
	public String delDevice(String device, String target);
	
	public String delRemoteDevice(Map<String, String> args);
	
	public String getVolDevJson();
	
	public String getRemoteVolDev(String ip);
	
	//获取授权用户名称接口
	public List<BlockUser> queryListBLockUserNames(String poolName,String target) throws Exception;
}
