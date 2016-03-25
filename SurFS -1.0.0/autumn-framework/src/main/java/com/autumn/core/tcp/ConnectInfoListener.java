package com.autumn.core.tcp;

import java.util.EventListener;

/**
 * <p>Title:Tcp客户端/服务端组件</p>
 *
 * <p>Description: tcp连接状态事件处理接口</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public interface ConnectInfoListener extends EventListener {

    public void onConnectInfo(ConnectInfoEvent event);
}
