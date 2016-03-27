/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.tcp;

import com.autumn.core.DataCommand;
import java.util.EventObject;

/**
 * <p>Title:Tcp客户端/服务端组件</p>
 *
 * <p>Description: tcp连接接收到数据事件</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ReceiveEvent extends EventObject {

    /**
     * 初始化
     *
     * @param source 收到的字符串
     */
    public ReceiveEvent(String source) {
        super(source);
    }

    /**
     * 将收到的字符串-〉datacommand返回
     *
     * @return DataCommand
     */
    public DataCommand getCommand() {
        DataCommand mycmd = new DataCommand(source.toString());
        return mycmd;
    }
}
