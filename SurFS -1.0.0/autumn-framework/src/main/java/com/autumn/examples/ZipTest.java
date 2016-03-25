package com.autumn.examples;

import com.autumn.util.zlib.DeflateSerialize;
import com.autumn.util.zlib.Deflater;
import com.autumn.util.zlib.ZipDeflater;
import com.autumn.util.zlib.Zipper;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterOutputStream;

public class ZipTest {

    public static void main(String[] ae) throws Exception {
        //zip();
        //myzlib();
        zlib();
        //unzlib();
    }

    public static void zip() throws IOException {
        long l = System.currentTimeMillis();
        InputStream in = new FileInputStream("E:\\softwares\\DEEPIN-LITEXP-6.2.iso");
        OutputStream out = new FileOutputStream("d:\\DEEPIN-LITEXP-6.2.zip");
        ZipDeflater df = new ZipDeflater("DEEPIN-LITEXP-6.2.iso");
        byte[] bss = df.toBytes();
        System.out.println("序列化长度:" + bss.length);
        df = (ZipDeflater) DeflateSerialize.newInstance(bss);
        int len;
        byte[] buf = new byte[1024];
        long count = 0;
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                byte[] bs = df.doDeflate(buf, 0, len);
                if (bs.length > 0) {
                    out.write(bs);
                }
            }
            if (count % 102400 == 0) {
                byte[] bs = df.toBytes();
                System.out.println("序列化长度:" + bs.length);
                bs = Zipper.deflate(bs);
                System.out.println("压缩后序列化长度:" + bs.length);
                try {
                    bs = Zipper.inflate(bs);
                } catch (DataFormatException ex) {
                }
                df = (ZipDeflater) DeflateSerialize.newInstance(bs);
            }
            count++;
        }
        bss = df.toBytes();
        df = (ZipDeflater) DeflateSerialize.newInstance(bss);
        byte[] bs = df.doFinal();
        if (bs.length > 0) {
            out.write(bs);
        }
        bss = df.toBytes();
        df = (ZipDeflater) DeflateSerialize.newInstance(bss);
        out.close();
        System.out.println("用时:" + (System.currentTimeMillis() - l));
    }

    public static void unzlib() throws IOException {
        long l = System.currentTimeMillis();
        InputStream in = new FileInputStream("d:\\aaa");
        OutputStream out = new FileOutputStream("D:\\bbb");
        java.util.zip.InflaterOutputStream inf = new java.util.zip.InflaterOutputStream(out);
        int len;
        byte[] buf = new byte[1024];
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                inf.write(buf, 0, len);
            }
        }
        inf.finish();
        inf.flush();
        inf.close();
        System.out.println("用时:" + (System.currentTimeMillis() - l));
    }

    public static void myzlib() throws IOException {
        long l = System.currentTimeMillis();
        InputStream in = new FileInputStream("E:\\softwares\\Firefox-latest.exe");
        OutputStream out = new FileOutputStream("E:\\XP_64bit.iso");
        Deflater df = new Deflater();
        byte[] bss = df.toBytes();
        df = DeflateSerialize.newInstance(bss);
        int len;
        byte[] buf = new byte[1024];
        long count = 0;
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                byte[] bs = df.doDeflate(buf, 0, len);
                if (bs.length > 0) {
                    out.write(bs);
                }
            }
            if (count % 102400 == 0) {
                byte[] bs = df.toBytes();
                System.out.println("序列化长度:" + bs.length);
                df = DeflateSerialize.newInstance(bs);
            }
            count++;
        }
        bss = df.toBytes();
        df = DeflateSerialize.newInstance(bss);
        byte[] bs = df.doFinal();
        if (bs.length > 0) {
            out.write(bs);
        }
        bss = df.toBytes();
        df = DeflateSerialize.newInstance(bss);
        out.close();
        System.out.println("用时:" + (System.currentTimeMillis() - l));
    }

    public static void zlib() throws IOException {
        long l = System.currentTimeMillis();
        InputStream in = new FileInputStream("d:\\a.txt");
        in=new com.autumn.util.zlib.DeflaterInputStream(in);
        OutputStream inf = new FileOutputStream("d:\\b");
        //DeflaterOutputStream inf = new  DeflaterOutputStream(out);
        int len;
        byte[] buf = new byte[1024];
        while (true) {
            len = in.read(buf);
            if (len <= 0) {
                break;
            } else {
                inf.write(buf, 0, len);
            }
        }
       // inf.finish();
        inf.flush();
        inf.close();
        System.out.println("用时:" + (System.currentTimeMillis() - l));
    }
}
