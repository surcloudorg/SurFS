package com.autumn.core.web;

import com.autumn.core.sql.ConnectionFactory;
import com.autumn.util.Function;
import com.autumn.util.TextUtils;
import java.sql.Connection;
import java.util.Date;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: 用户登录</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class LoginValidate extends Action {

    protected static String filterIpList = null;
    private String username = "";
    private String password = "";
    private Connection con = null;

    static {
        setFilterIpList(Initializer.servletContext.getInitParameter("consoleIp"));
    }

    /**
     * 允许访问console的ip
     *
     * @param ip
     */
    protected static void setFilterIpList(String ip) {
        if (ip == null) {
            filterIpList = "0:0:0:0:0:0:0:1,127.0.0.*";
            String[] ss = Function.getLocalHostIP();
            if (ss != null) {
                for (String s : ss) {
                    int index = s.lastIndexOf(".");
                    s = index > 0 ? s.substring(0, index) + ".*" : s;
                    if (!filterIpList.contains(s)) {
                        filterIpList = filterIpList + "," + s;
                    }
                }
            }
        } else {
            filterIpList = ip.trim();
        }
    }

    /**
     * execute
     *
     * @return ActionForward
     */
    @Override
    public Forward execute() {
        if (getUsername().equals("")) {
            this.getRequest().setAttribute("error", "没有输入用户名！");
            return new ActionForward("/login.jsp");
        }
        this.getRequest().setAttribute("user", this);
        if (getPassword().equals("")) {
            this.getRequest().setAttribute("error", "没有输入密码！");
            return new ActionForward("/login.jsp");
        }
        //检测登录次数
        String cip =LoginIpcheck.getAddr(getRequest());
        if (LoginIpcheck.Check(cip)) {
            this.getRequest().setAttribute("error", "拒绝访问，该IP登录次数太多！");
            return new ActionForward("/login.jsp");
        }

        con = this.getConnect(ConnectionFactory.systemSourceName);
        try {
            LoginUser loginuser = new LoginUser();
            loginuser.setUserName(getUsername());
            loginuser.queryLoginUser(con);
            if (loginuser.getId() != null) {
                if (loginuser.getDirid().intValue() == 0) {//smconsole目录仅允许私网访问
                    if (!TextUtils.checkIpRange(filterIpList, cip)) {
                        this.getRequest().setAttribute("error", "ip地址无效！");
                        LoginIpcheck.addip(cip);
                        return new ActionForward("/login.jsp");
                    }
                } else {
                    if (!TextUtils.checkIpRange(loginuser.getIp(), cip)) {
                        this.getRequest().setAttribute("error", "ip地址无效！");
                        LoginIpcheck.addip(cip);
                        return new ActionForward("/login.jsp");
                    }
                }
                if (!loginuser.getIsActive().booleanValue()) {
                    this.getRequest().setAttribute("error", "帐号" + getUsername() + "已被停用！");
                    LoginIpcheck.addip(cip);
                    return new ActionForward("/login.jsp");
                }
                if (!password.equalsIgnoreCase(loginuser.getPassWord())) {
                    this.getRequest().setAttribute("error", "无效的密码！");
                    LoginIpcheck.addip(cip);
                    return new ActionForward("/login.jsp");
                } else {
                    WebDirectory wc = WebFactory.getWebDirectory(loginuser.getDirid().intValue());
                    if (wc == null) {
                        this.getRequest().setAttribute("error", "账号已过期！");
                        return new ActionForward("/login.jsp");
                    }
                    if (wc.getLogintype() == 4) {
                        this.getRequest().setAttribute("error", wc.getDirName() + "目录只能通过Basic认证方式访问！");
                        return new ActionForward("/login.jsp");
                    }
                    LoginUser.setLoginUser(this.getRequest(), loginuser);
                    loginuser.setLogintime(new Date());
                    loginuser.updateLoginUser(con);
                    warn("[" + cip + "][" + loginuser.getId() + "." + loginuser.getUserName() + "/" + loginuser.getRealname() + "]登录成功,路径：" + wc.getDirName());
                    LoginIpcheck.removeip(cip);
                    String page = wc.getDefaultPage();
                    if (wc.getDirName().equalsIgnoreCase("root")) {
                        return new RedirectForward(page);
                    } else {
                        return new RedirectForward(wc.getDirName() + "/" + page);
                    }
                }
            } else {
                this.getRequest().setAttribute("error", "帐号" + getUsername() + "不存在！");
                LoginIpcheck.addip(cip);
                return new ActionForward("/login.jsp");
            }
        } catch (Exception e) {
            trace("执行登陆失败!", e);
        }
        closeConnect(con);
        return null;
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
}
