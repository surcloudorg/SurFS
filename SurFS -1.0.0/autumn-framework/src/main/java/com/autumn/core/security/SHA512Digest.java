/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.security;

import com.autumn.util.Function;
import java.io.IOException;
import java.io.ObjectInput;
import java.util.Arrays;

public final class SHA512Digest extends GeneralDigest {

    private static final long serialVersionUID = 21140701123456L;
    static final int DIGEST_LENGTH = 64;

    private long H1, H2, H3, H4, H5, H6, H7, H8;
    private final long[] W = new long[80];
    private byte wOff;

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
        int len = byte_length + 18;
        byte[] bs = new byte[len + 8 * 8 + 8 * 80 + 1];
        System.arraycopy(sbs, 0, bs, 0, len);
        Function.long2byte(H1, bs, len);
        Function.long2byte(H2, bs, len + 8);
        Function.long2byte(H3, bs, len + 8 * 2);
        Function.long2byte(H4, bs, len + 8 * 3);
        Function.long2byte(H5, bs, len + 8 * 4);
        Function.long2byte(H6, bs, len + 8 * 5);
        Function.long2byte(H7, bs, len + 8 * 6);
        Function.long2byte(H8, bs, len + 8 * 7);
        long2byte(W, 0, bs, len + 8 * 8, 80);
        bs[len + 8 * 8 + 8 * 80] = wOff;
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
        int len = byte_length + 18;
        H1 = Function.byte2Integer(bs, len, 8);
        H2 = Function.byte2Integer(bs, len + 8, 8);
        H3 = Function.byte2Integer(bs, len + 8 * 2, 8);
        H4 = Function.byte2Integer(bs, len + 8 * 3, 8);
        H5 = Function.byte2Integer(bs, len + 8 * 4, 8);
        H6 = Function.byte2Integer(bs, len + 8 * 5, 8);
        H7 = Function.byte2Integer(bs, len + 8 * 6, 8);
        H8 = Function.byte2Integer(bs, len + 8 * 7, 8);
        byte2long(bs, len + 8 * 8, W, 0, 80);
        wOff = bs[len + 8 * 8 + 8 * 80];
    }

    /**
     * 反序列化
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte[] bs = new byte[byte_length + 18 + 8 * 8 + 8 * 80 + 1];
        in.read(bs);
        fill(bs);
    }

    /**
     * 构造
     *
     * @param algorithm
     */
    protected SHA512Digest(String algorithm) {
        super(algorithm);
        reset();
    }

    /**
     * 构造
     */
    public SHA512Digest() {
        this("SHA512");
    }

    /**
     * 创建
     *
     * @param bs
     */
    public SHA512Digest(byte[] bs) {
        this();
        fill(bs);
    }

    /**
     * 初始化 (清零)
     */
    @Override
    public void reset() {
        super.reset();
        H1 = 0x6a09e667f3bcc908L;
        H2 = 0xbb67ae8584caa73bL;
        H3 = 0x3c6ef372fe94f82bL;
        H4 = 0xa54ff53a5f1d36f1L;
        H5 = 0x510e527fade682d1L;
        H6 = 0x9b05688c2b3e6c1fL;
        H7 = 0x1f83d9abfb41bd6bL;
        H8 = 0x5be0cd19137e2179L;
        wOff = 0;
        Arrays.fill(W, 0);
    }

    @Override
    protected int doFinal(byte[] out, int outOff) {
        finish();
        Function.long2byte(H1, out, outOff);
        Function.long2byte(H2, out, outOff + 8);
        Function.long2byte(H3, out, outOff + 8 * 2);
        Function.long2byte(H4, out, outOff + 8 * 3);
        Function.long2byte(H5, out, outOff + 8 * 4);
        Function.long2byte(H6, out, outOff + 8 * 5);
        Function.long2byte(H7, out, outOff + 8 * 6);
        Function.long2byte(H8, out, outOff + 8 * 7);
        return this.getDigestLength();
    }

    @Override
    protected void processWord(byte[] in, int inOff) {
        long n = Function.byte2Integer(in, inOff, 8);
        W[wOff] = n;
        if (++wOff == 16) {
            processBlock();
        }
    }

    @Override
    protected void processLength(long lowW, long hiW) {
        if (wOff > 14) {
            processBlock();
        }
        W[14] = hiW;
        W[15] = lowW;
    }

    @Override
    protected void processLength(long bitLength) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void processBlock() {
        adjustByteCounts();
        for (int t = 16; t <= 79; t++) {
            W[t] = Sigma1(W[t - 2]) + W[t - 7] + Sigma0(W[t - 15]) + W[t - 16];
        }
        long a = H1;
        long b = H2;
        long c = H3;
        long d = H4;
        long e = H5;
        long f = H6;
        long g = H7;
        long h = H8;
        int t = 0;
        for (int i = 0; i < 10; i++) {
            // t = 8 * i
            h += Sum1(e) + Ch(e, f, g) + K[t] + W[t++];
            d += h;
            h += Sum0(a) + Maj(a, b, c);
            // t = 8 * i + 1
            g += Sum1(d) + Ch(d, e, f) + K[t] + W[t++];
            c += g;
            g += Sum0(h) + Maj(h, a, b);
            // t = 8 * i + 2
            f += Sum1(c) + Ch(c, d, e) + K[t] + W[t++];
            b += f;
            f += Sum0(g) + Maj(g, h, a);
            // t = 8 * i + 3
            e += Sum1(b) + Ch(b, c, d) + K[t] + W[t++];
            a += e;
            e += Sum0(f) + Maj(f, g, h);
            // t = 8 * i + 4
            d += Sum1(a) + Ch(a, b, c) + K[t] + W[t++];
            h += d;
            d += Sum0(e) + Maj(e, f, g);
            // t = 8 * i + 5
            c += Sum1(h) + Ch(h, a, b) + K[t] + W[t++];
            g += c;
            c += Sum0(d) + Maj(d, e, f);
            // t = 8 * i + 6
            b += Sum1(g) + Ch(g, h, a) + K[t] + W[t++];
            f += b;
            b += Sum0(c) + Maj(c, d, e);
            // t = 8 * i + 7
            a += Sum1(f) + Ch(f, g, h) + K[t] + W[t++];
            e += a;
            a += Sum0(b) + Maj(b, c, d);
        }
        H1 += a;
        H2 += b;
        H3 += c;
        H4 += d;
        H5 += e;
        H6 += f;
        H7 += g;
        H8 += h;
        wOff = 0;
        for (int i = 0; i < 16; i++) {
            W[i] = 0;
        }
    }

    /* SHA-384 and SHA-512 functions (as for SHA-256 but for longs) */
    private long Ch(
            long x,
            long y,
            long z) {
        return ((x & y) ^ ((~x) & z));
    }

    private long Maj(
            long x,
            long y,
            long z) {
        return ((x & y) ^ (x & z) ^ (y & z));
    }

    private long Sum0(
            long x) {
        return ((x << 36) | (x >>> 28)) ^ ((x << 30) | (x >>> 34)) ^ ((x << 25) | (x >>> 39));
    }

    private long Sum1(
            long x) {
        return ((x << 50) | (x >>> 14)) ^ ((x << 46) | (x >>> 18)) ^ ((x << 23) | (x >>> 41));
    }

    private long Sigma0(
            long x) {
        return ((x << 63) | (x >>> 1)) ^ ((x << 56) | (x >>> 8)) ^ (x >>> 7);
    }

    private long Sigma1(
            long x) {
        return ((x << 45) | (x >>> 19)) ^ ((x << 3) | (x >>> 61)) ^ (x >>> 6);
    }

    /* SHA-384 and SHA-512 Constants
     * (represent the first 64 bits of the fractional parts of the
     * cube roots of the first sixty-four prime numbers)
     */
    static final long K[] = {
        0x428a2f98d728ae22L, 0x7137449123ef65cdL, 0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL,
        0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL, 0xab1c5ed5da6d8118L,
        0xd807aa98a3030242L, 0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
        0x72be5d74f27b896fL, 0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L,
        0xe49b69c19ef14ad2L, 0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L,
        0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L, 0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L,
        0x983e5152ee66dfabL, 0xa831c66d2db43210L, 0xb00327c898fb213fL, 0xbf597fc7beef0ee4L,
        0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL, 0x142929670a0e6e70L,
        0x27b70a8546d22ffcL, 0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL,
        0x650a73548baf63deL, 0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL,
        0xa2bfe8a14cf10364L, 0xa81a664bbc423001L, 0xc24b8b70d0f89791L, 0xc76c51a30654be30L,
        0xd192e819d6ef5218L, 0xd69906245565a910L, 0xf40e35855771202aL, 0x106aa07032bbd1b8L,
        0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
        0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L,
        0x748f82ee5defb2fcL, 0x78a5636f43172f60L, 0x84c87814a1f0ab72L, 0x8cc702081a6439ecL,
        0x90befffa23631e28L, 0xa4506cebde82bde9L, 0xbef9a3f7b2c67915L, 0xc67178f2e372532bL,
        0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL, 0xf57d4f7fee6ed178L,
        0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
        0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL,
        0x4cc5d4becb3e42b6L, 0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L
    };
}
