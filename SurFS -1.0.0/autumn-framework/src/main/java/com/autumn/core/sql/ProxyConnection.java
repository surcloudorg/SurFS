/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.sql;

import com.autumn.core.log.LogFactory;
import com.autumn.util.TextUtils;
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

/**
 * <p>
 * Title:托管connection的方法</p>
 *
 * <p>
 * Description: 托管关闭连接，创建声明方法...</p>
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
public class ProxyConnection implements InvocationHandler {

    private final static String CLOSE_METHOD_NAME = "close";
    private final static String SETAUTOCOMMIT_NAME = "setAutoCommit";
    private final static String PREPARE_CALL = "prepareCall";
    private final static String PREPARE_STAT = "prepareStatement";
    private final static String CREATE_STAT = "createStatement";
    private final static List<String> connectMethodName = new ArrayList<String>();
    private String className = null; //使用该连接的类名
    private Connection conn = null;//未代理物理连接
    private Connection proxyconn = null;//代理连接
    private boolean inUse = true; //数据库的忙状态
    private boolean autoCommit = true;//是否自动提交
    protected long lastAccessTime = System.currentTimeMillis(); //用户最后一次访问该连接方法的时间
    private final List<ProxyStatement> statements = new LinkedList<ProxyStatement>(); //存储创建的statement
    protected SmartDataSource datasource = null;

    static {
        Method[] ms = Connection.class.getDeclaredMethods();
        for (Method m : ms) {
            connectMethodName.add(m.getName());
        }
    }

    /**
     * 构造方法
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
     * 返回数据库连接conn的接管类，以便截住close方法
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
     * 获取物理连接
     *
     * @return Connection
     */
    protected Connection getTargetConnection() {
        return conn;
    }

    /**
     * 该方法真正的关闭了数据库的连接 由于类属性conn是没有被接管的连接，因此一旦调用close方法后就直接关闭连接
     *
     * @throws SQLException
     */
    protected void close() throws SQLException {
        conn.close();
    }

    /**
     * 激活测试
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
     * 判断是否空闲.
     *
     * @return boolean
     */
    public boolean isInUse() {
        return inUse;
    }

    /**
     * 清除连接
     *
     * @param statement ProxyStatement
     */
    protected void removeStatement(ProxyStatement statement) {
        JdbcUtils.closeStatement(statement.getTargetStatement());
        statements.remove(statement);
    }

    /**
     * 释放
     */
    private void backConnection() {
        for (ProxyStatement ps : statements) {
            JdbcUtils.closeStatement(ps.getTargetStatement());
        }
        statements.clear();
        setInUse(false);
        setClassName(null);
        if (autoCommit != datasource.isAutoCommit()) {//需要重置
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
     * 判断是否调用了close的方法，如果调用close方法则把连接置为无用状态
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
            lastAccessTime = System.currentTimeMillis();//设置最后一次访问时间，以便及时清除超时的连接
            return null;
        }
        if ((!inUse) && connectMethodName.contains(m.getName())) {//代理已经停止
            SQLException se = new SQLException("No operations allowed after connection closed.");
            SortedSQLException sse = new SortedSQLException(se);
            sse.exceptionType = SortedSQLException.DataAccessResourceFailure;
            throw sse;
        }
        Object obj = null;
        if (PREPARE_STAT.equals(m.getName()) || CREATE_STAT.equals(m.getName()) || PREPARE_CALL.equals(m.getName())) {
            if (datasource.getConnParam().getMaxStatement() > 0 && statements.size() >= datasource.getConnParam().getMaxStatement()) { //需要限制最大Statement数量
                LogFactory.error("该连接创建的声明达到最大值{0}", new Object[]{datasource.getConnParam().getMaxStatement()}, className);
                throw new IllegalAccessException("创建的声明达到最大值");
            }
        } else if (SETAUTOCOMMIT_NAME.equals(m.getName())) {
            Boolean b = (Boolean) args[0];
            autoCommit = b.booleanValue();
        }
        try {
            obj = m.invoke(conn, args);
            lastAccessTime = System.currentTimeMillis();
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof SQLException) {
                SortedSQLException se = new SortedSQLException(getTargetConnection(), (SQLException) e.getTargetException());
                if (se.getExceptionType() == SortedSQLException.DataAccessResourceFailure) {
                    datasource.removeConnection(this);
                    LogFactory.fatal("数据库访问失败：{0}", new Object[]{se.getMessage()}, className);
                } else {
                    LogFactory.trace("数据库操作失败！", se, className);
                }
                throw se;
            } else {
                LogFactory.trace("数据库操作发生了一个不可预期的错误!", e.getTargetException(), className);
                throw new IllegalAccessException("数据库操作发生了一个不可预期的错误:" + e.getTargetException().getMessage());
            }
        }
        if (PREPARE_STAT.equals(m.getName()) || CREATE_STAT.equals(m.getName()) || PREPARE_CALL.equals(m.getName())) {
            if (datasource.getConnParam().getMaxStatement() > 0) { //需要托管
                ProxyStatement ps = new ProxyStatement((Statement) obj, this);
                statements.add(ps);
                return ps.getStatement();
            }
        }
        return obj;
    }

    /**
     * 返回lastAccessTime.
     *
     * @return long
     */
    public String getLastAccessTime() {
        return TextUtils.Date2String(new java.util.Date(lastAccessTime));
    }

    /**
     * 获取使用该连接的类名
     *
     * @return String
     */
    public String getClassName() {
        return className == null ? "" : className;
    }

    /**
     * @return 已创建的声明个数
     */
    public int getStatementSize() {
        return statements.size();
    }

    /**
     * 设置inUse.
     *
     * @param inUse The inUse to set
     */
    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    /**
     * 设置使用该连接的类名
     *
     * @param className String
     */
    public void setClassName(String className) {
        this.className = className;
    }
}
