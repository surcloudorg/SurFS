/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.sql;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionParam implements Serializable {

    private static final long serialVersionUID = 20120701000004L;
    private String driver;
    private String url;
    private String user;
    private String password;
    private int minConnection = 2;
    private int maxConnection = 50;
    private long timeoutValue = 600000;
    private String jndi = "";
    private String testsql = null;
    private int maxStatement = 50;

    public ConnectionParam(String jndi) throws Exception {
        setJndi(jndi);
    }

    public ConnectionParam(String jndi, String driver, String url, String user, String password) throws Exception {
        setJndi(jndi);
        setDriver(driver);
        setUrl(url);
        setUser(user);
        setPassword(password);
    }

    /**
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
     *
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
            throw new Exception("driver invalid!");
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
     * @param url the url to set
     * @throws java.lang.Exception
     */
    public void setUrl(String url) throws Exception {
        if (url == null || url.trim().isEmpty()) {
            throw new Exception("url invalid!");
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
            throw new Exception("pool name invalid!");
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
