package com.surfs.nas;

import com.autumn.core.cfg.Config;
import com.surfs.nas.client.ClientSourceMgr;
import com.surfs.nas.client.Setup;
import com.surfs.nas.mysql.MysqlDataSource;
import com.surfs.nas.server.ServerSourceMgr;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import net.sf.json.JSONObject;
import org.jdom.Element;
import org.jdom.JDOMException;

public final class StoragePool {

    private final String name;
    private String comment = "storagepool";
    private boolean defaultPool = false;
    private String dbprovider = "mysql";
    private Config dbconfig = null;
    private NosqlDataSource datasource = null;
    private ClientSourceMgr clientSourceMgr = null;
    private ServerSourceMgr serverSourceMgr = null;

    public StoragePool(String name) {
        this.name = name;
    }

    /**
     * @return the datasource
     * @throws java.io.IOException
     */
    public synchronized NosqlDataSource getDatasource() throws IOException {
        if (datasource == null) {
            if (this.dbprovider.equalsIgnoreCase("mysql")) {
                datasource = new MysqlDataSource(dbconfig);
            } else {
                throw new java.lang.UnsupportedOperationException("dbprovider Unsupported：" + this.dbprovider);
            }
        }
        return datasource;
    }

    /**
     * @return the dbprovider
     */
    public String getDbprovider() {
        return dbprovider;
    }

    /**
     * @return the dbconfig
     */
    public Config getDbconfig() {
        return dbconfig;
    }

    /**
     * @param e the dbconfig to set
     */
    public void setDbconfig(Element e) {
        try {
            String provider = e.getAttributeValue("provider");
            if (provider == null || provider.isEmpty()) {
                dbprovider = "mysql";
            } else {
                dbprovider = provider;
            }
            this.dbconfig = new Config(e, "");
        } catch (JDOMException ex) {
            throw new ServiceConfigurationError(ex.getMessage());
        }
    }

    /**
     *
     * @return totalSpace,freeSpace
     * @throws IOException
     */
    public Map<String, Long> getSpace() throws IOException {
        long size = 0, free = 0;
        Setup setup = new Setup(getName());
        List<String> ls = getClientSourceMgr().listVolumeID();
        for (String volid : ls) {
            String space = setup.getVolumeSpace(volid);
            JSONObject json = JSONObject.fromObject(space);
            free = free + json.getLong("freeSpace");
            size = size + json.getLong("totalSpace");
        }
        Map<String, Long> map = new HashMap<>();
        map.put("totalSpace", size);
        map.put("freeSpace", free);
        return map;
    }

    /**
     * 本地服务池
     *
     * @return
     */
    public boolean isServicePool() {
        return StorageSources.servicePool == this;
    }

    /**
     *
     * @return @throws IOException
     */
    public synchronized ClientSourceMgr getClientSourceMgr() throws IOException {
        if (clientSourceMgr == null) {
            clientSourceMgr = new ClientSourceMgr(getDatasource());
            clientSourceMgr.initialize();
        }
        return clientSourceMgr;
    }

    /**
     *
     * @return @throws IOException
     */
    public synchronized ServerSourceMgr getServerSourceMgr() throws IOException {
        if (serverSourceMgr == null && isServicePool()) {
            serverSourceMgr = new ServerSourceMgr(getDatasource());
            serverSourceMgr.initialize();
        }
        return serverSourceMgr;
    }

    /**
     * close
     */
    public void close() {
        if (clientSourceMgr != null) {
            clientSourceMgr.shutdown();
            clientSourceMgr = null;
        }
        if (serverSourceMgr != null) {
            serverSourceMgr.shutdown();
            serverSourceMgr = null;
        }
        if (datasource != null) {
            datasource.shutdown();
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the defaultNode
     */
    public boolean isDefaultPool() {
        return defaultPool;
    }

    /**
     * @param defaultPool
     */
    public void setDefaultPool(String defaultPool) {
        if (defaultPool != null) {
            this.defaultPool = defaultPool.equalsIgnoreCase("true");
        }
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        if (comment != null) {
            this.comment = comment;
        }
    }
}
