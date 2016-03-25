package com.autumn.console;

import com.autumn.core.SystemInfo;
import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;


/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 察看线程信息</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class SystemThreads extends Action {

    private String[] rows = null;

    @Override
    public Forward execute() {
        String ss = SystemInfo.getThreadInfo();
        rows = ss.split("\r\n");
        setAttribute("menwatch", this);
        return new ActionForward("threadwatch.jsp");
    }

    /**
     * @return the rows
     */
    public String[] getRows() {
        return rows;
    }
}
