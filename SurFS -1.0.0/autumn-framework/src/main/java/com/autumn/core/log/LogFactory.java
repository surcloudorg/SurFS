/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.log;

import com.autumn.core.service.ServiceFactory;
import com.autumn.core.soap.SoapFactory;
import com.autumn.core.web.WebFactory;
import com.autumn.util.FileOperation;
import com.autumn.util.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Appender;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.net.SyslogAppender;

/**
 * <p>
 * Title: 日志工厂</p>
 *
 * <p>
 * Description: 提供获取日志输出器函数，管理各个日志目录</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public final class LogFactory {

    public static final String SYSTEM_LOGNAME = "system";
    protected static String logPath = null;//日志根目录
    protected static final HashMap<String, Logger> logHash = new HashMap<String, Logger>();

    /**
     * 获取所有日志目录名
     *
     * @return List<String>
     */
    public static List<String> getLogNames() {
        return new ArrayList<>(logHash.keySet());
    }

    /**
     * 设置日志输出路径
     *
     * @param alogpath
     */
    public static void setLogPath(String alogpath) {
        if (logPath == null) {
            if (alogpath != null && (!alogpath.trim().isEmpty())) {
                logPath = alogpath.trim();
                File path = new File(logPath);
                logPath = path.getAbsolutePath();
            }
        }
    }

    /**
     * 查找日志组件，找不到返回null
     *
     * @param name String
     * @return Logger
     */
    public static Logger findLogger(String name) {
        synchronized (logHash) {
            return logHash.get(name.toLowerCase());
        }
    }

    /**
     * 移除日志目录
     *
     * @param name String
     */
    public static Logger removeLogger(String name) {
        synchronized (logHash) {
            return logHash.remove(name.toLowerCase());
        }
    }

    /**
     * 查找日志目录，找不到返回system目录
     *
     * @return Logger
     */
    public static Logger getLogger() {
        Logger log = ServiceFactory.getLogger();//服务 
        if (log != null) {
            return log;
        }
        log = SoapFactory.getLogger();//soap服务 
        if (log != null) {
            return log;
        }
        log = WebFactory.getLogger();//web服务 
        if (log != null) {
            return log;
        }
        return getLogger(LogFactory.SYSTEM_LOGNAME);
    }

    /**
     * 查找日志目录
     *
     * @param caller
     * @return Logger
     */
    public static Logger getLogger(Class caller) {
        return getLogger().getLogger(caller);
    }

    /**
     * 构造一个日志组件，指定发起日志类
     *
     * @param name String
     * @param caller Class
     * @return Logger Logger
     */
    public static Logger getLogger(String name, Class caller) {
        return getLogger(name).getLogger(caller);
    }

    /**
     * 构造一个日志组件，指定发起日志类
     *
     * @param name String
     * @param caller String
     * @return Logger Logger
     */
    public static Logger getLogger(String name, String caller) {
        return getLogger(name).getLogger(caller);
    }

    /**
     * 查找日志目录,，找不到返回system目录
     *
     * @param name
     * @return Logger
     */
    public static Logger getLogger(String name) {
        Logger mylog = findLogger(name);
        if (mylog == null) {
            if (name.equalsIgnoreCase(SYSTEM_LOGNAME)) {
                try {
                    return addLogger(new LogProperties(SYSTEM_LOGNAME));
                } catch (Exception ex) {
                }
            }
            return getLogger(SYSTEM_LOGNAME);
        } else {
            return mylog;
        }
    }

    /**
     * 初始化日志
     *
     * @param p
     * @return Logger
     */
    public static Logger addLogger(LogProperties p) {
        Logger log = removeLogger(p.getLogName());
        if (log == null) {
            log = new Logger(p);
        } else {
            log.setProperties(p);
        }
        synchronized (logHash) {
            logHash.put(p.getLogName().toLowerCase(), log);
        }
        return log;
    }

    /**
     * 更新日志参数
     *
     * @param p
     */
    public static void updateLogger(LogProperties p) {
        Logger log = findLogger(p.getLogName());
        if (log != null) {
            log.setProperties(p);
        }
    }

    /**
     * 写系统日志
     *
     * @param s String
     * @param caller Class
     */
    public static void info(String s, Class caller) {
        getLogger().info(s, null, caller);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void info(String pattern, Object[] args, Class caller) {
        getLogger().info(pattern, args, caller);
    }

    /**
     * 写系统日志
     *
     * @param s String
     * @param caller String
     */
    public static void info(String s, String caller) {
        getLogger().info(s, null, caller);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void info(String pattern, Object[] args, String caller) {
        getLogger().info(pattern, args, caller);
    }

    /**
     * 写系统日志
     *
     * @param s String
     */
    public static void info(String s) {
        getLogger().info(s, null, (String) null);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     */
    public static void info(String pattern, Object[] args) {
        getLogger().info(pattern, args, (String) null);
    }

    /**
     * 写系统日志
     *
     * @param s String
     * @param caller Class
     */
    public static void debug(String s, Class caller) {
        getLogger().debug(s, null, caller);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void debug(String pattern, Object[] args, Class caller) {
        getLogger().debug(pattern, args, caller);
    }

    /**
     * 写系统日志
     *
     * @param s String
     * @param caller String
     */
    public static void debug(String s, String caller) {
        getLogger().debug(s, null, caller);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void debug(String pattern, Object[] args, String caller) {
        getLogger().debug(pattern, args, caller);
    }

    /**
     * 写系统日志
     *
     * @param s String
     */
    public static void debug(String s) {
        getLogger().debug(s, null, (String) null);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     */
    public static void debug(String pattern, Object[] args) {
        getLogger().debug(pattern, args, (String) null);
    }

    /**
     * 写系统日志
     *
     * @param s String
     * @param caller Class
     */
    public static void warn(String s, Class caller) {
        getLogger().warn(s, null, caller);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void warn(String pattern, Object[] args, Class caller) {
        getLogger().warn(pattern, args, caller);
    }

    /**
     * 写系统日志
     *
     * @param s String
     * @param caller String
     */
    public static void warn(String s, String caller) {
        getLogger().warn(s, null, caller);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void warn(String pattern, Object[] args, String caller) {
        getLogger().warn(pattern, args, caller);
    }

    /**
     * 写系统日志
     *
     * @param s String
     */
    public static void warn(String s) {
        getLogger().warn(s, null, (String) null);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     */
    public static void warn(String pattern, Object[] args) {
        getLogger().warn(pattern, args, (String) null);
    }

    /**
     * 写系统日志
     *
     * @param s String
     * @param caller Class
     */
    public static void error(String s, Class caller) {
        getLogger().error(s, null, caller);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void error(String pattern, Object[] args, Class caller) {
        getLogger().error(pattern, args, caller);
    }

    /**
     * 写系统日志
     *
     * @param s String
     * @param caller String
     */
    public static void error(String s, String caller) {
        getLogger().error(s, null, caller);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void error(String pattern, Object[] args, String caller) {
        getLogger().error(pattern, args, caller);
    }

    /**
     * 写系统日志
     *
     * @param s String
     */
    public static void error(String s) {
        getLogger().error(s, null, (String) null);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     */
    public static void error(String pattern, Object[] args) {
        getLogger().error(pattern, args, (String) null);
    }

    /**
     * 写系统日志
     *
     * @param s String
     * @param caller Class
     */
    public static void fatal(String s, Class caller) {
        getLogger().fatal(s, null, caller);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void fatal(String pattern, Object[] args, Class caller) {
        getLogger().fatal(pattern, args, caller);
    }

    /**
     * 写系统日志
     *
     * @param s String
     * @param caller String
     */
    public static void fatal(String s, String caller) {
        getLogger().fatal(s, null, caller);
    }

    /**
     * 写系统日志
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void fatal(String pattern, Object[] args, String caller) {
        getLogger().fatal(pattern, args, caller);
    }

    /**
     * 写系统日志
     *
     * @param s String
     */
    public static void fatal(String s) {
        fatal(s, null, (String) null);
    }

    /**
     * 写系统日志
     *
     * @param s
     * @param args
     */
    public static void fatal(String s, Object[] args) {
        fatal(s, args, (String) null);
    }

    /**
     * 输出错误
     *
     * @param t
     */
    public static void trace(String s, Throwable t, Class callclass) {
        getLogger().trace(s, t, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出错误
     *
     * @param t
     */
    public static void trace(String s, Throwable t, String caller) {
        getLogger().trace(s, t, caller);
    }

    /**
     * 输出错误
     *
     * @param t
     */
    public static void trace(String s, Throwable t) {
        getLogger().trace(s, t, (String) null);
    }

    /**
     * 初始化log4j日志
     */
    public static void configLog4j() {
        configLog4j(null);
    }

    /**
     * 初始化log4j日志
     */
    public static void configLog4j(Logger log) {
        String filename = FileOperation.searchFile("log4j.properties");
        if (filename != null) {
            configLog4j(filename, log);
        }
    }

    /**
     * 初始化log4j日志
     *
     * @param filename
     */
    @SuppressWarnings("unchecked")
    public static void configLog4j(String filename, Logger log) {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                throw new Exception("文件不存在");
            }
            org.apache.log4j.spi.LoggerRepository lr = org.apache.log4j.LogManager.getLoggerRepository();
            org.apache.log4j.PropertyConfigurator configurator = new org.apache.log4j.PropertyConfigurator();
            configurator.doConfigure(filename, lr);
            boolean needRedirect = true;
            Enumeration<Appender> e = org.apache.log4j.Logger.getRootLogger().getAllAppenders();
            while (e.hasMoreElements()) {
                Appender app = e.nextElement();
                if (app instanceof SocketAppender || app instanceof SyslogAppender) {//不能重定向
                    needRedirect = false;
                    break;
                }
            }
            if (needRedirect) {
                Log4jAppender appender = new Log4jAppender(log);
                org.apache.log4j.Logger.getRootLogger().removeAllAppenders();
                org.apache.log4j.Logger.getRootLogger().addAppender(appender);
                if (log == null) {
                    LogFactory.warn("log4j日志输出被重定向到com.autumn.core.log.Logger！", LogFactory.class);
                } else {
                    LogFactory.warn("log4j日志输出被重定向到com.autumn.core.log.Logger的{0}目录！",
                            new Object[]{log.getProperties().getLogName()}, LogFactory.class);
                }
            } else {
                Logger.writeLog4j = true;
            }
        } catch (Exception e) {
            LogFactory.error("从{0}读取配置错误！{1}", new Object[]{filename, e.getMessage()}, LogFactory.class);
        }
    }

    /**
     * 初始化JDKlog日志
     */
    public static void configJDKLog() {
        configJDKLog(null);
    }

    /**
     * 初始化JDKlog日志
     */
    public static void configJDKLog(Logger log) {
        String filename = FileOperation.searchFile("logging.properties");
        if (filename != null) {
            configJDKLog(filename, log);
        }
    }

    /**
     * 初始化JDKlog日志
     *
     * @param filename
     */
    public static void configJDKLog(String filename, Logger log) {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                throw new Exception("文件不存在");
            }
            java.util.logging.LogManager.getLogManager().readConfiguration(new FileInputStream(f));
            java.util.logging.Logger logger = java.util.logging.LogManager.getLogManager().getLogger("");
            java.util.logging.Handler[] handlers = logger.getHandlers();
            for (java.util.logging.Handler h : handlers) {
                logger.removeHandler(h);
            }
            JdkLogHandler handle = new JdkLogHandler(log);
            logger.addHandler(handle);
            if (log == null) {
                LogFactory.warn("JDK日志输出被重定向到com.autumn.core.log.Logger!", LogFactory.class);
            } else {
                LogFactory.warn("JDK日志输出被重定向到com.autumn.core.log.Logger的{0}目录！",
                        new Object[]{log.getProperties().getLogName()}, LogFactory.class);
            }
        } catch (Exception e) {
            LogFactory.error("从{0}读取配置错误！{1}", new Object[]{filename, e.getMessage()}, LogFactory.class);
        }
    }

    /**
     * 重定向System.err
     */
    public static void setSystemErr() {
        setSystemErr(LogFactory.getLogger("error", LogFactory.class));
    }

    /**
     * 重定向System.err
     */
    public static void setSystemErr(Logger log) {
        System.setErr(new TextPrinter(System.err, log));
    }

    /**
     * 将commons-logging-1.1.jar产生的日志重定向到log4j
     */
    public static void setLog4jLogger() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
    }

    /**
     * 将commons-logging-1.1.jar产生的日志重定向到JDKlog
     */
    public static void setJdkLogger() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
    }

    /**
     * 构造FileFinder（用来查找日志里的字符串）
     *
     * @param logName String 目录名
     * @return FileFinder
     */
    public static FileFinder getLogFinder(String logName) throws Exception {
        return getLogFinder(logName, new Date());
    }

    /**
     * 构造FileFinder（用来查找日志里的字符串）
     *
     * @param logName String 目录名
     * @param pageSize int 每页显示行数
     * @return FileFinder
     */
    public static FileFinder getLogFinder(String logName, int pageSize) throws Exception {
        return getLogFinder(logName, new Date(), pageSize);
    }

    /**
     * 构造FileFinder（用来查找日志里的字符串）
     *
     * @param logName String 目录名
     * @param date Date 日志日期
     * @return FileFinder
     */
    public static FileFinder getLogFinder(String logName, Date date) throws Exception {
        return getLogFinder(logName, TextUtils.Date2String(date, "yyyyMMdd"));
    }

    /**
     * 构造FileFinder（用来查找日志里的字符串）
     *
     * @param logName String 目录名
     * @param date Date 日志日期
     * @param pageSize int 每页显示行数
     * @return FileFinder
     */
    public static FileFinder getLogFinder(String logName, Date date,
            int pageSize) throws Exception {
        return getLogFinder(logName, TextUtils.Date2String(date, "yyyyMMdd"), pageSize);
    }

    /**
     * 构造FileFinder（用来查找日志里的字符串）
     *
     * @param logName String 目录名
     * @param datestr String 日期字符串
     * @return FileFinder
     */
    public static FileFinder getLogFinder(String logName, String datestr) throws Exception {
        return getLogFinder(logName, datestr, 100);
    }

    /**
     * 构造FileFinder（用来查找日志里的字符串）
     *
     * @param logName String 目录名
     * @param datestr String 日期字符串
     * @param pageSize int 每页显示行数
     * @return FileFinder
     */
    public static FileFinder getLogFinder(String logName, String datestr, int pageSize) throws Exception {
        Logger log = LogFactory.findLogger(logName);
        if (log != null) {
            return log.getFinder(datestr, pageSize);
        }
        if (logPath == null) {
            throw new Exception("文件未找到");
        }
        String path = logPath;
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        path = path + logName + File.separator + datestr + ".log";
        return new FileFinder(path, pageSize);
    }
}
