package com.autumn.core.web;

import com.autumn.core.cfg.Config;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: web目录配置</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WebDirectoryConfig implements FilterConfig, ServletConfig {

    WebDirectory dir = null;

    public WebDirectoryConfig(WebDirectory dir) {
        this.dir = dir;
    }

    @Override
    public String getFilterName() {
        return "Custom_filter";
    }

    @Override
    public ServletContext getServletContext() {
        return Initializer.servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        if (dir == null) {
            return null;
        }
        Config cfg = dir.getConfig();
        if (cfg != null) {
            return cfg.getAttributeValue(name);
        }
        Properties p = dir.getProperties();
        if (p != null) {
            return p.getProperty(name);
        }
        return null;
    }

    @Override
    public Enumeration getInitParameterNames() {
        if (dir == null) {
            return null;
        }
        Config cfg = dir.getConfig();
        if (cfg != null) {
            return cfg.getAttributeNames();
        }
        Properties p = dir.getProperties();
        if (p != null) {
            return p.propertyNames();
        }
        return null;
    }

    @Override
    public String getServletName() {
        return "soap_services";
    }
}
