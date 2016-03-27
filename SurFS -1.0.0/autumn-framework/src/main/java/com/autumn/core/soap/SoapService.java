/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.soap;

import com.autumn.core.ClassManager;
import com.autumn.core.log.Logger;
import com.autumn.util.FileOperation;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.namespace.QName;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.jaxb2.JaxbServiceFactory;
import org.codehaus.xfire.jaxb2.JaxbType;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.codehaus.xfire.soap.SoapConstants;

/**
 * <p>Title: Soap框架</p>
 *
 * <p>Description: Soap服务线程监视器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SoapService extends Thread {

    private SoapContext soapcontext = null;
    private Logger log = null;
    protected ThreadGroup threadGroup = null;

    public SoapService(ThreadGroup tg, String name, SoapContext soapcontext) {
        super(tg, name);
        this.threadGroup = tg;
        this.soapcontext = soapcontext;
        log = soapcontext.getLogger().getLogger(SoapService.class);
    }

    @Override
    public void run() {
        try {
            register();
            log.warn("[{0}]Soap服务注册成功！", new Object[]{soapcontext.getServicename()});
        } catch (Throwable ex) {
            log.trace("[" + soapcontext.getServicename() + "]Soap服务注册失败！", ex);
            stoprun();
        }
    }

    /**
     * 关闭服务
     */
    protected void stoprun() {
        SoapFactory.setSoapService(this);
        try {
            getSoapContext().destroy();
        } catch (Throwable ee) {
            log.trace("[" + soapcontext.getServicename() + "]Soap服务关闭时抛出错误!", ee);
        }
        SoapFactory.removeSoapService();
        unregister();//取消注册
        SoapFactory.removeService(this);
        log.warn("[{0}]Soap服务退出！", new Object[]{soapcontext.getServicename()});
    }

    /**
     * 删除Aegis文件
     *
     * @param cls
     */
    protected static void deleteAegisFile(Class cls) {
        URL url = cls.getResource(cls.getSimpleName() + ".aegis.xml");
        if (url != null) {
            File f = new File(url.getFile());
            if (f.exists()) {
                f.delete();
            }
        }
    }

    /**
     * 检查aegisfile文件
     *
     * @param cls
     */
    private void CheckAegisfile(Class cls, String xml) {
        if (xml.isEmpty()) {
            deleteAegisFile(cls);
        } else {//需要写入
            URL url = cls.getResource(cls.getSimpleName() + ".class");
            if (url == null) {
                return;
            }
            File f = new File(url.getFile());
            String path = f.getParent();
            path = path.endsWith(File.separator)
                    ? path + cls.getSimpleName() + ".aegis.xml"
                    : path + File.separator + cls.getSimpleName() + ".aegis.xml";
            try {
                FileOperation.writeFile(xml.getBytes("utf-8"), path);
            } catch (IOException ex) {
                log.error("[{2}]创建{0}文件错误:{1}", new Object[]{path, ex, soapcontext.getServicename()});
            }
        }
    }

    /**
     * 实例化过滤器
     *
     * @param s
     * @return Object
     */
    private Object instanceFilter(String s) {
        if (!(s == null || s.trim().isEmpty())) {
            try {
                Class infilter = ClassManager.loadclass(s.trim());
                Object infilterobj = infilter.newInstance();
                log.info("[{0}]实例化{1}成功！", new Object[]{soapcontext.getServicename(), s});
                return infilterobj;
            } catch (Exception e) {
                log.trace("[" + soapcontext.getServicename() + "]实例化" + s + "失败!", e);
            }
        }
        return null;
    }

    /**
     * 取消注册
     */
    private void unregister() {
        Service service = SoapFactory.getService(soapcontext);
        if (service != null) {
            SoapService.deleteAegisFile(service.getServiceInfo().getServiceClass());
            XFireFactory.newInstance().getXFire().getServiceRegistry().unregister(service);
        }
    }

    /**
     * 注册
     *
     * @param soapcontext
     */
    @SuppressWarnings("unchecked")
    private void register() throws Throwable {
        String xml = soapcontext.getAegis();
        xml = xml == null || xml.trim().isEmpty() || xml.trim().equalsIgnoreCase("NA") ? "" : xml.trim();
        ObjectServiceFactory serviceFactory ;
        java.util.Map<String, Object> props = null;
        if (soapcontext.getClassName() == null || soapcontext.getClassName().isEmpty()) {
            throw new Exception("没有指定服务类,注册失败!");
        }
        Class cls = ClassManager.loadclass(soapcontext.getClassName());
        if (cls.isAnnotationPresent(FromWsdl.class) && (!xml.isEmpty())) {
            serviceFactory = new JaxbServiceFactory(XFireFactory.newInstance().getXFire().getTransportManager());
            serviceFactory.setStyle(SoapConstants.STYLE_DOCUMENT);
            ArrayList<String> schemas = new ArrayList<String>();
            schemas.add(soapcontext.getAegis().trim());
            props = new HashMap<String, Object>();
            props.put(ObjectServiceFactory.SCHEMAS, schemas);
        } else {
            CheckAegisfile(cls, xml);
            serviceFactory = new ObjectServiceFactory(XFireFactory.newInstance().getXFire().getTransportManager());
            serviceFactory.setStyle(soapcontext.getStyle().toLowerCase());
        }
        serviceFactory.setUse(soapcontext.getUseType().toLowerCase());
        Service service ;
        if (props == null) {
            service = serviceFactory.create(cls);
        } else {
            service = serviceFactory.create(cls, props);
            service.setProperty(JaxbType.ENABLE_VALIDATION, "true");
        }
        Object obj;
        Class cls1 = soapcontext.getImplClass();
        if (cls1 != null) {
            cls = cls1;
        }
        if (SoapInstance.class.isAssignableFrom(cls)) {
            Object[] initargs = new Object[]{soapcontext};
            obj = cls.getConstructor(new Class[]{SoapContext.class}).newInstance(initargs);
        } else {
            obj = cls.newInstance();
        }
        log.info("[{0}]实例化{1}成功！", new Object[]{soapcontext.getServicename(), obj.getClass().getName()});
        soapcontext.setSoapInstance(obj);
        service.setInvoker(new BeanInvoker(obj));
        Object infilterobj = instanceFilter(soapcontext.getInfilter());
        Object outfilterobj = instanceFilter(soapcontext.getOutfilter());
        if (!(soapcontext.getIpList() == null || soapcontext.getIpList().trim().isEmpty())) {
            service.addInHandler(new IpFilterHandler(soapcontext.getIpList().trim()));
        }
        if (soapcontext.getAuthtype() == 1) {
            service.addInHandler(new BasicAuthHandler());
        }
        if (infilterobj != null && infilterobj instanceof AbstractHandler) {
            service.addInHandler((AbstractHandler) infilterobj);
        }
        if (outfilterobj != null && outfilterobj instanceof AbstractHandler) {
            service.addOutHandler((AbstractHandler) outfilterobj);
        }
        QName oldname = service.getName();
        service.setName(new QName(oldname.getNamespaceURI(), soapcontext.getServicename()));
        XFireFactory.newInstance().getXFire().getServiceRegistry().register(service);
        if (infilterobj != null && infilterobj instanceof SoapFilter) {
            soapcontext.setInfilerobj((SoapFilter) infilterobj);
        }
        if (outfilterobj != null && outfilterobj instanceof SoapFilter) {
            soapcontext.setOutfilerobj((SoapFilter) outfilterobj);
        }
    }

    /**
     * @return the soapcontext
     */
    public SoapContext getSoapContext() {
        return soapcontext;
    }
}
