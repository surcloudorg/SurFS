/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.monitor.service.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFilter implements Filter {

	@Override
	public boolean isFilter(String line, String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String enDate = sdf.parse(date).toString();
			String[] enDates = enDate.split(" ");
			String day = String.valueOf(Integer.parseInt(enDates[2]));
			String dateMatch1 = enDates[1].concat(" ").concat(day);
			String dateMatch2 = enDates[1].concat("  ").concat(day);
			int i = line.indexOf(dateMatch1);
			if (i > -1)
				return true;
			int j = line.indexOf(dateMatch2);
			if (j > -1)
				return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) {
		DateFilter dateFilter = new DateFilter();
		boolean i = dateFilter.isFilter("Nov 2", "2015-11-01");
		System.out.println(i);
	}
}
