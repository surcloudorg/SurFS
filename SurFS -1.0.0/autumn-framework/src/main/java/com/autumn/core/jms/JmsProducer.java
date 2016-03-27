/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.jms;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.core.security.Crc64;
import com.autumn.core.service.ServiceConfig;
import com.autumn.core.service.ServiceFactory;
import com.autumn.core.soap.SoapContext;
import com.autumn.core.soap.SoapFactory;
import com.autumn.core.web.WebDirectory;
import com.autumn.core.web.WebFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: 远程磁盘缓存-JMS客户端</p>
 *
 * <p>Description: 发布消息</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class JmsProducer<E> {

    private Buffer<E> queue = null; //内存队列
    private QueueClient<E> client = null;
    private List<MessageSender> threads = new ArrayList<MessageSender>();
    private Logger log = LogFactory.getLogger(JmsProducer.class);
    private long timeout = 0;//>0:如果队列满时，等？毫秒，=0时:如果队列满时,写磁盘

    public JmsProducer(QueueClient<E> client) throws Exception {
        this(client, 1, 0, 10);
    }

    /**
     * 创建发送客户端
     *
     * @param client 发送业务实现
     * @param connectionCount 发送线程数
     * @param timeout 发送超时
     * @param buffersize 缓存大小
     * @throws Exception
     */
    public JmsProducer(QueueClient<E> client, int connectionCount, long timeout, int buffersize) throws Exception {
        this.client = client;
        if (client == null) {
            throw new Exception("没有指定客户端！");
        }
        this.timeout = timeout;
        connectionCount = connectionCount < 1 || connectionCount > 10 ? 1 : connectionCount;
        for (int ii = 0; ii < connectionCount; ii++) {
            threads.add(new MessageSender(client));
        }
        buffersize = buffersize < 1 || buffersize > 1000000 ? 10 : buffersize;
        String buffername = getBufferName();
        queue = new Buffer<E>(buffername, buffersize);
        log.warn("创建磁盘缓存[{0}],队列长度{1}", new Object[]{buffername, connectionCount * 10});
        for (Thread t : threads) {
            t.start();
        }
        log.warn("JMS发送程序[" + client.serviceURL + "]启动！连接数:" + String.valueOf(connectionCount));
    }

    /**
     * 发送消息
     *
     * @param msg
     * @throws InterruptedException
     */
    public void sendMessageWithBlock(E msg) throws InterruptedException {
        queue.put(msg);
    }

    /**
     * 发送消息
     *
     * @param msg
     * @param timeout
     * @throws IOException
     */
    public void sendMessageWithWait(E msg, long timeout) throws IOException {
        queue.offer(msg, timeout);
    }

    /**
     * 发送消息
     *
     * @param msg
     * @throws IOException
     */
    public void sendMessage(E msg) throws IOException {
        queue.add(msg);
    }

    /**
     * 获取缓存队列最大长度
     *
     * @return int
     */
    public int getMemMaxLine() {
        return queue.getMemMaxLine();
    }

    /**
     * 获取连接数
     *
     * @return int
     */
    public int getConnectionCount() {
        return threads.size();
    }

    /**
     * 关闭发送
     */
    public void stop() {
        for (MessageSender t : threads) {
            t.interrupt();
        }
        for (MessageSender t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        queue.close();
        log.warn("JMS发送程序[" + getClient().serviceURL + "]安全退出！");
    }

    /**
     * 获取缓存队列名称
     *
     * @return String
     */
    private String getBufferName() {
        ServiceConfig sc = ServiceFactory.getServiceConfig();
        if (sc != null) {
            return "service_" + String.valueOf(sc.getId()) + "_sender";
        }
        SoapContext sct = SoapFactory.getSoapContext();
        if (sct != null) {
            return "soapservice_" + String.valueOf(sct.getId()) + "_sender";
        }
        WebDirectory wd = WebFactory.getWebDirectory();
        if (wd != null) {
            return "webservice_" + String.valueOf(wd.getId()) + "_sender";
        }
        return Crc64.generateCrc64(getClient().serviceURL.toLowerCase().trim()) + "_sender";
    }

    /**
     * @return the timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * @return the client
     */
    public QueueClient<E> getClient() {
        return client;
    }

    /**
     * 发送线程
     */
    private class MessageSender extends Thread {

        private QueueClient<E> jq = null;
        private E data = null;

        public MessageSender(QueueClient<E> jq) {
            this.jq = jq;
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    if (data == null) {
                        data = queue.take();
                    }
                    if (data != null) {
                        if (getTimeout() <= 0) {
                            jq.add(data);
                        } else {
                            jq.offer(data, getTimeout());
                        }
                        data = null;
                    }
                } catch (InterruptedException ex) {
                    break;
                } catch (Throwable ex) {
                    log.trace("访问远程接口[" + getClient().serviceURL + "]失败！", ex);
                    try {
                        sleep(10000);
                    } catch (InterruptedException ex1) {
                        break;
                    }
                }
            }
            if (data != null) {
                try {
                    queue.add(data);
                } catch (IOException ex) {
                }
            }
        }
    }
}
