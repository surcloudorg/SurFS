/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.security;

import com.autumn.util.Function;
import java.io.IOException;
import java.io.ObjectInput;
import java.util.Arrays;

/**
 * Title: sha1摘要算法
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public final class SHA1Digest extends GeneralDigest {

    private static final long serialVersionUID = 20140701123456L;
    static final int DIGEST_LENGTH = 20;

    private int H1, H2, H3, H4, H5;// IV's
    private final int[] X = new int[80];
    private byte xOff;

    @Override
    public String toString() {
        return Hex.encode(toBytes());
    }

    /**
     * 序列化
     *
     * @return byte[] len=354
     */
    @Override
    public byte[] toBytes() {
        byte[] sbs = super.toBytes();
        int len = byte_length + 10;
        byte[] bs = new byte[len + 4 * 5 + 4 * 80 + 1];
        System.arraycopy(sbs, 0, bs, 0, len);
        Function.int2byte(H1, bs, len);
        Function.int2byte(H2, bs, len + 4);
        Function.int2byte(H3, bs, len + 4 * 2);
        Function.int2byte(H4, bs, len + 4 * 3);
        Function.int2byte(H5, bs, len + 4 * 4);
        int2byte(X, 0, bs, len + 4 * 5, 80);
        bs[len + 4 * 5 + 4 * 80] = xOff;
        return bs;
    }

    /**
     * 反序列化
     *
     * @param bs
     */
    @Override
    protected void fill(byte[] bs) {
        super.fill(bs);
        int len = byte_length + 10;
        H1 = (int) Function.byte2Integer(bs, len, 4);
        H2 = (int) Function.byte2Integer(bs, len + 4, 4);
        H3 = (int) Function.byte2Integer(bs, len + 4 * 2, 4);
        H4 = (int) Function.byte2Integer(bs, len + 4 * 3, 4);
        H5 = (int) Function.byte2Integer(bs, len + 4 * 4, 4);
        byte2int(bs, len + 4 * 5, X, 0, 80);
        xOff = bs[len + 4 * 5 + 4 * 80];
    }

    /**
     * 反序列化
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte[] bs = new byte[byte_length + 10 + 4 * 5 + 4 * 80 + 1];
        in.read(bs);
        fill(bs);
    }

    /**
     * 构造
     *
     * @param algorithm
     */
    protected SHA1Digest(String algorithm) {
        super(algorithm);
        reset();
    }

    /**
     * 构造
     */
    public SHA1Digest() {
        this("SHA1");
    }

    /**
     * 创建
     *
     * @param bs
     */
    public SHA1Digest(byte[] bs) {
        this();
        fill(bs);
    }

    /**
     * 初始化 (清零)
     */
    @Override
    public void reset() {
        super.reset();
        H1 = 0x67452301;
        H2 = 0xefcdab89;
        H3 = 0x98badcfe;
        H4 = 0x10325476;
        H5 = 0xc3d2e1f0;
        xOff = 0;
        Arrays.fill(X, 0);
    }

    /**
     * 输出摘要值
     *
     * @param out
     * @param outOff
     * @return int
     */
    @Override
    protected int doFinal(byte[] out, int outOff) {
        super.finish();
        Function.int2byte(H1, out, outOff);
        Function.int2byte(H2, out, outOff + 4);
        Function.int2byte(H3, out, outOff + 8);
        Function.int2byte(H4, out, outOff + 12);
        Function.int2byte(H5, out, outOff + 16);
        return this.getDigestLength();
    }

    /**
     * SHA-1 转换算法
     */
    private int f(int u, int v, int w) {
        return ((u & v) | ((~u) & w));
    }

    /**
     * SHA-1 转换算法
     */
    private int h(int u, int v, int w) {
        return (u ^ v ^ w);
    }

    /**
     * SHA-1 转换算法
     */
    private int g(int u, int v, int w) {
        return ((u & v) | (u & w) | (v & w));
    }

    /**
     * 加入转换
     *
     * @param in
     * @param inOff
     */
    protected void processWord(byte[] in, int inOff) {
        int n = (int) Function.byte2Integer(in, inOff, 4);
        X[xOff] = n;
        if (++xOff == 16) {
            processBlock();
        }
    }

    @Override
    protected void processLength(long lowW, long hiW) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 加入结束标志
     *
     * @param bitLength
     */
    protected void processLength(long bitLength) {
        if (xOff > 14) {
            processBlock();
        }
        X[14] = (int) (bitLength >>> 32);
        X[15] = (int) (bitLength & 0xffffffff);
    }

    private static final int Y1 = 0x5a827999;
    private static final int Y2 = 0x6ed9eba1;
    private static final int Y3 = 0x8f1bbcdc;
    private static final int Y4 = 0xca62c1d6;

    /**
     * 转换
     */
    @Override
    protected void processBlock() {
        for (int i = 16; i < 80; i++) {
            int t = X[i - 3] ^ X[i - 8] ^ X[i - 14] ^ X[i - 16];
            X[i] = t << 1 | t >>> 31;
        }
        int A = H1;
        int B = H2;
        int C = H3;
        int D = H4;
        int E = H5;
        int idx = 0;
        for (int j = 0; j < 4; j++) {
            E += (A << 5 | A >>> 27) + f(B, C, D) + X[idx++] + Y1;
            B = B << 30 | B >>> 2;
            D += (E << 5 | E >>> 27) + f(A, B, C) + X[idx++] + Y1;
            A = A << 30 | A >>> 2;
            C += (D << 5 | D >>> 27) + f(E, A, B) + X[idx++] + Y1;
            E = E << 30 | E >>> 2;
            B += (C << 5 | C >>> 27) + f(D, E, A) + X[idx++] + Y1;
            D = D << 30 | D >>> 2;
            A += (B << 5 | B >>> 27) + f(C, D, E) + X[idx++] + Y1;
            C = C << 30 | C >>> 2;
        }
        for (int j = 0; j < 4; j++) {
            E += (A << 5 | A >>> 27) + h(B, C, D) + X[idx++] + Y2;
            B = B << 30 | B >>> 2;
            D += (E << 5 | E >>> 27) + h(A, B, C) + X[idx++] + Y2;
            A = A << 30 | A >>> 2;
            C += (D << 5 | D >>> 27) + h(E, A, B) + X[idx++] + Y2;
            E = E << 30 | E >>> 2;
            B += (C << 5 | C >>> 27) + h(D, E, A) + X[idx++] + Y2;
            D = D << 30 | D >>> 2;
            A += (B << 5 | B >>> 27) + h(C, D, E) + X[idx++] + Y2;
            C = C << 30 | C >>> 2;
        }
        for (int j = 0; j < 4; j++) {
            E += (A << 5 | A >>> 27) + g(B, C, D) + X[idx++] + Y3;
            B = B << 30 | B >>> 2;
            D += (E << 5 | E >>> 27) + g(A, B, C) + X[idx++] + Y3;
            A = A << 30 | A >>> 2;
            C += (D << 5 | D >>> 27) + g(E, A, B) + X[idx++] + Y3;
            E = E << 30 | E >>> 2;
            B += (C << 5 | C >>> 27) + g(D, E, A) + X[idx++] + Y3;
            D = D << 30 | D >>> 2;
            A += (B << 5 | B >>> 27) + g(C, D, E) + X[idx++] + Y3;
            C = C << 30 | C >>> 2;
        }
        for (int j = 0; j <= 3; j++) {
            E += (A << 5 | A >>> 27) + h(B, C, D) + X[idx++] + Y4;
            B = B << 30 | B >>> 2;
            D += (E << 5 | E >>> 27) + h(A, B, C) + X[idx++] + Y4;
            A = A << 30 | A >>> 2;
            C += (D << 5 | D >>> 27) + h(E, A, B) + X[idx++] + Y4;
            E = E << 30 | E >>> 2;
            B += (C << 5 | C >>> 27) + h(D, E, A) + X[idx++] + Y4;
            D = D << 30 | D >>> 2;
            A += (B << 5 | B >>> 27) + h(C, D, E) + X[idx++] + Y4;
            C = C << 30 | C >>> 2;
        }
        H1 += A;
        H2 += B;
        H3 += C;
        H4 += D;
        H5 += E;
        xOff = 0;
        for (int i = 0; i < 16; i++) {
            X[i] = 0;
        }
    }

}
