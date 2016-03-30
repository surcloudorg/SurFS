/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class SmartDataSource implements DataSource {

    private static int LOGIN_TIMEOUT = 30;
    protected final List<ProxyConnection> conns = new ArrayList<>();
    private ConnectionParam connParam = null;
    private Timer checkTimer = null;
    private boolean autoCommit = true;

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        getConnParam().setUser(username);
        getConnParam().setPassword(password);
        return getConnection("*");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection("*");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return LOGIN_TIMEOUT;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        LOGIN_TIMEOUT = seconds;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param connParam ConnectionParam
     * @throws java.lang.ClassNotFoundException
     */
    public SmartDataSource(ConnectionParam connParam) throws ClassNotFoundException {
        this.connParam = connParam;
        Class.forName(this.connParam.getDriver());
        DriverManager.setLoginTimeout(LOGIN_TIMEOUT);
        checkTimer = new Timer(true);
        checkTimer.schedule(new DataSourceChecker(this), 0, 1000 * 60);
    }

    /**
     *
     * @throws com.surfs.nas.sql.SortedSQLException
     */
    protected void initConnection() throws SortedSQLException {
        Connection con = null;
        try {
            con = this.getConnection(SmartDataSource.class.getName());
            if (connParam.getTestsql() != null) {
                JdbcPerformer.executeQuery(con, connParam.getTestsql());
            }
            try {
                autoCommit = con.getAutoCommit();
            } catch (SQLException ex) {
            }
        } catch (SortedSQLException r) {
            throw r;
        } finally {
            JdbcUtils.closeConnect(con);
        }
    }

    /**
     *
     * @param classname
     * @return Connection
     * @throws com.surfs.nas.sql.SortedSQLException
     */
    protected Connection getConnection(String classname) throws SortedSQLException {
        Connection conn = getFreeConnection(classname);
        if (conn != null) {
            return conn;
        }
        synchronized (conns) {
            if (conns.size() >= getConnParam().getMaxConnection()) {
                try {
                    conns.wait(1000 * 30);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();

                    SortedSQLException se = new SortedSQLException(new SQLException("Connection pool is full"));
                    se.exceptionType = SortedSQLException.DataAccessResourceFailure;
                    throw se;
                }
            } else {
                Connection conn2 = null;
                try {
                    conn2 = getConnParam().getConnect();
                } catch (SQLException r) {

                    SortedSQLException se = new SortedSQLException(r);
                    se.exceptionType = SortedSQLException.DataAccessResourceFailure;
                    throw se;
                }
                ProxyConnection _conn = new ProxyConnection(conn2, this);
                _conn.setClassName(classname);
                conns.add(_conn);
                return _conn.getConnection();
            }
        }
        return getConnection(classname);
    }

    /**
     *
     * @param con ProxyConnection
     */
    protected void removeConnection(ProxyConnection con) {
        JdbcUtils.closeConnect(con.getTargetConnection());
        synchronized (conns) {
            conns.remove(con);
        }
    }

    /**
     *
     * @param classname
     * @return Connection
     */
    protected Connection getFreeConnection(String classname) {
        Connection conn;
        ProxyConnection _conn;
        synchronized (conns) {
            for (ProxyConnection pc : conns) {
                _conn = pc;
                if (!_conn.isInUse()) {
                    if (!_conn.isAvalid()) {
                        removeConnection(_conn);
                        return getFreeConnection(classname);
                    } else {
                        conn = _conn.getConnection();
                        _conn.setInUse(true);
                        _conn.setClassName(classname);
                        return conn;
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @return int
     */
    public int getConnectionCount() {
        return conns.size();
    }

    /**
     *
     * @return
     */
    public int getFreeConCount() {
        int count = 0;
        synchronized (conns) {
            for (ProxyConnection pc : conns) {
                if (!pc.isInUse()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     *
     *
     * @return
     */
    public int getUseConCount() {
        int count = 0;
        synchronized (conns) {
            for (ProxyConnection pc : conns) {
                if (pc.isInUse()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * close
     */
    public void close() {
        synchronized (conns) {
            for (ProxyConnection pc : conns) {
                JdbcUtils.closeConnect(pc.getTargetConnection());
            }
        }
        conns.clear();
        if (checkTimer != null) {
            checkTimer.cancel();
        }
    }

    /**
     *
     * @return ConnectionParam
     */
    public ConnectionParam getConnParam() {
        return connParam;
    }

    /**
     *
     * @return List
     */
    public List<ProxyConnection> getConns() {
        return conns;
    }

    /**
     * @param connParam the connParam to set
     */
    public void setConnParam(ConnectionParam connParam) {
        this.connParam = connParam;
    }

    /**
     * @return the autoCommit
     */
    public boolean isAutoCommit() {
        return autoCommit;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
