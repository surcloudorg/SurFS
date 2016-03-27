/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.common.util;

import java.util.Properties;

public abstract class Constant {
	
	public static final String REMOTE_HOST_CMD;
	
	public static final String REST_SERVICE_PORT;
	
	public static final String PROJECT_DIR_PATH;
	
	static {
		PROJECT_DIR_PATH = CmdUtils.getProjectPath();
				
		Properties restProp = PropertiesFactory.getProperties("/rest.properties");
		REST_SERVICE_PORT = restProp.getProperty("rest_service_port");
		
		Properties shellProp = PropertiesFactory.getProperties("/shell.properties");
		REMOTE_HOST_CMD = shellProp.getProperty("remote_host_cmd");
	}

}
