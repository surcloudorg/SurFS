/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.sql.Statement;

public class ProxyStatement implements InvocationHandler {

    private final static String CLOSE = "close";
    private ProxyConnection proxyConnection = null;
    private final Statement statement;

    public ProxyStatement(Statement stmt, ProxyConnection proxyConnection) {
        this.proxyConnection = proxyConnection;
        this.statement = stmt;
    }

    /**
     *
     * @throws SQLException
     */
    protected void close() throws SQLException {
        statement.close();
    }

    /**
     *
     * @return Statement
     */
    protected Statement getTargetStatement() {
        return statement;
    }

    /**
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
        if (CLOSE.equals(method)) {
            proxyConnection.removeStatement(this);
            return null;
        }
        try {
            Object obj = m.invoke(statement, args);
            proxyConnection.lastAccessTime = System.currentTimeMillis();
            return obj;
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof SQLException) {
                SortedSQLException se = new SortedSQLException(proxyConnection.getTargetConnection(), (SQLException) e.getTargetException());
                if (se.getExceptionType() == SortedSQLException.BadSqlGrammerException) {
                } else if (se.getExceptionType() == SortedSQLException.DataAccessResourceFailure) {
                    proxyConnection.datasource.removeConnection(proxyConnection);
                } else if (se.getExceptionType() == SortedSQLException.DuplicateKeyException
                        || se.getExceptionType() == SortedSQLException.DeadlockLoserException
                        || se.getExceptionType() == SortedSQLException.DataIntegrityViolationException
                        || se.getExceptionType() == SortedSQLException.ObjectExistsException
                        || se.getExceptionType() == SortedSQLException.ObjectNotExistsException
                        || se.getExceptionType() == SortedSQLException.CannotAcquireLockException) {
                } else {
                }
                throw se;
            } else {
                throw new IllegalAccessException(e.getTargetException().getMessage());
            }
        }
    }
}
