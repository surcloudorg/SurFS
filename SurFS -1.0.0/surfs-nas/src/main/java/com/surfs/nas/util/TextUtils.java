/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    private static final Pattern p = Pattern.compile("[\\w\\.]+");

    /**
     *
     * @param s
     * @return boolean
     */
    public static boolean isValidFileName(String s) {
        if (s != null) {
            if (s.isEmpty()) {
                return true;
            }
            Matcher m = p.matcher(s);
            if (m.find()) {
                if (m.group().equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 2*20->40
     *
     * @param value String
     * @return int
     * @throws Exception
     */
    public static long getTrueLongValue(String value) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            throw new Exception("");
        }
        int multiplier = 1;
        if (value.endsWith("K") || value.endsWith("k")) {
            multiplier = 1024;
        } else if (value.endsWith("M") || value.endsWith("m")) {
            multiplier = 1024 * 1024;
        } else if (value.endsWith("G") || value.endsWith("g")) {
            multiplier = 1024 * 1024 * 1024;
        }
        if (multiplier != 1) {
            value = value.substring(0, value.length() - 1);
        }
        StringTokenizer st = new StringTokenizer(value.trim(), "*");
        long res = 1;
        while (st.hasMoreTokens()) {
            long n = Long.parseLong(st.nextToken().trim());
            res = res * n;
        }
        return res * multiplier;
    }

    /**
     * 2*20->40
     *
     * @param value
     * @param defaultValue
     * @return long
     */
    public static long getTrueLongValue(String value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return getTrueLongValue(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * String--〉boolean
     *
     * @param s
     * @return boolean
     * @throws Exception
     */
    public static boolean parseBoolean(String s) throws Exception {
        if (s == null || s.trim().equals("")) {
            throw new Exception("");
        }
        if (s.equalsIgnoreCase("true")) {
            return true;
        } else if (s.equalsIgnoreCase("false")) {
            return false;
        } else {
            return Integer.parseInt(s) > 0;
        }
    }

    /**
     * String--〉boolean
     *
     * @param s
     * @param defaultValue
     * @return boolean
     */
    public static boolean parseBoolean(String s, boolean defaultValue) {
        if (s == null) {
            return defaultValue;
        }
        try {
            return parseBoolean(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    private static final ThreadLocal<HashMap<String, SimpleDateFormat>> simpleDateFormatLocal = new ThreadLocal<>();

    /**
     *
     * @param formatPattern
     * @param locale Locale
     * @param timeZone TimeZone
     * @return SimpleDateFormat
     */
    private static SimpleDateFormat getDateFormat(String formatPattern, Locale locale, TimeZone timeZone) {
        HashMap<String, SimpleDateFormat> dfmap = simpleDateFormatLocal.get();
        String key = locale == null ? formatPattern : (formatPattern + locale.toString());
        SimpleDateFormat df;
        if (dfmap == null) {
            dfmap = new HashMap<>();
            df = locale == null ? new SimpleDateFormat(formatPattern) : new SimpleDateFormat(formatPattern, locale);
            dfmap.put(key, df);
            simpleDateFormatLocal.set(dfmap);
        } else {
            df = dfmap.get(key);
            if (df == null) {
                df = locale == null ? new SimpleDateFormat(formatPattern) : new SimpleDateFormat(formatPattern, locale);
                if (dfmap.size() < 100) {
                    dfmap.put(key, df);
                }
            }
        }
        if (timeZone == null) {
            df.setTimeZone(TimeZone.getDefault());
        } else {
            df.setTimeZone(timeZone);
        }
        return df;
    }

    /**
     *
     * @param date String
     * @param formatPattern String
     * @param locale Locale
     * @param timeZone TimeZone
     * @return Date
     * @throws java.text.ParseException
     */
    public static Date String2Date(String date, String formatPattern, Locale locale, TimeZone timeZone) throws ParseException {
        if ((formatPattern == null) || formatPattern.isEmpty()) {
            formatPattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat df = getDateFormat(formatPattern, locale, timeZone);
        return df.parse(date);
    }

    /**
     *
     * @param date
     * @param formatPattern
     * @return Date
     * @throws ParseException
     */
    public static Date String2Date(String date, String formatPattern) throws ParseException {
        return String2Date(date, formatPattern, null, null);
    }

    /**
     *
     * @param date
     * @return Date
     * @throws java.text.ParseException
     */
    public static Date String2Date(String date) throws ParseException {
        return String2Date(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     *
     * @param date Date
     * @return String
     */
    public static String Date2String(Date date) {
        return Date2String(date, "yyyy-MM-dd HH:mm:ss", null, null);
    }

    /**
     *
     * @param date
     * @param formatPattern
     * @return String
     */
    public static String Date2String(Date date, String formatPattern) {
        return Date2String(date, formatPattern, null, null);
    }

    /**
     *
     * @param date
     * @param formatPattern
     * @param locale
     * @param timeZone
     * @return String
     */
    public static String Date2String(Date date, String formatPattern, Locale locale, TimeZone timeZone) {
        if ((formatPattern == null) || formatPattern.isEmpty()) {
            formatPattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat df = getDateFormat(formatPattern, locale, timeZone);
        return df.format(date);
    }
    private static final Pattern zhfilter = Pattern.compile("[\\u4E00-\\u9FA5]");
    private static final Pattern jpfilter = Pattern.compile("[\\u3040-\\u309F|\\u30A0-\\u30FF|\\u31F0-\\u31FF]");

    /**
     *
     * @param str
     * @return
     */
    public static boolean iszh(String str) {
        if (str == null || str.trim().equals("")) {
            return true;
        }
        Matcher matcher = zhfilter.matcher(str);
        boolean b = matcher.find();
        if (b) {
            matcher = jpfilter.matcher(str);
            return !matcher.find();
        } else {
            return false;
        }
    }

    /**
     *
     * @param range String
     * @param ip String
     * @return boolean
     */
    public static boolean checkIpRange(String range, String ip) {
        if (range == null || range.trim().isEmpty()) {
            return true;
        }
        StringTokenizer st = new StringTokenizer(range, ",;|");
        while (st.hasMoreTokens()) {
            String pp = st.nextToken().trim();
            if (pp.isEmpty()) {
                continue;
            }
            boolean b = ip.matches(pp);
            if (b) {
                return true;
            }
        }
        return false;
    }
}
