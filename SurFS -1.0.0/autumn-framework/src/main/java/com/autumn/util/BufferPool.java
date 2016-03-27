/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.util;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BufferPool {

    private static final int defaultBlockSize = 1024 * (512 + 16);
    private static final int minPoolSize = 333;
    private static final int maxPoolSize = minPoolSize * 3;
    private static final ConcurrentLinkedQueue<ByteBuffer> queue = new ConcurrentLinkedQueue();
    private static final AtomicInteger curPoolSize = new AtomicInteger(0);

    static {
        Thread check = new Thread() {
            @Override
            public void run() {
                for (;;) {
                    try {
                        sleep(1000 * 60 * 10);
                        disposeByteBuffer();
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
        };
        check.setDaemon(true);
        com.autumn.core.ThreadPools.stopThread(check);
    }

    /**
     * 释放多余的
     */
    public static void disposeByteBuffer() {
        while (queue.size() > minPoolSize) {
            if (queue.poll() != null) {
                curPoolSize.decrementAndGet();
            }
        }
    }

    /**
     * 回收内存
     *
     * @param buf
     */
    public static void freeByteBuffer(ByteBuffer buf) {
        if (buf != null) {
            if (buf.isDirect()) {
                buf.clear();
                queue.offer(buf);
            }
        }
    }

    /**
     * 从内存中取出buf
     *
     * @return
     */
    public static ByteBuffer getByteBuffer() {
        ByteBuffer buf = queue.poll();
        if (buf == null) {
            buf = createByteBuffer();
        }
        return buf;
    }

    /**
     * 申请内存
     *
     * @return
     */
    private static ByteBuffer createByteBuffer() {
        ByteBuffer buf;
        if (curPoolSize.get() < maxPoolSize) {
            buf = ByteBuffer.allocateDirect(defaultBlockSize);
            curPoolSize.incrementAndGet();
        } else {
            buf = ByteBuffer.allocate(defaultBlockSize);
        }
        return buf;
    }

    public static String getInfo() {
        return "总数:" + curPoolSize.get() + ",空闲:" + queue.size();
    }
}
