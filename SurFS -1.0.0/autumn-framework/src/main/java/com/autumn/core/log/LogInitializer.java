package com.autumn.core.log;

import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.sql.SortedSQLException;
import com.autumn.core.web.WebFactory;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * <p>Title: 日志工厂初始化</p>
 *
 * <p>Description: 日志配置的数据库访问实现</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public final class LogInitializer {

    /**
     * 创建系统日志
     *
     * @param con
     */
    public static void createSystemLogger(Connection con) {
        JdbcPerformer jt = new JdbcPerformer(con);
        Object[] args = new Object[]{ LogFactory.SYSTEM_LOGNAME, WebFactory.getInstanceName(), "[MM-dd HH:mm:ss]", 1, 1, 1};
        jt.addParameter(args);
        String sql = "insert into logcfg(logname,host,dateformatter,addlevel,addclassname,outconsole) values(?,?,?,?,?,?)";
        try {
            jt.update(sql);
        } catch (SortedSQLException se) {
            if (se.getExceptionType() != SortedSQLException.DuplicateKeyException) {
                LogFactory.error("创建系统(system)日志目录失败：" + se.getMessage(), LogInitializer.class);
            }
        }
    }

    /**
     * 创建错误日志
     *
     * @param con
     */
    public static void createErrorLogger(Connection con) {
        JdbcPerformer jt = new JdbcPerformer(con);
        Object[] args = new Object[]{"error", WebFactory.getInstanceName(), "[MM-dd HH:mm:ss]", 0, 0, 0};
        jt.addParameter(args);
        String sql = "insert into logcfg(logname,host,dateformatter,addlevel,addclassname,outconsole) values(?,?,?,?,?,?)";
        try {
            jt.update(sql);
        } catch (SortedSQLException se) {
            if (se.getExceptionType() != SortedSQLException.DuplicateKeyException) {
                LogFactory.error("创建错误(error)日志目录失败：" + se.getMessage(), LogInitializer.class);
            }
        }
    }

    /**
     * 查询
     *
     * @param con
     */
    public static void initLogger(Connection con) {
        JdbcPerformer jt = new JdbcPerformer(con);
        jt.addParameter(WebFactory.getInstanceName());
        String sql = "select * from logcfg where host=?";
        try {
            ResultSet rs = jt.query(sql);
            while (rs.next()) {
                LogProperties ip = getLogProperties(rs);
                LogFactory.addLogger(ip);
            }
        } catch (Exception e) {
            LogFactory.error("从数据库读取log配置失败:" + e.getMessage(), LogInitializer.class);
        }
    }

    /**
     * ResultSet--〉LogProperties
     *
     * @param rs
     * @return LogProperties
     * @throws Exception
     */
    private static LogProperties getLogProperties(ResultSet rs) throws Exception {
        LogProperties cfg = new LogProperties(rs.getString("logname"));
        cfg.setAddClassName(rs.getBoolean("addclassname"));
        cfg.setAddLevel(rs.getBoolean("addlevel"));
        cfg.setDateformatter(rs.getString("dateformatter"));
        cfg.setFilter(rs.getString("filter"));
        cfg.setLevel(rs.getInt("level"));
        cfg.setOutConsole(rs.getBoolean("outconsole"));
        cfg.setWarnClass(rs.getString("warnclass"));
        cfg.setWarnInteral(rs.getInt("warninteral"));
        cfg.setParams(JdbcUtils.getResultSetStringValue(rs, "params"));
        return cfg;
    }
}
