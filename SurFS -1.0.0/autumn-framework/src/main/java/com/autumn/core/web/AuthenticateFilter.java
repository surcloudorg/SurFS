package com.autumn.core.web;

import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.util.Function;
import com.autumn.util.TextUtils;
import java.io.IOException;
import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: WEB框架-过滤器</p>
 *
 * <p>Description: 执行basic认证过滤器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public final class AuthenticateFilter {

    private WebDirectory webDirectory = null;

    public AuthenticateFilter(WebDirectory wd) {
        this.webDirectory = wd;
    }

    /**
     * 判断是否登陆认证
     *
     * @param httpRequest
     * @param httpResponse
     * @return boolean false表示被拦截
     * @throws IOException
     */
    public boolean doFilter(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        if (webDirectory.getLogintype() == 2) { //无需验证
            return true;
        }
        LoginUser loginuser = LoginUser.getLoginUser(httpRequest);
        if (loginuser != null) {
            if (webDirectory.getLogintype() != 3) { //3 公用目录
                if (loginuser.getDirid().intValue() != webDirectory.getId()) {//更改路径
                    String msg = (httpRequest.getHeader("Authorization") != null) ? authenticate(httpRequest) : "不能改变登录路径!";
                    if (msg != null) {
                        if (webDirectory.getLogintype() == 4) { //需要basic认证
                            httpResponse.setStatus(401);
                            httpResponse.setHeader("WWW-authenticate", "Basic realm=\"www.autumn.com\"");
                        } else {                            
                            DispatchFilter.gotoError(httpRequest, httpResponse, msg);
                        }
                        return false;
                    }
                }
            }
        } else { //session失效,需要登录
            String msg = (httpRequest.getHeader("Authorization") != null) ? authenticate(httpRequest) : "需要登录!";
            if (msg != null) {
                if (webDirectory.getLogintype() == 4) { //需要basic认证
                    httpResponse.setStatus(401);
                    httpResponse.setHeader("WWW-authenticate", "Basic realm=\"www.autumn.com\"");
                } else {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 进行Basic认证
     *
     * @param httpRequest HttpServletRequest
     * @return String
     */
    private String authenticate(HttpServletRequest httpRequest) {
        String ip =LoginIpcheck.getAddr(httpRequest);
        String cip = "[" + ip + "]";
        String uriStr = httpRequest.getRequestURI();
        if (LoginIpcheck.Check(ip)) {
            return cip + "拒绝访问，该IP登录次数太多:" + uriStr;
        }
        String[] userandpwd = Function.basicParser(httpRequest.getHeader("Authorization"));
        String user = userandpwd[0];
        String password = userandpwd[1];
        LoginUser loginuser = new LoginUser();
        loginuser.setUserName(user);
        Connection con = null;
        try {
            con = ConnectionFactory.getConnect(DispatchFilter.class);
            loginuser.queryLoginUser(con);
        } catch (Exception e) {
        }
        JdbcUtils.closeConnect(con);
        if (loginuser.getId() != null) {
            if (loginuser.getDirid().intValue() == 0) {//console目录仅允许私网访问
                if (!TextUtils.checkIpRange(LoginValidate.filterIpList, ip)) {
                    LoginIpcheck.addip(ip);
                    return cip + "拒绝访问，ip地址无效！" + uriStr;
                }
            }
            if (loginuser.getDirid().intValue() != webDirectory.getId()) {
                if (webDirectory.getLogintype() != 3) { //公用目录
                    LoginIpcheck.addip(ip);
                    return cip + "拒绝访问，帐号" + user + "无效！" + uriStr;
                }
            }
            if (!loginuser.getIsActive().booleanValue()) {
                LoginIpcheck.addip(ip);
                return cip + "拒绝访问，帐号" + user + "已被停用！" + uriStr;
            }
            if (!password.equalsIgnoreCase(loginuser.getPassWord())) {
                LoginIpcheck.addip(ip);
                return cip + "拒绝访问，密码无效！" + uriStr;
            }
            if (!TextUtils.checkIpRange(loginuser.getIp(), ip)) {
                LoginIpcheck.addip(ip);
                return cip + "拒绝访问，ip地址无效！" + uriStr;
            }
            LoginIpcheck.removeip(ip);
            LoginUser.setLoginUser(httpRequest, loginuser);
            return null;
        } else {
            LoginIpcheck.addip(ip);
            return cip + "拒绝访问，帐号无效！" + uriStr;
        }
    }
}
