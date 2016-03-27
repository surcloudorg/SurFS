/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.service;

import com.autumn.core.ClassManager;
import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.ConfigListener;
import com.autumn.core.cfg.ConfigParser;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <p>Title: 服务配置参数</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ServiceConfig {

    private int id = 0; //id
    private String title = "";//标题
    private String classname = null;//实现ServiceImpl接口的类名
    private String logname =  LogFactory.SYSTEM_LOGNAME;//日志输出目录
    private int status = 0;//0随服务器启动1手动2禁用
    private final ConfigParser params = new ConfigParser();//服务运行时配置（string，Config、properties格式）

    public ServiceConfig(int id) {
        this.id = id;
    }

    /**
     * 获取日志输出
     *
     * @return Logger
     */
    public Logger getLogger() {
        return LogFactory.getLogger(logname);
    }

    /**
     * 保存params
     *
     * @throws SQLException
     * @throws java.io.IOException
     */
    public void saveConfig() throws SQLException, IOException {
        Connection con = ConnectionFactory.getConnect(ServiceConfig.class);
        try {
            saveConfig(con);
        } catch (SQLException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            JdbcUtils.closeConnect(con);
        }
    }

    /**
     * 保存params
     *
     * @param con
     * @throws SQLException
     * @throws java.io.IOException
     */
    public void saveConfig(Connection con) throws SQLException, IOException {
        params.update();
        String param = params.getString();
        if (param != null) {
            ServiceInitializer.updateConfig(con, id, param);
        }
    }

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    public Connection getConnect() {
        if (getConfig() != null) {
            String jdbc = getConfig().getAttributeValue("config.datasource");
            if (jdbc != null) {
                return ConnectionFactory.getConnect(jdbc, this.getClass());
            }
        }
        if (getProperties() != null) {
            String jdbc = getProperties().getProperty("datasource");
            if (jdbc != null) {
                return ConnectionFactory.getConnect(jdbc, this.getClass());
            }
        }
        return null;
    }

    /**
     * 获取服务接口实现实例
     *
     * @return ServiceImpl
     * @throws Exception
     */
    public ServiceImpl getService() throws Exception {
        if (classname == null || classname.isEmpty()) {
            throw new Exception("没有指定启动类，无法启动！");
        }
        Class cls = ClassManager.loadclass(classname);
        Object obj = cls.newInstance();
        if (!(obj instanceof ServiceImpl)) {
            throw new Exception(classname + "必须实现" + ServiceImpl.class.getName() + "接口");
        }
        Config config = getConfig();
        if (config != null) {
            if (obj instanceof ConfigListener) {
                config.setListener((ConfigListener) obj);
                getLogger().warn("服务(" + getId() + ")设置配置监听器：" + classname, Service.class);
            }
        }
        return (ServiceImpl) obj;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the classname
     */
    public String getClassname() {
        return classname;
    }

    /**
     * @param classname the classname to set
     */
    public void setClassname(String classname) {
        this.classname = classname == null ? null : classname.trim();
    }

    /**
     * @return the logname
     */
    public String getLogname() {
        return logname;
    }

    /**
     * @param logname the logname to set
     */
    public void setLogname(String logname) {
        this.logname = logname;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取配置
     *
     * @return Properties
     */
    public Properties getProperties() {
        return params.getProperties();
    }

    /**
     * 获取配置
     *
     * @return Config
     */
    public Config getConfig() {
        return params.getConfig();
    }

    /**
     * @return the params
     */
    public String getParams() {
        return params.getString();
    }

    /**
     * @param params the params to set
     */
    public synchronized void setParams(String params) {
        this.params.parse(params);
    }
}
