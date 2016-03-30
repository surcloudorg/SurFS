/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Method implements Serializable,Comparable {

    private static final long serialVersionUID = 20110720103955453L;
    private String methodName = ""; 
    private String comment = ""; 
    private final List<Property> params = new ArrayList<>(); 

    @Override
    public String toString() {
        return "Method{" + "methodName=" + methodName + ", comment=" + comment + ", params=" + params + '}';
    }

    public Method(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @param methodName
     * @param comment
     */
    public Method(String methodName, String comment) {
        this.methodName = methodName;
        this.comment = comment;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @return the params
     */
    public List<Property> getParams() {
        return params;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * 添加输入参数
     *
     * @param param
     */
    public void addParam(Property param) {
        Property p = getParam(param.getKey());
        if (p != null) {
            this.getParams().remove(p);
        }
        this.getParams().add(param);
    }

    public void addParam(String key, String value) {
        addParam(new Property(key, value));
    }

    /**
     *
     * @param key
     * @return Property
     */
    public Property getParam(String key) {
        if (getParams() != null) {
            for (Property p : getParams()) {
                if (p.getKey().equals(key)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param key
     * @return Property
     */
    public Property removeParam(String key) {
        if (getParams() != null) {
            for (Property p : getParams()) {
                if (p.getKey().equals(key)) {
                    getParams().remove(p);
                    return p;
                }
            }
        }
        return null;
    }


    public void clearParam() {
        if (getParams() != null) {
            getParams().clear();
        }
    }

    /**
     *
     * @param key
     * @return String
     */
    public String getParamValue(String key) {
        Property p = getParam(key);
        if (p != null) {
            return p.getValue();
        }
        return null;
    }

    /**
     *
     * @param key
     * @param defaueValue 
     * @return int
     */
    public int getParamIntValue(String key, int defaueValue) {
        String value = getParamValue(key);
        return (int) TextUtils.getTrueLongValue(value, defaueValue);
    }

    /**
     * 
     *
     * @param key
     * @param defaueValue 
     * @return long
     */
    public long getParamLongValue(String key, long defaueValue) {
        String value = getParamValue(key);
        return TextUtils.getTrueLongValue(value, defaueValue);
    }

    /**
     * 
     *
     * @param key
     * @param defaueValue 
     * @return boolean
     */
    public boolean getParamBooleanValue(String key, boolean defaueValue) {
        String value = getParamValue(key);
        if (value == null) {
            return defaueValue;
        } else {
            if (value.equalsIgnoreCase("true")) {
                return true;
            } else if (value.equalsIgnoreCase("false")) {
                return false;
            } else {
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
    public Date getParamDateValue(String key, Date defaueValue) {
        String value = getParamValue(key);
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
    public Date getParamDateTimeValue(String key, Date defaueValue) {
        String value = getParamValue(key);
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
    public Date getParamTimeValue(String key, Date defaueValue) {
        String value = getParamValue(key);
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
     * @param o
     * @return  int
     */
    @Override
    public int compareTo(Object o) {
        Method m=(Method)o;
        return this.methodName.compareTo(m.methodName);
    }
}
