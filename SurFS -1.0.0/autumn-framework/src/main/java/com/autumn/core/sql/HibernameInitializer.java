/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.sql;

import com.autumn.core.log.LogFactory;
import com.autumn.core.web.WebFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:Hibername映射数据库访问实现</p>
 *
 * <p>Description:Hibername映射数据库访问实现</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class HibernameInitializer {
    

    /**
     * 初始化对话连接池
     *
     * @param jdbc
     * @return HibernateMapping[]
     */
    public static HibernateMapping[] initSessionSource(String jdbc) {
        List<HibernateMapping> list = new ArrayList<HibernateMapping>();
        Connection con = ConnectionFactory.getConnect(ConnectionFactory.systemSourceName, HibernameInitializer.class);
        JdbcTemplate jt = new JdbcPerformer(con);
        jt.addParameter(WebFactory.getInstanceName());
        jt.addParameter(jdbc);
        String sql = "select * FROM hibernatemap WHERE host=? and datasource=?";
        try {
            ResultSet rs = jt.query(sql);
            while (rs.next()) {
                try {
                    HibernateMapping param = getHibernateMapping(rs);
                    list.add(param);
                } catch (Exception ex) {
                    LogFactory.trace("读取Hibername映射失败!", ex, DataSourceInitializer.class);
                }
            }
            return (HibernateMapping[]) list.toArray(new HibernateMapping[list.size()]);
        } catch (SQLException ex) {
            LogFactory.error("查询Hibername映射失败:" + ex.getMessage(), DataSourceInitializer.class);
        } finally {
            JdbcUtils.closeConnect(con);
        }
        return null;
    }

    /**
     * 组装为HibernateMapping
     *
     * @param rs
     * @return ConnectionParam
     * @throws SQLException
     */
    private static HibernateMapping getHibernateMapping(ResultSet rs) throws Exception {
        HibernateMapping cfg = new HibernateMapping(
                JdbcUtils.getResultSetStringValue(rs, "xmlmap"),
                rs.getString("classname"),
                rs.getString("tablename"),
                rs.getString("catalogname"));
        return cfg;
    }
}
