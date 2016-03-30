/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class JdkLogHandler extends Handler {

    protected JdkLogHandler(Logger log) {
        this.log = log;
    }

    /**
     * Close the <tt>Handler</tt> and free all associated resources.
     *
     * @throws SecurityException if a security manager exists and if the caller
     * does not have <tt>LoggingPermission("control")</tt>.
     */
    @Override
    public void close() throws SecurityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Flush any buffered output.
     */
    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    /**
     * Publish a <tt>LogRecord</tt>.
     *
     * @param record description of the log event. A null record is silently
     * ignored and is not published
     * @todo Implement this java.util.logging.Handler method
     */
    private Logger log = null;

    @Override
    public void publish(LogRecord record) {
        if (Logger.isWrap()) {
            return;
        }
        Logger mylog = getLog();
        if (mylog == null) {
            mylog = LogFactory.getLogger();
        }
        if (record.getLoggerName().equalsIgnoreCase("org.mortbay.log")) {
            return;
        }
        java.util.logging.Level level = record.getLevel();
        if (level.equals(java.util.logging.Level.SEVERE)) {
            mylog.error(record.getMessage(), record.getParameters(), record.getLoggerName());
        } else if (level.equals(java.util.logging.Level.WARNING)) {
            mylog.warn(record.getMessage(), record.getParameters(), record.getLoggerName());
        } else if (level.equals(java.util.logging.Level.INFO)) {
            mylog.info(record.getMessage(), record.getParameters(), record.getLoggerName());
        } else if (level.equals(java.util.logging.Level.CONFIG)) {
            mylog.info(record.getMessage(), record.getParameters(), record.getLoggerName());
        } else if (level.equals(java.util.logging.Level.FINE)) {
            mylog.info(record.getMessage(), record.getParameters(), record.getLoggerName());
        } else {
            mylog.debug(record.getMessage(), record.getParameters(), record.getLoggerName());
        }
    }

    /**
     * @return the log
     */
    public Logger getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Logger log) {
        this.log = log;
    }
}
