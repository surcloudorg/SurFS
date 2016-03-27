/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.autopage;

import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * <p>Title: AUTOPAGE-表字段原始属性</p>
 *
 * <p>Description: 一般通过jdbc接口从数据源读取</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Field {

    private String name = null;//字段名
    private String classname = null;//java类名，如：java.lang.String
    private String type = null;//数据库字段类型名，如：varchar
    private int typeNum = 0;//数据库字段类型编号
    private int size = 0;//字段长度
    private boolean increment = false;//是否自增
    private int nullable = 0;//0不为空1null

    /**
     * 如果涉及到关联查询，需要通过此方法获取的被关联的表相关字段的属性
     *
     * @param datasource
     * @param fullname
     * @param name
     * @return Field
     */
    public static Field queryFieldInfo(String datasource, String fullname, String name) {
        int index = fullname.indexOf(".");
        if (index <= 0) {
            return null;
        }
        String table = fullname.substring(0, index);
        String fieldname = fullname.substring(index + 1);
        Connection con = ConnectionFactory.getConnect(datasource, Field.class);
        if (con == null) {
            return null;
        }
        ResultSet rs;
        try {
            String dbtype = con.getMetaData().getDatabaseProductName();
            if (dbtype.equalsIgnoreCase("oracle")) {
                rs = con.createStatement().executeQuery("select " + fieldname + " from " + table + " where rownum=0");
            } else if (dbtype.equalsIgnoreCase("mysql")) {
                rs = con.createStatement().executeQuery("select " + fieldname + " from " + table + " limit 0,0");
            } else if (dbtype.equalsIgnoreCase("apache derby")) {
                rs = con.createStatement().executeQuery("select " + fieldname + " from " + table);
            } else {
                rs = con.createStatement().executeQuery("select top 0 " + fieldname + " from " + table);
            }
        } catch (Exception e) {
            JdbcUtils.closeConnect(con);
            return null;
        }
        try {
            Field info = new Field();
            ResultSetMetaData mdata = rs.getMetaData();
            info.setClassname(mdata.getColumnClassName(1));
            info.setIncrement(mdata.isAutoIncrement(1));
            info.setName(mdata.getColumnName(1).toLowerCase());
            info.setNullable(mdata.isNullable(1));
            info.setSize(mdata.getColumnDisplaySize(1));
            info.setType(mdata.getColumnTypeName(1));
            info.setTypeNum(mdata.getColumnType(1));
            JdbcUtils.closeConnect(con);
            return info;
        } catch (Exception e) {
            JdbcUtils.closeConnect(con);
            return null;
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the classname
     */
    public String getClassname() {
        return classname;
    }

    /**
     * @param classname the classname to set
     */
    public void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the increment
     */
    public boolean isIncrement() {
        return increment;
    }

    /**
     * @param increment the increment to set
     */
    public void setIncrement(boolean increment) {
        this.increment = increment;
    }

    /**
     * @return the nullable
     */
    public int getNullable() {
        return nullable;
    }

    /**
     * @param nullable the nullable to set
     */
    public void setNullable(int nullable) {
        this.nullable = nullable;
    }

    /**
     * @return the typeNum
     */
    public int getTypeNum() {
        return typeNum;
    }

    /**
     * @param typeNum the typeNum to set
     */
    public void setTypeNum(int typeNum) {
        this.typeNum = typeNum;
    }
}
