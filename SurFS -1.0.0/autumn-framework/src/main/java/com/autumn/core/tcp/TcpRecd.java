/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.tcp;

import com.autumn.core.DataCommand;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.util.IOUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * <p>Title:Tcp客户端/服务端组件</p>
 *
 * <p>Description: 接收线程</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class TcpRecd extends Thread {

    private TcpConnect mytcp = null;
    private BufferedReader in = null;
    private final Object inLock = new Object();
    private Logger log = null;

    public TcpRecd(TcpConnect mytcp) {
        this.mytcp = mytcp;
        log = mytcp.getLog() == null ? LogFactory.getLogger(TcpRecd.class) : mytcp.getLog().getLogger(TcpRecd.class);
    }

    /**
     * 设置BufferedReader
     *
     * @param sin
     */
    protected void setin(BufferedReader sin) {
        synchronized (inLock) {
            this.in = sin;
            inLock.notify();
        }
    }

    @Override
    public void run() {
        String strCommand;
        if (mytcp == null) {
            return;
        }
        int n = 0, m = 0;
        while (!this.isInterrupted()) {
            try {
                synchronized (inLock) {
                    if (in == null) {
                        inLock.wait();
                    }
                }
                strCommand = in.readLine();
                if (strCommand == null) {
                    throw new IOException("读错误");
                }
                if (strCommand.trim().equals("")) {
                    continue;
                }
                DataCommand comm = new DataCommand(strCommand);
                String commid = comm.getItemValue("CommandId");
                if (commid == null) {
                    commid = "";
                }
                if (comm.getCommID().compareTo("received") != 0 && commid.compareTo("") != 0) {
                    DataCommand cmd = new DataCommand("Received CommandId=" + commid);
                    mytcp.mysend.addRepList(cmd);
                }
                if (comm.getCommID().compareTo("received") == 0) {
                    if (!commid.equals("")) {
                        mytcp.mysend.delList(commid);
                        n = 0;
                    }
                    ConnectInfoEvent myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.RECEIVED, strCommand);
                    mytcp.fireOnConnectInfo(myevent);
                    log.debug(strCommand);
                } else if (comm.getCommID().compareTo("activetest") == 0) {
                    ConnectInfoEvent myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.RECDACTIVETEST, strCommand);
                    mytcp.fireOnConnectInfo(myevent);
                    log.debug(strCommand);
                } else {
                    ReceiveEvent myevent = new ReceiveEvent(strCommand);
                    mytcp.fireOnReceive(myevent);
                    String str = comm.getDispCommand();
                    if (str.length() > 800) {
                        str = str.substring(0, 800) + "...";
                    }
                    log.info(str);
                    mytcp.recCount++;
                }
                m = 0;
            } catch (InterruptedException ee) {
                break;
            } catch (InterruptedIOException ie) {
                if (mytcp.mysend.getListCount() >= this.mytcp.getBufSize()) {
                    n++;
                    if (n >= 3) {
                        if (mytcp.mysend != null) {
                            destoryIn();
                            mytcp.mysend.reLogin();
                            ConnectInfoEvent myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.CONNECT_BREAKED, "超过3分钟无回应，断开连接！");
                            mytcp.fireOnConnectInfo(myevent);
                            log.warn("超过3分钟无回应，断开连接！");
                        }
                        n = 0;
                    }
                } else {
                    m++;
                    if (m >= 3) {
                        if (mytcp.mysend != null) {
                            destoryIn();
                            mytcp.mysend.reLogin();
                            ConnectInfoEvent myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.CONNECT_BREAKED, "超过3分钟无回应，断开连接！");
                            log.warn("超过3分钟无回应，断开连接！");
                            mytcp.fireOnConnectInfo(myevent);
                        }
                        m = 0;
                    }
                    sendActiveTest();
                }
            } catch (IOException e) {
                if (!this.isInterrupted()) {
                    destoryIn();
                    mytcp.mysend.reLogin();
                }
            } catch (Exception e) {
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    break;
                }
            }

        }
        log.warn("线程退出！");
    }

    /**
     * 关闭BufferedReader
     */
    protected void destoryIn() {
        synchronized (inLock) {
            IOUtils.close(in);
            in = null;
        }
    }

    /**
     * 发送激活测试
     */
    private void sendActiveTest() {
        if (mytcp.isLogin()) {
            String strCommand = "ActiveTest CommandId=" + DataCommand.getNewCommandid();
            ConnectInfoEvent myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.SENDACTIVETEST, strCommand);
            mytcp.fireOnConnectInfo(myevent);
            mytcp.mysend.addRepList(new DataCommand(strCommand));
        }
    }
}
