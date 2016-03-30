/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;

import com.surfs.nas.util.TextUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Writer {

    private String logPath = null;
    private File logFile = null;
    private FileWriter logWriter = null;
    private long lastwarntime = 0;

    protected Writer(String logPath) {
        this.logPath = logPath;
    }


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
