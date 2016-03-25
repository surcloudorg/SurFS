package com.autumn.util.zlib;

import com.autumn.core.security.Adler32;
import com.autumn.core.security.Crc32;
import com.autumn.util.Function;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * Title: deflate压缩-序列化
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 */
public class DeflateSerialize {

    /**
     * 反序列化
     *
     * @param bs
     * @return Deflate
     * @throws IOException
     */
    public static Deflater newInstance(byte[] bs) throws IOException {
        if (bs == null) {
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bs);
        DataInputStream dis = new DataInputStream(bis);
        byte[] adlerbs = new byte[16];
        dis.read(adlerbs);
        Adler32 adler = new Adler32(adlerbs);
        boolean finished = dis.readBoolean();
        Deflate df = null;
        if (!finished) {
            df = new Deflate();
            df.status = dis.readInt();
            df.pending_buf = new byte[Constant.lit_bufsize * 3];
            dis.read(df.pending_buf);
            df.pending_out = dis.readInt();
            df.pending = dis.readInt();
            df.wrap = dis.readInt();
            df.data_type = dis.readByte();
            df.last_flush = dis.readInt();
            df.window = new byte[Constant.w_size * 2];
            dis.read(df.window);
            df.prev = new short[Constant.w_size];
            for (int ii = 0; ii < Constant.w_size; ii++) {
                df.prev[ii] = dis.readShort();
            }
            df.head = new short[Constant.hash_size];
            for (int ii = 0; ii < Constant.hash_size; ii++) {
                df.head[ii] = dis.readShort();
            }
            df.ins_h = dis.readInt();
            df.block_start = dis.readInt();
            df.match_length = dis.readInt();
            df.prev_match = dis.readInt();
            df.match_available = dis.readInt();
            df.strstart = dis.readInt();
            df.match_start = dis.readInt();
            df.lookahead = dis.readInt();
            df.prev_length = dis.readInt();
            df.dyn_ltree = new short[Constant.HEAP_SIZE * 2];
            for (int ii = 0; ii < df.dyn_ltree.length; ii++) {
                df.dyn_ltree[ii] = dis.readShort();
            }
            df.dyn_dtree = new short[(2 * Constant.D_CODES + 1) * 2]; // distance tree
            for (int ii = 0; ii < df.dyn_dtree.length; ii++) {
                df.dyn_dtree[ii] = dis.readShort();
            }
            df.bl_tree = new short[(2 * Constant.BL_CODES + 1) * 2];  // Huffman tree for bit lengths
            for (int ii = 0; ii < df.bl_tree.length; ii++) {
                df.bl_tree[ii] = dis.readShort();
            }
            df.l_desc.dyn_tree = df.dyn_ltree;
            df.l_desc.stat_desc = StaticTree.static_l_desc;
            df.d_desc.dyn_tree = df.dyn_dtree;
            df.d_desc.stat_desc = StaticTree.static_d_desc;
            df.bl_desc.dyn_tree = df.bl_tree;
            df.bl_desc.stat_desc = StaticTree.static_bl_desc;
            df.bl_count = new short[Constant.MAX_WBITS + 1];
            for (int ii = 0; ii < df.bl_count.length; ii++) {
                df.bl_count[ii] = dis.readShort();
            }
            df.next_code = new short[Constant.MAX_WBITS + 1];
            for (int ii = 0; ii < df.next_code.length; ii++) {
                df.next_code[ii] = dis.readShort();
            }
            df.heap = new int[2 * Constant.L_CODES + 1];
            for (int ii = 0; ii < df.heap.length; ii++) {
                df.heap[ii] = dis.readInt();
            }
            df.heap_len = dis.readInt();
            df.heap_max = dis.readInt();
            df.depth = new byte[2 * Constant.L_CODES + 1];
            dis.read(df.depth);
            df.l_buf = new byte[Constant.lit_bufsize];
            dis.read(df.l_buf);
            df.last_lit = dis.readInt();
            df.d_buf = dis.readInt();
            df.opt_len = dis.readInt();
            df.static_len = dis.readInt();
            df.matches = dis.readInt();
            df.last_eob_len = dis.readInt();
            df.bi_buf = dis.readShort();
            df.bi_valid = dis.readInt();
        }
        long total_in = dis.readLong();
        long total_out = dis.readLong();
        byte[] crcbs = new byte[4];
        int rc = dis.read(crcbs);
        if (rc != 4) {
            Deflater def = new Deflater(adler, df);
            def.total_in = total_in;
            def.total_out = total_out;
            def.finished = finished;
            return def;
        } else {
            ZipDeflater def = new ZipDeflater(adler, df);
            def.total_in = total_in;
            def.total_out = total_out;
            def.finished = finished;
            int v = (int) Function.byte2Integer(crcbs, 0, 4);
            if (v != 0) {
                def.crc = new Crc32(v);
            }
            def.size = dis.readLong();
            def.csize = dis.readLong();
            def.written = dis.readLong();
            byte[] namebs = new byte[2048];
            rc = dis.read(namebs);
            if (rc > 0) {
                def.name = new String(namebs, 0, rc);
            }
            return def;
        }
    }

    /**
     * 序列化
     *
     * @param df
     * @return byte[] len=267874
     */
    public static byte[] toBytes(Deflater df) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.write(df.adler.toBytes());
            dos.writeBoolean(df.finished);
            if (!df.finished()) {
                dos.writeInt(df.dstate.status);
                dos.write(df.dstate.pending_buf);
                dos.writeInt(df.dstate.pending_out);
                dos.writeInt(df.dstate.pending);
                dos.writeInt(df.dstate.wrap);
                dos.writeByte(df.dstate.data_type);
                dos.writeInt(df.dstate.last_flush);
                dos.write(df.dstate.window);
                for (short s : df.dstate.prev) {
                    dos.writeShort(s);
                }
                for (short s : df.dstate.head) {
                    dos.writeShort(s);
                }
                dos.writeInt(df.dstate.ins_h);
                dos.writeInt(df.dstate.block_start);
                dos.writeInt(df.dstate.match_length);
                dos.writeInt(df.dstate.prev_match);
                dos.writeInt(df.dstate.match_available);
                dos.writeInt(df.dstate.strstart);
                dos.writeInt(df.dstate.match_start);
                dos.writeInt(df.dstate.lookahead);
                dos.writeInt(df.dstate.prev_length);
                for (short s : df.dstate.dyn_ltree) {
                    dos.writeShort(s);
                }
                for (short s : df.dstate.dyn_dtree) {
                    dos.writeShort(s);
                }
                for (short s : df.dstate.bl_tree) {
                    dos.writeShort(s);
                }
                for (short s : df.dstate.bl_count) {
                    dos.writeShort(s);
                }
                for (short s : df.dstate.next_code) {
                    dos.writeShort(s);
                }
                for (int s : df.dstate.heap) {
                    dos.writeInt(s);
                }
                dos.writeInt(df.dstate.heap_len);
                dos.writeInt(df.dstate.heap_max);
                dos.write(df.dstate.depth);
                dos.write(df.dstate.l_buf);
                dos.writeInt(df.dstate.last_lit);
                dos.writeInt(df.dstate.d_buf);
                dos.writeInt(df.dstate.opt_len);
                dos.writeInt(df.dstate.static_len);
                dos.writeInt(df.dstate.matches);
                dos.writeInt(df.dstate.last_eob_len);
                dos.writeShort(df.dstate.bi_buf);
                dos.writeInt(df.dstate.bi_valid);
            }
            dos.writeLong(df.total_in);
            dos.writeLong(df.total_out);
            if (df instanceof ZipDeflater) {
                if (((ZipDeflater) df).crc == null) {
                    dos.writeInt(0);
                } else {
                    dos.write(((ZipDeflater) df).crc.toBytes());
                }
                dos.writeLong(((ZipDeflater) df).size);
                dos.writeLong(((ZipDeflater) df).csize);
                dos.writeLong(((ZipDeflater) df).written);
                dos.writeBytes(((ZipDeflater) df).name);
            }
            dos.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            return null;
        }
    }
}
