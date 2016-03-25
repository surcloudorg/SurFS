package com.autumn.core.web;

/**
 * <p>Title: WEB框架-输出器</p>
 *
 * <p>Description: 输出器接口</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public interface Forward {

    public void doForward(Action action) throws Throwable;
}
