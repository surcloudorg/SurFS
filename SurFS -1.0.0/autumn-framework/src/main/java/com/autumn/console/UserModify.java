package com.autumn.console;

import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.web.Action;
import com.autumn.core.web.ActionForward;
import com.autumn.core.web.Forward;
import com.autumn.core.web.LoginUser;
import java.sql.Connection;

/**
 * <p>Title: 框架控制台</p>
 *
 * <p>Description: 更改当前帐户密码</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class UserModify extends Action {

    private String username = "";
    private String password = "";
    private String newpwd1 = "";
    private String newpwd2 = "";
    private String realname = "";
    private String mobile = "";
    private String email = "";
    private String dotype = null;
    private LoginUser loginuser = null; //用户信息
    private String doMsg = null; //执行结果
    private Connection con = null;

    @Override
    public Forward execute() {
        loginuser = LoginUser.getLoginUser(this.getRequest());
        con = this.getConnect(ConnectionFactory.systemSourceName);
        if (dotype != null) {
            try {
                if (!username.equals(loginuser.getUserName())) {
                    throw new Exception("输入的账号名无效！");
                }
                if (!password.equals(loginuser.getPassWord())) {
                    throw new Exception("输入的密码无效！");
                }
                LoginUser log = new LoginUser();
                if (!newpwd1.equals("")) {
                    if (!newpwd1.equals(newpwd2)) {
                        throw new Exception("输入的新密码不一致！");
                    } else {
                        log.setPassWord(getNewpwd1());
                    }
                }
                log.setId(loginuser.getId());
                log.setRealname(getRealname());
                log.setMobile(getMobile());
                log.setEmail(getEmail());
                log.updateLoginUser(con);
                if (!newpwd1.equals("")) {
                    loginuser.setPassWord(getNewpwd1());
                }
                loginuser.setRealname(getRealname());
                loginuser.setMobile(getMobile());
                loginuser.setEmail(getEmail());
                doMsg = "修改成功！";
                warn("帐号" + getUsername() + "修改成功！");
            } catch (Exception e) {
                doMsg = "帐号修改失败！";
                error(getDoMsg() + e.getMessage());
            }
        } else {
            setRealname(loginuser.getRealname());
            setMobile(loginuser.getMobile());
            this.setEmail(loginuser.getEmail());
        }
        setAttribute("smusermodify", this);
        return new ActionForward("usermodify.jsp");
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the newpwd1
     */
    public String getNewpwd1() {
        return newpwd1;
    }

    /**
     * @param newpwd1 the newpwd1 to set
     */
    public void setNewpwd1(String newpwd1) {
        this.newpwd1 = newpwd1;
    }

    /**
     * @return the newpwd2
     */
    public String getNewpwd2() {
        return newpwd2;
    }

    /**
     * @param newpwd2 the newpwd2 to set
     */
    public void setNewpwd2(String newpwd2) {
        this.newpwd2 = newpwd2;
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
}
