/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.tcp;

import java.util.EventListener;

/**
 * <p>Title:Tcp客户端/服务端组件</p>
 *
 * <p>Description: tcp连接接收到数据处理接口</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public interface ReceiveListener extends EventListener {

    public void onReceive(ReceiveEvent event);
}
