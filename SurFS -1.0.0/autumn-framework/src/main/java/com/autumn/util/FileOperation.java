package com.autumn.util;

import com.autumn.core.web.Initializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>Title: 文件操作函数</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class FileOperation {

    /**
     * 在指定路径下查找文件
     *
     * @param path String 文件夹路径及名称 如c:/fqf
     * @param fileName String 文件名 如config.ini
     * @return String 文件详细路径
     */
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
        File temp = null;
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
     * 查找文件
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

    /**
     * 从文件读出byte[]
     *
     * @param filename
     * @return byte[]
     * @throws IOException
     */
    public static byte[] readFile(String filename) throws IOException {
        return IOUtils.read(new FileInputStream(filename));
    }

    /**
     * 将byte写入文件
     *
     * @param output byte[]
     * @param filename String
     * @throws IOException
     */
    public synchronized static void writeFile(byte[] output, String filename) throws IOException {
        FileOutputStream out = new FileOutputStream(new java.io.File(filename), false);
        IOUtils.write(output, out);
    }

    /**
     * 判断文件字符集
     *
     * @param file File
     * @return String
     */
    public static String getCharset(File file) {
        String charset = "GBK";
        try {
            return IOUtils.getCharset(new FileInputStream(file));
        } catch (Exception e) {
        }
        return charset;
    }

    /**
     * 删除文件,包括子目录
     *
     * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
     */
    public static void delFile(String filePathAndName) {
        File file = new File(filePathAndName);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] tempList = file.listFiles();
            for (File temp : tempList) {
                delFile(temp.getAbsolutePath());
            }
        }
        file.delete();
    }
}
