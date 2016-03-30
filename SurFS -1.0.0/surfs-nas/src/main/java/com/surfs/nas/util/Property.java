/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.util;

import java.io.Serializable;


public class Property implements Serializable, Comparable {

    private static final long serialVersionUID = 20110720103955452L;
    private String key = ""; 
    private String comment = ""; 
    private boolean cdata = false;
    private String value = ""; 

    @Override
    public String toString() {
        return "Property{" + "key=" + key + ", comment=" + comment + ", cdata=" + cdata + ", value=" + value + '}';
    }

    /**
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
     *
     * @param key
     * @param value
     */
    public Property(String key, String value) {
        this.key = key;
        this.value = value != null ? value.trim() : null;
    }

    /**
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
