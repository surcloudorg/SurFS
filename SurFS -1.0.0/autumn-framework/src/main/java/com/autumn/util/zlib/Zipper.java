package com.autumn.util.zlib;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * Title: 压缩解压缩
 *
 * Copyright: Autumn Copyright (c) 2011
 *
 * Company: Autumn
 *
 * @author 刘社朋
 * @version 2.0
 */
public class Zipper {

    /**
     * 压缩文件夹
     *
     * @param zipfile String
     * @param inputDirectory String
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void zip(String zipfile, String inputDirectory) throws FileNotFoundException, IOException {
        zip(zipfile, inputDirectory, "UTF-8");
    }

    /**
     * 压缩文件夹
     *
     * @param zipfile
     * @param inputDirectory
     * @param charset
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void zip(String zipfile, String inputDirectory, String charset) throws FileNotFoundException, IOException {
        File file = new File(inputDirectory);
        if (!file.exists()) {
            throw new FileNotFoundException(inputDirectory + "不存在");
        }
        ZipOutputStream out;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipfile), Charset.forName(charset));
        } catch (Throwable t) {
            out = new ZipOutputStream(new FileOutputStream(zipfile));
        }
        String name = file.getName();
        int index = inputDirectory.lastIndexOf(name);
        String root = inputDirectory.substring(0, index);
        zip(out, file.getAbsolutePath(), root);
        out.close();
    }

    /**
     * 压缩文件夹
     *
     * @param outstream
     * @param inputDirectory
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void zip(OutputStream outstream, String inputDirectory) throws FileNotFoundException, IOException {
        zip(outstream, inputDirectory, "UTF-8");
    }

    /**
     * 压缩文件夹
     *
     * @param outstream
     * @param inputDirectory
     * @param charset
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void zip(OutputStream outstream, String inputDirectory, String charset) throws FileNotFoundException, IOException {
        File file = new File(inputDirectory);
        if (!file.exists()) {
            throw new FileNotFoundException(inputDirectory + "不存在");
        }
        ZipOutputStream out;
        try {
            out = new ZipOutputStream(outstream, Charset.forName(charset));
        } catch (Throwable t) {
            out = new ZipOutputStream(outstream);
        }
        String name = file.getName();
        int index = inputDirectory.lastIndexOf(name);
        String root = inputDirectory.substring(0, index);
        zip(out, file.getAbsolutePath(), root);
        out.close();
    }

    /**
     * 压缩文件夹
     *
     * @param out
     * @param inputDirectory
     * @param root
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static void zip(ZipOutputStream out, String inputDirectory, String root) throws FileNotFoundException, IOException {
        File file = new File(inputDirectory);
        if (file.isDirectory()) {
            File[] mks = file.listFiles();
            if (mks.length == 0) {
                String zippath = file.getAbsolutePath().substring(root.length());
                ZipEntry ze = new ZipEntry(zippath + File.separator);//ze.setTime(file.lastModified());
                out.putNextEntry(ze);
                out.closeEntry();
            } else {
                for (File f : mks) {
                    zip(out, f.getAbsolutePath(), root);
                }
            }
        } else {
            String zippath = file.getAbsolutePath().substring(root.length());
            ZipEntry ze = new ZipEntry(zippath);
            ze.setTime(file.lastModified());
            out.putNextEntry(ze);
            int s;
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(file);
            while ((s = fis.read(buffer)) > 0) {
                out.write(buffer, 0, s);
                out.flush();
            }
            fis.close();
            out.closeEntry();
        }
    }

    /**
     * 解压至文件夹
     *
     * @param zipFileName String
     * @param outputDirectory String
     * @return * @throws IOException
     * @throws FileNotFoundException
     */
    public static List<String> unzip(String zipFileName, String outputDirectory) throws FileNotFoundException, IOException {
        return unzip(zipFileName, outputDirectory, "UTF-8");
    }

    /**
     * 解压至文件夹
     *
     * @param zipFileName
     * @param outputDirectory
     * @param charset
     * @return List<String>
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static List<String> unzip(String zipFileName, String outputDirectory, String charset) throws FileNotFoundException, IOException {
        InputStream is = new FileInputStream(zipFileName);
        return unzip(is, outputDirectory, charset);
    }

    /**
     * 解压至文件夹
     *
     * @param is
     * @param outputDirectory
     * @return List<String>
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static List<String> unzip(InputStream is, String outputDirectory) throws FileNotFoundException, IOException {
        return unzip(is, outputDirectory, "UTF-8");
    }

    /**
     * 解压至文件夹
     *
     * @param is
     * @param outputDirectory
     * @param charset
     * @return List<String>
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static List<String> unzip(InputStream is, String outputDirectory, String charset) throws FileNotFoundException, IOException {
        List<String> list = new ArrayList<String>();
        ZipInputStream in;
        try {
            in = new ZipInputStream(is, Charset.forName(charset));
        } catch (Throwable t) {
            in = new ZipInputStream(is);
        }
        ZipEntry z = in.getNextEntry();
        while (z != null) {
            if ((!z.isDirectory()) && (!z.getName().endsWith("/"))) {
                list.add(z.getName());
                File f = new File(outputDirectory + File.separator + z.getName());
                File path = new File(f.getParent());
                if (!path.exists()) {
                    path.mkdirs();
                }
                f.createNewFile();
                FileOutputStream out = new FileOutputStream(f);
                byte[] buf = new byte[1024];
                int rc;
                while ((rc = in.read(buf)) > 0) {
                    out.write(buf, 0, rc);
                }
                out.close();
                f.setLastModified(z.getTime());
            } else {
                File f = new File(outputDirectory + File.separator + z.getName());
                if (!f.exists()) {
                    f.mkdirs();
                }
            }
            z = in.getNextEntry(); //读取下一个ZipEntry
        }
        in.close();
        return list;
    }

    /**
     * 解压缩字节流
     *
     * @param bs byte[]
     * @return byte[]
     */
    public static byte[] gunzip(byte[] bs) {
        InputStream bi = new ByteArrayInputStream(bs);
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num;
        byte[] out = null;
        try {
            GZIPInputStream gzip = new GZIPInputStream(bi);
            while ((num = gzip.read(buf)) > 0) {
                bo.write(buf, 0, num);
            }
            bo.flush();
            out = bo.toByteArray();
            gzip.close();
            bo.close();
        } catch (Exception er) {
        }
        return out;
    }

    /**
     * 压缩字节流
     *
     * @param bs byte[]
     * @return byte[]
     */
    public static byte[] gzip(byte[] bs) {
        byte[] out = null;
        ByteArrayOutputStream bo = new ByteArrayOutputStream(bs.length);
        try {
            GZIPOutputStream gzip = new GZIPOutputStream(bo);
            gzip.write(bs);
            gzip.flush();
            gzip.close();
            out = bo.toByteArray();
            bo.close();
        } catch (Exception er) {
        }
        return out;
    }

    /**
     * 压缩
     *
     * @param bs
     * @return byte[]
     */
    public static byte[] deflate(byte[] bs) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bs.length);
        java.util.zip.Deflater compressor = new java.util.zip.Deflater();
        try {
            compressor.setLevel(9);  //将当前压缩级别设置为指定值。  
            compressor.setInput(bs);
            compressor.finish(); //调用时，指示压缩应当以输入缓冲区的当前内容结尾。  
            final byte[] buf = new byte[1024];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            compressor.end(); //关闭解压缩器并放弃所有未处理的输入。  
        }
        return bos.toByteArray();
    }

    /**
     * 解压
     *
     * @param bs
     * @return byte[]
     * @throws java.util.zip.DataFormatException
     */
    public static byte[] inflate(byte[] bs) throws DataFormatException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bs.length);
        java.util.zip.Inflater compressor = new java.util.zip.Inflater();
        try {
            compressor.setInput(bs);
            final byte[] buf = new byte[1024];
            while (!compressor.finished()) {
                int count = compressor.inflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            compressor.end(); //关闭解压缩器并放弃所有未处理的输入。  
        }
        return bos.toByteArray();
    }
}
