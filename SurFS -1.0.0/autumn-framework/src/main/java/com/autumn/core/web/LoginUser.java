package com.autumn.core.web;

import com.autumn.core.log.LogFactory;
import com.autumn.core.soap.SoapContext;
import com.autumn.core.soap.SoapFactory;
import com.autumn.core.sql.JdbcUtils;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: 用户登录账号</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 20091127;

    /**
     * 从Session获取用户登陆信息
     *
     * @param request HttpServletRequest
     * @return LoginUser
     */
    public static LoginUser getLoginUser(HttpServletRequest request) {
        HttpSession hs = request.getSession(false);
        LoginUser loginuser = null;
        if (hs != null) {
            loginuser = (LoginUser) hs.getAttribute(LoginUser.class.getSimpleName());
        }
        if (loginuser == null) {
            loginuser = (LoginUser) request.getAttribute(LoginUser.class.getSimpleName());
        }
        return loginuser;
    }

    /**
     * 存储用户登陆信息至session
     *
     * @param request HttpServletRequest
     * @param log LoginUser
     */
    public static void setLoginUser(HttpServletRequest request, LoginUser log) {
        HttpSession mySession = request.getSession(true);
        mySession.setAttribute(LoginUser.class.getSimpleName(), log);
        if (log != null) {
            if (log.getStimeOut().intValue() > 1000 * 30 && log.getStimeOut().intValue() < 1000 * 60 * 60 * 24 * 7) {
                mySession.setMaxInactiveInterval(log.getStimeOut().intValue());
            }
        }
    }

    /**
     * 从记录集生成一个LoginUser
     *
     * @param crs ResultSet
     * @return LoginUser
     */
    public static LoginUser assembleLoginUser(ResultSet crs) {
        if (crs == null) {
            return null;
        }
        LoginUser logu = new LoginUser();
        try {
            logu.setId(new Integer(crs.getInt("id")));
        } catch (Exception d) {
            return null;
        }
        try {
            logu.setUserName(crs.getString("userName"));
        } catch (Exception d) {
            return null;
        }
        try {
            logu.setPassWord(crs.getString("passWord"));
        } catch (Exception d) {
            return null;
        }
        try {
            logu.setRealname(crs.getString("realname"));
        } catch (Exception d) {
        }
        try {
            logu.setUserGroup(crs.getString("UserGroup"));
        } catch (Exception d) {
        }
        try {
            logu.setMobile(crs.getString("mobile"));
        } catch (Exception d) {
        }
        try {
            logu.setEmail(crs.getString("email"));
        } catch (Exception d) {
        }
        try {
            logu.setPermission(crs.getString("permission"));
        } catch (Exception d) {
        }
        try {
            logu.setStimeOut(new Integer(crs.getInt("stimeOut")));
        } catch (Exception d) {
        }
        try {
            logu.setIsActive(Boolean.valueOf(crs.getBoolean("isActive")));
        } catch (Exception d) {
        }
        try {
            logu.setRectime(crs.getTimestamp("createtime"));
        } catch (Exception d) {
        }
        try {
            logu.setLogintime(crs.getTimestamp("logintime"));
        } catch (Exception d) {
        }
        try {
            logu.setNote(JdbcUtils.getResultSetStringValue(crs, "memo"));
        } catch (Exception d) {
        }
        try {
            logu.setDirid(new Integer(crs.getInt("dirid")));
        } catch (Exception d) {
        }
        try {
            logu.setSoapid(new Integer(crs.getInt("soapid")));
        } catch (Exception d) {
        }
        try {
            logu.setIp(crs.getString("iplist"));
        } catch (Exception d) {
        }
        try {
            logu.setExtParam(crs.getString("extParams"));
        } catch (Exception d) {
        }
        return logu;
    }

    /**
     * 构建LoginUser[]从记录集
     *
     * @param crs ResultSet
     * @return LoginUser[]
     */
    public static List<LoginUser> assembleLoginUsers(ResultSet crs) {
        if (crs == null) {
            return null;
        }
        try {
            List<LoginUser> list = new ArrayList<LoginUser>();
            try {
                crs.beforeFirst();
            } catch (Exception r) {
            }
            while (crs.next()) {
                list.add(assembleLoginUser(crs));
            }
            return list;
        } catch (Exception d) {
            return null;
        }
    }
    private Integer id = null;
    private Integer dirid = null;
    private Integer soapid = null;
    private String userName = null;
    private String passWord = null;
    private String realname = null;
    private String userGroup = null;
    private String mobile = null;
    private String email = null;
    private String permission = null;
    private Integer stimeOut = null;
    private Boolean isActive = null;
    private String ip = null;
    private Date rectime = null;
    private Date logintime = null;
    private String note = null;
    private String extParam = null;

    /**
     * 删除登录id
     *
     * @param con Connection
     * @throws Exception
     */
    public void deleteLoginUser(Connection con) throws Exception {
        Object key = null;
        String add = null;
        if (this.getId() != null) {
            add = "delete from webuser where id=?";
            key = this.getId(); //更新条件
        } else {
            if (this.getUserName() != null) {
                add = "delete from webuser where userName=?";
                key = this.getUserName(); //更新条件
            } else {
                throw new Exception("必须设置删除条件（账号id或用户名）");
            }
        }
        PreparedStatement st = null;
        try {
            st = con.prepareStatement(add);
            if (key instanceof Integer) {
                st.setInt(1, this.getId());
            } else {
                st.setString(1, this.getUserName());
            }
            st.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            JdbcUtils.closeStatement(st);
        }
    }

    /**
     * 查询某个id
     *
     * @param con Connection
     * @throws Exception
     */
    public void queryLoginUser(Connection con) throws Exception {
        Object key = null;
        String add = null;
        if (this.getId() != null) {
            add = "SELECT * FROM webuser where id=?";
            key = this.getId(); //更新条件
        } else {
            if (this.getUserName() != null) {
                add = "SELECT * FROM webuser where userName=?";
                key = this.getUserName();
            } else {
                throw new Exception("必须设置查询条件（账号id或用户名）");
            }
        }
        ResultSet crs;
        PreparedStatement st = null;
        try {
            st = con.prepareStatement(add);
            if (key instanceof Integer) {
                st.setInt(1, this.getId());
            } else {
                st.setString(1, this.getUserName());
            }
            crs = st.executeQuery();
            if (crs.next()) {
                LoginUser log = assembleLoginUser(crs);
                this.setDirid(log.getDirid());
                this.setSoapid(log.soapid);
                this.setEmail(log.getEmail());
                this.setId(log.getId());
                this.setIsActive(log.getIsActive());
                this.setLogintime(log.getLogintime());
                this.setMobile(log.getMobile());
                this.setNote(log.getNote());
                this.setPassWord(log.getPassWord());
                this.setPermission(log.getPermission());
                this.setRealname(log.getRealname());
                this.setRectime(log.getRectime());
                this.setStimeOut(log.getStimeOut());
                this.setUserGroup(log.getUserGroup());
                this.setUserName(log.getUserName());
                this.setIp(log.getIp());
                this.setExtParam(log.getExtParam());
            }
        } catch (Exception e) {
        }
        JdbcUtils.closeStatement(st);
    }

    /**
     * 添加账号
     *
     * @param con Connection
     * @throws Exception
     */
    public void insertLoginUser(Connection con) throws
            Exception {
        if (this.getDirid() != null) {
            WebDirectory wc = WebFactory.getWebDirectory(this.getDirid().intValue());
            if (wc == null) {
                throw new Exception("找不到目录ID（" + this.getDirid().intValue() + "），不能添加账号");
            }
        } else {
            throw new Exception("必须指定账号的访问目录（dirid字段）！");
        }
        int soapId = 0;
        if (this.getSoapid() != null) {
            if (this.getDirid() == -1) {
                SoapContext sc = SoapFactory.getSoapContext(this.getSoapid().intValue());
                if (sc == null) {
                    throw new Exception("找不到soap服务ID（" + this.getSoapid().intValue() + "），不能添加账号");
                }
                soapId = this.getSoapid().intValue();
            }
        }
        String username = "";
        if (this.getUserName() != null) {
            username = this.getUserName();
        } else {
            throw new Exception("没有设置账号");
        }
        String password = "";
        if (this.getPassWord() != null) {
            password = this.getPassWord();
        } else {
            throw new Exception("没有设置密码");
        }
        String realname1 = this.getRealname();
        if (realname1 == null) {
            realname1 = "";
        }
        String UserGroup = this.getUserGroup();
        if (UserGroup == null) {
            UserGroup = "";
        }
        String mobile1 = this.getMobile();
        if (mobile1 == null) {
            mobile1 = "";
        }
        String email1 = this.getEmail();
        if (email1 == null) {
            email1 = "";
        }
        String permission1 = this.getPermission();
        if (permission1 == null) {
            permission1 = "";
        }
        int sTimeout = 0;
        if (this.getStimeOut() != null) {
            sTimeout = this.getStimeOut().intValue();
        }
        int isactive = 1;
        if (this.getIsActive() != null) {
            if (!this.getIsActive().booleanValue()) {
                isactive = 0;
            }
        }
        String note1 = this.getNote();
        if (note1 == null) {
            note1 = "";
        }
        String ip1 = this.getIp();
        if (ip1 == null) {
            ip1 = "";
        }
        String extParam1 = this.getExtParam();
        if (extParam1 == null) {
            extParam1 = "";
        }
        Date rectime1 = this.getRectime();
        if (rectime1 == null) {
            rectime1 = new Date();
        }
        Date logintime1 = this.getLogintime();
        if (logintime1 == null) {
            logintime1 = new Date();
        }
        PreparedStatement prest = null;
        try {
            prest = con.prepareStatement(
                    "insert into webuser(username,password,realname,UserGroup,mobile,email,permission,dirId,stimeOut,"
                    + "isActive,memo,extParams,iplist,createtime,logintime,soapid)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            prest.setString(1, username);
            prest.setString(2, password);
            prest.setString(3, realname1);
            prest.setString(4, UserGroup);
            prest.setString(5, mobile1);
            prest.setString(6, email1);
            prest.setString(7, permission1);
            prest.setInt(8, getDirid());
            prest.setInt(9, sTimeout);
            prest.setInt(10, isactive);
            prest.setString(11, note1);
            prest.setString(12, extParam1);
            prest.setString(13, ip1);
            prest.setTimestamp(14, new Timestamp(rectime1.getTime()));
            prest.setTimestamp(15, new Timestamp(logintime1.getTime()));
            prest.setInt(16, soapId);
            prest.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            JdbcUtils.closeStatement(prest);
        }
    }

    /**
     * 更新账号信息
     *
     * @param con Connection
     * @throws Exception
     */
    public void updateLoginUser(Connection con) throws
            Exception {
        StringBuilder sql = new StringBuilder();
        String add = "";
        Object key = null;
        List<Object> map = new ArrayList<Object>();
        if (this.getId() != null) {
            if (this.getUserName() != null) {
                sql.append(",userName=?");
                map.add(this.getUserName());
            }
            add = " where id=?"; //更新条件
            key = this.getId();
        } else {
            if (this.getUserName() != null) {
                add = " where userName=?"; //更新条件
                key = this.getUserName();
            } else {
                throw new Exception("必须设置更新条件（账号id或用户名）");
            }
        }

        if (this.getDirid() != null) {
            WebDirectory wc = WebFactory.getWebDirectory(this.getDirid().intValue());
            if (wc == null) {
                throw new Exception("找不到目录ID（" + this.getDirid().intValue() + "），不能修改账号");
            }
            sql.append(",dirid=?");
            map.add(this.getDirid());
        }
        if (this.getSoapid() != null) {
            if (this.getDirid() == null || this.getDirid() == -1) {
                SoapContext sc = SoapFactory.getSoapContext(this.getSoapid().intValue());
                if (sc == null) {
                    throw new Exception("找不到soap服务ID（" + this.getSoapid().intValue() + "），不能修改账号");
                }
            }
            sql.append(",soapid=?");
            map.add(this.getSoapid());
        }
        if (this.getPassWord() != null) {
            sql.append(",passWord=?");
            map.add(this.getPassWord());
        }
        if (this.getRealname() != null) {
            sql.append(",realname=?");
            map.add(this.getRealname());
        }
        if (this.getUserGroup() != null) {
            sql.append(",UserGroup=?");
            map.add(this.getUserGroup());
        }
        if (this.getMobile() != null) {
            sql.append(",mobile=?");
            map.add(this.getMobile());
        }
        if (this.getEmail() != null) {
            sql.append(",email=?");
            map.add(this.getEmail());
        }
        if (this.getPermission() != null) {
            sql.append(",permission=?");
            map.add(this.getPermission());
        }
        if (this.getStimeOut() != null) {
            sql.append(",stimeOut=?");
            map.add(this.getStimeOut());
        }
        if (this.getIsActive() != null) {
            sql.append(",isActive=?");
            map.add(this.getIsActive());
        }
        if (this.getRectime() != null) {
            sql.append(",createtime=?");
            map.add(this.getRectime());
        }
        if (this.getLogintime() != null) {
            sql.append(",logintime=?");
            map.add(this.getLogintime());
        }
        if (this.getNote() != null) {
            sql.append(",memo=?");
            map.add(this.getNote());
        }
        if (this.getIp() != null) {
            sql.append(",iplist=?");
            map.add(this.getIp());
        }
        if (this.getExtParam() != null) {
            sql.append(",extParams=?");
            map.add(this.getExtParam());
        }
        String sqlstr = "";
        if (map.isEmpty()) {
            throw new Exception("不包含需要更新的字段");
        } else {
            map.add(key);
            sql.append(add);
            sqlstr = sql.toString().substring(1);
        }
        int index = 0;
        PreparedStatement prest = null;
        try {
            prest = con.prepareStatement("update webuser set " + sqlstr);
            for (Object value : map) {
                index++;
                if (value instanceof Boolean) {
                    Boolean bol = (Boolean) value;
                    if (bol) {
                        prest.setInt(index, 1);
                    } else {
                        prest.setInt(index, 0);
                    }
                } else if (value instanceof Integer) {
                    prest.setInt(index, (Integer) value);
                } else if (value instanceof Date) {
                    long l = ((Date) value).getTime();
                    prest.setTimestamp(index, new Timestamp(l));
                } else if (value instanceof Clob) {
                    prest.setClob(index, (Clob) value);
                } else {
                    prest.setString(index, value.toString());
                }
            }
            prest.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            JdbcUtils.closeStatement(prest);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (getId() != null) {
            str.append("id=").append(getId().intValue()).append(",");
        }
        if (getDirid() != null) {
            str.append("dirid=").append(getDirid().intValue()).append(",");
        }
        if (getSoapid() != null) {
            str.append("soapid=").append(getSoapid().intValue()).append(",");
        }
        if (getUserName() != null) {
            str.append("userName=").append(getUserName()).append(",");
        }
        if (getPassWord() != null) {
            str.append("passWord=").append(getPassWord()).append(",");
        }
        if (getRealname() != null) {
            str.append("realname=").append(getRealname()).append(",");
        }
        if (getUserGroup() != null) {
            str.append("userGroup=").append(getUserGroup()).append(",");
        }
        if (getMobile() != null) {
            str.append("mobile=").append(getMobile()).append(",");
        }
        if (getEmail() != null) {
            str.append("email=").append(getEmail()).append(",");
        }
        if (getPermission() != null) {
            str.append("permission=").append(getPermission()).append(",");
        }
        if (getStimeOut() != null) {
            str.append("stimeOut=").append(getStimeOut().intValue()).append(",");
        }
        if (getIsActive() != null) {
            str.append("isActive=").append(getIsActive().booleanValue()).append(",");
        }
        if (getNote() != null) {
            str.append("note=").append(getNote()).append(",");
        }
        if (getIp() != null) {
            str.append("ip=").append(getIp()).append(",");
        }
        if (getExtParam() != null) {
            str.append("extParam=").append(getExtParam()).append(",");
        }
        return str.toString();
    }

    /**
     * 克隆
     *
     * @return LoginUser
     */
    public LoginUser copy() {
        LoginUser log = new LoginUser();
        log.setEmail(this.getEmail());
        log.setId(this.getId());
        log.setDirid(this.getDirid());
        log.setSoapid(this.getSoapid());
        log.setIsActive(this.getIsActive());
        log.setLogintime(this.getLogintime());
        log.setMobile(this.getMobile());
        log.setNote(this.getNote());
        log.setPassWord(this.getPassWord());
        log.setPermission(this.getPermission());
        log.setRealname(this.getRealname());
        log.setRectime(this.getRectime());
        log.setStimeOut(this.getStimeOut());
        log.setUserGroup(this.getUserGroup());
        log.setUserName(this.getUserName());
        log.setExtParam(this.getExtParam());
        log.setIp(this.getIp());
        return log;
    }

    /**
     * @return the dirid
     */
    public Integer getDirid() {
        return dirid;
    }

    /**
     * @param dirid the dirid to set
     */
    public void setDirid(Integer dirid) {
        this.dirid = dirid;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
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
     * @return the stimeOut
     */
    public Integer getStimeOut() {
        return stimeOut;
    }

    /**
     * @param stimeOut the stimeOut to set
     */
    public void setStimeOut(Integer stimeOut) {
        this.stimeOut = stimeOut;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the rectime
     */
    public Date getRectime() {
        return rectime;
    }

    /**
     * @param rectime the rectime to set
     */
    public void setRectime(Date rectime) {
        this.rectime = rectime;
    }

    /**
     * @return the logintime
     */
    public Date getLogintime() {
        return logintime;
    }

    /**
     * @param logintime the logintime to set
     */
    public void setLogintime(Date logintime) {
        this.logintime = logintime;
    }

    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return the extParam
     */
    public String getExtParam() {
        return extParam;
    }

    /**
     * @param extParam the extParam to set
     */
    public void setExtParam(String extParam) {
        this.extParam = extParam;
    }

    /**
     * @return the soapid
     */
    public Integer getSoapid() {
        return soapid;
    }

    /**
     * @param soapid the soapid to set
     */
    public void setSoapid(Integer soapid) {
        this.soapid = soapid;
    }

    /**
     * 检查系统帐号
     *
     * @param con
     */
    public static void checkAdministrator(Connection con) {
        try {
            LoginUser login = new LoginUser();
            login.setUserName("admin");
            login.queryLoginUser(con);
            if (login.getId() == null) {
                login.deleteLoginUser(con);
                login.setDirid(0);
                login.setPassWord("admin");
                login.setRealname("管理员");
                login.setPermission("33333333333");
                login.insertLoginUser(con);
            } else {
                login.setDirid(0);
                login.setPermission("33333333333");
                login.updateLoginUser(con);
            }
        } catch (Exception ea) {
            LogFactory.warn("数据库操作失败！" + ea.getMessage(), LoginUser.class);
        }
    }
}
