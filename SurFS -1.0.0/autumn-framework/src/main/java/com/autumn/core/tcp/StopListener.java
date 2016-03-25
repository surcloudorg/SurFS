package com.autumn.core.tcp;

import java.util.EventListener;

/**
 * <p>Title:Tcp客户端/服务端组件</p>
 *
 * <p>Description: 处理tcp连接终止事件的接口</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public interface StopListener extends EventListener {

    public void onStop(StopEvent event);
}
