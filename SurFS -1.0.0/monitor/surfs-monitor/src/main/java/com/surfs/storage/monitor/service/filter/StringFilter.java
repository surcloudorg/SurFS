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
