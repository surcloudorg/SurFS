package com.autumn.core.jms;

import java.util.concurrent.Callable;

/**
 * <p>Title: 磁盘缓存消息处理器</p>
 *
 * <p>Description: 磁盘缓存消息处理器</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 * @param <V>
 *
 */
public class ErrorMessageCaller<V> implements Callable {

    private MessageWrapper<V> message = null;
    private final ErrorBuffer<V> smbuffer;

    public ErrorMessageCaller(MessageWrapper<V> message, ErrorBuffer<V> smbuffer) {
        this.message = message;
        this.smbuffer = smbuffer;
    }

    @Override
    public Object call() throws Exception {
        try {
            smbuffer.handler.doMessage(message);
        } catch (Throwable ex) {
            smbuffer.log.trace("执行doMessage(" + message.getMessage().toString() + ")失败!", ex, smbuffer.handler.getClass());
            message.lastHT = System.currentTimeMillis();
            smbuffer.writer.write(message);
        }
        return null;
    }
}
