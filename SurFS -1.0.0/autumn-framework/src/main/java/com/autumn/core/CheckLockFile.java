/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core;

import com.autumn.core.log.LogFactory;
import java.io.*;
import java.nio.channels.FileLock;

/**
 * <p>Title: 服务运行锁</p>
 *
 * <p>Description: 防止启动两个实例，检测磁盘文件是否输入exit指令</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class CheckLockFile extends Thread {

    private JVMListener listener = null;
    private File lockfile = null;
    private File ctrlfile = null;
    private LineNumberReader linereader = null;

    /**
     * 创建
     *
     * @throws Exception 启动第二个实例
     */
    protected CheckLockFile() throws Exception {
        String filename = JVMController.getFileName();
        ctrlfile = new File(filename);
        lockfile = new File(ctrlfile.getParent(), "lock");
        RandomAccessFile raf = new RandomAccessFile(lockfile, "rws");
        FileLock fl = raf.getChannel().tryLock();
        if (fl == null) {
            throw new Exception("系统已启动！");
        } 
        if (ctrlfile.exists() && !ctrlfile.delete()) {
            throw new Exception("系统无法启动，不能删除控制文件exitsign！");
        }
        if (!ctrlfile.createNewFile()) {
            throw new Exception("系统无法启动，不能创建控制文件exitsign！");
        }
    }

    /**
     * 创建
     *
     * @param alistener 关闭接口
     * @throws Exception
     */
    protected CheckLockFile(JVMListener alistener) throws Exception {
        this();
        this.listener = alistener;
    }

    /**
     * 打开文件
     *
     * @throws FileNotFoundException
     */
    private void openfile() throws IOException {
        FileInputStream fis = new FileInputStream(ctrlfile);
        Reader reader = new InputStreamReader(fis);
        linereader = new LineNumberReader(reader);
    }

    /**
     * 关闭文件
     */
    private void closefile() {
        try {
            if (linereader != null) {
                linereader.close();
            }
        } catch (Exception e) {
        }
        linereader = null;
    }

    @Override
    public void run() {
        this.setName(this.getClass().getName());
        String line;
        while (!this.isInterrupted()) {
            try {
                try {
                    if (linereader == null) {
                        openfile();
                    }
                } catch (Exception e) {
                    LogFactory.error("打开文件" + ctrlfile.getAbsolutePath() + "失败：" + e.getMessage(), CheckLockFile.class);
                    sleep(5000);
                    continue;
                }
                line = linereader.readLine();
                if (line == null) {
                    sleep(5000);
                    continue;
                }
                if (line.equals("exit")) {//监视到退出指令
                    break;
                }
            } catch (InterruptedException e) {
                return;
            } catch (IOException e) {
                closefile();
            }
        }
        writeMessage();
        System.exit(0);
    }

    /**
     * 向文件输出关闭信息！
     */
    private void writeMessage() {
        if (!this.isInterrupted()) {
            closefile();
            if (listener != null) {
                listener.systemDestroyed();
            }
            if (ctrlfile.exists()) {//写入退出完毕信息
                try {
                    FileWriter logWriter = new FileWriter(ctrlfile);
                    logWriter.write("The service closed successfully!");
                    logWriter.flush();
                    logWriter.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * @param listener the listener to set
     */
    protected void setListener(JVMListener listener) {
        this.listener = listener;
    }
}
