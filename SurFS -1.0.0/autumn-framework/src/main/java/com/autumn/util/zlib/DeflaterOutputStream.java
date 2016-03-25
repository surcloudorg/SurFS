package com.autumn.util.zlib;

import java.io.*;

/**
 *
 * Title: deflate压缩-OutputStream
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 */
public class DeflaterOutputStream extends FilterOutputStream {

    protected final Deflater deflater;

    protected byte[] buffer;

    private boolean closed = false;

    private boolean syncFlush = false;

    protected static final int DEFAULT_BUFSIZE = 1024;

    public DeflaterOutputStream(OutputStream out) throws IOException {
        this(out, new Deflater(), DEFAULT_BUFSIZE);
    }

    public DeflaterOutputStream(OutputStream out, Deflater def) throws IOException {
        this(out, def, DEFAULT_BUFSIZE);
    }

    public DeflaterOutputStream(OutputStream out, Deflater deflater, int size) throws IOException {
        super(out);
        if (out == null || deflater == null) {
            throw new NullPointerException();
        } else if (size <= 0) {
            throw new IllegalArgumentException("buffer size must be greater than 0");
        }
        this.deflater = deflater;
        buffer = new byte[size];
    }
    private final byte[] buf1 = new byte[1];

    @Override
    public void write(int b) throws IOException {
        buf1[0] = (byte) (b & 0xff);
        write(buf1, 0, 1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (deflater.finished()) {
            throw new IOException("finished");
        } else if (off < 0 | len < 0 | off + len > b.length) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
        } else {
            int flush = syncFlush ? Constant.Z_SYNC_FLUSH : Constant.Z_NO_FLUSH;
            deflater.setInput(b, off, len, true);
            while (deflater.avail_in > 0) {
                int err = deflate(flush);
                if (err == Constant.Z_STREAM_END) {
                    break;
                }
            }
        }
    }

    public void finish() throws IOException {
        while (!deflater.finished()) {
            deflate(Constant.Z_FINISH);
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            finish();
            deflater.end();
            out.close();
            closed = true;
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

    @Override
    public void flush() throws IOException {
        if (syncFlush && !deflater.finished()) {
            while (true) {
                int err = deflate(Constant.Z_SYNC_FLUSH);
                if (deflater.next_out_index < buffer.length) {
                    break;
                }
                if (err == Constant.Z_STREAM_END) {
                    break;
                }
            }
        }
        out.flush();
    }

    public long getTotalIn() {
        return deflater.getTotalIn();
    }

    public long getTotalOut() {
        return deflater.getTotalOut();
    }

    public void setSyncFlush(boolean syncFlush) {
        this.syncFlush = syncFlush;
    }

    public boolean getSyncFlush() {
        return this.syncFlush;
    }

    public Deflater getDeflater() {
        return deflater;
    }
}
