/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.sql;

import com.surfs.nas.util.TextUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProxyConnection implements InvocationHandler {

    private final static String CLOSE_METHOD_NAME = "close";
    private final static String SETAUTOCOMMIT_NAME = "setAutoCommit";
    private final static String PREPARE_CALL = "prepareCall";
    private final static String PREPARE_STAT = "prepareStatement";
    private final static String CREATE_STAT = "createStatement";
    private final static List<String> connectMethodName = new ArrayList<>();
    private String className = null;
    private Connection conn = null;
    private Connection proxyconn = null;
    private boolean inUse = true;
    private boolean autoCommit = true;
    protected long lastAccessTime = System.currentTimeMillis();
    private final List<ProxyStatement> statements = new LinkedList<>();
    protected SmartDataSource datasource = null;

    static {
        Method[] ms = Connection.class.getDeclaredMethods();
        for (Method m : ms) {
            connectMethodName.add(m.getName());
        }
    }

    /**
     *
     * @param conn Connection
     * @param ds SmartDataSource
     */
    protected ProxyConnection(Connection conn, SmartDataSource ds) {
        this.conn = conn;
        this.datasource = ds;
        autoCommit = datasource.isAutoCommit();
    }

    /**
     *
     * @return Connection
     */
    protected Connection getConnection() {
        if (proxyconn == null) {
            Class[] interfaces = new Class[]{java.sql.Connection.class};
            proxyconn = (Connection) Proxy.newProxyInstance(conn.getClass().getClassLoader(), interfaces, this);
        }
        return proxyconn;
    }

    /**
     *
     * @return Connection
     */
    protected Connection getTargetConnection() {
        return conn;
    }

    /**
     *
     * @throws SQLException
     */
    protected void close() throws SQLException {
        conn.close();
    }

    /**
     *
     * @return boolean
     */
    protected boolean isAvalid() {
        String testsql = datasource.getConnParam().getTestsql();
        if (testsql == null) {
            return true;
        }
        if (System.currentTimeMillis() - lastAccessTime < datasource.getConnParam().getTimeoutValue()) {
            return true;
        }
        try {
            JdbcPerformer.executeQuery(conn, testsql);
            lastAccessTime = System.currentTimeMillis();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     *
     * @return boolean
     */
    public boolean isInUse() {
        return inUse;
    }

    /**
     *
     * @param statement ProxyStatement
     */
    protected void removeStatement(ProxyStatement statement) {
        JdbcUtils.closeStatement(statement.getTargetStatement());
        statements.remove(statement);
    }

    /**
     * free
     */
    private void backConnection() {
        for (ProxyStatement ps : statements) {
            JdbcUtils.closeStatement(ps.getTargetStatement());
        }
        statements.clear();
        setInUse(false);
        setClassName(null);
        if (autoCommit != datasource.isAutoCommit()) {
            try {
                proxyconn.setAutoCommit(datasource.isAutoCommit());
                autoCommit = datasource.isAutoCommit();
            } catch (SQLException ex) {
            }
        }
        synchronized (datasource.conns) {
            datasource.conns.notify();
        }
    }

    /**
     *
     * @param proxy
     * @param m
     * @param args
     * @return Object
     * @throws SortedSQLException
     * @throws IllegalAccessException
     */
    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws SortedSQLException, IllegalAccessException {
        if (CLOSE_METHOD_NAME.equals(m.getName())) {
            backConnection();
            lastAccessTime = System.currentTimeMillis();
            return null;
        }
        if ((!inUse) && connectMethodName.contains(m.getName())) {
            SQLException se = new SQLException("No operations allowed after connection closed.");
            SortedSQLException sse = new SortedSQLException(se);
            sse.exceptionType = SortedSQLException.DataAccessResourceFailure;
            throw sse;
        }
        Object obj = null;
        if (PREPARE_STAT.equals(m.getName()) || CREATE_STAT.equals(m.getName()) || PREPARE_CALL.equals(m.getName())) {
            if (datasource.getConnParam().getMaxStatement() > 0 && statements.size() >= datasource.getConnParam().getMaxStatement()) {
                throw new IllegalAccessException("Number of declaration reaches maximum value.");
            }
        } else if (SETAUTOCOMMIT_NAME.equals(m.getName())) {
            Boolean b = (Boolean) args[0];
            autoCommit = b;
        }
        try {
            obj = m.invoke(conn, args);
            lastAccessTime = System.currentTimeMillis();
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof SQLException) {
                SortedSQLException se = new SortedSQLException(getTargetConnection(), (SQLException) e.getTargetException());
                if (se.getExceptionType() == SortedSQLException.DataAccessResourceFailure) {
                    datasource.removeConnection(this);
                } else {
                }
                throw se;
            } else {
                throw new IllegalAccessException(e.getTargetException().getMessage());
            }
        }
        if (PREPARE_STAT.equals(m.getName()) || CREATE_STAT.equals(m.getName()) || PREPARE_CALL.equals(m.getName())) {
            if (datasource.getConnParam().getMaxStatement() > 0) {
                ProxyStatement ps = new ProxyStatement((Statement) obj, this);
                statements.add(ps);
                return ps.getStatement();
            }
        }
        return obj;
    }

    /**
     * lastAccessTime.
     *
     * @return long
     */
    public String getLastAccessTime() {
        return TextUtils.Date2String(new java.util.Date(lastAccessTime));
    }

    /**
     *
     * @return String
     */
    public String getClassName() {
        return className == null ? "" : className;
    }

    /**
     * @return
     */
    public int getStatementSize() {
        return statements.size();
    }

    /**
     * inUse.
     *
     * @param inUse The inUse to set
     */
    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    /**
     *
     * @param className String
     */
    public void setClassName(String className) {
        this.className = className;
    }
}
