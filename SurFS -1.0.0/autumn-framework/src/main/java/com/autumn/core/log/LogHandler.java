package com.autumn.core.log;

/**
 * <p>Title: 日志组件</p>
 *
 * <p>Description: 日志输出接口</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public interface LogHandler {

    /**
     * fatal
     *
     * @param strLine 日志内容
     * @param classname 类名
     */
    public void fatal(String strLine, String classname);

    /**
     * fatal
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param classname 类名
     */
    public void fatal(String pattern, Object[] args, String classname);

    /**
     * fatal
     *
     * @param strLine 日志内容
     * @param callclass 类
     */
    public void fatal(String strLine, Class callclass);

    /**
     * fatal
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    public void fatal(String pattern, Object[] args, Class callclass);

    /**
     * fatal
     *
     * @param strLine 日志内容
     */
    public void fatal(String strLine);

    /**
     * fatal
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    public void fatal(String pattern, Object[] args);

    /**
     * error
     *
     * @param strLine 日志内容
     * @param classname 类名
     */
    public void error(String strLine, String classname);

    /**
     * error
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param classname 类名
     */
    public void error(String pattern, Object[] args, String classname);

    /**
     * error
     *
     * @param strLine 日志内容
     * @param callclass 类
     */
    public void error(String strLine, Class callclass);

    /**
     * error
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    public void error(String pattern, Object[] args, Class callclass);

    /**
     * error
     *
     * @param strLine 日志内容
     */
    public void error(String strLine);

    /**
     * error
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    public void error(String pattern, Object[] args);

    /**
     * warn
     *
     * @param strLine 日志内容
     * @param classname 类名
     */
    public void warn(String strLine, String classname);

    /**
     * warn
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param classname 类名
     */
    public void warn(String pattern, Object[] args, String classname);

    /**
     * warn
     *
     * @param strLine 日志内容
     * @param callclass 类
     */
    public void warn(String strLine, Class callclass);

    /**
     * warn
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    public void warn(String pattern, Object[] args, Class callclass);

    /**
     * warn
     *
     * @param strLine 日志内容
     */
    public void warn(String strLine);

    /**
     * warn
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    public void warn(String pattern, Object[] args);

    /**
     * info
     *
     * @param strLine 日志内容
     * @param classname 类名
     */
    public void info(String strLine, String classname);

    /**
     * info
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param classname 类名
     */
    public void info(String pattern, Object[] args, String classname);

    /**
     * info
     *
     * @param strLine 日志内容
     * @param callclass 类
     */
    public void info(String strLine, Class callclass);

    /**
     * info
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    public void info(String pattern, Object[] args, Class callclass);

    /**
     * info
     *
     * @param strLine 日志内容
     */
    public void info(String strLine);

    /**
     * info
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    public void info(String pattern, Object[] args);

    /**
     * debug
     *
     * @param strLine 日志内容
     * @param classname 类名
     */
    public void debug(String strLine, String classname);

    /**
     * debug
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param classname 类名
     */
    public void debug(String pattern, Object[] args, String classname);

    /**
     * debug
     *
     * @param strLine 日志内容
     * @param callclass 类
     */
    public void debug(String strLine, Class callclass);

    /**
     * debug
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    public void debug(String pattern, Object[] args, Class callclass);

    /**
     * debug
     *
     * @param strLine 日志内容
     */
    public void debug(String strLine);

    /**
     * debug
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    public void debug(String pattern, Object[] args);

    /**
     * trace
     *
     * @param strLine 日志内容
     * @param t 错误实例
     * @param classname 类名
     */
    public void trace(String strLine, Throwable t, String classname);

    /**
     * trace
     *
     * @param strLine 日志内容
     * @param t 错误实例
     * @param callclass 类
     */
    public void trace(String strLine, Throwable t, Class callclass);

    /**
     * trace
     *
     * @param strLine 日志内容
     * @param t 错误实例
     */
    public void trace(String strLine, Throwable t);

    /**
     * log
     *
     * @param logLevel 日志输出级别
     * @param strLine 日志内容
     * @param callclass 类
     */
    public void log(Level logLevel, String strLine, Class callclass);

    /**
     * log
     *
     * @param logLevel 日志输出级别
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    public void log(Level logLevel, String pattern, Object[] args, Class callclass);

    /**
     * log
     *
     * @param logLevel 日志输出级别
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类名
     */
    public void log(Level logLevel, String pattern, Object[] args, String callclass);

    /**
     * log
     *
     * @param logLevel 日志输出级别
     * @param strLine 日志内容
     * @param callclass 类名
     */
    public void log(Level logLevel, String strLine, String callclass);

    /**
     * log
     *
     * @param logLevel 日志输出级别
     * @param strLine 日志内容
     */
    public void log(Level logLevel, String strLine);

    /**
     * log
     *
     * @param logLevel 日志输出级别
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    public void log(Level logLevel, String pattern, Object[] args);
}
