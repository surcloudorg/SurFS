package com.autumn.core.sql;

import com.autumn.core.log.LogFactory;
import com.autumn.core.web.WebFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Title:数据库连接池数据库访问实现</p>
 *
 * <p>Description:初始化连接池，创建系统数据源</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class DataSourceInitializer {

    /**
     * 创建系统数据库连接池
     *
     * @param con
     * @param url
     * @param driver
     * @param user
     * @param pwd
     */
    public static void createSystemSource(Connection con, String url, String driver, String user, String pwd) {
        JdbcTemplate jt = new JdbcPerformer(con);
        jt.addParameter(ConnectionFactory.systemSourceName);
        jt.addParameter(WebFactory.getInstanceName());
        String sql = "select * from datasource where jndiname=? and host=?";
        ResultSet rs;
        try {
            rs = jt.query(sql);
            if (rs.next()) {
                sql = "update datasource set driver=?,dburl=?,username=?,pwd=?,maxconnection=?,minconnection=?,"
                        + "timeoutvalue=?,testsql=?,maxstatement=? where jndiname=? and host=?";
            } else {
                sql = "insert into datasource(driver,dburl,username,pwd,maxconnection,minconnection,"
                        + "timeoutvalue,testsql,maxstatement,jndiname,host) values(?,?,?,?,?,?,?,?,?,?,?)";
            }
            String test;
            if (con.getMetaData().getDatabaseProductName().equalsIgnoreCase("apache derby")) {
                test = "";
            } else if (con.getMetaData().getDatabaseProductName().equalsIgnoreCase("oracle")) {
                test = "select 1 from dual";
            } else {
                test = "select 1";
            }
            jt.clearParameters();
            jt.addParameter(new Object[]{driver, url, user, pwd, 100, 5, 600000, test, 50});
            jt.addParameter(ConnectionFactory.systemSourceName);
            jt.addParameter(WebFactory.getInstanceName());
            jt.update(sql);
        } catch (SQLException ex) {
            LogFactory.error("创建系统连接池(" + ConnectionFactory.systemSourceName + ")失败:" + ex.getMessage(), DataSourceInitializer.class);
        }
    }

    /**
     * 初始化连接池
     *
     * @param con
     */
    public static void initDataSource(Connection con) {
        JdbcTemplate jt = new JdbcPerformer(con);
        jt.addParameter(WebFactory.getInstanceName());
        String sql = "select * from datasource where host=?";
        try {
            ResultSet rs = jt.query(sql);
            while (rs.next()) {
                try {
                    ConnectionParam param = getConnectionParam(rs);
                    ConnectionFactory.bind(param);
                } catch (Exception ex) {
                    LogFactory.trace("创建数据库连接池失败!", ex, DataSourceInitializer.class);
                }
            }
        } catch (SQLException ex) {
            LogFactory.error("从数据库读取连接池配置失败:" + ex.getMessage(), DataSourceInitializer.class);
        }
    }

    /**
     * 组装为ConnectionParam
     *
     * @param rs
     * @return ConnectionParam
     * @throws SQLException
     */
    private static ConnectionParam getConnectionParam(ResultSet rs) throws Exception {
        ConnectionParam cfg = new ConnectionParam(rs.getString("jndiname"));
        cfg.setDriver(rs.getString("driver"));
        cfg.setMaxConnection(rs.getInt("maxconnection"));
        cfg.setMaxStatement(rs.getInt("maxstatement"));
        cfg.setMinConnection(rs.getInt("minconnection"));
        cfg.setPassword(rs.getString("pwd"));
        cfg.setTestsql(rs.getString("testsql"));
        cfg.setTimeoutValue(rs.getInt("timeoutvalue"));
        cfg.setUrl(rs.getString("dburl"));
        cfg.setUser(rs.getString("username"));
        return cfg;
    }
}
