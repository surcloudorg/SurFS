/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.console;

import com.autumn.core.sql.*;
import com.autumn.core.web.*;
import com.autumn.util.TextUtils;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: hibernate映射编辑</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class HBMSourceMgr extends Action {

    private int id = 0;
    private String title = "";
    private String datasource = "";
    private String classname = "";
    private String tablename = "";
    private String catalogname = "";
    private String createTime = "";
    private String xml = "";
    //操作类型
    private String dotype = null; //操作类型
    private String doMsg = null; //执行结果
    //查询参数
    private String fieldName = null; //查询字段名
    private String fieldValue = null; //查询字段值
    private int rowCount = 0; //当前查询记录数
    private int pageCount = 0; //查询总页数
    private int pageSize = 20; //每页记录数
    private int pageNum = 1; //当前页码
    private List<HBMSourceMgr> rows = new ArrayList<HBMSourceMgr>();
    private List<String> jdbc = new ArrayList<String>();
    private Connection con = null;

    /**
     * 获取共公配置文件路径
     */
    private void setConfigPath() {
        String ss = HibernateConfig.getConfigFileName();//可能为空
        try {
            File f = new File(ss);
            if (f.exists()) {
                setAttribute("pathStr", f.getParent());
                setAttribute("dirname", f.getName());
                return;
            }
        } catch (Exception e) {
        }
        setAttribute("pathStr", "");
        setAttribute("dirname", "");
    }

    /**
     * ResultSet--〉HBMSourceMgr
     *
     * @param crs
     * @return
     * @throws Exception
     */
    private HBMSourceMgr assembler(ResultSet crs) throws Exception {
        HBMSourceMgr mysrv = (HBMSourceMgr) this.assemble(crs);
        mysrv.xml = JdbcUtils.getResultSetStringValue(crs, "xmlmap");
        return mysrv;
    }

    /**
     * 执行查询操作
     *
     * @return ActionForward
     */
    public ActionForward executeQuery() {
        ResultSet crs;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * from hibernatemap where host='");
            sb.append(WebFactory.getInstanceName());
            sb.append("'");
            if (fieldName == null) {
                fieldName = "";
            }
            if (fieldValue == null) {
                fieldValue = "";
            }
            if ((!fieldName.trim().equals("")) && (!fieldValue.trim().equals(""))) {
                if (fieldName.trim().equalsIgnoreCase("id")) {
                    sb.append(" and ");
                    sb.append(fieldName);
                    sb.append("=");
                    sb.append(fieldValue);
                } else {
                    sb.append(" and ");
                    sb.append(fieldName);
                    sb.append(" like '%");
                    sb.append(fieldValue);
                    sb.append("%'");
                }
            }
            SmRowSet myrs = new SmRowSet(con, sb.toString(), "id");
            myrs.setPagesize(pageSize);
            pageCount = myrs.getPageCount();
            rowCount = myrs.getRowCount();
            if (pageNum < 1) {
                pageNum = 1;
            }
            if (pageNum > pageCount) {
                pageNum = pageCount;
            }
            if (myrs.movePage(pageNum)) {
                crs = myrs.getRowset();
                pageNum = myrs.getCurrentPage();
                while (crs.next()) {
                    rows.add(assembler(crs));
                }
            }
        } catch (Exception e) {
            error("查询失败:" + e);
            setDoMsg("查询失败,可能输入的查询条件违法");
        }
        setAttribute("hbmmap", this);
        return new ActionForward("hbmmaps.jsp");
    }

    /**
     * 映射测试
     *
     * @param JDBC
     * @param maps
     * @throws Exception
     */
    private void checkMap(String JDBC, HibernateMapping maps) throws Exception {
        this.closeConnect(con);//derby
        HibernateSessionSource ss = new HibernateSessionSource(JDBC);
        ss.rebuildSessionFactory(maps);
        ss.getSession();
        con = this.getConnect(ConnectionFactory.systemSourceName);
    }

    //执行更新操作
    public ActionForward executeUpdate() {
        if (dotype == null) {
            return null;
        }
        if (dotype.equalsIgnoreCase("添加映射")&&this.getAccessPermission() > 1) { //添加操作
            try {
                if (datasource.equals("")) {
                    setDoMsg("添加映射失败:没有填写连接池名！");
                    throw new Exception("");
                } else {
                    if (!jdbc.contains(datasource)) {
                        setDoMsg("添加映射失败:连接池名" + datasource + "无效！");
                        throw new Exception("");
                    }
                }
                HibernateMapping xmlparser = new HibernateMapping(xml);
                checkMap(datasource, xmlparser);
                String sql =
                        "insert into hibernatemap(host,title,classname,datasource,"
                        + "tablename,catalogname,xmlmap,createtime)values(?,?,?,?,?,?,?,?)";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, WebFactory.getInstanceName());
                prest.setString(2, title);
                prest.setString(3, xmlparser.getClassname());
                prest.setString(4, datasource);
                prest.setString(5, xmlparser.getTablename());
                prest.setString(6, xmlparser.getCatalog());
                prest.setString(7, xmlparser.getXml());
                prest.setTimestamp(8, new Timestamp((new java.util.Date()).getTime()));
                prest.executeUpdate();
                setDoMsg("添加映射" + xmlparser.getClassname() + "成功!");
                StringBuilder sb = new StringBuilder();
                sb.append("title=").append(title).append("\r\n");
                sb.append("Classname=").append(xmlparser.getClassname()).append("\r\n");
                sb.append("datasource=").append(datasource).append("\r\n");
                sb.append("Tablename=").append(xmlparser.getTablename()).append("\r\n");
                sb.append("Catalog=").append(xmlparser.getCatalog()).append("\r\n");
                sb.append("Xml=").append(xmlparser.getXml());
                warn("添加映射:" + sb.toString());
                warn(getDoMsg());
                fieldName = "title";
                fieldValue = title;
                HibernateSessionFactory.removeSessionSource(datasource);
            } catch (Exception e1) {
                if (getDoMsg() == null) {
                    setDoMsg("添加映射失败！");
                }
                error(getDoMsg() + e1.getMessage());
                setAttribute("hbmmap", this);
                return new ActionForward("hbmmapedit.jsp"); //返回编辑页
            }
        } else if (dotype.equalsIgnoreCase("更新映射")&&this.getAccessPermission() > 1) { //更新
            try {
                HibernateMapping xmlparser = new HibernateMapping(xml);
                checkMap(datasource, xmlparser);
                String sql =
                        "update hibernatemap set classname=?,tablename=?,catalogname=?,xmlmap=?,"
                        + "createTime=?,title=?,datasource=? where id=" + id + " and host=?";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, xmlparser.getClassname());
                prest.setString(2, xmlparser.getTablename());
                prest.setString(3, xmlparser.getCatalog());
                prest.setString(4, xmlparser.getXml());
                prest.setTimestamp(5, new Timestamp((new java.util.Date()).getTime()));
                prest.setString(6, title);
                prest.setString(7, datasource);
                prest.setString(8, WebFactory.getInstanceName());
                int res = prest.executeUpdate();
                if (res == 0) {
                    setDoMsg("映射id= " + id + "修改失败:没有权限！");
                    throw new Exception("");
                }
                setDoMsg("映射id= " + id + "修改成功！");
                StringBuilder sb = new StringBuilder();
                sb.append("id=").append(id).append("\r\n");
                sb.append("title=").append(title).append("\r\n");
                sb.append("Classname=").append(xmlparser.getClassname()).append("\r\n");
                sb.append("datasource=").append(datasource).append("\r\n");
                sb.append("Tablename=").append(xmlparser.getTablename()).append("\r\n");
                sb.append("Catalog=").append(xmlparser.getCatalog()).append("\r\n");
                sb.append("Xml=").append(xmlparser.getXml());
                warn("修改映射:" + sb.toString());
                warn(getDoMsg());
                HibernateSessionFactory.removeSessionSource(datasource);
            } catch (Exception e2) { //返回编辑页
                if (getDoMsg() == null) {
                    setDoMsg("映射id= " + id + "修改失败！");
                }
                error(getDoMsg() + e2.getMessage());
                setAttribute("hbmmap", this);
                return new ActionForward("hbmmapedit.jsp");
            }
        } else if (dotype.equalsIgnoreCase("delete")&&this.getAccessPermission() > 1) { //删除
            try {
                int res = JdbcPerformer.executeUpdate(con,
                        "delete from hibernatemap where id=" + id + " and host='" + WebFactory.getInstanceName() + "'");
                if (res == 0) {
                    setDoMsg("没有权限对映射 " + id + " 进行删除操作");
                } else {
                    setDoMsg("映射 " + id + " 删除成功！");
                    warn("删除映射:id=" + id);
                    HibernateSessionFactory.removeSessionSource(datasource);
                }
                warn(getDoMsg());
            } catch (Exception e3) {
                setDoMsg("映射 " + id + " 删除失败！");
                error(getDoMsg() + e3.getMessage());
            }
        } else if (dotype.equalsIgnoreCase("new")&&this.getAccessPermission() > 1) { //新建
            HBMSourceMgr mysrv = new HBMSourceMgr();
            mysrv.setDotype("添加映射");
            mysrv.setDatasource(datasource);
            mysrv.jdbc = this.jdbc;
            mysrv.setCreateTime(TextUtils.Date2String(new java.util.Date(), "yyyy-MM-dd HH:mm:ss"));
            setAttribute("hbmmap", mysrv);
            return new ActionForward("hbmmapedit.jsp");
        } else if (dotype.equalsIgnoreCase("edit")) { //修改
            ResultSet crs;
            try {
                crs = JdbcPerformer.executeQuery(con,
                        "select * from hibernatemap where id=" + id + " and host='" + WebFactory.getInstanceName() + "'");
                if (crs.next()) {
                    HBMSourceMgr mysrv = assembler(crs);
                    mysrv.setDotype("更新映射");
                    mysrv.jdbc = this.jdbc;
                    setAttribute("hbmmap", mysrv);
                    return new ActionForward("hbmmapedit.jsp");
                } else {
                    setDoMsg("找不到映射ID= " + id);
                }
            } catch (Exception d) {
                setDoMsg("数据库查询失败，映射Id= " + id);
                error(getDoMsg() + "！" + d.getMessage());
            }
        }
        return null;
    }

    @Override
    public Forward execute() {
        con = this.getConnect(ConnectionFactory.systemSourceName);
        setConfigPath();
        jdbc = ConnectionFactory.getPoolsKeys();
        ActionForward af = executeUpdate();
        if (af != null) {
            return af;
        }
        return executeQuery();
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the datasource
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * @param datasource the datasource to set
     */
    public void setDatasource(String datasource) {
        this.datasource = datasource;
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
     * @return the tablename
     */
    public String getTablename() {
        return tablename;
    }

    /**
     * @param tablename the tablename to set
     */
    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    /**
     * @return the catalog
     */
    public String getCatalogname() {
        return catalogname;
    }

    /**
     * @param catalog the catalog to set
     */
    public void setCatalogname(String catalog) {
        this.catalogname = catalog;
    }

    /**
     * @return the createTime
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the xml
     */
    public String getXml() {
        return xml;
    }

    /**
     * @param xml the xml to set
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * @return the dotype
     */
    public String getDotype() {
        return dotype;
    }

    /**
     * @param dotype the dotype to set
     */
    public void setDotype(String dotype) {
        this.dotype = dotype;
    }

    /**
     * @return the doMsg
     */
    public String getDoMsg() {
        return doMsg;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName the fieldName to set
     */
    @SessionMethod
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the fieldValue
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * @param fieldValue the fieldValue to set
     */
    @SessionMethod
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * @return the rowCount
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * @param rowCount the rowCount to set
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * @return the pageCount
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * @param pageCount the pageCount to set
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    @SessionMethod
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the pageNum
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * @param pageNum the pageNum to set
     */
    @SessionMethod
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * @return the rows
     */
    public List getRows() {
        return rows;
    }

    /**
     * @return the jdbc
     */
    public List<String> getJdbc() {
        return jdbc;
    }

    /**
     * @param doMsg the doMsg to set
     */
    public void setDoMsg(String doMsg) {
        this.doMsg = doMsg;
    }
}
