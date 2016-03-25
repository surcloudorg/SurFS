package com.autumn.core.soap;

import com.autumn.core.AsyncCloseServices;
import com.autumn.core.SystemInfo;
import com.autumn.core.ThreadPools;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.core.service.ServiceFactory;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.http.XFireServletController;

/**
 * <p>Title: SOAP框架</p>
 *
 * <p>Description: soap管理器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class SoapFactory {

    protected static final HashMap<String, SoapService> servicelist = new HashMap<String, SoapService>();
    private static final HashMap<ThreadGroup, SoapService> threadGroupMap = new HashMap<ThreadGroup, SoapService>();//<服务线程组,服务实例>
    private static final ThreadLocal<SoapService> threadService = new ThreadLocal<SoapService>();//线程变量
    public static final String SoapContextkey = "SOAPCONTEXT_IN_REQUEST";

    /**
     * 线程组的命名方式"ThreadGroup_" + Integer.toString(id)
     *
     * @param id
     * @return String
     */
    public static String makeThreadGroupName(int id) {
        return "FRAME/SOAP/..." + String.format("%1$5d", id).replaceAll(" ", ".");
    }

    /**
     * 服务参数
     *
     * @param servicename String 服务名
     * @return SoapContext
     */
    public static SoapContext getSoapContext(String servicename) {
        SoapService srv = getSoapService(servicename);
        if (srv != null) {
            return srv.getSoapContext();
        }
        return null;
    }

    /**
     * 获取soap服务实例
     *
     * @param servicename
     * @return SoapService
     */
    public static SoapService getSoapService(String servicename) {
        synchronized (servicelist) {
            return servicelist.get(servicename);
        }
    }

    /**
     * 服务参数
     *
     * @param id
     * @return SoapContext
     */
    public static SoapContext getSoapContext(int id) {
        SoapService srv = getSoapService(id);
        if (srv != null) {
            return srv.getSoapContext();
        }
        return null;
    }

    /**
     * 获取soap服务实例
     *
     * @param id
     * @return SoapService
     */
    public static SoapService getSoapService(int id) {
        synchronized (servicelist) {
            Collection<SoapService> e = servicelist.values();
            for (SoapService ssp : e) {
                if (ssp.getSoapContext().getId() == id) {
                    return ssp;
                }
            }
        }
        return null;
    }

    /**
     * 日志
     *
     * @return Logger
     */
    public static Logger getLogger() {
        SoapContext context = getSoapContext();
        if (context != null) {
            return context.getLogger();
        }
        return null;
    }

    /**
     * 设置线程变量
     *
     * @param s
     */
    public static void setSoapService(SoapService s) {
        threadService.set(s);
    }

    /**
     * 移出线程变量
     */
    public static void removeSoapService() {
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
     * 获取服务实例
     *
     * @return SoapService
     */
    public static SoapService getSoapService() {
        SoapService srv = threadService.get();
        if (srv == null) {
            HttpServletRequest request = XFireServletController.getRequest();
            if (request == null) {
                ThreadGroup group = getThreadGroup();
                if (group != null) {
                    synchronized (threadGroupMap) {
                        return threadGroupMap.get(group);
                    }
                }
            } else {
                return (SoapService) request.getAttribute(SoapContextkey);
            }
        }
        return srv;
    }

    /**
     * 获取服务参数
     *
     * @return SoapContext
     */
    public static SoapContext getSoapContext() {
        SoapService srv = getSoapService();
        if (srv != null) {
            return srv.getSoapContext();
        }
        return null;
    }


    /**
     * 获取服务标题
     *
     * @return TreeMap<String, String>
     */
    public static TreeMap<String, String> getSoapTitles() {
        TreeMap<String, String> hash = new TreeMap<String, String>();
        synchronized (servicelist) {
            Collection<SoapService> en = servicelist.values();
            for (SoapService wc : en) {
                if (wc.getSoapContext().getAuthtype() == 1 || wc.getSoapContext().getAuthtype() == 2) {
                    String id=String.valueOf(wc.getSoapContext().getId());
                    hash.put(id, id + "." + wc.getSoapContext().getServicename() + "--" + wc.getSoapContext().getTitle());
                }
            }
        }
        return hash;
    }

    /**
     * 服务实例
     *
     * @param params
     * @return Service
     */
    public static Service getService(SoapContext params) {
        return getService(params.getServicename());
    }

    /**
     * 服务实例
     *
     * @param name
     * @return Service
     */ 
    public static Service getService(String name) {
        try {
            return XFireFactory.newInstance().getXFire().getServiceRegistry().getService(name);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 启动服务
     */
    public static void startService(List<SoapContext> cfgs) {
        if (cfgs != null) {
            for (SoapContext cfg : cfgs) {
                if (cfg.getAuthtype() < 4) {
                    startService(cfg);
                }
            }
            LogFactory.warn("Soap服务启动完毕！", ServiceFactory.class);
        }
    }

    /**
     * 启动服务
     *
     * @param soapcontext
     */
    public static void startService(SoapContext soapcontext) {
        SoapService sc = getSoapService(soapcontext.getId());
        if (sc != null) {
            stopService(soapcontext.getId());
        }
        if (getSoapService(soapcontext.getId()) != null) {
            LogFactory.warn("Soap服务(" + soapcontext.getServicename() + ")已经启动！", SoapFactory.class);
            return;
        }
        if (soapcontext.getAuthtype() == 4) {
            LogFactory.warn("Soap服务(" + soapcontext.getServicename() + ")被禁止启动！", SoapFactory.class);
            return;
        }
        String threadgroupname = makeThreadGroupName(soapcontext.getId());
        ThreadGroup group = SystemInfo.getThreadGroup(threadgroupname);
        if (group == null) {
            group = new ThreadGroup(threadgroupname);
        }
        SoapService srv = new SoapService(group, "thread-" + soapcontext.getId(), soapcontext);
        synchronized (servicelist) {
            servicelist.put(soapcontext.getServicename(), srv);
        }
        synchronized (threadGroupMap) {
            threadGroupMap.put(group, srv);
        }
        srv.start();
        try {
            srv.join();//等待结束
            if (getSoapService(soapcontext.getId()) == null) {
                SystemInfo.destroyThreadGroup(makeThreadGroupName(soapcontext.getId()));
            } else {
                Object obj = getSoapContext(soapcontext.getId()).getSoapInstance();
                if (!(obj instanceof SoapInstance)) {
                    SystemInfo.destroyThreadGroup(makeThreadGroupName(soapcontext.getId()));
                }
            }
        } catch (InterruptedException ex) {
        }
    }

    /**
     * 注销
     *
     * @param sid Integer
     */
    public static void stopService(Integer sid) {
        SoapService ss = getSoapService(sid);
        if (ss != null) {
            ss.stoprun();
            SystemInfo.destroyThreadGroup(makeThreadGroupName(sid));
        } else {
            LogFactory.error("服务(" + sid + ")没有启动，不能执行关闭命令！", SoapFactory.class);
        }
    }

    /**
     * 移除服务句柄
     */
    protected static void removeService(SoapService soapService) {
        synchronized (servicelist) {
            servicelist.remove(soapService.getSoapContext().getServicename());
        }
        synchronized (threadGroupMap) {
            threadGroupMap.remove(soapService.threadGroup);
        }
    }

    /**
     * 注销
     */
    public static void stopService() {
        List<SoapService> list = new ArrayList<SoapService>(servicelist.values());
        List<Future<Boolean>> lists = new ArrayList<Future<Boolean>>();
        for (SoapService sid : list) {
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
        synchronized (servicelist) {
            servicelist.clear();
        }
        synchronized (threadGroupMap) {
            threadGroupMap.clear();
        }
        LogFactory.warn("Soap服务全部关闭！", SoapFactory.class);
    }
}
