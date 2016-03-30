/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;

import java.util.Date;


public class WarnCommand {

    private Logger log = null;
    private String message = null;
    private String classname = null;
    private Date warnTime = new Date();

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
