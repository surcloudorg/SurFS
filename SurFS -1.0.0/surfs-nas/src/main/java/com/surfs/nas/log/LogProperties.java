/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;

import com.surfs.nas.util.Config;
import com.surfs.nas.util.ConfigParser;
import com.surfs.nas.util.TextUtils;
import java.util.Properties;
import java.util.regex.Pattern;


public class LogProperties {

    private boolean outConsole = true; 
    private String Dateformatter = "[MM-dd HH:mm:ss]";
    private boolean AddClassName = true; 
    private boolean AddLevel = true; 
    private Level level = Level.DEBUG; 
    private String logName = ""; 
    private Pattern filter = null;
    private int WarnInteral = 1000 * 60 * 10;
    private String WarnClass = null;
    private final ConfigParser params = new ConfigParser();

    /**
     *
     * @param logName
     * @throws Exception
     */
    public LogProperties(String logName) throws Exception {
        if (!TextUtils.isValidFileName(logName)) {
            throw new Exception("");
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
     *
     * @return WarnImpl
     */
    public WarnImpl getWarnObject() {
        return null;
    }

    /**
     * @param WarnClass the WarnClass to set
     */
    public synchronized void setWarnClass(String WarnClass) {
        this.WarnClass = WarnClass == null ? null : WarnClass.trim();
    }

    /**

     *
     * @return Properties
     */
    public Properties getProperties() {
        return params.getProperties();
    }

    /**

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
