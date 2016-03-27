/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core;

import com.autumn.util.MainArgs;
import java.io.File;
import java.lang.management.ManagementFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.management.MBeanContainer;

/**
 * <p>
 * Title: 启动JETTY</p>
 *
 * <p>
 * Description: 启动JETTY服务</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class JettyServer {

    /**
     * 启动http服务
     *
     * @param port int 服务端口
     * @param webapp String 发布的web目录
     * @return Server 返回org.mortbay.jetty.Server
     * @throws Exception
     */
    public static Server start(int port, String webapp) throws Exception {
        System.setProperty("org.mortbay.jetty.Request.maxFormContentSize", "-1");
        long connectionnum = 2000;
        try {
            connectionnum = Long.parseLong(System.getProperty("lowResourcesConnections", "2000"));
        } catch (Exception r) {
        }
        server = new Server();
        //设置端口
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        connector.setLowResourceMaxIdleTime(30000);
        connector.setMaxIdleTime(30000);
        connector.setUseDirectBuffers(false);
        connector.setLowResourcesConnections(connectionnum);
        server.setConnectors(new Connector[]{connector});
        System.out.println("设置端口：" + port);
        String jmxport = System.getProperty("com.sun.management.jmxmpport");
        if (jmxport != null) {
            try {
                MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
                server.getContainer().addEventListener(mbContainer);
                mbContainer.start();
                System.out.println("启动JMX服务!");
            } catch (Throwable re) {
                re.printStackTrace();
            }
        }
        File f = new File((new File(webapp)).getAbsolutePath());
        String usepath = System.getProperty("user.dir");
        if (f.exists() && f.isDirectory()) {
            usepath = f.getAbsolutePath();
        } else {//获取webapp路径
            if (usepath == null) {
                usepath = "";
            }
            if (usepath.endsWith(File.separator)) {
                usepath = usepath + "main" + File.separator + webapp;
            } else {
                usepath = usepath + File.separator + File.separator + webapp;
            }
        }
        File webAppDir = new File(usepath);
        if (!webAppDir.exists()) {
            System.out.println("无效的webapp：[" + webAppDir.getCanonicalPath()  + "],框架无法启动！");
        } else {
            System.out.println("webapp：[" + webAppDir.getCanonicalPath() + "],最大连接数:[" + connectionnum + "],框架启动......");
        }
        System.setProperty("user.dir", webAppDir.getCanonicalFile().getParentFile().getCanonicalPath());
        System.setProperty("derby.system.home", webAppDir.getCanonicalFile().getParentFile().getCanonicalPath());
        //启动
        WebAppContext context = new WebAppContext(new ContextHandlerCollection(), webAppDir.getAbsolutePath(), "/");
        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{context, new DefaultHandler()});
        server.addHandler(handlers);
        server.start();
        return server;
    }

    /**
     * 终止http服务
     *
     * @param server Server
     * @throws Exception
     */
    public static void stop(Server server) throws Exception {
        server.stop();
        server.join();
    }
    public static Server server = null;

    /**
     * @param args String[] 添加参数port=8080,http服务端口
     */
    public static void main(String[] args) {
        try {
            MainArgs param = new MainArgs(args);
            //获取服务端口
            int port = param.getInt("port", 8080);
            String webapp = param.getString("webapp", "web");
            JVMController.start();
            System.out.println("webapp>>端口:" + port + "，web目录:" + webapp);
            server = start(port, webapp);
            JettyListener jl = new JettyListener(server);
            JVMController.registry(jl);

            //安全退出！关闭控制台时执行
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println("webapp>>系统执行退出...");
                        JettyServer.stop(server);
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
