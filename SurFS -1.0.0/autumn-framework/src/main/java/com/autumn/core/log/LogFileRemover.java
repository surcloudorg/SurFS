package com.autumn.core.log;

import com.autumn.core.ThreadPools;
import com.autumn.util.TextUtils;
import com.autumn.util.zlib.Zipper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * <p>
 * Title: 定时清除日志文件</p>
 *
 * <p>
 * Description: 定时清除日志文件</p>
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
public class LogFileRemover extends Thread {

    static LogFileRemover me = null;

    /**
     * 启动
     *
     * @param distDirectory
     * @param remainDay
     * @throws Exception
     */
    public static synchronized void start(String distDirectory, int remainDay) throws Exception {
        if (me == null) {
            me = new LogFileRemover(distDirectory, remainDay);
            me.start();
        }
    }

    /**
     * 关闭
     */
    public static synchronized void terminate() {
        ThreadPools.stopThread(me);
        me = null;
    }
    private String distDirectory = null;//迁移目的地
    private int remainDay = 7;//保留7天日志

    LogFileRemover(String distDirectory, int remainDay) throws Exception {
        File f = new File(distDirectory);
        if (f.exists()) {
            if (!f.isDirectory()) {
                throw new Exception("目录无效:" + distDirectory);
            }
        } else {
            if (!f.mkdirs()) {
                throw new Exception("目录无效:" + distDirectory);
            }
        }
        this.distDirectory = f.getAbsolutePath();
        this.remainDay = remainDay;
        this.setDaemon(true);
        this.setName("LogFileRemover");
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            Date date = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * remainDay));
            String dayname = TextUtils.Date2String(date, "yyyyMMdd") + ".log";
            Collection<Logger> col = LogFactory.logHash.values();
            for (Logger log : col) {
                if (log.getLogPath() == null) {
                    continue;
                }
                if (this.isInterrupted()) {
                    break;
                }
                remove(log.getLogPath(), dayname);
            }
            try {
                sleep(1000 * 60 * 60 * 24);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    /**
     * 移动
     *
     * @param dir
     */
    private void remove(String dir, String dayname) {
        File f = new File(dir);
        if (!f.exists()) {
            return;
        }
        String[] fs = null;
        if (f.isDirectory()) {
            fs = f.list();
        } else {
            return;
        }
        Arrays.sort(fs);
        for (String fileName : fs) {
            File file = new File(f.getAbsolutePath(), fileName);
            if (file.getName().compareTo(dayname) >= 0) {
                return;
            }
            copy(file);
        }
    }

    /**
     * 拷贝
     *
     * @param f
     */
    private void copy(File f) {
        String newname = f.getName() + ".zip";
        String dirname = f.getParentFile().getName();
        File dir = new File(distDirectory, dirname);
        dir.mkdirs();
        File file = new File(dir, newname);
        try {
            Zipper.zip(file.getAbsolutePath(), f.getAbsolutePath());
            f.delete();
        } catch (IOException ex) {
            file.delete();
        }
    }
}
