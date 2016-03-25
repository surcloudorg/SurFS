package com.autumn.core.web;

import com.autumn.core.ClassManager;
import com.autumn.core.log.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: web目录服务线程监视器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WebService extends Thread {

    private WebDirectory webDirectory = null;//服务配置
    private Logger log = null;
    protected ThreadGroup threadGroup = null;

    /**
     * 创建
     *
     * @param tg 线程组
     * @param name 线程组名
     * @param cfg 服务配置
     */
    public WebService(ThreadGroup tg, String name, WebDirectory cfg) {
        super(tg, name);
        this.threadGroup = tg;
        this.webDirectory = cfg;
        log = cfg.getLogger().getLogger(WebService.class);
    }

    @Override
    public void run() {        
        try {
            register();
            if (webDirectory.getId() > 0) {
                log.warn("[{0}]WEB目录注册成功！", new Object[]{getWebDirectory().getDirName()});
            }
        } catch (Throwable ex) {
            log.trace("[" + getWebDirectory().getDirName() + "]WEB目录注册失败！", ex);
            stoprun();
        }
    }

    /**
     * 加载web服务监听器
     * @throws java.lang.Throwable
     */
    protected void register() throws Throwable {
        if (getWebDirectory().getClassname() == null || getWebDirectory().getClassname().trim().isEmpty()) {
            return;
        }
        Class cls = ClassManager.loadclass(getWebDirectory().getClassname().trim());
        Object instance = cls.newInstance();
        log.info("实例化" + instance.getClass().getName() + "成功！");
        if (instance instanceof Filter) {
            FilterConfig cfg=new WebDirectoryConfig(getWebDirectory());
            ((Filter) instance).init(cfg);
            log.info("执行" + instance.getClass().getName() + "初始化完毕！");
        }
        getWebDirectory().setWebInstance(instance);
    }

    /**
     * 关闭服务
     */
    protected void stoprun() {
        WebFactory.setWebService(this);
        try {
            webDirectory.destory();
        } catch (Throwable ee) {
            log.trace("[" + webDirectory.getDirName() + "]WEB目录服务关闭时抛出错误!", ee);
        }
        WebFactory.removeWebService();
        WebFactory.removeService(this);
        if (webDirectory.getId() > 0) {
            log.warn("[{0}]WEB目录服务退出！", new Object[]{webDirectory.getDirName()});
        }
    }

    /**
     * @return the webDirectory
     */
    public WebDirectory getWebDirectory() {
        return webDirectory;
    }
}
