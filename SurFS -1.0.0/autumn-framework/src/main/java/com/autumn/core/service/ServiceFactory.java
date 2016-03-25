package com.autumn.core.service;

import com.autumn.core.AsyncCloseServices;
import com.autumn.core.SystemInfo;
import com.autumn.core.ThreadPools;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: 服务工厂</p>
 *
 * <p>Description: 每个服务会创建一个线程组，通过线程组能获取到上下文参数</p>
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
public class ServiceFactory {

    private static final HashMap<Integer, Service> serviceMap = new HashMap<Integer, Service>();//<服务ID,服务实例>
    private static final HashMap<ThreadGroup, Service> threadGroupMap = new HashMap<ThreadGroup, Service>();//<服务线程组,服务实例>
    private static final ThreadLocal<Service> threadService = new ThreadLocal<Service>();//线程变量

    /**
     * 线程组的命名方式"ThreadGroup_" + Integer.toString(id)
     *
     * @param id
     * @return String
     */
    public static String makeThreadGroupName(int id) {
        return "FRAME/SERVICE/" + String.format("%1$5d", id).replaceAll(" ", ".");
    }

    /**
     * 获取当前线程所属服务的服务配置
     *
     * @return ServiceConfig
     */
    public static ServiceConfig getServiceConfig() {
        Service service = getService();
        if (service != null) {
            return service.getServiceConfig();
        }
        return null;
    }

    /**
     * 获取当前线程所属服务的日志输出
     *
     * @return Logger
     */
    public static Logger getLogger() {
        ServiceConfig service = getServiceConfig();
        if (service != null) {
            return service.getLogger();
        }
        return null;
    }

    /**
     * 获取当前线程所属服务实例
     *
     * @return Service
     */
    public static Service getService() {
        Service s = threadService.get();
        if (s != null) {
            return s;
        }
        ThreadGroup group = getThreadGroup();
        if (group != null) {
            synchronized (threadGroupMap) {
                return threadGroupMap.get(group);
            }
        }
        return null;
    }

    /**
     * 设置线程变量
     *
     * @param s
     */
    public static void setService(Service s) {
        threadService.set(s);
    }

    /**
     * 移出线程变量
     */
    public static void removeService() {
        threadService.remove();
    }

    /**
     * 获取当前线程所属线程组
     *
     * @return ThreadGroup
     */
    public static ThreadGroup getThreadGroup() {
        ThreadGroup tg = SystemInfo.getCurrentThreadGroup();
        if (tg != null) {
            synchronized (threadGroupMap) {
                if (threadGroupMap.containsKey(tg)) {
                    return tg;
                }
            }
        }
        return null;
    }

    /**
     * 启动服务
     * @param cfgs
     */
    public static void startService(List<ServiceConfig> cfgs) {
        if (cfgs != null) {
            for (ServiceConfig cfg : cfgs) {
                if (cfg.getStatus() == 0) {
                    startService(cfg);
                }
            }
            LogFactory.warn("服务启动完毕！", ServiceFactory.class);
        }
    }

    /**
     * 启动服务
     * @param cfg
     */
    public static void startService(ServiceConfig cfg) {
        Service s = getService(cfg.getId());
        if (s != null) {
            stopService(cfg.getId());
        }
        if (getService(cfg.getId()) != null) {
            LogFactory.warn("服务(" + cfg.getId() + ")已经启动！", ServiceFactory.class);
            return;
        }
        if (cfg.getStatus() > 1) {
            LogFactory.warn("服务(" + cfg.getId() + ")被禁止启动！", ServiceFactory.class);
            return;
        }
        String threadgroupname = makeThreadGroupName(cfg.getId());
        ThreadGroup group = SystemInfo.getThreadGroup(threadgroupname);
        if (group == null) {
            group = new ThreadGroup(threadgroupname);
        }
        Service srv = new Service(group, "thread-" + cfg.getId(), cfg);
        synchronized (serviceMap) {
            serviceMap.put(cfg.getId(), srv);
        }
        synchronized (threadGroupMap) {
            threadGroupMap.put(group, srv);
        }
        srv.start();
        try {
            srv.join();//等待结束
            if (getService(cfg.getId()) == null) {
                SystemInfo.destroyThreadGroup(makeThreadGroupName(cfg.getId()));
            }
        } catch (InterruptedException ex) {
        }
    }

    /**
     * 移除服务句柄
     * @param service
     */
    protected static void removeService(Service service) {
        synchronized (serviceMap) {
            serviceMap.remove(service.getServiceConfig().getId());
        }
        synchronized (threadGroupMap) {
            threadGroupMap.remove(service.threadGroup);
        }
    }

    /**
     * 获取服务实例
     *
     * @param cfg
     * @return Service
     */
    public static Service getService(ServiceConfig cfg) {
        return getService(cfg.getId());
    }

    /**
     * 获取服务实例
     *
     * @param sid
     * @return Service
     */
    public static Service getService(Integer sid) {
        synchronized (serviceMap) {
            return serviceMap.get(sid);
        }
    }

    /**
     * 终止指定服务
     *
     * @param sid
     */
    public static void stopService(Integer sid) {
        Service s = getService(sid);
        if (s != null) {
            s.stoprun();
            SystemInfo.destroyThreadGroup(makeThreadGroupName(sid));
        } else {
            LogFactory.error("服务(" + sid + ")没有启动，不能执行关闭命令！", ServiceFactory.class);
        }
    }

    /**
     * 终止服务
     *
     */
    public static void stopService() {
        List<Service> list = new ArrayList<Service>(serviceMap.values());
        List<Future<Boolean>> lists = new ArrayList<Future<Boolean>>();
        for (Service sid : list) {
            AsyncCloseServices as = new AsyncCloseServices(sid);
            Future<Boolean> future = ThreadPools.startThread(as);
            lists.add(future);
        }
        for (Future<Boolean> future : lists) {
            try {
                future.get(60, TimeUnit.SECONDS);
            } catch (Exception ex) {
            }
        }
        synchronized (serviceMap) {
            serviceMap.clear();
        }
        synchronized (threadGroupMap) {
            threadGroupMap.clear();
        }
        LogFactory.warn("服务全部关闭！", ServiceFactory.class);
    }
}
