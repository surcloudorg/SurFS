/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.examples;

import com.autumn.core.SystemAttributes;
import com.autumn.core.log.LogFactory;
import com.autumn.core.service.ServiceImpl;
import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import java.util.Date;

/**
 * <p>Title: 测试系统变量存储</p>
 *
 * <p>Description: 可以实现ConfigListener接口用来监听Config中的参数变化</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SystemAttTest extends Action implements ServiceImpl {

    @Override
    public ActionForward execute() {
        Object obj = SystemAttributes.getAttribute(SystemAttTest.class.getName());
        if (obj == null) {
            obj = new Demo("张三", 22, false, "", new Date(), "");
            SystemAttributes.setAttribute(SystemAttTest.class.getName(), obj);
        }
        this.setAttribute("demo", obj);
        this.getResponse().setHeader("test", "sssssssssssssssssssssssssssss");
        return new ActionForward("input.jsp");
    }

    @Override
    public void start() throws Exception {
        try {
            Object obj = SystemAttributes.loadAttribute(SystemAttTest.class.getName());
            if (obj == null) {
                Demo demo = new Demo("张三", 22, false, "", new Date(), "");
                SystemAttributes.setAttribute(SystemAttTest.class.getName(), demo);
            }
        } catch (Exception e) {
            LogFactory.error("加载错误:" + e.getMessage(), SystemAttTest.class);
        }
    }

    @Override
    public void stop() {
        try {
            SystemAttributes.saveAttribute(SystemAttTest.class.getName());
        } catch (Exception e) {
            LogFactory.error("存储错误:" + e.getMessage(), SystemAttTest.class);
        }
    }
}
