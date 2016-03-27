/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.sql;

import java.util.HashMap;
import org.hibernate.Session;

/**
 *
 * <p>Title: Hibernate对话工厂</p>
 *
 * <p>Description: Hibernate对话工厂</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class HibernateSessionFactory {

    public static final HashMap<String, HibernateSessionSource> sources = new HashMap<String, HibernateSessionSource>(); //存储session连接池

    /**
     * 当执行重载时，映射需要重载
     */
    public static void rebuild() {
        synchronized (sources) {
            sources.clear();
        }
    }

    /**
     * 获取session连接池
     *
     * @param JDBC String 数据库连接池名
     * @return HibernateSessionSource
     */
    public static HibernateSessionSource getSessionSource(String JDBC) {
        HibernateSessionSource ss;
        synchronized (sources) {
            ss = sources.get(JDBC);
        }
        return ss;
    }

    /**
     * 移除session连接池
     *
     * @param JDBC String 数据库连接池名
     * @return HibernateSessionSource
     */
    public static HibernateSessionSource removeSessionSource(String JDBC) {
        HibernateSessionSource ss;
        synchronized (sources) {
            ss = sources.remove(JDBC);
        }
        return ss;
    }

    /**
     * 添加session连接池
     *
     * @param JDBC String 数据库连接池名
     * @return HibernateSessionSource
     * @throws Exception
     */
    private static HibernateSessionSource addSessionSource(String JDBC) throws Exception {
        if (getSessionSource(JDBC) != null) {
            removeSessionSource(JDBC);
        }
        if (!ConnectionFactory.getPoolsKeys().contains(JDBC)) {
            throw new Exception("连接池" + JDBC + "不存在");
        }
        HibernateSessionSource ss = new HibernateSessionSource(JDBC);
        synchronized (sources) {
            sources.put(JDBC, ss);
        }
        return ss;
    }

    /**
     * 获取对应连接池session
     *
     * @param JDBC String 连接池名称名
     * @return Session
     * @throws Exception
     */
    public static Session getSession(String JDBC) throws Exception {
        HibernateSessionSource ss = getSessionSource(JDBC);
        if (ss == null) {
            ss = addSessionSource(JDBC);
        }
        return ss.getSession();
    }
}
