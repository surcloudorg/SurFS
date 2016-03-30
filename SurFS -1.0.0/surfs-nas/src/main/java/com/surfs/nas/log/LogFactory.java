/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.log;

import com.surfs.nas.util.Function;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Appender;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.net.SyslogAppender;

public final class LogFactory {

    public static final String SYSTEM_LOGNAME = "system";
    protected static String logPath = null;
    protected static final HashMap<String, Logger> logHash = new HashMap<>();

    public static List<String> getLogNames() {
        return new ArrayList<>(logHash.keySet());
    }

    /**
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
     *
     * @param name String
     * @return
     */
    public static Logger removeLogger(String name) {
        synchronized (logHash) {
            return logHash.remove(name.toLowerCase());
        }
    }

    /**
     *
     * @return Logger
     */
    public static Logger getLogger() {
        return getLogger(LogFactory.SYSTEM_LOGNAME);
    }

    /**
     *
     * @param caller
     * @return Logger
     */
    public static Logger getLogger(Class caller) {
        return getLogger().getLogger(caller);
    }

    /**
     *
     * @param name String
     * @param caller Class
     * @return Logger Logger
     */
    public static Logger getLogger(String name, Class caller) {
        return getLogger(name).getLogger(caller);
    }

    /**
     *
     * @param name String
     * @param caller String
     * @return Logger Logger
     */
    public static Logger getLogger(String name, String caller) {
        return getLogger(name).getLogger(caller);
    }

    /**
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
     *
     * @param s String
     * @param caller Class
     */
    public static void info(String s, Class caller) {
        getLogger().info(s, null, caller);
    }

    /**
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void info(String pattern, Object[] args, Class caller) {
        getLogger().info(pattern, args, caller);
    }

    /**
     * @param s String
     * @param caller String
     */
    public static void info(String s, String caller) {
        getLogger().info(s, null, caller);
    }

    /**
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void info(String pattern, Object[] args, String caller) {
        getLogger().info(pattern, args, caller);
    }

    /**
     *
     * @param s String
     */
    public static void info(String s) {
        getLogger().info(s, null, (String) null);
    }

    /**
     *
     * @param pattern
     * @param args
     */
    public static void info(String pattern, Object[] args) {
        getLogger().info(pattern, args, (String) null);
    }

    /**
     *
     * @param s String
     * @param caller Class
     */
    public static void debug(String s, Class caller) {
        getLogger().debug(s, null, caller);
    }

    /**
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void debug(String pattern, Object[] args, Class caller) {
        getLogger().debug(pattern, args, caller);
    }

    /**
     *
     * @param s String
     * @param caller String
     */
    public static void debug(String s, String caller) {
        getLogger().debug(s, null, caller);
    }

    /**
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void debug(String pattern, Object[] args, String caller) {
        getLogger().debug(pattern, args, caller);
    }

    /**
     *
     * @param s String
     */
    public static void debug(String s) {
        getLogger().debug(s, null, (String) null);
    }

    /**
     *
     * @param pattern
     * @param args
     */
    public static void debug(String pattern, Object[] args) {
        getLogger().debug(pattern, args, (String) null);
    }

    /**
     *
     * @param s String
     * @param caller Class
     */
    public static void warn(String s, Class caller) {
        getLogger().warn(s, null, caller);
    }

    /**
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void warn(String pattern, Object[] args, Class caller) {
        getLogger().warn(pattern, args, caller);
    }

    /**
     *
     * @param s String
     * @param caller String
     */
    public static void warn(String s, String caller) {
        getLogger().warn(s, null, caller);
    }

    /**
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void warn(String pattern, Object[] args, String caller) {
        getLogger().warn(pattern, args, caller);
    }

    /**
     *
     * @param s String
     */
    public static void warn(String s) {
        getLogger().warn(s, null, (String) null);
    }

    /**
     *
     * @param pattern
     * @param args
     */
    public static void warn(String pattern, Object[] args) {
        getLogger().warn(pattern, args, (String) null);
    }

    /**
     *
     * @param s String
     * @param caller Class
     */
    public static void error(String s, Class caller) {
        getLogger().error(s, null, caller);
    }

    /**
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void error(String pattern, Object[] args, Class caller) {
        getLogger().error(pattern, args, caller);
    }

    /**
     * @param s String
     * @param caller String
     */
    public static void error(String s, String caller) {
        getLogger().error(s, null, caller);
    }

    /**
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void error(String pattern, Object[] args, String caller) {
        getLogger().error(pattern, args, caller);
    }

    /**
     *
     * @param s String
     */
    public static void error(String s) {
        getLogger().error(s, null, (String) null);
    }

    /**
     *
     * @param pattern
     * @param args
     */
    public static void error(String pattern, Object[] args) {
        getLogger().error(pattern, args, (String) null);
    }

    /**
     * @param s String
     * @param caller Class
     */
    public static void fatal(String s, Class caller) {
        getLogger().fatal(s, null, caller);
    }

    /**
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void fatal(String pattern, Object[] args, Class caller) {
        getLogger().fatal(pattern, args, caller);
    }

    /**
     *
     * @param s String
     * @param caller String
     */
    public static void fatal(String s, String caller) {
        getLogger().fatal(s, null, caller);
    }

    /**
     *
     * @param pattern
     * @param args
     * @param caller
     */
    public static void fatal(String pattern, Object[] args, String caller) {
        getLogger().fatal(pattern, args, caller);
    }

    /**
     *
     * @param s String
     */
    public static void fatal(String s) {
        fatal(s, null, (String) null);
    }

    /**
     *
     * @param s
     * @param args
     */
    public static void fatal(String s, Object[] args) {
        fatal(s, args, (String) null);
    }

    /**
     *
     * @param s
     * @param t
     * @param callclass
     */
    public static void trace(String s, Throwable t, Class callclass) {
        getLogger().trace(s, t, callclass == null ? null : callclass.getName());
    }

    /**
     *
     * @param s
     * @param t
     * @param caller
     */
    public static void trace(String s, Throwable t, String caller) {
        getLogger().trace(s, t, caller);
    }

    /**
     *
     * @param s
     * @param t
     */
    public static void trace(String s, Throwable t) {
        getLogger().trace(s, t, (String) null);
    }


    public static void configLog4j() {
        configLog4j(null);
    }

    public static void configLog4j(Logger log) {
        String filename = Function.searchFile("log4j.properties");
        if (filename != null) {
            configLog4j(filename, log);
        }
    }

    /**
     *
     * @param filename
     * @param log
     */
    @SuppressWarnings("unchecked")
    public static void configLog4j(String filename, Logger log) {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                return;
            }
            org.apache.log4j.spi.LoggerRepository lr = org.apache.log4j.LogManager.getLoggerRepository();
            org.apache.log4j.PropertyConfigurator configurator = new org.apache.log4j.PropertyConfigurator();
            configurator.doConfigure(filename, lr);
            boolean needRedirect = true;
            Enumeration<Appender> e = org.apache.log4j.Logger.getRootLogger().getAllAppenders();
            while (e.hasMoreElements()) {
                Appender app = e.nextElement();
                if (app instanceof SocketAppender || app instanceof SyslogAppender) {
                    needRedirect = false;
                    break;
                }
            }
            if (needRedirect) {
                Log4jAppender appender = new Log4jAppender(log);
                org.apache.log4j.Logger.getRootLogger().removeAllAppenders();
                org.apache.log4j.Logger.getRootLogger().addAppender(appender);
            } else {
                Logger.writeLog4j = true;
            }
        } catch (Exception e) {
        }
    }

    public static void configJDKLog() {
        configJDKLog(null);
    }

    public static void configJDKLog(Logger log) {
        String filename = Function.searchFile("logging.properties");
        if (filename != null) {
            configJDKLog(filename, log);
        }
    }

    /**
     *
     *
     * @param filename
     * @param log
     */
    public static void configJDKLog(String filename, Logger log) {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                return;
            }
            java.util.logging.LogManager.getLogManager().readConfiguration(new FileInputStream(f));
            java.util.logging.Logger logger = java.util.logging.LogManager.getLogManager().getLogger("");
            java.util.logging.Handler[] handlers = logger.getHandlers();
            for (java.util.logging.Handler h : handlers) {
                logger.removeHandler(h);
            }
            JdkLogHandler handle = new JdkLogHandler(log);
            logger.addHandler(handle);

        } catch (Exception e) {
        }
    }

    public static void setSystemErr() {
        setSystemErr(LogFactory.getLogger("error", LogFactory.class));
    }

    /**
     * @param log
     */
    public static void setSystemErr(Logger log) {
        System.setErr(new TextPrinter(System.err, log));
    }

    public static void setLog4jLogger() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
    }

    public static void setJdkLogger() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
    }

}
