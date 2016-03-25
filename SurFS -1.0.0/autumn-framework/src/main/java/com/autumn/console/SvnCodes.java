package com.autumn.console;

import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.sql.SmRowSet;
import com.autumn.core.svn.ActionMessage;
import com.autumn.core.svn.RepositoryTree;
import com.autumn.core.svn.WCopyAction;
import com.autumn.core.svn.WCopyActionMgr;
import com.autumn.core.web.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONObject;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: SVN代码同步管理编辑</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class SvnCodes extends Action {

    private String id = "0";
    private String title = "";
    private String url = "";
    private String dirName = "";
    private String userName = "";
    private String passWord = "";
    private int dirType = 0; //目录类型，0.java源码 1web目录（jsp）
    private String message = "";
    private String[] modified = null;
    private String[] updated = null;
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
    private List<SvnCodes> rows = new ArrayList<SvnCodes>();
    private Connection con = null;

    /**
     * ResultSet-〉SvnCodes
     *
     * @param crs
     * @return
     * @throws Exception
     */
    private SvnCodes assembler(ResultSet crs) throws Exception {
        SvnCodes mysrv = (SvnCodes) this.assemble(crs);
        String msg = mysrv.getMessage();
        mysrv.setMessage("");
        WCopyAction wa = WCopyActionMgr.get("" + mysrv.getId());
        if (wa == null) {
            if (msg == null || msg.trim().equals("")) {
                mysrv.setMessage("检查更新");
            } else {
                mysrv.setMessage("查看状态...");
            }
        } else {
            mysrv.setMessage(wa.actionTypeString());
        }
        String svnurl = mysrv.getUrl();
        int index = svnurl.indexOf("://");
        if (index > 0) {
            svnurl = svnurl.substring(0, index + 3) + mysrv.getUserName() + ":"
                    + mysrv.getPassWord() + "@" + svnurl.substring(index + 3);
        } else {
            svnurl = mysrv.getUserName() + ":"
                    + mysrv.getPassWord() + "@" + svnurl;
        }
        mysrv.setUrl(svnurl);
        return mysrv;
    }

    //执行查询操作
    public Forward executeQuery() {
        ResultSet crs;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT id,title,url,dirName,userName,passWord,dirType,message FROM  svncodes  where host='");
            sb.append(WebFactory.getInstanceName());
            sb.append("'");
            if (fieldName == null) {
                fieldName = "";
            }
            if (fieldValue == null) {
                fieldValue = "";
            }
            if (fieldName.equalsIgnoreCase("srcdir")) {
                fieldValue = "0";
            }
            if (fieldName.equalsIgnoreCase("webdir")) {
                fieldValue = "1";
            }
            if ((!fieldName.trim().equals("")) && (!fieldValue.trim().equals(""))) {
                if (fieldName.trim().equalsIgnoreCase("id")) {
                    sb.append(" and id=");
                    sb.append(fieldValue);
                } else if (fieldName.trim().equalsIgnoreCase("srcdir")) {
                    sb.append(" and dirType=0");
                } else if (fieldName.trim().equalsIgnoreCase("webdir")) {
                    sb.append(" and dirType=1");
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
            doMsg = "查询失败,可能输入的查询条件违法";
        }
        setAttribute("smcodes", this);
        if (getRequest().getParameter("ajax") != null && getRequest().getParameter("ajax").equalsIgnoreCase("true")) {
            return ajaxresponse();
        } else {
            return new ActionForward("svncodes.jsp");
        }
    }

    /**
     * ajax操作返回
     *
     * @return
     */
    private Forward ajaxresponse() {
        JSONObject json = new JSONObject();
        int count = rows.size();
        String[] ids = new String[count];
        String[] contents = new String[count];
        for (int ii = 0; ii < count; ii++) {
            SvnCodes sc = rows.get(ii);
            String value = sc.getMessage();
            String content;
            if (value.equalsIgnoreCase("检查更新")) {
                content = "<a href=\"svncodes.do?dotype=check&id=" + sc.getId()
                        + "\">检查更新</a>";
            } else if (value.equalsIgnoreCase("查看状态...")) {
                content = "<a href=\"svncodes.do?dotype=view&id=" + sc.getId()
                        + "\">查看状态...</a>";
            } else {
                content = " <span class=\"redtitle\">" + value + "</span>";
            }
            ids[ii] = "content" + sc.getId();
            contents[ii] = content;
        }
        json.put("ids", ids);
        json.put("contents", contents);
        return new PlainForward(json);
    }

    //执行更新操作
    public ActionForward executeUpdate() {
        String addtion = " and (host='" + WebFactory.getInstanceName() + "')";
        if (dotype == null) {
            return null;
        }
        if (dotype.equalsIgnoreCase("创建工程") && this.getAccessPermission() > 1) { //添加操作
            try {
                if (title.equals("")) {
                    doMsg = "没有填写标题！";
                    throw new Exception("");
                }
                String filename;
                if (dirType == 0) {
                    filename = Initializer.getWebpath() + "WEB-INF" + File.separator + "src" + File.separator + dirName;
                } else {
                    filename = Initializer.getWebpath() + dirName;
                }
                File f = new File(filename);
                if (f.exists()) {
                    doMsg = "目录‘" + filename + "’已经存在，请删除目录或重设目录名！";
                    throw new Exception("");
                }
                filename = filename.substring(Initializer.getWebpath().length());
                RepositoryTree rt = new RepositoryTree(url, userName, passWord);
                if (!rt.exists()) {
                    doMsg = "SVN地址无效！";
                    throw new Exception("");
                }
                String sql = "insert into svncodes(Host,title,Url,dirName,"
                        + "Username,Password,dirType)values(?,?,?,?,?,?,?)";
                PreparedStatement prest = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                prest.setString(1, WebFactory.getInstanceName());
                prest.setString(2, title);
                prest.setString(3, url);
                prest.setString(4, filename);
                prest.setString(5, userName);
                prest.setString(6, passWord);
                prest.setInt(7, dirType);
                prest.executeUpdate();
                ResultSet rs = prest.getGeneratedKeys();
                rs.next();
                id = String.valueOf(rs.getInt(1));
                WCopyAction wa = new WCopyAction(id);
                wa.setActionType(WCopyAction.CHECK);
                wa.setUserName(userName);
                wa.setPassWord(passWord);
                wa.setRepositoryUrl(url);
                wa.setWorkingCopyPath(f.getAbsolutePath());
                WCopyActionMgr.put(wa);
                doMsg = "工程‘" + title + "’创建成功,正在执行导入...";
                warn(doMsg);
                warn("创建工程:" + this.toString());
            } catch (Exception e1) {
                if (doMsg == null) {
                    doMsg = "工程‘" + title + "’创建失败！";
                } else {
                    doMsg = "工程‘" + title + "’创建失败：" + doMsg;
                }
                error(doMsg + e1.getMessage());
                setAttribute("smcode", this);
                return new ActionForward("svncodeedit.jsp"); //返回编辑页
            }
        } else if (dotype.equalsIgnoreCase("delete") && this.getAccessPermission() > 1) { //删除
            try {
                int res = JdbcPerformer.executeUpdate(con,
                        "delete from svncodes where id=" + id + " " + addtion);
                if (res == 0) {
                    doMsg = "没有权限对工程代码 " + id + " 进行删除操作！";
                } else {
                    doMsg = "工程代码 " + id + " 删除成功！";
                }
                warn(doMsg);
                warn("删除工程:id=" + id);
            } catch (Exception e3) {
                doMsg = "工程代码 " + id + " 删除失败！";
                error(doMsg + e3.getMessage());
            }
        } else if (dotype.equalsIgnoreCase("new") && this.getAccessPermission() > 1) { //新建
            SvnCodes mysrv = new SvnCodes();
            mysrv.setDotype("创建工程");
            setAttribute("smcode", mysrv);
            return new ActionForward("svncodeedit.jsp");
        } else if ((dotype.equalsIgnoreCase("check") || dotype.equalsIgnoreCase("重新检查更新")) && this.getAccessPermission() > 1) { //新建
            ResultSet crs;
            WCopyAction wa = WCopyActionMgr.get("" + id);
            if (wa != null) {
                doMsg = "不能执行检查更新操作,id:" + id + "," + wa.actionTypeString();
                warn(doMsg);
                return null;
            }
            try {
                crs = JdbcPerformer.executeQuery(con,
                        "select username,password,url,dirname from svncodes where id="
                        + id + addtion);
                if (crs.next()) {
                    wa = new WCopyAction(id);
                    wa.setActionType(WCopyAction.CHECK);
                    wa.setUserName(crs.getString("username"));
                    wa.setPassWord(crs.getString("password"));
                    wa.setRepositoryUrl(crs.getString("url"));
                    String filename = Initializer.getWebpath() + crs.getString("dirname");
                    wa.setWorkingCopyPath(filename);
                    WCopyActionMgr.put(wa);
                    doMsg = "工程ID=" + id + "准备执行检查更新操作";
                } else {
                    doMsg = "没有权限对工程ID=" + id + "执行检查更新操作";
                }
                warn(doMsg);
            } catch (Exception d) {
                doMsg = "工程Id=" + id + "执行检查更新错误！";
                error(doMsg + d.getMessage());
            }
        } else if (dotype.equalsIgnoreCase("view")) { //新建
            ResultSet crs;
            try {
                crs = JdbcPerformer.executeQuery(con,
                        "select message from svncodes where id="
                        + id + addtion);
                if (crs.next()) {
                    String ss = JdbcUtils.getResultSetStringValue(crs, "message");
                    if (ss == null || ss.trim().equals("")) {
                        doMsg = "工程ID=" + id + "暂时还没有查找到更新";
                    } else {
                        try {
                            ActionMessage am = ActionMessage.parse(ss);
                            setAttribute("actionmessage", am);
                            setAttribute("smcode", this);
                            return new ActionForward("svncodeup.jsp");
                        } catch (Exception e) {
                            WCopyAction.clearMessage(con, id);
                            doMsg = "数据校验错误，已修复！";
                            error(doMsg + e.getMessage());
                        }
                    }
                } else {
                    doMsg = "没有权限查看工程ID=" + id + "状态";
                }
            } catch (Exception d) {
                doMsg = "工程Id=" + id + "查看更新错误！";
                error(doMsg + d.getMessage());
            }
        } else if ((dotype.equalsIgnoreCase("提交")
                || dotype.equalsIgnoreCase("提交全部")
                || dotype.equalsIgnoreCase("还原")
                || dotype.equalsIgnoreCase("还原全部")
                || dotype.equalsIgnoreCase("更新")
                || dotype.equalsIgnoreCase("更新全部"))&&this.getAccessPermission() > 1) { //新建
            ResultSet crs;
            WCopyAction wa = WCopyActionMgr.get("" + id);
            if (wa != null) {
                doMsg = "不能执行提交、还原、更新等操作,id:" + id + "," + wa.actionTypeString();
                warn(doMsg);
                return null;
            }
            try {
                crs = JdbcPerformer.executeQuery(con,
                        "select message,username,password,url,dirname from svncodes where id="
                        + id + addtion);
                if (crs.next()) {
                    String ss = JdbcUtils.getResultSetStringValue(crs, "message");
                    if (ss == null || ss.trim().equals("")) {
                        doMsg = "工程ID=" + id + "暂时还没有查找到更新";
                    } else {
                        try {
                            ActionMessage am = ActionMessage.parse(ss);
                            if (dotype.equalsIgnoreCase("更新全部")
                                    || dotype.equalsIgnoreCase("还原全部")
                                    || dotype.equalsIgnoreCase("提交全部")) {
                                wa = new WCopyAction(id);
                                wa.setUserName(crs.getString("username"));
                                wa.setPassWord(crs.getString("password"));
                                wa.setRepositoryUrl(crs.getString("url"));
                                String filename = Initializer.getWebpath() + crs.getString("dirname");
                                wa.setWorkingCopyPath(filename);
                                if (dotype.equalsIgnoreCase("更新全部")) {
                                    wa.setActionType(WCopyAction.UPDATE);
                                    warn("工程ID=" + id + "准备执行更新全部");
                                } else if (dotype.equalsIgnoreCase("还原全部")) {
                                    wa.setActionType(WCopyAction.ROLLBACK);
                                    warn("工程ID=" + id + "准备执行还原全部");
                                } else {
                                    wa.setActionType(WCopyAction.COMMIT);
                                    warn("工程ID=" + id + "准备执行提交全部");
                                }
                                wa.setMsg(am);
                                WCopyActionMgr.put(wa);
                            } else {
                                if (((modified == null || modified.length == 0)
                                        && (dotype.equalsIgnoreCase("提交")
                                        || dotype.equalsIgnoreCase("还原")))
                                        || ((updated == null || updated.length == 0)
                                        && (dotype.equalsIgnoreCase("更新")))) {
                                    setAttribute("actionmessage", am);
                                    setAttribute("smcode", this);
                                    return new ActionForward(
                                            "smconsole/smcodeup.jsp");
                                } else {
                                    wa = new WCopyAction(id);
                                    wa.setUserName(crs.getString("username"));
                                    wa.setPassWord(crs.getString("password"));
                                    wa.setRepositoryUrl(crs.getString("url"));
                                    String filename = Initializer.getWebpath() + crs.getString("dirname");
                                    wa.setWorkingCopyPath(filename);
                                    if (dotype.equalsIgnoreCase("更新")) {
                                        wa.setActionType(WCopyAction.UPDATE);
                                        wa.setUpdated(updated);
                                        warn("工程ID=" + id + "准备执行更新");
                                    } else {
                                        if (dotype.equalsIgnoreCase("提交")) {
                                            wa.setActionType(WCopyAction.COMMIT);
                                            warn("工程ID=" + id + "准备执行提交");
                                        } else {
                                            wa.setActionType(WCopyAction.ROLLBACK);
                                            warn("工程ID=" + id + "准备执行还原");
                                        }
                                        wa.setModified(modified);
                                    }
                                    wa.setMsg(am);
                                    WCopyActionMgr.put(wa);
                                }
                            }
                        } catch (Exception e) {
                            WCopyAction.clearMessage(con, id);
                            doMsg = "数据校验错误，已修复！";
                            error(doMsg + e.getMessage());
                        }
                    }
                } else {
                    doMsg = "没有权限操作工程ID=" + id;
                }
            } catch (Exception d) {
                doMsg = "工程Id=" + id + "执行更新还原时错误！";
                error(doMsg + d.getMessage());
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
        if (dotype != null && dotype.equalsIgnoreCase("创建工程")) {
            fieldName = null;
            pageNum = 1;
        }
        return executeQuery();
    }

    public int getDirType() {
        return dirType;
    }

    public String getDoMsg() {
        return doMsg;
    }

    public String getDotype() {
        return dotype;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getPageNum() {
        return pageNum;
    }

    public String getPassWord() {
        return passWord;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getRowCount() {
        return rowCount;
    }

    public List<SvnCodes> getRows() {
        return rows;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getDirName() {
        return dirName;
    }

    public String[] getModified() {
        return modified;
    }

    public String[] getUpdated() {
        return updated;
    }

    public void setDirType(int dirType) {
        this.dirType = dirType;
    }

    public void setDoMsg(String doMsg) {
        this.doMsg = doMsg;
    }

    public void setDotype(String dotype) {
        this.dotype = dotype;
    }

    @SessionMethod
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @SessionMethod
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @SessionMethod
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    @SessionMethod
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public void setModified(String[] modified) {
        this.modified = modified;
    }

    public void setUpdated(String[] updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id).append("\r\n");
        sb.append("title=").append(title).append("\r\n");
        sb.append("url=").append(url).append("\r\n");
        sb.append("dirName=").append(dirName).append("\r\n");
        sb.append("userName=").append(userName).append("\r\n");
        sb.append("passWord=").append(passWord).append("\r\n");
        sb.append("dirType=").append(dirType);
        return sb.toString();
    }
}
