/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.monitor.service;

import java.util.Properties;

import com.surfs.storage.common.util.PropertiesFactory;

public abstract class MonitorConstant {
	
	//public static final String REST_SERVICE_PORT;
	
	public static final String DISK_SERVICE_PATH;
	
	public static final String DISK_SERVICE_DISKINFOS_NAME;
	
	public static final String DISKINFOS_SHELL_PATH;
	
	public static final String DISK_LOGFILE_PATH;
	
	public static final String ZPOOL_STATUS_PATH;
	
	public static final String NETWORK_STATUS_PATH;
	
	public static final String CLUSTER_SERVICE_PATH;
	
	public static final String CLUSTER_SERVICE_STATUS_NAME;
	
	public static final String SURFS_LOCAL_POOL;
	
	static {
		Properties restProp = PropertiesFactory.getProperties("/rest.properties");
		//REST_SERVICE_PORT = restProp.getProperty("rest_service_port");
		DISK_SERVICE_PATH = restProp.getProperty("disk_service_path");
		DISK_SERVICE_DISKINFOS_NAME = restProp.getProperty("disk_service_diskInfos_name");
		CLUSTER_SERVICE_PATH = restProp.getProperty("cluster_service_path");
		CLUSTER_SERVICE_STATUS_NAME = restProp.getProperty("cluster_service_status_name");
				
		Properties shellProp = PropertiesFactory.getProperties("/shell.properties");
		DISKINFOS_SHELL_PATH = shellProp.getProperty("diskInfos_shell_path");
		DISK_LOGFILE_PATH = shellProp.getProperty("disk_logfile_path");
		ZPOOL_STATUS_PATH = shellProp.getProperty("zpool_status_path");
		NETWORK_STATUS_PATH = shellProp.getProperty("network_status_path");
		
		Properties surfsProp = PropertiesFactory.getProperties("/surfs.properties");
		SURFS_LOCAL_POOL = surfsProp.getProperty("surfs_local_pool");
	}

}
