/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.examples;

import com.autumn.core.ThreadPools;
import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.ConfigListener;
import com.autumn.core.cfg.Method;
import com.autumn.core.cfg.Property;
import com.autumn.core.log.LogFactory;
import com.autumn.core.web.WebFactory;
import java.io.IOException;
import javax.servlet.*;

/**
 * <p>Title: web过滤器演示</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class WebFilterTest implements ConfigListener, Filter {

    ThreadTest t = null;

    public WebFilterTest() {
        LogFactory.warn("WebFilterTest被创建！", WebFilterTest.class);
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
        LogFactory.warn("WebFilterTest被初始化！", WebFilterTest.class);
        t = new ThreadTest();
        t.start();
    }

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        LogFactory.warn("WebFilterTest执行doFilter！", WebFilterTest.class);
    }

    @Override
    public void destroy() {
        ThreadPools.stopThread(t);
        LogFactory.warn("WebFilterTest被销毁！", WebFilterTest.class);
    }

    @Override
    public Object callMethod(Method method) {
        String ss = "呼叫：" + method.getMethodName();
        LogFactory.info(ss, WebFilterTest.class);
        return ss;
    }

    @Override
    public boolean changeProperty(Property property) {
        Config cfg = WebFactory.getWebDirectory().getConfig();
        String info = "更改参数：" + property.getKey() + ",新值：" + property.getValue() + ",旧值：" + cfg.getAttributeValue(property.getKey());
        LogFactory.info(info, WebFilterTest.class);
        return true;//true(同意更改),false(拒绝更改)
    }
}
