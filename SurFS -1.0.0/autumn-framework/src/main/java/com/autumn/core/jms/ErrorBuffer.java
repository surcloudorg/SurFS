package com.autumn.core.jms;

import com.autumn.core.cfg.Config;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/**
 * <p>Title: 失败数据的磁盘缓存</p>
 *
 * <p>Description: 仅缓存处理失败的数据。</p>
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
public class ErrorBuffer<V> {

    private String bufferName = ""; //缓存名，进程里只能有一个该名称的实例
    private long fileMaxSize = Buffer.MAX_FILE_SIZE; //缓存文件最大长度（字节数），超过换文件
    private String bufPath = null; //缓存文件路径
    private long blocksize = Buffer.MAX_BLOCK_SIZE;
    protected Config cfg = null;
    protected BufWriter<MessageWrapper<V>> writer = null;
    protected ErrorMessageReader<V> reader = null;
    protected RetryHandler<V> handler = null;
    private int threadnum = 5;//允许并发重试的线程数
    protected Logger log = LogFactory.getLogger(ErrorBuffer.class);

    public ErrorBuffer(String bufferName, RetryHandler<V> handler, int threadnum) throws Exception {
        this(bufferName, handler, threadnum, Buffer.MAX_FILE_SIZE, Buffer.MAX_BLOCK_SIZE);
    }

    /**
     * 初始化
     *
     * @param bufferName String 缓存名
     * @param fileMaxSize long 缓存文件最大尺寸
     * @param blocksize long 数据最大长度byte
     * @throws Exception 配置错误，缓存文件打开写入读取错误抛出
     */
    public ErrorBuffer(String bufferName, RetryHandler<V> handler, int threadnum, long fileMaxSize, long blocksize) throws Exception {
        if (handler == null) {
            throw new Exception("没有指定业务处理类！");
        }
        this.handler = handler;
        this.threadnum = threadnum > 100 ? 100 : (threadnum < 1 ? 1 : threadnum);
        this.blocksize = blocksize > Buffer.MAX_BLOCK_SIZE ? Buffer.MAX_BLOCK_SIZE : (blocksize < 10 ? 10 : blocksize);
        this.fileMaxSize = fileMaxSize < 1024 * 1024 ? 1024 * 1024 : (fileMaxSize > Buffer.MAX_FILE_SIZE ? Buffer.MAX_FILE_SIZE : fileMaxSize);
        if (bufferName == null || bufferName.trim().isEmpty()) {
            throw new Exception("缓存名为空");
        } else {
            this.bufferName = bufferName.trim().toLowerCase();
        }
        bufPath = Buffer.initBufferPath();
        lock(this.bufPath, this.bufferName);
        cfg = Buffer.getConfig(this.bufPath, this.bufferName);
        writer = new BufWriter<MessageWrapper<V>>(cfg, bufPath, this.bufferName, this.fileMaxSize, this.blocksize);
        reader = new ErrorMessageReader<V>(this);
        reader.start();
    }
    private RandomAccessFile filelock = null;
    private FileLock lock = null;

    /**
     * 加锁
     *
     * @param path
     * @param filename
     * @throws Exception
     */
    private void lock(String path, String filename) throws Exception {
        File f = new File(path + filename + ".lock");
        filelock = new RandomAccessFile(f, "rws");
        lock = filelock.getChannel().tryLock();
        if (lock == null) {
            throw new Exception("已经创建了名称为" + filename + "的缓存实例！");
        }
    }

    /**
     * 解锁
     *
     * @param raf
     * @throws IOException
     */
    private void unlock() {
        try {
            lock.release();
        } catch (IOException ex) {
        }
        try {
            filelock.close();
        } catch (IOException ex) {
        }
    }

    /**
     * 关闭线程
     */
    public void close() {
        if (reader != null) {
            reader.close();
        }
        writer.close();
        unlock();
    }

    /**
     * 处理数据
     *
     * @param data
     * @return 是否执行成功
     * @throws IOException
     */
    public boolean doMessage(V data) throws IOException {
        if (data == null) {
            return true;
        }
        MessageWrapper<V> em = new MessageWrapper<V>(data);
        try {
            handler.doMessage(em);
            return true;
        } catch (Throwable ex) {
            log.trace("执行doMessage(" + data.toString() + ")失败!", ex, handler.getClass());
            em.lastHT = System.currentTimeMillis();
            writer.write(em);
            return false;
        }
    }

    /**
     * 处理数据
     *
     * @param em
     * @return 是否执行成功
     * @throws IOException
     */
    public boolean doMessage(MessageWrapper<V> em) throws IOException {
        if (em == null || em.getMessage() == null) {
            return true;
        }
        try {
            handler.doMessage(em);
            return true;
        } catch (Throwable ex) {
            log.trace("执行doMessage(" + em.getMessage() + ")失败!", ex, handler.getClass());
            em.lastHT = System.currentTimeMillis();
            writer.write(em);
            return false;
        }
    }

    /**
     * @return the handler
     */
    public RetryHandler<V> getHandler() {
        return handler;
    }

    /**
     * @return the bufferName
     */
    public String getBufferName() {
        return bufferName;
    }

    /**
     * @return the fileMaxSize
     */
    public long getFileMaxSize() {
        return fileMaxSize;
    }

    /**
     * @return the bufPath
     */
    public String getBufPath() {
        return bufPath;
    }

    /**
     * @return the blocksize
     */
    public long getBlocksize() {
        return blocksize;
    }

    /**
     * @return the threadnum
     */
    public int getThreadnum() {
        return threadnum;
    }
}
