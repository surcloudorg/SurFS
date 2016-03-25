package com.surfs.storage.monitor.utils;

import java.util.Comparator;

public class DiskComparator implements Comparator<String> {

	@Override
	public int compare(String s1, String s2) {
		int i1 = Integer.parseInt(s1);
		int i2 = Integer.parseInt(s2);
		if (i1 > i2)
			return 1;
		else if (i1 < i2)
			return -1;
		else
			return 0;
	}

}
