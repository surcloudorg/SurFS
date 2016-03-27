/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.tcp;

import com.autumn.core.DataCommand;
import com.autumn.core.jms.Buffer;
import com.autumn.core.log.Logger;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * <p>Title:Tcp客户端/服务端组件</p>
 *
 * <p>Description:Tcp客户端/服务端组件</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @param passiveMode boolean 连接模式，作为服务端/客户端，false（客户端），true（服务端）
 * @param host String 主机地址，如果是服务端，根据此地址限制客户端登陆，如果作为客户端，此地址就是目标地址
 * @param port int 主机端口，如果是服务端，为监听端口，如果作为客户端，就是目标端口
 * @param userName String 连接帐号
 * @param passWord String 连接密码
 * @param type int 连接类型,0双向，1接收，2发送，3接收，4发送，3，4需要等待接收方回应
 * @param recdThread int 接收处理线程数，接收事件OnReceive采用并发处理
 * @param bufSize int 发送队列最大缓存数
 * @param lostRecdData boolean 是否忽略接收数据处理
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class TcpConnect {

    private int bufSize = 50;
    private String host = "";
    private int port = 8020;
    private String userName = "";
    private String passWord = "";
    private int type = 0;
    private boolean passiveMode = false;
    private int recdThread = 1;
    private Buffer<DataCommand> recdBuffer = null;
    private Buffer<DataCommand> sendBuffer = null;
    private Logger log = null;
    private boolean lostRecdData = false;
    protected int recCount = 0; //接收记数
    protected int sndCount = 0; //发送计数
    protected boolean login = false; //登陆状态
    protected StopEvent evtData = null; //终止连接后的缓存数据
    protected BlockingQueue<ReceiveEvent> recdQueue = null; //接收指令队列
    protected BlockingQueue<DataCommand> sendQueue = null; //发送指令队列
    protected TcpRecd myrecd = null; //接收线程
    protected TcpSend mysend = null; //发送线程
    protected List<TaskExecuter> recdTaskList = null; //处理接收数据的线程队列
    

    /**
     * 设置登录状态
     *
     * @param login
     */
    protected void setLogin(boolean login) {
        this.login = login;
    }

    /**
     * 启动接收处理线程
     */
    private void startRecdTask() {
        if (recdTaskList == null) {
            if (recdThread < 1) {
                recdThread = 1;
            }
            if (recdThread > 10) {
                recdThread = 10;
            }
            recdTaskList = new ArrayList<TaskExecuter>();
            for (int ii = 0; ii < recdThread; ii++) {
                TaskExecuter myrec = new TaskExecuter(this);
                myrec.start();
                recdTaskList.add(myrec);
            }
        }
    }

    /**
     * 终止接收处理线程
     */
    private void stopRecdTask() {
        if (recdTaskList != null) {
            for (TaskExecuter myrec : recdTaskList) {
                if (myrec != null) {
                    myrec.interrupt();
                    try {
                        myrec.join(15000);
                    } catch (Exception ee) {
                    }
                }
            }
        }
        recdTaskList = null;
    }

    /**
     * 启动连接，检查参数设置，如果参数错误抛出错误。 启动发送/接收线程
     */
    public void start() throws Exception {
        if (mysend != null) {
            throw new Exception("Tcp连接线程已经运行");
        }
        if (!passiveMode) {
            if (host.equals("")) {
                throw new Exception("Host参数没有设置");
            }
            try {
                InetAddress.getByName(host);
            } catch (Exception e) {
                throw new Exception("Host参数（" + host + "）设置错误");
            }
        }
        if (type < 0 || type > 4) {
            throw new Exception("type参数（" + type + "）设置错误");
        }
        if (port < 1) {
            throw new Exception("port参数（" + port + "）设置错误");
        }
        if (userName.equals("")) {
            throw new Exception("userName参数没有设置");
        }
        if (passWord.equals("")) {
            throw new Exception("passWord参数没有设置");
        }
        if (bufSize < 10) {
            bufSize = 10;
        }
        sendQueue = new ArrayBlockingQueue<DataCommand>(bufSize);
        recdQueue = new ArrayBlockingQueue<ReceiveEvent>(bufSize);
        if (receiveListeners != null) {
            startRecdTask();
        }
        myrecd = new TcpRecd(this);
        mysend = new TcpSend(this);
        mysend.start();
        myrecd.start();
    }

    /**
     * 设置主机地址，如果连接已经启动，设置无效
     *
     * @param host String ip/域名/主机名
     */
    public void setHost(String host) {
        if (mysend != null) {
            return;
        }
        this.host = host == null ? "" : host.trim();
    }

    /**
     * 设置主机端口，如果连接已经启动，设置无效
     *
     * @param port int 端口号
     */
    public void setPort(int port) {
        if (mysend != null) {
            return;
        }
        this.port = port;
    }

    /**
     * 设置用户名，如果连接已经启动，设置无效
     *
     * @param userName String
     */
    public void setUserName(String userName) {
        if (mysend != null) {
            return;
        }
        this.userName = userName == null ? "" : userName.trim();
    }

    /**
     * 设置密码，如果连接已经启动，设置无效
     *
     * @param passWord String
     */
    public void setPassWord(String passWord) {
        if (mysend != null) {
            return;
        }
        this.passWord = passWord == null ? "" : passWord.trim();
    }

    /**
     * 设置连接类型，如果连接已经启动，设置无效
     *
     * @param type int
     */
    public void setType(int type) {
        if (mysend != null) {
            return;
        }
        this.type = type;
    }

    /**
     * 设置连接模式，如果连接已经启动，设置无效
     *
     * @param passiveMode boolean
     */
    public void setPassiveMode(boolean passiveMode) {
        if (mysend != null) {
            return;
        }
        this.passiveMode = passiveMode;
    }

    /**
     * 设置接收buffer队列实例
     *
     * @param recdBuffer
     */
    public void setRecdBuffer(Buffer<DataCommand> recdBuffer) {
        if (mysend != null) {
            return;
        }
        this.recdBuffer = recdBuffer;
    }

    /**
     * 设置发送buffer队列实例
     *
     * @param sendBuffer
     */
    public void setSendBuffer(Buffer<DataCommand> sendBuffer) {
        if (mysend != null) {
            return;
        }
        this.sendBuffer = sendBuffer;
    }

    /**
     * 设置日志
     *
     * @param log
     */
    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     * 设置处理线程数，如果连接已经启动，设置无效
     *
     * @param maxRecdThread int 端口号
     */
    public void setRecdThread(int maxRecdThread) {
        if (recdTaskList != null) {
            return;
        }
        this.recdThread = maxRecdThread;
    }

    /**
     * 设置缓存大小，如果连接已经启动，设置无效
     *
     * @param BufSize int 端口号
     */
    public void setBufSize(int BufSize) {
        if (mysend != null) {
            return;
        }
        if (BufSize > 100) {
            BufSize = 100;
        }
        if (BufSize < 10) {
            BufSize = 10;
        }
        this.bufSize = BufSize;
    }

    /**
     * 获取主机地址
     *
     * @return host String
     */
    public String getHost() {
        return host;
    }

    /**
     * 获取主机端口
     *
     * @return port int
     */
    public int getPort() {
        return port;
    }

    /**
     * 获取用户名
     *
     * @return userName String
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 获取密码
     *
     * @return passWord String
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * 获取连接类型
     *
     * @return type int
     */
    public int getType() {
        return type;
    }

    /**
     * 获取登陆状态
     *
     * @return boolean，true（已登陆）/false（未登陆）
     */
    public boolean isLogin() {
        return login;
    }

    /**
     * 获取连接模式
     *
     * @return passiveMode boolean
     */
    public boolean isPassiveMode() {
        return passiveMode;
    }

    /**
     * 获取接收buffer队列实例
     *
     * @return Buffer<DataCommand>
     */
    public Buffer<DataCommand> getRecdBuffer() {
        return recdBuffer;
    }

    /**
     * 获取发送buffer队列实例
     *
     * @return Buffer<DataCommand>
     */
    public Buffer<DataCommand> getSendBuffer() {
        return sendBuffer;
    }

    /**
     * 获取日志
     *
     * @return Logger
     */
    public Logger getLog() {
        return log;
    }

    /**
     * 获取已接收的命令个数
     *
     * @return int
     */
    public int getRecCount() {
        return recCount;
    }

    /**
     * 获取已发送的命令个数
     *
     * @return int
     */
    public int getSndCount() {
        return sndCount;
    }

    /**
     * 获取处理线程个数
     *
     * @return RecdThread int
     */
    public int getRecdThread() {
        return recdThread;
    }

    /**
     * 获取缓存大小
     *
     * @return BufSize int
     */
    public int getBufSize() {
        return bufSize;
    }

    /**
     * 获取连接终止后的缓存数据
     *
     * @return StopEvent
     */
    public StopEvent getStopEvent() {
        return evtData;
    }

    /**
     * 接收数据，如果组件没有调用addReceiveListener加入事件接收器，可直接调用
     * BlockReceive()获取数据，如果没有数据线程被阻塞
     *
     * @return TcpCommand
     */
    public DataCommand blockReceive() throws InterruptedException, IOException {
        if (receiveListeners != null) {
            if (!receiveListeners.isEmpty()) {
                return null;
            }
        }
        if (recdBuffer != null) {
            return recdBuffer.take();
        } else {
            ReceiveEvent mye = recdQueue.take();
            DataCommand mycmd = new DataCommand(mye.getCommand());
            return mycmd;
        }
    }

    /**
     * 接收数据，如果组件没有调用addReceiveListener加入事件接收器，可直接调用
     * BlockReceive()获取数据，如果没有数据返回null
     *
     * @return TcpCommand
     */
    public DataCommand receive() throws IOException {
        if (receiveListeners != null) {
            if (!receiveListeners.isEmpty()) {
                return null;
            }
        }
        if (recdBuffer != null) {
            DataCommand mycmd = recdBuffer.poll();
            if (mycmd == null) {
                return null;
            } else {
                return mycmd;
            }
        } else {
            ReceiveEvent mye = recdQueue.poll();
            if (mye == null) {
                return null;
            } else {
                return mye.getCommand();
            }
        }
    }

    /**
     * 发送数据，阻塞模式
     */
    public void blockSend(DataCommand comm) throws InterruptedException,
            Exception {
        if (comm == null) {
            throw new Exception("指令为空");
        }
        if (!isPassiveMode()) {
            if (getType() == 1 || getType() == 3) {
                throw new Exception("连接类型不正确，不能执行发送命令");
            }
        }
        if (!comm.getCommID().trim().equals("")) {
            if (sendBuffer != null) {
                sendBuffer.put(comm);
            } else {
                sendQueue.put(comm);
            }
        } else {
            throw new Exception("没有命令标识");
        }
    }

    /**
     * 发送数据，非阻塞模式
     *
     * @return boolean,true(发送成功)/false(发送失败)
     */
    public boolean send(DataCommand comm) throws Exception {
        if (comm == null) {
            throw new Exception("指令为空");
        }
        if (!isPassiveMode()) {
            if (getType() == 1 || getType() == 3) {
                throw new Exception("连接类型不正确，不能执行发送命令");
            }
        }
        if (!comm.getCommID().trim().equals("")) {
            if (!isLogin()) {
                return false;
            }
            if (sendBuffer != null) {
                sendBuffer.add(comm);
                return true;
            } else {
                return sendQueue.offer(comm);
            }
        } else {
            throw new Exception("没有命令标识");
        }
    }

    /**
     * 检查当前发送线程是否空闲
     *
     * @return boolean,true(可以发送)/false(不能发送)
     */
    public boolean sendReady() {
        if (!isPassiveMode()) {
            if (getType() == 1 || getType() == 3) {
                return false;
            }
        }
        if (!isLogin()) {
            return false;
        }
        if (sendBuffer != null) {
            return true;
        } else {
            if (sendQueue.size() < bufSize) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 断开当前连接
     */
    public void close() {
        if (mysend != null) {
            mysend.close();
        }
    }

    /**
     * 终止连接
     */
    public void stop() {
        if (myrecd != null) {
            myrecd.interrupt();
        }
        if (mysend != null) {
            mysend.terminate();
        }
        if (myrecd != null) {
            try {
                myrecd.join(15000);
            } catch (Exception e) {
            }
        }
        if (mysend != null) {
            mysend.interrupt();
        }
        if (mysend != null) {
            try {
                mysend.join(15000);
            } catch (Exception e) {
            }
        }
        stopRecdTask();
    }
    transient List<ConnectInfoListener> connectInfoListeners;

    /**
     * 设置连接信息监听器
     *
     * @param l
     */
    public synchronized void addConnectInfoListener(ConnectInfoListener l) {
        if (connectInfoListeners == null) {
            connectInfoListeners = new ArrayList<ConnectInfoListener>(2);
        }
        if (!connectInfoListeners.contains(l)) {
            connectInfoListeners.add(l);
        }
    }

    /**
     * 移出连接信息监听器
     *
     * @param l
     */
    public synchronized void removeConnectInfoListener(ConnectInfoListener l) {
        if (connectInfoListeners != null && connectInfoListeners.contains(l)) {
            connectInfoListeners.remove(l);
        }
    }

    /**
     * 处理连接信息
     *
     * @param event
     */
    protected void fireOnConnectInfo(ConnectInfoEvent event) {
        if (connectInfoListeners != null) {
            try {
                for (ConnectInfoListener cl : connectInfoListeners) {
                    cl.onConnectInfo(event);
                }
            } catch (Exception e) {
            }
        }
    }
    transient List<ReceiveListener> receiveListeners;

    /**
     * 设置数据处理监听器
     *
     * @param l
     */
    public synchronized void addReceiveListener(ReceiveListener l) {
        if (receiveListeners == null) {
            receiveListeners = new ArrayList<ReceiveListener>(2);
        }
        if (!receiveListeners.contains(l)) {
            receiveListeners.add(l);
            if (recdQueue != null) {
                startRecdTask();
            }
        }
    }

    /**
     * 移出数据处理监听器
     *
     * @param l
     */
    public synchronized void removeReceiveListener(ReceiveListener l) {
        if (receiveListeners != null && receiveListeners.contains(l)) {
            receiveListeners.remove(l);
            if (receiveListeners.isEmpty()) {
                if (mysend != null) {
                    stopRecdTask();
                }
            }
        }
    }

    /**
     * 处理数据
     *
     * @param event
     * @throws InterruptedException
     */
    protected void fireOnReceive(ReceiveEvent event) throws InterruptedException {
        if (lostRecdData) {
            return;
        }
        if (recdBuffer != null) {
            try {
                recdBuffer.add(event.getCommand());
            } catch (Exception e) {
            }
            return;
        }
        if (receiveListeners != null) {
            int count = receiveListeners.size();
            if (count == 0) {
                recdQueue.put(event);
            } else {
                if (recdTaskList != null) {
                    recdQueue.put(event);
                } else {
                    try {
                        for (ReceiveListener rl : receiveListeners) {
                            rl.onReceive(event);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            recdQueue.put(event);
        }
    }
    transient List<StopListener> stopListeners;

    /**
     * 设置连接终止事件处理实例
     *
     * @param l
     */
    public synchronized void addStopListener(StopListener l) {
        if (stopListeners == null) {
            stopListeners = new ArrayList<StopListener>(2);
        }
        if (!stopListeners.contains(l)) {
            stopListeners.add(l);
        }
    }

    /**
     * 移出连接终止事件处理实例
     *
     * @param l
     */
    public synchronized void removeStopListener(StopListener l) {
        if (stopListeners != null && stopListeners.contains(l)) {
            stopListeners.remove(l);
        }
    }

    /**
     * 终止事件处理
     *
     * @param event
     */
    protected void fireOnStop(StopEvent event) {
        if (stopListeners != null) {
            int count = stopListeners.size();
            if (count == 0) {
                evtData = event;
            } else {
                try {
                    for (StopListener sl : stopListeners) {
                        sl.onStop(event);
                    }
                } catch (Exception e) {
                }
            }
        } else {
            evtData = event;
        }
    }

    /**
     * @return the lostRecdData
     */
    public boolean isLostRecdData() {
        return lostRecdData;
    }

    /**
     * @param lostRecdData the lostRecdData to set
     */
    public void setLostRecdData(boolean lostRecdData) {
        this.lostRecdData = lostRecdData;
    }
}
