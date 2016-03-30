/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.util;

import com.surfs.nas.transport.ThreadPool;
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
        ThreadPool.stopThread(check);
    }

    /**
     */
    public static void disposeByteBuffer() {
        while (queue.size() > minPoolSize) {
            if (queue.poll() != null) {
                curPoolSize.decrementAndGet();
            }
        }
    }

    /**
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

}
