/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.jms;

import java.io.IOException;

/**
 * <p>Title: 远程磁盘缓存接口</p>
 *
 * <p>Description: 定义远程访问缓存的接口</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public interface Queue {

    /**
     * 队列长度
     *
     * @return int
     */
    public int size();

    /**
     * 队列空位
     *
     * @return int
     */
    public int remainingCapacity();

    /**
     * 加入队列
     *
     * @param ss
     * @param timeout
     */
    public void offer(Message ss, long timeout) throws IOException;

    /**
     * 加入队列
     *
     * @param ss
     */
    public void add(Message ss) throws IOException;

    /**
     * 取出数据
     *
     * @param timeout
     * @return Message
     * @throws IOException
     */
    public Message poll(long timeout) throws IOException;

    /**
     * 取出数据
     *
     * @return Message
     * @throws IOException
     */
    public Message poll() throws IOException;
}
