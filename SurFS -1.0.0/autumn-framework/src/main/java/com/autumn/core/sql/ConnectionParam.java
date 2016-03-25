package com.autumn.core.sql;

import com.autumn.core.web.Initializer;
import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <p>
 * Title:连接池参数</p>
 *
 * <p>
 * Description: 连接池驱动，主机，地址，端口...</p>
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
public final class ConnectionParam implements Serializable {

    private static final long serialVersionUID = 20120701000004L;
    private String driver; //数据库驱动程序
    private String url; //数据连接的URL
    private String user; //数据库用户名
    private String password; //数据库密码
    private int minConnection = 2; //保留最小连接数
    private int maxConnection = 50; //最大连接数
    private long timeoutValue = 600000; //连接的最大空闲时间,  对于释放/未释放连接，超过这个时间关闭物理连接
    private String jndi = ""; //连接池名称
    private String testsql = null; //测试指令
    private int maxStatement = 50; //允许最多创建多少Statement,=0不托管Statement

    public ConnectionParam(String jndi) throws Exception {
        setJndi(jndi);
    }

    /**
     * 构造
     *
     * @param jndi
     * @param driver
     * @param url
     * @param user
     * @param password
     * @throws SQLException
     */
    public ConnectionParam(String jndi, String driver, String url, String user, String password) throws Exception {
        setJndi(jndi);
        setDriver(driver);
        setUrl(url);
        setUser(user);
        setPassword(password);
    }

    /**
     * 比较参数driver，url,user,password,charset不同，视为不同
     *
     * @param cp
     * @return boolean
     */
    public boolean equal(ConnectionParam cp) {
        if (!driver.equalsIgnoreCase(cp.driver)) {
            return false;
        }
        if (!url.equalsIgnoreCase(cp.url)) {
            return false;
        }
        if (!user.equalsIgnoreCase(cp.user)) {
            return false;
        }
        return password.equalsIgnoreCase(cp.password);
    }

    /**
     * 创建物理连接
     *
     * @return Connection
     * @throws SQLException
     */
    public Connection getConnect() throws SQLException {
        String urll = getUrl();
        return DriverManager.getConnection(urll, user, password);
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driverstr the driver to set
     * @throws java.lang.Exception
     */
    public void setDriver(String driverstr) throws Exception {
        if (driverstr == null || driverstr.trim().isEmpty()) {
            throw new Exception("连接driver不能为空！");
        }
        this.driver = driverstr.trim();
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 获取derby数据库连接地址
     *
     * @param dbname
     * @return String
     */
    public static String getDerbyUrl(String dbname) {
        String usepath = Initializer.getWebpath();
        if (usepath == null) {
            usepath = System.getProperty("user.dir");
        } else {
            usepath = usepath + "WEB-INF";
        }
        if (usepath != null) {
            if (usepath.endsWith(File.separator)) {
                usepath = usepath + "db";
            } else {
                usepath = usepath + File.separator + "db";
            }
            dbname = usepath + File.separator + dbname;
        }
        dbname = "jdbc:derby:" + dbname + ";create=true";
        return dbname;
    }

    /**
     * @param url the url to set
     * @throws java.lang.Exception
     */
    public void setUrl(String url) throws Exception {
        if (url == null || url.trim().isEmpty()) {
            throw new Exception("连接url不能为空！");
        }
        this.url = url.trim();
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the minConnection
     */
    public int getMinConnection() {
        return minConnection;
    }

    /**
     * @param minConnection the minConnection to set
     */
    public void setMinConnection(int minConnection) {
        this.minConnection = minConnection;
    }

    /**
     * @return the maxConnection
     */
    public int getMaxConnection() {
        return maxConnection;
    }

    /**
     * @param maxConnection the maxConnection to set
     */
    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    /**
     * @return the timeoutValue
     */
    public long getTimeoutValue() {
        return timeoutValue;
    }

    /**
     * @param timeoutvalue the timeoutValue to set
     */
    public void setTimeoutValue(long timeoutvalue) {
        this.timeoutValue = timeoutvalue < 60000 ? 60000 : timeoutvalue;
    }

    /**
     * @return the jndi
     */
    public String getJndi() {
        return jndi;
    }

    /**
     * @param jndi the jndi to set
     * @throws java.lang.Exception
     */
    public void setJndi(String jndi) throws Exception {
        if (jndi == null || jndi.trim().isEmpty()) {
            throw new Exception("连接池名不能为空！");
        }
        this.jndi = jndi.trim();
    }

    /**
     * @return the testsql
     */
    public String getTestsql() {
        return testsql;
    }

    /**
     * @param sql the testsql to set
     */
    public void setTestsql(String sql) {
        this.testsql = sql == null || sql.trim().isEmpty() ? null : sql.trim();
    }

    /**
     * @return the maxStatement
     */
    public int getMaxStatement() {
        return maxStatement;
    }

    /**
     * @param max the maxStatement to set
     */
    public void setMaxStatement(int max) {
        this.maxStatement = max > 1000 || max < 0 ? 0 : max;
    }
}
