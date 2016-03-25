package com.autumn.core.cfg;

import com.autumn.util.TextUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Title: 配置中的方法</p>
 * 
 * <p>Description: 方法数据模型</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Method implements Serializable,Comparable {

    private static final long serialVersionUID = 20110720103955453L;
    private String methodName = ""; //方法名
    private String comment = ""; //注释
    private final List<Property> params = new ArrayList<Property>(); //方法输入参数集

    @Override
    public String toString() {
        return "Method{" + "methodName=" + methodName + ", comment=" + comment + ", params=" + params + '}';
    }

    /**
     * 构造
     */
    public Method(String methodName) {
        this.methodName = methodName;
    }

    /**
     * 构造
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
     * 获取方法输入参数
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
     * 移除方法输入参数
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

    /**
     * 清除方法输入参数
     */
    public void clearParam() {
        if (getParams() != null) {
            getParams().clear();
        }
    }

    /**
     * 获取方法输入参数值
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
     * 获取整型属性值
     *
     * @param key
     * @param defaueValue 设置不合法或未设置的默认值
     * @return int
     */
    public int getParamIntValue(String key, int defaueValue) {
        String value = getParamValue(key);
        return (int) TextUtils.getTrueLongValue(value, defaueValue);
    }

    /**
     * 获取长整型属性值
     *
     * @param key
     * @param defaueValue 设置不合法或未设置的默认值
     * @return long
     */
    public long getParamLongValue(String key, long defaueValue) {
        String value = getParamValue(key);
        return TextUtils.getTrueLongValue(value, defaueValue);
    }

    /**
     * 获取布尔型属性值
     *
     * @param key
     * @param defaueValue 设置不合法或未设置的默认值
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
     * 获取Date属性值，格式"yyyy-MM-dd"
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
     * 获取Date属性值，格式"yyyy-MM-dd HH:mm:ss"
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
     * 获取Date属性值，格式"HH:mm:ss"
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
     * 对方法排序
     * @param o
     * @return  int
     */
    @Override
    public int compareTo(Object o) {
        Method m=(Method)o;
        return this.methodName.compareTo(m.methodName);
    }
}
