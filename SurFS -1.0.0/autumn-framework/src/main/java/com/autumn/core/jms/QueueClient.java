/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.jms;

import java.io.IOException;

/**
 * <p>Title: 远程磁盘缓存-JMS客户端</p>
 *
 * <p>Description: 远程磁盘缓存</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public abstract class QueueClient<E> extends JmsClient {

    protected Buffer<E> buffer = null;

    /**
     * 处理消息
     *
     * @param message
     * @throws Throwable 如果抛出错误，表示处理失败
     */
    public abstract void doMessage(E message) throws Throwable;

    /**
     * 将消息返还队列等待下次处理
     *
     * @param message
     */
    public void backMessage(E message) {
        try {
            buffer.add(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 当前线程等待？毫秒
     *
     * @param time
     */
    public void pause(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public QueueClient(String serviceURL, Class cls) throws Exception {
        this(serviceURL, null, null, cls);
    }

    public QueueClient(String serviceURL, String username, String password, Class cls) throws Exception {
        super(serviceURL, username, password, cls);
    }

    /**
     * 队列长度
     *
     * @return int
     */
    public abstract int size();

    /**
     * 队列剩余空间
     *
     * @return int
     */
    public abstract int remainingCapacity();

    /**
     * 发送消息
     *
     * @param ss
     * @param timeout
     * @throws IOException
     */
    public abstract void offer(E ss, long timeout) throws Exception;

    /**
     * 发送消息
     *
     * @param ss
     * @throws IOException
     */
    public abstract void add(E ss) throws Exception;

    /**
     * 取出数据
     *
     * @param timeout
     * @return E
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public abstract E poll(long timeout) throws Exception;

    /**
     * 取出数据
     *
     * @return E
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public abstract E poll() throws Exception;
}
