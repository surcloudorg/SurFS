/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.log;

import java.util.Date;

/**
 * <p>Title: 报警消息</p>
 *
 * <p>Description: 报警消息数据模型</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class WarnCommand {

    private Logger log = null;
    private String message = null;//消息
    private String classname = null;//类名
    private Date warnTime = new Date();//时间

    protected WarnCommand(Logger logName, String message, String classname, Date warnTime) {
        this.log = logName;
        this.message = message;
        this.classname = classname;
        this.warnTime = warnTime;
    }

    /**
     * @return the logName
     */
    public Logger getLog() {
        return log;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the warnTime
     */
    public Date getWarnTime() {
        return warnTime;
    }

    /**
     * @return the classname
     */
    public String getClassname() {
        return classname;
    }
}
