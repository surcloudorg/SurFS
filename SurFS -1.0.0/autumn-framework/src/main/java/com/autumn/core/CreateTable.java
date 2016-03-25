package com.autumn.core;

import com.autumn.core.log.LogFactory;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.sql.ScriptExecuter;
import com.autumn.core.web.WebFactory;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: 创建系统表</p>
 *
 * <p>Description: 在启动框架时创建框架需要的表</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class CreateTable {

    private Connection conn = null;
    private List<String> tables = null;
    private String dbType = "apache derby";

    /**
     * 获取数据库表名
     *
     * @param conn
     */
    public CreateTable(Connection conn) {
        this.conn = conn;
        try {
            dbType = conn.getMetaData().getDatabaseProductName();
            tables = ScriptExecuter.showtables(conn);
        } catch (Exception r) {
            r.printStackTrace();
            tables = new ArrayList<String>();
        }
    }

    /**
     * 创建表
     */
    public void dotask() {
        create("datasource");
        create("hibernatemap");
        create("soaps");
        create("demo");
        create("logcfg");
        create("services");
        create("webdirectory");
        create("actionmap");
        create("webuser");
        create("svncodes");
        if (dbType.equalsIgnoreCase("apache derby")) {//本地数据库需要更新机器名
            update("datasource");
            update("hibernatemap");
            update("soaps");
            update("logcfg");
            update("services");
            update("webdirectory");
            update("svncodes");
        }
    }

    /**
     * 更新
     *
     * @param tabname
     */
    private void update(String tabname) {
        Statement st = null;
        String sql = "update " + tabname.toUpperCase() + " set host='" + WebFactory.getInstanceName() + "'";
        try {
            st = conn.createStatement();
            st.executeUpdate(sql);
        } catch (Exception r) {
            LogFactory.error("更新失败:" + sql + "," + r.getMessage(), CreateTable.class);
        } finally {
            JdbcUtils.closeStatement(st);
        }
    }

    /**
     * 创建
     *
     * @param tabname
     */
    private void create(String tabname) {
        tabname = tabname.toLowerCase();
        if (tables.contains(tabname)) {
            return;
        }
        try {
            URL url;
            if (dbType.equalsIgnoreCase("mysql")) {
                url = CreateTable.class.getResource("/resources/sql/mysql/" + tabname + ".sql");
            } else if (dbType.equalsIgnoreCase("Microsoft SQL Server")) {
                url = CreateTable.class.getResource("/resources/sql/mssql/" + tabname + ".sql");
            } else if (dbType.equalsIgnoreCase("oracle")) {
                url = CreateTable.class.getResource("/resources/sql/oracle/" + tabname + ".sql");
            } else {
                url = CreateTable.class.getResource("/resources/sql/derby/" + tabname + ".sql");
            }
            ScriptExecuter.doScript(conn, url.openStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
