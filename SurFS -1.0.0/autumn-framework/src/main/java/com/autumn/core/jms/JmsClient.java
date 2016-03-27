/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.jms;

import java.lang.reflect.Proxy;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.http.CommonsHttpMessageSender;

/**
 * <p>Title: JMS客户端</p>
 *
 * <p>Description: JMS客户端</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class JmsClient {

    private static final HttpClientParams clientParams = new HttpClientParams();
    private static final int maxConnectionsPerHost = 30;
    private static final int maxTotalConnections = 300;

    static {
        int soTimeout = 60000;
        try {
            soTimeout = Integer.parseInt(System.getProperty("sun.net.client.defaultReadTimeout", "60000"));
        } catch (Exception e) {
        }
        clientParams.setParameter(HttpClientParams.USE_EXPECT_CONTINUE, Boolean.FALSE);
        clientParams.setConnectionManagerTimeout(soTimeout);
        clientParams.setSoTimeout(soTimeout);
        clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
    }
    protected String serviceURL;
    protected String username;
    protected String password;
    protected Object proxy = null;

    public JmsClient(String serviceURL, Class cls) throws Exception {
        this(serviceURL, null, null, cls);
    }

    /**
     * 创建
     *
     * @param serviceURL 服务地址
     * @param username basic认证账号
     * @param password 密码
     * @param cls 接口类
     * @throws Exception
     */
    public JmsClient(String serviceURL, String username, String password, Class cls) throws Exception {
        this.serviceURL = serviceURL;
        this.username = username;
        this.password = password;
        Service serviceModel = new ObjectServiceFactory().create(cls);
        XFireProxyFactory serviceFactory = new XFireProxyFactory();
        proxy = serviceFactory.create(serviceModel, serviceURL);
        Client client = ((XFireProxy) Proxy.getInvocationHandler(proxy)).getClient();
        client.setProperty(CommonsHttpMessageSender.HTTP_CLIENT_PARAMS, clientParams);
        client.setProperty(CommonsHttpMessageSender.MAX_CONN_PER_HOST, String.valueOf(maxConnectionsPerHost));
        client.setProperty(CommonsHttpMessageSender.MAX_TOTAL_CONNECTIONS, String.valueOf(maxTotalConnections));
        if (!(username == null || password == null)) {
            client.setProperty(Channel.USERNAME, username);
            client.setProperty(Channel.PASSWORD, password);
        }
    }
}
