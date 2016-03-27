/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: WEB框架-输出器</p>
 *
 * <p>Description: 前转输出器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ActionForward implements Forward {

    private String url = "";//jsp页

    /**
     * 前转到jsp
     *
     * @param url String
     */
    public ActionForward(String url) {
        this.url = url;
    }

    /**
     * 前转
     *
     * @param action
     * @throws Exception
     */
    @Override
    public void doForward(Action action) throws Throwable {
        String sendurl = url;
        HttpServletRequest request = action.getRequest();
        HttpServletResponse response = action.getResponse();
        if (sendurl.endsWith(".jsp")) {
            String sevlet = sendurl;
            int index = sendurl.lastIndexOf("/");
            if (index >= 0) {
                sevlet = sendurl.substring(index + 1);
            }
            CompiledJspFilter jsm=CompiledJspFilter.getMap(sevlet);
            if (jsm!=null) {
                String ss;
                if (!sendurl.startsWith("/")) {
                    ss = request.getServletPath().substring(0, request.getServletPath().lastIndexOf("/") + 1);
                    ss = ss + sendurl;
                    ss = request.getSession(true).getServletContext().getRealPath(ss);//查找绝对路径
                } else {
                    ss = request.getSession(true).getServletContext().getRealPath(sendurl);
                }
                if (!CompiledJspFilter.findSource(ss)) {
                    jsm.getJspbase()._jspService(request, response);
                    return;
                }
            }
        }
        RequestDispatcher rd = request.getRequestDispatcher(sendurl);
        rd.forward(request, response);
    }
}
