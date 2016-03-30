/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

import static com.surfs.nas.StorageSources.storageMap;
import com.surfs.nas.log.LogFactory;
import com.surfs.nas.server.Initializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ServiceConfigurationError;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class StorageConfig {

    public static String surfs_pools_key = "surfs_pools";
    public static String surfs_server_key = "surfs_server";

    /**
     * client init
     */
    public synchronized static void initClient() {
        while (storageMap.isEmpty()) {
            try {
                InputStream is = getConfig(false);
                parse(is);
                LogFactory.info("Client initialization success...", StorageConfig.class);
                break;
            } catch (Exception e) {
                LogFactory.error("Client init failed:" + e.getMessage(), StorageConfig.class);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }

    /**
     * server init
     */
    public synchronized static void initServer() {
        while (storageMap.isEmpty()) {
            try {
                InputStream is = getConfig(true);
                parse(is);
                LogFactory.info("Server initialization success...", StorageConfig.class);
                break;
            } catch (Exception e) {
                LogFactory.error("Server init failed:" + e.getMessage(), StorageConfig.class);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }

    public static InputStream getConfig() throws IOException {
        return getConfig(true);
    }

    /**
     *
     * @param isServer
     * @return
     * @throws FileNotFoundException
     */
    private static InputStream getConfig(boolean isServer) throws IOException {
        InputStream is = null;
        if (!isServer) {
            String path = System.getProperty(surfs_server_key);
            if (path != null) {
                String[] urls = path.split(";");
                for (String url : urls) {
                    try {
                        URL u = new URL("http://" + url.trim() + "/zfs/cfg");
                        is = u.openStream();
                        break;
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            String path = System.getProperty(surfs_pools_key);
            if (path != null) {
                File f = new File(path);
                if (f.exists() && f.isFile()) {
                    is = new FileInputStream(f);
                }
            }
        }
        if (is == null) {
            String path = Initializer.getWebpath();
            if (path != null) {
                File f = new File(path, "WEB-INF" + File.separator + "surfs_pools.xml");
                if (f.exists() && f.isFile()) {
                    is = new FileInputStream(f);

                }
            }
        }
        if (is == null) {
            File f = new File("bin/surfs_pools.xml");
            if (f.exists() && f.isFile()) {
                is = new FileInputStream(f);
            }
        }
        if (is == null) {
            File f = new File("../../bin/surfs_pools.xml");
            if (f.exists() && f.isFile()) {
                is = new FileInputStream(f);
            }
        }
        if (is == null) {
            throw new IOException("No properties file could be found for storage pools");
        }
        return is;
    }

    /**
     *
     * @param is
     * @throws IOException
     */
    private static void parse(InputStream is) throws IOException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(is);
            Element root = doc.getRootElement();
            List list = root.getChildren("pool");
            if (list == null || list.isEmpty()) {
                throw new ServiceConfigurationError("'surfs_pools.xml' is empty!");
            }
            for (Object obj : list) {
                Element e = (Element) obj;
                parse(e);
            }
            String serverpool = root.getAttributeValue("servicepool");
            if (!(serverpool == null || serverpool.isEmpty())) {
                StorageSources.servicePool = StorageSources.getStoragePool(serverpool);
            }
        } catch (JDOMException ex) {
            throw new ServiceConfigurationError(ex.getMessage());
        } finally {
            is.close();
        }
    }

    /**
     *
     * @param e
     * @param isZFS
     * @throws IOException
     */
    private static void parse(Element e) throws IOException {
        String name = e.getAttributeValue("name");
        if (name == null || name.isEmpty()) {
            throw new ServiceConfigurationError("name not set.");
        }
        StoragePool pool = new StoragePool(name);
        pool.setComment(e.getAttributeValue("comment"));
        pool.setDefaultPool(e.getAttributeValue("default"));
        pool.setDbconfig(e.getChild("datasource"));
        StorageSources.putStorage(pool);
    }
}
