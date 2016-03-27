/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.jms;

import com.autumn.core.cfg.Config;
import com.autumn.core.cfg.ConfigListener;
import com.autumn.core.cfg.Method;
import com.autumn.core.cfg.Property;
import com.autumn.core.soap.SoapContext;
import com.autumn.core.soap.SoapInstance;
import java.io.IOException;

/**
 * <p>Title: 磁盘缓存远程访问实现-服务端</p>
 *
 * <p>Description: 磁盘缓存Buffer远程访问实现</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class QueueInstance extends SoapInstance implements Queue, ConfigListener {
    
   
    private Buffer<Message> buffer = null;

    public QueueInstance(SoapContext context) throws Exception {
        super(context);
        String buffername = context.getServicename();
        Config cfg = context.getConfig();
        if (cfg == null) {
            buffer = new Buffer<Message>(buffername);
        } else {
            int maxLine = cfg.getAttributeIntValue("config.maxline", 100);
            long fileMaxSize = cfg.getAttributeLongValue("config.filemaxsize", Buffer.MAX_FILE_SIZE);
            long size = cfg.getAttributeLongValue("config.blocksize", Buffer.MAX_BLOCK_SIZE);
            buffer = new Buffer<Message>(buffername, maxLine, fileMaxSize, size);
        }
    }

    @Override
    public int size() {
        return getBuffer().size();
    }

    @Override
    public int remainingCapacity() {
        return getBuffer().remainingCapacity();
    }

    @Override
    public void offer(Message ss, long timeout) throws IOException {
        getBuffer().offer(ss, timeout);
    }

    @Override
    public void add(Message ss) throws IOException {
        getBuffer().add(ss);
    }

    @Override
    public void contextDestroyed() {
        getBuffer().close();
    }

    @Override
    public Message poll(long timeout) throws IOException {
        return getBuffer().poll(timeout);
    }

    @Override
    public Message poll() throws IOException {
        return getBuffer().poll();
    }

    @Override
    public Object callMethod(Method method) {
        return "队列：" + getBuffer().getBufferName() + "(" + String.valueOf(size()).concat("/").concat(String.valueOf(getBuffer().getMemMaxLine())) + ")";
    }

    @Override
    public boolean changeProperty(Property property) {
        return false;
    }

    /**
     * @return the buffer
     */
    public Buffer<Message> getBuffer() {
        return buffer;
    }
}
