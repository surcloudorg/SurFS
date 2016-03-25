package com.autumn.core;

import com.autumn.core.log.LogFactory;
import com.autumn.core.service.ServiceConfig;
import com.autumn.core.service.ServiceFactory;
import com.autumn.core.soap.SoapContext;
import com.autumn.core.soap.SoapFactory;
import com.autumn.core.web.WebDirectory;
import com.autumn.core.web.WebFactory;
import com.autumn.util.FileOperation;
import com.autumn.util.IOUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * <p>Title: 系统变量存储器</p>
 *
 * <p>Description: 为不同服务创建单独的变量存储空间</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SystemAttributes {

    protected static final HashMap<String, SystemAttribute> sysattribute = new HashMap<String, SystemAttribute>();//系统变量
    protected static final HashMap<Integer, HashMap<String, SystemAttribute>> servicesattribute = new HashMap<Integer, HashMap<String, SystemAttribute>>();//服务变量
    protected static final HashMap<Integer, HashMap<String, SystemAttribute>> soapattribute = new HashMap<Integer, HashMap<String, SystemAttribute>>();//soap服务变量
    protected static final HashMap<Integer, HashMap<String, SystemAttribute>> webattribute = new HashMap<Integer, HashMap<String, SystemAttribute>>();//web服务变量

    /**
     * 获取服务变量
     *
     * @return HashMap<String, Object>
     */
    public static HashMap<Integer, HashMap<String, SystemAttribute>> getServicesAttribute() {
        return new HashMap<Integer, HashMap<String, SystemAttribute>>(servicesattribute);
    }

    /**
     * 获取soap服务变量
     *
     * @return HashMap<String, Object>
     */
    public static HashMap<Integer, HashMap<String, SystemAttribute>> getSoapAttribute() {
        return new HashMap<Integer, HashMap<String, SystemAttribute>>(soapattribute);
    }

    /**
     * 获取web服务变量
     *
     * @return HashMap<String, Object>
     */
    public static HashMap<Integer, HashMap<String, SystemAttribute>> getWebAttribute() {
        return new HashMap<Integer, HashMap<String, SystemAttribute>>(webattribute);
    }

    /**
     * 获取系统变量
     *
     * @return HashMap<String, Object>
     */
    public static HashMap<String, SystemAttribute> getSysAttribute() {
        return new HashMap<String, SystemAttribute>(sysattribute);
    }

    /**
     * 变量持久化存储路径
     *
     * @return File
     */
    private static synchronized File getPath() {
        String usepath = System.getProperty("user.dir");
        usepath = usepath == null ? "" : usepath;
        String filename = usepath.endsWith(File.separator) ? usepath + "attributes" : usepath + File.separator + "attributes";
        ServiceConfig sc = ServiceFactory.getServiceConfig();
        if (sc != null) {
            filename = filename + File.separator + "service" + File.separator + sc.getId();
            return new File(filename);
        }
        WebDirectory wd = WebFactory.getWebDirectory();
        if (wd != null) {
            filename = filename + File.separator + "web" + File.separator + wd.getId();
            return new File(filename);
        }
        SoapContext sct = SoapFactory.getSoapContext();
        if (sct != null) {
            filename = filename + File.separator + "soap" + File.separator + sct.getId();
            return new File(filename);
        }
        return new File(filename);
    }

    /**
     * 获取单个服务的系统变量表
     *
     * @return HashMap<String, SystemAttribute>
     */
    private static synchronized HashMap<String, SystemAttribute> getMap() {
        ServiceConfig sc = ServiceFactory.getServiceConfig();
        if (sc != null) {
            HashMap<String, SystemAttribute> attributes = servicesattribute.get(sc.getId());
            if (attributes == null) {
                attributes = new HashMap<String, SystemAttribute>();
                servicesattribute.put(sc.getId(), attributes);
            }
            return attributes;
        }
        WebDirectory wd = WebFactory.getWebDirectory();
        if (wd != null) {
            HashMap<String, SystemAttribute> attributes = webattribute.get(wd.getId());
            if (attributes == null) {
                attributes = new HashMap<String, SystemAttribute>();
                webattribute.put(wd.getId(), attributes);
            }
            return attributes;
        }
        SoapContext sct = SoapFactory.getSoapContext();
        if (sct != null) {
            HashMap<String, SystemAttribute> attributes = soapattribute.get(sct.getId());
            if (attributes == null) {
                attributes = new HashMap<String, SystemAttribute>();
                soapattribute.put(sct.getId(), attributes);
            }
            return attributes;
        }
        return sysattribute;
    }

    /**
     * 单个服务的系统变量表，指定变量空间
     *
     * @param namespace
     * @return HashMap<String, SystemAttribute>
     */
    private static synchronized HashMap<String, SystemAttribute> getMap(String namespace) {
        if (namespace == null) {
            return sysattribute;
        }
        namespace = namespace.trim();
        if (namespace.startsWith("/service/")) {
            try {
                int id = Integer.parseInt(namespace.substring(9));
                HashMap<String, SystemAttribute> attributes = servicesattribute.get(id);
                if (attributes == null) {
                    attributes = new HashMap<String, SystemAttribute>();
                    servicesattribute.put(id, attributes);
                }
                return attributes;
            } catch (Exception e) {
            }
        }
        if (namespace.startsWith("/soap/")) {
            try {
                int id = Integer.parseInt(namespace.substring(6));
                HashMap<String, SystemAttribute> attributes = soapattribute.get(id);
                if (attributes == null) {
                    attributes = new HashMap<String, SystemAttribute>();
                    soapattribute.put(id, attributes);
                }
                return attributes;
            } catch (Exception e) {
            }
        }
        if (namespace.startsWith("/web/")) {
            try {
                int id = Integer.parseInt(namespace.substring(5));
                HashMap<String, SystemAttribute> attributes = webattribute.get(id);
                if (attributes == null) {
                    attributes = new HashMap<String, SystemAttribute>();
                    webattribute.put(id, attributes);
                }
                return attributes;
            } catch (Exception e) {
            }
        }
        return sysattribute;
    }

    /**
     * 设置变量
     *
     * @param namespace
     * @param key
     * @param value
     */
    public static void setAttribute(String namespace, String key, Object value) {
        setAttribute(namespace, key, value, 0);
    }

    /**
     * 设置变量
     *
     * @param namespace
     * @param key
     * @param value
     * @param timeout
     */
    public static void setAttribute(String namespace, String key, Object value, long timeout) {
        HashMap<String, SystemAttribute> attributes = getMap(namespace);
        SystemAttribute.setAttribute(attributes, key, value, timeout);
    }

    /**
     * 设置属性值
     *
     * @param key
     * @param value
     * @param timeout
     */
    public static void setAttribute(String key, Object value, long timeout) {
        HashMap<String, SystemAttribute> attributes = getMap();
        SystemAttribute.setAttribute(attributes, key, value, timeout);
    }

    /**
     * 设置属性值
     *
     * @param key
     * @param value
     */
    public static void setAttribute(String key, Object value) {
        setAttribute(key, value, 0);
    }

    /**
     * 获取变量
     *
     * @param namespace
     * @param key
     * @return Object
     */
    public static Object getAttribute(String namespace, String key) {
        HashMap<String, SystemAttribute> attributes = getMap(namespace);
        return SystemAttribute.getAttribute(attributes, key);
    }

    /**
     * 获取属性值
     *
     * @param key
     * @return Object
     */
    public static Object getAttribute(String key) {
        HashMap<String, SystemAttribute> attributes = getMap();
        return SystemAttribute.getAttribute(attributes, key);
    }

    /**
     * 剔除属性
     *
     * @param key
     * @return Object
     */
    public static Object removeAttribute(String namespace, String key) {
        HashMap<String, SystemAttribute> attributes = getMap(namespace);
        synchronized (attributes) {
            SystemAttribute sa = attributes.remove(key);
            if (sa == null) {
                return null;
            } else {
                return sa.getObject();
            }
        }
    }

    /**
     * 剔除属性
     *
     * @param key
     * @return Object
     */
    public static Object removeAttribute(String key) {
        HashMap<String, SystemAttribute> attributes = getMap();
        synchronized (attributes) {
            SystemAttribute sa = attributes.remove(key);
            if (sa == null) {
                return null;
            } else {
                return sa.getObject();
            }
        }
    }

    /**
     * 清所有属性
     */
    public static void clearAttribute(String namespace) {
        HashMap<String, SystemAttribute> attributes = getMap(namespace);
        synchronized (attributes) {
            attributes.clear();
        }
    }

    /**
     * 清所有属性
     */
    public static void clearAttribute() {
        HashMap<String, SystemAttribute> attributes = getMap();
        synchronized (attributes) {
            attributes.clear();
        }
    }

    /**
     * 获取变量表
     *
     * @return HashMap<String, SystemAttribute>
     */
    public static HashMap<String, SystemAttribute> getAttributes() {
        HashMap<String, SystemAttribute> attributes = getMap();
        if (attributes != null) {
            return new HashMap<String, SystemAttribute>(attributes);
        } else {
            return null;
        }
    }

    /**
     * 持久化变量
     *
     * @param key
     * @throws IOException
     */
    public static void saveAttribute(String key) throws IOException {
        File path = getPath();
        if (!path.exists()) {
            path.mkdirs();
        }
        String filename = path.getAbsolutePath() + File.separator + DataCommand.enCode(key.toString().getBytes());
        Object obj = getAttribute(key);
        if (obj != null) {
            byte[] bs = IOUtils.objectToBytes(obj);
            if (obj != null) {
                FileOperation.writeFile(bs, filename);
            }
            LogFactory.warn("存储系统变量" + key + "成功！", SystemAttributes.class);
        } else {
            File f = new File(filename);
            if (f.exists()) {
                f.delete();
            }
        }
    }

    /**
     * 加载变量
     *
     * @param key
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object loadAttribute(String key) throws IOException, ClassNotFoundException {
        String filename = getPath().getAbsolutePath() + File.separator + DataCommand.enCode(key.toString().getBytes());
        Object obj = null;
        File f = new File(filename);
        if (f.exists()) {
            byte[] bs = FileOperation.readFile(filename);
            obj = IOUtils.bytesToObject(bs, ClassManager.getClassLoader());
            if (obj != null) {
                setAttribute(key, obj);
            }
            LogFactory.warn("加载系统变量" + key + "成功！", SystemAttributes.class);
            f.delete();
        }
        return obj;
    }
}
