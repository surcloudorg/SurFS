/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.security;

import com.autumn.util.Function;
import java.io.Serializable;

/**
 * Title: crc32
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Crc32 implements Checksum, Serializable {

    private static final long serialVersionUID = 201427111123456L;
    public static final int[] crc_table = new int[256];

    static {
        for (int n = 0; n < 256; n++) {
            int c = n;
            for (int k = 8; --k >= 0;) {
                if ((c & 1) != 0) {
                    c = 0xedb88320 ^ (c >>> 1);
                } else {
                    c = c >>> 1;
                }
            }
            crc_table[n] = c;
        }
    }

    private int v = 0;

    /**
     * 序列化
     *
     * @return byte[]
     */
    public byte[] toBytes() {
        byte[] bs = new byte[4];
        Function.int2byte(v, bs, 0);
        return bs;
    }

    /**
     * 反序列化
     *
     * @param bs
     */
    private void fill(byte[] bs) {
        v =  (int)Function.byte2Integer(bs, 0, 4);
    }

    /**
     * 创建
     *
     * @param s
     */
    public Crc32(int s) {
        this.v = s;
    }

    /**
     * 创建
     *
     * @param bs
     */
    public Crc32(byte[] bs) {
        fill(bs);
    }

    /**
     * 创建
     */
    public Crc32() {
    }

    /**
     * 更新
     *
     * @param b
     */
    public void update(byte b) {
        byte[] bs = {b};
        update(bs);
    }

    /**
     * 更新
     *
     * @param buf
     */
    public void update(byte[] buf) {
        update(buf, 0, buf.length);
    }

    /**
     * 更新
     *
     * @param buf
     * @param index
     * @param len
     */
    public void update(byte[] buf, int index, int len) {
        int c = ~v;
        while (--len >= 0) {
            c = crc_table[(c ^ buf[index++]) & 0xff] ^ (c >>> 8);
        }
        v = ~c;
    }

    /**
     * 复位
     */
    public void reset() {
        v = 0;
    }

    /**
     * 复位
     *
     * @param vv
     */
    public void reset(long vv) {
        v = (int) (vv & 0xffffffffL);
    }

    /**
     * crc32值
     *
     * @return
     */
    public long getValue() {
        return (long) (v & 0xffffffffL);
    }

    
    /**
     * @return 16进制crc32值
     */
    public String toHexString() {
        return Long.toHexString(getValue());
    }

    /**
     * @return 10进制crc32值
     */
    public String toDecString() {
        return String.valueOf(getValue());
    }
}
