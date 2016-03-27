/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.autopage;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * <p>Title: 获取javabean实例中的字段值</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class BeanFieldValue {

    private Object object = null;
    private HashMap<String, Method> getmethods = new HashMap<String, Method>();
    private HashMap<String, Method> setmethods = new HashMap<String, Method>();

    public BeanFieldValue(Object object) {
        this.object = object;
        fillMethods();
    }

    /**
     * 获取getter,setter方法列表
     */
    private void fillMethods() {
        Method[] mym = object.getClass().getMethods();
        for (Method m : mym) {
            String namestr = m.getName().toLowerCase();
            if (namestr.startsWith("get")) {
                namestr = namestr.substring(3);
                if (m.getParameterTypes().length == 0) {
                    getmethods.put(namestr, m);
                }
            }
            if (namestr.startsWith("set")) {
                namestr = namestr.substring(3);
                if (m.getParameterTypes().length == 1
                        && m.getReturnType().getName().equalsIgnoreCase("void")) {
                    setmethods.put(namestr, m);
                }
            }
        }
    }

    /**
     * 获取字段值
     *
     * @param name
     * @return String
     */
    public String getValue(String name) {
        Method m = getmethods.get(name.toLowerCase());
        if (m == null) {
            return null;
        }
        try {
            Object obj = m.invoke(object, new Object[]{});
            if (obj == null) {
                return "";
            } else {
                return obj.toString();
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取字段值
     *
     * @param name
     * @return Object
     * @throws Exception
     */
    public Object getObject(String name) throws Exception {
        Method m = getmethods.get(name.toLowerCase());
        if (m == null) {
            throw new Exception("找不到方法get" + name);
        }
        Object obj = m.invoke(object, new Object[]{});
        return obj;
    }

    /**
     * 设置字段值
     *
     * @param name
     * @param arg
     * @throws Exception
     */
    public void SetObject(String name, Object arg) throws Exception {
        Method m = getmethods.get(name.toLowerCase());
        if (m == null) {
            throw new Exception("找不到方法set" + name);
        }
        m.invoke(object, new Object[]{arg});
    }
}
