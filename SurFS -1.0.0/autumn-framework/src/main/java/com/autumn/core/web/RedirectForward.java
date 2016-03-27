/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: WEB框架-输出器</p>
 *
 * <p>Description: 重定向输出器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class RedirectForward implements Forward {

    private String redirectUrl = null;//重定向地址

    public RedirectForward(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public void doForward(Action action) throws Throwable {
        String sendurl = redirectUrl;
        String rurl = redirectUrl.toLowerCase();
        if (rurl.startsWith("http://") || rurl.startsWith("https://")) {
            action.getResponse().sendRedirect(action.getResponse().encodeRedirectURL(sendurl));
            return;
        }
        HttpServletRequest request = action.getRequest();
        HttpServletResponse response = action.getResponse();
        if (!sendurl.startsWith("/")) {
            String ss = request.getServletPath().substring(0, request.getServletPath().lastIndexOf("/") + 1);
            sendurl = ss + sendurl;
        }
        if (!"".equals(request.getContextPath())) {
            sendurl = request.getContextPath() + sendurl;
        }
        response.sendRedirect(sendurl);
    }
}
