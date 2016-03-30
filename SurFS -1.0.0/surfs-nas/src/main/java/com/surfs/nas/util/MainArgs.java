/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.util;

import java.util.HashMap;

public class MainArgs {

    private String[] args = null;
    private final HashMap<String, String> map = new HashMap<>();

    public MainArgs(String[] args) {
        this.args = args;
        parse();
    }

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
     *
     * @param key
     * @return String
     */
    public String getString(String key) {
        return map.get(key.toLowerCase());
    }

    /**
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
