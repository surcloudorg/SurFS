package com.surfs.nas.transport;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.util.BufferPool;
import static com.surfs.nas.GlobleProperties.charset;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class TcpCommandEncoder extends Thread {

    private static final Logger log = LogFactory.getLogger(TcpCommandEncoder.class);

    private final LinkedBlockingQueue<TcpCommand> queue = new LinkedBlockingQueue();
    private ByteBuffer curBuffer;
    private boolean exiting = false;

    /**
     * Send packet
     *
     * @param buf
     * @throws IOException
     */
    public abstract void sendBuffer(ByteBuffer buf) throws IOException;

    /**
     * @throws IOException
     */
    private void getNextBuffer() throws IOException {
        if (curBuffer == null) {
            curBuffer = BufferPool.getByteBuffer();
        } else {
            if (!curBuffer.hasRemaining()) {
                curBuffer.flip();
                sendBuffer(curBuffer);
                curBuffer = BufferPool.getByteBuffer();
            }
        }
    }

    /**
     * @throws IOException
     */
    private void flush() throws IOException {
        if (curBuffer != null) {
            curBuffer.flip();
            sendBuffer(curBuffer);
            curBuffer = null;
        }
    }

    /**
     *
     * @param resp
     * @param waittime
     * @throws IOException
     */
    public void addTcpResponse(TcpCommand resp, long waittime) throws IOException {
        if (exiting) {
            throw new IOException("encoder closed!");
        }
        try {
            boolean b = queue.offer(resp, waittime, TimeUnit.SECONDS);
            if (!b) {
                throw new SocketTimeoutException("add timeout!");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException(ex);
        }
    }

    /**
     * close encoder
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
        TcpCommand cmd;
        while (!exiting) {
            try {
                cmd = queue.take();
                this.writeByte(cmd.getCommandType());
                this.writeInt(cmd.getSequence());
                cmd.write(this);
                flush();
            } catch (java.lang.InterruptedException ie) {
                break;
            } catch (Throwable s) {
                if (!exiting) {
                    log.trace("encode err:", s);
                }
                break;
            }
        }
        BufferPool.freeByteBuffer(curBuffer);
    }

    /**
     *
     * @param i int
     * @throws IOException
     */
    public void writeInt(int i) throws IOException {
        getNextBuffer();
        if (curBuffer.remaining() >= 4) {
            curBuffer.putInt(i);
        } else {
            flush();
            getNextBuffer();
            curBuffer.putInt(i);
        }
    }

    /**
     *
     * @param l long
     * @throws IOException
     */
    public void writeLong(long l) throws IOException {
        getNextBuffer();
        if (curBuffer.remaining() >= 8) {
            curBuffer.putLong(l);
        } else {
            flush();
            getNextBuffer();
            curBuffer.putLong(l);
        }
    }

    /**
     *
     * @param b boolean
     * @throws IOException
     */
    public void writeBoolean(boolean b) throws IOException {
        writeByte(b ? (byte) 0x01 : (byte) 0x00);
    }

    /**
     *
     * @param b byte
     * @throws IOException
     */
    public void writeByte(byte b) throws IOException {
        getNextBuffer();
        curBuffer.put(b);
    }

    /**
     *
     * @param s String
     * @throws IOException
     */
    public void writeString(String s) throws IOException {
        writeBytes(s == null || s.isEmpty() ? null : s.getBytes(charset));
    }

    /**
     *
     * @param b byte[]
     * @throws IOException
     */
    public void writeBytes(byte[] b) throws IOException {
        writeBytes(b, 0, b == null ? 0 : b.length);
    }

    /**
     *
     * @param b byte[]
     * @param off
     * @param len
     * @throws IOException
     */
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        if (b == null || len <= 0) {
            writeInt(0);
            return;
        }
        writeInt(len);
        int count = 0;
        while (count < len) {
            getNextBuffer();
            int remaining = curBuffer.remaining();
            int needing = len - count;
            if (remaining >= needing) {
                curBuffer.put(b, off + count, needing);
                count = count + needing;
            } else {
                curBuffer.put(b, off + count, remaining);
                count = count + remaining;
            }
        }
    }
}
