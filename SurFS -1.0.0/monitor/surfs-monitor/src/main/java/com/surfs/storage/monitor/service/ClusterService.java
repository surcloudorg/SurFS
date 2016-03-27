/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.monitor.service;

import java.util.List;

import com.surfs.storage.monitor.model.Monitor;

public interface ClusterService {
	
	public List<Monitor> getClusterList();
	
	public Monitor getRemoteStatus();
	
	public Monitor getLocalStatus();

}
