/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.tcp;

import com.autumn.core.DataCommand;
import java.util.EventObject;
import java.util.List;

/**
 * <p>Title:Tcp客户端/服务端组件</p>
 *
 * <p>Description: tcp连接终止事件</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class StopEvent extends EventObject {

    private List<DataCommand> recData;//未处理得接收到的数据
    private List<DataCommand> sndData;//未发送的数据

    public StopEvent(Object source) {
        super(source);
    }

    /**
     * @return the recData
     */
    public List<DataCommand> getRecData() {
        return recData;
    }

    /**
     * @param recData the recData to set
     */
    public void setRecData(List<DataCommand> recData) {
        this.recData = recData;
    }

    /**
     * @return the sndData
     */
    public List<DataCommand> getSndData() {
        return sndData;
    }

    /**
     * @param sndData the sndData to set
     */
    public void setSndData(List<DataCommand> sndData) {
        this.sndData = sndData;
    }
}
