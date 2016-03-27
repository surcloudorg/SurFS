/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.util.Function;
import com.autumn.util.TextUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: 从httprequest注入</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class RequestFill {

    /**
     * 将request组装为bean实体
     *
     * @param request HttpServletRequest http请求
     * @param object Object 类实体,更新实体参数
     * @throws Exception
     */
    public static void assemble(HttpServletRequest request, Object object) {
        RequestFill rfill = new RequestFill(request, object);
        rfill.fill();
    }

    /**
     * 将request组装为bean实体
     *
     * @param request HttpServletRequest http请求
     * @param cl Class 要组装的类
     * @return Object 类实体
     * @throws Exception
     */
    public static Object assemble(HttpServletRequest request, Class cl) throws InstantiationException, IllegalAccessException {
        Object object;
        object = cl.newInstance();
        assemble(request, object);
        return object;
    }
    private HttpServletRequest request = null;
    private Object object = null;
    private HashMap<String, Method> methods = new HashMap<String, Method>();
    private Logger log = null;
    private HashMap<String, Object> sessionvalue = new HashMap<String, Object>();//session级变量值
    private String sessionKey = null;//session变量key

    /**
     * 注入
     *
     * @param request
     * @param cl
     * @throws Exception
     */
    public RequestFill(HttpServletRequest request, Class cl) throws Exception {
        this(request, cl.newInstance());
    }

    /**
     * 注入
     *
     * @param request
     * @param object
     */
    public RequestFill(HttpServletRequest request, Object object) {
        this.request = request;
        this.object = object;
    }

    /**
     * 获取日志
     *
     * @return Logger
     */
    private Logger getLogger() {
        if (log == null) {
            log = LogFactory.getLogger().getLogger(object.getClass());
        }
        return log;
    }

    /**
     * 获取函数
     */
    private void fillMethods() {
        sessionvalue.clear();
        methods.clear();
        Method[] mym = object.getClass().getDeclaredMethods();
        for (Method m : mym) {
            String namestr = m.getName().toLowerCase();
            if (namestr.startsWith("set")) {
                namestr = namestr.substring(3);
                if (m.getParameterTypes().length == 1 && m.getReturnType().getName().equalsIgnoreCase("void")) {
                    if (sessionKey != null && m.isAnnotationPresent(SessionMethod.class)) {
                        HttpSession mySession = request.getSession(false);
                        if (mySession != null) {
                            Object obj = mySession.getAttribute(sessionKey + "." + namestr);
                            if (obj != null) {
                                sessionvalue.put(namestr, obj);
                            }
                        }
                    }
                    methods.put(namestr, m);
                }
            }
        }
    }

    /**
     * 按request参数依次注入
     */
    private void fillField(String param, boolean formrequest) {
        Method m = methods.get(param.toLowerCase());
        if (m == null) {
            return;
        }
        Class type = m.getParameterTypes()[0];
        try {
            setFieldValue(m, type, param, formrequest);
        } catch (Exception e) {
            getLogger().error("给[{0}.{1}]赋值失败:{2}", new Object[]{object.getClass().getName(), param, e.getMessage()});
        }
    }

    /**
     * 按request参数依次注入
     */
    private void fillField() {
        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            fillField(en.nextElement().toString(), true);
        }
        if (request instanceof MultipartRequestWrapper) {
            MultipartRequestWrapper mr = (MultipartRequestWrapper) request;
            en = mr.getFileFormNames();
            while (en.hasMoreElements()) {
                fillField(en.nextElement().toString(), true);
            }
        }
    }

    /**
     * 注入
     *
     * @param action
     */
    public void fill(ActionMap action) {
        sessionKey = action.getActionid();
        fillMethods();
        fillField();
        Set<String> set = sessionvalue.keySet();
        for (String param : set) {
            fillField(param, false);
        }
    }

    /**
     * 注入
     */
    public void fill() {
        sessionKey = null;
        fillMethods();
        fillField();
    }

    /**
     * 给java成员赋值
     *
     * @param m
     * @param typestr
     * @param paramname
     * @param formrequest
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setFieldValue(Method m, Class typestr, String paramname, boolean formrequest) throws
            IllegalAccessException, InvocationTargetException {
        String value;
        String[] values;
        if (formrequest) {
            if (typestr == FileItem.class) {
                if (request instanceof MultipartRequestWrapper) {
                    MultipartRequestWrapper mr = (MultipartRequestWrapper) request;
                    FileItem file = mr.getFileForm(paramname);
                    if (file != null) {
                        m.invoke(object, new Object[]{file});
                    }
                }
                return;
            }
            if (typestr == FileItem[].class) {
                if (request instanceof MultipartRequestWrapper) {
                    MultipartRequestWrapper mr = (MultipartRequestWrapper) request;
                    FileItem[] files = mr.getFileForms(paramname);
                    if (files != null) {
                        m.invoke(object, new Object[]{files});
                    }
                }
                return;
            }
            value = request.getParameter(paramname);
            values = request.getParameterValues(paramname);
            if (sessionKey != null && m.isAnnotationPresent(SessionMethod.class)) {
                HttpSession mySession = request.getSession(true);
                if (values.length == 1 && (value == null || value.isEmpty())) {
                    mySession.removeAttribute(sessionKey + "." + paramname);
                } else {
                    mySession.setAttribute(sessionKey + "." + paramname, values);
                }
                sessionvalue.remove(paramname);
            }
        } else {
            Object obj = sessionvalue.get(paramname);
            if (obj instanceof String[]) {
                value = ((String[]) obj)[0];
                values = (String[]) obj;
            } else {
                value = obj.toString();
                values = new String[]{obj.toString()};
            }
        }
        if (typestr == int.class || typestr == Integer.class) { //int
            if (!value.isEmpty()) {
                m.invoke(object, new Object[]{new Integer(value)});
            }
        } else if (typestr == short.class || typestr == Short.class) { //short
            if (!value.isEmpty()) {
                m.invoke(object, new Object[]{new Short(value)});
            }
        } else if (typestr == long.class || typestr == Long.class) { //long
            if (!value.isEmpty()) {
                m.invoke(object, new Object[]{new Long(value)});
            }
        } else if (typestr == float.class || typestr == Float.class) { //float
            if (!value.isEmpty()) {
                m.invoke(object, new Object[]{new Float(value)});
            }
        } else if (typestr == double.class || typestr == Double.class) { //double
            if (!value.isEmpty()) {
                m.invoke(object, new Object[]{new Double(value)});
            }
        } else if (typestr == boolean.class || typestr == Boolean.class) { //boolean
            boolean b = TextUtils.parseBoolean(value, false);
            m.invoke(object, new Object[]{Boolean.valueOf(b)});
        } else if (typestr == int[].class || typestr == Integer[].class) { //int[]
            m.invoke(object, new Object[]{Function.StringArray2IntArray(values)});
        } else if (typestr == short[].class || typestr == Short[].class) { //short[]
            m.invoke(object, new Object[]{Function.StringArray2ShortArray(values)});
        } else if (typestr == long[].class || typestr == Long[].class) { //long[]
            m.invoke(object, new Object[]{Function.StringArray2LongArray(values)});
        } else if (typestr == float[].class || typestr == Float[].class) { //float[]
            m.invoke(object, new Object[]{Function.StringArray2FloatArray(values)});
        } else if (typestr == double[].class || typestr == Double[].class) { //double[]
            m.invoke(object, new Object[]{Function.StringArray2DoubleArray(values)});
        } else if (typestr == byte[].class || typestr == Byte[].class) { //byte[]
            m.invoke(object, new Object[]{value.getBytes()});
        } else if (typestr == String[].class) { //String[]
            m.invoke(object, new Object[]{values});
        } else if (typestr == boolean[].class || typestr == Boolean[].class) { //boolean[]
            m.invoke(object, new Object[]{Function.StringArray2BooleanArray(values)});
        } else {
            m.invoke(object, new Object[]{value});
        }
    }

    /**
     * javabean
     *
     * @return Object
     */
    public Object getObject() {
        return object;
    }
}
