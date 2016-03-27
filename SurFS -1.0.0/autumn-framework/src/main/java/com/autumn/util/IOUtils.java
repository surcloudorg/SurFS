/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.util;

import com.autumn.core.ObjectInputStreamWCL;
import com.autumn.core.security.Base64;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>Title: IO函数</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class IOUtils {

    /**
     * 复制InputStream->OutputStream
     *
     * @param in InputStream
     * @param out OutputStream
     * @throws IOException
     */
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
     * 从InputStream读出byte[]
     *
     * @param is
     * @return byte[]
     * @throws IOException
     */
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

    /**
     * 将byte写入output
     *
     * @param output
     * @param out
     * @throws IOException
     */
    public static void write(byte[] output, OutputStream out) throws IOException {
        OutputStream bos = out instanceof BufferedOutputStream ? out : new BufferedOutputStream(out);
        try {
            bos.write(output);
            bos.flush();
        } catch (IOException d) {
            throw d;
        } finally {
            bos.close();
        }
    }

    /**
     * Object-->byte[]
     *
     * @param obj
     * @return byte[]
     * @throws IOException
     */
    public static byte[] objectToBytes(Object obj) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream obout = new ObjectOutputStream(os);
        obout.writeObject(obj);
        obout.flush();
        return os.toByteArray();
    }

    /**
     * byte[]-->Object
     *
     * @param bs
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object bytesToObject(byte[] bs) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bo = new ByteArrayInputStream(bs);
        ObjectInputStream oin = new ObjectInputStream(bo);
        return oin.readObject();
    }

    /**
     * byte[]-->Object
     *
     * @param bs
     * @param cl
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object bytesToObject(byte[] bs, ClassLoader cl) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bo = new ByteArrayInputStream(bs);
        if (cl != null) {
            ObjectInputStreamWCL oin = new ObjectInputStreamWCL(bo, cl);
            return oin.readObject();
        } else {
            ObjectInputStream oin = new ObjectInputStream(bo);
            return oin.readObject();
        }
    }

    /**
     * Object-->String
     *
     * @param obj
     * @return String
     * @throws IOException
     */
    public static String objectToString(Object obj) throws IOException {
        return Base64.encode(objectToBytes(obj));
    }

    /**
     * String-->Object
     *
     * @param ss
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object stringToObject(String ss) throws IOException, ClassNotFoundException {
        byte[] bs = Base64.decode(ss);
        return bytesToObject(bs);
    }

    /**
     * 判断字节流的字符集
     *
     * @param bytes byte[]
     * @return String
     */
    public static String getCharset(byte[] bytes) {
        return getCharset(new ByteArrayInputStream(bytes));
    }

    /**
     * 判断输入流的字符集
     *
     * @param is InputStream
     * @return String
     */
    public static String getCharset(InputStream is) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(is);
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset;
            }//这是常用的文件头
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0) {
                        break;
                    }
                    if (0x80 <= read && read <= 0xBF) { // 单独出现BF以下的，也算是GBK
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) { // 双字节 (0xC0 - 0xDF) (0x80
                            continue;
                        } else {
                            break;
                        }
                    } else if (0xE0 <= read && read <= 0xEF) { // 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
            }
        }
        return charset;
    }

    /**
     * 关闭Reader
     *
     * @param reader
     */
    public static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * 关闭Writer
     *
     * @param writer
     */
    public static void close(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * 关闭Socket
     *
     * @param socket
     */
    public static void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * 关闭ServerSocket
     *
     * @param serverSocket
     */
    public static void close(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
            }
        }
    }
}
