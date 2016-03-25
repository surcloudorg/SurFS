package com.autumn.util.zlib;

import com.autumn.core.security.Adler32;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * Title: deflate压缩
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Deflater extends DefStream {

    protected boolean finished = false;
    protected ByteArrayOutputStream out = null;
    protected DeflaterOutputStream dos = null;

    /**
     * 序列化
     *
     * @return
     */
    public byte[] toBytes() {
        return DeflateSerialize.toBytes(this);
    }

    public Deflater() {
        super();
        dstate = new Deflate();
        init();
    }

    Deflater(Adler32 adler, Deflate def) {
        super(adler);
        dstate = def;
        if (dstate != null) {
            dstate.strm = this;
        }
    }

    /**
     * 初始化
     */
    private void init() {
        dstate.strm = this;
        dstate.dyn_ltree = new short[Constant.HEAP_SIZE * 2];
        dstate.dyn_dtree = new short[(2 * Constant.D_CODES + 1) * 2]; // distance tree
        dstate.bl_tree = new short[(2 * Constant.BL_CODES + 1) * 2];  // Huffman tree for bit lengths
        deflateInit();
    }

    private void deflateInit() {
        dstate.strm.msg = null;
        dstate.strm.dstate = dstate;
        dstate.wrap = 1;
        dstate.window = new byte[Constant.w_size * 2];
        dstate.prev = new short[Constant.w_size];
        dstate.head = new short[Constant.hash_size];
        dstate.pending_buf = new byte[Constant.lit_bufsize * 3];
        dstate.d_buf = Constant.lit_bufsize;
        dstate.l_buf = new byte[Constant.lit_bufsize];
        deflateReset();
    }

    private void deflateReset() {
        dstate.strm.total_in = dstate.strm.total_out = 0;
        dstate.strm.msg = null;
        dstate.pending = 0;
        dstate.pending_out = 0;
        if (dstate.wrap < 0) {
            dstate.wrap = -dstate.wrap;
        }
        dstate.status = (dstate.wrap == 0) ? Constant.BUSY_STATE : Constant.INIT_STATE;
        dstate.strm.adler.reset();
        dstate.last_flush = Constant.Z_NO_FLUSH;
        tr_init();
        lm_init();
    }

    private void lm_init() {
        dstate.head[Constant.hash_size - 1] = 0;
        for (int i = 0; i < Constant.hash_size - 1; i++) {
            dstate.head[i] = 0;
        }
        dstate.strstart = 0;
        dstate.block_start = 0;
        dstate.lookahead = 0;
        dstate.match_length = dstate.prev_length = Constant.MIN_MATCH - 1;
        dstate.match_available = 0;
        dstate.ins_h = 0;
    }

    /**
     * Initialize the tree data structures for a new zlib stream.
     */
    private void tr_init() {
        dstate.l_desc.dyn_tree = dstate.dyn_ltree;
        dstate.l_desc.stat_desc = StaticTree.static_l_desc;
        dstate.d_desc.dyn_tree = dstate.dyn_dtree;
        dstate.d_desc.stat_desc = StaticTree.static_d_desc;
        dstate.bl_desc.dyn_tree = dstate.bl_tree;
        dstate.bl_desc.stat_desc = StaticTree.static_bl_desc;
        dstate.bi_buf = 0;
        dstate.bi_valid = 0;
        dstate.last_eob_len = 8; // enough lookahead for inflate
        init_block();
    }

    void init_block() {
        for (int i = 0; i < Constant.L_CODES; i++) {
            dstate.dyn_ltree[i * 2] = 0;
        }
        for (int i = 0; i < Constant.D_CODES; i++) {
            dstate.dyn_dtree[i * 2] = 0;
        }
        for (int i = 0; i < Constant.BL_CODES; i++) {
            dstate.bl_tree[i * 2] = 0;
        }
        dstate.dyn_ltree[Constant.END_BLOCK * 2] = 1;
        dstate.opt_len = dstate.static_len = 0;
        dstate.last_lit = dstate.matches = 0;
    }
    final byte[] buf1 = new byte[1];

    /**
     * 压缩
     *
     * @param b
     * @return byte[]
     * @throws IOException
     */
    public byte[] doDeflate(byte b) throws IOException {
        buf1[0] = b;
        return doDeflate(buf1, 0, 1);
    }

    /**
     * 压缩
     *
     * @param b
     * @return byte[]
     * @throws IOException
     */
    public byte[] doDeflate(byte[] b) throws IOException {
        return doDeflate(b, 0, b.length);
    }

    /**
     * 压缩
     *
     * @param b
     * @param off
     * @param len
     * @return　byte[]
     * @throws java.io.IOException
     */
    public synchronized byte[] doDeflate(byte[] b, int off, int len) throws IOException {
        check();
        dos.write(b, off, len);
        byte[] bs = out.toByteArray();
        out.reset();
        return bs;
    }

    /**
     * 检查
     *
     * @throws IOException
     */
    private void check() throws IOException {
        if (finished) {
            throw new IOException("finished");
        }
        if (out == null) {
            out = new ByteArrayOutputStream();
        }
        if (dos == null) {
            dos = new DeflaterOutputStream(out, this);
        }
    }

    /**
     * 压缩完毕！
     *
     * @return byte[]
     * @throws IOException
     */
    public byte[] doFinal() throws IOException {
        check();
        dos.close();
        byte[] bs = out.toByteArray();
        out.reset();
        return bs;
    }

    /**
     * 压缩完毕！
     *
     * @param b
     * @return byte[]
     * @throws IOException
     */
    public byte[] doFinal(byte b) throws IOException {
        buf1[0] = b;
        return doFinal(buf1);
    }

    /**
     * 压缩完毕！
     *
     * @param b
     * @return byte[]
     * @throws IOException
     */
    public byte[] doFinal(byte[] b) throws IOException {
        return doFinal(b, 0, b.length);
    }

    /**
     * 压缩完毕！
     *
     * @param b
     * @param off
     * @param len
     * @return byte[]
     * @throws java.io.IOException
     */
    public synchronized byte[] doFinal(byte[] b, int off, int len) throws IOException {
        check();
        dos.write(b, off, len);
        dos.close();
        byte[] bs = out.toByteArray();
        out.reset();
        return bs;
    }

    public int deflate(int flush) {
        if (dstate == null) {
            return Constant.Z_STREAM_ERROR;
        }
        int ret = dstate.deflate(flush);
        if (ret == Constant.Z_STREAM_END) {
            finished = true;
        }
        return ret;
    }

    @Override
    public int end() {
        finished = true;
        if (dstate == null) {
            return Constant.Z_STREAM_ERROR;
        }
        int ret = deflateEnd();
        dstate = null;
        free();
        return ret;
    }

    @Override
    public boolean finished() {
        return finished;
    }
}
