package com.autumn.util;

import com.autumn.core.security.Base64;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

/**
 * <p>
 * Title: 常用函数</p>
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
public class Function {

    /**
     * String[]->String 如{1,32,54}->"'1','32','54'"
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
     * 检查图片文件格式
     *
     * @param mapObj
     * @return String
     */
    public static String getContentType(byte[] mapObj) {
        String type = "";
        ByteArrayInputStream bais = null;
        MemoryCacheImageInputStream mcis = null;
        try {
            bais = new ByteArrayInputStream(mapObj);
            mcis = new MemoryCacheImageInputStream(bais);
            Iterator itr = ImageIO.getImageReaders(mcis);
            while (itr.hasNext()) {
                ImageReader reader = (ImageReader) itr.next();
                type = "image/" + reader.getFormatName().toLowerCase();
            }
        } catch (Exception r) {
            type = null;
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException ioe) {
                }
            }
            if (mcis != null) {
                try {
                    mcis.close();
                } catch (IOException ioe) {
                }
            }
        }
        return type;
    }

    /**
     * 生成basic认证字符串
     *
     * @param user
     * @param pwd
     * @return String
     */
    public static String basicEncoder(String user, String pwd) {
        String Authorization = user + ":" + pwd;
        Authorization = Base64.encode(Authorization.getBytes());
        Authorization = "Basic " + Authorization;
        return Authorization;
    }

    /**
     * BASIC认证解析
     *
     * @param encoded String BASIC认证字符串
     * @return String[] 0为用户名 1为密码
     */
    public static String[] basicParser(String encoded) {
        try {
            String tmp = encoded.substring(6);
            byte[] bytes = Base64.decode(tmp);
            String up = new String(bytes);
            String[] ss = new String[2];
            ss[0] = up.substring(0, up.indexOf(":"));
            ss[1] = up.substring(up.indexOf(":") + 1);
            return ss;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取本机的私网IP
     *
     * @return String 如:192.168.0.*
     */
    public static String[] getLocalHostIP() {
        List<String> hosts = new ArrayList<String>();
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
     * 获取工网ip
     *
     * @return String
     */
    public static String[] getPublicIp() {
        List<String> hosts = new ArrayList<String>();
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
     * @param off 偏移量
     * @param len 长度
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
