package com.autumn.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;

/**
 * <p>Title: 创建服务运行锁</p>
 *
 * <p>Description: 检测磁盘文件是否输入exit指令，检测到执行退出接口实现</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class JVMController {

    private static CheckLockFile check = null;

    /**
     * 注册系统退出接口实现
     * @throws java.lang.Exception
     */
    public static void start() throws Exception {
        if (check == null) {
            check = new CheckLockFile();
            check.start();
        }
    }

    /**
     * 注册系统退出接口实现
     *
     * @param alistener
     * @throws java.lang.Exception
     */
    public static void registry(JVMListener alistener) throws Exception {
        if (check == null) {
            check = new CheckLockFile(alistener);
            check.start();
        } else {
            check.setListener(alistener);
        }
    }

    /**
     * 取消系统退出接口监听
     */
    public static void stop() {
        ThreadPools.stopThread(check);
    }

    /**
     * 向文件写入（exit），等待返回退出
     *
     */
    public static void exit() {
        String filename = getFileName();
        File f = new File(filename);
        File fl = new File(f.getParent(), "lock");
        if (f.exists()) {
            try {
                FileWriter logWriter = new FileWriter(f);
                logWriter.write("exit");
                logWriter.flush();
                logWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            System.out.print("等待关闭...");
            LineNumberReader linereader;
            try {
                int count = 0;
                for (;;) {
                    linereader = new LineNumberReader(new FileReader(f));
                    String line = linereader.readLine();
                    linereader.close();
                    if (line != null && line.equalsIgnoreCase("The service closed successfully!")) {
                        System.out.println();
                        System.out.println("服务被安全关闭！");
                        f.delete();
                        fl.delete();
                        return;
                    }
                    for (int ii = 0; ii < 10; ii++) {
                        Thread.sleep(100);
                        System.out.print(".");
                    }
                    count++;
                    if (count > 60) {
                        System.out.println();
                        System.out.println("服务不能正常关闭,按任意键退出！");
                        System.in.read();
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("没有发现文件:" + filename);
        }
    }

    /**
     * 退出
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        exit();
    }

    /**
     * 获取进程锁文件名
     *
     * @return String
     */
    public static String getFileName() {
        String userDir = System.getProperty("user.dir");
        if (userDir == null) {
            userDir = "";
        }
        if (userDir.endsWith(File.separator)) {
            return userDir + "exitsign";
        } else {
            return userDir + File.separator + "exitsign";
        }
    }
}
