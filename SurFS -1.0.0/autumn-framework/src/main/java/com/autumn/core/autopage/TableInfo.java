package com.autumn.core.autopage;

import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

/**
 * <p>Title: 表信息</p>
 *
 * <p>Description: 通过jdbc接口从数据源读取表信息</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class TableInfo {

    private String datasource = null;//数据源
    private String table = null;//表名
    private String primarykey = null;//主健
    private HashMap<String, Field> fieldinfos = new HashMap<String, Field>();

    public TableInfo(String datasource, String table, String primarykey) throws Exception {
        this.datasource = datasource;
        this.table = table;
        this.primarykey = primarykey;
        query();
    }

    /**
     * 查询表信息
     *
     * @throws Exception
     */
    private void query() throws Exception {
        Connection con = ConnectionFactory.getConnect(getDatasource(), TableInfo.class);
        if (con == null) {
            throw new Exception("数据源设置错误,无法获取到连接");
        }
        ResultSet rs = null;
        try {
            String dbtype = con.getMetaData().getDatabaseProductName();
            if (dbtype.equalsIgnoreCase("oracle")) {
                rs = con.createStatement().executeQuery("select * from " + getTable() + " where rownum=0");
            } else if (dbtype.equalsIgnoreCase("mysql")) {
                rs = con.createStatement().executeQuery("select * from " + getTable() + " limit 0,0");
            } else if (dbtype.equalsIgnoreCase("apache derby")) {
                rs = con.createStatement().executeQuery("select * from " + getTable());
            } else {
                rs = con.createStatement().executeQuery("select top 0 * from " + getTable());
            }
        } catch (Exception e) {
            JdbcUtils.closeConnect(con);
            throw new Exception("数据库表名设置错误,无法查询到记录");
        }
        try {
            ResultSetMetaData mdata = rs.getMetaData();
            int count = mdata.getColumnCount();
            for (int ii = 1; ii <= count; ii++) {
                Field info = new Field();
                String name = mdata.getColumnName(ii).toLowerCase();
                info.setClassname(mdata.getColumnClassName(ii));
                info.setIncrement(mdata.isAutoIncrement(ii));
                info.setName(name);
                info.setNullable(mdata.isNullable(ii));
                info.setSize(mdata.getColumnDisplaySize(ii));
                info.setType(mdata.getColumnTypeName(ii));
                info.setTypeNum(mdata.getColumnType(ii));
                fieldinfos.put(name, info);
            }
        } catch (Exception e) {
            JdbcUtils.closeConnect(con);
            throw new Exception("获取表字段信息时失败:" + e.getMessage());
        }
        JdbcUtils.closeConnect(con);
        if (fieldinfos.get(primarykey) == null) {
            throw new Exception("表主健设置错误,没有" + primarykey + "字段");
        }
    }

    /**
     * @return the fieldinfos
     */
    public HashMap<String, Field> getFieldinfos() {
        return fieldinfos;
    }

    /**
     * @return the datasource
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @return the primarykey
     */
    public String getPrimarykey() {
        return primarykey;
    }
}
