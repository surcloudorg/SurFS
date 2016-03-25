package com.surfs.storage.block.service;

import java.util.Properties;

import com.surfs.storage.common.util.PropertiesFactory;

public abstract class BlockConstant {
	
	public static final String BLOCK_POOL_PATH;
	
	public static final String BLOCK_POOL_PATH_P;
	
	public static final String BLOCK_POOLSTATUS_PATH;
	
	public static final String BLOCK_TARGET_PATH;
	
	public static final String POOL_SERVICE_PATH;
	
	public static final String POOL_SERVICE_POOLJSON_NAME;
	
	public static final String POOL_SERVICE_DELETEVOL_NAME;
	
	public static final String POOL_SERVICE_ADDVOL_NAME;
	
	public static final String EXPORT_SERVICE_ADDTARGET_NAME;
	
	public static final String EXPORT_SERVICE_DELTARGET_NAME;
	
	public static final String EXPORT_SERVICE_ADDDEVICE_NAME;
	
	public static final String EXPORT_SERVICE_DELDEVICE_NAME;
	
	public static final String EXPORT_SERVICE_VOLDEV_NAME;
	
	public static final String EXPORT_SERVICE_TARGET_NAME;
	
	public static final String EXPORT_SERVICE_ADDBLOCKUSER_NAME;
	
	public static final String BLOCK_VOLDEV_PATH;
	
	public static final String BLOCK_DELVOL_PATH;
	
	public static final String BLOCK_ADDVOL_PATH;
	
	public static final String BLOCK_ADDTARGET_PATH;
	
	public static final String BLOCK_DELTARGET_PATH;
	
	public static final String BLOCK_ADDDEVICE_PATH;
	
	public static final String BLOCK_ADDDEVICE_PATH_ACL;
	
	public static final String BLOCK_ADDDEVICE_PATH_OAUTH;
	
	public static final String BLOCK_DELDEVICE_PATH;
	
	public static final String BLOCK_SNAP_CREATE;
	
	public static final String BLOCK_SNAP_LIST;
	
	public static final String BLOCK_SNAP_GENERATE;
	
	public static final String BLOCK_SNAP_DEL;
	
	public static final String BLOCK_VOL_COPY;
	
	//public static final String CURRENT_POOL_NAME;
	
	public static final String SURFS_DEFAULT_DEV;
	
	public static final String SURFS_VERSION;
	
	public static final String SURFS_LOCAL_POOL;
	
	public static final String SURFS_REMOTE_POOL;
	
	public static final String SURFS_REMOTE_IP;
	
	public static final String PING_SPEED;
	
	static {
		Properties restProp = PropertiesFactory.getProperties("/rest.properties");
		POOL_SERVICE_PATH = restProp.getProperty("pool_service_path");
		POOL_SERVICE_POOLJSON_NAME = restProp.getProperty("pool_service_pooljson_name");
		POOL_SERVICE_DELETEVOL_NAME = restProp.getProperty("pool_service_deletevol_name");
		POOL_SERVICE_ADDVOL_NAME = restProp.getProperty("pool_service_addvol_name");
		EXPORT_SERVICE_ADDTARGET_NAME = restProp.getProperty("export_service_addtarget_name");
		EXPORT_SERVICE_ADDDEVICE_NAME = restProp.getProperty("export_service_adddevice_name");
		EXPORT_SERVICE_DELTARGET_NAME = restProp.getProperty("export_service_deltarget_name");
		EXPORT_SERVICE_DELDEVICE_NAME = restProp.getProperty("export_service_deldevice_name");
		EXPORT_SERVICE_VOLDEV_NAME = restProp.getProperty("export_service_voldev_name");
		EXPORT_SERVICE_TARGET_NAME = restProp.getProperty("export_service_target_name");
		//CURRENT_POOL_NAME = restProp.getProperty("current_pool_name");
		SURFS_DEFAULT_DEV = restProp.getProperty("surfs_default_dev");
		EXPORT_SERVICE_ADDBLOCKUSER_NAME = restProp.getProperty("export_service_addblockuser_name");
		
		Properties shellProp = PropertiesFactory.getProperties("/shell.properties");
		BLOCK_POOL_PATH = shellProp.getProperty("block_pool_path");
		BLOCK_POOL_PATH_P = shellProp.getProperty("block_pool_path_p");
		BLOCK_TARGET_PATH = shellProp.getProperty("block_target_path");
		BLOCK_DELVOL_PATH = shellProp.getProperty("block_delvol_path");
		BLOCK_ADDVOL_PATH = shellProp.getProperty("block_addvol_path");
		BLOCK_POOLSTATUS_PATH = shellProp.getProperty("block_poolstatus_path");
		BLOCK_ADDTARGET_PATH = shellProp.getProperty("block_addtarget_path");
		BLOCK_DELTARGET_PATH = shellProp.getProperty("block_deltarget_path");
		BLOCK_ADDDEVICE_PATH = shellProp.getProperty("block_adddevice_path");
		BLOCK_ADDDEVICE_PATH_ACL = shellProp.getProperty("block_adddevice_path_acl");
		BLOCK_ADDDEVICE_PATH_OAUTH = shellProp.getProperty("block_adddevice_path_oauth");
		BLOCK_DELDEVICE_PATH = shellProp.getProperty("block_deldevice_path");
		BLOCK_VOLDEV_PATH = shellProp.getProperty("block_voldev_path");
		BLOCK_SNAP_CREATE = shellProp.getProperty("block_snap_create");
		BLOCK_SNAP_LIST = shellProp.getProperty("block_snap_list");
		BLOCK_VOL_COPY = shellProp.getProperty("block_vol_copy");
		BLOCK_SNAP_GENERATE = shellProp.getProperty("block_snap_generate");
		BLOCK_SNAP_DEL = shellProp.getProperty("block_snap_del");
		PING_SPEED = shellProp.getProperty("ping_speed");
		
		Properties surfsProp = PropertiesFactory.getProperties("/surfs.properties");
		SURFS_VERSION = surfsProp.getProperty("surfs_version");
		SURFS_LOCAL_POOL = surfsProp.getProperty("surfs_local_pool");
		SURFS_REMOTE_POOL = surfsProp.getProperty("surfs_remote_pool");
		SURFS_REMOTE_IP = surfsProp.getProperty("surfs_remote_ip");
		
	}
	
}
