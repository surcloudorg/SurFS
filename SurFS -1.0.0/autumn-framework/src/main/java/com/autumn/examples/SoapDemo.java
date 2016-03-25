package com.autumn.examples;

/**
 * <p>Title: SOAP服务接口测试</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public interface SoapDemo {

    public Demo[] getDemos();

    public void addDemo(Demo demo) throws Exception;
}
