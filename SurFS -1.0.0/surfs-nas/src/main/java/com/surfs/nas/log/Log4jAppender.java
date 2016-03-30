/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class Log4jAppender implements Appender {

    protected Log4jAppender(Logger log) {
        this.log = log;
    }
    private Logger log = null;

    /**
     * addFilter
     *
     * @param filter Filter
     */
    @Override
    public void addFilter(Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * clearFilters
     */
    @Override
    public void clearFilters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * close
     */
    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * doAppend
     *
     * @param loggingEvent LoggingEvent
     */
    @Override
    public void doAppend(LoggingEvent loggingEvent) {
        if (Logger.isWrap()) {
            return;
        }
        Logger mylog = log;
        if (mylog == null) {
            mylog = LogFactory.getLogger();
        }
        if (loggingEvent.getLoggerName().equalsIgnoreCase("org.mortbay.log")) {
            return;
        }
        org.apache.log4j.Level level = loggingEvent.getLevel();
        if (org.apache.log4j.Level.DEBUG_INT == level.toInt()) {
            mylog.debug(loggingEvent.getMessage().toString(), loggingEvent.getLoggerName());
        } else if (org.apache.log4j.Level.WARN_INT == level.toInt()) {
            mylog.warn(loggingEvent.getMessage().toString(), loggingEvent.getLoggerName());
        } else if (org.apache.log4j.Level.ERROR_INT == level.toInt()) {
            mylog.error(loggingEvent.getMessage().toString(), loggingEvent.getLoggerName());
        } else if (org.apache.log4j.Level.FATAL_INT == level.toInt()) {
            mylog.fatal(loggingEvent.getMessage().toString(), loggingEvent.getLoggerName());
        } else {
            mylog.info(loggingEvent.getMessage().toString(), loggingEvent.getLoggerName());
        }
    }

    /**
     * getErrorHandler
     *
     * @return ErrorHandler
     */
    @Override
    public ErrorHandler getErrorHandler() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * getFilter
     *
     * @return Filter
     */
    @Override
    public Filter getFilter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * getLayout
     *
     * @return Layout
     */
    @Override
    public Layout getLayout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * getName
     *
     * @return String
     */
    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * requiresLayout
     *
     * @return boolean
     */
    @Override
    public boolean requiresLayout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * setErrorHandler
     *
     * @param errorHandler ErrorHandler
     */
    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * setLayout
     *
     * @param layout Layout
     */
    @Override
    public void setLayout(Layout layout) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * setName
     *
     * @param string String
     */
    @Override
    public void setName(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }
}
