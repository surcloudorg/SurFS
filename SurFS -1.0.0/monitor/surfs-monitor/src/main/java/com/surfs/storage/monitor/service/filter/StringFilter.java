/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.monitor.service.filter;

public class StringFilter implements Filter {

	@Override
	public boolean isFilter(String line, String str) {
		String[] strs = str.split(",");
		for (String string : strs) {
			int i = line.indexOf(string.trim());
			if (i > -1)
				return true;
		}
		return false;
	}

}
