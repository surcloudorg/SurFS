/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.examples;

import com.autumn.core.security.AESCoder;
import com.autumn.core.security.Hex;
import com.autumn.core.security.MD5Digest;
import com.autumn.core.security.SHA1Digest;
import com.autumn.core.security.SHA512Digest;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class SecurityTest {

    public static void main(String[] ae) throws Exception {
        //encrypt();
        //decrypt();
        //md5();
       // mymd5();
        //mysha();
        //sha();
        mysha512();
        sha512();
    }

    public static void encrypt() throws Exception {
        byte[] encrypted = "1234567812345678".getBytes();
        AESCoder df = new AESCoder(encrypted);
        long l = System.currentTimeMillis();
        InputStream in = new FileInputStream("E:\\softwares\\DEEPIN-LITEXP-6.2.iso");
        OutputStream out = new FileOutputStream("E:\\DEEPIN-LITEXP-6.2.enc");
        byte[] bss = df.toBytes();
        df = new AESCoder(bss);
        int len;
        byte[] buf = new byte[1024];
        long count = 0;
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                byte[] bs = df.update(buf, 0, len);
                if (bs.length > 0) {
                    out.write(bs);
                }
            }
            if (count == 1024 * 100 || count == 1024 * 200 || count == 1024 * 300 || count == 1024 * 400 || count == 1024 * 500) {
                byte[] bs = df.toBytes();
                df = new AESCoder(bs);
            }
            count++;
        }
        bss = df.toBytes();
        df = new AESCoder(bss);
        byte[] bs = df.doFinal();
        if (bs.length > 0) {
            out.write(bs);
        }
        out.close();
        System.out.println("用时:" + (System.currentTimeMillis() - l));
    }

    public static void decrypt() throws Exception {
        byte[] encrypted = "1234567812345678".getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(encrypted, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        InputStream in = new FileInputStream("E:\\DEEPIN-LITEXP-6.2.enc");
        OutputStream out = new FileOutputStream("D:\\DEEPIN-LITEXP-6.2.ISO");
        in = new CipherInputStream(in, cipher);
        long l = System.currentTimeMillis();
        int len;
        byte[] buf = new byte[1024];
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                out.write(buf, 0, len);
            }
        }
        out.flush();
        out.close();
        System.out.println("用时:" + (System.currentTimeMillis() - l));
    }

    public static void mymd5() throws Exception {
        MD5Digest newsha = new MD5Digest();
        byte[] bss = newsha.toBytes();
        newsha = new MD5Digest(bss);
        InputStream in = new FileInputStream("E:\\softwares\\DEEPIN-LITEXP-6.2.iso");
        long l = System.currentTimeMillis();
        int len;
        byte[] buf = new byte[3434];
        int count = 0;
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                newsha.update(buf, 0, len);
            }
            if (count == 100 || count == 200) {
                byte[] bs = newsha.toBytes();
                newsha = new MD5Digest(bs);
            }
            count++;
        }
        byte shatmp[] = newsha.digest();
        System.out.println("用时:" + (System.currentTimeMillis() - l) + ",hash:" + Hex.encode(shatmp));
        bss = newsha.toBytes();
        newsha = new MD5Digest(bss);
        shatmp = newsha.digest();
        System.out.println("用时:" + (System.currentTimeMillis() - l) + ",hash:" + Hex.encode(shatmp));
    }

    public static void md5() throws Exception {
        MessageDigest sha = MessageDigest.getInstance("MD5");
        InputStream in = new FileInputStream("E:\\softwares\\DEEPIN-LITEXP-6.2.iso");
        long l = System.currentTimeMillis();
        int len;
        byte[] buf = new byte[3434];
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                sha.update(buf, 0, len);
            }
        }
        byte shatmp1[] = sha.digest();
        System.out.println("用时:" + (System.currentTimeMillis() - l) + ",hash:" + Hex.encode(shatmp1));
    }

    public static void sha() throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA1");
        InputStream in = new FileInputStream("E:\\softwares\\DEEPIN-LITEXP-6.2.iso");
        long l = System.currentTimeMillis();
        int len;
        byte[] buf = new byte[3434];
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                sha.update(buf, 0, len);
            }
        }

        byte shatmp1[] = sha.digest();
        System.out.println("用时:" + (System.currentTimeMillis() - l) + ",hash:" + Hex.encode(shatmp1));
    }

    public static void mysha() throws Exception {
        SHA1Digest newsha = new SHA1Digest();
        byte[] bss = newsha.toBytes();
        newsha = new SHA1Digest(bss);
        InputStream in = new FileInputStream("E:\\softwares\\DEEPIN-LITEXP-6.2.iso");
        long l = System.currentTimeMillis();
        int len;
        byte[] buf = new byte[3434];
        int count = 0;
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                newsha.update(buf, 0, len);
            }
            if (count == 100 || count == 200) {
                byte[] bs = newsha.toBytes();
                newsha = new SHA1Digest(bs);
            }
            count++;
        }
        byte shatmp[] = newsha.digest();
        System.out.println("用时:" + (System.currentTimeMillis() - l) + ",hash:" + Hex.encode(shatmp));
        bss = newsha.toBytes();
        newsha = new SHA1Digest(bss);
        shatmp = newsha.digest();
        System.out.println("用时:" + (System.currentTimeMillis() - l) + ",hash:" + Hex.encode(shatmp));
    }

    public static void sha512() throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-512");
        InputStream in = new FileInputStream("E:\\softwares\\XP_64bit.iso");
        long l = System.currentTimeMillis();
        int len;
        byte[] buf = new byte[3434];
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                sha.update(buf, 0, len);
            }
        }

        byte shatmp1[] = sha.digest();
        System.out.println("用时:" + (System.currentTimeMillis() - l) + ",hash:" + Hex.encode(shatmp1));
    }

    public static void mysha512() throws Exception {
        SHA512Digest newsha = new SHA512Digest();
        byte[] bss = newsha.toBytes();
        newsha = new SHA512Digest(bss);
        InputStream in = new FileInputStream("E:\\softwares\\XP_64bit.iso");
        long l = System.currentTimeMillis();
        int len;
        byte[] buf = new byte[3434];
        int count = 0;
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                newsha.update(buf, 0, len);
            }
            if (count == 100 || count == 200) {
                byte[] bs = newsha.toBytes();
                newsha = new SHA512Digest(bs);
            }
            count++;
        }
        byte shatmp[] = newsha.digest();
        System.out.println("用时:" + (System.currentTimeMillis() - l) + ",hash:" + Hex.encode(shatmp));
        bss = newsha.toBytes();
        newsha = new SHA512Digest(bss);
        shatmp = newsha.digest();
        System.out.println("用时:" + (System.currentTimeMillis() - l) + ",hash:" + Hex.encode(shatmp));
    }
}
