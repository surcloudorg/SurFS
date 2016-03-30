/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;


public interface LogHandler {

    /**
     * fatal
     *
     * @param strLine 
     * @param classname 
     */
    public void fatal(String strLine, String classname);

    /**
     * fatal
     *
     * @param pattern 
     * @param args 
     * @param classname 
     */
    public void fatal(String pattern, Object[] args, String classname);

    /**
     * fatal
     *
     * @param strLine 
     * @param callclass 
     */
    public void fatal(String strLine, Class callclass);

    /**
     * fatal
     *
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    public void fatal(String pattern, Object[] args, Class callclass);

    /**
     * fatal
     *
     * @param strLine 
     */
    public void fatal(String strLine);

    /**
     * fatal
     *
     * @param pattern 
     * @param args 
     */
    public void fatal(String pattern, Object[] args);

    /**
     * error
     *
     * @param strLine 
     * @param classname 
     */
    public void error(String strLine, String classname);

    /**
     * error
     *
     * @param pattern 
     * @param args 
     * @param classname 
     */
    public void error(String pattern, Object[] args, String classname);

    /**
     * error
     *
     * @param strLine 
     * @param callclass 
     */
    public void error(String strLine, Class callclass);

    /**
     * error
     *
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    public void error(String pattern, Object[] args, Class callclass);

    /**
     * error
     *
     * @param strLine 
     */
    public void error(String strLine);

    /**
     * error
     *
     * @param pattern 
     * @param args 
     */
    public void error(String pattern, Object[] args);

    /**
     * warn
     *
     * @param strLine 
     * @param classname 
     */
    public void warn(String strLine, String classname);

    /**
     * warn
     *
     * @param pattern 
     * @param args 
     * @param classname 
     */
    public void warn(String pattern, Object[] args, String classname);

    /**
     * warn
     *
     * @param strLine 
     * @param callclass 
     */
    public void warn(String strLine, Class callclass);

    /**
     * warn
     *
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    public void warn(String pattern, Object[] args, Class callclass);

    /**
     * warn
     *
     * @param strLine 
     */
    public void warn(String strLine);

    /**
     * warn
     *
     * @param pattern 
     * @param args 
     */
    public void warn(String pattern, Object[] args);

    /**
     * info
     *
     * @param strLine 
     * @param classname 
     */
    public void info(String strLine, String classname);

    /**
     * info
     *
     * @param pattern 
     * @param args 
     * @param classname 
     */
    public void info(String pattern, Object[] args, String classname);

    /**
     * info
     *
     * @param strLine 
     * @param callclass 
     */
    public void info(String strLine, Class callclass);

    /**
     * info
     *
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    public void info(String pattern, Object[] args, Class callclass);

    /**
     * info
     *
     * @param strLine 
     */
    public void info(String strLine);

    /**
     * info
     *
     * @param pattern 
     * @param args 
     */
    public void info(String pattern, Object[] args);

    /**
     * debug
     *
     * @param strLine 
     * @param classname 
     */
    public void debug(String strLine, String classname);

    /**
     * debug
     *
     * @param pattern 
     * @param args 
     * @param classname 
     */
    public void debug(String pattern, Object[] args, String classname);

    /**
     * debug
     *
     * @param strLine 
     * @param callclass 
     */
    public void debug(String strLine, Class callclass);

    /**
     * debug
     *
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    public void debug(String pattern, Object[] args, Class callclass);

    /**
     * debug
     *
     * @param strLine 
     */
    public void debug(String strLine);

    /**
     * debug
     *
     * @param pattern 
     * @param args 
     */
    public void debug(String pattern, Object[] args);

    /**
     * trace
     *
     * @param strLine 
     * @param t 
     * @param classname 
     */
    public void trace(String strLine, Throwable t, String classname);

    /**
     * trace
     *
     * @param strLine 
     * @param t 
     * @param callclass 
     */
    public void trace(String strLine, Throwable t, Class callclass);

    /**
     * trace
     *
     * @param strLine 
     * @param t 
     */
    public void trace(String strLine, Throwable t);

    /**
     * log
     *
     * @param logLevel 
     * @param strLine 
     * @param callclass 
     */
    public void log(Level logLevel, String strLine, Class callclass);

    /**
     * log
     *
     * @param logLevel 
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    public void log(Level logLevel, String pattern, Object[] args, Class callclass);

    /**
     * log
     *
     * @param logLevel 
     * @param pattern 
     * @param args 
     * @param callclass 
     */
    public void log(Level logLevel, String pattern, Object[] args, String callclass);

    /**
     * log
     *
     * @param logLevel 
     * @param strLine 
     * @param callclass 
     */
    public void log(Level logLevel, String strLine, String callclass);

    /**
     * log
     *
     * @param logLevel 
     * @param strLine 
     */
    public void log(Level logLevel, String strLine);

    /**
     * log
     *
     * @param logLevel 
     * @param pattern 
     * @param args 
     */
    public void log(Level logLevel, String pattern, Object[] args);
}
