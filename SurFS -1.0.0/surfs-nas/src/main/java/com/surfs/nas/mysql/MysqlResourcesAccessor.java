package com.surfs.nas.mysql;

import com.autumn.core.sql.JdbcTemplate;
import com.autumn.util.TextUtils;
import com.surfs.nas.GlobleProperties;
import static com.surfs.nas.GlobleProperties.key_client_config_version;
import static com.surfs.nas.GlobleProperties.key_globle_config;
import static com.surfs.nas.GlobleProperties.key_server_config_version;
import com.surfs.nas.NodeProperties;
import com.surfs.nas.ResourcesAccessor;
import static com.surfs.nas.ResourcesAccessor.TABLE_NODE;
import static com.surfs.nas.ResourcesAccessor.TABLE_SERVICE;
import static com.surfs.nas.ResourcesAccessor.TABLE_VOLUME;
import com.surfs.nas.VolumeProperties;
import com.surfs.nas.error.NosqlException;
import com.surfs.nas.server.UUID;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import net.sf.json.JSONObject;


public class MysqlResourcesAccessor extends JdbcTemplate implements ResourcesAccessor {

    public MysqlResourcesAccessor(DataSource datasource) {
        super(datasource);
    }

    @Override
    public void putVolumeProperties(VolumeProperties volumeProperties) throws NosqlException {
        try {
            this.addParameter(volumeProperties.toString(), volumeProperties.getVolumeID());
            String sql = "update " + TABLE_VOLUME + " set properties=? where volumeID=?";
            int rc = this.update(sql);
            if (rc <= 0) {
                this.addParameter(volumeProperties.getVolumeID(), volumeProperties.toString());
                sql = "insert into " + TABLE_VOLUME + "(volumeID,properties) values(?,?)";
                this.update(sql);
            }
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public VolumeProperties getVolumeProperties(String volumeID) throws NosqlException {
        try {
            this.addParameter(volumeID);
            String sql = "select properties from " + TABLE_VOLUME + " where volumeID=?";
            ResultSet rs = this.query(sql);
            if (rs.next()) {
                String json = rs.getString("properties");
                JSONObject obj = JSONObject.fromObject(json);
                return (VolumeProperties) JSONObject.toBean(obj, VolumeProperties.class);
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public void deleteVolumeProperties(String volumeID) throws NosqlException {
        this.addParameter(volumeID);
        String sql = "delete from " + TABLE_VOLUME + " where volumeID=?";
        try {
            this.update(sql);
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public VolumeProperties[] listVolumeProperties() throws NosqlException {
        try {
            List<VolumeProperties> list = new ArrayList<>();
            String sql = "select properties from " + TABLE_VOLUME;
            ResultSet rs = this.query(sql);
            while (rs.next()) {
                String json = rs.getString("properties");
                JSONObject obj = JSONObject.fromObject(json);
                VolumeProperties vp = (VolumeProperties) JSONObject.toBean(obj, VolumeProperties.class);
                list.add(vp);
            }
            VolumeProperties[] vps = new VolumeProperties[list.size()];
            return list.toArray(vps);
        } catch (Exception ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public void putNodeProperties(NodeProperties nodeProperties) throws NosqlException {
        try {
            this.addParameter(nodeProperties.toString(), nodeProperties.getServerHost());
            String sql = "update " + TABLE_NODE + " set properties=? where serverHost=?";
            int rc = this.update(sql);
            if (rc <= 0) {
                this.addParameter(nodeProperties.getServerHost(), nodeProperties.toString());
                sql = "insert into " + TABLE_NODE + "(serverHost,properties) values(?,?)";
                this.update(sql);
            }
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public NodeProperties getNodeProperties(String serverip) throws NosqlException {
        try {
            this.addParameter(serverip);
            String sql = "select properties from " + TABLE_NODE + " where serverHost=?";
            ResultSet rs = this.query(sql);
            if (rs.next()) {
                String json = rs.getString("properties");
                JSONObject obj = JSONObject.fromObject(json);
                return (NodeProperties) JSONObject.toBean(obj, NodeProperties.class);
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public void deleteNodeProperties(String serverip) throws NosqlException {
        this.addParameter(serverip);
        String sql = "delete from " + TABLE_NODE + " where serverHost=?";
        try {
            this.update(sql);
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public NodeProperties[] listNodeProperties() throws NosqlException {
        try {
            List<NodeProperties> list = new ArrayList<>();
            String sql = "select properties from " + TABLE_NODE;
            ResultSet rs = this.query(sql);
            while (rs.next()) {
                String json = rs.getString("properties");
                JSONObject obj = JSONObject.fromObject(json);
                NodeProperties np = (NodeProperties) JSONObject.toBean(obj, NodeProperties.class);
                list.add(np);
            }
            NodeProperties[] vps = new NodeProperties[list.size()];
            return list.toArray(vps);
        } catch (Exception ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public void putGlobleProperties(GlobleProperties serverProperties) throws NosqlException {
        try {
            this.addParameter(serverProperties.toString(), key_globle_config);
            String sql = "update " + TABLE_SERVICE + " set config_value=? where config_key=?";
            int rc = this.update(sql);
            if (rc <= 0) {
                this.addParameter(key_globle_config, serverProperties.toString());
                sql = "insert into " + TABLE_SERVICE + "(config_key,config_value) values(?,?)";
                this.update(sql);
            }
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public synchronized GlobleProperties getGlobleProperties() throws NosqlException {
        try {
            this.addParameter(key_globle_config);
            String sql = "select config_value from " + TABLE_SERVICE + " where config_key=?";
            ResultSet rs = this.query(sql);
            if (rs.next()) {
                String json = rs.getString("config_value");
                JSONObject obj = JSONObject.fromObject(json);
                return (GlobleProperties) JSONObject.toBean(obj, GlobleProperties.class);
            } else {
                GlobleProperties sp = new GlobleProperties();
                this.addParameter(key_globle_config, sp.toString());
                sql = "insert into " + TABLE_SERVICE + "(config_key,config_value) values(?,?)";
                this.update(sql);
                return sp;
            }
        } catch (Exception ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public void updateServerSourceVersion() throws NosqlException {
        try {
            String ver = TextUtils.Date2String(new Date()) + "-" + UUID.makeUUID();
            this.addParameter(ver, key_server_config_version);
            String sql = "update " + TABLE_SERVICE + " set config_value=? where config_key=?";
            int rc = this.update(sql);
            if (rc <= 0) {
                this.addParameter(key_server_config_version, ver);
                sql = "insert into " + TABLE_SERVICE + "(config_key,config_value) values(?,?)";
                this.update(sql);
            }
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public void updateClientSourceVersion() throws NosqlException {
        try {
            String ver = TextUtils.Date2String(new Date()) + "-" + UUID.makeUUID();
            this.addParameter(ver, key_client_config_version);
            String sql = "update " + TABLE_SERVICE + " set config_value=? where config_key=?";
            int rc = this.update(sql);
            if (rc <= 0) {
                this.addParameter(key_client_config_version, ver);
                sql = "insert into " + TABLE_SERVICE + "(config_key,config_value) values(?,?)";
                this.update(sql);
            }
        } catch (SQLException ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public String getServerSourceVersion() throws NosqlException {
        try {
            this.addParameter(key_server_config_version);
            String sql = "select config_value from " + TABLE_SERVICE + " where config_key=?";
            ResultSet rs = this.query(sql);
            if (rs.next()) {
                return rs.getString("config_value");
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new NosqlException(ex);
        }
    }

    @Override
    public String getClientSourceVersion() throws NosqlException {
        try {
            this.addParameter(key_client_config_version);
            String sql = "select config_value from " + TABLE_SERVICE + " where config_key=?";
            ResultSet rs = this.query(sql);
            if (rs.next()) {
                return rs.getString("config_value");
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new NosqlException(ex);
        }
    }
}
