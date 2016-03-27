/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.console;

import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.sql.SmRowSet;
import com.autumn.core.web.*;
import com.autumn.util.TextUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: web类控制器编辑</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class ActionMaps extends Action {

    private String id = "0";
    private String actionId = "";
    private String subdir = "";
    private String className = "";
    private String menu = "NA";
    private int permissionOrder = -1;
    private String dirId = "";
    private String dirName = "";
    private String params = "";
    private String memo = "";
    private String createTime = "";
    private TreeMap dirList = null;
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
    private final List<ActionMaps> rows = new ArrayList<ActionMaps>();
    private Connection con = null;

    /**
     * ResultSet-->ActionMaps
     *
     * @param crs
     * @return
     * @throws Exception
     */
    private ActionMaps assembler(ResultSet crs) throws Exception {
        ActionMaps mysrv = (ActionMaps) this.assemble(crs);
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
            sb.append("SELECT actionmap.*,webdirectory.dirname FROM webdirectory INNER JOIN  ");
            sb.append("actionmap ON actionmap.dirid = webdirectory.id");
            sb.append(" where (webdirectory.host='");
            sb.append(WebFactory.getInstanceName());
            sb.append("')");
            if (fieldName == null) {
                fieldName = "";
            }
            if (fieldValue == null) {
                fieldValue = "";
            }
            if ((!fieldName.trim().equals("")) && (!fieldValue.trim().equals(""))) {
                if (fieldName.trim().equalsIgnoreCase("id")) {
                    sb.append(" and actionmap.");
                    sb.append(fieldName);
                    sb.append("=");
                    sb.append(fieldValue);
                } else if (fieldName.trim().equalsIgnoreCase("dirid")) {
                    sb.append(" and webdirectory.id=");
                    sb.append(fieldValue);
                } else if (fieldName.trim().equalsIgnoreCase("dirname")) {
                    sb.append(" and webdirectory.");
                    sb.append(fieldName);
                    sb.append(" like '%");
                    sb.append(fieldValue);
                    sb.append("%'");
                } else {
                    sb.append(" and actionmap.");
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
            trace("查询失败!" , e);
            doMsg = "查询失败" + (e.getMessage() == null ? "!" : (":" + e.getMessage()));
        }
        setAttribute("smactions", this);
        return new ActionForward("actions.jsp");
    }

    /**
     * 检查参数有效性
     *
     * @throws Exception
     */
    private void checkParams() throws Exception {
        if (actionId.equals("")) {
            doMsg = "没有填写ActionID!";
            throw new Exception("");
        }
        if (className.equals("")) {
            doMsg = "没有填写类名!";
            throw new Exception("");
        }
        if (!TextUtils.isValidFileName(subdir)) {
            doMsg = "函数名只能是字母数字下划线组成的字符串!";
            throw new Exception("");
        }
    }

    /**
     * 执行更新操作
     *
     * @return ActionForward
     */
    public ActionForward executeUpdate() {
        String addtion = " and actionmap.dirId IN (SELECT dirid FROM webdirectory "
                + "WHERE host='" + WebFactory.getInstanceName() + "')";
        if (dotype == null) {
            return null;
        }
        TreeMap<String, String> hash = WebFactory.getWebDirs(false);
        hash.remove("0");
        hash.remove("-1");
        hash.remove("-2");
        hash.remove("-3");
        boolean chkdir = hash.containsKey(dirId);
        if (dotype.equalsIgnoreCase("添加类控制") && this.getAccessPermission() > 1) { //添加操作
            try {
                checkParams();
                if (!chkdir) {
                    doMsg = "没有权限在目录ID" + dirId + "上添加Action！";
                    throw new Exception("");
                }
                String sql =
                        "insert into actionmap(actionId,className,subdir,dirid,permissionOrder,params,"
                        + "menu,memo,createTime)values(?,?,?,?,?,?,?,?,?)";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, actionId);
                prest.setString(2, className);
                prest.setString(3, subdir);
                prest.setInt(4, Integer.parseInt(dirId));
                prest.setInt(5, permissionOrder);
                prest.setString(6, params);
                prest.setString(7, menu);
                prest.setString(8, memo);
                prest.setTimestamp(9, new Timestamp((new Date()).getTime()));
                prest.executeUpdate();
                warn(doMsg);
                WebDirectory wd = WebInitializer.initService(con, Integer.parseInt(dirId));
                WebFactory.startService(wd);
                doMsg = "类控制(" + className + ")添加成功!";
                if (WebFactory.getWebDirectory(dirId) == null) {
                    doMsg = "类控制(" + className + ")添加成功,需要重启对应WEB服务生效!";
                }
                warn("添加控制:" + this.toString());
            } catch (Exception e1) {
                if (doMsg == null) {
                    doMsg = "检查字段值是否合法或存在重复！";
                }
                doMsg = "类控制(" + className + ")添加失败:" + doMsg;
                error(doMsg + e1.getMessage());
                dirList = hash;
                setAttribute("smaction", this);
                return new ActionForward("actionedit.jsp"); //返回编辑页
            }
        } else if (dotype.equalsIgnoreCase("修改类控制") && this.getAccessPermission() > 1) { //更新
            try {
                checkParams();
                if (!chkdir) {
                    doMsg = "没有权限修改目录ID：" + dirId + "的ACTION";
                    throw new Exception("");
                }
                WebDirectory cfg2 = WebFactory.getWebDirectory(dirName);
                if (cfg2 == null) {
                    doMsg = "没找到web目录：" + dirName + "！";
                    throw new Exception("");
                }
                String sql =
                        "update actionmap set actionId=?,className=?,subdir=?,dirid=?,"
                        + "permissionOrder=?,params=?,menu=?,memo=?,createTime=? where id=? "
                        + addtion;
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, actionId);
                prest.setString(2, className);
                prest.setString(3, subdir);
                prest.setInt(4, Integer.parseInt(dirId));
                prest.setInt(5, permissionOrder);
                prest.setString(6, params);
                prest.setString(7, menu);
                prest.setString(8, memo);
                prest.setTimestamp(9, new Timestamp((new Date()).getTime()));
                prest.setInt(10, Integer.parseInt(id));
                prest.executeUpdate();
                WebDirectory wd1 = WebInitializer.initService(con, Integer.parseInt(dirId));
                WebDirectory wd2;
                WebFactory.startService(wd1);
                if (cfg2.getId() != Integer.parseInt(dirId)) {
                    wd2 = WebInitializer.initService(con, cfg2.getId());
                    WebFactory.startService(wd2);
                }
                doMsg = "类控制id= " + id + "修改成功!" + (doMsg == null ? "" : doMsg);
                warn(doMsg);
                warn("修改控制:" + this.toString());
            } catch (Exception e2) { //返回编辑页
                if (doMsg == null) {
                    doMsg = "请检查各字段是否合法！";
                }
                doMsg = "类控制id= " + id + "修改失败:" + doMsg;
                error(doMsg + e2.getMessage());
                dirList = hash;
                setAttribute("smaction", this);
                return new ActionForward("actionedit.jsp");
            }
        } else if (dotype.equalsIgnoreCase("delete") && this.getAccessPermission() > 1) { //删除
            try {
                String sql = "delete from actionmap where id=" + id + addtion;
                int res = JdbcPerformer.executeUpdate(con, sql);
                if (res == 0) {
                    doMsg = "没有权限对类控制 " + id + " 进行删除操作!";
                } else {
                    WebDirectory wd = WebInitializer.initService(con, dirName);
                    WebFactory.startService(wd);
                    doMsg = "类控制 " + id + " 删除成功！";
                    warn("删除控制:id=" + id);
                }
                warn(doMsg);
            } catch (Exception e3) {
                doMsg = "类控制 " + id + " 删除失败！";
                error(doMsg + e3.getMessage());
            }
        } else if (dotype.equalsIgnoreCase("new") && this.getAccessPermission() > 1) { //新建
            ActionMaps mysrv = new ActionMaps();
            mysrv.setDirId(dirId);
            mysrv.dirList = hash;
            mysrv.setDotype("添加类控制");
            setAttribute("smaction", mysrv);
            return new ActionForward("actionedit.jsp");
        } else if (dotype.equalsIgnoreCase("edit")) { //修改
            ResultSet crs;
            try {
                crs = JdbcPerformer.executeQuery(con,
                        "SELECT actionmap.*,webdirectory.dirname FROM webdirectory INNER JOIN  "
                        + "actionmap ON actionmap.dirid = webdirectory.Id where actionmap.id="
                        + id + addtion);
                if (crs.next()) {
                    ActionMaps mysrv = assembler(crs);
                    mysrv.dirList = hash;
                    mysrv.setDotype("修改类控制");
                    setAttribute("smaction", mysrv);
                    return new ActionForward("actionedit.jsp");
                } else {
                    doMsg = "找不到类控制ID= " + id;
                }
            } catch (Exception d) {
                doMsg = "数据库查询失败，类控制Id= " + id;
                error(doMsg + "！" + d.getMessage());
            }
        }
        return null;
    }

    @Override
    public Forward execute() {
        con = this.getConnect(ConnectionFactory.systemSourceName);
        ActionForward af = executeUpdate();
        if (af != null) {
            return af;
        }
        return executeQuery();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the actionId
     */
    public String getActionId() {
        return actionId;
    }

    /**
     * @param actionId the actionId to set
     */
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    /**
     * @return the subdir
     */
    public String getSubdir() {
        return subdir;
    }

    /**
     * @param subdir the subdir to set
     */
    public void setSubdir(String subdir) {
        this.subdir = subdir;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the menu
     */
    public String getMenu() {
        return menu;
    }

    /**
     * @param menu the menu to set
     */
    public void setMenu(String menu) {
        this.menu = menu;
    }

    /**
     * @return the permissionOrder
     */
    public int getPermissionOrder() {
        return permissionOrder;
    }

    /**
     * @param permissionOrder the permissionOrder to set
     */
    public void setPermissionOrder(int permissionOrder) {
        this.permissionOrder = permissionOrder;
    }

    /**
     * @return the dirId
     */
    public String getDirId() {
        return dirId;
    }

    /**
     * @param dirId the dirId to set
     */
    public void setDirId(String dirId) {
        this.dirId = dirId;
    }

    /**
     * @return the dirName
     */
    public String getDirName() {
        return dirName;
    }

    /**
     * @param dirName the dirName to set
     */
    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    /**
     * @return the params
     */
    public String getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(String params) {
        this.params = params;
    }

    /**
     * @return the memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * @param memo the memo to set
     */
    public void setMemo(String memo) {
        this.memo = memo;
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
     * @return the dirList
     */
    public TreeMap getDirList() {
        return dirList;
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
     * @param doMsg the doMsg to set
     */
    public void setDoMsg(String doMsg) {
        this.doMsg = doMsg;
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
     * @return the con
     */
    public Connection getCon() {
        return con;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id).append("\r\n");
        sb.append("actionId=").append(actionId).append("\r\n");
        sb.append("className=").append(className).append("\r\n");
        sb.append("subdir=").append(subdir).append("\r\n");
        sb.append("dirId=").append(dirId).append("\r\n");
        sb.append("permissionOrder=").append(permissionOrder).append("\r\n");
        sb.append("params=").append(params);
        return sb.toString();
    }
}
