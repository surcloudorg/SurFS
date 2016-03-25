package com.autumn.util.zlib;

import com.autumn.core.security.Adler32;

/**
 *
 * Title: deflate压缩-数据流
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 */
public class DefStream {

    public byte[] next_in;     // next input byte
    public int next_in_index;
    public int avail_in;       // number of bytes available at next_in
    public long total_in;      // total nb of input bytes read so far

    public byte[] next_out;    // next output byte should be put there
    public int next_out_index;
    public int avail_out;      // remaining free space at next_out
    public long total_out;     // total nb of bytes output so far

    public String msg;
    Deflate dstate;
    Adler32 adler;

    DefStream() {
        this(new Adler32());
    }

    DefStream(Adler32 adler) {
        this.adler = adler;
    }

    int deflateEnd() {
        if (dstate == null) {
            return Constant.Z_STREAM_ERROR;
        }
        int ret;
        if (dstate.status != Constant.INIT_STATE && dstate.status != Constant.BUSY_STATE && dstate.status != Constant.FINISH_STATE) {
            return Constant.Z_STREAM_ERROR;
        }
        dstate.pending_buf = null;
        dstate.l_buf = null;
        dstate.head = null;
        dstate.prev = null;
        dstate.window = null;
        ret = dstate.status == Constant.BUSY_STATE ? Constant.Z_DATA_ERROR : Constant.Z_OK;
        dstate = null;
        return ret;
    }

    void flush_pending() {
        int len = dstate.pending;
        if (len > avail_out) {
            len = avail_out;
        }
        if (len == 0) {
            return;
        }
        if (dstate.pending_buf.length <= dstate.pending_out
                || next_out.length <= next_out_index
                || dstate.pending_buf.length < (dstate.pending_out + len)
                || next_out.length < (next_out_index + len)) {
        }
        System.arraycopy(dstate.pending_buf, dstate.pending_out, next_out, next_out_index, len);
        next_out_index += len;
        dstate.pending_out += len;
        total_out += len;
        avail_out -= len;
        dstate.pending -= len;
        if (dstate.pending == 0) {
            dstate.pending_out = 0;
        }
    }

    int read_buf(byte[] buf, int start, int size) {
        int len = avail_in;
        if (len > size) {
            len = size;
        }
        if (len == 0) {
            return 0;
        }
        avail_in -= len;
        if (dstate.wrap != 0) {
            adler.update(next_in, next_in_index, len);
        }
        System.arraycopy(next_in, next_in_index, buf, start, len);
        next_in_index += len;
        total_in += len;
        return len;
    }

    public long getAdler() {
        return adler.getValue();
    }

    public void free() {
        next_in = null;
        next_out = null;
        msg = null;
    }

    public void setOutput(byte[] buf) {
        setOutput(buf, 0, buf.length);
    }

    public void setOutput(byte[] buf, int off, int len) {
        next_out = buf;
        next_out_index = off;
        avail_out = len;
    }

    public void setInput(byte[] buf) {
        setInput(buf, 0, buf.length, false);
    }

    public void setInput(byte[] buf, boolean append) {
        setInput(buf, 0, buf.length, append);
    }

    public void setInput(byte[] buf, int off, int len, boolean append) {
        if (len <= 0 && append && next_in != null) {
            return;
        }
        if (avail_in > 0 && append) {
            byte[] tmp = new byte[avail_in + len];
            System.arraycopy(next_in, next_in_index, tmp, 0, avail_in);
            System.arraycopy(buf, off, tmp, avail_in, len);
            next_in = tmp;
            next_in_index = 0;
            avail_in += len;
        } else {
            next_in = buf;
            next_in_index = off;
            avail_in = len;
        }
    }

    public byte[] getNextIn() {
        return next_in;
    }

    public void setNextIn(byte[] next_in) {
        this.next_in = next_in;
    }

    public int getNextInIndex() {
        return next_in_index;
    }

    public void setNextInIndex(int next_in_index) {
        this.next_in_index = next_in_index;
    }

    public int getAvailIn() {
        return avail_in;
    }

    public void setAvailIn(int avail_in) {
        this.avail_in = avail_in;
    }

    public byte[] getNextOut() {
        return next_out;
    }

    public void setNextOut(byte[] next_out) {
        this.next_out = next_out;
    }

    public int getNextOutIndex() {
        return next_out_index;
    }

    public void setNextOutIndex(int next_out_index) {
        this.next_out_index = next_out_index;
    }

    public int getAvailOut() {
        return avail_out;

    }

    public void setAvailOut(int avail_out) {
        this.avail_out = avail_out;
    }

    public long getTotalOut() {
        return total_out;
    }

    public long getTotalIn() {
        return total_in;
    }

    public String getMessage() {
        return msg;
    }

    public int end() {
        return Constant.Z_OK;
    }

    public boolean finished() {
        return false;
    }
}
