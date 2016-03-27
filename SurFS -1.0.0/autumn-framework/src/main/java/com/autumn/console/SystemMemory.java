/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.console;

import com.autumn.core.SystemInfo;
import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;


/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 察看内存</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class SystemMemory extends Action {

    private String clear = null;
    private String doMsg = null; //执行结果
    private String[] rows = null;

    @Override
    public Forward execute() {
        if (clear != null&&this.getAccessPermission()>1) {
            System.gc();
            doMsg = "发送内存回收请求！";
            warn(doMsg);
        }
        try {
            String setlog = SystemInfo.getSimpleProperties() + "\r\n";
            setlog = setlog + SystemInfo.getDiskInfo() + "\r\n";
            setlog = setlog + SystemInfo.getMemInfo();
            rows = setlog.split("\r\n");
        } catch (Exception r) {
        }
        setAttribute("menwatch", this);
        return new ActionForward("menwatch.jsp");
    }

    /**
     * 操作结果
     *
     * @return String
     */
    public String getDoMsg() {
        return doMsg;
    }

    /**
     * 内存回收指令
     *
     * @param clear
     */
    public void setClear(String clear) {
        this.clear = clear;
    }

    /**
     * @return the rows
     */
    public String[] getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(String[] rows) {
        this.rows = rows;
    }
}
