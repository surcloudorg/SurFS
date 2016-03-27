/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

import com.autumn.core.log.*;
import com.surfs.nas.NodeProperties;
import com.surfs.nas.NosqlDataSource;
import com.surfs.nas.ResourcesAccessor;
import com.surfs.nas.ResourcesManager;
import com.surfs.nas.ReusableThread;
import com.surfs.nas.VolumeProperties;
import com.surfs.nas.error.VolumeNotFoundException;
import com.surfs.nas.error.VolumeStateException;
import com.surfs.nas.transport.TcpServer;
import com.surfs.nas.transport.ThreadPool;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ServerSourceMgr extends ResourcesManager {

    static final Logger log = LogFactory.getLogger(ServerSourceMgr.class);
    private static String localHostName = null;
    private static int localPort = 8020;


    static {
        try {
            String aLocalip = System.getProperty(TcpServer.class.getName() + ".Host");
            if (aLocalip != null && (!aLocalip.trim().isEmpty())) {//优先使用IP
                localHostName = aLocalip.trim();
                
            } else {
                localHostName = InetAddress.getLocalHost().getHostName();
            }
        } catch (UnknownHostException ex) {
            
            System.exit(0);
        }
        try {
            String aLocalPort = System.getProperty(TcpServer.class.getName() + ".Port");
            int port = Integer.parseInt(aLocalPort);
            if (port > 0) {
                localPort = port;
            }
        } catch (Exception ex) {
           
        }
    }

    /**
     * @return the localPort
     */
    public static int getLocalPort() {
        return localPort;
    }

    /**
     * @return the localHostName
     */
    public static String getLocalHostName() {
        return localHostName;
    }

    private Map<String, Volume> volumeMap; 
    private VolumeSelector selector;  
    private VolumeScaner volumeScaner;  
    ReusableThread versionUpdater;  
    private TcpActionMgr tcpActionMgr = null;
    private TcpServer tcpServer;

    public ServerSourceMgr(NosqlDataSource datasource) throws IOException {
        super(datasource);
    }

    @Override
    protected void init() throws IOException {
        volumeMap = Collections.synchronizedMap(new HashMap<String, Volume>());

        versionUpdater = new ReusableThread(15000, "versionUpdater") {
            @Override
            public void doTask() throws IOException {
                ResourcesAccessor configDataAccessor = datasource.getResourcesAccessor();//加载全局参数
                configDataAccessor.updateClientSourceVersion();
            }
        };
        register();
        volumeScaner = new VolumeScaner(this);
        getVolumeScaner().doTask();//初始化卷
        selector = new VolumeSelectorDynamic(this);
        Collection<Volume> col = getVolumeMap().values();//启动卷
        for (Volume vol : col) {
            vol.init();
        }
        getSelector().spaceChange();
        tcpServer = new TcpServer(this);
        ThreadPool.pool.execute(getTcpServer());
    }

    /**
     *
     * @throws IOException
     */
    private void register() throws IOException {
        ResourcesAccessor configDataAccessor = datasource.getResourcesAccessor();
        NodeProperties node = configDataAccessor.getNodeProperties(getLocalHostName());
        if (node == null) {
            node = new NodeProperties();
            node.setServerHost(getLocalHostName());
            node.setPort(getLocalPort());
        } else {
            node.setPort(getLocalPort());
        }
        configDataAccessor.putNodeProperties(node);
        versionUpdater.restart();
    }

    /**
     *
     * @return false
     * @throws IOException
     */
    @Override
    public boolean load() throws IOException {
        ResourcesAccessor configDataAccessor = datasource.getResourcesAccessor();
        String ver = configDataAccessor.getServerSourceVersion();
        if (ver == null) {
            ver = "";
        }
        if (ver.equalsIgnoreCase(version)) { 
            return false;
        }
        globleProperties = configDataAccessor.getGlobleProperties();
        version = ver;
        return true;
    }

    /**
     *
     * @param node
     * @throws IOException
     */
    public void updateNodeProperties(NodeProperties node) throws IOException {
        ResourcesAccessor configDataAccessor = datasource.getResourcesAccessor();
        NodeProperties oldnode = configDataAccessor.getNodeProperties(getLocalHostName());
        if (node.getBackupList() == null ? oldnode.getBackupList() == null
                : node.getBackupList().equals(oldnode.getBackupList())) {//没做改动，不更新
            return;
        }
        node.setPort(getLocalPort());
        configDataAccessor.putNodeProperties(node);
        versionUpdater.restart();
    }

    /**
     *
     * @param vol
     * @throws IOException
     */
    public void updateVolumeProperties(VolumeProperties vol) throws IOException {
        Volume volume = getVolumeMap().get(vol.getVolumeID());
        if (volume == null) {
            throw new VolumeNotFoundException();
        }
        File fileid = new File(volume.path, "volume.cfg");
        if (!fileid.exists()) {
            throw new VolumeStateException();
        }
        ResourcesAccessor configDataAccessor = datasource.getResourcesAccessor();
        if (!vol.getServerHost().equals(localHostName)) {
            throw new VolumeNotFoundException();
        }
        configDataAccessor.putVolumeProperties(vol);
        volume.setVolumeProperties(vol);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (tcpServer != null) {
            tcpServer.shutdown();
        }
        if (volumeScaner != null) {
            volumeScaner.terminate();
        }
        if (versionUpdater != null) {
            versionUpdater.terminate();
        }
        if (tcpActionMgr != null) {
            tcpActionMgr.shutdown();
        }
        if (getVolumeMap() != null) {
            Collection<Volume> col = getVolumeMap().values();
            for (Volume vol : col) {
                vol.destroy();
            }
        }
    }

    /**
     * @return the selector
     */
    public VolumeSelector getSelector() {
        return selector;
    }

    /**
     * @return the tcpActionMgr
     */
    public synchronized TcpActionMgr getTcpActionMgr() {
        if (tcpActionMgr == null) {
            tcpActionMgr = new TcpActionMgr(this);
            ThreadPool.pool.execute(tcpActionMgr);
        }
        return tcpActionMgr;
    }

    /**
     * @return the volumeScaner
     */
    public VolumeScaner getVolumeScaner() {
        return volumeScaner;
    }

    /**
     * @return the volumeMap
     */
    public Map<String, Volume> getVolumeMap() {
        return volumeMap;
    }

    /**
     * @return the tcpServer
     */
    public TcpServer getTcpServer() {
        return tcpServer;
    }
}
