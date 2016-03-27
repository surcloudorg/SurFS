/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.console;

import com.autumn.core.soap.SoapContext;
import com.autumn.core.soap.SoapFactory;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcPerformer;
import com.autumn.core.sql.SmRowSet;
import com.autumn.core.web.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 登陆用户管理</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class UserManager extends Action {

    private String id = "0";
    private String userName = "";
    private String passWord = "";
    private String realname = "";
    private String userGroup = null;
    private String mobile = "";
    private String email = null;
    private String permission = "2222222222";
    private int mailPermission = 0;
    private int stimeOut = 0;
    private boolean isActive = false;
    private String createtime = "";
    private String logintime = "";
    private String memo = "";
    private int dirId = 0;
    private int soapId = 0;
    private String iplist = "";
    private String extParams = "";
    private String loginPath = "";
    private TreeMap<String, String> dirList = null;
    private TreeMap<String, String> soapList = null;
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
    private List<UserManager> rows = new ArrayList<UserManager>();
    private Connection con = null;

    /**
     * ResultSet--〉UserManager
     *
     * @param crs
     * @return
     * @throws Exception
     */
    private UserManager assembler(ResultSet crs) throws Exception {
        UserManager mysrv = (UserManager) assemble(crs);
        if (mysrv.getDirId() != -1) {
            WebDirectory wc = WebFactory.getWebDirectory(mysrv.getDirId());
            if (wc != null) {
                mysrv.loginPath = wc.getDirName() + "--" + wc.getTitle();
            }
        } else {
            SoapContext sc = SoapFactory.getSoapContext(mysrv.getSoapId());
            if (sc != null) {
                mysrv.loginPath = "services--" + sc.getTitle();
            }
        }
        return mysrv;
    }

    //执行查询操作
    public ActionForward executeQuery() {
        ResultSet crs;
        try {
            StringBuilder sb = new StringBuilder();
            Set<String> en = dirList.keySet();
            for (String dirid : en) {
                if (!dirid.equals("-1")) {
                    sb.append(dirid).append(",");
                }
            }
            String addtion1 = sb.toString();
            sb = new StringBuilder();
            en = soapList.keySet();
            for (String ss : en) {
                sb.append(ss).append(",");
            }
            String addtion2 = sb.toString();
            sb = new StringBuilder("SELECT * FROM webuser");
            if (!addtion1.equals("")) {
                sb.append(" where (dirid in(");
                sb.append(addtion1);
                sb.append("-99)");
            } else {
                sb.append(" where (dirid=-99");
            }
            if (!addtion2.equals("")) {
                sb.append(" or (dirid=-1 and soapid in(");
                sb.append(addtion2);
                sb.append("-99)))");
            } else {
                sb.append(" or (dirid=-1 and soapid=0))");
            }
            if (fieldName == null) {
                fieldName = "";
            }
            if ((!fieldName.trim().equals("")) && (fieldValue != null)) {
                if (fieldName.trim().equalsIgnoreCase("id")) {
                    if (!fieldValue.trim().equals("")) {
                        sb.append(" and ").append(fieldName);
                        sb.append("=");
                        sb.append(fieldValue);
                    }
                } else if (fieldName.trim().equalsIgnoreCase("dirname")) {
                    sb.append(" and ((dirid in(select id from webdirectory");
                    sb.append(" where dirname like '%");
                    sb.append(fieldValue);
                    sb.append("%'");
                    sb.append(" or title like '%");
                    sb.append(fieldValue);
                    sb.append("%'))");
                    sb.append(" or (soapid in(select id from soaps");
                    sb.append(" where servicename like '%");
                    sb.append(fieldValue);
                    sb.append("%'");
                    sb.append(" or title like '%");
                    sb.append(fieldValue);
                    sb.append("%')))");
                } else if (fieldName.trim().equalsIgnoreCase("active")) {
                    sb.append(" and isactive=1");
                } else if (fieldName.trim().equalsIgnoreCase("noactive")) {
                    sb.append(" and isactive=0");
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
                    UserManager smuser = assembler(crs);
                    if (smuser.getUserName().equalsIgnoreCase("admin")) {
                        continue;
                    }
                    rows.add(smuser);
                }
            }
        } catch (Exception e) {
            error("查询失败:" + e);
            doMsg = "查询失败,可能输入的查询条件违法";
        }
        setAttribute("smusers", this);
        return new ActionForward("users.jsp");
    }

    /**
     * 检查参数
     *
     * @throws Exception
     */
    private void checkParams() throws Exception {
        if (userName.equals("")) {
            doMsg = "没有填写用户名！";
            throw new Exception("");
        }
        if (passWord.equals("")) {
            doMsg = "没有填写用户密码！";
            throw new Exception("");
        }
    }

    //执行更新操作
    public ActionForward executeUpdate() {
        dirList = WebFactory.getWebDirs(true);
        soapList = SoapFactory.getSoapTitles();
        if (!soapList.isEmpty()) {
            WebDirectory wc = WebFactory.getWebDirectory(-1);
            if (wc != null) {
                dirList.put(wc.getId() + "", "0." + wc.getDirName() + "--" + wc.getTitle());
            }
        }
        if (dotype == null) {
            return null;
        }
        boolean chkdir = false;
        Set<String> en = dirList.keySet();
        for (String ss : en) {
            if (ss.equals(dirId + "")) {
                chkdir = true;
                break;
            }
        }
        boolean chksoap = false;
        en = soapList.keySet();
        for (String str : en) {
            if (str.equals(soapId + "")) {
                chksoap = true;
                break;
            }
        }
        String add2 = "((dirid in(select id from webdirectory where host='"
                + WebFactory.getInstanceName()
                + "')) or (dirid=0) or (dirid=-1 and soapid in(select id from soaps where host='"
                + WebFactory.getInstanceName() + "')))";
        if (dotype.equalsIgnoreCase("添加账号")&&this.getAccessPermission() > 1) { //添加操作
            try {
                checkParams();
                if (!chkdir) {
                    doMsg = "没有权限添加目录ID是" + dirId + "账号！";
                    throw new Exception("");
                }
                if (dirId == -1) { //soap
                    if (!chksoap) {
                        doMsg = "没有权限添加Soap服务ID是" + soapId + "账号！";
                        throw new Exception("");
                    }
                } else {
                    soapId = 0;
                }
                if (userName.equalsIgnoreCase("admin")) {
                    doMsg = "不能添加用户名是\"admin\"的账号！";
                    throw new Exception("");
                }
                LoginUser log = new LoginUser();
                log.setUserName(userName);
                log.setPassWord(passWord);
                log.setRealname(realname);
                log.setUserGroup(userGroup);
                log.setMobile(mobile);
                log.setEmail(email);
                log.setPermission(permission);
                log.setStimeOut(new Integer(stimeOut));
                log.setIsActive(Boolean.valueOf(isActive));
                log.setDirid(new Integer(dirId));
                log.setSoapid(new Integer(soapId));
                log.setNote(memo);
                log.setIp(iplist);
                log.setExtParam(extParams);
                log.insertLoginUser(con);
                doMsg = "账号(" + userName + ")添加成功";
                warn("添加账号:" + log.toString());
                warn(doMsg);
            } catch (Exception e1) {
                if (doMsg == null) {
                    doMsg = "检查字段值是否合法或存在重复！";
                }
                doMsg = "账号(" + userName + ")添加失败:" + doMsg;
                error(doMsg + e1.getMessage());
                setAttribute("smuseredit", this);
                return new ActionForward("useredit.jsp"); //返回编辑页
            }
        } else if (dotype.equalsIgnoreCase("修改账号")&&this.getAccessPermission() > 1) { //更新
            try {
                checkParams();
                if (!chkdir) {
                    doMsg = "没有权限修改目录ID是" + dirId + "账号！";
                    throw new Exception("");
                }
                if (dirId == -1) { //soap
                    if (!chksoap) {
                        doMsg = "没有权限修改Soap服务ID是" + soapId + "账号！";
                        throw new Exception("");
                    }
                } else {
                    soapId = 0;
                }
                if (userName.equalsIgnoreCase("admin")) {
                    doMsg = "\"admin\"的账号不能修改！";
                    throw new Exception("");
                }
                LoginUser log = new LoginUser();
                log.setId(new Integer(id));
                log.setUserName(userName);
                log.setPassWord(passWord);
                log.setRealname(realname);
                log.setUserGroup(userGroup);
                log.setMobile(mobile);
                log.setEmail(email);
                log.setPermission(permission);
                log.setStimeOut(new Integer(stimeOut));
                log.setIsActive(Boolean.valueOf(isActive));
                log.setDirid(new Integer(dirId));
                log.setSoapid(new Integer(soapId));
                log.setNote(memo);
                log.setIp(iplist);
                log.setExtParam(extParams);
                log.updateLoginUser(con);
                if (id.equals(this.getLoginUser().getId() + "")) {
                    LoginUser.setLoginUser(getRequest(), log);
                }
                doMsg = "账号(" + id + ")修改成功";
                warn("修改账号:" + log.toString());
                warn(doMsg);
            } catch (Exception e2) { //返回编辑页
                if (doMsg == null) {
                    doMsg = "请检查各字段是否合法";
                }
                doMsg = "账号(" + id + ")修改失败:" + doMsg;
                error(doMsg + e2.getMessage());
                setAttribute("smuseredit", this);
                return new ActionForward("useredit.jsp");
            }
        } else if (dotype.equalsIgnoreCase("delete")&&this.getAccessPermission() > 1) { //删除
            try {
                String sql =
                        "delete from webuser where (username<>'admin' and id="
                        + id + ")";
                sql = sql + " and " + add2;
                int ii = JdbcPerformer.executeUpdate(con, sql);
                if (ii > 0) {
                    doMsg = "账号(" + id + ")删除成功！";
                    warn("删除账号:id=" + id);
                } else {
                    doMsg = "没有权限删除账号(" + id + ")!";
                }
                warn(doMsg);
            } catch (Exception e3) {
                doMsg = "账号(" + id + ")删除失败！";
                error(doMsg + e3.getMessage());
            }
        } else if (dotype.equalsIgnoreCase("new")&&this.getAccessPermission() > 1) { //新建
            UserManager mysrv = new UserManager();
            mysrv.setDotype("添加账号");
            mysrv.setDirId(dirId);
            mysrv.setIsActive(true);
            mysrv.dirList = dirList;
            mysrv.soapList = soapList;
            setAttribute("smuseredit", mysrv);
            return new ActionForward("useredit.jsp");
        } else if (dotype.equalsIgnoreCase("edit")) { //修改
            ResultSet crs;
            try {
                String sql =
                        "SELECT * FROM webuser where (username<>'admin' and id="
                        + id + ")";
                sql = sql + " and " + add2;
                crs = JdbcPerformer.executeQuery(con, sql);
                if (crs.next()) {
                    UserManager mysrv = assembler(crs);
                    mysrv.setDotype("修改账号");
                    mysrv.dirList = dirList;
                    mysrv.soapList = soapList;
                    setAttribute("smuseredit", mysrv);
                    return new ActionForward("useredit.jsp");
                } else {
                    doMsg = "没有权限编辑查看账号ID：" + id;
                }
            } catch (Exception d) {
                doMsg = "数据库查询失败，账号Id=" + id;
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
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the passWord
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * @param passWord the passWord to set
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    /**
     * @return the realname
     */
    public String getRealname() {
        return realname;
    }

    /**
     * @param realname the realname to set
     */
    public void setRealname(String realname) {
        this.realname = realname;
    }

    /**
     * @return the userGroup
     */
    public String getUserGroup() {
        return userGroup;
    }

    /**
     * @param userGroup the userGroup to set
     */
    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @param permission the permission to set
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * @return the mailPermission
     */
    public int getMailPermission() {
        return mailPermission;
    }

    /**
     * @param mailPermission the mailPermission to set
     */
    public void setMailPermission(int mailPermission) {
        this.mailPermission = mailPermission;
    }

    /**
     * @return the stimeOut
     */
    public int getStimeOut() {
        return stimeOut;
    }

    /**
     * @param stimeOut the stimeOut to set
     */
    public void setStimeOut(int stimeOut) {
        this.stimeOut = stimeOut;
    }

    /**
     * @return the isActive
     */
    public boolean isIsActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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
     * @return the logintime
     */
    public String getLogintime() {
        return logintime;
    }

    /**
     * @param logintime the logintime to set
     */
    public void setLogintime(String logintime) {
        this.logintime = logintime;
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
     * @return the dirId
     */
    public int getDirId() {
        return dirId;
    }

    /**
     * @param dirId the dirId to set
     */
    public void setDirId(int dirId) {
        this.dirId = dirId;
    }

    /**
     * @return the iplist
     */
    public String getIplist() {
        return iplist;
    }

    /**
     * @param iplist the iplist to set
     */
    public void setIplist(String iplist) {
        this.iplist = iplist;
    }

    /**
     * @return the extParams
     */
    public String getExtParams() {
        return extParams;
    }

    /**
     * @param extParams the extParams to set
     */
    public void setExtParams(String extParams) {
        this.extParams = extParams;
    }

    /**
     * @return the loginPath
     */
    public String getLoginPath() {
        return loginPath;
    }

    /**
     * @return the dirList
     */
    public TreeMap<String, String> getDirList() {
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
     * @return the fieldName
     */
    @SessionMethod
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the fieldValue
     */
    @SessionMethod
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * @param fieldValue the fieldValue to set
     */
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
    public List<UserManager> getRows() {
        return rows;
    }

    /**
     * @return the soapId
     */
    public int getSoapId() {
        return soapId;
    }

    /**
     * @param soapId the soapId to set
     */
    public void setSoapId(int soapId) {
        this.soapId = soapId;
    }

    /**
     * @return the soapList
     */
    public TreeMap<String, String> getSoapList() {
        return soapList;
    }
}
