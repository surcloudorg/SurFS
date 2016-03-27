/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.service;

import com.autumn.core.log.LogFactory;
import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.web.WebFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:服务参数数据库访问实现</p>
 *
 * <p>Description:服务参数数据库访问实现</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ServiceInitializer {

    /**
     * 查询服务参数
     *
     * @param conn
     * @return List<ServiceConfig>
     */
    public static List<ServiceConfig> initService(Connection conn) {
        return query(conn, 0);
    }

    /**
     * 获取单个服务参数
     *
     * @param conn
     * @param id
     * @return ServiceConfig
     */
    public static ServiceConfig initService(Connection conn, int id) {
        List<ServiceConfig> list = query(conn, id);
        if (list != null && (!list.isEmpty())) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 查询服务参数
     *
     * @param conn
     * @param id
     * @return List<ServiceConfig>
     */
    private static List<ServiceConfig> query(Connection conn, int id) {
        JdbcPerformer jt = new JdbcPerformer(conn);
        jt.addParameter(WebFactory.getInstanceName());
        String sql = "select id, title,classname,params,logname,status from services where host=?";
        if (id > 0) {
            jt.addParameter(id);
            sql = sql + " and id=?";
        }
        List<ServiceConfig> services = new ArrayList<ServiceConfig>();
        try {
            ResultSet crs = jt.query(sql);
            while (crs.next()) {
                ServiceConfig action = getServiceConfig(crs);
                services.add(action);
            }
            if (id == 0) {
                LogFactory.warn("初始化服务参数完毕！", ServiceInitializer.class);
            }
        } catch (Exception e) {
            LogFactory.trace("初始化服务参数失败!", e, ServiceInitializer.class);
        }
        return services;
    }

    /**
     * 更新服务配置
     *
     * @param con
     * @param id
     * @param param
     * @throws SQLException
     */
    protected static void updateConfig(Connection con, int id, String param) throws SQLException {
        JdbcPerformer jt = new JdbcPerformer(con);
        jt.addParameter(param);
        jt.addParameter(id);
        jt.addParameter(WebFactory.getInstanceName());
        String sql = "update services set params=? where id=? and host=?";
        jt.update(sql);
    }

    /**
     * ResultSet--〉ServiceConfig
     *
     * @param rs
     * @return ServiceConfig
     * @throws Exception
     */
    private static ServiceConfig getServiceConfig(ResultSet crs) throws SQLException, IOException {
        ServiceConfig action = new ServiceConfig(crs.getInt("id"));
        action.setTitle(crs.getString("title"));
        action.setClassname(crs.getString("classname"));
        action.setParams(JdbcUtils.getResultSetStringValue(crs, "params"));
        action.setLogname(crs.getString("logname"));
        action.setStatus(crs.getInt("status"));
        return action;
    }
}
