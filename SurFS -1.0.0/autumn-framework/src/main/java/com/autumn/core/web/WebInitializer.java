/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.LogInitializer;
import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.sql.SortedSQLException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title:WEB服务参数数据库访问</p>
 *
 * <p>
 * Description:WEB服务参数数据库访问实现</p>
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
public class WebInitializer {

    /**
     * 创建根目录
     *
     * @param con
     */
    public static void createRootDirectory(Connection con) {
        JdbcPerformer jt = new JdbcPerformer(con);
        String sql = "select * from webdirectory where dirname=? and host=?";
        Object[] args = new Object[]{"root", WebFactory.getInstanceName()};
        jt.addParameter(args);
        try {
            if (jt.query(sql).next()) {
                return;
            }
            args = new Object[]{WebFactory.getInstanceName(), "root", "根目录", 2, "index.html", LogFactory.SYSTEM_LOGNAME};
            jt.addParameter(args);
            sql = "insert into webdirectory(host,dirname,title,logintype,defaultpage,logname) values(?,?,?,?,?,?)";
            jt.update(sql);
        } catch (SQLException se) {
            if (se instanceof SortedSQLException
                    && ((SortedSQLException) se).getExceptionType() != SortedSQLException.DuplicateKeyException) {
                LogFactory.error("创建WEB根目录失败：" + se.getMessage(), LogInitializer.class);
            }
        }
    }

    /**
     * ResultSet--〉ActionMap
     *
     * @param crs
     * @return ActionMap
     * @throws SQLException
     * @throws IOException
     */
    private static ActionMap getActionMap(ResultSet crs) throws SQLException, IOException {
        ActionMap action = new ActionMap(crs.getString("actionid").toLowerCase());
        action.setClassname(crs.getString("classname"));
        action.setDirid(crs.getInt("dirId"));
        action.setMenu(crs.getString("menu"));
        action.setParams(JdbcUtils.getResultSetStringValue(crs, "params"));
        action.setPermissionorder(crs.getInt("permissionorder"));
        action.setFunctionName(crs.getString("subdir"));
        return action;
    }

    /**
     * ResultSet--〉WebDirectory
     *
     * @param crs
     * @return WebDirectory
     * @throws SQLException
     * @throws IOException
     */
    private static WebDirectory getWebDirectory(ResultSet crs) throws SQLException, IOException {
        WebDirectory web = new WebDirectory(crs.getInt("id"), crs.getString("dirname"));
        web.setCharset(crs.getString("charset"));
        web.setDefaultPage(crs.getString("DefaultPage"));
        web.setParams(JdbcUtils.getResultSetStringValue(crs, "params"));
        web.setIpList(crs.getString("iplist"));
        web.setLogintype(crs.getInt("Logintype"));
        web.setLogname(crs.getString("logname"));
        web.setTitle(crs.getString("title"));
        web.setClassname(crs.getString("classname"));
        return web;
    }

    /**
     * 获取单个web目录参数
     *
     * @param conn Connection
     * @param dname String 目录名
     * @return List<WebDirectory>
     */
    public static WebDirectory initService(Connection conn, String dname) {
        String sql = "select dirid,id, actionid, subdir, classname,permissionorder,params, menu from actionmap where dirid in(select id from webdirectory where dirname=?)";
        String sqldir = "select id,dirname,title,classname,defaultpage,iplist,logintype,params,logname,charset from webdirectory where host=? and dirname=?";
        JdbcPerformer jt = new JdbcPerformer(conn);
        jt.addParameter(WebFactory.getInstanceName());
        jt.addParameter(dname);
        try {
            ResultSet crs = jt.query(sqldir);
            if (crs.next()) {
                WebDirectory web = getWebDirectory(crs);
                jt.clearParameters();
                jt.addParameter(dname);
                crs = jt.query(sql);
                while (crs.next()) {
                    ActionMap action = getActionMap(crs);
                    web.addAction(action);
                }
                return web;
            }
        } catch (Exception e) {
            LogFactory.error("初始化web服务参数(" + dname + ")失败:" + e.getMessage(), WebInitializer.class);
        }
        return null;
    }

    /**
     * 获取单个web目录参数
     *
     * @param conn
     * @param dirId
     * @return WebDirectory
     */
    public static WebDirectory initService(Connection conn, int dirId) {
        List<WebDirectory> list = query(conn, dirId);
        if (list != null && (!list.isEmpty())) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 初始化
     *
     * @param conn
     * @return List<WebDirectory>
     */
    public static List<WebDirectory> initService(Connection conn) {
        return query(conn, 0);
    }

    /**
     * 查询目录参数
     *
     * @param conn Connection
     * @param dirId int 目录id,=0返回全部
     * @return List<WebDirectory>
     */
    private static List<WebDirectory> query(Connection conn, int dirId) {
        String sql = "select dirid,id, actionid, subdir, classname,permissionorder,params, menu from actionmap";
        JdbcPerformer jt = new JdbcPerformer(conn);
        String sqldir = "select id,dirname,title,classname,defaultpage,iplist,logintype,params,logname,charset from webdirectory where host=?";
        JdbcPerformer jtdir = new JdbcPerformer(conn);
        jtdir.addParameter(WebFactory.getInstanceName());
        if (dirId > 0) {
            sql = sql + " where dirid=?";
            jt.addParameter(dirId);
            sqldir = sqldir + " and id=?";
            jtdir.addParameter(dirId);
        }
        List<ActionMap> actions = new ArrayList<ActionMap>();
        List<WebDirectory> webs = new ArrayList<WebDirectory>();
        try {
            ResultSet crs = jt.query(sql);
            while (crs.next()) {
                actions.add(getActionMap(crs));
            }
            ResultSet crsdir = jtdir.query(sqldir);
            while (crsdir.next()) {
                WebDirectory web = getWebDirectory(crsdir);
                for (ActionMap action : actions) {
                    if (action.getDirid() == web.getId()) {
                        web.addAction(action);
                    }
                }
                if (web.getDirName().equalsIgnoreCase("root")) {
                    webs.add(0, web);
                } else {
                    webs.add(web);
                }
            }
            if (dirId == 0) {
                LogFactory.warn("初始化web服务参数完毕！", WebInitializer.class);
            }
        } catch (Exception e) {
            LogFactory.error("初始化web服务参数(" + dirId + ")失败:" + e.getMessage(), WebInitializer.class);
        }
        return webs;
    }

    /**
     * 更新服务配置
     *
     * @param con
     * @param id
     * @param param
     * @throws SQLException
     */
    public static void updateConfig(Connection con, int id, String param) throws SQLException {
        JdbcPerformer jt = new JdbcPerformer(con);
        jt.addParameter(param);
        jt.addParameter(id);
        jt.addParameter(WebFactory.getInstanceName());
        String sql = "update webdirectory set params=? where id=? and host=?";
        jt.update(sql);
    }

    /**
     * 添加console/services等系统控制目录
     *
     * @return List<WebDirectory>
     */
    public static List<WebDirectory> initService() {
        List<WebDirectory> webs = new ArrayList<WebDirectory>();
        WebDirectory cfg = new WebDirectory(0, "console");
        cfg.setCharset("UTF-8");
        cfg.setTitle("系统控制台");
        cfg.setDefaultPage("default.jsp");

        ActionMap action = new ActionMap("services.do");
        action.setClassname(com.autumn.console.ServiceManager.class.getName());
        action.setPermissionorder(0);
        action.setMenu("01)服务管理");
        cfg.addAction(action);

        action = new ActionMap("soaps.do");
        action.setClassname(com.autumn.console.SoapManager.class.getName());
        action.setPermissionorder(1);
        action.setMenu("02)SOAP服务");
        cfg.addAction(action);

        action = new ActionMap("dbpools.do");
        action.setClassname(com.autumn.console.DbSourceManager.class.getName());
        action.setPermissionorder(2);
        action.setMenu("03)数据库连接池");
        cfg.addAction(action);

        action = new ActionMap("sqltest.do");
        action.setClassname(com.autumn.console.SqlTest.class.getName());
        action.setPermissionorder(2);
        cfg.addAction(action);

        action = new ActionMap("hbms.do");
        action.setClassname(com.autumn.console.HBMSourceMgr.class.getName());
        action.setPermissionorder(3);
        action.setMenu("04)Hibernate映射");
        cfg.addAction(action);

        action = new ActionMap("logsystem.do");
        action.setClassname(com.autumn.console.LogManager.class.getName());
        action.setPermissionorder(4);
        action.setMenu("05)日志查看及设置");
        cfg.addAction(action);

        action = new ActionMap("webs.do");
        action.setClassname(com.autumn.console.WebManager.class.getName());
        action.setPermissionorder(5);
        action.setMenu("06)WEB目录");
        cfg.addAction(action);

        action = new ActionMap("actions.do");
        action.setClassname(com.autumn.console.ActionMaps.class.getName());
        action.setPermissionorder(5);
        action.setMenu("07)页面控制器");
        cfg.addAction(action);

        action = new ActionMap("users.do");
        action.setClassname(com.autumn.console.UserManager.class.getName());
        action.setPermissionorder(6);
        action.setMenu("08)登录账号");
        cfg.addAction(action);

        action = new ActionMap("svncodes.do");
        action.setClassname(com.autumn.console.SvnCodes.class.getName());
        action.setPermissionorder(7);
        action.setMenu("09)工程源码同步");
        cfg.addAction(action);

        action = new ActionMap("upload.do");
        action.setClassname(com.autumn.console.FileManager.class.getName());
        action.setPermissionorder(8);
        action.setMenu("10)文件操作");
        cfg.addAction(action);

        action = new ActionMap("system.do");
        action.setClassname(com.autumn.console.SystemReset.class.getName());
        action.setPermissionorder(9);
        action.setMenu("11)系统设置");
        cfg.addAction(action);
        action = new ActionMap("sysproperties.do");
        action.setClassname(com.autumn.console.SystemProperties.class.getName());
        action.setPermissionorder(9);
        cfg.addAction(action);
        action = new ActionMap("systhreads.do");
        action.setClassname(com.autumn.console.SystemThreads.class.getName());
        action.setPermissionorder(9);
        cfg.addAction(action);
        action = new ActionMap("sysmemory.do");
        action.setClassname(com.autumn.console.SystemMemory.class.getName());
        action.setPermissionorder(9);
        cfg.addAction(action);

        action = new ActionMap("modifyuser.do");
        action.setClassname(com.autumn.console.UserModify.class.getName());
        action.setPermissionorder(-1);
        action.setMenu("12)修改密码");
        cfg.addAction(action);

        action = new ActionMap("logfind.do");
        action.setClassname(com.autumn.console.LogFinder.class.getName());
        action.setPermissionorder(4);
        cfg.addAction(action);

        action = new ActionMap("menu.do");
        action.setClassname(com.autumn.console.MenuAction.class.getName());
        action.setPermissionorder(-1);
        cfg.addAction(action);
        webs.add(cfg);

        cfg = new WebDirectory(-1, "services");
        cfg.setTitle("Soap服务");
        cfg.setDefaultPage("index.html");
        cfg.setIpList("");
        cfg.setLogintype(2);
        webs.add(cfg);
        return webs;
    }
}
