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
 * <p>Description: 接收处理消息</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 * @param <E>
 *
 */
public class JmsConsumer<E> {

    private Buffer<E> queue = null; //内存队列
    private QueueClient<E> client = null;
    private final Logger log = LogFactory.getLogger(JmsConsumer.class);
    private final List<MessageConsumer> threads = new ArrayList<MessageConsumer>();
    private final List<MessageRecder> connects = new ArrayList<MessageRecder>();

    public JmsConsumer(QueueClient<E> client) throws Exception {
        this(client, 1, 3, 10);
    }

    /**
     * 创建接收客户端
     *
     * @param client 下载业务接口
     * @param connectionCount 下载线程数
     * @param threadCount 处理数据线程数
     * @param buffersize 缓存大小
     * @throws Exception
     */
    public JmsConsumer(QueueClient<E> client, int connectionCount, int threadCount, int buffersize) throws Exception {
        this.client = client;
        if (client == null) {
            throw new Exception("没有指定客户端！");
        }
        connectionCount = connectionCount < 1 || connectionCount > 10 ? 1 : connectionCount;
        threadCount = threadCount < 1 || threadCount > 100 ? 1 : threadCount;
        buffersize = buffersize < 1 || buffersize > 1000000 ? 10 : buffersize;
        for (int ii = 0; ii < connectionCount; ii++) {
            connects.add(new MessageRecder(client));
        }
        String buffername = getBufferName();
        queue = new Buffer<E>(buffername, buffersize);
        this.client.buffer = queue;
        log.warn("创建磁盘缓存[{0}],队列长度{1}", new Object[]{buffername, threadCount * 10});
        for (Thread t : connects) {
            t.start();
        }
        for (int ii = 0; ii < threadCount; ii++) {
            MessageConsumer mc = new MessageConsumer();
            mc.start();
            threads.add(mc);
        }
        log.warn("JMS接收程序[" + client.serviceURL + "]启动！连接数:" + String.valueOf(connectionCount) + ",线程数:" + String.valueOf(threadCount));
    }

    /**
     * 获取连接数
     *
     * @return int
     */
    public int getConnectionCount() {
        return connects.size();
    }

    /**
     * 获取线程数
     *
     * @return int
     */
    public int getThreadCount() {
        return threads.size();
    }

    /**
     * 关闭发送
     */
    public void stop() {       
        for (MessageRecder t : connects) {
            t.interrupt();
        }
        for (MessageRecder t : connects) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        for (MessageConsumer t : threads) {
            t.interrupt();
        }
        for (MessageConsumer t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        queue.close();
        log.warn("JMS接收程序[" + client.serviceURL + "]安全退出！");
    }

    /**
     * 获取缓存队列名称
     *
     * @return String
     */
    private String getBufferName() {
        ServiceConfig sc = ServiceFactory.getServiceConfig();
        if (sc != null) {
            return "service_" + String.valueOf(sc.getId()) + "_recder";
        }
        SoapContext sct = SoapFactory.getSoapContext();
        if (sct != null) {
            return "soapservice_" + String.valueOf(sct.getId()) + "_recder";
        }
        WebDirectory wd = WebFactory.getWebDirectory();
        if (wd != null) {
            return "webservice_" + String.valueOf(wd.getId()) + "_recder";
        }
        return Crc64.generateCrc64(client.serviceURL.toLowerCase().trim()) + "_recder";
    }

    private class MessageConsumer extends Thread {

        private E data = null;

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    if (client == null) {
                        log.error("接口[" + client.serviceURL + "]没有指定处理类！");
                        sleep(1000 * 60);
                        continue;
                    }
                    if (data == null) {
                        data = queue.take();
                    }
                    if (data != null) {
                        client.doMessage(data);
                        data = null;
                    }
                } catch (InterruptedException ex) {
                    break;
                } catch (Throwable ex) {
                    log.trace("处理接口[" + client.serviceURL + "]数据时发生错误！", ex);
                    try {
                        sleep(10000);
                    } catch (InterruptedException ex1) {
                        break;
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

    /**
     * 发送线程
     */
    private class MessageRecder extends Thread {

        private QueueClient<E> jq = null;
        private E data = null;

        public MessageRecder(QueueClient<E> jq) {
            this.jq = jq;
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    if (data == null) {
                        data = jq.poll(10000);
                    }
                    if (data != null) {
                        queue.put(data);
                        data = null;
                    } else {
                        sleep(5000);
                    }
                } catch (InterruptedException ex) {
                    break;
                } catch (Throwable ex) {
                    log.trace("访问远程接口[" + client.serviceURL + "]失败！", ex);
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
