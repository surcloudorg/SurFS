package com.autumn.core;

/**
 * <p>Title: 系统退出接口</p>
 *
 * <p>Description: 在检测到需要关闭系统的消息后，执行此接口</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public interface JVMListener {

    public abstract void systemDestroyed();
}
