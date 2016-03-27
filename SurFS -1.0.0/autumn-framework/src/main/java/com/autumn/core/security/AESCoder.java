/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.security;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Title: aes算法,可序列化
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class AESCoder implements Externalizable {

    private boolean finished = false;
    private byte[] key = null;//16b
    private Cipher cipher = null;
    private final byte[] buffer = new byte[128];
    private byte pos = 0;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    /**
     * 序列化
     *
     * @return byte[] len=146
     */
    public byte[] toBytes() {
        byte[] bs = new byte[128 + 16 + 2];
        System.arraycopy(getKey(), 0, bs, 0, 16);
        System.arraycopy(buffer, 0, bs, 16, 128);
        bs[128 + 16] = pos;
        bs[128 + 16 + 1] = (byte) (finished ? 1 : 0);
        return bs;
    }

    /**
     * 反序列化
     *
     * @param bs
     */
    private void fill(byte[] bs) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        this.key = new byte[16];
        System.arraycopy(bs, 0, getKey(), 0, 16);
        init();
        System.arraycopy(bs, 16, buffer, 0, 128);
        pos = bs[128 + 16];
        finished = bs[128 + 16 + 1] != 0;
    }

    /**
     * 反序列化
     *
     * @param in
     * @throws IOException
     */
    public void readExternal(ObjectInput in) throws IOException {
        byte[] bs = new byte[128 + 16 + 2];
        in.read(bs);
        try {
            fill(bs);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
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
     * 构造
     *
     * @param key
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    public AESCoder(byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException();
        }
        if (key.length == 128 + 16 + 2) {
            fill(key);
        } else {
            this.key = key;
            init();
        }
    }

    /**
     * 初始化
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    private void init() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec skeySpec = new SecretKeySpec(getKey(), "AES");
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
    }
    final byte[] buf1 = new byte[1];

    /**
     * 压缩
     *
     * @param b
     * @return byte[]
     * @throws IOException
     */
    public byte[] update(byte b) throws IOException {
        buf1[0] = b;
        return update(buf1);
    }

    /**
     * 加密
     *
     * @param data
     * @return byte[]
     * @throws java.io.IOException
     */
    public synchronized byte[] update(byte[] data) throws IOException {
        if (finished) {
            throw new IOException("finished");
        }
        int dp = 0, num;
        while ((num = data.length - dp) > 0) {
            if (pos + num >= 128) {
                System.arraycopy(data, dp, buffer, pos, 128 - pos);
                dp = dp + 128 - pos;
                pos = 0;
                out.write(cipher.update(buffer));
            } else {
                System.arraycopy(data, dp, buffer, pos, num);
                pos = (byte) ((int) pos + num);
                break;
            }
        }
        byte[] bs = out.toByteArray();
        out.reset();
        return bs;
    }

    /**
     * 加密
     *
     * @param data
     * @param off
     * @param len
     * @return byte[]
     * @throws IOException
     */
    public byte[] update(byte[] data, int off, int len) throws IOException {
        if (finished) {
            throw new IOException("finished");
        } else if (off < 0 | len < 0 | off + len > data.length) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return new byte[0];
        } else {
            byte[] ndata = new byte[len];
            System.arraycopy(data, off, ndata, 0, len);
            return update(ndata);
        }
    }

    /**
     * 加密完毕
     *
     * @param data
     * @return byte[]
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws java.io.IOException
     */
    public synchronized byte[] doFinal(byte data) throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (finished) {
            throw new IOException("finished");
        }
        out.write(update(data));
        out.write(doFinal());
        return out.toByteArray();
    }

    /**
     * 加密完毕
     *
     * @param data
     * @return byte[]
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws java.io.IOException
     */
    public synchronized byte[] doFinal(byte[] data) throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (finished) {
            throw new IOException("finished");
        }
        out.write(update(data));
        out.write(doFinal());
        return out.toByteArray();
    }

    /**
     * 加密完毕
     *
     * @param data
     * @param off
     * @param len
     * @return byte[]
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws java.io.IOException
     */
    public synchronized byte[] doFinal(byte[] data, int off, int len) throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (finished) {
            throw new IOException("finished");
        }
        out.write(update(data, off, len));
        out.write(doFinal());
        return out.toByteArray();
    }

    /**
     * 加密完毕
     *
     * @return byte[]
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws java.io.IOException
     */
    public synchronized byte[] doFinal() throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (finished) {
            throw new IOException("finished");
        }
        finished = true;
        if (pos == 0) {
            return cipher.doFinal();
        } else {
            return cipher.doFinal(buffer, 0, pos);
        }

    }

    public boolean finished() {
        return finished;
    }

    /**
     * @return the key
     */
    public byte[] getKey() {
        return key;
    }
}
