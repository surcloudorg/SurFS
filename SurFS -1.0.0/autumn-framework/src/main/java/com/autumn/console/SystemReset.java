/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.console;

import com.autumn.core.ClassManager;
import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 重载</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class SystemReset extends Action {

    private String loadclass = null;
    private String loadclassmsg = "";

    @Override
    public Forward execute() {
        if (loadclass != null&&this.getAccessPermission()>1) {
            ClassManager.reloadclass();
            loadclassmsg = "重载class成功！";
            warn(loadclassmsg);
        }
        setAttribute("smsystem", this);
        return new ActionForward("system.jsp");
    }

    /**
     * @return the loadclass
     */
    public String getLoadclass() {
        return loadclass;
    }

    /**
     * @param loadclass the loadclass to set
     */
    public void setLoadclass(String loadclass) {
        this.loadclass = loadclass;
    }



    /**
     * @return the loadclassmsg
     */
    public String getLoadclassmsg() {
        return loadclassmsg;
    }

}
