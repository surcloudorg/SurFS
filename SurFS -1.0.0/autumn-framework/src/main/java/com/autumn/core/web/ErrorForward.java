package com.autumn.core.web;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>Title: WEB框架-输出器</p>
 *
 * <p>Description: 错误输出器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ErrorForward implements Forward {

    private int errorNumber = 0;//输出错误码
    private String content = null;

    public ErrorForward(int errorCode) {
        this.errorNumber = errorCode;
    }

    public ErrorForward(int errorCode, String content) {
        this.errorNumber = errorCode;
        this.content = content;
    }

    @Override
    public void doForward(Action action) throws Throwable {
        if (content != null) {
            PrintWriter pw = action.getResponse().getWriter();
            pw.print(content);
            pw.flush();
            pw.close();
        }
        try {
            action.getResponse().sendError(errorNumber);
        } catch (IOException ex) {
        }
    }
}
