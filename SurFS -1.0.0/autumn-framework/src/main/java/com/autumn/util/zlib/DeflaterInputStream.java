package com.autumn.util.zlib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * Title: deflate压缩-InputStream
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 */
public class DeflaterInputStream extends InputStream {

    protected final Deflater deflater;
    private byte[] readBuffer = null;
    private byte[] writeBuffer = null;
    private byte[] buffer = null;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private boolean syncFlush = false;
    private int count;
    private int pos;
    private InputStream input = null;
    protected static final int DEFAULT_BUFSIZE = 1024;

    public DeflaterInputStream(InputStream in) throws IOException {
        this(in, new Deflater(), DEFAULT_BUFSIZE);
    }

    public DeflaterInputStream(InputStream in, Deflater def) throws IOException {
        this(in, def, DEFAULT_BUFSIZE);
    }

    public DeflaterInputStream(InputStream in, Deflater deflater, int size) throws IOException {
        this.input = in;
        if (in == null || deflater == null) {
            throw new NullPointerException();
        } else if (size <= 0) {
            throw new IllegalArgumentException("buffer size must be greater than 0");
        }
        this.deflater = deflater;
        readBuffer = new byte[size];
        buffer = new byte[size];
    }

    @Override
    public int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count) {
                return -1;
            }
        }
        return writeBuffer[pos++] & 0xff;
    }

    /**
     * 转换(压缩)数据
     *
     * @throws IOException
     */
    private void fill() throws IOException {
        if (deflater.finished()) {
            return;
        }
        while (true) {
            int n = getInIfOpen().read(readBuffer);
            if (n > 0) {
                int flush = syncFlush ? Constant.Z_SYNC_FLUSH : Constant.Z_NO_FLUSH;
                deflater.setInput(readBuffer, 0, n, true);
                while (deflater.avail_in > 0) {
                    int err = deflate(flush);
                    if (err == Constant.Z_STREAM_END) {
                        break;
                    }
                }
            } else {
                while (!deflater.finished()) {
                    deflate(Constant.Z_FINISH);
                }
                deflater.end();
            }
            writeBuffer = out.toByteArray();
            if (writeBuffer.length > 0) {
                pos = 0;
                count = writeBuffer.length;
                out.reset();
                break;
            }
            if (n == 0) {
                break;
            }
        }
    }

    protected int deflate(int flush) throws IOException {
        deflater.setOutput(buffer, 0, buffer.length);
        int err = deflater.deflate(flush);
        switch (err) {
            case Constant.Z_OK:
            case Constant.Z_STREAM_END:
                break;
            case Constant.Z_BUF_ERROR:
                if (deflater.avail_in <= 0 && flush != Constant.Z_FINISH) {
                    break;
                }
            default:
                throw new IOException("failed to deflate");
        }
        int len = deflater.next_out_index;
        if (len > 0) {
            out.write(buffer, 0, len);
        }
        return err;
    }

    public long getTotalIn() {
        return deflater.getTotalIn();
    }

    public long getTotalOut() {
        return deflater.getTotalOut();
    }

    public Deflater getDeflater() {
        return deflater;
    }

    private InputStream getInIfOpen() throws IOException {
        if (input == null) {
            throw new IOException("Stream closed");
        }
        return input;
    }

    @Override
    public void close() throws IOException {
        if (input != null) {
            input.close();
            input = null;
        }
    }

    /**
     * @return the syncFlush
     */
    public boolean isSyncFlush() {
        return syncFlush;
    }

    /**
     * @param syncFlush the syncFlush to set
     */
    public void setSyncFlush(boolean syncFlush) {
        this.syncFlush = syncFlush;
    }
}
