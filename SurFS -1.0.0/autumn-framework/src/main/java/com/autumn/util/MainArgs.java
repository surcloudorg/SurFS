package com.autumn.util;

import java.util.HashMap;

/**
 * <p>Title: main函数输入参数格式化</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class MainArgs {

    private String[] args = null;
    private final HashMap<String, String> map = new HashMap<String, String>();

    public MainArgs(String[] args) {
        this.args = args;
        parse();
    }

    /**
     * 解析
     */
    private void parse() {
        for (String param : args) {
            param = param.trim();
            int index = param.indexOf("=");
            if (index > 0) {
                String key = param.substring(0, index).trim();
                String value = param.substring(index + 1).trim();
                map.put(key.toLowerCase(), value);
            }
        }
    }

    /**
     * 获取参数值
     *
     * @param key
     * @param defaultValue
     * @return String
     */
    public String getString(String key, String defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    /**
     * 获取参数值
     *
     * @param key
     * @return String
     */
    public String getString(String key) {
        return map.get(key.toLowerCase());
    }

    /**
     * 获取参数值
     *
     * @param key
     * @param defaultValue
     * @return int
     */
    public int getInt(String key, int defaultValue) {
        try {
            return getInt(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取参数值
     *
     * @param key
     * @return int
     * @throws Exception
     */
    public int getInt(String key) throws Exception {
        String value = map.get(key.toLowerCase());
        return Integer.parseInt(value);
    }

    /**
     * 获取参数值
     *
     * @param key
     * @param defaultValue
     * @return long
     */
    public long getLong(String key, int defaultValue) {
        try {
            return getLong(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取参数值
     *
     * @param key
     * @return long
     * @throws Exception
     */
    public long getLong(String key) throws Exception {
        String value = map.get(key.toLowerCase());
        return Long.parseLong(value);
    }

    /**
     * 获取参数值
     *
     * @param key
     * @param defaultValue
     * @return boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取参数值
     *
     * @param key
     * @return boolean
     * @throws Exception
     */
    public boolean getBoolean(String key) throws Exception {
        String value = map.get(key.toLowerCase());
        return TextUtils.parseBoolean(value);
    }
}
