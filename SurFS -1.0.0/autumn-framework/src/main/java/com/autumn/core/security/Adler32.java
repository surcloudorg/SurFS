/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.security;

import com.autumn.util.Function;
import java.io.Serializable;

/**
 * Title: Adler32
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
final public class Adler32 implements Checksum, Serializable {

    private static final long serialVersionUID = 201407111123456L;
    static final private int BASE = 65521;// largest prime smaller than 65536
    static final private int NMAX = 5552; // NMAX is the largest n such that 255n(n+1)/2 + (n+1)(BASE-1) <= 2^32-1

    private long s1 = 1L;
    private long s2 = 0L;

    /**
     * 序列化
     *
     * @return byte[]
     */
    public byte[] toBytes() {
        byte[] bs = new byte[16];
        Function.long2byte(s1, bs, 0);
        Function.long2byte(s2, bs, 8);
        return bs;
    }

    /**
     * 反序列化
     *
     * @param bs
     */
    private void fill(byte[] bs) {
        s1 = Function.byte2Integer(bs, 0, 8);
        s2 = Function.byte2Integer(bs, 8, 8);
    }

    /**
     * 创建
     *
     * @param s1
     * @param s2
     */
    public Adler32(long s1, long s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    /**
     * 创建
     *
     * @param bs
     */
    public Adler32(byte[] bs) {
        fill(bs);
    }

    /**
     * 创建
     */
    public Adler32() {
    }

    /**
     * 复位
     *
     * @param init
     */
    public void reset(long init) {
        s1 = init & 0xffff;
        s2 = (init >> 16) & 0xffff;
    }

    /**
     * 复位
     */
    public void reset() {
        s1 = 1L;
        s2 = 0L;
    }

    /**
     * Adler32值
     *
     * @return
     */
    public long getValue() {
        return ((s2 << 16) | s1);
    }

    /**
     * @return 16进制Adler32值
     */
    public String toHexString() {
        return Long.toHexString(getValue());
    }

    /**
     * @return 10进制Adler32值
     */
    public String toDecString() {
        return String.valueOf(getValue());
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
        if (len == 1) {
            s1 += buf[index++] & 0xff;
            s2 += s1;
            s1 %= BASE;
            s2 %= BASE;
            return;
        }
        int len1 = len / NMAX;
        int len2 = len % NMAX;
        while (len1-- > 0) {
            int k = NMAX;
            len -= k;
            while (k-- > 0) {
                s1 += buf[index++] & 0xff;
                s2 += s1;
            }
            s1 %= BASE;
            s2 %= BASE;
        }
        int k = len2;
        len -= k;
        while (k-- > 0) {
            s1 += buf[index++] & 0xff;
            s2 += s1;
        }
        s1 %= BASE;
        s2 %= BASE;
    }
}
