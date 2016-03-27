/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.log;

import com.autumn.util.TextUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <p>Title: 写日志文件</p>
 *
 * <p>Description: 日志组件的写文件实现代码</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Writer {

    private String logPath = null;
    private File logFile = null;
    private FileWriter logWriter = null;
    private long lastwarntime = 0;

    protected Writer(String logPath) {
        this.logPath = logPath;
    }

    /**
     * 写日志
     *
     * @param str String
     */
    protected void write(String str) {
        if (logPath == null) {
            return;
        }
        synchronized (this) {
            CheckLogFile();
            if (logWriter != null) {
                try {
                    logWriter.write(str);
                    logWriter.flush();
                } catch (IOException ioe) {
                    try {
                        logWriter.close();
                    } catch (IOException ex) {
                    }
                    logWriter = null;
                    logFile = null;
                }
            }
        }
    }

    /**
     * 检查是否跨天
     */
    private void CheckLogFile() {
        Date now = new Date();
        String strFileName = logPath.concat(TextUtils.Date2String(now, "yyyyMMdd")).concat(".log");
        if (logFile == null) {
            logWriter = null;
            logFile = new File(strFileName);
            try {
                if (logFile.exists()) {
                    GregorianCalendar logCalendar, fileCalendar;
                    logCalendar = new GregorianCalendar();
                    logCalendar.setTime(now);
                    fileCalendar = new GregorianCalendar();
                    fileCalendar.setTime(new Date(logFile.lastModified()));
                    if (logCalendar.get(Calendar.YEAR) == fileCalendar.get(Calendar.YEAR)) {
                        logWriter = new FileWriter(strFileName, true);
                    } else {
                        logWriter = new FileWriter(logFile);
                    }
                } else {
                    logFile.createNewFile();
                    logWriter = new FileWriter(logFile);
                }
            } catch (IOException ioe) {
                logWriter = null;
                logFile = null;
            }
        } else {
            if (!strFileName.equalsIgnoreCase(logFile.toString())) {
                logFile = new File(strFileName);
                try {
                    if (logWriter != null) {
                        logWriter.close();
                    }
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }
                    logWriter = new FileWriter(logFile);
                } catch (IOException ioe) {
                    logWriter = null;
                    logFile = null;
                }
            }
        }
    }

    /**
     * @return the lastwarntime
     */
    protected long getLastwarntime() {
        return lastwarntime;
    }

    /**
     * @param lastwarntime the lastwarntime to set
     */
    protected void setLastwarntime(long lastwarntime) {
        this.lastwarntime = lastwarntime;
    }
}
