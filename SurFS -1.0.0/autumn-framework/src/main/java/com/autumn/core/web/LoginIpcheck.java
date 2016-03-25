package com.autumn.core.web;

import com.autumn.util.TextUtils;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: 登录ip检测</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class LoginIpcheck {

    protected static boolean checkip = false;//登录ip检测
    public static ConcurrentHashMap<String, LoginIpcheck> iplist = new ConcurrentHashMap<String, LoginIpcheck>();
    private int times = 0;
    private long lasttime = 0;

    static {
        String bol = Initializer.servletContext.getInitParameter("checkLoginIp");
        checkip = TextUtils.parseBoolean(bol, false);
    }

    /**
     * 获取远程IP
     *
     * @param request
     * @return String
     */
    public static String getAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;

    }

    /**
     * 清除3小时前访问ip
     */
    private static void clearTimeout() {
        if (iplist.size() <= 10000) {
            return;
        }
        long nowtime = (new Date()).getTime();
        for (Map.Entry<String, LoginIpcheck> entry : iplist.entrySet()) {
            LoginIpcheck info = entry.getValue();
            if (nowtime - info.getLasttime() > 1000 * 60 * 60 * 3) {
                iplist.remove(entry.getKey());
            }
        }
    }

    /**
     * 检查访问ip半小时内是否超过3次
     *
     * @param cip String
     * @return boolean
     */
    public static boolean Check(String cip) {
        if (!checkip) {
            return false;
        }
        clearTimeout();
        if (iplist.get(cip) != null) {
            LoginIpcheck info = iplist.get(cip);
            long nowtime = (new Date()).getTime();
            int times = info.getTimes();
            if (times >= 5) {
                long lasttime = info.getLasttime();
                if (nowtime - lasttime < 1000 * 60 * 30) {
                    return true;
                } else {
                    info.setTimes(1);
                    info.setLasttime(nowtime);
                }
            } else {
                long lasttime = info.getLasttime();
                if (nowtime - lasttime < 1000 * 60 * 6) {
                    info.setTimes(times + 1);
                    info.setLasttime(nowtime);
                } else {
                    info.setLasttime(nowtime);
                }
            }
        }
        return false;
    }

    /**
     * 添加ip
     *
     * @param cip String
     */
    public static void addip(String cip) {
        if (!checkip) {
            return;
        }
        if (iplist.get(cip) == null) {
            long nowtime = (new Date()).getTime();
            LoginIpcheck info = new LoginIpcheck();
            info.setTimes(1);
            info.setLasttime(nowtime);
            iplist.put(cip, info);
        }
    }

    /**
     * 清除ip
     *
     * @param cip String
     */
    public static void removeip(String cip) {
        if (iplist.get(cip) != null) {
            iplist.remove(cip);
        }
    }

    /**
     * 清除
     */
    public static void clear() {
        iplist.clear();
    }

    /**
     * @return the times
     */
    public int getTimes() {
        return times;
    }

    /**
     * @param times the times to set
     */
    public void setTimes(int times) {
        this.times = times;
    }

    /**
     * @return the lasttime
     */
    public long getLasttime() {
        return lasttime;
    }

    /**
     * @param lasttime the lasttime to set
     */
    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }
}
