package com.autumn.core.soap;

import com.autumn.core.log.LogFactory;
import com.autumn.core.web.LoginIpcheck;
import com.autumn.util.TextUtils;
import javax.servlet.http.HttpServletRequest;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.transport.http.XFireServletController;

/**
 * <p>Title: SOAP框架</p>
 *
 * <p>Description: ip验证</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class IpFilterHandler extends AbstractHandler {

    private String iplist = "";

    public IpFilterHandler(String iplist) {
        this.iplist = iplist;
    }

    @Override
    public void invoke(MessageContext ctx) throws Exception {
        HttpServletRequest request = XFireServletController.getRequest();
        String cip =LoginIpcheck.getAddr(request);
        if (!TextUtils.checkIpRange(iplist, cip)) {
            String msg = "ip地址无效！";
            LogFactory.warn("[" + cip + "]" + msg, IpFilterHandler.class);
            throw new XFireFault(msg, XFireFault.SENDER);
        }
    }
}
