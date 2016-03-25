package com.surfs.nas.client;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.GlobleProperties;
import com.surfs.nas.server.HandleProgress;
import com.surfs.nas.NosqlDataSource;
import com.surfs.nas.StoragePool;
import com.surfs.nas.StorageSources;
import com.surfs.nas.error.VolumeStateException;
import com.surfs.nas.NodeProperties;
import com.surfs.nas.StorageConfig;
import com.surfs.nas.VolumeProperties;
import com.surfs.nas.protocol.ActiveTestRequest;
import com.surfs.nas.protocol.GetSpaceRequest;
import com.surfs.nas.protocol.GetVolumeHandlerRequest;
import com.surfs.nas.protocol.InitVolumeRequest;
import com.surfs.nas.protocol.ScanDirectoryRequest;
import com.surfs.nas.protocol.ScanVolumeRequest;
import com.surfs.nas.protocol.SetNodeRequest;
import com.surfs.nas.protocol.SetVolumeRequest;
import com.surfs.nas.server.VolumeInfo;
import com.surfs.nas.transport.OriginResponse;
import com.surfs.nas.transport.TcpClient;
import java.io.IOException;

public class Setup {

    static {
        StorageConfig.initClient();
    }
    static Logger log = LogFactory.getLogger(Setup.class);
    private ClientSourceMgr clientSourceMgr = null;
    private StoragePool pool = null;

    public Setup() throws IOException {
        this(StorageSources.getDefaultStoragePool());
    }

    public Setup(String name) throws IOException {
        this(StorageSources.getStoragePool(name));
    }

    public Setup(StoragePool pool) throws IOException {
        this.pool = pool;
        clientSourceMgr = pool.getClientSourceMgr();
    }

    /**
     *
     * @param properties
     * @throws IOException
     */
    public void setVolumeProperties(VolumeProperties properties) throws IOException {
        TcpClient tcpclient = getClientSourceMgr().getClientByNode(properties.getServerHost(), true);
        SetVolumeRequest tr = new SetVolumeRequest();
        tr.setObject(properties);
        tcpclient.get(tr);
    }

    /**
     *
     * @param properties
     * @throws VolumeStateException
     * @throws IOException
     */
    public void setNodeProperties(NodeProperties properties) throws IOException {
        TcpClient tcpclient = getClientSourceMgr().getClientByNode(properties.getServerHost(), true);
        SetNodeRequest tr = new SetNodeRequest();
        tr.setObject(properties);
        tcpclient.get(tr);
    }

    public TcpClient getTcpClient(String hostname) throws IOException {
        NodeProperties nodeProperties = clientSourceMgr.getDatasource().getResourcesAccessor().getNodeProperties(hostname);
        GlobleProperties globleProperties = clientSourceMgr.getDatasource().getResourcesAccessor().getGlobleProperties();
        Node node = new Node();
        node.globleProperties = globleProperties;
        node.nodeProperties = nodeProperties;
        return new TcpClient(node);
    }

    /**
     *
     * @param hostname
     * @throws IOException
     */
    public void scan(String hostname) throws IOException {
        try {
            TcpClient tcpclient = getClientSourceMgr().getClientByNode(hostname, true);
            ScanVolumeRequest tr = new ScanVolumeRequest();
            tcpclient.get(tr);
        } catch (IOException r) {
            TcpClient client = null;
            try {
                client = getTcpClient(hostname);
                ScanVolumeRequest tr = new ScanVolumeRequest();
                client.get(tr);
            } finally {
                if (client != null) {
                    client.destory();
                }
            }
        }
    }

    /**
     *
     * @param hostname
     * @return json
     * @throws IOException
     */
    public VolumeInfo[] getVolumeList(String hostname) throws IOException {
        OriginResponse response;
        try {
            TcpClient tcpclient = getClientSourceMgr().getClientByNode(hostname, true);
            ScanDirectoryRequest tr = new ScanDirectoryRequest();
            response = (OriginResponse) tcpclient.get(tr);
        } catch (IOException r) {
            TcpClient client = null;
            try {
                client = getTcpClient(hostname);
                ScanDirectoryRequest tr = new ScanDirectoryRequest();
                response = (OriginResponse) client.get(tr);
            } finally {
                if (client != null) {
                    client.destory();
                }
            }
        }
        return response.getObjects(VolumeInfo.class);
    }

    /**
     *
     * @param hostname
     * @param path
     * @return
     * @throws IOException
     */
    public VolumeInfo initVolume(String hostname, String path) throws IOException {
        OriginResponse response;
        try {
            TcpClient tcpclient = getClientSourceMgr().getClientByNode(hostname, true);
            InitVolumeRequest tr = new InitVolumeRequest();
            tr.setString(path);
            response = (OriginResponse) tcpclient.get(tr);
        } catch (IOException r) {
            TcpClient client = null;
            try {
                client = getTcpClient(hostname);
                InitVolumeRequest tr = new InitVolumeRequest();
                tr.setString(path);
                response = (OriginResponse) client.get(tr);
            } finally {
                if (client != null) {
                    client.destory();
                }
            }
        }
        return response.getObject(VolumeInfo.class);
    }

    /**
     *
     * @param hostname
     * @return boolean
     */
    public boolean isReady(String hostname) {
        try {
            TcpClient tcpclient = getClientSourceMgr().getClientByNode(hostname, true);
            ActiveTestRequest tr = new ActiveTestRequest();
            tcpclient.get(tr);
            return true;
        } catch (IOException ie) {
            return false;
        }
    }

    /**
     *
     * @param volID
     * @return json {"freeSpace" = 0;totalSpace = 0; percent = 100}
     * @throws java.io.IOException
     */
    public String getVolumeSpace(String volID) throws IOException {
        TcpClient tcpclient = getClientSourceMgr().getClientByVolume(volID, true);
        GetSpaceRequest tr = new GetSpaceRequest();
        tr.setString(volID);
        OriginResponse response = (OriginResponse) tcpclient.get(tr);
        return response.getString();
    }

    /**
     *
     * @param volID
     * @return
     * @throws java.io.IOException
     */
    public HandleProgress[] getThreadInfo(String volID) throws IOException {
        TcpClient tcpclient = getClientSourceMgr().getClientByVolume(volID, true);
        GetVolumeHandlerRequest tr = new GetVolumeHandlerRequest();
        tr.setString(volID);
        OriginResponse response = (OriginResponse) tcpclient.get(tr);
        return response.getObjects(HandleProgress.class);
    }

    /**
     * @return the clientSourceMgr
     */
    public ClientSourceMgr getClientSourceMgr() {
        return clientSourceMgr;
    }

    /**
     * @return the clientSourceMgr
     */
    public NosqlDataSource getDataSource() {
        return clientSourceMgr.getDatasource();
    }

    /**
     * @return the pool
     */
    public StoragePool getPool() {
        return pool;
    }
}
