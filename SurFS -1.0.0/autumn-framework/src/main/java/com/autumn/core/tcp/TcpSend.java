/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.tcp;

import com.autumn.core.DataCommand;
import com.autumn.core.DataItem;
import com.autumn.core.log.Level;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.util.IOUtils;
import com.autumn.util.TextUtils;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Title:Tcp客户端/服务端组件</p>
 *
 * <p>
 * Description: 发送线程</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class TcpSend extends Thread {

    private TcpConnect mytcp = null;
    private ServerSocket ssc = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    private Socket sc = null;
    private ConcurrentHashMap<String, DataCommand> sendData = null; //发送完等待回应的数据
    private final List<DataCommand> repList = new LinkedList<DataCommand>();
    private boolean exitSign = false;
    private Logger log = null;

    /**
     * 设置需要回复的指令
     *
     * @param smd
     */
    protected void addRepList(DataCommand smd) {
        synchronized (repList) {
            repList.add(smd);
        }
        this.interrupt();
    }

    /**
     * 取出一条回复指令准备回复
     *
     * @return DataCommand
     */
    private DataCommand getRepList() {
        DataCommand mycmd = null;
        synchronized (repList) {
            if (!repList.isEmpty()) {
                mycmd = repList.remove(0);
            }
        }
        return mycmd;
    }

    /**
     * 初始化
     *
     * @param mytcp
     */
    public TcpSend(TcpConnect mytcp) {
        this.mytcp = mytcp;
        if (!this.mytcp.isPassiveMode()) {
            if (this.mytcp.getType() == 0 || this.mytcp.getType() > 2) {
                sendData = new ConcurrentHashMap<String, DataCommand>(this.mytcp.getBufSize());
            }
        } else {
            sendData = new ConcurrentHashMap<String, DataCommand>(this.mytcp.getBufSize());
        }
        log = mytcp.getLog() == null ? LogFactory.getLogger(TcpSend.class) : mytcp.getLog().getLogger(TcpSend.class);
    }

    /**
     * 根据commandid从待回复队列中删除
     *
     * @param commandid
     */
    public void delList(String commandid) {
        if (sendData == null) {
            return;
        }
        sendData.remove(commandid);
    }

    /**
     * 待回复队列长度
     *
     * @return int
     */
    public int getListCount() {
        if (sendData == null) {
            return 0;
        }
        return sendData.size();
    }

    @Override
    public void run() {
        if (mytcp == null) {
            return;
        }
        Login();
        DataCommand sendcomm = null;
        int type = 0;
        while (!exitSign) {
            try {
                if (sendData != null && sendcomm == null) {
                    if (getListCount() >= this.mytcp.getBufSize() && mytcp.isLogin()) { //需要等待回应
                        sleep(1000);
                        continue;
                    }
                }
            } catch (InterruptedException iie) {
                continue;
            } catch (Exception e) {
            }
            try {
                if (mytcp.isLogin()) {
                    if (sendcomm == null) {
                        sendcomm = getRepList();
                    }
                    if (sendcomm == null) {
                        if (mytcp.getSendBuffer() != null) {
                            if (mytcp.isPassiveMode() == false && (mytcp.getType() == 1 || mytcp.getType() == 3)) { //不是发送类型
                                sendcomm = mytcp.sendQueue.take();
                                type = 1;
                            } else {
                                sendcomm = mytcp.getSendBuffer().take();
                                type = 1;
                            }
                        } else {
                            sendcomm = mytcp.sendQueue.take();
                            type = 1;
                        }
                    }
                    if (sendcomm == null) {
                        continue;
                    }
                    String Commandid = sendcomm.getItemValue("CommandId");
                    if (Commandid == null) {
                        Commandid = DataCommand.getNewCommandid();
                        sendcomm.addItem(new DataItem("CommandId", Commandid));
                    }
                    if (sendData != null && (!sendcomm.getCommID().equalsIgnoreCase("received"))) {
                        sendData.replace(Commandid, sendcomm);
                    }
                    String ss = sendcomm.getDispCommand();
                    out.write(sendcomm.getCommand() + "\r\n");
                    out.flush();
                    log.log(Level.newstance(type), ss);
                    mytcp.sndCount++;
                    sendcomm = null;
                    type = 0;
                } else {
                    Login();
                }
            } catch (InterruptedException e) {
            } catch (IOException e) {
                if (!exitSign) {
                    reLogin();
                }
            } catch (Exception e) {
                log.error("未知错误：" + e.getMessage());
            }
        }
        List<DataCommand> vreceive = new ArrayList<DataCommand>();
        while (!mytcp.recdQueue.isEmpty()) {
            ReceiveEvent mye = (ReceiveEvent) mytcp.recdQueue.poll();
            if (mye != null) {
                vreceive.add(mye.getCommand());
            }
        }
        List<DataCommand> vsend = new ArrayList<DataCommand>();
        while (!mytcp.sendQueue.isEmpty()) {
            DataCommand myc = mytcp.sendQueue.poll();
            if (myc != null) {
                vsend.add(myc);
            }
        }
        if (sendcomm != null && type == 1) {
            if (mytcp.getSendBuffer() != null) {
                try {
                    mytcp.getSendBuffer().add(sendcomm);
                } catch (Exception e) {
                }
            } else {
                vsend.add(sendcomm);
            }
        }
        if ((!vreceive.isEmpty()) || (!vsend.isEmpty())) {
            StopEvent event = new StopEvent(this);
            event.setRecData(vreceive);
            event.setSndData(vsend);
            mytcp.fireOnStop(event);
        }
        close();
        IOUtils.close(ssc);
        log.warn("线程退出！");
    }

    /**
     * 重新登陆
     */
    protected void reLogin() {
        if (mytcp.isLogin()) {
            ConnectInfoEvent myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.CONNECT_BREAKED, "连接断开");
            mytcp.fireOnConnectInfo(myevent);
            mytcp.setLogin(false);
            if (sendData != null) {
                sendData.clear();
            }
            log.warn("连接断开！");
        }
        this.interrupt();
    }

    /**
     * 连接是否断开
     *
     * @return boolean
     */
    protected boolean isClosed() {
        if (sc == null) {
            return true;
        } else {
            if (sc.isClosed()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 终止
     */
    protected void terminate() {
        exitSign = true;
        close();
        IOUtils.close(ssc);
    }

    /**
     * 关闭套接
     */
    protected void close() {
        IOUtils.close(sc);
        IOUtils.close(out);
        if (mytcp.myrecd != null) {
            mytcp.myrecd.destoryIn();
        }
    }

    /**
     * 登陆
     */
    private void Login() {
        if (!mytcp.isPassiveMode()) {
            connect();
        } else {
            accept();
        }
    }

    /**
     * 检查ip
     *
     * @param ip
     * @return boolean
     */
    private boolean cheekIp(String ip) {
        return TextUtils.checkIpRange(mytcp.getHost(), ip);
    }

    /**
     * 检查帐号
     *
     * @param Ac
     * @return boolean
     */
    private boolean cheekAccount(String Ac) {
        try {
            DataCommand cmd = new DataCommand(Ac);
            if (cmd.getItemValue("name").equals(mytcp.getUserName()) && cmd.getItemValue("pwd").equals(mytcp.getPassWord())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 监听
     */
    private void accept() {
        ConnectInfoEvent myevent;
        if (ssc == null) {
            try {
                ssc = new ServerSocket(mytcp.getPort());
                myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.ACCEPT_OK, "开始监听端口" + mytcp.getPort() + "！");
                mytcp.fireOnConnectInfo(myevent);
                log.warn("开始监听端口" + mytcp.getPort() + "！");
            } catch (Exception e) {
                myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.ACCEPT_ERR, "监听端口" + mytcp.getPort() + "失败！");
                mytcp.fireOnConnectInfo(myevent);
                log.error("监听端口" + mytcp.getPort() + "失败！");
                return;
            }
        }
        while (!exitSign) {
            try {
                close();
                sc = ssc.accept();
            } catch (Exception e) {
                IOUtils.close(sc);
                try {
                    sleep(5000);
                } catch (InterruptedException ie) {
                    continue;
                }
            }
            String ip = "";
            if (sc != null) {
                try {
                    ip = sc.getInetAddress().getHostAddress();
                    if (cheekIp(ip)) {
                        myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.CONNECT_OK, "[" + ip + ":" + sc.getPort() + "]连接成功！");
                        mytcp.fireOnConnectInfo(myevent);
                        log.warn("[" + ip + ":" + sc.getPort() + "]连接成功！");
                    } else {
                        throw new Exception("[" + ip + ":" + sc.getPort() + "]非法ip,连接被拒绝！");
                    }
                    try {
                        sc.setSoTimeout(60000);
                        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                        out = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream()));
                    } catch (Exception ioe) {
                        throw new Exception("[" + ip + ":" + sc.getPort() + "]创建输入输出流失败！");
                    }
                    String strResult = "";
                    try {
                        strResult = in.readLine();
                    } catch (Exception ie) {
                        throw new Exception("[" + ip + ":" + sc.getPort() + "]等待登录超时！");
                    }
                    if (cheekAccount(strResult)) {
                        try {
                            out.write("Pass\r\n");
                            out.flush();
                        } catch (Exception ee) {
                            throw new Exception("[" + ip + ":" + sc.getPort() + "]返回注册状态信息失败！");
                        }
                        log.warn("[" + ip + ":" + sc.getPort() + "]注册成功！");
                        myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.LOGIN_OK, "[" + ip + ":" + sc.getPort() + "]注册成功！");
                        mytcp.fireOnConnectInfo(myevent);
                        mytcp.setLogin(true);
                        if (mytcp.myrecd != null) {
                            mytcp.myrecd.setin(in);
                        }
                        break;
                    } else {
                        throw new Exception("[" + ip + ":" + sc.getPort() + "]账号认证失败！");
                    }
                } catch (Exception e) {
                    close();
                    if (!exitSign) {
                        if (e.getMessage().endsWith("账号认证失败！")) {
                            myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.LOGIN_ERR, e.getMessage());
                        } else {
                            myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.CONNECT_ERR, e.getMessage());
                        }
                        mytcp.fireOnConnectInfo(myevent);
                        log.warn(e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 连接
     */
    private void connect() {
        ConnectInfoEvent myevent;
        while (!exitSign) {
            try {
                close();
                try {
                    myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.CONNECTING, "开始连接" + mytcp.getHost() + ":" + mytcp.getPort() + "！");
                    mytcp.fireOnConnectInfo(myevent);
                    sc = new Socket(mytcp.getHost(), mytcp.getPort());
                    myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.CONNECT_OK, "连接成功！");
                    mytcp.fireOnConnectInfo(myevent);
                    log.warn("连接成功！");
                } catch (IOException ie) {
                    throw new Exception("连接失败！");
                }
                try {
                    sc.setReceiveBufferSize(sc.getReceiveBufferSize() * 2);
                    sc.setSendBufferSize(sc.getSendBufferSize() * 2);
                    sc.setSoTimeout(60000);
                    in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream()));
                } catch (Exception ioe) {
                    throw new Exception("创建输入输出流失败！");
                }
                String strResult = "";
                try {
                    out.write("Login Name=" + mytcp.getUserName() + "&Pwd=" + mytcp.getPassWord() + "&Type=" + mytcp.getType() + "\r\n");
                    out.flush();
                } catch (Exception oe) {
                    throw new Exception("发送注册信息失败！");
                }
                try {
                    strResult = in.readLine();
                } catch (Exception ie) {
                    throw new Exception("等待认证返回信息超时！");
                }
                if (strResult == null) {
                    strResult = "";
                }
                if (strResult.toLowerCase().equals("pass")) {
                    myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.LOGIN_OK, "注册成功！");
                    mytcp.fireOnConnectInfo(myevent);
                    mytcp.setLogin(true);
                    if (mytcp.myrecd != null) {
                        mytcp.myrecd.setin(in);
                    }
                    log.warn("注册成功！");
                    break;
                } else {
                    throw new Exception("账号认证失败！");
                }
            } catch (Exception e) {
                close();
                if (!exitSign) {
                    if (e.getMessage().equals("账号认证失败！")) {
                        myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.LOGIN_ERR, e.getMessage() + "10秒钟后重试");
                    } else {
                        myevent = new ConnectInfoEvent(mytcp, ConnectInfoEvent.CONNECT_ERR, e.getMessage() + "10秒钟后重试");
                    }
                    mytcp.fireOnConnectInfo(myevent);
                    log.error(e.getMessage() + "10秒钟后重试！");
                    try {
                        sleep(10000);
                    } catch (InterruptedException ee) {
                    }
                }
            }
        }
    }
}
