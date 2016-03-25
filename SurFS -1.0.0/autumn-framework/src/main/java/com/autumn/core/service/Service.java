package com.autumn.core.service;

import com.autumn.core.ClassManager;
import com.autumn.core.log.Logger;

/**
 * <p>Title: 服务运行实例</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Service extends Thread {

    private ServiceImpl impl = null;//接口实现
    private ServiceConfig ServiceConfig = null;//服务配置
    private Logger log = null;
    protected ThreadGroup threadGroup = null;

    /**
     * 创建
     *
     * @param tg 线程组
     * @param name 线程组名
     * @param cfg 服务配置
     */
    public Service(ThreadGroup tg, String name, ServiceConfig cfg) {
        super(tg, name);
        this.threadGroup = tg;
        this.ServiceConfig = cfg;
        log = ServiceConfig.getLogger().getLogger(Service.class);
    }

    /**
     * 执行，注意执行start失败后，执行stop
     */
    @Override
    public void run() {
        try {
            impl = ServiceConfig.getService();
            impl.start();
            log.warn("[{0}]服务启动完毕！", new Object[]{ServiceConfig.getId()});
        } catch (Throwable e) {
            log.trace("[" + ServiceConfig.getId() + "]服务启动失败！", e);
            stoprun();
        }
    }

    /**
     * 获取服务配置
     *
     * @return 服务配置
     */
    public ServiceConfig getServiceConfig() {
        return ServiceConfig;
    }

    /**
     * 关闭服务
     */
    protected void stoprun() {
        ServiceFactory.setService(this);
        try {
            if (impl != null) {
                impl.stop();
            }
        } catch (Throwable ee) {
            log.trace("[" + ServiceConfig.getId() + "]服务关闭时抛出错误!", ee);
        }
        ServiceFactory.removeService();
        ServiceFactory.removeService(this);
        log.warn("[{0}]服务退出！", new Object[]{ServiceConfig.getId()});
    }

    /**
     * @return the impl
     */
    public ServiceImpl getServiceImpl() {
        return impl;
    }

    /**
     * 是否需要重新加载
     *
     * @return boolean
     */
    public boolean classNeedReload() {
        try {
            if (impl == null) {
                return false;
            }
            Class cls = ClassManager.loadclass(impl.getClass().getName());
            return cls != impl.getClass();
        } catch (ClassNotFoundException ex) {
            return true;
        }
    }
}
