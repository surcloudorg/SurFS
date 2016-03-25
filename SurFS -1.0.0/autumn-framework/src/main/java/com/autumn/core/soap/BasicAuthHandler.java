package com.autumn.core.soap;

import com.autumn.util.Function;
import javax.servlet.http.HttpServletRequest;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.transport.http.XFireServletController;

/**
 * <p>Title: SOAP框架</p>
 *
 * <p>Description: BASIC认证</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class BasicAuthHandler extends AbstractHandler {

    /**
     * 提取帐号密码
     *
     * @param ctx MessageContext
     * @throws Exception
     */
    @Override
    public void invoke(MessageContext ctx) throws Exception {
        HttpServletRequest request = XFireServletController.getRequest();
        String userNameAndPassword = request.getHeader("Authorization");
        String[] userandpwd = Function.basicParser(userNameAndPassword);
        Authentication.filter(ctx, userandpwd[0], userandpwd[1]);
    }
}
