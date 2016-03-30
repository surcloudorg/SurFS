/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.util;

import com.surfs.nas.server.Initializer;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Function {

    public static String searchFile(String path, String fileName) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        if (!file.isDirectory()) {
            return null;
        }
        File temp;
        if (path.endsWith(File.separator)) {
            temp = new File(path + fileName);
        } else {
            temp = new File(path + File.separator + fileName);
        }
        if (temp.exists()) {
            return temp.getAbsolutePath();
        }
        File[] tempList = file.listFiles();
        for (File tempfile : tempList) {
            if (tempfile.isDirectory()) {
                String str = searchFile(tempfile.getAbsolutePath(), fileName);
                if (str != null) {
                    return str;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param fileName
     * @return String
     */
    public static String searchFile(String fileName) {
        if (Initializer.getWebpath() != null) {
            String fullName = searchFile(Initializer.getWebpath() + "WEB-INF", fileName);
            if (fullName != null) {
                return fullName;
            }
        }
        return null;
    }

    public static byte[] read(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] c = new byte[8192];
        int readChars;
        try {
            while ((readChars = is.read(c)) != -1) {
                os.write(c, 0, readChars);
            }
        } catch (IOException d) {
            throw d;
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return os.toByteArray();
    }
    public static void copy(InputStream in, OutputStream out) throws IOException {
        OutputStream bos = out instanceof BufferedOutputStream ? out : new BufferedOutputStream(out);
        try {
            int len;
            byte[] buf = new byte[8192];
            while ((len = in.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            bos.close();
            in.close();
        }
    }
    /**
     * String[]->String {1,32,54}->"'1','32','54'"
     *
     * @param Strs String[]
     * @return String
     */
    public static String arraytoString(String[] Strs) {
        if (Strs == null) {
            return null;
        }
        StringBuilder s = new StringBuilder();
        for (int ii = 0; ii < Strs.length; ii++) {
            if (ii == Strs.length - 1) {
                s.append("'");
                s.append(Strs[ii]);
                s.append("'");
            } else {
                s.append("'");
                s.append(Strs[ii]);
                s.append("'");
                s.append(",");
            }
        }
        return s.toString();
    }

    /**
     *
     * @return String
     */
    public static String[] getLocalHostIP() {
        List<String> hosts = new ArrayList<>();
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ip = ips.nextElement();
                    if (ip.isSiteLocalAddress()) {
                        hosts.add(ip.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
        }
        if (hosts.isEmpty()) {
            return null;
        }
        String[] hs = new String[hosts.size()];
        return hosts.toArray(hs);
    }

    /**
     *
     * @return String
     */
    public static String[] getPublicIp() {
        List<String> hosts = new ArrayList<>();
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                ip = ni.getInetAddresses().nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()) {
                    hosts.add(ip.getHostAddress());
                }
            }
        } catch (Exception e) {
        }
        if (hosts.isEmpty()) {
            return null;
        }
        String[] hs = new String[hosts.size()];
        return hosts.toArray(hs);
    }

    /**
     * String[]->int[]
     *
     * @param values String[]
     * @return int[]
     */
    public static int[] StringArray2IntArray(String[] values) {
        try {
            int len = values.length;
            int[] res = new int[len];
            for (int ii = 0; ii < len; ii++) {
                res[ii] = Integer.parseInt(values[ii]);
            }
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * String[]->short[]
     *
     * @param values String[]
     * @return short[]
     */
    public static short[] StringArray2ShortArray(String[] values) {
        try {
            int len = values.length;
            short[] res = new short[len];
            for (int ii = 0; ii < len; ii++) {
                res[ii] = Short.parseShort(values[ii]);
            }
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * String[]->long[]
     *
     * @param values String[]
     * @return long[]
     */
    public static long[] StringArray2LongArray(String[] values) {
        try {
            int len = values.length;
            long[] res = new long[len];
            for (int ii = 0; ii < len; ii++) {
                res[ii] = Long.parseLong(values[ii]);
            }
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * String[]->float[]
     *
     * @param values String[]
     * @return float[]
     */
    public static float[] StringArray2FloatArray(String[] values) {
        try {
            int len = values.length;
            float[] res = new float[len];
            for (int ii = 0; ii < len; ii++) {
                res[ii] = Float.parseFloat(values[ii]);
            }
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * String[]->double[]
     *
     * @param values String[]
     * @return double[]
     */
    public static double[] StringArray2DoubleArray(String[] values) {
        try {
            int len = values.length;
            double[] res = new double[len];
            for (int ii = 0; ii < len; ii++) {
                res[ii] = Double.parseDouble(values[ii]);
            }
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * String[] values-> boolean[]
     *
     * @param values String[]
     * @return boolean[]
     */
    public static boolean[] StringArray2BooleanArray(String[] values) {
        try {
            int len = values.length;
            boolean[] res = new boolean[len];
            for (int ii = 0; ii < len; ii++) {
                res[ii] = TextUtils.parseBoolean(values[ii]);
            }
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * byte[]->Integer
     *
     * @param bs
     * @param off
     * @param len
     * @return int
     */
    public static long byte2Integer(byte[] bs, int off, int len) {
        long num = 0;
        for (int ix = off, len1 = off + len, len2 = bs.length; ix < len1 && ix < len2; ++ix) {
            num <<= 8;
            num |= (bs[ix] & 0xff);
        }
        return num;
    }

    /**
     * bytes->int
     *
     * @param bs
     * @return int
     */
    public static int byte2int(byte[] bs) {
        if (bs == null || bs.length < 1 || bs.length > 4) {
            throw new java.lang.IllegalArgumentException();
        }
        return (int) byte2Integer(bs, 0, 4);
    }

    /**
     * bytes->long
     *
     * @param bs
     * @return long
     */
    public static long byte2long(byte[] bs) {
        if (bs == null || bs.length < 1 || bs.length > 8) {
            throw new java.lang.IllegalArgumentException();
        }
        return byte2Integer(bs, 0, 8);
    }

    /**
     * int->byte[]
     *
     * @param n
     * @param bs
     * @param off
     */
    public static void int2byte(int n, byte[] bs, int off) {
        if (bs == null || bs.length < off + 4) {
            throw new java.lang.IllegalArgumentException();
        }
        bs[off] = (byte) (n >>> 24);
        bs[++off] = (byte) (n >>> 16);
        bs[++off] = (byte) (n >>> 8);
        bs[++off] = (byte) (n);
    }

    /**
     * int->byte
     *
     * @param num
     * @return byte[]
     */
    public static byte[] int2byte(int num) {
        byte[] result = new byte[4];
        int2byte(num, result, 0);
        return result;
    }

    /**
     * int->byte[]
     *
     * @param n
     * @param bs
     * @param off
     */
    public static void long2byte(long n, byte[] bs, int off) {
        if (bs == null || bs.length < off + 8) {
            throw new java.lang.IllegalArgumentException();
        }
        bs[off] = (byte) (n >>> 56);
        bs[++off] = (byte) (n >>> 48);
        bs[++off] = (byte) (n >>> 40);
        bs[++off] = (byte) (n >>> 32);
        bs[++off] = (byte) (n >>> 24);
        bs[++off] = (byte) (n >>> 16);
        bs[++off] = (byte) (n >>> 8);
        bs[++off] = (byte) (n);
    }

    /**
     * long->byte
     *
     * @param num
     * @return byte[]
     */
    public static byte[] long2byte(long num) {
        byte[] result = new byte[8];
        long2byte(num, result, 0);
        return result;
    }
}
