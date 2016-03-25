package com.autumn.core;

import com.autumn.core.service.Service;
import com.autumn.core.service.ServiceFactory;
import com.autumn.core.soap.SoapFactory;
import com.autumn.core.soap.SoapService;
import com.autumn.core.web.WebFactory;
import com.autumn.core.web.WebService;
import java.util.concurrent.Callable;

/**
 * <p>Title: 异步关闭服务</p>
 *
 * <p>Description: 在服务器关闭时，通过AsyncCloseServices并发关闭容器中的所有服务</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class AsyncCloseServices implements Callable {

    private Service service = null;//服务
    private SoapService soapService = null;//soap服务
    private WebService webService = null;//web服务

    /**
     * 关闭web服务
     *
     * @param webService
     */
    public AsyncCloseServices(WebService webService) {
        this.webService = webService;
    }

    /**
     * 关闭soap服务
     *
     * @param soapService
     */
    public AsyncCloseServices(SoapService soapService) {
        this.soapService = soapService;
    }

    /**
     * 关闭服务
     *
     * @param service
     */
    public AsyncCloseServices(Service service) {
        this.service = service;
    }

    @Override
    public Boolean call() {
        if (service != null) {
            ServiceFactory.stopService(service.getServiceConfig().getId());
        }
        if (soapService != null) {
            SoapFactory.stopService(soapService.getSoapContext().getId());
        }
        if (webService != null) {
            WebFactory.stopService(webService.getWebDirectory().getId());
        }
        return Boolean.TRUE;
    }
}
