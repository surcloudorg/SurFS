/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.sql;

import com.autumn.core.log.Level;
import com.autumn.core.log.LogFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <p>Title:Statement代理</p>
 *
 * <p>Description: 托管Statement的close，setString，executeQuery方法</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ProxyStatement implements InvocationHandler {

    private final static String CLOSE = "close";
    private ProxyConnection proxyConnection = null;
    private Statement statement; //保存所接管对象的实例

    public ProxyStatement(Statement stmt, ProxyConnection proxyConnection) {
        this.proxyConnection = proxyConnection;
        this.statement = stmt;
    }

    /**
     * 关闭声明
     *
     * @throws SQLException
     */
    protected void close() throws SQLException {
        statement.close();
    }

    /**
     * 获取真实声明对象
     *
     * @return Statement
     */ 
    protected Statement getTargetStatement() {
        return statement;
    }

    /**
     * 获取一个接管后的对象实例
     *
     * @return Statement
     */
    protected Statement getStatement() {
        Class cls = statement.getClass();
        Class[] interfaces = null;
        while (interfaces == null || interfaces.length == 0) {
            interfaces = cls.getInterfaces();
            cls = cls.getSuperclass();
        }
        Statement stmt = (Statement) Proxy.newProxyInstance(statement.getClass().getClassLoader(), interfaces, this);
        return stmt;
    }

    /**
     * 方法接管
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
        String method = m.getName();
        if (CLOSE.equals(method)) {//接管close方法
            proxyConnection.removeStatement(this);
            return null;
        }
        try {
            Object obj = m.invoke(statement, args);//设置连接代理的最后访问时间
            proxyConnection.lastAccessTime = System.currentTimeMillis();
            return obj;
        } catch (InvocationTargetException e) {//分析错误，对DataAccessResourceFailure错误，强制释放连接
            if (e.getTargetException() instanceof SQLException) {
                SortedSQLException se = new SortedSQLException(proxyConnection.getTargetConnection(), (SQLException) e.getTargetException());
                if (se.getExceptionType() == SortedSQLException.BadSqlGrammerException) {
                    LogFactory.error("数据库操作遇到语法错误:{0}", new Object[]{se.getMessage()}, proxyConnection.getClassName());
                } else if (se.getExceptionType() == SortedSQLException.DataAccessResourceFailure) {
                    proxyConnection.datasource.removeConnection(proxyConnection);
                    LogFactory.fatal("数据库访问失败：{0}", new Object[]{se.getMessage()}, proxyConnection.getClassName());
                } else if (se.getExceptionType() == SortedSQLException.DuplicateKeyException
                        || se.getExceptionType() == SortedSQLException.DeadlockLoserException
                        || se.getExceptionType() == SortedSQLException.DataIntegrityViolationException
                        || se.getExceptionType() == SortedSQLException.ObjectExistsException
                        || se.getExceptionType() == SortedSQLException.ObjectNotExistsException
                        || se.getExceptionType() == SortedSQLException.CannotAcquireLockException) {
                    LogFactory.warn("数据库操作被拒绝，原因：{0}", new Object[]{se.getMessage()}, proxyConnection.getClassName());
                } else {
                    LogFactory.error("数据库操作遇到未分类故障,SQLState:{0},ErrorCode:{1},{2}", new Object[]{se.getSQLState(), se.getErrorCode(), se.getMessage()}, proxyConnection.getClassName());
                }
                if (LogFactory.getLogger().getProperties().getLevel() == Level.DEBUG) {
                    LogFactory.debug("错误信息:" + statement.toString(), proxyConnection.getClassName());
                }
                throw se;
            } else {//不可能发生,可能是驱动runtime错误
                LogFactory.trace("数据库操作发生了一个不可预期的错误!", e.getTargetException(), proxyConnection.getClassName());
                throw new IllegalAccessException("数据库操作发生了一个不可预期的错误:" + e.getTargetException().getMessage());
            }
        }
    }
}
