/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.tcp;

/**
 * <p>Title:Tcp客户端/服务端组件</p>
 *
 * <p>Description: 处理接收到的数据，多线程并发</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class TaskExecuter extends Thread {

    private TcpConnect mytcp = null;

    public TaskExecuter(TcpConnect mytcp) {
        this.mytcp = mytcp;
    }

    @Override
    public void run() {
        ReceiveEvent event;
        while (!this.isInterrupted()) {
            try {
                event = (ReceiveEvent) mytcp.recdQueue.take();
                for (ReceiveListener rl : mytcp.receiveListeners) {
                    rl.onReceive(event);
                }
            } catch (InterruptedException r) {
                this.interrupt();
                break;
            } catch (Exception e) {
            }
        }
    }
}
