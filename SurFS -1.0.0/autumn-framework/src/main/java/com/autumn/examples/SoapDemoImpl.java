package com.autumn.examples;

import com.autumn.core.ThreadPools;
import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.ConfigListener;
import com.autumn.core.cfg.Method;
import com.autumn.core.cfg.Property;
import com.autumn.core.log.LogFactory;
import com.autumn.core.soap.SoapContext;
import com.autumn.core.soap.SoapFactory;
import com.autumn.core.soap.SoapInstance;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.HibernateSessionFactory;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * <p>Title: SOAP服务接口实现测试</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
@SuppressWarnings("unchecked")
public class SoapDemoImpl extends SoapInstance implements SoapDemo, ConfigListener {

    ThreadTest t = null;

    public SoapDemoImpl(SoapContext context) {
        super(context);
        t = new ThreadTest();
        t.start();
        LogFactory.warn("SoapDemoImpl初始化！", SoapDemoImpl.class);
    }

    @Override
    public void contextDestroyed() {
        ThreadPools.stopThread(t);
        LogFactory.warn("SoapDemoImpl被销毁！", SoapDemoImpl.class);
    }

    @Override
    public Object callMethod(Method method) {
        String ss = "呼叫：" + method.getMethodName();
        LogFactory.info(ss, SoapDemoImpl.class);
        return ss;
    }

    @Override
    public boolean changeProperty(Property property) {
        Config cfg = SoapFactory.getSoapContext().getConfig();
        String info = "更改参数：" + property.getKey() + ",新值：" + property.getValue() + ",旧值：" + cfg.getAttributeValue(property.getKey());
        LogFactory.info(info, SoapDemoImpl.class);
        return true;//true(同意更改),false(拒绝更改)
    }

    @Override
    public void addDemo(Demo demo) throws Exception {
        Transaction tx = null;
        try {
            Session session = HibernateSessionFactory.getSession(ConnectionFactory.systemSourceName);
            tx = session.beginTransaction();
            session.save(demo); //demo存入数据库
            tx.commit();
            LogFactory.info("插入成功：" + demo.toString(), SoapDemoImpl.class);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            LogFactory.trace("插入失败!",e, SoapDemoImpl.class);
            throw e;
        }
    }

    @Override
    public Demo[] getDemos() {
        try {
            Session session = HibernateSessionFactory.getSession(ConnectionFactory.systemSourceName);
            Query query = session.createQuery("from Demo order by id desc");
            query.setMaxResults(10);
            List<Demo> list =new ArrayList<Demo>(query.list());
            Demo[] d = new Demo[list.size()];
            LogFactory.info("查询数据成功:"+list.size(), SoapDemoImpl.class);  
            return (Demo[])list.toArray(d);
        } catch (Exception e) {
            LogFactory.trace("查询数据错误!" ,e, SoapDemoImpl.class);
            return null;
        }
    }

}
