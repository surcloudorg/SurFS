/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.cfg;

import com.autumn.core.log.LogFactory;
import com.autumn.util.TextUtils;
import java.io.*;
import java.util.*;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * <p>
 * Title: xml配置文件解析保存工具</p>
 *
 * <p>
 * Description: 定义了一个统一的xml文档格式，实现参数存取，提供参数变更接口</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Config {

    private ConfigListener listener = null; //监听器
    private final HashMap<String, Property> properties = new HashMap<String, Property>(); //存储属性
    private final HashMap<String, Method> methods = new HashMap<String, Method>(); //存储方法
    private Document doc = null; //xml文档
    private boolean changed = false; //是否更新

    /**
     * 构造
     *
     * @param xml String
     * @throws IOException
     * @throws JDOMException
     */
    public Config(String xml) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        doc = builder.build(new StringReader(xml));
        Element root = doc.getRootElement();
        parseDoc(root, "");
    }

    /**
     * 构造
     *
     * @param is InputStream xml输入流
     * @throws IOException
     * @throws JDOMException
     */
    public Config(InputStream is) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        doc = builder.build(is);
        Element root = doc.getRootElement();
        parseDoc(root, "");
    }

    public Config(Element e, String parentname) throws JDOMException {
        parseDoc(e, parentname == null ? "" : parentname);
    }

    /**
     * 解析doc
     */
    private void parseDoc(Element e, String parentName) throws JDOMException {
        String name = e.getName();
        if (name.contains(".")) {
            throw new JDOMException("元素名中不能包含.号");
        }
        String fullname = parentName.isEmpty() ? name : parentName + "." + name;
        String title = e.getAttributeValue("comment");
        boolean isMethod = TextUtils.parseBoolean(e.getAttributeValue("method"), false);
        boolean cdata = TextUtils.parseBoolean(e.getAttributeValue("cdata"), false);
        List dataList = e.getChildren();
        if (isMethod) {
            if (getMethod(fullname) != null) {
                throw new JDOMException("存在重复方法:" + fullname);
            }
            Method method = new Method(fullname, title);
            if (dataList != null && (!dataList.isEmpty())) {//只存在默认参数
                Iterator i = dataList.iterator();
                while (i.hasNext()) {
                    Element ee = (Element) i.next();
                    boolean cdata1 = TextUtils.parseBoolean(ee.getAttributeValue("cdata"), false);
                    Property property = new Property(ee.getName(), ee.getText(), ee.getAttributeValue("comment"), cdata1);
                    method.addParam(property);
                }
            }
            synchronized (methods) {
                methods.put(fullname, method);
            }
        } else {
            if (dataList == null || dataList.isEmpty()) {
                if (getAttribute(fullname) != null) {
                    throw new JDOMException("存在重复参数:" + fullname);
                }
                String value = e.getText();
                Property property = new Property(fullname, value, title, cdata);
                synchronized (properties) {
                    properties.put(fullname, property);
                }
            } else {
                Iterator i = dataList.iterator();
                while (i.hasNext()) {
                    Element ee = (Element) i.next();
                    parseDoc(ee, fullname);
                }
            }
        }
    }

    /**
     * 更新doc
     *
     * @param e Element
     * @param rootName String
     */
    private void updateDoc(Element e, String parentName) {
        String name = e.getName();
        boolean isMethod = TextUtils.parseBoolean(e.getAttributeValue("method"), false);
        String fullname = parentName.equals("") ? name : parentName + "." + name;
        if (!isMethod) {
            List dataList = e.getChildren();
            if (dataList == null || dataList.isEmpty()) {
                Property eValue = getAttribute(fullname);
                if (eValue != null) {
                    if (eValue.getValue() == null) {
                        e.setText("");
                    } else {
                        if (eValue.isCdata()) {
                            e.setContent(new CDATA(eValue.getValue()));
                        } else {
                            e.setText(eValue.getValue());
                        }
                    }
                }
            } else {
                Iterator i = dataList.iterator();
                while (i.hasNext()) {
                    Element ee = (Element) i.next();
                    updateDoc(ee, fullname);
                }
            }
        }
    }

    /**
     * 获取所有参数名
     *
     * @return Enumeration
     */
    public Enumeration<String> getAttributeNames() {
        synchronized (properties) {
            return Collections.enumeration(properties.keySet());
        }
    }

    /**
     * 获取所有方法名
     *
     * @return Enumeration
     */
    public Enumeration<String> getMethodNames() {
        synchronized (methods) {
            return Collections.enumeration(methods.keySet());
        }
    }

    /**
     * 获取属性
     *
     * @param key String 属性名，包括父节点
     * @return Property
     */
    public Property getAttribute(String key) {
        synchronized (properties) {
            return properties.get(key);
        }
    }

    /**
     * 获取属性值
     *
     * @param key String 属性名
     * @return String
     */
    public String getAttributeValue(String key) {
        synchronized (properties) {
            Property p = properties.get(key);
            if (p != null) {
                return p.getValue();
            }
        }
        return null;
    }

    /**
     * 获取整型属性值
     *
     * @param key
     * @param defaueValue 设置不合法或未设置的默认值
     * @return int
     */
    public int getAttributeIntValue(String key, int defaueValue) {
        return (int) TextUtils.getTrueLongValue(getAttributeValue(key), defaueValue);
    }

    /**
     * 获取长整型属性值
     *
     * @param key
     * @param defaueValue 设置不合法或未设置的默认值
     * @return long
     */
    public long getAttributeLongValue(String key, long defaueValue) {
        return TextUtils.getTrueLongValue(getAttributeValue(key), defaueValue);
    }

    /**
     * 获取Date属性值，格式"yyyy-MM-dd HH:mm:ss"
     *
     * @param key
     * @param defaueValue
     * @return Date
     */
    public Date getAttributeDateValue(String key, Date defaueValue) {
        String value = getAttributeValue(key);
        if (value == null) {
            return defaueValue;
        } else {
            try {
                return TextUtils.String2Date(value.trim(), "yyyy-MM-dd");
            } catch (Exception r) {
                return defaueValue;
            }
        }
    }

    /**
     * 获取Date属性值，格式"yyyy-MM-dd HH:mm:ss"
     *
     * @param key
     * @param defaueValue
     * @return Date
     */
    public Date getAttributeDateTimeValue(String key, Date defaueValue) {
        String value = getAttributeValue(key);
        if (value == null) {
            return defaueValue;
        } else {
            try {
                return TextUtils.String2Date(value.trim());
            } catch (Exception r) {
                return defaueValue;
            }
        }
    }

    /**
     * 获取Date属性值，格式"yyyy-MM-dd HH:mm:ss"
     *
     * @param key
     * @param defaueValue
     * @return Date
     */
    public Date getAttributeTimeValue(String key, Date defaueValue) {
        String value = getAttributeValue(key);
        if (value == null) {
            return defaueValue;
        } else {
            try {
                return TextUtils.String2Date(value.trim(), "HH:mm:ss");
            } catch (Exception r) {
                return defaueValue;
            }
        }
    }

    /**
     * 获取布尔型属性值
     *
     * @param key
     * @param defaueValue 设置不合法或未设置的默认值
     * @return boolean
     */
    public boolean getAttributeBooleanValue(String key, boolean defaueValue) {
        return TextUtils.parseBoolean(getAttributeValue(key), defaueValue);
    }

    /**
     * 获取方法
     *
     * @param key String 方法名
     * @return Method
     */
    public Method getMethod(String key) {
        synchronized (methods) {
            return methods.get(key);
        }
    }

    /**
     * 设置属性
     *
     * @param property Property
     */
    public boolean setAttribute(Property property) throws Exception {
        Property p1 = this.getAttribute(property.getKey());
        if (p1 == null) {
            throw new Exception("参数名" + property.getKey() + "不存在");
        }
        p1.setComment(property.getComment());
        return setAttributeValue(property.getKey(), property.getValue());
    }

    /**
     * 设置属性
     *
     * @param key String 属性名
     * @param value String 属性值
     * @return boolean 允许更改 true,false不允许更改
     * @throws Exception
     */
    public synchronized boolean setAttributeValue(String key, String value) throws Exception {
        Property p1 = this.getAttribute(key);
        if (p1 == null) {
            throw new Exception("参数名" + key + "不存在");
        }
        if (p1.getValue().equals(value)) { //参数值没变化
            return true;
        }
        boolean allowChange = true;
        if (listener != null) {
            Property p2 = new Property(p1);
            p2.setValue(value);
            try {
                allowChange = listener.changeProperty(p2);
            } catch (Throwable ex) {
                LogFactory.trace("执行'" + listener.getClass().getName() + "changeProperty(" + p2.toString() + ")'发生未知错误!", ex, Config.class);
                throw new Exception(ex);
            }
        }
        if (allowChange) {
            synchronized (properties) {
                p1.setValue(value);
                changed = true;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 呼叫函数
     *
     * @param method Method
     * @return Object
     * @throws Exception
     */
    public Object callMethod(Method method) throws Exception {
        if (listener == null) {
            throw new Exception("没有设置监听器,不能调用方法:" + method.getMethodName());
        } else {
            Object str = null;
            try {
                str = listener.callMethod(method);
            } catch (Throwable ex) {
                LogFactory.trace("执行'" + listener.getClass().getName() + "callMethod(" + method.toString() + ")'发生未知错误!", ex, Config.class);
                throw new Exception(ex);
            }
            synchronized (methods) {
                if (methods.get(method.getMethodName()) != null) {
                    methods.put(method.getMethodName(), method);
                }
            }
            return str;
        }
    }

    /**
     * 恢复所有参数值
     */
    public void reset() {
        synchronized (properties) {
            properties.clear();
        }
        synchronized (methods) {
            methods.clear();
        }
        try {
            Element root = doc.getRootElement();
            parseDoc(root, root.getName());
        } catch (Exception e) {
        }
    }

    /**
     * xml写入OutputStream..
     *
     * @param charset String 指定字符集
     * @param os OutputStream
     * @throws IOException
     */
    public void save(OutputStream os, String charset) throws IOException {
        update();
        Format format = Format.getCompactFormat();
        format.setEncoding(charset);//设置文档字符编码
        format.setIndent("  ");//设置缩进字符串
        XMLOutputter outputter = new XMLOutputter(format);
        outputter.output(doc, os);
        os.flush();
        os.close();
    }

    /**
     * xml写入OutputStream..
     *
     * @param os
     * @throws IOException
     */
    public void save(OutputStream os) throws IOException {
        update();
        XMLOutputter outputter = new XMLOutputter();
        outputter.output(doc, os);
    }

    /**
     * 更新属性到xml文档
     */
    public synchronized void update() {
        Element root = doc.getRootElement();
        updateDoc(root, "");
        changed = false;
    }

    /**
     * 保存到文件
     *
     * @param filename String
     * @throws IOException
     */
    public synchronized void save(String filename) throws IOException {
        save(new FileOutputStream(filename));
    }

    /**
     * 保存到文件
     *
     * @param filename
     * @param charset 设定文件编码
     * @throws IOException
     */
    public synchronized void save(String filename, String charset) throws IOException {
        save(new FileOutputStream(filename), charset);
    }

    /**
     * 获取xml字符串
     *
     * @param charSet 指定字符集
     * @return String
     * @throws IOException
     */
    public String getDoc(String charSet) throws IOException {
        Format format = Format.getCompactFormat();
        if (charSet != null) {
            format.setEncoding(charSet);//设置文档字符编码
        }
        format.setIndent("  ");//设置缩进字符串
        XMLOutputter outputter = new XMLOutputter(format);
        StringWriter sw = new StringWriter();
        outputter.output(doc, sw);
        return sw.toString();
    }

    /**
     * 获取xml字符串
     *
     * @return String
     * @throws IOException
     */
    public String getDoc() throws IOException {
        return getDoc(null);
    }

    /**
     * 参数值是否被更改
     *
     * @return boolean
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * 获取监听器
     *
     * @return ConfigListener
     */
    public ConfigListener getListener() {
        return listener;
    }

    /**
     * 获取所有属性
     *
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<Property> getProperties() {
        List<Property> list = new ArrayList<Property>(properties.values());
        Collections.sort(list);
        return list;
    }

    /**
     * 移除属性
     *
     * @param key
     * @return Property
     */
    public Property removeProperties(String key) {
        return properties.remove(key);
    }

    /**
     * 获取所有方法
     *
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<Method> getMethods() {
        List<Method> list = new ArrayList<Method>(methods.values());
        Collections.sort(list);
        return list;
    }

    /**
     * 设置监听器
     *
     * @param listener ConfigListener
     */
    public void setListener(ConfigListener listener) {
        this.listener = listener;
    }
}
