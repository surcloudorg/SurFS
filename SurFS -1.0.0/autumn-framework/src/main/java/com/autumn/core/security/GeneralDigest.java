package com.autumn.core.security;

import com.autumn.util.Function;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Title: 摘要
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public abstract class GeneralDigest extends MessageDigest implements Externalizable {

    private static final long serialVersionUID = 20140701123456L;

    protected int byte_length = 4;
    private final byte[] xBuf;//待转换缓冲区
    private byte xBufOff;//缓冲区已写入字节数
    private long byteCount1;//已输入字节数  
    private long byteCount2;//已输入字节数  
    private int digestLength = 0;
    private boolean finished = false;

    /**
     * 序列化
     *
     * @return byte[]
     */
    public byte[] toBytes() {
        byte[] bs = byte_length == 4 ? new byte[1 + byte_length + 1 + 8] : new byte[1 + byte_length + 1 + 8 + 8];
        bs[0] = (byte) (finished ? 1 : 0);
        System.arraycopy(xBuf, 0, bs, 1, byte_length);
        bs[byte_length + 1] = xBufOff;
        Function.long2byte(byteCount1, bs, byte_length + 2);
        if (byte_length == 8) {
            Function.long2byte(byteCount2, bs, byte_length + 2 + 8);
        }
        return bs;
    }

    /**
     * 反序列化
     *
     * @param bs
     */
    protected void fill(byte[] bs) {
        finished = bs[0] != 0;
        System.arraycopy(bs, 1, xBuf, 0, byte_length);
        xBufOff = bs[byte_length + 1];
        byteCount1 = Function.byte2Integer(bs, byte_length + 2, 8);
        if (byte_length == 8) {
            byteCount2 = Function.byte2Integer(bs, byte_length + 2 + 8, 8);
        }
    }

    /**
     * 构造
     *
     * @param algorithm
     */
    protected GeneralDigest(String algorithm) {
        super(algorithm);
        if (algorithm.equals("SHA1")) {
            digestLength = SHA1Digest.DIGEST_LENGTH;
        } else if (algorithm.equals("SHA512")) {
            digestLength = SHA512Digest.DIGEST_LENGTH;
            byte_length = 8;
        } else {
            digestLength = MD5Digest.DIGEST_LENGTH;
        }
        xBuf = new byte[byte_length];
        xBufOff = 0;
    }

    /**
     * 序列化
     *
     * @param out
     * @throws IOException
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.write(toBytes());
    }

    /**
     * 初始化 (清零)
     */
    protected void engineReset() {
        if (finished) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        byteCount1 = 0;
        byteCount2 = 0;
        xBufOff = 0;
        Arrays.fill(xBuf, (byte) 0);
    }

    /**
     * 输出摘要值
     *
     * @return byte[]
     */
    @Override
    protected byte[] engineDigest() {
        byte[] bs = new byte[this.getDigestLength()];
        doFinal(bs, 0);
        return bs;
    }

    @Override
    protected int engineGetDigestLength() {
        return digestLength;
    }

    /**
     * 更新摘要值
     *
     * @param in
     */
    @Override
    protected synchronized void engineUpdate(byte in) {
        if (finished) {
            throw new IllegalArgumentException("finished");
        }
        xBuf[xBufOff++] = in;
        if (xBufOff == xBuf.length) {
            processWord(xBuf, 0);
            xBufOff = 0;
        }
        byteCount1++;
    }

    /**
     * 更新摘要值
     *
     * @param in
     * @param inOff
     * @param len
     */
    @Override
    protected synchronized void engineUpdate(byte[] in, int inOff, int len) {
        if (finished) {
            throw new IllegalArgumentException("finished");
        }
        while ((xBufOff != 0) && (len > 0)) {//填充缓冲区
            engineUpdate(in[inOff]);
            inOff++;
            len--;
        }
        while (len > xBuf.length) {
            processWord(in, inOff);
            inOff += xBuf.length;
            len -= xBuf.length;
            byteCount1 += xBuf.length;
        }
        while (len > 0) {
            engineUpdate(in[inOff]);
            inOff++;
            len--;
        }
    }

    /**
     * 完成转换
     */
    protected void finish() {
        if (finished) {
            return;
        }
        if (byte_length == 8) {
            adjustByteCounts();
            long lowBitLength = getByteCount() << 3;
            long hiBitLength = byteCount2;
            update((byte) 128);
            byteCount1--;
            while (xBufOff != 0) {
                update((byte) 0);
                byteCount1--;
            }
            processLength(lowBitLength, hiBitLength);
        } else {
            long bitLength = (getByteCount() << 3);
            engineUpdate((byte) 128);
            byteCount1--;
            while (xBufOff != 0) {
                update((byte) 0);
                byteCount1--;
            }
            processLength(bitLength);
        }
        processBlock();
        finished = true;
    }

    void adjustByteCounts() {
        if (byteCount1 > 0x1fffffffffffffffL) {
            byteCount2 += (byteCount1 >>> 61);
            byteCount1 &= 0x1fffffffffffffffL;
        }
    }

    /**
     * byte[]-〉int[]
     */
    void byte2int(byte[] src, int srcOffset, int[] dst, int dstOffset, int length) {
        while (length-- > 0) {
            dst[dstOffset++] = (src[srcOffset++] << 24) | ((src[srcOffset++] & 0xFF) << 16) | ((src[srcOffset++] & 0xFF) << 8) | (src[srcOffset++] & 0xFF);
        }
    }

    /**
     * byte[]-〉long[]
     */
    void byte2long(byte[] src, int srcOffset, long[] dst, int dstOffset, int length) {
        while (length-- > 0) {
            long num = 0;
            for (int i = 0; i < 8; i++) {
                num <<= 8;
                num |= (src[srcOffset++] & 0xff);
            }
            dst[dstOffset++] = num;
        }
    }

    /**
     * int->byte
     */
    void int2byte(int[] src, int srcOffset, byte[] dst, int dstOffset, int length) {
        while (length-- > 0) {
            dst[dstOffset++] = (byte) (src[srcOffset] >>> 24);
            dst[dstOffset++] = (byte) (src[srcOffset] >>> 16);
            dst[dstOffset++] = (byte) (src[srcOffset] >>> 8);
            dst[dstOffset++] = (byte) (src[srcOffset]);
            srcOffset++;
        }
    }

    /**
     * int->byte
     */
    void long2byte(long[] src, int srcOffset, byte[] dst, int dstOffset, int length) {
        while (length-- > 0) {
            dst[dstOffset++] = (byte) (src[srcOffset] >>> 56);
            dst[dstOffset++] = (byte) (src[srcOffset] >>> 48);
            dst[dstOffset++] = (byte) (src[srcOffset] >>> 40);
            dst[dstOffset++] = (byte) (src[srcOffset] >>> 32);
            dst[dstOffset++] = (byte) (src[srcOffset] >>> 24);
            dst[dstOffset++] = (byte) (src[srcOffset] >>> 16);
            dst[dstOffset++] = (byte) (src[srcOffset] >>> 8);
            dst[dstOffset++] = (byte) (src[srcOffset]);
            srcOffset++;
        }
    }

    /**
     * int->byte[]
     */
    void unpackWord(int n, byte[] bs, int off) {
        bs[off] = (byte) n;
        bs[off + 1] = (byte) (n >>> 8);
        bs[off + 2] = (byte) (n >>> 16);
        bs[off + 3] = (byte) (n >>> 24);
    }

    /**
     * 完成并重置
     *
     * @param out
     * @param outOff
     * @return int
     */
    protected abstract int doFinal(byte[] out, int outOff);

    /**
     * 加入转换
     *
     * @param in
     * @param inOff
     */
    protected abstract void processWord(byte[] in, int inOff);

    /**
     * 加入结束标志
     *
     * @param bitLength
     */
    protected abstract void processLength(long bitLength);

    /**
     * 加入结束标志
     *
     * @param lowW
     * @param hiW
     */
    protected abstract void processLength(long lowW, long hiW);

    /**
     * 转换
     */
    protected abstract void processBlock();

    /**
     * @return the byteCount
     */
    public long getByteCount() {
        return byteCount1;
    }

    public boolean finished() {
        return finished;
    }
}
