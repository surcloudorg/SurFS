/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;

import java.text.MessageFormat;

public abstract class AbstractLogger implements LogHandler {


    protected String className = null;

    /**

     *
     * @param pattern 
     * @param args 
     * @param classname 
     */
    @Override
    public void fatal(String pattern, Object[] args, String classname) {
        fatal(MessageFormat.format(pattern, args), classname);
    }

    /**
     *
     * @param strLine 
     * @param callclass 
     */
    @Override
    public void fatal(String strLine, Class callclass) {
        fatal(strLine, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    @Override
    public void fatal(String pattern, Object[] args, Class callclass) {
        fatal(MessageFormat.format(pattern, args), callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param strLine 
     */
    @Override
    public void fatal(String strLine) {
        fatal(strLine, className);
    }

    /**
     * 
     *
     * @param pattern 
     * @param args 
     */
    @Override
    public void fatal(String pattern, Object[] args) {
        fatal(MessageFormat.format(pattern, args), className);
    }

    /**
     * 
     *
     * @param strLine 
     * @param classname 
     */
    @Override
    public void error(String strLine, String classname) {
        error(strLine, null, classname);
    }

    /**
     * 
     *
     * @param strLine 
     * @param callclass 
     */
    @Override
    public void error(String strLine, Class callclass) {
        error(strLine, null, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    @Override
    public void error(String pattern, Object[] args, Class callclass) {
        error(pattern, args, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param strLine 
     */
    @Override
    public void error(String strLine) {
        error(strLine, null, className);
    }

    /**
     * 
     *
     * @param pattern 
     * @param args 
     */
    @Override
    public void error(String pattern, Object[] args) {
        error(pattern, args, className);
    }

    /**
     * 
     *
     * @param strLine 
     * @param classname 
     */
    @Override
    public void warn(String strLine, String classname) {
        error(strLine, null, classname);
    }

    /**
     * 
     *
     * @param strLine 
     * @param callclass 
     */
    @Override
    public void warn(String strLine, Class callclass) {
        warn(strLine, null, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    @Override
    public void warn(String pattern, Object[] args, Class callclass) {
        warn(pattern, args, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param strLine 
     */
    @Override
    public void warn(String strLine) {
        warn(strLine, null, className);
    }

    /**
     * 
     *
     * @param pattern 
     * @param args 
     */
    @Override
    public void warn(String pattern, Object[] args) {
        warn(pattern, args, className);
    }

    /**
     * 
     *
     * @param strLine 
     * @param classname 
     */
    @Override
    public void info(String strLine, String classname) {
        info(strLine, null, classname);
    }

    /**
     * 
     *
     * @param strLine 
     * @param callclass 
     */
    @Override
    public void info(String strLine, Class callclass) {
        info(strLine, null, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    @Override
    public void info(String pattern, Object[] args, Class callclass) {
        info(pattern, args, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param strLine 
     */
    @Override
    public void info(String strLine) {
        info(strLine, null, className);
    }

    /**
     * 
     *
     * @param pattern 
     * @param args 
     */
    @Override
    public void info(String pattern, Object[] args) {
        info(pattern, args, className);
    }

    /**
     * 
     *
     * @param strLine 
     * @param classname 
     */
    @Override
    public void debug(String strLine, String classname) {
        debug(strLine, null, classname);
    }

    /**
     * 
     *
     * @param strLine 
     * @param callclass 
     */
    @Override
    public void debug(String strLine, Class callclass) {
        debug(strLine, null, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    @Override
    public void debug(String pattern, Object[] args, Class callclass) {
        debug(pattern, args, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param strLine 
     */
    @Override
    public void debug(String strLine) {
        debug(strLine, null, className);
    }

    /**
     * 
     *
     * @param pattern 
     * @param args 
     */
    @Override
    public void debug(String pattern, Object[] args) {
        debug(pattern, args, className);
    }

    /**
     * 
     *
     * @param strLine 
     * @param t 
     * @param callclass 
     */
    @Override
    public void trace(String strLine, Throwable t, Class callclass) {
        trace(strLine, t, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param strLine 
     * @param t 
     */
    @Override
    public void trace(String strLine, Throwable t) {
        trace(strLine, t, className);
    }

    /**
     * 
     *
     * @param logLevel 
     * @param strLine 
     * @param callclass 
     */
    @Override
    public void log(Level logLevel, String strLine, Class callclass) {
        log(logLevel, strLine, null, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param logLevel 
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    @Override
    public void log(Level logLevel, String pattern, Object[] args, Class callclass) {
        log(logLevel, pattern, args, callclass == null ? null : callclass.getName());
    }

    /**
     * 
     *
     * @param logLevel 
     * @param strLine 
     * @param callclass 
     */
    @Override
    public void log(Level logLevel, String strLine, String callclass) {
        log(logLevel, strLine, null, callclass);
    }

    /**
     * 
     *
     * @param logLevel 
     * @param strLine 
     */
    @Override
    public void log(Level logLevel, String strLine) {
        log(logLevel, strLine, className);
    }

    /**
     * 
     *
     * @param logLevel 
     * @param pattern 
     * @param args 
     */
    @Override
    public void log(Level logLevel, String pattern, Object[] args) {
        log(logLevel, pattern, args, className);
    }
}
