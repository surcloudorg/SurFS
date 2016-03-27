/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.console;

import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.Method;
import com.autumn.core.cfg.Property;
import com.autumn.core.log.LogFactory;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.sql.SmRowSet;
import com.autumn.core.web.*;
import com.autumn.util.TextUtils;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

/**
 * <p>
 * Title: 框架控制台</p>
 *
 * <p>
 * Description: WEB服务管理</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class WebManager extends Action {

    private int id = 0;
    private String title = "";
    private String dirName = "";
    private String classname = "";
    private String defaultPage = "index.jsp";
    private String ipList = "";
    private int logintype = 1;
    private String createTime = "";
    private String params = "";
    private String memo = "";
    private String charset = "";
    private String logname = "";
    private String imgname = "run";
    private Config serviceConfig = null;
    //操作类型
    private String dotype = null; //操作类型
    private String doMsg = null; //执行结果
    private String fieldName = null; //查询字段名
    private String fieldValue = null; //查询字段值
    private int rowCount = 0; //当前查询记录数
    private int pageCount = 0; //查询总页数
    private int pageSize = 20; //每页记录数
    private int pageNum = 1; //当前页码
    private List rows = new ArrayList();
    private List<String> dirlist = null;
    private Connection con = null;

    /**
     * ResultSet--〉WebManager
     *
     * @param crs
     * @return
     * @throws Exception
     */
    private WebManager assembler(ResultSet crs) throws Exception {
        WebManager mysrv = (WebManager) this.assemble(crs);
        WebDirectory webdir = WebFactory.getWebDirectory(mysrv.getDirName());
        if (webdir != null) {
            mysrv.serviceConfig = webdir.getConfig();
            if (mysrv.getLogintype() == 0) {
                mysrv.imgname = "forbidden";
            } else {
                if (webdir.classNeedReload()) {
                    mysrv.setImgname("runwithwarn");
                }
            }
        } else {
            mysrv.imgname = "forbidden";
        }
        return mysrv;
    }

    //执行查询操作
    public ActionForward executeQuery() {
        ResultSet crs;
        try {
            if (con == null) {
                con = this.getConnect(ConnectionFactory.systemSourceName);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * from webdirectory where host='").append(WebFactory.getInstanceName()).append("'");
            if (getFieldName() == null) {
                setFieldName("");
            }
            if (getFieldValue() == null) {
                setFieldValue("");
            }
            if ((!fieldName.trim().equals("")) && (!fieldValue.trim().equals(""))) {
                if (getFieldName().trim().equalsIgnoreCase("classname")) {
                    sb.append(" and classname like '%");
                    sb.append(getFieldValue());
                    sb.append("%'");
                } else if (getFieldName().trim().equalsIgnoreCase("title")) {
                    sb.append(" and title like '%");
                    sb.append(getFieldValue());
                    sb.append("%'");
                } else if (getFieldName().trim().equalsIgnoreCase("dirname")) {
                    sb.append(" and dirname like '%");
                    sb.append(getFieldValue());
                    sb.append("%'");
                } else {
                    sb.append(" and ");
                    sb.append(getFieldName());
                    sb.append("=");
                    sb.append(getFieldValue());
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
            doMsg = "查询失败,可能输入的查询条件违法";
        }
        setAttribute("smwebs", this);
        return new ActionForward("webs.jsp");
    }

    /**
     * 检查参数
     *
     * @throws Exception
     */
    private void checkParams() throws Exception {
        if (!(charset == null || charset.trim().isEmpty())) {
            if (!Charset.isSupported(charset.trim())) {
                doMsg = "不支持的编码:" + charset;
                throw new Exception("");
            }
        }
        if (title.equals("")) {
            doMsg = "没有填写标题！";
            throw new Exception("");
        }
        if (dirName.equals("")) {
            doMsg = "没有填写目录名！";
            throw new Exception("");
        }
        if (dirName.equalsIgnoreCase("console")
                || dirName.equalsIgnoreCase("services")) {
            doMsg = "目录名不能为console/htmleditor/mail/services！";
            throw new Exception("");
        }
        if (!TextUtils.isValidFileName(dirName)) {
            doMsg = "目录名只能是字母数字下划线组成的字符串";
            throw new Exception("");
        }
    }

    //执行更新操作
    public ActionForward executeUpdate() {
        if (dotype == null) {
            return null;
        }
        dirlist = LogFactory.getLogNames();
        dirlist.remove("error");
        if (dotype.equalsIgnoreCase("添加目录") && this.getAccessPermission() > 1) { //添加操作
            try {
                con = this.getConnect(ConnectionFactory.systemSourceName);
                if (LogFactory.findLogger(logname) == null) {
                    logname = LogFactory.SYSTEM_LOGNAME;
                }
                checkParams();
                String sql
                        = "insert into webdirectory(dirName,title,classname,defaultpage,iplist,logintype,"
                        + "params,logname,charset,memo,CreateTime,Host)values(?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, dirName);
                prest.setString(2, title);
                prest.setString(3, classname);
                prest.setString(4, defaultPage);
                prest.setString(5, ipList);
                prest.setInt(6, logintype);
                prest.setString(7, params);
                prest.setString(8, logname);
                prest.setString(9, charset);
                prest.setString(10, memo);
                prest.setTimestamp(11, new Timestamp((new Date()).getTime()));
                prest.setString(12, WebFactory.getInstanceName());
                prest.executeUpdate();
                doMsg = "目录(" + dirName + ")添加成功！";
                WebDirectory sc = WebInitializer.initService(con, dirName);
                JdbcUtils.closeConnect(con);
                con = null;
                WebFactory.startService(sc);
                warn(doMsg);
                warn("创建目录:" + this.toString());
            } catch (Exception e1) {
                if (doMsg == null) {
                    doMsg = "检查字段值是否合法或存在重复！";
                }
                doMsg = "目录(" + dirName + ")添加失败！" + doMsg;
                error(doMsg + e1.getMessage());
                setAttribute("smweb", this);
                return new ActionForward("webedit.jsp"); //返回编辑页
            }
        } else if (dotype.equalsIgnoreCase("重新注册") && this.getAccessPermission() > 1) { //更新
            try {
                con = this.getConnect(ConnectionFactory.systemSourceName);
                if (LogFactory.findLogger(logname) == null) {
                    logname = LogFactory.SYSTEM_LOGNAME;
                }
                checkParams();
                WebDirectory wd = WebFactory.getWebDirectory(id);
                if (wd != null && wd.getDirName().equalsIgnoreCase("root")) {
                    dirName = "root";
                }
                String sql
                        = "update webdirectory set dirName=?,title=?,classname=?,defaultpage=?,ipList=?,"
                        + "logintype=?,params=?,logname=?,charset=?,memo=?,createTime=? where host=? and id=?";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, dirName);
                prest.setString(2, title);
                prest.setString(3, classname);
                prest.setString(4, defaultPage);
                prest.setString(5, ipList);
                prest.setInt(6, logintype);
                prest.setString(7, params);
                prest.setString(8, logname);
                prest.setString(9, charset);
                prest.setString(10, memo);
                prest.setTimestamp(11, new Timestamp((new Date()).getTime()));
                prest.setString(12, WebFactory.getInstanceName());
                prest.setInt(13, id);
                int res = prest.executeUpdate();
                if (res == 0) {
                    doMsg = "没有权限对服务ID=" + id + "进行修改！";
                    throw new Exception("");
                } else {
                    WebDirectory sc = WebInitializer.initService(con, id);
                    JdbcUtils.closeConnect(con);
                    con = null;
                    WebFactory.startService(sc);
                    if (WebFactory.getWebService(id) != null) {
                        doMsg = "目录(" + dirName + ")重新注册成功！";
                    } else {
                        if (logintype != 0) {
                            doMsg = "目录(" + dirName + ")重新注册失败！";
                        } else {
                            doMsg = "目录(" + dirName + ")被禁用！";
                        }
                    }
                    warn("修改目录:" + this.toString());
                }
                warn(doMsg);
            } catch (Exception e2) { //返回编辑页
                e2.printStackTrace();
                if (doMsg == null) {
                    doMsg = "请检查各字段是否合法";
                }
                doMsg = "目录(" + dirName + ")重新注册失败！" + doMsg;
                error(doMsg + e2.getMessage());
                setAttribute("smweb", this);
                return new ActionForward("webedit.jsp");
            }
        } else if (dotype.equalsIgnoreCase("delete") && this.getAccessPermission() > 1) { //删除
            try {
                WebDirectory wd = WebFactory.getWebDirectory(id);
                if (wd != null && wd.getDirName().equalsIgnoreCase("root")) {
                    throw new Exception("不能删除根目录！");
                }
                WebFactory.stopService(id);
                if (WebFactory.getWebService(id) != null) {
                    throw new Exception("服务无法停止！");
                }
                con = this.getConnect(ConnectionFactory.systemSourceName);
                int ret = JdbcPerformer.executeUpdate(con,
                        "delete from webdirectory where id=" + id + " and host='" + WebFactory.getInstanceName() + "'");
                if (ret > 0) {
                    JdbcPerformer.executeUpdate(con,
                            "delete from actionmap where dirid=" + id);
                    JdbcPerformer.executeUpdate(con,
                            "delete from webuser where dirid=" + id);
                    doMsg = "目录(" + id + ")，删除成功！";
                    warn("删除目录:id=" + id);
                    warn(doMsg);
                }
            } catch (Exception e3) {
                doMsg = "目录(" + id + ")删除失败！";
                error(doMsg + e3.getMessage());
            }
        } else if (dotype.equalsIgnoreCase("getproperty")) {
            WebDirectory service = WebFactory.getWebDirectory(dirName);
            if (service != null) {
                serviceConfig = service.getConfig();
            }
            if (getServiceConfig() == null || getServiceConfig().getProperties().isEmpty()) {
                doMsg = "服务" + dirName + "不能设置属性";
                warn(doMsg);
            } else {
                this.setTitle(service.getTitle());
                setAttribute("smweb", this);
                return new ActionForward("webproperty.jsp");
            }
        } else if ((dotype.equalsIgnoreCase("设置属性")
                || dotype.equalsIgnoreCase("保存属性")) && this.getAccessPermission() > 1) {
            WebDirectory service = WebFactory.getWebDirectory(dirName);
            if (service != null) {
                serviceConfig = service.getConfig();
            }
            if (getServiceConfig() == null || getServiceConfig().getProperties().isEmpty()) {
                doMsg = "服务" + dirName + "不能设置属性";
                warn(doMsg);
            } else {
                try {
                    WebFactory.setWebService(WebFactory.getWebService(dirName));
                    Enumeration<String> e = getServiceConfig().getAttributeNames();
                    while (e.hasMoreElements()) {
                        String key = e.nextElement();
                        String values = this.getRequest().getParameter(key);
                        if (values != null) {
                            getServiceConfig().setAttributeValue(key, values);
                        }
                    }
                    doMsg = "服务" + dirName + "属性设置完毕！";
                    if (dotype.equalsIgnoreCase("保存属性")) {
                        try {
                            con = this.getConnect(ConnectionFactory.systemSourceName);
                            service.saveConfig(con);
                            doMsg = "服务" + dirName + "属性保存完毕！";
                        } catch (Exception e2) {
                            doMsg = "服务" + dirName + "属性保存失败！";
                        }
                    }
                    warn(doMsg);
                } catch (Exception e) {
                    doMsg = "服务" + dirName + "属性设置失败！";
                    error(doMsg + e.getMessage());
                }
                WebFactory.removeWebService();
                this.setTitle(service.getTitle());
                setAttribute("smweb", this);
                return new ActionForward("webproperty.jsp");
            }
        } else if (dotype.equalsIgnoreCase("getmethod")) {
            WebDirectory service = WebFactory.getWebDirectory(dirName);
            if (service != null) {
                serviceConfig = service.getConfig();
            }
            if (getServiceConfig() == null || getServiceConfig().getMethods().isEmpty()) {
                doMsg = "服务" + dirName + "不能执行方法";
                warn(doMsg);
            } else {
                this.setTitle(service.getTitle());
                setAttribute("smweb", this);
                return new ActionForward("webmethod.jsp");
            }
        } else if (dotype.equalsIgnoreCase("呼叫此方法") && this.getAccessPermission() > 1) {
            WebDirectory service = WebFactory.getWebDirectory(dirName);
            if (service != null) {
                serviceConfig = service.getConfig();
            }
            if (getServiceConfig() == null || getServiceConfig().getMethods().isEmpty()) {
                doMsg = "服务" + dirName + "不能执行方法";
            } else {
                Method method = getServiceConfig().getMethod(classname);
                if (method == null) {
                    doMsg = "服务" + dirName + "中找不到方法" + classname;
                    warn(doMsg);
                } else {
                    try {
                        WebFactory.setWebService(WebFactory.getWebService(dirName));
                        Iterator<Property> e = method.getParams().iterator();
                        while (e.hasNext()) {
                            Property p = e.next();
                            String key = p.getKey();
                            String values = this.getRequest().getParameter(key);
                            if (values != null) {
                                p.setValue(values);
                            }
                        }
                        String Msg = getServiceConfig().callMethod(method).toString();
                        if (Msg != null) {
                            Msg = "呼叫返回:\r\n" + Msg;
                        }
                        String[] ss = Msg.split("\r\n");
                        rows = Arrays.asList(ss);
                        info("服务" + dirName + "方法" + dirName + "呼叫成功！");
                    } catch (Exception e) {
                        rows.add("呼叫失败:" + e.getMessage());
                        error("服务" + dirName + "方法" + dirName + "呼叫失败！" + e.getMessage());
                    }
                    WebFactory.removeWebService();
                    this.setTitle(service.getTitle());
                    setAttribute("smweb", this);
                    return new ActionForward("webmethod.jsp");
                }
            }
        } else if (dotype.equalsIgnoreCase("new") && this.getAccessPermission() > 1) { //新建
            WebManager mysrv = new WebManager();
            mysrv.setDotype("添加目录");
            mysrv.setCreateTime(TextUtils.Date2String(new Date(), "yyyy-MM-dd HH:mm:ss"));
            setAttribute("smweb", mysrv);
            mysrv.dirlist = dirlist;
            return new ActionForward("webedit.jsp"); //返回编辑页
        } else if (dotype.equalsIgnoreCase("edit")) { //修改
            ResultSet crs;
            try {
                con = this.getConnect(ConnectionFactory.systemSourceName);
                crs = JdbcPerformer.executeQuery(con,
                        "select * from webdirectory where id=" + id + " and host='" + WebFactory.getInstanceName() + "'");
                if (crs.next()) {
                    WebManager mysrv = assembler(crs);
                    mysrv.dirlist = dirlist;
                    mysrv.setDotype("重新注册");
                    getRequest().setAttribute("smweb", mysrv);
                    return new ActionForward("webedit.jsp"); //返回编辑页
                } else {
                    doMsg = "找不到目录ID" + id;
                }
            } catch (Exception d) {
                doMsg = "数据库查询失败，目录Id=" + id;
                error(doMsg + "！" + d.getMessage());
            }
        }
        return null;
    }

    @Override
    public Forward execute() {
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
     * @return the defaultPage
     */
    public String getDefaultPage() {
        return defaultPage;
    }

    /**
     * @param defaultPage the defaultPage to set
     */
    public void setDefaultPage(String defaultPage) {
        this.defaultPage = defaultPage;
    }

    /**
     * @return the ipList
     */
    public String getIpList() {
        return ipList;
    }

    /**
     * @param ipList the ipList to set
     */
    public void setIpList(String ipList) {
        this.ipList = ipList;
    }

    /**
     * @return the logintype
     */
    public int getLogintype() {
        return logintype;
    }

    /**
     * @param logintype the logintype to set
     */
    public void setLogintype(int logintype) {
        this.logintype = logintype;
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
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @return the logname
     */
    public String getLogname() {
        return logname;
    }

    /**
     * @param logname the logname to set
     */
    public void setLogname(String logname) {
        this.logname = logname;
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
     * @return the dirlist
     */
    public List<String> getDirlist() {
        return dirlist;
    }

    /**
     * @return the serviceConfig
     */
    public Config getServiceConfig() {
        return serviceConfig;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id).append("\r\n");
        sb.append("dirName=").append(dirName).append("\r\n");
        sb.append("title=").append(title).append("\r\n");
        sb.append("classname=").append(classname).append("\r\n");
        sb.append("defaultPage=").append(defaultPage).append("\r\n");
        sb.append("ipList=").append(ipList).append("\r\n");
        sb.append("logintype=").append(logintype).append("\r\n");
        sb.append("logname=").append(logname).append("\r\n");
        sb.append("charset=").append(charset).append("\r\n");
        sb.append("params=").append(params);
        return sb.toString();
    }

    /**
     * @return the imgname
     */
    public String getImgname() {
        return imgname;
    }

    /**
     * @param imgname the imgname to set
     */
    public void setImgname(String imgname) {
        this.imgname = imgname;
    }
}
