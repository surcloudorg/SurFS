package com.autumn.console;

import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.Method;
import com.autumn.core.cfg.Property;
import com.autumn.core.log.LogFactory;
import com.autumn.core.service.Service;
import com.autumn.core.service.ServiceConfig;
import com.autumn.core.service.ServiceFactory;
import com.autumn.core.service.ServiceInitializer;
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

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 服务编辑</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class ServiceManager extends Action {

    private int id = 0;
    private String title = "";
    private String classname = null;
    private String params = null;
    private String logname =  LogFactory.SYSTEM_LOGNAME;
    private int status = 0;
    private String memo = "";
    private String createtime = null;
    private String statuMsg = ""; //状态信息
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
     * ResultSet-->ServiceManager
     *
     * @param crs
     * @return
     * @throws Exception
     */
    private ServiceManager assembler(ResultSet crs) throws Exception {
        ServiceManager mysrv = (ServiceManager) this.assemble(crs);
        Service service = ServiceFactory.getService(mysrv.getId());
        if (service == null) {
            if (mysrv.getStatus() == 2) {
                mysrv.setStatuMsg("服务被禁用");
                mysrv.setImgname("forbidden");
            } else {
                mysrv.setStatuMsg("启动服务");
                ServiceConfig sc = new ServiceConfig(mysrv.getId());
                sc.setParams(mysrv.params);
                mysrv.serviceConfig = sc.getConfig();
                mysrv.setImgname("norun");
            }
        } else {
            mysrv.setStatuMsg("关闭服务");
            mysrv.serviceConfig = service.getServiceConfig().getConfig();
            mysrv.setImgname("run");
            if (service.classNeedReload()) {
                mysrv.setImgname("runwithwarn");
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
            sb.append("SELECT * from services where host='").append(WebFactory.getInstanceName()).append("'");
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
        setAttribute("smsrvs", this);
        return new ActionForward("services.jsp");
    }

    //执行更新操作
    public Forward executeUpdate() {
        if (dotype == null) {
            return null;
        }
        dirlist = LogFactory.getLogNames();
        dirlist.remove("error");
        if (dotype.equalsIgnoreCase("添加服务") && this.getAccessPermission() > 1) { //添加操作
            try {
                con = this.getConnect(ConnectionFactory.systemSourceName);
                if (LogFactory.findLogger(logname) == null) {
                    logname =  LogFactory.SYSTEM_LOGNAME;
                }
                String sql = "insert into services(title, classname,params,logname,status,memo,createtime,host)values(?,?,?,?,?,?,?,?)";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, title);
                prest.setString(2, classname);
                prest.setString(3, params);
                prest.setString(4, logname);
                prest.setInt(5, status);
                prest.setString(6, memo);
                prest.setTimestamp(7, new Timestamp((new Date()).getTime()));
                prest.setString(8, WebFactory.getInstanceName());
                prest.executeUpdate();
                doMsg = "服务(" + title + ")添加成功";
                warn("添加服务:" + this.toString());
                warn(doMsg);
                fieldName = "title";
                fieldValue = title;
            } catch (Exception e1) {
                if (doMsg == null) {
                    doMsg = "检查字段值是否合法或存在重复！";
                }
                doMsg = "服务(" + title + ")添加失败:" + doMsg;
                trace(doMsg, e1);
                setAttribute("smsrv", this);
                return new ActionForward("serviceedit.jsp"); //返回编辑页
            }
        } else if (dotype.equalsIgnoreCase("修改服务") && this.getAccessPermission() > 1) { //更新
            try {
                con = this.getConnect(ConnectionFactory.systemSourceName);
                if (LogFactory.findLogger(logname) == null) {
                    logname =  LogFactory.SYSTEM_LOGNAME;
                }
                String sql = "update services set title=?, classname=?,params=?,logname=?,status=?,"
                        + "memo=?,createtime=? where id=? and host=?";
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, title);
                prest.setString(2, classname);
                prest.setString(3, params);
                prest.setString(4, logname);
                prest.setInt(5, status);
                prest.setString(6, memo);
                prest.setTimestamp(7, new Timestamp((new Date()).getTime()));
                prest.setInt(8, id);
                prest.setString(9, WebFactory.getInstanceName());
                prest.executeUpdate();
                if (ServiceFactory.getService(id) == null) {
                    doMsg = "服务(" + id + ")修改成功";
                } else {
                    doMsg = "服务(" + id + ")修改成功,重启服务后生效！";
                }
                warn("修改服务:" + this.toString());
                warn(doMsg);
            } catch (Exception e1) {
                if (doMsg == null) {
                    doMsg = "检查字段值是否合法或存在重复！";
                }
                doMsg = "服务(" + id + ")修改失败:" + doMsg;
                trace(doMsg, e1);
                setAttribute("smsrv", this);
                return new ActionForward("serviceedit.jsp"); //返回编辑页
            }
        } else if (dotype.equalsIgnoreCase("delete") && this.getAccessPermission() > 1) { //删除
            try {
                if (ServiceFactory.getService(id) != null) {
                    ServiceFactory.stopService(id);
                }
                if (ServiceFactory.getService(id) != null) {
                    throw new Exception("服务无法停止！");
                }
                con = this.getConnect(ConnectionFactory.systemSourceName);
                JdbcPerformer.executeUpdate(con, "delete from services where id=" + id + " and host='" + WebFactory.getInstanceName() + "'");
                doMsg = "删除服务:" + id;
                warn(doMsg);
            } catch (Exception e3) {
                doMsg = "服务(" + id + ")删除失败！";
                trace(doMsg, e3);
            }
        } else if (dotype.equalsIgnoreCase("close") && this.getAccessPermission() > 1) { //关闭服务
            setAttribute("id", id);
            int st = 0;
            try {
                st = ServiceFactory.getService(id).getServiceConfig().getStatus();
            } catch (Exception e) {
            }
            ServiceFactory.stopService(id);
            if (ServiceFactory.getService(id) == null) { //执行关闭
                setAttribute("doMsg", "服务" + id + "关闭完毕！");
                setAttribute("title", "启动服务");
                if (st == 2) {
                    setAttribute("imgname", "../img/pub/forbidden.gif");
                } else {
                    setAttribute("imgname", "../img/pub/norun.gif");
                }
            } else {
                setAttribute("doMsg", "服务" + id + "关闭失败！");
                setAttribute("title", "关闭服务");
                Service service = ServiceFactory.getService(id);
                if (service.classNeedReload()) {
                    setAttribute("imgname", "../img/pub/runwithwarn.gif");
                } else {
                    setAttribute("imgname", "../img/pub/run.gif");
                }
            }
            warn(getAttribute("doMsg").toString());
            return PlainForward.responseJson();
        } else if (dotype.equalsIgnoreCase("start") && this.getAccessPermission() > 1) { //启动
            if (id == 0) {
                return null;
            }
            setAttribute("id", id);
            con = this.getConnect(ConnectionFactory.systemSourceName);
            ServiceConfig sc = ServiceInitializer.initService(con, id);
            JdbcUtils.closeConnect(con);
            con=null;
            if (sc != null) {
                ServiceFactory.startService(sc);
                if (ServiceFactory.getService(id) == null) { //执行关闭
                    setAttribute("doMsg", "服务" + id + "启动失败！");
                    setAttribute("title", "启动服务");
                    setAttribute("imgname", "../img/pub/norun.gif");
                } else {
                    setAttribute("doMsg", "服务" + id + "启动完毕！");
                    setAttribute("title", "关闭服务");
                    setAttribute("imgname", "../img/pub/run.gif");
                }
                warn(getAttribute("doMsg").toString());
                return PlainForward.responseJson();
            }
        } else if (dotype.equalsIgnoreCase("getproperty")) {
            Service service = ServiceFactory.getService(id);
            if (service != null) {
                serviceConfig = service.getServiceConfig().getConfig();
            } else {
                doMsg = "服务" + id + "没启动，不能设置属性";
                warn(doMsg);
                return null;
            }
            if (serviceConfig == null || serviceConfig.getProperties().isEmpty()) {
                doMsg = "服务" + id + "不能设置属性";
                warn(doMsg);
            } else {
                this.setTitle(service.getServiceConfig().getTitle());
                setAttribute("smsrv", this);
                return new ActionForward("serviceproperty.jsp");
            }
        } else if ((dotype.equalsIgnoreCase("设置属性") || dotype.equalsIgnoreCase("保存属性")) && this.getAccessPermission() > 1) {
            Service service = ServiceFactory.getService(id);
            if (service != null) {
                serviceConfig = service.getServiceConfig().getConfig();
            } else {
                doMsg = "服务" + id + "没启动，不能设置属性";
                warn(doMsg);
                return null;
            }
            if (serviceConfig == null || serviceConfig.getProperties().isEmpty()) {
                doMsg = "服务" + id + "不能设置属性";
                warn(doMsg);
            } else {
                try {
                    ServiceFactory.setService(service);
                    Enumeration<String> e = serviceConfig.getAttributeNames();
                    while (e.hasMoreElements()) {
                        String key = e.nextElement();
                        String values = this.getRequest().getParameter(key);
                        if (values != null) {
                            serviceConfig.setAttributeValue(key, values);
                        }
                    }
                    doMsg = "服务" + id + "属性设置完毕！";
                    if (dotype.equalsIgnoreCase("保存属性")) {
                        try {
                            con = this.getConnect(ConnectionFactory.systemSourceName);
                            service.getServiceConfig().saveConfig(con);//需要写日志
                            doMsg = "服务" + id + "属性保存完毕！";
                        } catch (Exception e2) {
                            doMsg = "服务" + id + "属性保存失败！";
                        }
                    }
                    warn(doMsg);
                } catch (Exception e) {
                    doMsg = "服务" + id + "属性设置失败！";
                    error(doMsg + e.getMessage());
                } finally {
                    ServiceFactory.removeService();
                }
                this.setTitle(service.getServiceConfig().getTitle());
                setAttribute("smsrv", this);
                return new ActionForward("serviceproperty.jsp");
            }
        } else if (dotype.equalsIgnoreCase("getmethod")) { //新建
            Service service = ServiceFactory.getService(id);
            if (service != null) {
                serviceConfig = service.getServiceConfig().getConfig();
            } else {
                doMsg = "服务" + id + "没启动，不能执行方法";
                warn(doMsg);
                return null;
            }
            if (serviceConfig == null || serviceConfig.getMethods().isEmpty()) {
                doMsg = "服务" + id + "不能执行方法";
                warn(doMsg);
            } else {
                this.setTitle(service.getServiceConfig().getTitle());
                setAttribute("smsrv", this);
                return new ActionForward("servicemethod.jsp");
            }
        } else if (dotype.equalsIgnoreCase("呼叫此方法") && this.getAccessPermission() > 1) {
            Service service = ServiceFactory.getService(id);
            if (service != null) {
                serviceConfig = service.getServiceConfig().getConfig();
            } else {
                doMsg = "服务" + id + "没启动，不能执行方法";
                warn(doMsg);
                return null;
            }
            if (serviceConfig == null || serviceConfig.getMethods().isEmpty()) {
                doMsg = "服务" + id + "不能执行方法";
            } else {
                ServiceFactory.setService(service);
                Method method = serviceConfig.getMethod(statuMsg);
                if (method == null) {
                    doMsg = "服务" + id + "中找不到方法" + statuMsg;
                    warn(doMsg);
                } else {
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
                        String Msg = serviceConfig.callMethod(method).toString();
                        if (Msg != null) {
                            Msg = "呼叫返回:\r\n" + Msg;
                        }
                        String[] ss = Msg.split("\r\n");
                        rows = Arrays.asList(ss);
                        info("服务" + id + "方法" + statuMsg + "呼叫成功！");
                    } catch (Exception e) {
                        rows.add("呼叫失败!");
                        error("服务" + id + "方法" + statuMsg + "呼叫失败！"
                                + e.getMessage());
                    } finally {
                        ServiceFactory.removeService();
                    }
                    this.setTitle(service.getServiceConfig().getTitle());
                    setAttribute("smsrv", this);
                    return new ActionForward("servicemethod.jsp");
                }
            }
        } else if (dotype.equalsIgnoreCase("new") && this.getAccessPermission() > 1) { //新建
            ServiceManager mysrv = new ServiceManager();
            mysrv.setDotype("添加服务");
            mysrv.setCreatetime(TextUtils.Date2String(new Date(), "yyyy-MM-dd HH:mm:ss"));
            setAttribute("smsrv", mysrv);
            mysrv.dirlist = dirlist;
            return new ActionForward("serviceedit.jsp"); //返回编辑页
        } else if (dotype.equalsIgnoreCase("edit")) { //修改
            ResultSet crs;
            try {
                con = this.getConnect(ConnectionFactory.systemSourceName);
                crs = JdbcPerformer.executeQuery(con,
                        "select * from services where id=" + id + " and host='" + WebFactory.getInstanceName() + "'");
                if (crs.next()) {
                    ServiceManager mysrv = assembler(crs);
                    mysrv.dirlist = dirlist;
                    mysrv.setDotype("修改服务");
                    getRequest().setAttribute("smsrv", mysrv);
                    return new ActionForward("serviceedit.jsp"); //返回编辑页
                } else {
                    doMsg = "找不到服务ID" + id;
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
        Forward af = executeUpdate();
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
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
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
     * @return the createtime
     */
    public String getCreatetime() {
        return createtime;
    }

    /**
     * @param createtime the createtime to set
     */
    public void setCreatetime(String createtime) {
        this.createtime = createtime;
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
     * @return the statuMsg
     */
    public String getStatuMsg() {
        return statuMsg;
    }

    /**
     * @param statuMsg the statuMsg to set
     */
    public void setStatuMsg(String statuMsg) {
        this.statuMsg = statuMsg;
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
     * @return the serviceConfig
     */
    public Config getServiceConfig() {
        return serviceConfig;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id).append("\r\n");
        sb.append("title=").append(title).append("\r\n");
        sb.append("classname=").append(classname).append("\r\n");
        sb.append("params=").append(params).append("\r\n");
        sb.append("logname=").append(logname).append("\r\n");
        sb.append("status=").append(status);
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
