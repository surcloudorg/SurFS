/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.transport;
 
import com.surfs.nas.util.BufferPool;
import static com.surfs.nas.GlobleProperties.charset;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

public abstract class TcpCommandDecoder extends Thread {

    private static final Logger log = Logger.getLogger(TcpCommandDecoder.class);

    private final LinkedBlockingQueue<ByteBuffer> queue = new LinkedBlockingQueue(100);
    private ByteBuffer curBuffer;
    private boolean exiting = false;

    /**
     * @param buf
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    public void addByteBuffer(ByteBuffer buf) throws InterruptedException, IOException {
        if (exiting) {
            BufferPool.freeByteBuffer(curBuffer);
            throw new IOException("decoder closed!");
        } else {
            queue.put(buf);
        }
    }

    /**
     * decode
     *
     * @throws Throwable
     */
    public abstract void decode() throws Throwable;

    /**
     * close decoder
     */
    public void shutdown() {
        exiting = true;
        if (currentThread != null) {
            currentThread.interrupt();
        }
    }

    private Thread currentThread = null;

    @Override
    public void run() {
        currentThread = Thread.currentThread();
        while (!exiting) {
            try {
                decode();
            } catch (java.lang.InterruptedException ie) {
                break;
            } catch (Throwable s) {
                if (!exiting) {
                    log.trace("decode err:", s);
                }
                break;
            }
        }
        while (curBuffer != null) {
            BufferPool.freeByteBuffer(curBuffer);
            curBuffer = queue.poll();
        }
    }

    /**
     *
     * @throws IOException
     */
    private void getNextBuffer() throws IOException {
        try {
            if (curBuffer == null) {
                curBuffer = queue.take();
            } else {
                if (!curBuffer.hasRemaining()) {
                    BufferPool.freeByteBuffer(curBuffer);
                    curBuffer = null;
                    curBuffer = queue.take();
                }
            }
        } catch (InterruptedException d) {
            Thread.currentThread().interrupt();
            throw new IOException(d);
        }
    }

    /**
     *
     * @return String
     * @throws IOException
     */
    public String readString() throws IOException {
        int len = readInt();
        byte[] bs = readBytes(len);
        return bs == null ? null : new String(bs, charset);
    }

    /**
     *
     * @return Boolean
     * @throws IOException
     */
    public boolean readBoolean() throws IOException {
        return readByte() != 0x00;
    }

    /**
     *
     * @return byte
     * @throws IOException
     */
    public byte readByte() throws IOException {
        getNextBuffer();
        return curBuffer.get();
    }

    /**
     *
     * @return long
     * @throws IOException
     */
    public long readLong() throws IOException {
        getNextBuffer();
        if (curBuffer.remaining() >= 8) {
            return curBuffer.getLong();
        } else {
            byte[] bs = readBytes(8);
            return com.surfs.nas.util.Function.byte2long(bs);
        }
    }

    /**
     *
     * @return int
     * @throws IOException
     */
    public int readInt() throws IOException {
        getNextBuffer();
        if (curBuffer.remaining() >= 4) {
            return curBuffer.getInt();
        } else {
            byte[] bs = readBytes(4);
            return com.surfs.nas.util.Function.byte2int(bs);
        }
    }

    /**
     *
     * @return byte[]
     * @throws IOException
     */
    public byte[] readBytes() throws IOException {
        int len = readInt();
        return readBytes(len);
    }

    /**
     *
     * @param len
     * @return byte[]
     * @throws IOException
     */
    private byte[] readBytes(int len) throws IOException {
        if (len > 0) {
            byte[] bs = new byte[len];
            int count = 0;
            while (count < len) {
                getNextBuffer();
                int remaining = curBuffer.remaining();
                int needing = len - count;
                if (remaining >= needing) {
                    curBuffer.get(bs, count, needing);
                    count = count + needing;
                } else {
                    curBuffer.get(bs, count, remaining);
                    count = count + remaining;
                }
            }
            return bs;
        } else {
            return null;
        }
    }
}
