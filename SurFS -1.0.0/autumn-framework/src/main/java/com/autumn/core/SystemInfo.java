package com.autumn.core;

import com.autumn.core.log.LogFactory;
import com.autumn.core.web.WebFactory;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.*;

/**
 * <p>Title: 系统信息</p>
 *
 * <p>Description: 获取磁盘，线程，内存等信息</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
@SuppressWarnings("all")
public class SystemInfo {

    /**
     * 获取内存信息
     *
     * @return String
     */
    public static String getMemInfo() {
        DecimalFormat df = new DecimalFormat("0.00");
        com.sun.management.OperatingSystemMXBean osmxb = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double totalMemorySize = osmxb.getTotalPhysicalMemorySize();
        double freeMemorySize = osmxb.getFreePhysicalMemorySize();
        double usedMemorySize = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize());
        StringBuilder sb = new StringBuilder("服务器内存使用情况:\r\n");
        sb.append("------物理内存:").append(df.format(totalMemorySize / 1024 / 1024)).append("M\r\n");
        sb.append("------占用内存:").append(df.format(usedMemorySize / 1024 / 1024)).append("M\r\n");
        sb.append("------空闲内存:").append(df.format(freeMemorySize / 1024 / 1024)).append("M\r\n");
        sb.append("虚拟机内存使用情况:\r\n");
        double maxMemory = Runtime.getRuntime().maxMemory();
        double totalMemory = Runtime.getRuntime().totalMemory();
        double freeMemory = Runtime.getRuntime().freeMemory();
        sb.append("------最大允许内存:").append(df.format(maxMemory / 1024 / 1024)).append("M\r\n");
        sb.append("------占用物理内存:").append(df.format(totalMemory / 1024 / 1024)).append("M\r\n");
        sb.append("------未使用的内存:").append(df.format(freeMemory / 1024 / 1024)).append("M\r\n");
        return sb.toString();
    }
    private static final String MAIN_THREADGROUP = "main";

    /**
     * 获取当前线程的main一级线程组
     *
     * @return ThreadGroup
     */
    public static ThreadGroup getCurrentThreadGroup() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while (group != null) {
            if (group.getParent() != null && group.getParent().getName().equals(MAIN_THREADGROUP)) {
                return group;
            }
            group = group.getParent();
        }
        return null;
    }

    /**
     * 获取main的下一级线程组，通过组名
     *
     * @param tgname
     * @return ThreadGroup
     */
    public static ThreadGroup getThreadGroup(String tgname) {
        ThreadGroup group = getMainThreadGroup();
        int gsize = group.activeGroupCount();
        ThreadGroup[] tgs = new ThreadGroup[gsize];
        int size = group.enumerate(tgs, false);
        for (int ii = 0; ii < size; ii++) {
            ThreadGroup tg = tgs[ii];
            if (tg.getName().equals(tgname)) {
                return tg;
            }
        }
        return null;
    }

    /**
     * 获取系统(main)线程组
     *
     * @return ThreadGroup
     */
    public static ThreadGroup getMainThreadGroup() {
        ThreadGroup maingroup = Thread.currentThread().getThreadGroup();
        while (maingroup != null) {
            if (maingroup.getName().equals(MAIN_THREADGROUP)) {
                return maingroup;
            }
            maingroup = maingroup.getParent();
        }
        return maingroup;
    }

    /**
     * 销毁线程组
     *
     * @param tg
     */
    public static void destroyThreadGroup(ThreadGroup tg) {
        if (tg != null) {
            try {
                if (tg.activeCount() == 0) {
                    if (!tg.isDestroyed()) {
                        tg.destroy();
                    }
                } else {
                    LogFactory.error("销毁线程组{0}无法销毁！存在活动线程:\r\n{1}",
                            new Object[]{tg.getName(), SystemInfo.getThreadGroupInfo(tg, "--")});

                }
            } catch (Exception e) {
                LogFactory.error("销毁线程组{1}失败：{0}", new Object[]{e, tg.getName()}, SystemInfo.class);
            }
        }
    }

    /**
     * 销毁线程组
     *
     * @param tgname
     */
    public static void destroyThreadGroup(String tgname) {
        destroyThreadGroup(getThreadGroup(tgname));
    }

    /**
     * 获取线程信息
     *
     * @return String
     */
    public static String getThreadInfo() {
        ThreadGroup maingroup = getMainThreadGroup();
        StringBuilder sb = new StringBuilder();
        sb.append("系统总线程数：").append(maingroup.activeCount()).append("\r\n");
        sb.append(getThreadGroupInfo(maingroup, ""));
        return sb.toString();
    }

    /**
     * 获取线程组线程信息
     *
     * @param group
     * @param space
     * @return String
     */
    public static String getThreadGroupInfo(ThreadGroup group, String space) {
        StringBuilder sb = new StringBuilder();
        int gsize = group.activeGroupCount();
        ThreadGroup[] tgs = new ThreadGroup[gsize];
        int size = group.enumerate(tgs, false);
        HashMap<String, ThreadGroup> tglist = new HashMap<String, ThreadGroup>();
        for (int ii = 0; ii < size; ii++) {//需要按名称排序
            ThreadGroup tg = tgs[ii];
            tglist.put(tg.getName(), tg);
        }
        List<String> tgnames = new ArrayList<String>(tglist.keySet());
        Collections.sort(tgnames);
        for (String tgname : tgnames) {
            ThreadGroup tg = tglist.get(tgname);
            sb.append(space).append("[").append(tg.getName()).append("]线程数：").append(tg.activeCount()).append("\r\n");
            String ss = getThreadGroupInfo(tg, space + "------");
            sb.append(ss);
        }
        int tsize = group.activeCount();
        Thread[] ts = new Thread[tsize];
        size = group.enumerate(ts, false);
        for (int ii = 0; ii < size; ii++) {
            Thread t = ts[ii];
            if (t == Thread.currentThread()) {
                continue;
            }
            String ss = t.getName();
            String clsname = t.getClass().getName();
            sb.append(space).append(ss).append(",类名：").append(clsname).append(",状态：").append(t.getState()).append("\r\n");
        }
        return sb.toString();
    }

    /**
     * 获取虚拟机运行参数
     *
     * @return String
     */
    public static String getSimpleProperties() {
        StringBuilder sb = new StringBuilder("实例名：");
        sb.append(WebFactory.getInstanceName());
        sb.append("\r\n");
        sb.append("虚拟机运行参数:\r\n");
        sb.append("------os.name：").append(System.getProperty("os.name")).append("\r\n");
        sb.append("------os.version：").append(System.getProperty("os.version")).append("\r\n");
        sb.append("------os.arch：").append(System.getProperty("os.arch")).append("\r\n");
        sb.append("------user.name：").append(System.getProperty("user.name")).append("\r\n");
        sb.append("------user.dir：").append(System.getProperty("user.dir")).append("\r\n");
        sb.append("------user.home：").append(System.getProperty("user.home")).append("\r\n");
        sb.append("------user.timezone：").append(System.getProperty("user.timezone")).append("\r\n");
        sb.append("------user.language：").append(System.getProperty("user.language")).append("\r\n");
        sb.append("------java.home：").append(System.getProperty("java.home")).append("\r\n");
        sb.append("------java.version：").append(System.getProperty("java.version")).append("\r\n");
        sb.append("------file.encoding：").append(System.getProperty("file.encoding")).append("\r\n");
        sb.append("------classpath：").append(System.getProperty("java.class.path")).append("\r\n");
        return sb.toString();
    }

    /**
     * 获取虚拟机运行参数
     *
     * @return String
     */
    public static String getJVMProperties() {
        Properties ps = System.getProperties();
        StringBuilder sb = new StringBuilder("虚拟机运行参数:\r\n");
        sb.append("------实例名：").append(WebFactory.getInstanceName()).append("\r\n");
        Set<Object> set = ps.keySet();
        for (Object obj : set) {
            sb.append("------").append(obj).append("=").append(ps.getProperty(obj.toString())).append("\r\n");
        }
        return sb.toString();
    }

    /**
     * 磁盘信息
     *
     * @return String
     */
    public static String getDiskInfo() {
        File[] roots = File.listRoots(); //获取磁盘分区列表　
        DecimalFormat df = new DecimalFormat("0.00");
        StringBuilder sb = new StringBuilder("服务器磁盘使用情况:\r\n");
        for (File file : roots) {
            if (file.getTotalSpace() == 0) {
                continue;
            }
            sb.append("------驱动器路径：").append(file.getAbsolutePath()).append("\r\n");
            double size = file.getTotalSpace();
            sb.append("------------总容量：").append(df.format(size / 1024 / 1024 / 1024)).append("G\r\n");
            size = file.getUsableSpace();
            sb.append("------------未使用容量：").append(df.format(size / 1024 / 1024 / 1024)).append("G\r\n");
            size = file.getTotalSpace() - file.getUsableSpace();
            sb.append("------------已使用容量：").append(df.format(size / 1024 / 1024 / 1024)).append("G\r\n");
        }
        return sb.toString();
    }
}
