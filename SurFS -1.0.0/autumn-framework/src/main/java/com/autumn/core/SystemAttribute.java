package com.autumn.core;

import com.autumn.core.log.LogFactory;
import com.autumn.util.IOUtils;
import com.autumn.util.TextUtils;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title: 系统变量存储对象</p>
 *
 * <p>
 * Description: 存储变量过期时间</p>
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
public class SystemAttribute {

    private Object object = null;//变量值
    private long stime = System.currentTimeMillis();//存入时间
    private long timeout = 0;//超时时长，0用不过时

    /**
     * 创建
     *
     * @param obj
     * @param timeout
     */
    public SystemAttribute(Object obj, long timeout) {
        this.object = obj;
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return object.toString();
    }

    /**
     * 变量是否过期
     *
     * @return boolean
     */
    public boolean isTimeOut() {
        if (timeout <= 0) {
            return false;
        } else {
            if (System.currentTimeMillis() > (stime + timeout)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 获取类名
     *
     * @return String
     */
    public String getClassName() {
        return object.getClass().getName();
    }

    /**
     * 是否需要重新加载
     *
     * @return boolean
     */
    public boolean classNeedReload() {
        try {
            Class cls = ClassManager.loadclass(object.getClass().getName());
            return cls != object.getClass();
        } catch (ClassNotFoundException ex) {
            return true;
        }
    }

    /**
     * @return the object
     */
    public Object getObject() {
        if (timeout <= 0) {
            return object;
        } else {
            if (System.currentTimeMillis() > (stime + timeout)) {
                return null;
            }
        }
        Object obj1 = checkClassLoader(object);
        if (obj1 != null) {
            if (object != obj1) {
                object = obj1;
                LogFactory.warn("{0}类型变量重新加载成功！", new Object[]{object.getClass().getName()}, SystemAttribute.class);
            }
            return object;
        }
        return null;
    }

    /**
     * @param obj the object to set
     */
    public void setObject(Object obj) {
        this.object = obj;
    }

    /**
     * 返回变量设置时间
     *
     * @return the stime
     */
    public String getStime() {
        return TextUtils.Date2String(new Date(stime));
    }

    /**
     * 返回变量过期时间
     *
     * @return the timeout
     */
    public String getEtime() {
        if (timeout <= 0) {
            return "--";
        }
        return TextUtils.Date2String(new Date(stime + timeout));
    }

    /**
     * 获取变量大小
     *
     * @return long
     */
    public long getObjectSize() {
        try {
            byte[] bs = IOUtils.objectToBytes(object);
            return bs.length;
        } catch (Exception e) {
            LogFactory.trace(object.getClass().getName() + "序列化错误!", e, SystemAttribute.class);
        }
        return -1;
    }

    /**
     * 获取变量
     *
     * @param attributes
     * @param key
     * @return Object
     */
    public static Object getAttribute(HashMap<String, SystemAttribute> attributes, String key) {
        if (key == null) {
            return null;
        }
        synchronized (attributes) {
            SystemAttribute sa = attributes.get(key);
            if (sa == null) {
                return null;
            } else {
                return sa.getObject();
            }
        }
    }

    /**
     * 检查classloader
     *
     * @param obj
     * @return Object
     */
    public static Object checkClassLoader(Object obj) {
        if (obj == null) {
            return null;
        }
        Class cls = obj.getClass();
        Class cls1;
        try {
            cls1 = ClassManager.loadclass(cls.getName());
        } catch (ClassNotFoundException ex) {
            LogFactory.error(cls.getName() + "不能加载：" + ex.getMessage(), SystemAttribute.class);
            return null;
        }
        if (cls == cls1) {
            return obj;
        } else {
            try {
                byte[] bs = IOUtils.objectToBytes(obj);
                Object obj1 = IOUtils.bytesToObject(bs, ClassManager.getClassLoader());
                return obj1;
            } catch (Exception e) {
                LogFactory.error(cls.getName() + "已过期，序列化时失败：" + e.getMessage(), SystemAttribute.class);
                return null;
            }
        }
    }

    /**
     * 设置变量
     *
     * @param attributes
     * @param key
     * @param value
     * @param timeout
     */
    public static void setAttribute(HashMap<String, SystemAttribute> attributes, String key, Object value, long timeout) {
        if (key == null || value == null) {
            LogFactory.error("系统变量：" + key + "设置失败！对象值为空", SystemAttribute.class);
            return;
        }
        synchronized (attributes) {
            attributes.put(key, new SystemAttribute(value, timeout));
        }
    }
}
