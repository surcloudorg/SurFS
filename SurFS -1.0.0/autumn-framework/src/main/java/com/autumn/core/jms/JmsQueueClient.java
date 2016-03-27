/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.jms;

/**
 * <p>Title: 远程磁盘缓存-JMS客户端</p>
 *
 * <p>Description: 远程磁盘缓存-默认实现</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public abstract class JmsQueueClient<E> extends QueueClient<E> {

    private Queue queue = null;

    /**
     * 创建
     *
     * @throws Exception
     */
    public JmsQueueClient(String serviceURL, String username, String password) throws Exception {
        super(serviceURL, Queue.class);
        queue = (Queue) proxy;
    }

    public JmsQueueClient(String serviceURL) throws Exception {
        this(serviceURL, null, null);
    }

    /**
     * 队列长度
     *
     * @return int
     */
    @Override
    public int size() {
        return queue.size();
    }

    /**
     * 队列剩余空间
     *
     * @return int
     */
    @Override
    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    /**
     * 发送消息
     *
     * @param ss
     * @param timeout
     * @throws Exception
     */
    @Override
    public void offer(E ss, long timeout) throws Exception {
        queue.offer(new Message(ss), timeout);
    }

    /**
     * 发送消息
     *
     * @param ss
     * @throws Exception
     */
    @Override
    public void add(E ss) throws Exception {
        queue.add(new Message(ss));
    }

    /**
     * 取出数据
     *
     * @param timeout
     * @return E
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public E poll(long timeout) throws Exception {
        Message msg = queue.poll(timeout);
        return (E) Message.getObject(msg);
    }

    /**
     * 取出数据
     *
     * @return E
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public E poll() throws Exception {
        Message msg = queue.poll();
        return (E) Message.getObject(msg);
    }
}
