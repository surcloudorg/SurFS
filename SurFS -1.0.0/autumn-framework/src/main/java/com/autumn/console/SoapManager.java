/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.console;

import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.Method;
import com.autumn.core.cfg.Property;
import com.autumn.core.log.LogFactory;
import com.autumn.core.soap.SoapContext;
import com.autumn.core.soap.SoapFactory;
import com.autumn.core.soap.SoapInitializer;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.sql.SmRowSet;
import com.autumn.core.web.*;
import com.autumn.util.TextUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;
import org.codehaus.xfire.service.Service;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: SOAP服务编辑</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class SoapManager extends Action {

    private int id = 0;
    private String title = "";
    private String servicename = "";
    private String implClass = "";
    private String className = "";
    private int authtype = 0;
    private String style = "rpc";
    private String useType = "literal";
    private String ipList = "";
    private String infilter = "";
    private String outfilter = "";
    private String createTime = "";
    private String params = "";
    private String aegis = "";
    private String logname = LogFactory.SYSTEM_LOGNAME;
    private String memo = "";
    private String statuMsg = ""; //状态信息
    private String imgname = "run";
    //操作类型
    private String dotype = null; //操作类型
    private String doMsg = null; //执行结果
    private List<String> dirlist = null;
    //查询参数
    private String fieldName = null; //查询字段名
    private String fieldValue = null; //查询字段值
    private int rowCount = 0; //当前查询记录数
    private int pageCount = 0; //查询总页数
    private int pageSize = 20; //每页记录数
    private int pageNum = 1; //当前页码
    private List rows = new ArrayList();
    private Config soapConfig = null;
    private Connection con = null;

    /**
     * ResultSet-->SoapManager
     *
     * @param crs
     * @return
     * @throws Exception
     */
    private SoapManager assembler(ResultSet crs) throws Exception {
        SoapManager mysrv = (SoapManager) this.assemble(crs);
        SoapContext ssp = SoapFactory.getSoapContext(mysrv.getId());
        if (ssp != null) {
            mysrv.soapConfig = ssp.getConfig();
        }
        ssp = SoapFactory.getSoapContext(mysrv.getServicename());
        if (ssp != null) {
            if (mysrv.getAuthtype() == 0) {
                mysrv.statuMsg = "不需认证";
            } else if (mysrv.getAuthtype() == 1) {
                mysrv.statuMsg = "Basic认证";
            } else if (mysrv.getAuthtype() == 2) {
                mysrv.statuMsg = "SoapHeader认证";
            } else {
                mysrv.statuMsg = "停用";
            }
            if (ssp.classNeedReload()) {
                mysrv.setImgname("runwithwarn");
            }
        } else {
            if (mysrv.getAuthtype() == 4) {
                mysrv.statuMsg = "停用";
                mysrv.setImgname("forbidden");
            } else {
                mysrv.statuMsg = "未注册";
                mysrv.setImgname("norun");
            }
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
            sb.append("SELECT * from soaps where soaps.host='");
            sb.append(WebFactory.getInstanceName());
            sb.append("'");
            if (getFieldName() == null) {
                setFieldName("");
            }
            if (getFieldValue() == null) {
                setFieldValue("");
            }
            if ((!fieldName.trim().equals("")) && (!fieldValue.trim().equals(""))) {
                if (getFieldName().trim().equalsIgnoreCase("title")
                        || getFieldName().trim().equalsIgnoreCase("classname")) {
                    sb.append(" and ");
                    sb.append(getFieldName());
                    sb.append(" like '%");
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
            myrs.setPagesize(getPageSize());
            setPageCount(myrs.getPageCount());
            setRowCount(myrs.getRowCount());
            if (getPageNum() < 1) {
                setPageNum(1);
            }
            if (getPageNum() > getPageCount()) {
                setPageNum(getPageCount());
            }
            if (myrs.movePage(getPageNum())) {
                crs = myrs.getRowset();
                setPageNum(myrs.getCurrentPage());
                while (crs.next()) {
                    getRows().add(assembler(crs));
                }
            }
        } catch (Exception e) {
            error("查询失败:" + e);
            doMsg = "查询失败,可能输入的查询条件违法";
        }
        setAttribute("smsoap", this);
        return new ActionForward("soaps.jsp");
    }

    /**
     * 检查参数
     *
     * @throws Exception
     */
    private void checkParams() throws Exception {
        if (title.equals("")) {
            doMsg = "没有填写标题字段！";
            throw new Exception("");
        }
        if (className.equals("")) {
            doMsg = "没有填写接口类名！";
            throw new Exception("");
        }
        if (servicename.equals("")) {
            doMsg = "没有填写服务名！";
            throw new Exception("");
        }
        if (!TextUtils.isValidFileName(servicename)) {
            doMsg = "服务名只能是字母数字下划线组成的字符串";
            throw new Exception("");
        }
    }

    /**
     * 编辑
     *
     * @return ActionForward
     */
    public ActionForward executeUpdate() {
        if (dotype == null) {
            return null;
        }
        dirlist = LogFactory.getLogNames();
        dirlist.remove("error");
        if (dotype.equalsIgnoreCase("添加服务") && this.getAccessPermission() > 1) { //添加操作
            try {
                con = this.getConnect(ConnectionFactory.systemSourceName);
                if (LogFactory.findLogger(logname) == null) {
                    logname = LogFactory.SYSTEM_LOGNAME;
                }
                checkParams();
                String name = className.substring(className.lastIndexOf(".")
                        + 1);
                if (SoapFactory.getSoapContext(name) != null) {
                    doMsg = "已经存在一个名称为:" + name + "的服务！";
                    throw new Exception("");
                }
                String sql;
                int soapid = 0;
                sql = "insert into soaps(title,servicename,ImplClass,className,authtype,Style,UseType,"
                        + "CreateTime,memo,iplist,Host,infilter,outfilter,aegis,params,logname)values"
                        + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, title);
                prest.setString(2, servicename);
                prest.setString(3, implClass);
                prest.setString(4, className);
                prest.setInt(5, authtype);
                prest.setString(6, style);
                prest.setString(7, useType);
                prest.setTimestamp(8, new Timestamp((new Date()).getTime()));
                prest.setString(9, memo);
                prest.setString(10, ipList);
                prest.setString(11, WebFactory.getInstanceName());
                prest.setString(12, infilter);
                prest.setString(13, outfilter);
                prest.setString(14, aegis);
                prest.setString(15, params);
                prest.setString(16, logname);
                prest.executeUpdate();
                SoapContext sc = SoapInitializer.initService(con, soapid);
                JdbcUtils.closeConnect(con);
                con = null;
                SoapFactory.startService(sc);
                doMsg = "服务(" + title + ")添加成功！";
                warn("添加服务:" + this.toString());
                warn(doMsg);
                fieldName = "title";
                fieldValue = title;
            } catch (Exception e1) {
                if (doMsg == null) {
                    doMsg = "检查字段值是否合法或存在重复！";
                }
                doMsg = "SOAP服务(" + title + ")添加失败:" + doMsg;
                error(doMsg + e1.getMessage());
                setAttribute("smsoap", this);
                return new ActionForward("soapedit.jsp"); //返回编辑页
            }
        } else if (dotype.equalsIgnoreCase("重新注册") && this.getAccessPermission() > 1) { //更新
            try {
                con = this.getConnect(ConnectionFactory.systemSourceName);
                if (LogFactory.findLogger(logname) == null) {
                    logname = LogFactory.SYSTEM_LOGNAME;
                }
                checkParams();
                String sql = "update soaps set title=?,implClass=?,classname=?,authtype=?"
                        + ",style=?,useType=?,createTime=?,memo=?,ipList=?,infilter=?"
                        + ",outfilter=?,aegis=?,params=?,servicename=?,logname=? where id="
                        + id + " and host=?";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, title);
                prest.setString(2, implClass);
                prest.setString(3, className);
                prest.setInt(4, authtype);
                prest.setString(5, style);
                prest.setString(6, useType);
                prest.setTimestamp(7, new Timestamp((new Date()).getTime()));
                prest.setString(8, memo);
                prest.setString(9, ipList);
                prest.setString(10, infilter);
                prest.setString(11, outfilter);
                prest.setString(12, aegis);
                prest.setString(13, params);
                prest.setString(14, servicename);
                prest.setString(15, logname);
                prest.setString(16, WebFactory.getInstanceName());
                int res = prest.executeUpdate();
                if (res == 0) {
                    doMsg = "没有权限对服务ID=" + id + "进行修改！";
                    throw new Exception("");
                } else {
                    SoapContext sc = SoapInitializer.initService(con, id);
                    JdbcUtils.closeConnect(con);
                    con = null;
                    SoapFactory.startService(sc);
                    if (SoapFactory.getSoapService(id) != null) {
                        doMsg = "服务(" + id + ")重新注册成功！";
                    } else {
                        if (authtype != 4) {
                            doMsg = "服务(" + id + ")重新注册失败！";
                        } else {
                            doMsg = "服务(" + id + ")被禁用！";
                        }
                    }
                    warn("修改服务:" + this.toString());
                }
                warn(doMsg);
            } catch (Exception e2) { //返回编辑页
                if (doMsg == null) {
                    doMsg = "检查字段值是否合法或存在重复！";
                }
                doMsg = "SOAP服务ID(" + id + ")修改失败:" + doMsg;
                error(doMsg + e2.getMessage());
                setAttribute("smsoap", this);
                return new ActionForward("soapedit.jsp");
            }
        } else if (dotype.equalsIgnoreCase("delete") && this.getAccessPermission() > 1) { //删除
            try {
                SoapFactory.stopService(id);
                if (SoapFactory.getSoapContext(id) != null) {
                    throw new Exception("服务无法停止！");
                }
                con = this.getConnect(ConnectionFactory.systemSourceName);
                int rc = JdbcPerformer.executeUpdate(con,
                        "delete from soaps where id=" + id + " and host='" + WebFactory.getInstanceName() + "'");
                if (rc < 1) {
                    doMsg = "没有权限对SOAP服务id=" + id + "进行删除操作";
                } else {
                    JdbcPerformer.executeUpdate(con,
                            "delete from webuser where dirid=-1 and soapid="
                            + id);
                    warn("删除服务:" + id);
                    doMsg = "服务ID=(" + id + ")，删除成功！";
                }
                warn(doMsg);
            } catch (Exception e3) {
                doMsg = "SOAP服务ID(" + id + ")删除失败！";
                error(doMsg + e3.getMessage());
            }
        } else if (dotype.equalsIgnoreCase("getproperty")) { //新建          
            SoapContext srvs = SoapFactory.getSoapContext(id);
            if (srvs != null) {
                soapConfig = srvs.getConfig();
            }
            if (soapConfig == null || soapConfig.getProperties().isEmpty()) {
                doMsg = "SOAP服务" + id + "不能设置属性";
            } else {
                this.setTitle(srvs.getTitle());
                setAttribute("smsoap", this);
                return new ActionForward("soapproperty.jsp");
            }
        } else if ((dotype.equalsIgnoreCase("设置属性")
                || dotype.equalsIgnoreCase("保存属性")) && this.getAccessPermission() > 1) {
            SoapContext srvs = SoapFactory.getSoapContext(id);
            if (srvs != null) {
                soapConfig = srvs.getConfig();
            }
            if (soapConfig == null || soapConfig.getProperties().isEmpty()) {
                doMsg = "SOAP服务" + id + "不能设置属性";
            } else {
                try {
                    SoapFactory.setSoapService(SoapFactory.getSoapService(id));
                    Enumeration<String> e = soapConfig.getAttributeNames();
                    while (e.hasMoreElements()) {
                        String key = e.nextElement();
                        String values = this.getRequest().getParameter(key);
                        if (values != null) {
                            soapConfig.setAttributeValue(key, values);
                        }
                    }
                    doMsg = "SOAP服务" + id + "属性设置完毕！";
                    if (dotype.equalsIgnoreCase("保存属性")) {
                        con = this.getConnect(ConnectionFactory.systemSourceName);
                        srvs.saveConfig(con);
                        doMsg = "SOAP服务" + id + "属性保存完毕！";
                    }
                    warn(doMsg);
                } catch (Exception e) {
                    doMsg = "SOAP服务" + id + "属性设置失败！";
                    error(doMsg + e.getMessage());
                }
                SoapFactory.removeSoapService();
                this.setTitle(srvs.getTitle());
                setAttribute("smsoap", this);
                return new ActionForward("soapproperty.jsp");

            }
        } else if (dotype.equalsIgnoreCase("getmethod")) { //新建
            SoapContext srvs = SoapFactory.getSoapContext(id);
            if (srvs != null) {
                soapConfig = srvs.getConfig();
            }
            if (soapConfig == null || soapConfig.getMethods().isEmpty()) {
                doMsg = "SOAP服务" + id + "不能执行方法";
            } else {
                this.setTitle(srvs.getTitle());
                setAttribute("smsoap", this);
                return new ActionForward("soapmethod.jsp");
            }
        } else if (dotype.equalsIgnoreCase("呼叫此方法") && this.getAccessPermission() > 1) { //新建
            SoapContext srvs = SoapFactory.getSoapContext(id);
            if (srvs != null) {
                soapConfig = srvs.getConfig();
            }
            if (soapConfig == null || soapConfig.getMethods().isEmpty()) {
                doMsg = "SOAP服务" + id + "不能执行方法";
                info(doMsg);
            } else {
                Method method = soapConfig.getMethod(className);
                if (method == null) {
                    doMsg = "SOAP服务" + id + "中找不到方法" + className;
                    warn(doMsg);
                } else {
                    SoapFactory.setSoapService(SoapFactory.getSoapService(id));
                    try {
                        Iterator<Property> e = method.getParams().iterator();
                        while (e.hasNext()) {
                            Property p = e.next();
                            String key = p.getKey();
                            String values = this.getRequest().getParameter(key);
                            if (values != null) {
                                p.setValue(values);
                            }
                        }
                        String Msg = soapConfig.callMethod(method).toString();
                        if (Msg != null) {
                            Msg = "呼叫返回:\r\n" + Msg;
                        }
                        String[] ss = Msg.split("\r\n");
                        rows = Arrays.asList(ss);
                        info("SOAP服务" + id + "方法" + className + "呼叫成功！");
                    } catch (Exception e) {
                        rows.add("呼叫失败!");
                        error("SOAP服务" + id + "方法" + className + "呼叫失败！"
                                + e.getMessage());
                    }
                    SoapFactory.removeSoapService();
                    this.setTitle(srvs.getTitle());
                    setAttribute("smsoap", this);
                    return new ActionForward("soapmethod.jsp");
                }
            }
        } else if (dotype.equalsIgnoreCase("wsdl")) {
            SoapContext ssp = SoapFactory.getSoapContext(id);
            Service service = SoapFactory.getService(ssp);
            try {
                getResponse().reset();
                getResponse().setContentType("application/octet-stream; charset=UTF-8");
                getResponse().addHeader("Content-Disposition", "attachment; filename=\"" + new String((service.getName().getLocalPart() + ".wsdl").getBytes(), "iso8859-1") + "\"");
                service.getWSDLWriter().write(getResponse().getOutputStream());
            } catch (Exception e) {
                error("获取WSDL失败：" + e.getMessage());
            }
        } else if (dotype.equalsIgnoreCase("new") && this.getAccessPermission() > 1) { //新建
            SoapManager mysrv = new SoapManager();
            mysrv.setDotype("添加服务");
            mysrv.setCreateTime(TextUtils.Date2String(new java.util.Date(), "yyyy-MM-dd HH:mm:ss"));
            mysrv.dirlist = dirlist;
            mysrv.logname = LogFactory.SYSTEM_LOGNAME;
            setAttribute("smsoap", mysrv);
            return new ActionForward("soapedit.jsp");
        } else if (dotype.equalsIgnoreCase("edit")) { //修改
            ResultSet crs;
            try {
                con = this.getConnect(ConnectionFactory.systemSourceName);
                crs = JdbcPerformer.executeQuery(con, "select * from soaps where id=" + id + " and host='" + WebFactory.getInstanceName() + "'");
                if (crs.next()) {
                    SoapManager mysrv = assembler(crs);
                    mysrv.setDotype("重新注册");
                    mysrv.dirlist = dirlist;
                    if (!dirlist.contains(mysrv.logname)) {
                        mysrv.logname = LogFactory.SYSTEM_LOGNAME;
                    }
                    setAttribute("smsoap", mysrv);
                    return new ActionForward("soapedit.jsp");
                } else {
                    doMsg = "找不到服务ID=" + id;
                }
            } catch (Exception d) {
                doMsg = "数据库查询失败，服务Id=" + id;
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
        if (dotype != null) {
            if (dotype.equalsIgnoreCase("wsdl")) {
                return null;
            }
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
     * @return the servicename
     */
    public String getServicename() {
        return servicename;
    }

    /**
     * @param servicename the servicename to set
     */
    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    /**
     * @return the implClass
     */
    public String getImplClass() {
        return implClass;
    }

    /**
     * @param implClass the implClass to set
     */
    public void setImplClass(String implClass) {
        this.implClass = implClass;
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
     * @return the authtype
     */
    public int getAuthtype() {
        return authtype;
    }

    /**
     * @param authtype the authtype to set
     */
    public void setAuthtype(int authtype) {
        this.authtype = authtype;
    }

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * @return the useType
     */
    public String getUseType() {
        return useType;
    }

    /**
     * @param useType the useType to set
     */
    public void setUseType(String useType) {
        this.useType = useType;
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
     * @return the infilter
     */
    public String getInfilter() {
        return infilter;
    }

    /**
     * @param infilter the infilter to set
     */
    public void setInfilter(String infilter) {
        this.infilter = infilter;
    }

    /**
     * @return the outfilter
     */
    public String getOutfilter() {
        return outfilter;
    }

    /**
     * @param outfilter the outfilter to set
     */
    public void setOutfilter(String outfilter) {
        this.outfilter = outfilter;
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
     * @return the aegis
     */
    public String getAegis() {
        return aegis;
    }

    /**
     * @param aegis the aegis to set
     */
    public void setAegis(String aegis) {
        this.aegis = aegis;
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
     * @return the statuMsg
     */
    public String getStatuMsg() {
        return statuMsg;
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
     * @return the srvList
     */
    public List<String> getDirlist() {
        return dirlist;
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
     * @return the soapConfig
     */
    public Config getSoapConfig() {
        return soapConfig;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id).append("\r\n");
        sb.append("title=").append(title).append("\r\n");
        sb.append("servicename=").append(servicename).append("\r\n");
        sb.append("implClass=").append(implClass).append("\r\n");
        sb.append("className=").append(className).append("\r\n");
        sb.append("authtype=").append(authtype).append("\r\n");
        sb.append("style=").append(style).append("\r\n");
        sb.append("useType=").append(useType).append("\r\n");
        sb.append("ipList=").append(ipList).append("\r\n");
        sb.append("infilter=").append(infilter).append("\r\n");
        sb.append("outfilter=").append(outfilter).append("\r\n");
        sb.append("aegis=").append(aegis).append("\r\n");
        sb.append("params=").append(params).append("\r\n");
        sb.append("logname=").append(logname);
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
