package com.autumn.core.jms;

import com.autumn.core.cfg.Config;
import com.autumn.util.IOUtils;
import java.io.*;

/**
 * <p>Title: 缓存写入磁盘</p>
 *
 * <p>Description: 缓存超过指定行，写入磁盘，空闲时读出。</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 * @param <V>
 *
 */
public class BufWriter<V> {

    private DataOutputStream writer = null;//打开文件
    private long filesize = 0;//当前文件大小
    private Config cfg = null;
    private String bufPath = null; //缓存文件路径
    private String bufferName = ""; //缓存名，进程里只能有一个该名称的实例
    private long fileMaxSize = Buffer.MAX_FILE_SIZE; //缓存文件最大长度（字节数），超过换文件
    private long blocksize = Buffer.MAX_BLOCK_SIZE;

    /**
     * 由Buffer创建
     *
     * @param cfg
     * @param bufPath
     * @param bufferName
     * @param fileMaxSize
     * @param blocksize
     * @throws IOException
     */
    protected BufWriter(Config cfg, String bufPath, String bufferName, long fileMaxSize, long blocksize) throws IOException {
        this.cfg = cfg;
        this.bufPath = bufPath;
        this.bufferName = bufferName;
        this.fileMaxSize = fileMaxSize;
        this.blocksize = blocksize;
        openfilewrite();
    }

    /**
     * 关闭文件
     */
    protected void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 打开文件,准备写入
     *
     * @throws IOException
     */
    private void openfilewrite() throws IOException {
        close();
        String filename = cfg.getAttributeValue("cfg.wfn");
        File file = new File(bufPath + filename);
        if (file.exists()) {
            writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file, true)));
            filesize = file.length();
        } else {
            file.createNewFile();
            writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        }
    }

    /**
     * 更改写文件
     */
    private void changWriteFile() throws IOException {
        String curfilename = cfg.getAttributeValue("cfg.wfn").toLowerCase();
        curfilename = curfilename.replaceAll(bufferName, "");
        curfilename = curfilename.replaceAll(".dat", "");
        int ii = 0;
        try {
            ii = Integer.parseInt(curfilename) + 1;
        } catch (Exception e) {
        }
        curfilename = bufferName + ii + ".dat";
        File f = new File(bufPath + curfilename);
        if (f.exists()) {
            f.delete();
        }
        writer.writeByte(Buffer.EOF_TYPE);
        byte[] data = curfilename.getBytes("utf-8");
        writer.writeLong(data.length);
        writer.write(data);
        writer.flush();
        try {
            cfg.setAttributeValue("cfg.wfn", curfilename);
            cfg.save(bufPath + bufferName + ".cfg");
        } catch (Exception ex) {
        }
        openfilewrite();
    }

    /**
     * 写入
     *
     * @param line
     * @throws IOException
     */
    protected synchronized void write(V line) throws IOException {
        if (line == null) {
            return;
        }
        if (filesize >= fileMaxSize) {  //检查是否文件超长
            changWriteFile();//需要换文件
            filesize = 0;
        }
        byte[] data;
        if (line instanceof String) {
            data = line.toString().getBytes("utf-8");
        } else {//执行序列化
            data = IOUtils.objectToBytes(line);
        }
        if (data.length > blocksize) {
            throw new IOException("数据长度超过设定值：" + blocksize);
        }
        if (line instanceof String) {
            writer.writeByte(Buffer.STRING_TYPE);
        } else {
            writer.writeByte(Buffer.OBJECT_TYPE);
        }
        writer.writeLong(data.length);
        filesize = filesize + data.length + 9;
        writer.write(data);
        writer.flush();
    }
}
