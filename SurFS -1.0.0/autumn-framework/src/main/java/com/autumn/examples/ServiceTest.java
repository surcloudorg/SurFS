package com.autumn.examples;

import com.autumn.core.ThreadPools;
import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.ConfigListener;
import com.autumn.core.cfg.Method;
import com.autumn.core.cfg.Property;
import com.autumn.core.log.LogFactory;
import com.autumn.core.service.ServiceFactory;
import com.autumn.core.service.ServiceImpl;

/**
 * <p>Title: 测试服务</p>
 *
 * <p>Description: 可以实现ConfigListener接口用来监听Config中的参数变化</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn</p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ServiceTest implements ConfigListener, ServiceImpl {

    private CommandTest threadtest = null;
    

    @Override
    public void start() throws Exception {
        LogFactory.warn("hello!", ServiceTest.class);
        LogFactory.fatal("告警测试开始！", ServiceTest.class);
        //注意：如果在start过程抛出了错误，一定要终止已经创建的线程  

        threadtest = new CommandTest();
        threadtest.start();
    }

    @Override
    public void stop() {
        LogFactory.warn("goodbye!", ServiceTest.class);
        LogFactory.fatal("告警测试结束！", ServiceTest.class);
        //必须等待所有线程退出再返回此函数
        ThreadPools.stopThread(threadtest);
    }

    /**
     * 呼叫方法
     *
     * @param method
     * @return String
     */
    @Override
    public Object callMethod(Method method) {
        String ss = "呼叫：" + method.getMethodName();
        LogFactory.info(ss, ServiceTest.class);
        return ss;
    }

    /**
     * 在服务配置的param参数值更改
     *
     * @param property
     * @return boolean
     */
    @Override
    public boolean changeProperty(Property property) {
        Config cfg = ServiceFactory.getServiceConfig().getConfig();
        String info = "更改参数：" + property.getKey() + ",新值：" + property.getValue() + ",旧值：" + cfg.getAttributeValue(property.getKey());
        LogFactory.info(info, ServiceTest.class);
        return true;//true(同意更改),false(拒绝更改)
    }
}
