package com.autumn.core.log;

import com.autumn.core.ClassManager;
import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.ConfigParser;
import com.autumn.util.TextUtils;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * <p>Title: 日志目录配置</p>
 *
 * <p>Description: 日志目录配置，支持动态更改</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class LogProperties {

    private boolean outConsole = true; //是否输出到控制台
    private String Dateformatter = "[MM-dd HH:mm:ss]";
    private boolean AddClassName = true; //添加类名
    private boolean AddLevel = true; //添加输出级别
    private Level level = Level.DEBUG; //输出级别
    private String logName = ""; //目录名
    private Pattern filter = null;//过滤，正则表达式
    private int WarnInteral = 1000 * 60 * 10;//报警间隔，0持续报警
    private String WarnClass = null;//报警类名
    private ConfigParser params = new ConfigParser();//报警参数配置

    /**
     * 目录名不能为汉字
     *
     * @param logName
     * @throws Exception
     */
    public LogProperties(String logName) throws Exception {
        if (!TextUtils.isValidFileName(logName)) {
            throw new Exception("无效的目录名");
        }
        this.logName = logName.trim();
    }

    /**
     * @return the outConsole
     */
    public boolean isOutConsole() {
        return outConsole;
    }

    /**
     * @param outConsole the outConsole to set
     */
    public synchronized void setOutConsole(boolean outConsole) {
        this.outConsole = outConsole;
    }

    /**
     * @return the Dateformatter
     */
    public String getDateformatter() {
        return Dateformatter;
    }

    /**
     * @param Dateformatter the Dateformatter to set
     */
    public synchronized void setDateformatter(String Dateformatter) {
        this.Dateformatter = Dateformatter == null ? "" : Dateformatter.trim();
    }

    /**
     * @return the AddClassName
     */
    public boolean isAddClassName() {
        return AddClassName;
    }

    /**
     * @param AddClassName the AddClassName to set
     */
    public synchronized void setAddClassName(boolean AddClassName) {
        this.AddClassName = AddClassName;
    }

    /**
     * @return the AddLevel
     */
    public boolean isAddLevel() {
        return AddLevel;
    }

    /**
     * @param AddLevel the AddLevel to set
     */
    public synchronized void setAddLevel(boolean AddLevel) {
        this.AddLevel = AddLevel;
    }

    /**
     * @return the level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public synchronized void setLevel(int level) {
        this.level = Level.newstance(level);
    }

    /**
     * @return the logName
     */
    public String getLogName() {
        return logName;
    }

    /**
     * @return the filter
     */
    public Pattern getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public synchronized void setFilter(String filter) {
        if (filter == null || filter.equals("")) {
            this.filter = null;
            return;
        }
        try {
            Pattern p = Pattern.compile(filter);
            this.filter = p;
        } catch (Exception e) {
            LogFactory.error("过滤词设置错误，正则表达式不能编译:{0}", new Object[]{e}, LogProperties.class);
            this.filter = null;
        }
    }

    /**
     * @return the WarnInteral
     */
    public int getWarnInteral() {
        return WarnInteral;
    }

    /**
     * @param WarnInteral the WarnInteral to set
     */
    public synchronized void setWarnInteral(int WarnInteral) {
        this.WarnInteral = WarnInteral;
    }

    /**
     * 获取告警接口实例
     *
     * @return WarnImpl
     */
    public WarnImpl getWarnObject() {
        String classname = this.getWarnClass();
        Class cl = null;
        if (classname != null && (!classname.isEmpty())) {
            try {
                cl = ClassManager.loadclass(classname);
                if (!WarnImpl.class.isAssignableFrom(cl)) {
                    LogFactory.warn("告警类‘{0}’没有实现WarnImpl接口，将尝试全局告警接口！", new Object[]{cl.getName()}, LogProperties.class);
                    cl = null;
                }
            } catch (ClassNotFoundException ex) {
            }
        }
        if (cl == null && (!this.logName.equalsIgnoreCase(LogFactory.SYSTEM_LOGNAME))) {//查找SystemLogger的报警实例
            WarnImpl wi = LogFactory.getLogger(LogFactory.SYSTEM_LOGNAME).getProperties().getWarnObject();
            if (wi != null) {
                return wi;
            }
        }
        if (cl == null) {
            return null;
        }
        try {
            return (WarnImpl) cl.newInstance();
        } catch (Exception e) {
            LogFactory.error("实例化告警类‘{0}’失败：", new Object[]{cl.getName(), e}, LogProperties.class);
            return null;
        }
    }

    /**
     * @param WarnClass the WarnClass to set
     */
    public synchronized void setWarnClass(String WarnClass) {
        this.WarnClass = WarnClass == null ? null : WarnClass.trim();
    }

    /**
     * 获取配置
     *
     * @return Properties
     */
    public Properties getProperties() {
        return params.getProperties();
    }

    /**
     * 获取配置
     *
     * @return Config
     */
    public Config getConfig() {
        return params.getConfig();
    }

    /**
     * @return the params
     */
    public String getParams() {
        return params.getString();
    }

    /**
     * @param params the params to set
     */
    public synchronized void setParams(String params) {
        this.params.parse(params);
    }

    /**
     * @return the WarnClass
     */
    public String getWarnClass() {
        return WarnClass;
    }
}
