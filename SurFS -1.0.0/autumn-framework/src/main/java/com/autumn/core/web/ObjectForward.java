/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.web;

import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: WEB框架-输出器</p>
 *
 * <p>Description: 输出字节流</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ObjectForward implements Forward {
    
    private Object object = null;
    private String contentType = null;
    
    public ObjectForward(Object obj, String contentType) {
        this.object = obj;
        this.contentType = contentType;
    }
    
    @Override
    public void doForward(Action action) throws Throwable {
        HttpServletResponse response = action.getResponse();
        if (contentType != null && (!contentType.isEmpty())) {
            response.setContentType(contentType);
        }//response.setHeader("Content-Length", String.valueOf(((byte[]) content).length));
        OutputStream os = response.getOutputStream();
        if (object instanceof byte[]) {
            os.write((byte[]) object);
        } else {
            byte[] bs = object.toString().getBytes(response.getCharacterEncoding());
            os.write(bs);
        }
        os.flush();
        os.close();
    }
}
