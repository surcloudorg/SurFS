/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.util;

import java.io.*;
import java.util.*;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Config {

    private ConfigListener listener = null;
    private final HashMap<String, Property> properties = new HashMap<>();
    private final HashMap<String, Method> methods = new HashMap<>();
    private Document doc = null;
    private boolean changed = false;

    /**
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
     *
     * @param is InputStream
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

    private void parseDoc(Element e, String parentName) throws JDOMException {
        String name = e.getName();
        if (name.contains(".")) {
            throw new JDOMException("");
        }
        String fullname = parentName.isEmpty() ? name : parentName + "." + name;
        String title = e.getAttributeValue("comment");
        boolean isMethod = TextUtils.parseBoolean(e.getAttributeValue("method"), false);
        boolean cdata = TextUtils.parseBoolean(e.getAttributeValue("cdata"), false);
        List dataList = e.getChildren();
        if (isMethod) {
            if (getMethod(fullname) != null) {
                throw new JDOMException();
            }
            Method method = new Method(fullname, title);
            if (dataList != null && (!dataList.isEmpty())) {
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
                    throw new JDOMException();
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
     *
     * @return Enumeration
     */
    public Enumeration<String> getAttributeNames() {
        synchronized (properties) {
            return Collections.enumeration(properties.keySet());
        }
    }

    /**
     *
     * @return Enumeration
     */
    public Enumeration<String> getMethodNames() {
        synchronized (methods) {
            return Collections.enumeration(methods.keySet());
        }
    }

    /**
     *
     * @param key String
     * @return Property
     */
    public Property getAttribute(String key) {
        synchronized (properties) {
            return properties.get(key);
        }
    }

    /**
     *
     * @param key String
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
     *
     * @param key
     * @param defaueValue
     * @return int
     */
    public int getAttributeIntValue(String key, int defaueValue) {
        return (int) TextUtils.getTrueLongValue(getAttributeValue(key), defaueValue);
    }

    /**
     *
     *
     * @param key
     * @param defaueValue
     * @return long
     */
    public long getAttributeLongValue(String key, long defaueValue) {
        return TextUtils.getTrueLongValue(getAttributeValue(key), defaueValue);
    }

    /**
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
     *
     * @param key
     * @param defaueValue
     * @return boolean
     */
    public boolean getAttributeBooleanValue(String key, boolean defaueValue) {
        return TextUtils.parseBoolean(getAttributeValue(key), defaueValue);
    }

    /**
     *
     * @param key String
     * @return Method
     */
    public Method getMethod(String key) {
        synchronized (methods) {
            return methods.get(key);
        }
    }

    /**
     *
     * @param property Property
     * @return
     * @throws java.lang.Exception
     */
    public boolean setAttribute(Property property) throws Exception {
        Property p1 = this.getAttribute(property.getKey());
        if (p1 == null) {
            throw new Exception(property.getKey() + " is not exist");
        }
        p1.setComment(property.getComment());
        return setAttributeValue(property.getKey(), property.getValue());
    }

    /**
     *
     * @param key String
     * @param value String
     * @return boolean
     * @throws Exception
     */
    public synchronized boolean setAttributeValue(String key, String value) throws Exception {
        Property p1 = this.getAttribute(key);
        if (p1 == null) {
            throw new Exception(key + "is not exist");
        }
        if (p1.getValue().equals(value)) {
            return true;
        }
        boolean allowChange = true;
        if (listener != null) {
            Property p2 = new Property(p1);
            p2.setValue(value);
            try {
                allowChange = listener.changeProperty(p2);
            } catch (Throwable ex) {
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
     *
     * @param method Method
     * @return Object
     * @throws Exception
     */
    public Object callMethod(Method method) throws Exception {
        Object str = null;
        try {
            str = listener.callMethod(method);
        } catch (Throwable ex) {

            throw new Exception(ex);
        }
        synchronized (methods) {
            if (methods.get(method.getMethodName()) != null) {
                methods.put(method.getMethodName(), method);
            }
        }
        return str;
    }

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
     *
     *
     * @param charset String
     * @param os OutputStream
     * @throws IOException
     */
    public void save(OutputStream os, String charset) throws IOException {
        update();
        Format format = Format.getCompactFormat();
        format.setEncoding(charset);
        format.setIndent("  ");
        XMLOutputter outputter = new XMLOutputter(format);
        outputter.output(doc, os);
        os.flush();
        os.close();
    }

    /**
     *
     * @param os
     * @throws IOException
     */
    public void save(OutputStream os) throws IOException {
        update();
        XMLOutputter outputter = new XMLOutputter();
        outputter.output(doc, os);
    }

    public synchronized void update() {
        Element root = doc.getRootElement();
        updateDoc(root, "");
        changed = false;
    }

    /**
     *
     * @param filename String
     * @throws IOException
     */
    public synchronized void save(String filename) throws IOException {
        save(new FileOutputStream(filename));
    }

    /**
     *
     * @param filename
     * @param charset
     * @throws IOException
     */
    public synchronized void save(String filename, String charset) throws IOException {
        save(new FileOutputStream(filename), charset);
    }

    /**
     *
     * @param charSet
     * @return String
     * @throws IOException
     */
    public String getDoc(String charSet) throws IOException {
        Format format = Format.getCompactFormat();
        if (charSet != null) {
            format.setEncoding(charSet);
        }
        format.setIndent("  ");
        XMLOutputter outputter = new XMLOutputter(format);
        StringWriter sw = new StringWriter();
        outputter.output(doc, sw);
        return sw.toString();
    }

    /**
     *
     * @return String
     * @throws IOException
     */
    public String getDoc() throws IOException {
        return getDoc(null);
    }

    /**
     *
     * @return boolean
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     *
     * @return ConfigListener
     */
    public ConfigListener getListener() {
        return listener;
    }

    /**
     *
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<Property> getProperties() {
        List<Property> list = new ArrayList<>(properties.values());
        Collections.sort(list);
        return list;
    }

    /**
     *
     * @param key
     * @return Property
     */
    public Property removeProperties(String key) {
        return properties.remove(key);
    }

    /**
     *
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<Method> getMethods() {
        List<Method> list = new ArrayList<>(methods.values());
        Collections.sort(list);
        return list;
    }

    /**
     *
     * @param listener ConfigListener
     */
    public void setListener(ConfigListener listener) {
        this.listener = listener;
    }
}
