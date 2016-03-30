/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.server;

import java.io.File;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyServer {

    public static Server start(int port, String webapp) throws Exception {
        System.setProperty("org.mortbay.jetty.Request.maxFormContentSize", "-1");
        long connectionnum = 2000;
        try {
            connectionnum = Long.parseLong(System.getProperty("lowResourcesConnections", "2000"));
        } catch (Exception r) {
        }
        server = new Server();

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        connector.setLowResourceMaxIdleTime(30000);
        connector.setMaxIdleTime(30000);
        connector.setUseDirectBuffers(false);
        connector.setLowResourcesConnections(connectionnum);
        server.setConnectors(new Connector[]{connector});

        File f = new File((new File(webapp)).getAbsolutePath());
        String usepath = System.getProperty("user.dir");
        if (f.exists() && f.isDirectory()) {
            usepath = f.getAbsolutePath();
        } else {
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
        System.setProperty("user.dir", webAppDir.getCanonicalFile().getParentFile().getCanonicalPath());
        System.setProperty("derby.system.home", webAppDir.getCanonicalFile().getParentFile().getCanonicalPath());
        
        WebAppContext context = new WebAppContext(new ContextHandlerCollection(), webAppDir.getAbsolutePath(), "/");
        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{context, new DefaultHandler()});
        server.addHandler(handlers);
        server.start();
        return server;
    }

    /**
     *
     * @param server Server
     * @throws Exception
     */
    public static void stop(Server server) throws Exception {
        server.stop();
        server.join();
    }
    public static Server server = null;
}
