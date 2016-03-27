/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import com.autumn.core.AsyncCloseServices;
import com.autumn.core.SystemInfo;
import com.autumn.core.ThreadPools;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.servlet.Filter;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: 初始化web模块</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WebFactory {

    protected static String instanceName = null; //本地机器名
    private static final HashMap<String, WebService> dirs = new HashMap<String, WebService>(); //存储所有web目录参数
    private static final HashMap<ThreadGroup, WebService> threadGroupMap = new HashMap<ThreadGroup, WebService>();//<服务线程组,服务实例>
    private static final ThreadLocal<WebService> threadService = new ThreadLocal<WebService>();//线程变量

    /**
     * 获取框架运行的实例名
     *
     * @return String
     */
    public static String getInstanceName() {
        if (instanceName == null) {
            try {
                InetAddress addr = InetAddress.getLocalHost();
                instanceName = addr.getHostName();
            } catch (Exception ex) {
                instanceName = "";
            }
        }
        instanceName = instanceName.replaceAll("\\.", "").replaceAll("'", "’");
        return instanceName;
    }

    /**
     * 设置线程变量
     *
     * @param s
     */
    public static void setWebService(WebService s) {
        threadService.set(s);
    }

    /**
     * 移出线程变量
     */
    public static void removeWebService() {
        threadService.remove();
    }

    /**
     * web目录列表 排序
     *
     * @param needlogin
     * @return TreeMap<String, String>
     */
    public static TreeMap<String, String> getWebDirs(boolean needlogin) {
        TreeMap<String, String> hash = new TreeMap<String, String>();
        synchronized (dirs) {
            Collection<WebService> en = dirs.values();
            for (WebService ws : en) {
                WebDirectory wc = ws.getWebDirectory();
                if (wc.getId() <= 0) {
                    continue;
                }
                if (wc.getLogintype() != 0) {
                    if (needlogin) { //仅包含需要登陆的
                        if (wc.getLogintype() == 1 || wc.getLogintype() == 4) {
                            hash.put(String.valueOf(wc.getId()), wc.getId() + "." + wc.getDirName() + "--" + wc.getTitle());
                        }
                    } else {
                        hash.put(String.valueOf(wc.getId()), wc.getId() + "." + wc.getDirName() + "--" + wc.getTitle());
                    }
                }
            }
        }
        return hash;
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
     * 线程组的命名方式"ThreadGroup_" + Integer.toString(id)
     *
     * @param id
     * @return String
     */
    public static String makeThreadGroupName(int id) {
        return "FRAME/WEB/...." + String.format("%1$5d", id).replaceAll(" ", ".");
    }

    /**
     * 获取单个目录服务实例
     *
     * @param dirname String 目录名
     * @return WebFactory
     */
    public static WebService getWebService(String dirname) {
        synchronized (dirs) {
            return dirs.get(dirname);
        }
    }

    /**
     * 获取单个目录配置
     *
     * @param dirname String 目录名
     * @return WebFactory
     */
    public static WebDirectory getWebDirectory(String dirname) {
        WebService ws = getWebService(dirname);
        if (ws != null) {
            return ws.getWebDirectory();
        } else {
            return null;
        }
    }

    /**
     * 获取单个目录服务实例
     *
     * @param dirid int 目录ID
     * @return WebFactory
     */
    public static WebService getWebService(int dirid) {
        Collection<WebService> en;
        synchronized (dirs) {
            en = dirs.values();
            for (WebService wc : en) {
                if (dirid == wc.getWebDirectory().getId()) {
                    return wc;
                }
            }
        }
        return null;
    }

    /**
     * 获取单个目录配置
     *
     * @param dirid int 目录ID
     * @return WebFactory
     */
    public static WebDirectory getWebDirectory(int dirid) {
        WebService ws = getWebService(dirid);
        if (ws != null) {
            return ws.getWebDirectory();
        } else {
            return null;
        }
    }

    /**
     * 获取当前目录服务实例
     *
     * @return WebFactory
     */
    public static WebService getWebService() {
        WebService wd = threadService.get();
        if (wd == null) {
            ThreadGroup group = getThreadGroup();
            if (group != null) {
                synchronized (threadGroupMap) {
                    return threadGroupMap.get(group);
                }
            }
        }
        return wd;
    }

    /**
     * 获取当前目录配置
     *
     * @return WebFactory
     */
    public static WebDirectory getWebDirectory() {
        WebService ws = getWebService();
        if (ws != null) {
            return ws.getWebDirectory();
        } else {
            ActionContext ac = ActionContext.getActionContext();
            if (ac != null && ac.getWebDirectory() != null) {
                return ac.getWebDirectory();
            } else {
                return null;
            }
        }
    }

    /**
     * 获取当前线程所属服务的日志输出
     *
     * @return Logger
     */
    public static Logger getLogger() {
        WebDirectory service = getWebDirectory();
        if (service != null) {
            return service.getLogger();
        }
        return null;
    }

    /**
     * 更新web配置,初始化web服务
     */
    public static void startService(WebDirectory cfg) { //更新web配置
        WebService ws = getWebService(cfg.getId());
        if (ws != null) {
            stopService(cfg.getId());
        }
        if (getWebService(cfg.getId()) != null) {
            LogFactory.warn("WEB服务(" + cfg.getDirName() + ")已经启动！", WebFactory.class);
            return;
        }
        if (cfg.getLogintype() == 0) {
            LogFactory.warn("WEB服务(" + cfg.getDirName() + ")被禁止启动！", WebFactory.class);
            return;
        }
        String threadgroupname = makeThreadGroupName(cfg.getId());
        ThreadGroup group = SystemInfo.getThreadGroup(threadgroupname);
        if (group == null) {
            group = new ThreadGroup(threadgroupname);
        }
        WebService srv = new WebService(group, "thread-" + cfg.getId(), cfg);
        synchronized (dirs) {
            dirs.put(cfg.getDirName(), srv);
        }
        synchronized (threadGroupMap) {
            threadGroupMap.put(group, srv);
        }
        srv.start();
        try {
            srv.join();//等待结束
            if (getWebService(cfg.getId()) == null) {
                SystemInfo.destroyThreadGroup(makeThreadGroupName(cfg.getId()));
            } else {
                Object obj = getWebDirectory(cfg.getId()).getWebInstance();
                if (obj != null) {
                    if (!(obj instanceof Filter)) {
                        SystemInfo.destroyThreadGroup(makeThreadGroupName(cfg.getId()));
                    }
                } else {
                    SystemInfo.destroyThreadGroup(makeThreadGroupName(cfg.getId()));
                }
            }
        } catch (InterruptedException ex) {
        }
    }

    /**
     * 移出单个目录配置，关闭服务
     *
     * @param dirid int 目录id
     */
    public static void stopService(int dirid) {
        WebService wd = getWebService(dirid);
        if (wd != null) {
            wd.stoprun();
            SystemInfo.destroyThreadGroup(makeThreadGroupName(dirid));
        } else {
            LogFactory.error("WEB服务(" + dirid + ")没有启动，不能执行关闭命令！", WebFactory.class);
        }
    }

    /**
     * 移除服务句柄
     *
     * @param aThis
     */
    public static void removeService(WebService aThis) {
        synchronized (dirs) {
            dirs.remove(aThis.getWebDirectory().getDirName());
        }
        synchronized (threadGroupMap) {
            threadGroupMap.remove(aThis.threadGroup);
        }
    }

    /**
     * 清除web配置，关闭服务
     */
    @SuppressWarnings("unchecked")
    public static void stopService() {
        List<WebService> list = new ArrayList<WebService>(dirs.values());
        List<Future<Boolean>> lists = new ArrayList<Future<Boolean>>();
        for (WebService sid : list) {
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
        synchronized (dirs) {
            dirs.clear();
        }
        synchronized (threadGroupMap) {
            threadGroupMap.clear();
        }
        LogFactory.warn("WEB服务全部关闭！", WebFactory.class);
    }

    /**
     * 启动服务
     * @param cfgs
     */
    public static void startService(List<WebDirectory> cfgs) {
        if (cfgs != null) {
            for (WebDirectory cfg : cfgs) {
                startService(cfg);
            }
            LogFactory.warn("Web服务启动完毕！", WebFactory.class);
        }
    }

    /**
     * 添加console/services等系统控制目录
     */
    public static void initService() {
        List<WebDirectory> webs = WebInitializer.initService();
        for (WebDirectory cfg : webs) {
            startService(cfg);
        }
    }
}
