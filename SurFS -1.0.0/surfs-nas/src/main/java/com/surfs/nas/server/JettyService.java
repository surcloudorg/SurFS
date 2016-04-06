/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.server;

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
import com.surfs.nas.util.MainArgs;
import org.mortbay.jetty.Server;

public class JettyService {

    private int port = 8080;
    private Server server;

    public static void main(String[] args) {
        JettyService jetty = new JettyService();
        jetty.start(args);
        
    }

    public Integer start(String[] strings) {
        try {
            MainArgs param = new MainArgs(strings);
            port = param.getInt("port", 8080);
            String web = param.getString("webapp", "web");
            server = JettyServer.start(port, web);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 5;
        }
        return null;
    }

    public int stop(int exitCode) {
        try {
            JettyServer.stop(server);
        } catch (Exception ex) {
        }
        return exitCode;
    }
}
