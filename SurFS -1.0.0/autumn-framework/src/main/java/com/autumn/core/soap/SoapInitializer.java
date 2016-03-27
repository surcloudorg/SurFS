/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.soap;

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
 * <p>Title:SOAP服务参数数据库访问实现</p>
 *
 * <p>Description:SOAP服务参数数据库访问实现</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SoapInitializer {

    /**
     * 更新服务配置
     *
     * @param id
     * @param param
     * @throws SQLException
     */
    public static void updateConfig(Connection con, int id, String param) throws SQLException {
        JdbcPerformer jt = new JdbcPerformer(con);
        jt.addParameter(param);
        jt.addParameter(id);
        jt.addParameter(WebFactory.getInstanceName());
        String sql = "update soaps set params=? where id=? and host=?";
        jt.update(sql);
    }

    /**
     * 查询服务
     *
     * @param conn
     * @return List<SoapContext>
     */
    public static List<SoapContext> initService(Connection conn) {
        return query(conn, 0);
    }

    /**
     * 查询服务
     *
     * @param conn
     * @param servicename
     * @return SoapContext
     */
    public static SoapContext initService(Connection conn, String servicename) {
        JdbcPerformer jt = new JdbcPerformer(conn);
        jt.addParameter(WebFactory.getInstanceName());
        jt.addParameter(servicename);
        String sql = "select id, title, servicename,ImplClass, className, authtype, Style, "
                + "UseType, params, IpList, infilter, outfilter, aegis,logname "
                + "from soaps where host=? and servicename=?";
        try {
            ResultSet crs = jt.query(sql);
            if (crs.next()) {
                return getSoapContext(crs);
            }
        } catch (Exception e) {
            LogFactory.error("初始化soap服务(" + servicename + ")参数失败:" + e.getMessage(), SoapInitializer.class);
        }
        return null;
    }

    /**
     * 查询服务
     * @param conn
     * @param id
     * @return SoapContext
     */
    public static SoapContext initService(Connection conn, int id) {
        List<SoapContext> list = query(conn, id);
        if (list != null && (!list.isEmpty())) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 查询服务
     *
     * @param conn
     * @param Id
     * @return List<SoapContext>
     */
    private static List<SoapContext> query(Connection conn, int id) {
        JdbcPerformer jt = new JdbcPerformer(conn);
        jt.addParameter(WebFactory.getInstanceName());
        String sql = "select id, title, servicename,ImplClass, className, authtype, Style, "
                + "UseType, params, IpList, infilter, outfilter, aegis,logname "
                + "from soaps where host=?";
        if (id > 0) {
            jt.addParameter(id);
            sql = sql + " and id=?";
        }
        List<SoapContext> services = new ArrayList<SoapContext>();
        try {
            ResultSet crs = jt.query(sql);
            while (crs.next()) {
                SoapContext action = getSoapContext(crs);
                services.add(action);
            }
            if (id == 0) {
                LogFactory.warn("初始化SOAP服务参数完毕！", SoapInitializer.class);
            }
        } catch (Exception e) {
            LogFactory.trace("初始化SOAP服务参数失败!", e, SoapInitializer.class);
        }
        return services;
    }

    /**
     * ResultSet--〉SoapContext
     *
     * @param rs
     * @return SoapContext
     * @throws Exception
     */
    private static SoapContext getSoapContext(ResultSet crs) throws SQLException, IOException {
        SoapContext action = new SoapContext();
        action.setId(crs.getInt("id"));
        action.setTitle(crs.getString("title"));
        action.setServicename(crs.getString("servicename"));
        action.setImplClass(crs.getString("ImplClass"));
        action.setClassName(crs.getString("className"));
        action.setAuthtype(crs.getInt("authtype"));
        action.setStyle(crs.getString("Style"));
        action.setUseType(crs.getString("UseType"));
        action.setParams(JdbcUtils.getResultSetStringValue(crs, "params"));
        action.setIpList(crs.getString("IpList"));
        action.setInfilter(crs.getString("infilter"));
        action.setOutfilter(crs.getString("outfilter"));
        action.setAegis(JdbcUtils.getResultSetStringValue(crs, "aegis"));
        action.setLogname(crs.getString("logname"));
        return action;
    }
}
