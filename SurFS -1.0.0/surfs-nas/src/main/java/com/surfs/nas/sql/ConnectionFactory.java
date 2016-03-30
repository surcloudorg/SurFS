/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.sql;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.*;

public class ConnectionFactory {

    public static final Logger log = Logger.getLogger(ConnectionFactory.class.getName());
    public static final String systemSourceName = "SystemSource";
    private static final HashMap<String, SmartDataSource> connectionPools = new HashMap<>(2, 0.75F);

    /**
     * @param dataSourceName
     * @return SmartDataSource
     * @throws NameNotFoundException
     */
    public static SmartDataSource lookup(String dataSourceName) throws NameNotFoundException {
        SmartDataSource ds;
        synchronized (connectionPools) {
            ds = connectionPools.get(dataSourceName);
            if (ds == null) {
                throw new NameNotFoundException(dataSourceName);
            }
        }
        return ds;
    }

    /**
     *
     * @param param
     * @return SmartDataSource
     * @throws NameAlreadyBoundException
     * @throws ClassNotFoundException
     */
    public static SmartDataSource bind(ConnectionParam param) throws NameAlreadyBoundException, ClassNotFoundException {
        SmartDataSource source;
        String name = param.getJndi();
        try {
            source = lookup(name);
            throw new NameAlreadyBoundException(name);
        } catch (NameNotFoundException e) {
            source = new SmartDataSource(param);
            try {
                source.initConnection();
            } catch (SortedSQLException ex) {
            }
            synchronized (connectionPools) {
                connectionPools.put(name, source);
                bindContext(name, source);
            }
        }
        return source;
    }

    /**
     *
     * @param param
     * @return SmartDataSource
     * @throws NameAlreadyBoundException
     * @throws ClassNotFoundException
     * @throws SortedSQLException
     */
    public static SmartDataSource rebind(ConnectionParam param) throws NameAlreadyBoundException, ClassNotFoundException, SortedSQLException {
        String name = param.getJndi();
        try {
            SmartDataSource ds = lookup(name);
            if (!ds.getConnParam().equal(param)) {
                unbind(name);
                return bind(param);
            } else {
                ds.setConnParam(param);
                return ds;
            }
        } catch (NameNotFoundException e) {
            return bind(param);
        }
    }

    /**
     *
     * @param dataSourceName
     * @throws NameNotFoundException
     */
    public static void unbind(String dataSourceName) throws NameNotFoundException {
        SmartDataSource dataSource = lookup(dataSourceName);
        dataSource.close();
        synchronized (connectionPools) {
            unbindContext(dataSourceName);
            connectionPools.remove(dataSourceName);
        }
    }

    /**
     *
     * @param dataSourceName
     * @param obj
     */
    public static void bindContext(String dataSourceName, SmartDataSource obj) {
        try {
            Context ctx = new InitialContext();
            ctx.bind(dataSourceName, obj);
        } catch (NamingException ex) {
            log.log(Level.SEVERE, "bindContext err:{0}", ex);
        }

    }

    /**
     *
     * @param dataSourceName
     */
    public static void unbindContext(String dataSourceName) {
        try {
            Context ctx = new InitialContext();
            ctx.unbind(dataSourceName);
        } catch (NamingException ex) {
            log.log(Level.SEVERE, "unbindContext err:{0}", ex);
        }
    }

    /**
     * close all
     */
    public static void stop() {
        List<String> w = new ArrayList<>(connectionPools.keySet());
        for (String str : w) {
            try {
                unbind(str);
            } catch (NameNotFoundException ex) {
            }
        }
    }

    /**
     * @return List
     */
    public static List<SmartDataSource> getPoolsElement() {
        List<SmartDataSource> list = new ArrayList<>(connectionPools.values());
        return list;
    }

    /**
     *
     * @return List
     */
    public static List<String> getPoolsKeys() {
        return new ArrayList<>(connectionPools.keySet());
    }

    /**
     *
     * @param cls
     * @return Connection
     */
    public static Connection getConnect(Class cls) {
        return getConnect(systemSourceName, cls);
    }

    /**
     *
     * @param jndi
     * @param cls
     * @return Connection
     */
    public static Connection getConnect(String jndi, Class cls) {
        if (cls == null) {
            log.warning("class is empty!");
            return null;
        }
        return getConnect(jndi, cls.getName());
    }

    /**
     *
     * @param jndi String
     * @return Connection
     */
    private static Connection getConnect(String jndi, String classname) {
        if (jndi == null) {
            log.warning("pool name is empty!");
            return null;
        }
        try {
            SmartDataSource ds = ConnectionFactory.lookup(jndi);
            return ds.getConnection(classname);
        } catch (SortedSQLException ex) {
        } catch (NameNotFoundException e) {
            log.log(Level.WARNING, "pool ''{0}'' is not exist", jndi);
        }
        return null;
    }
}
