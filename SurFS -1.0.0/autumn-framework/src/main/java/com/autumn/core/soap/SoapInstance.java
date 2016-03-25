package com.autumn.core.soap;

/**
 * <p>Title: SOAP框架</p>
 *
 * <p>Description: 服务实例</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public abstract class SoapInstance {

    protected SoapContext soapcontext = null;

    public SoapInstance(SoapContext context)  {
        this.soapcontext = context;
    }

    public abstract void contextDestroyed();
}
