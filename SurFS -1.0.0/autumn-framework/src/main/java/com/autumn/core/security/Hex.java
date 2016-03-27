/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.security;

/**
 * <p>
 * Title: Hex加解码</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class Hex {

    private static final Hex encoder = new Hex();
    private static final byte[] hexTable = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

    /**
     * 加码
     *
     * @param array
     * @return String
     */
    public static String encode(byte[] array) {
        return new String(encode(array, 0, array.length));
    }

    /**
     * 加码
     *
     * @param array
     * @param off
     * @param length
     * @return byte[]
     */
    public static byte[] encode(byte[] array, int off, int length) {
        byte[] enc = new byte[length * 2];
        encoder.encode(array, off, length, enc, 0);
        return enc;
    }

    /**
     * 解码
     *
     * @param string
     * @return byte[]
     */
    public static byte[] decode(String string) {
        byte[] bytes = new byte[string.length() / 2];
        String buf = string.toLowerCase();
        for (int i = 0; i < buf.length(); i += 2) {
            char left = buf.charAt(i);
            char right = buf.charAt(i + 1);
            int index = i / 2;
            if (left < 'a') {
                bytes[index] = (byte) (left - '0' << 4);
            } else {
                bytes[index] = (byte) (left - 'a' + 10 << 4);
            }
            if (right < 'a') {
                int tmp92_90 = index;
                byte[] tmp92_89 = bytes;
                tmp92_89[tmp92_90] = (byte) (tmp92_89[tmp92_90]
                        + (byte) (right - '0'));
            } else {
                int tmp109_107 = index;
                byte[] tmp109_106 = bytes;
                tmp109_106[tmp109_107] = (byte) (tmp109_106[tmp109_107]
                        + (byte) (right - 'a' + 10));
            }
        }
        return bytes;
    }

    /**
     * 解码
     *
     * @param array
     * @return byte[]
     */
    public static byte[] decode(byte[] array) {
        byte[] bytes = new byte[array.length / 2];
        encoder.decode(array, 0, array.length, bytes, 0);
        return bytes;
    }

    /**
     * 加码
     *
     * @param in
     * @param inOff
     * @param length
     * @param out
     * @param outOff
     * @return int
     */
    public int encode(byte[] in, int inOff, int length, byte[] out, int outOff) {
        int i = 0;
        for (int j = 0; i < length;) {
            out[(outOff + j)] = hexTable[(in[inOff] >> 4 & 0xF)];
            out[(outOff + j + 1)] = hexTable[(in[inOff] & 0xF)];
            ++inOff;
            ++i;
            j += 2;
        }
        return (length * 2);
    }

    /**
     * 解码
     *
     * @param in
     * @param inOff
     * @param length
     * @param out
     * @param outOff
     * @return int
     */
    public int decode(byte[] in, int inOff, int length, byte[] out, int outOff) {
        int halfLength = length / 2;
        for (int i = 0; i < halfLength; ++i) {
            byte left = in[(inOff + i * 2)];
            byte right = in[(inOff + i * 2 + 1)];
            if (left < 97) {
                out[outOff] = (byte) (left - 48 << 4);
            } else {
                out[outOff] = (byte) (left - 97 + 10 << 4);
            }
            if (right < 97) {
                int tmp87_85 = outOff;
                byte[] tmp87_83 = out;
                tmp87_83[tmp87_85] = (byte) (tmp87_83[tmp87_85]
                        + (byte) (right - 48));
            } else {
                int tmp105_103 = outOff;
                byte[] tmp105_101 = out;
                tmp105_101[tmp105_103] = (byte) (tmp105_101[tmp105_103]
                        + (byte) (right - 97 + 10));
            }
            ++outOff;
        }
        return halfLength;
    }
}
