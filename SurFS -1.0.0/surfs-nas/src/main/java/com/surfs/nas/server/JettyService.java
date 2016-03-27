/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

import com.autumn.core.JettyServer;
import com.autumn.util.MainArgs;
import com.surfs.nas.LogInitializer;
 
import org.mortbay.jetty.Server;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

public class JettyService implements WrapperListener {

    private int port = 8080;
    private Server server;

    public static void main(String[] args) {
        WrapperManager.start(new JettyService(), args);
    }

    @Override
    public Integer start(String[] strings) {
        LogInitializer.initLogger();
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

    @Override
    public int stop(int exitCode) {
        try {
            JettyServer.stop(server);
        } catch (Exception ex) {
        }
        return exitCode;
    }

    @Override
    public void controlEvent(int event) {
        if (WrapperManager.isControlledByNativeWrapper() == false) {
            if (event == WrapperManager.WRAPPER_CTRL_C_EVENT
                    || event == WrapperManager.WRAPPER_CTRL_CLOSE_EVENT
                    || event == WrapperManager.WRAPPER_CTRL_SHUTDOWN_EVENT) {
                WrapperManager.stop(0);
            }
        }
    }
}
