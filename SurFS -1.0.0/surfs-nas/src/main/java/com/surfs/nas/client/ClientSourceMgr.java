/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import com.surfs.nas.GlobleProperties;
import com.surfs.nas.NosqlDataSource;
import com.surfs.nas.NodeProperties;
import com.surfs.nas.ResourcesAccessor;
import com.surfs.nas.ResourcesManager;
import com.surfs.nas.VolumeProperties;
import com.surfs.nas.error.SystemBusyException;
import com.surfs.nas.transport.TcpClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ClientSourceMgr extends ResourcesManager {

    private Map<String, Node> serverMap;
    NodeSelector selector;
    Map<String, String> volLocationMap;

    public ClientSourceMgr(NosqlDataSource datasource) throws IOException {
        super(datasource);
    }


    @Override
    protected synchronized boolean load() throws IOException {
        if (volLocationMap == null) {
            volLocationMap = Collections.synchronizedMap(new HashMap<String, String>());
        }
        if (getServerMap() == null) {
            serverMap = Collections.synchronizedMap(new HashMap<String, Node>());
        }
        ResourcesAccessor configDataAccessor = getDatasource().getResourcesAccessor();
        String ver = configDataAccessor.getClientSourceVersion();
        if (ver == null) {
            ver = "";
        }
        if (ver.equalsIgnoreCase(version)) {
            return false;
        }
        globleProperties = configDataAccessor.getGlobleProperties();       
        VolumeProperties[] vols = configDataAccessor.listVolumeProperties();
        Map<String, String> map = new HashMap<>();
        for (VolumeProperties vol : vols) {
            map.put(vol.getVolumeID(), vol.getServerHost());
        }
        List<String> set = new ArrayList<>(volLocationMap.keySet());
        for (String vol : set) {
            if (!map.containsKey(vol)) {
                volLocationMap.remove(vol);
            }
        }
        volLocationMap.putAll(map); 
        Map<String, NodeProperties> srvmap = new HashMap<>();
        List<Map.Entry<String, String>> sent = new ArrayList<>(volLocationMap.entrySet());
        for (Map.Entry<String, String> ent : sent) {
            if (!srvmap.containsKey(ent.getValue())) {
                NodeProperties np = configDataAccessor.getNodeProperties(ent.getValue());
                if (np != null) {
                    srvmap.put(ent.getValue(), np);
                } else {
                    volLocationMap.remove(ent.getKey());
                }
            }
        }
        List<String> srvset = new ArrayList<>(getServerMap().keySet());//需要清除不必要的node
        for (String serverip : srvset) {
            if (!srvmap.containsKey(serverip)) {
                Node node = getServerMap().remove(serverip);
                if (node != null) {
                    node.destroy();
                }
            }
        }
        Collection<NodeProperties> nodeprops = srvmap.values();
        for (NodeProperties nodeprop : nodeprops) {
            Node node = getServerMap().get(nodeprop.getServerHost());
            if (node != null) {
                node.setNodeProperties(nodeprop);
                node.setGlobleProperties(getGlobleProperties());
            } else {
                node = new Node(getGlobleProperties(), nodeprop);
                getServerMap().put(nodeprop.getServerHost(), node);
            }
        }
        createSelector();
        version = ver;
        return true;
    }

    private void createSelector() {
        switch (getGlobleProperties().getBalanceRule()) {
            case GlobleProperties.BalanceRule_DYNAMIC:
                if (getSelector() == null || (!(selector instanceof NodeSelectorDynamic))) {
                    selector = new NodeSelectorDynamic(this);
                }
                break;
            case GlobleProperties.BalanceRule_NEAR:
                if (getSelector() == null || (!(selector instanceof NodeSelectorNear))) {
                    selector = new NodeSelectorNear(this);
                }
                break;
            default:
                if (getSelector() == null || (!(selector instanceof NodeSelectorLoop))) {
                    selector = new NodeSelectorLoop(this);
                }
        }
    }

    /**
     *
     * @return List<>
     */
    public List<String> listServerName() {
        List<String> ls = new ArrayList<>(getServerMap().keySet());
        return ls;
    }

    /**
     *
     * @return List<>
     */
    public List<String> listVolumeID() {
        List<String> ls = new ArrayList<>(volLocationMap.keySet());
        return ls;
    }

    /**
     *
     * @param servername
     * @return List<>
     */
    public List<String> listVolumeID(String servername) {
        List<String> ls = new ArrayList<>();
        Map<String, String> map = new HashMap<>(volLocationMap);
        Set<Map.Entry<String, String>> set = map.entrySet();
        for (Map.Entry<String, String> ent : set) {
            if (ent.getValue().equalsIgnoreCase(servername)) {
                ls.add(ent.getKey());
            }
        }
        return ls;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Collection<Node> nodes = getServerMap().values();
        for (Node node : nodes) {
            node.destroy();
        }
    }

    /**
     * @return TcpClient
     * @throws com.surfs.nas.error.SystemBusyException
     */
    public final TcpClient getClient() throws IOException {
        for (int ii = 0; ii < getGlobleProperties().getErrRetryTimes(); ii++) {
            Node node = selector.getNode();
            TcpClient client = node.tcpclient;
            if (client.isConnected()) {
                return client;
            }
        }
        throw new SystemBusyException();
    }

    /**
     *
     * @param serverName
     * @return TcpClient
     * @throws java.io.IOException
     */
    public final TcpClient getClientByNode(String serverName) throws IOException {
        Node node = selector.getNode(serverName, true);
        return node.tcpclient;
    }

    /**
     *
     * @param volumeid
     * @return TcpClient
     * @throws java.io.IOException
     */
    public final TcpClient getClientByVolume(String volumeid) throws IOException {
        Node node = selector.getNode(volumeid, false);
        return node.tcpclient;
    }

    /**
     *
     * @param serverName
     * @param bln
     * @return TcpClient
     * @throws java.io.IOException
     */
    public final TcpClient getClientByNode(String serverName, boolean bln) throws IOException {
        Node node = selector.getNode(serverName, true, bln);
        return node.tcpclient;
    }

    /**
     *
     * @param volumeid
     * @param bln
     * @return TcpClient
     * @throws java.io.IOException
     */
    public final TcpClient getClientByVolume(String volumeid, boolean bln) throws IOException {
        Node node = selector.getNode(volumeid, false, bln);
        return node.tcpclient;
    }

    @Override
    protected void init() throws IOException {
    }

    /**
     * @return the selector
     */
    public NodeSelector getSelector() {
        return selector;
    }

    /**
     * @return the serverMap
     */
    public Map<String, Node> getServerMap() {
        return serverMap;
    }
}
