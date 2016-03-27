/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.monitor.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionUtil {
	
	public static final int FRONT_START = 12;
	
	public static final int BACK_START = 8;
	
	public static final int FRONT_END = 35;
	
	public static final int BACK_END = 28;
	
	private static Map<String, String> statusMap = new ConcurrentHashMap<String, String>();
	
	public static String getStatusCssClass(String status) {
		if (statusMap.size() == 0) {
			statusMap.put("FAIL", "btn btn-danger");
			statusMap.put("USED", "btn btn-success");
			statusMap.put("FREE", "btn btn-info");
			statusMap.put("NOT CONN", "btn btn-warning");
			statusMap.put("----", "btn btn-inverse");
		}
		return statusMap.get(status);
	}
	
}
