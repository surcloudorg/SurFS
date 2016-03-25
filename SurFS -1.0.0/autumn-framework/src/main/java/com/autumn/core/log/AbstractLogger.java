package com.autumn.core.log;

import java.text.MessageFormat;

/**
 * <p>Title: 日志组件</p>
 *
 * <p>Description: 日志输出</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public abstract class AbstractLogger implements LogHandler {

    /**
     * 输出的类名
     */
    protected String className = null;

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param classname 类名
     */
    @Override
    public void fatal(String pattern, Object[] args, String classname) {
        fatal(MessageFormat.format(pattern, args), classname);
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     * @param callclass 类
     */
    @Override
    public void fatal(String strLine, Class callclass) {
        fatal(strLine, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    @Override
    public void fatal(String pattern, Object[] args, Class callclass) {
        fatal(MessageFormat.format(pattern, args), callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     */
    @Override
    public void fatal(String strLine) {
        fatal(strLine, className);
    }

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    @Override
    public void fatal(String pattern, Object[] args) {
        fatal(MessageFormat.format(pattern, args), className);
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     * @param classname 类名
     */
    @Override
    public void error(String strLine, String classname) {
        error(strLine, null, classname);
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     * @param callclass 类
     */
    @Override
    public void error(String strLine, Class callclass) {
        error(strLine, null, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    @Override
    public void error(String pattern, Object[] args, Class callclass) {
        error(pattern, args, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     */
    @Override
    public void error(String strLine) {
        error(strLine, null, className);
    }

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    @Override
    public void error(String pattern, Object[] args) {
        error(pattern, args, className);
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     * @param classname 类名
     */
    @Override
    public void warn(String strLine, String classname) {
        error(strLine, null, classname);
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     * @param callclass 类
     */
    @Override
    public void warn(String strLine, Class callclass) {
        warn(strLine, null, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    @Override
    public void warn(String pattern, Object[] args, Class callclass) {
        warn(pattern, args, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     */
    @Override
    public void warn(String strLine) {
        warn(strLine, null, className);
    }

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    @Override
    public void warn(String pattern, Object[] args) {
        warn(pattern, args, className);
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     * @param classname 类名
     */
    @Override
    public void info(String strLine, String classname) {
        info(strLine, null, classname);
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     * @param callclass 类
     */
    @Override
    public void info(String strLine, Class callclass) {
        info(strLine, null, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    @Override
    public void info(String pattern, Object[] args, Class callclass) {
        info(pattern, args, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     */
    @Override
    public void info(String strLine) {
        info(strLine, null, className);
    }

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    @Override
    public void info(String pattern, Object[] args) {
        info(pattern, args, className);
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     * @param classname 类名
     */
    @Override
    public void debug(String strLine, String classname) {
        debug(strLine, null, classname);
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     * @param callclass 类
     */
    @Override
    public void debug(String strLine, Class callclass) {
        debug(strLine, null, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    @Override
    public void debug(String pattern, Object[] args, Class callclass) {
        debug(pattern, args, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param strLine 日志内容
     */
    @Override
    public void debug(String strLine) {
        debug(strLine, null, className);
    }

    /**
     * 输出日志
     *
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    @Override
    public void debug(String pattern, Object[] args) {
        debug(pattern, args, className);
    }

    /**
     * 输出错误
     *
     * @param strLine 日志内容
     * @param t 错误实例
     * @param callclass 类
     */
    @Override
    public void trace(String strLine, Throwable t, Class callclass) {
        trace(strLine, t, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出错误
     *
     * @param strLine 日志内容
     * @param t 错误实例
     */
    @Override
    public void trace(String strLine, Throwable t) {
        trace(strLine, t, className);
    }

    /**
     * 输出日志
     *
     * @param logLevel 日志输出级别
     * @param strLine 日志内容
     * @param callclass 类
     */
    @Override
    public void log(Level logLevel, String strLine, Class callclass) {
        log(logLevel, strLine, null, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param logLevel 日志输出级别
     * @param pattern 格式化字符串
     * @param args 格式化参数
     * @param callclass 类
     */
    @Override
    public void log(Level logLevel, String pattern, Object[] args, Class callclass) {
        log(logLevel, pattern, args, callclass == null ? null : callclass.getName());
    }

    /**
     * 输出日志
     *
     * @param logLevel 日志输出级别
     * @param strLine 日志内容
     * @param callclass 类名
     */
    @Override
    public void log(Level logLevel, String strLine, String callclass) {
        log(logLevel, strLine, null, callclass);
    }

    /**
     * 输出日志
     *
     * @param logLevel 日志输出级别
     * @param strLine 日志内容
     */
    @Override
    public void log(Level logLevel, String strLine) {
        log(logLevel, strLine, className);
    }

    /**
     * 输出日志
     *
     * @param logLevel 日志输出级别
     * @param pattern 格式化字符串
     * @param args 格式化参数
     */
    @Override
    public void log(Level logLevel, String pattern, Object[] args) {
        log(logLevel, pattern, args, className);
    }
}
