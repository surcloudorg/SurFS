package com.autumn.core.jms;

import com.autumn.core.cfg.Config;
import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jdom.JDOMException;

/**
 * <p>Title: 磁盘缓存</p>
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
public class Buffer<V> {

    public static final long MAX_BLOCK_SIZE = 1024 * 1024 * 5;//数据最大不能超5M
    public static final long MAX_FILE_SIZE = 1024 * 1024 * 32;//数据最大不能超32M
    public static final byte STRING_TYPE = 1;//String
    public static final byte OBJECT_TYPE = 2;//Object
    public static final byte EOF_TYPE = 0;//缓存文件末尾标志
    private String bufferName = ""; //缓存名，进程里只能有一个该名称的实例
    private int memMaxLine = 1000; //内存最大行数，初始化后不能更改
    private long fileMaxSize = MAX_FILE_SIZE; //缓存文件最大长度（字节数），超过换文件
    private String bufPath = null; //缓存文件路径
    private BlockingQueue<V> queue = null; //内存队列
    private BufWriter<V> writer = null;
    private BufReader<V> reader = null;
    private long blocksize = MAX_BLOCK_SIZE;
    protected Config cfg = null;
    protected Logger log = LogFactory.getLogger(Buffer.class);

    /**
     * 内存队列是否为空
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * 内存队列中的空位
     *
     * @return int
     */
    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    /**
     * 当前内存队列大小
     *
     * @return int
     */
    public int size() {
        return queue.size();
    }

    /**
     * 阻塞
     *
     * @param ss String
     * @throws IOException
     * @throws InterruptedException
     */
    public void put(V ss) throws InterruptedException {
        if (ss != null) {
            queue.put(ss);
        }
    }

    /**
     * 阻塞
     *
     * @param ss
     * @param timeout
     * @throws InterruptedException
     */
    public void offer(V ss, long timeout) throws IOException {
        if (ss != null) {
            try {
                boolean b = queue.offer(ss, timeout, TimeUnit.MILLISECONDS);
                if (!b) {
                    writer.write(ss);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                writer.write(ss);
            }
        }
    }

    /**
     * 加入缓存,如果内存队列满，立刻写入磁盘
     *
     * @param ss String
     * @throws IOException 致命错误需要报警
     */
    public void add(V ss) throws IOException {
        if (ss != null) {
            if (!queue.offer(ss)) {
                writer.write(ss);
            }
        }
    }

    /**
     * 从缓存中读取
     *
     * @return String
     * @throws Exception 致命错误需要报警
     */
    public V take() throws IOException, InterruptedException {
        if (reader == null || (!reader.isAlive())) {
            throw new IOException("读取缓存文件线程已终止");
        }
        return queue.take();
    }

    /**
     * 从缓存中读取
     *
     * @return String
     * @throws Exception 致命错误需要报警
     */
    public V poll(long timeout) throws IOException {
        if (reader == null || (!reader.isAlive())) {
            throw new IOException("读取缓存文件线程已终止");
        }
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * 从缓存中读取
     *
     * @return String
     * @throws Exception 致命错误需要报警
     */
    public V poll() throws IOException {
        if (reader == null || (!reader.isAlive())) {
            throw new IOException("读取缓存文件线程已终止");
        }
        return queue.poll();
    }

    /**
     * 将内存队列数据写入文件,记录最后读取行指针
     */
    public void close() {
        if (reader != null) {
            reader.close();
        }
        while (!queue.isEmpty()) {
            V ss = queue.poll();
            try {
                writer.write(ss);
            } catch (Exception e) {
            }
        }
        writer.close();
        unlock();
    }

    /**
     * 初始化
     *
     * @param BufferName String 缓存名
     * @throws Exception 配置错误，缓存文件打开写入读取错误抛出
     */
    public Buffer(String BufferName) throws Exception {
        this(BufferName, 1000, 1024 * 1024 * 512, MAX_BLOCK_SIZE);
    }

    /**
     * 初始化
     *
     * @param BufferName String 缓存名
     * @param maxLine int 内存队列最大行数
     * @throws Exception 配置错误，缓存文件打开写入读取错误抛出
     */
    public Buffer(String BufferName, int maxLine) throws Exception {
        this(BufferName, maxLine, 1024 * 1024 * 512, MAX_BLOCK_SIZE);
    }

    /**
     * 初始化
     *
     * @param bufferName String 缓存名
     * @param memMaxLine int 内存队列最大行数
     * @param fileMaxSize long 缓存文件最大尺寸
     * @param blocksize long 数据最大长度byte
     * @throws Exception 配置错误，缓存文件打开写入读取错误抛出
     */
    public Buffer(String bufferName, int memMaxLine, long fileMaxSize, long blocksize) throws Exception {
        this.blocksize = blocksize > MAX_BLOCK_SIZE ? MAX_BLOCK_SIZE : (blocksize < 10 ? 10 : blocksize);
        this.memMaxLine = memMaxLine > 100000 ? 100000 : (memMaxLine < 10 ? 10 : memMaxLine);//（10-100000）太大不应该
        queue = new ArrayBlockingQueue<V>(this.memMaxLine);
        this.fileMaxSize = fileMaxSize < 1024 * 1024 ? 1024 * 1024 : (fileMaxSize > MAX_FILE_SIZE ? MAX_FILE_SIZE : fileMaxSize);
        if (bufferName == null || bufferName.trim().isEmpty()) {
            throw new Exception("缓存名为空");
        } else {
            this.bufferName = bufferName.trim().toLowerCase();
        }
        bufPath = initBufferPath();
        lock(this.bufPath, this.bufferName);
        cfg = getConfig(this.bufPath, this.bufferName);
        writer = new BufWriter<V>(cfg, bufPath, this.bufferName, this.fileMaxSize, this.blocksize);
        reader = new BufReader<V>(this);
        reader.start();
    }

    /**
     * 初始化缓存文件路径
     *
     * @return String
     */
    protected static String initBufferPath() {
        String usepath = System.getProperty("user.dir");
        usepath = usepath == null ? "" : usepath;
        String bufPath = usepath.endsWith(File.separator) ? usepath + "buffer" : usepath + File.separator + "buffer";
        File path = new File(bufPath);
        if (!path.exists()) {
            path.mkdirs(); //创建
        }
        return bufPath + File.separator;
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
     * 创建配置
     *
     * @param path
     * @param filename
     * @return Config
     * @throws Exception
     */
    protected static Config getConfig(String path, String filename) throws Exception {
        File cfgfile = new File(path + filename + ".cfg");
        if (cfgfile.exists()) {
            try {
                return new Config(new FileInputStream(cfgfile));
            } catch (JDOMException je) {
                LogFactory.error("缓存配置文件已损坏,重建！", Buffer.class);
                cfgfile.delete();
                (new File(filename + "0.dat")).delete();
            }
        }
        Config cfg = new Config(getDefaultConfig());//读取配置文件，创建Config
        cfg.setAttributeValue("cfg.wfn", filename + "0.dat");
        cfg.setAttributeValue("cfg.rfn", filename + "0.dat");
        cfg.setAttributeValue("cfg.pos", "0");
        cfg.save(new FileOutputStream(cfgfile));
        return cfg;
    }

    /**
     * 配置文件格式
     *
     * @return String
     */
    protected static String getDefaultConfig() {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<cfg><rfn/><wfn/><pos/></cfg>");
        return sb.toString();
    }

    /**
     * @return the bufPath
     */
    public String getBufPath() {
        return bufPath;
    }

    /**
     * @return the fileMaxSize
     */
    public long getFileMaxSize() {
        return fileMaxSize;
    }

    /**
     * @return the memMaxLine
     */
    public int getMemMaxLine() {
        return memMaxLine;
    }

    /**
     * @return the bufferName
     */
    public String getBufferName() {
        return bufferName;
    }
}
