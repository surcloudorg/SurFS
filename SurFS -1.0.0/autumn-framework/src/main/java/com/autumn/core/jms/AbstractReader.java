package com.autumn.core.jms;

import com.autumn.core.cfg.Config;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
/**
 * <p>Title: 从磁盘读出缓存</p>
 *
 * <p>Description: 从磁盘读出缓存</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public abstract class AbstractReader<V> extends Thread {

    protected DataInputStream reader = null;//文件光标
    protected long position = 0; //读取的起始光标
    protected Config cfg = null;
    protected String bufPath = null;
    protected String bufName = null;

    /**
     * 由ErrorBuffer创建
     *
     * @param smbuffer
     * @throws IOException
     */
    protected AbstractReader(ErrorBuffer<V> smbuffer) throws IOException {
        this.cfg = smbuffer.cfg;
        this.bufPath = smbuffer.getBufPath();
        this.bufName = smbuffer.getBufferName();
        this.position = cfg.getAttributeLongValue("cfg.pos", position);
        this.setName("ErrorBufferReader-"+smbuffer.getBufferName());
        openfileread();
    }

    /**
     * 由Buffer创建
     *
     * @param smbuffer
     * @throws IOException
     */
    protected AbstractReader(Buffer<V> smbuffer) throws IOException {
        this.cfg = smbuffer.cfg;
        this.bufPath = smbuffer.getBufPath();
        this.bufName = smbuffer.getBufferName();
        this.position = cfg.getAttributeLongValue("cfg.pos", position);
        this.setName("BufferReader-"+smbuffer.getBufferName());
        openfileread();
    }

    /**
     * 打开读文件
     *
     * @throws IOException
     */
    private void openfileread() throws IOException {
        stopRead();
        String filename = cfg.getAttributeValue("cfg.rfn");
        File file = new File(bufPath + filename);
        reader = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        movepos();
    }

    /**
     * 移动到最后一次读取的行
     *
     * @throws IOException
     */
    private void movepos() throws IOException {
        if (position == 0) {
            return;
        }
        try {
            reader.skip(position);
        } catch (Exception r) {
            position = 0;
        }
        try {
            cfg.setAttributeValue("cfg.pos", Long.toString(position));
        } catch (Exception ex) {
        }
    }

    /**
     * 更改读文件
     *
     * @param line String
     */
    protected void changReadFile(String line) throws IOException {
        String curfilename = cfg.getAttributeValue("cfg.rfn");
        stopRead();
        File f = new File(bufPath + curfilename);
        if (f.exists()) {//删除读取完毕的文件
            f.delete();
        }
        String filename = line;
        saveAttributeValue("cfg.rfn", filename);
        position = 0;
        saveAttributeValue("cfg.pos", Long.toString(position));
        saveAttributes();
        openfileread();
    }

    /**
     * 存储配置到文件
     */
    protected void saveAttributes() {
        try {
            if (cfg.isChanged()) {
                cfg.save(bufPath + bufName + ".cfg");
            }
        } catch (Exception e) {
        }
    }

    /**
     * 更新参数值
     *
     * @param key String
     * @param value String
     */
    protected void saveAttributeValue(String key, String value) {
        try {
            cfg.setAttributeValue(key, value);
        } catch (Exception e) {
        }
    }

    /**
     * 退出
     */
    protected void close() {
        this.interrupt();
        try {
            this.join(5000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 关闭文件
     */
    protected void stopRead() {
        if (reader != null) {
            try {
                reader.close();
                reader = null;
            } catch (Exception e) {
            }
        }
    }
}
