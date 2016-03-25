package com.autumn.core;

import com.autumn.core.log.LogFactory;
import com.autumn.core.sql.HibernateSessionFactory;
import com.autumn.util.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 * Title: 类加载器</p>
 *
 * <p>
 * Description: 从磁盘加载class</p>
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
public class ClassManager extends ClassLoader {

    private static final Object classloadlock = new Object(); //默认classloader
    private static final List<String> classPathList = new ArrayList<String>(); //classpath
    private static ClassLoader currentClassload = null; //默认classloader
    private static ClassLoader systemClassload = null; //默认classloader

    static { //初始化classloader
        currentClassload = Thread.currentThread().getContextClassLoader();
        systemClassload = Thread.currentThread().getContextClassLoader();
        initClassPathList();
    }

    /**
     * 初始化classpath,去掉jar路径
     */
    private static void initClassPathList() {
        String classPath = System.getProperty("java.class.path");
        if ((classPath != null) && !(classPath.equals(""))) {
            StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);
            while (tokenizer.hasMoreTokens()) {
                String ss = tokenizer.nextToken();
                if (!ss.toLowerCase().endsWith(".jar")) {
                    addClassPath(ss);
                }
            }
        }
    }

    /**
     * 添加classpath
     *
     * @param classpath
     */
    public static void addClassPath(String classpath) {
        if (!classpath.endsWith(File.separator)) {
            classpath = classpath + File.separator;
        }
        synchronized (classPathList) {
            if (!classPathList.contains(classpath)) {
                classPathList.add(0, classpath);
            }
        }
    }

    /**
     * 获取当前类加载器
     *
     * @return ClassLoader
     */
    public static ClassLoader getClassLoader() {
        synchronized (classloadlock) {
            return currentClassload;
        }
    }

    /**
     * 从当前类加载器查找class,代替forname函数
     *
     * @param className String
     * @return Class
     * @throws java.lang.ClassNotFoundException
     */
    public static Class loadclass(String className) throws ClassNotFoundException {
        synchronized (classloadlock) {
            return currentClassload.loadClass(className);
        }
    }

    /**
     * 重载class,新建空ClassLoader
     */
    public static void reloadclass() {
        synchronized (classloadlock) {
            currentClassload = new ClassManager(systemClassload);
            HibernateSessionFactory.rebuild();//重载后，HibernateSessionFactory需重建
        }
        LogFactory.warn("新建ClassLoader", ClassManager.class);
    }
    private HashMap<String, Class> classesList = new HashMap<String, Class>();

    /**
     * 默认构造方法
     */
    public ClassManager() {
        super(ClassManager.class.getClassLoader());
        classesList.clear();
    }

    /**
     * 构造方法
     *
     * @param parent ClassLoader
     */
    public ClassManager(ClassLoader parent) {
        super(parent);
        classesList.clear();
    }

    /**
     * 从classpath查找class文件
     *
     * @param className
     * @return File
     */
    public synchronized static File getClassFileName(String className) {
        String classFileName = className.replace('.', File.separatorChar);
        classFileName += ".class";
        String realFileName;
        for (String dir : classPathList) {
            realFileName = dir + classFileName;
            File file = new File(realFileName);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }

    /**
     * 从classpath加载class
     *
     * @param className String
     * @return byte[]
     */
    private synchronized static byte[] getClassBytes(String className) {
        File file = getClassFileName(className);
        if (file != null) {
            try {
                InputStream is = new FileInputStream(file);
                return IOUtils.read(is);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    /**
     * 获取Class
     *
     * @param className String 类名
     * @return Class classloader存在从classloader返回,否则从classesList查找,没有从磁盘加载
     * @throws ClassNotFoundException
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class loadClass(String className) throws ClassNotFoundException {
        Class c = findLoadedClass(className);
        if (c != null) {
            return c;
        }
        c = classesList.get(className);
        if (c != null) {
            return c;
        }
        c = super.loadClass(className);
        if (className.startsWith("com.autumn.")) {
            return c;
        }
        if (c != null) {
            if (c.isAnnotationPresent(ReloadForbidden.class)) {
                LogFactory.warn(className + "被禁止重载！", ClassManager.class);
                classesList.put(className, c);
                return c;
            }
        }
        c = this.reLoadClass(className);//从磁盘加载
        if (c != null) {
            return c;
        }
        c = super.loadClass(className);
        if (c != null) {
            classesList.put(className, c);
        }
        if (c == null) {
            throw new ClassNotFoundException(className + "不能加载！");
        }
        return c;
    }

    /**
     * 从bytes载入class
     *
     * @param className String
     * @param classBytes byte[]
     * @return Class
     */
    public Class loadBytesClass(String className, byte[] classBytes) {
        if (classBytes == null) {
            return null;
        }
        Class c = this.defineClass(className, classBytes, 0, classBytes.length);
        LogFactory.warn("加载类:{0}", new Object[]{className}, ClassManager.class);
        return c;
    }

    /**
     * 从本地文件重新载入class
     *
     * @param className String
     * @return Class
     */
    private Class reLoadClass(String className) {
        byte[] classBytes = getClassBytes(className);
        if (classBytes != null) {
            return loadBytesClass(className, classBytes);
        }
        return null;
    }
}
