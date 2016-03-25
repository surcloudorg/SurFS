package com.autumn.console;

import com.autumn.core.log.Level;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.LogProperties;
import com.autumn.core.log.Logger;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.SmRowSet;
import com.autumn.core.web.*;
import com.autumn.util.TextUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 日志属性编辑</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class LogManager extends Action {

    private String logname = LogFactory.SYSTEM_LOGNAME;
    private String level = "INFO";
    private String dateformatter = "[MM-dd HH:mm:ss]";
    private boolean outconsole = false;
    private boolean addclassname = false;
    private boolean addlevel = false;
    private String filter = "";
    private String warnclass = "";
    private String params = "";
    private int warninteral = 60 * 30;
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
    private List<LogManager> rows = new ArrayList<LogManager>();
    private Connection con = null;

    /**
     * ResultSet-〉LogManager
     *
     * @param crs
     * @return
     * @throws Exception
     */
    private LogManager assembler(ResultSet crs) throws Exception {
        LogManager mysrv = (LogManager) this.assemble(crs);
        int le = Integer.parseInt(mysrv.getLevel());
        mysrv.setLevel(Level.newstance(le).toString());
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
            sb.append("SELECT * from logcfg").append(" where host='").append(WebFactory.getInstanceName()).append("'");
            if (fieldName == null) {
                fieldName = "";
            }
            if (fieldValue == null) {
                fieldValue = "";
            }
            if ((!fieldName.trim().equals("")) && (!fieldValue.trim().equals(""))) {
                sb.append(" and ");
                sb.append(fieldName);
                sb.append(" like '%");
                sb.append(fieldValue);
                sb.append("%'");
            }
            SmRowSet myrs = new SmRowSet(con, sb.toString(), "logname");
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
        setAttribute("smlogs", this);
        return new ActionForward("smlogs.jsp");
    }

    /**
     * 执行更新操作
     *
     * @return ActionForward
     */
    public ActionForward executeUpdate() {
        if (dotype == null) {
            return null;
        }
        if (dotype.equalsIgnoreCase("修改配置")&&this.getAccessPermission() > 1) { //更新
            if (logname.equalsIgnoreCase("error")) {
                doMsg = logname + " 日志不允许修改";
                return null;
            }
            try {
                String sql = "update logcfg set dateformatter=?,filter=?,warnclass=?,warninteral=?,level=?,addlevel=?,"
                        + "addclassname=?,outconsole=?,params=? where logname=? and host=?";
                if (con.getMetaData().getDatabaseProductName().equalsIgnoreCase("oracle")) {
                    sql = "update logcfg set dateformatter=?,filter=?,warnclass=?,warninteral=?,logcfg.\"LEVEL\"=?,addlevel=?,"
                            + "addclassname=?,outconsole=?,params=? where logname=? and host=?";
                }
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, dateformatter);
                prest.setString(2, filter);
                prest.setString(3, warnclass);
                prest.setInt(4, warninteral);
                prest.setInt(5, Level.parseLevel(level).intValue());
                prest.setBoolean(6, addlevel);
                prest.setBoolean(7, addclassname);
                prest.setBoolean(8, outconsole);
                prest.setString(9, params);
                prest.setString(10, logname);
                prest.setString(11, WebFactory.getInstanceName());
                prest.executeUpdate();
                doMsg = logname + " 的日志设置成功";
                Logger log = LogFactory.findLogger(logname);
                if (log != null) {
                    LogProperties cfg = log.getProperties();
                    cfg.setAddClassName(addclassname);
                    cfg.setAddLevel(addlevel);
                    cfg.setDateformatter(dateformatter);
                    cfg.setFilter(filter.replaceAll("'", "''"));
                    cfg.setWarnClass(warnclass);
                    cfg.setLevel(Level.parseLevel(level).intValue());
                    cfg.setWarnInteral(warninteral);
                    cfg.setOutConsole(outconsole);
                    cfg.setParams(params);
                }
                warn(doMsg);
                warn("修改日志:" + this.toString());
            } catch (Exception e2) { //返回编辑页
                doMsg = logname + " 的日志设置失败！";
                error(doMsg + e2.getMessage());
                setAttribute("smlog", this);
                return new ActionForward("smlogedit.jsp");
            }
        } else if (dotype.equalsIgnoreCase("新建目录")&&this.getAccessPermission() > 1) {
            if (LogFactory.findLogger(logname) != null) {
                doMsg = logname + " 日志已经存在不能创建";
                setAttribute("smlog", this);
                return new ActionForward("smlogedit.jsp");
            }
            try {
                if (!TextUtils.isValidFileName(logname)) {
                    throw new Exception("目录名只能是字母数字下划线组成的字符串");
                }
                String sql = "insert into logcfg(dateformatter,filter,warnclass,warninteral,level,addlevel,"
                        + "addclassname,outconsole,logname,host,params) values(?,?,?,?,?,?,?,?,?,?,?)";
                if (con.getMetaData().getDatabaseProductName().equalsIgnoreCase("oracle")) {
                    sql = "insert into logcfg(dateformatter,filter,warnclass,warninteral,logcfg.\"LEVEL\",addlevel,"
                            + "addclassname,outconsole,logname,host,params) values(?,?,?,?,?,?,?,?,?,?,?)";
                }
                PreparedStatement prest = con.prepareStatement(sql);
                prest.setString(1, dateformatter);
                prest.setString(2, filter);
                prest.setString(3, warnclass);
                prest.setInt(4, warninteral);
                prest.setInt(5, Level.parseLevel(level).intValue());
                prest.setBoolean(6, addlevel);
                prest.setBoolean(7, addclassname);
                prest.setBoolean(8, outconsole);
                prest.setString(9, logname);
                prest.setString(10, WebFactory.getInstanceName());
                prest.setString(11, params);
                prest.executeUpdate();
                doMsg = logname + " 的日志添加成功";
                LogProperties cfg = new LogProperties(logname);
                cfg.setAddClassName(addclassname);
                cfg.setAddLevel(addlevel);
                cfg.setDateformatter(dateformatter);
                cfg.setFilter(filter.replaceAll("'", "''"));
                cfg.setWarnClass(warnclass);
                cfg.setLevel(Level.parseLevel(level).intValue());
                cfg.setWarnInteral(warninteral);
                cfg.setOutConsole(outconsole);
                cfg.setParams(params);
                LogFactory.addLogger(cfg);
                warn(doMsg);
                warn("新建日志:" + this.toString());
            } catch (Exception e2) { //返回编辑页
                doMsg = logname + " 日志添加失败！";
                error(doMsg + e2.getMessage());
                setAttribute("smlog", this);
                return new ActionForward("smlogedit.jsp");
            }
        } else if (dotype.equalsIgnoreCase("delete")&&this.getAccessPermission() > 1) { //修改
            String sql = "delete from logcfg where logname= '" + logname + "' and logname<>'system' and logname<>'error' and host='" 
                    + WebFactory.getInstanceName() + "'";
            try {
                int rc = con.createStatement().executeUpdate(sql);
                if (rc > 0) {
                    LogFactory.removeLogger(logname);
                    doMsg = "删除目录: " + logname + "成功！";
                    warn("删除日志:logname=" + logname);
                } else {
                    doMsg = "没有权限删除目录: " + logname;
                }
            } catch (Exception d) {
                doMsg = "删除目录: " + logname + "失败！";
                error(doMsg + d.getMessage());
            }

        } else if (dotype.equalsIgnoreCase("edit")) { //修改
            try {
                LogManager smlog;
                String sql = "SELECT * from logcfg where logname= '" + logname + "' and host='" + WebFactory.getInstanceName() + "'";
                ResultSet crs = con.createStatement().executeQuery(sql);
                if (crs.next()) {
                    smlog = assembler(crs);
                    smlog.setDotype("修改配置");
                    setAttribute("smlog", smlog);
                    return new ActionForward("smlogedit.jsp");
                } else {
                    doMsg = "目录不存在或没有权限查看目录配置: " + logname;
                    throw new Exception(doMsg);
                }
            } catch (Exception d) {
                doMsg = "查看目录配置: " + logname + "失败！";
                error(doMsg + d.getMessage());
            }
        } else if (dotype.equalsIgnoreCase("new")&&this.getAccessPermission() > 1) { //修改
            LogManager smlog = new LogManager();
            smlog.setDotype("新建目录");
            setAttribute("smlog", smlog);
            return new ActionForward("smlogedit.jsp");
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
     * @return the logLevel
     */
    public String getLevel() {
        return level;
    }

    /**
     * @param logLevel the logLevel to set
     */
    public void setLevel(String logLevel) {
        this.level = logLevel;
    }

    /**
     * @return the dateformatter
     */
    public String getDateformatter() {
        return dateformatter;
    }

    /**
     * @param dateformatter the dateformatter to set
     */
    public void setDateformatter(String dateformatter) {
        this.dateformatter = dateformatter;
    }

    /**
     * @return the outconsole
     */
    public boolean isOutconsole() {
        return outconsole;
    }

    /**
     * @param outconsole the outconsole to set
     */
    public void setOutconsole(boolean outconsole) {
        this.outconsole = outconsole;
    }

    /**
     * @return the addclassname
     */
    public boolean isAddclassname() {
        return addclassname;
    }

    /**
     * @param addclassname the addclassname to set
     */
    public void setAddclassname(boolean addclassname) {
        this.addclassname = addclassname;
    }

    /**
     * @return the addlevel
     */
    public boolean isAddlevel() {
        return addlevel;
    }

    /**
     * @param addlevel the addlevel to set
     */
    public void setAddlevel(boolean addlevel) {
        this.addlevel = addlevel;
    }

    /**
     * @return the filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * @return the warnclass
     */
    public String getWarnclass() {
        return warnclass;
    }

    /**
     * @param warnclass the warnclass to set
     */
    public void setWarnclass(String warnclass) {
        this.warnclass = warnclass;
    }

    /**
     * @return the warninteral
     */
    public int getWarninteral() {
        return warninteral;
    }

    /**
     * @param warninteral the warninteral to set
     */
    public void setWarninteral(int warninteral) {
        this.warninteral = warninteral;
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
    public List<LogManager> getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(List<LogManager> rows) {
        this.rows = rows;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("logname=").append(logname).append("\r\n");
        sb.append("dateformatter=").append(dateformatter).append("\r\n");
        sb.append("filter=").append(filter).append("\r\n");
        sb.append("warnclass=").append(warnclass).append("\r\n");
        sb.append("warninteral=").append(warninteral).append("\r\n");
        sb.append("level=").append(level).append("\r\n");
        sb.append("addlevel=").append(addlevel).append("\r\n");
        sb.append("addclassname=").append(addclassname).append("\r\n");
        sb.append("outconsole=").append(outconsole).append("\r\n");
        sb.append("params=").append(params);
        return sb.toString();
    }
}
