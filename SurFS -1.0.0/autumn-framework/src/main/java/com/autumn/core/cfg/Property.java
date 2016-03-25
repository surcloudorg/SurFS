package com.autumn.core.cfg;

import java.io.Serializable;

/**
 * <p>Title: 配置中的属性</p>
 *
 * <p>Description: 属性数据模型</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Property implements Serializable, Comparable {

    private static final long serialVersionUID = 20110720103955452L;
    private String key = ""; //属性名
    private String comment = ""; //注释
    private boolean cdata = false;//是否跳过xml字符检测
    private String value = ""; //属性值

    @Override
    public String toString() {
        return "Property{" + "key=" + key + ", comment=" + comment + ", cdata=" + cdata + ", value=" + value + '}';
    }

    /**
     * 克隆
     *
     * @param p
     */
    public Property(Property p) {
        this.key = p.key;
        this.value = p.value;
        this.cdata = p.cdata;
        this.comment = p.comment;
    }

    /**
     * 构造
     *
     * @param key
     * @param value
     */
    public Property(String key, String value) {
        this.key = key;
        this.value = value != null ? value.trim() : null;
    }

    /**
     * 构造
     *
     * @param key
     * @param value
     * @param comment
     * @param cdata
     */
    public Property(String key, String value, String comment, boolean cdata) {
        this.key = key;
        this.value = value != null ? value.trim() : null;
        this.comment = comment;
        this.cdata = cdata;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the key
     */
    public String getSimpleKey() {
        if (key.contains(".")) {
            return key.substring(key.lastIndexOf(".") + 1);
        }
        return key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value != null ? value.trim() : null;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the cdata
     */
    public boolean isCdata() {
        return cdata;
    }

    /**
     * @param cdata the cdata to set
     */
    public void setCdata(boolean cdata) {
        this.cdata = cdata;
    }

    /**
     * 对属性排序
     *
     * @param o
     * @return int
     */
    @Override
    public int compareTo(Object o) {
        Property p = (Property) o;
        return this.key.compareTo(p.key);
    }
}
