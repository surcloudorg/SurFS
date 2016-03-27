/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

/**
 * <p>Title: WEB框架-输出器</p>
 *
 * <p>Description: json输出器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class PlainForward implements Forward {

    private String text = null;

    /**
     * 输出从 request.Attributes生成的json文档
     *
     * @return PlainForward
     */
    public static PlainForward responseJson() {
        HttpServletRequest request = ActionContext.getActionContext().getRequest();
        return new PlainForward(getJsonString(request));
    }

    public PlainForward(JSONObject json) {
        this.text = json.toString();
    }

    /**
     * 输出文本
     *
     * @param text
     */
    public PlainForward(String text) {
        this.text = text;
    }

    @Override
    public void doForward(Action action) throws Throwable {
        HttpServletResponse response = action.getResponse();
        action.getResponse().setContentType("text/plain; charset=".concat(action.getRequest().getCharacterEncoding()));
        PrintWriter out = response.getWriter();
        out.write(text);
        out.flush();
        out.close();
    }

    /**
     * 从 request.Attributes生成json字符串
     *
     * @param request
     */
    private static String getJsonString(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        Enumeration en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            Object value = request.getAttribute(key);
            json.put(key, value == null ? "" : value.toString());
        }
        return json.toString();
    }
}
