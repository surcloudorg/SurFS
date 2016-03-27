/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.web.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public class Stringutils {

	public static void main(String[] args) {

	}
	
	public static String compareReturnBerfore(String baseDate, String strDate) {
		 SimpleDateFormat sdf_us = new SimpleDateFormat("EEE MMM dd HH:mm yyyy", Locale.US);
	     SimpleDateFormat sdf_cn = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        
		if (StringUtils.isBlank(baseDate)) {
			if (StringUtils.isBlank(strDate)) {
				return null;
			} else {
				try {
					Date date = sdf_us.parse(strDate);
					return sdf_cn.format(date);
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		
		try {
			/*String date_us1 = sdf_cn.format(sdf_us.parse(strDate1));*/
			String date_us2 = sdf_cn.format(sdf_us.parse(strDate));
			if (baseDate.compareTo(date_us2) <= 0)
				return baseDate;
			else if (baseDate.compareTo(date_us2) > 0)
				return date_us2;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return strDate;
	}
	
	public static String sumSpace(String totalSpace, String currentSpace) {
		if (StringUtils.isBlank(totalSpace)) {
			 return convertToKB(currentSpace);
		}
		
		//String total = convertToKB(totalSpace);
		String current = convertToKB(currentSpace);
		BigDecimal sum = new BigDecimal(totalSpace);
		long sumNum = sum.add(new BigDecimal(current)).longValue();
		
		//BigDecimal sumTB = new BigDecimal(sumNum);
		//double divSum = sumTB.divide(new BigDecimal("1048576"), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		//return String.valueOf(divSum).concat("T");
		return String.valueOf(sumNum);
	}
	
	public static String convertToKB(String total) {
		if (StringUtils.isBlank(total)) {
			 return total;
		}
			
		String space = total.substring(0, total.length() - 1);
		String unit = total.substring(total.length() - 1, total.length());
		
		BigDecimal t = new BigDecimal(space);
		BigDecimal multiply = null;
		
		if (unit.equalsIgnoreCase("M")) {
			multiply = t.multiply(new BigDecimal("1048576"));
		} else if (unit.equalsIgnoreCase("T")) {
			multiply = t.multiply(new BigDecimal("1099511627776"));
		} else if (unit.equalsIgnoreCase("G")) {
			multiply = t.multiply(new BigDecimal("1073741824"));
		}

		return String.valueOf(multiply.doubleValue());
	}
	
	public static long subtractSpace(String totalSpace, String freeSpace) {
		//String total = convertToKB(totalSpace);
		//String free = convertToKB(freeSpace);
		
		BigDecimal subtract = new BigDecimal(totalSpace);
		long subtractNum = subtract.subtract(new BigDecimal(freeSpace)).longValue();
		
		/*BigDecimal subtractTB = new BigDecimal(subtractNum);
		double divSum = subtractTB.divide(new BigDecimal("1048576"), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return String.valueOf(divSum).concat("T");*/
		return subtractNum;
	}
	
	public static long compareDate(long date1, String date2) {
		if (date1 == 0) {
			return Long.parseLong(date2);
		}
		
		long time2 = Long.parseLong(date2);
		
		if (date1 > time2)
			return time2;
		
		return date1;
	} 
	
	public static long sum(long space1, String space2) {
		if (space1 == 0) {
			return Long.parseLong(space2);
		}
		long total2 = Long.parseLong(space2);
		
		return space1 + total2;
	} 
	
}
