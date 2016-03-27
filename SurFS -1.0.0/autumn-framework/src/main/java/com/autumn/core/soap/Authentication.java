/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.soap;

import com.autumn.core.log.LogFactory;
import com.autumn.core.sql.ConnectionFactory;
import com.autumn.core.sql.JdbcUtils;
import com.autumn.core.web.LoginIpcheck;
import com.autumn.core.web.LoginUser;
import com.autumn.util.TextUtils;
import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.transport.http.XFireServletController;

/**
 * <p>Title: SOAP框架</p>
 *
 * <p>Description: 从LoginUser表查询帐号,写入session</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Authentication {

    /**
     * 写日志
     */
    private static void log(String msg, String cip) {
        LogFactory.warn("[{0}]{1}", new Object[]{cip, msg}, Authentication.class);
    }

    /**
     * 验证
     *
     * @param ctx MessageContext
     * @param username String
     * @param password String
     * @throws Exception
     */
    public static void filter(MessageContext ctx, String username, String password) throws Exception {
        QName soapname = ctx.getService().getName();
        HttpServletRequest request = XFireServletController.getRequest();
        String cip =LoginIpcheck.getAddr(request);
        if (LoginIpcheck.Check(cip)) {
            String msg = "拒绝访问，该IP登录次数太多！";
            log(msg, cip);
            throw new XFireFault(msg, XFireFault.SENDER);
        }
        LoginUser loginuser = getLoginUser(username);
        if (loginuser.getId() == null) {
            LoginIpcheck.addip(cip);
            log("帐号无效！", cip);
            throw new XFireFault("帐号无效！", XFireFault.SENDER);
        }
        if (!TextUtils.checkIpRange(loginuser.getIp(), cip)) {
            String msg = "ip地址无效！";
            log(msg, cip);
            LoginIpcheck.addip(cip);
            throw new XFireFault(msg, XFireFault.SENDER);
        }
        if (!loginuser.getIsActive().booleanValue()) {
            LoginIpcheck.addip(cip);
            String msg = "帐号" + username + "已被停用！";
            log(msg, cip);
            throw new XFireFault(msg, XFireFault.SENDER);
        }
        if (!password.equalsIgnoreCase(loginuser.getPassWord())) {
            LoginIpcheck.addip(cip);
            String msg = "密码无效！";
            log(msg, cip);
            throw new XFireFault(msg, XFireFault.SENDER);
        }
        SoapContext ssp = SoapFactory.getSoapContext(loginuser.getSoapid());
        if (ssp == null) {
            String msg = "找不到服务配置：" + loginuser.getSoapid();
            log(msg, cip);
            throw new XFireFault(msg, XFireFault.SENDER);
        }
        if (!soapname.getLocalPart().equals(ssp.getServicename())) {
            LoginIpcheck.addip(cip);
            String msg = "帐号" + username + "没有权限访问服务" + soapname.getLocalPart();
            log(msg, cip);
            throw new XFireFault(msg, XFireFault.SENDER);
        }
        LoginIpcheck.removeip(cip);
        LoginUser.setLoginUser(request, loginuser);
    }

    /**
     * 查询用户
     *
     * @param username String
     * @return LoginUser
     */
    public static LoginUser getLoginUser(String username) {
        Connection con = null;
        LoginUser loginuser = null;
        try {
            con = ConnectionFactory.getConnect(Authentication.class);
            loginuser = new LoginUser();
            loginuser.setUserName(username);
            loginuser.queryLoginUser(con);
        } catch (Exception e) {
            LogFactory.error("数据库操作失败！" + e.getMessage(), Authentication.class);
        }
        JdbcUtils.closeConnect(con);
        return loginuser;
    }
}
