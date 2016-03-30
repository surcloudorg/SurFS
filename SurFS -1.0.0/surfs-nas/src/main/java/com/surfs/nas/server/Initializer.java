/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.server;

import com.surfs.nas.StorageConfig;
import com.surfs.nas.StoragePool;
import com.surfs.nas.StorageSources;
import com.surfs.nas.log.LogInitializer;
import com.surfs.nas.transport.ThreadPool;
import java.io.File;
import static java.lang.Thread.sleep;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Initializer implements ServletContextListener {

    private static String webpath = null;

    public static String getWebpath() {
        return webpath;
    }
    private Thread monitor = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        webpath = servletContext.getRealPath("") + (servletContext.getRealPath("").endsWith(File.separator) ? "" : File.separator);//web服务目录
        LogInitializer.initLogger();
        try {
            init();
        } catch (Throwable ex) {

            close();
            monitor = new Thread() {
                @Override
                public void run() {
                    while (!this.isInterrupted()) {
                        try {
                            sleep(30000);
                            init();
                            break;
                        } catch (InterruptedException ex1) {
                            break;
                        } catch (Throwable ex) {

                            close();
                        }
                    }
                }
            };
            monitor.start();
        }
    }

    private void init() throws Throwable {
        StorageConfig.initServer();
        StoragePool pool = StorageSources.getServiceStoragePool();
        if (pool == null) {
            throw new Exception("");
        }
        pool.getServerSourceMgr();
    }

    private void close() {
        StorageSources.terminate();
    }



    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ThreadPool.stopThread(monitor);
        close();
    }
}
