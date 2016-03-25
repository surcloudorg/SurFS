package com.autumn.core.sql;

import com.autumn.core.ClassManager;
import com.autumn.core.log.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * <p>Title: Hibernate session连接池</p>
 *
 * <p>Description: Hibernate session连接池</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class HibernateSessionSource {
    
    private ThreadLocal<Session> threadLocal = new ThreadLocal<Session>(); //线程池
    private Configuration configuration = new Configuration(); //配置
    private SessionFactory sessionFactory; //连接工厂
    private HibernateConfig cfg = null; //配置文档

    /**
     * 创建连接池
     *
     * @param jdbc String 数据库连接池名
     * @throws Exception
     */
    public HibernateSessionSource(String jdbc) throws Exception {
        this.cfg = new HibernateConfig(jdbc);
    }

    /**
     * 获取session
     *
     * @return Session
     * @throws HibernateException
     */
    public Session getSession() throws HibernateException {
        Session session = threadLocal.get();
        if (session == null || !session.isOpen()) {
            if (sessionFactory == null) {
                rebuildSessionFactory();
            }
            session = (sessionFactory != null)
                    ? sessionFactory.openSession()
                    : null;
            threadLocal.set(session);
        }
        return session;
    }

    /**
     * 创建连接池
     */
    public void rebuildSessionFactory(HibernateMapping mapping) {
        configuration.configure(cfg.getDocument());
        if (mapping != null) {
            configuration.addXML(mapping.getXml());
        }
        sessionFactory = configuration.buildSessionFactory();
    }

    /**
     * 创建连接池
     */
    public void rebuildSessionFactory() {
        Thread.currentThread().setContextClassLoader(ClassManager.getClassLoader());
        configuration.configure(cfg.getDocument());
        HibernateMapping[] mapping = HibernameInitializer.initSessionSource(this.getDataSource());
        if (mapping != null) {
            for (HibernateMapping map : mapping) {
                configuration.addXML(map.getXml());
            }
        }
        sessionFactory = configuration.buildSessionFactory();
        LogFactory.warn("Session工厂(" + cfg.getDataSource() + ")初始化完毕！", HibernateSessionSource.class);
    }

    /**
     * 关闭session
     *
     * @throws HibernateException
     */
    public void closeSession() throws HibernateException {
        Session session = threadLocal.get();
        threadLocal.set(null);
        if (session != null) {
            session.close();
        }
    }

    /**
     * 获取SessionFactory
     *
     * @return SessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * 获取配置
     *
     * @return Configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 获取数据源名
     *
     * @return String
     */
    public String getDataSource() {
        return cfg.getDataSource();
    }
}
