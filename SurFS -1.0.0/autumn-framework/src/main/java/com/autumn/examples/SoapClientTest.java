/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.examples;

import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import java.lang.reflect.Proxy;
import java.net.URL;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.http.CommonsHttpMessageSender;

/**
 * <p>Title: SOAP服务客户端测试程序</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SoapClientTest extends Action {

    private static Demo get(String s) throws Exception {
        String serviceURL = "http://localhost:8080/services/demo";
        if (!"".equals(s)) {
            serviceURL = "http://localhost:8080/" + s + "/services/demo";
        }
        Service serviceModel = new ObjectServiceFactory().create(SoapDemo.class);
        XFireProxyFactory serviceFactory = new XFireProxyFactory();
        // 获取服务对象
        SoapDemo service = (SoapDemo) serviceFactory.create(serviceModel, serviceURL);
        // 获取客户端代理
        Client client = ((XFireProxy) Proxy.getInvocationHandler(service)).getClient();

        //添加basic认证
        client.setProperty(Channel.USERNAME, "soapuser");
        client.setProperty(Channel.PASSWORD, "111111");

        // 启动response压缩
        client.setProperty(CommonsHttpMessageSender.GZIP_RESPONSE_ENABLED,Boolean.TRUE);

        // 启动request压缩
        //client.setProperty(CommonsHttpMessageSender.GZIP_REQUEST_ENABLED,
        //                   Boolean.TRUE);


        // 同时启动response和request压缩
        //client.setProperty(CommonsHttpMessageSender.GZIP_ENABLED, Boolean.TRUE);

        // 忽略超时
        client.setProperty(CommonsHttpMessageSender.HTTP_TIMEOUT, "0");

        // 调用服务
        Demo[] data = service.getDemos();
        if (data == null || data.length == 0) {
            return null;
        } else {
            return data[0];
        }
    }

    /**
     * 动态调用
     *
     * @throws Exception
     */
    private static void dynamic() throws Exception {
        Client client = new Client(new URL("http://localhost/services/demo?wsdl"));
        client.setProperty(Channel.USERNAME, "soapuser");
        client.setProperty(Channel.PASSWORD, "111111");
        client.setProperty(CommonsHttpMessageSender.GZIP_ENABLED, Boolean.TRUE);
        Object[] results = client.invoke("getDemos", new Object[]{});
        System.out.println(results[0]);
    }

    @Override
    public ActionForward execute() {
        try {
           // javax.servlet.jsp.PageContext p;
            
            String path = this.getRequest().getSession(true).getServletContext().getContextPath();
            Demo demo = get(path);
            this.setAttribute("message", "调用接口services/demo成功！");
            this.setAttribute("demo", demo);
            return new ActionForward("result.jsp");
        } catch (Exception e) {
            this.setAttribute("message", "调用接口services/demo失败:" + e.getMessage());
            return new ActionForward("result.jsp");
        }
    }
}
