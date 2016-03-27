/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.user.service;

import java.util.List;
import java.util.Map;

import com.surfs.storage.user.model.Mount;

public interface MountService {
	
	public int addMount(String poolName, Mount mount) throws Exception;
	
	public int modifyMount(String poolName, Mount mount) throws Exception;
	
	public int removeMount(String poolName, int mountId) throws Exception;
	
	public List<Mount> queryAllMount(String poolName) throws Exception;
	
	public void addUsersMount(String poolName, List<Map<String, String>> listMap) throws Exception;
	
	public void editUsersMount(String poolName, List<Map<String, String>> listMap) throws Exception;
}
