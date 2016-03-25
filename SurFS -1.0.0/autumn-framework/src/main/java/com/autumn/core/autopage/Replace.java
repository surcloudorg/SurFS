package com.autumn.core.autopage;

import com.autumn.core.web.Action;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: 搜索替换字符串中的${参数}</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Replace {

    private static Pattern pattern = null;//含.号
    private static Pattern pattern2 = null;//不含.号

    static {
        String patternString = "\\$\\{[_(A-Z)(a-z)(0-9)\\:\\/\\.]+\\}";
        pattern = Pattern.compile(patternString);
        String patternString2 = "\\$\\{[_(A-Z)(a-z)(0-9)]+\\}";
        pattern2 = Pattern.compile(patternString2);
    }

    /**
     * 替换${key}-->""
     *
     * @param source
     * @return String
     */
    public static String replace(String source) {
        Matcher matcher = pattern.matcher(source);
        return matcher.replaceAll("");
    }

    /**
     * 替换${key}-->value
     *
     * @param source
     * @param value
     * @return String
     */
    public static String replace(String source, String value) {
        Matcher matcher = pattern.matcher(source);
        return matcher.replaceAll(value);
    }

    /**
     * 替换sql语句中的${field}-->?,并存储字段名与参数索引map 这里不替换${包括.号的}
     *
     * @param source
     * @param map
     * @return String
     */
    public static String replaceSQL(String source, HashMap<String, Integer> map) {
        String res = source;
        Matcher matcher = pattern2.matcher(res);
        int ii = 1;
        int index = 0;
        while (matcher.find(index)) {
            String ss = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            index = start;
            res = res.substring(0, start) + "?" + res.substring(end);
            matcher = pattern2.matcher(res);
            ss = ss.substring(2);
            ss = ss.substring(0, ss.length() - 1);
            map.put(ss.trim().toLowerCase(), Integer.valueOf(ii));
            ii++;
        }
        return res;
    }

    /**
     * 查找${param}
     *
     * @param source
     * @param param
     * @return int -1没找到，〉=0索引
     */
    public static int find(String source, String param) {
        String pstr = "${" + param + "}";
        String res = source;
        Matcher matcher = pattern.matcher(res);
        int index = 0;
        while (matcher.find(index)) {
            String ss = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            if (ss.equalsIgnoreCase(pstr)) {
                return start;
            } else {
                index = end;
            }
        }
        return -1;
    }

    /**
     * 替换${param}-->value
     *
     * @param source
     * @param param
     * @param value
     * @return String
     */
    public static String replace(String source, String param, String value) {
        String pstr = "${" + param + "}";
        String res = source;
        Matcher matcher = pattern.matcher(res);
        int index = 0;
        while (matcher.find(index)) {
            String ss = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            if (ss.equalsIgnoreCase(pstr)) {
                res = res.substring(0, start) + value + res.substring(end);
                matcher = pattern.matcher(res);
            } else {
                index = end;
            }
        }
        return res;
    }

    /**
     * 这里需要替换${loginuser.*}${webcfg.*}${action.*}
     *
     * @param source
     * @param action
     * @return String 如果含有其他的抛出错误(存在不可替换的变量?)
     */
    public static String replace(String source, Action action, String yinhao) throws Exception {
        String res = source;
        Matcher matcher = pattern.matcher(res);
        int end = 0;
        while (matcher.find(end)) {
            String ss = matcher.group();
            int start = matcher.start();
            end = matcher.end();
            String lowerss = ss.toLowerCase();
            BeanFieldValue loginuser = null;
            if (action.getLoginUser() != null) {
                loginuser = new BeanFieldValue(action.getLoginUser());
            }
            BeanFieldValue webcfg = new BeanFieldValue(action.getWebDirectory());
            BeanFieldValue myaction = new BeanFieldValue(action);
            if (lowerss.startsWith("${loginuser.")) {
                if (loginuser == null) {
                    throw new Exception("存在不可替换的变量" + ss);
                }
                lowerss = lowerss.substring(12);
                lowerss = lowerss.substring(0, lowerss.length() - 1).trim();
                String value = loginuser.getValue(lowerss);
                if (value == null) {
                    throw new Exception("存在不可替换的变量" + ss);
                }
                res = res.substring(0, start) + yinhao + value + yinhao + res.substring(end);
                matcher = pattern.matcher(res);
            } else if (lowerss.startsWith("${webcfg.")) {
                lowerss = lowerss.substring(9);
                lowerss = lowerss.substring(0, lowerss.length() - 1).trim();
                String value = webcfg.getValue(lowerss);
                if (value == null) {
                    throw new Exception("存在不可替换的变量" + ss);
                }
                res = res.substring(0, start) + yinhao + value + yinhao + res.substring(end);
                matcher = pattern.matcher(res);
            } else if (lowerss.startsWith("${action.")) {
                lowerss = lowerss.substring(9);
                lowerss = lowerss.substring(0, lowerss.length() - 1).trim();
                String value = myaction.getValue(lowerss);
                if (value == null) {
                    throw new Exception("存在不可替换的变量" + ss);
                }
                res = res.substring(0, start) + yinhao + value + yinhao + res.substring(end);
                matcher = pattern.matcher(res);
            } else {
                throw new Exception("存在不可替换的变量" + ss);
            }
            if (end >= res.length() - 1) {
                break;
            }
        }
        return res;
    }

    /**
     * 替换${icon.*}--><img src=....
     *
     * @param source
     * @return String
     */
    public static String replaceIcon(String source) {
        String res = source;
        Matcher matcher = pattern.matcher(res);
        int index = 0;
        while (matcher.find(index)) {
            String ss = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            if (ss.toLowerCase().startsWith("${icon:")) {
                ss = ss.substring(7);
                ss = ss.substring(0, ss.length() - 1).trim();
                String value = "<img src=\"" + ss + "\"/>&nbsp;";
                res = res.substring(0, start) + value + res.substring(end);
                matcher = pattern.matcher(res);
            } else {
                index = end;
            }
        }
        return res;
    }
}
