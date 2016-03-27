/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import com.autumn.core.ClassManager;
import com.autumn.core.log.LogFactory;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.jasper.runtime.HttpJspBase;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: 系统编译后的jsp服务</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class CompiledJspFilter implements ServletConfig {

    private static final ConcurrentHashMap<String, CompiledJspFilter> pathmap = new ConcurrentHashMap<String, CompiledJspFilter>();//jspservlet映射
    private static final ConcurrentHashMap<String, Boolean> sourcemap = new ConcurrentHashMap<String, Boolean>();//资源是否存在

    /**
     * 所请求jsp资源是否在磁盘上存在
     *
     * @param jsp
     * @return boolean
     */
    public static boolean findSource(String jsp) {
        Boolean bol = sourcemap.get(jsp);
        if (bol == null) {
            File f = new File(jsp);
            bol = f.exists() && f.isFile();
            sourcemap.put(jsp, bol);
        }
        return bol.booleanValue();
    }

    /**
     * 所请求jsp资源是否在已编译jsp的map中
     *
     * @param servlet
     * @return boolean
     */
    public static boolean exist(String servlet) {
        return pathmap.containsKey(servlet);
    }

    /**
     * 获取已编译jsp服务实例
     *
     * @param key
     * @return CompiledJspFilter
     */
    public static CompiledJspFilter getMap(String key) {
        return pathmap.get(key);
    }

    /**
     * 在系统初始化时加载已编译jsp服务，map
     */
    @SuppressWarnings("unchecked")
    public synchronized static void loadMap() {
        try {
            URL url = CompiledJspFilter.class.getResource("/com/autumn/jsp/web.xml");
            HashMap<String, String> map = new HashMap<String, String>();
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(url);
            Element root = doc.getRootElement();
            List<Element> list = root.getChildren("servlet");
            for (Element ee : list) {
                String servletname = ee.getChild("servlet-name").getText();
                String servletclass = ee.getChild("servlet-class").getText();
                map.put(servletname, servletclass);
            }
            list = root.getChildren("servlet-mapping");
            for (Element ee : list) {
                String servletname = ee.getChild("servlet-name").getText();
                String servletpath = ee.getChild("url-pattern").getText();
                String classname = map.get(servletname);
                if (classname != null) {
                    int index = servletpath.lastIndexOf("/");
                    servletpath = servletpath.substring(index + 1);
                    CompiledJspFilter jsm = new CompiledJspFilter(classname);
                    pathmap.put(servletpath, jsm);
                }
            }
            LogFactory.warn("加载JSPServlet完毕：" + url.getPath(), CompiledJspFilter.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpJspBase jspbase = null;

    /**
     * 创建已编译jsp服务实例
     *
     * @param classname
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public CompiledJspFilter(String classname) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class cls = ClassManager.loadclass(classname);
        jspbase = (HttpJspBase) cls.newInstance();
    }

    /**
     * @return the jspbase
     */
    public synchronized HttpJspBase getJspbase() throws ServletException {
        if (jspbase.getServletConfig() == null) {
            jspbase.init(this);
        }
        return jspbase;
    }

    @Override
    public String getServletName() {
        return "console_jspservlet";
    }

    @Override
    public ServletContext getServletContext() {
        return Initializer.servletContext;
    }

    @Override
    public String getInitParameter(String string) {
        return null;
    }

    @Override
    public Enumeration getInitParameterNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
