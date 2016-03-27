/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.util;

import com.autumn.core.ThreadPools;
import com.autumn.core.log.Logger;
import java.io.*;
import java.util.concurrent.Callable;

/**
 * <p>
 * Title: 通过它启动外部进程</p>
 *
 * <p>
 * Description: 可以设置系统输入,并获取到输出</p>
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
public class ConsoleCommand implements Callable {

    private String[] commands = null; //命令行
    private String[] inputStrings = null; //输入响应
    private Process p = null; //进程
    private StringBuilder outputText = new StringBuilder(); //输出文本
    private StringBuilder errorText = new StringBuilder(); //执行错误,输出文本
    private Logger log = null;
    private Thread thread = null;

    /**
     * 构造
     *
     * @param command String 启动进程命令
     * @param inputStrings String[] 输入参数
     */
    public ConsoleCommand(String command, String[] inputStrings) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }
        this.commands = new String[1];
        commands[0] = command;
        this.inputStrings = inputStrings;
    }

    /**
     * 构造
     *
     * @param command String 启动进程命令
     */
    public ConsoleCommand(String command) {
        this(command, null);
    }

    /**
     * 构造
     *
     * @param commands String[] 启动进程命令
     */
    public ConsoleCommand(String[] commands) {
        this.commands = commands;
    }

    /**
     * 构造
     *
     * @param commands String[] 启动进程命令
     * @param inputStrings String[] 输入参数
     */
    public ConsoleCommand(String[] commands, String[] inputStrings) {
        this.commands = commands;
        this.inputStrings = inputStrings;
    }

    /**
     * 终止进程
     */
    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
        if (p != null) {
            p.destroy();
        }

    }

    /**
     * 启动进程
     *
     * @return ConsoleCommand
     * @throws Exception
     */
    @Override
    public Integer call() throws Exception {
        Exception exception = null;
        if (commands == null) {
            return -1;
        }
        thread = Thread.currentThread();
        try {
            if (commands.length == 1) {
                p = Runtime.getRuntime().exec(commands[0]);
            } else {
                p = Runtime.getRuntime().exec(commands);
            }
            ThreadPools.startThread(new InputStreamGobbler(p.getInputStream(),
                    outputText, false));
            ThreadPools.startThread(new InputStreamGobbler(p.getErrorStream(),
                    errorText, true));
            if (inputStrings != null) {
                ThreadPools.startThread(new OutputStreamGobbler(inputStrings, p.getOutputStream()));
            }
            p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            exception = e;
        } catch (IOException e) {
            exception = e;
        } finally {
            try {
                p.getInputStream().close();
            } catch (Exception r) {
            }
            try {
                p.getErrorStream().close();
            } catch (Exception r) {
            }
            try {
                p.getOutputStream().close();
            } catch (Exception r) {
            }
        }
        try {
            return p.exitValue();
        } catch (Exception r) {
            p.destroy();
        }
        if (exception != null) {
            throw exception;
        }
        return -1;
    }

    /**
     * 获取错误输出
     *
     * @return String
     */
    public String getErrorText() {
        String txt = errorText.toString();
        if (txt.equals("")) {
            return null;
        }
        return txt;
    }

    /**
     * 获取输出(执行结果)
     *
     * @return String
     */
    public String getOutputText() {
        String txt = outputText.toString();
        if (txt.equals("")) {
            return null;
        }
        return txt;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     *
     * <p>
     * Title: 输出流</p>
     *
     * <p>
     * Description: 将参数传递给进程</p>
     *
     * <p>
     * Copyright: BeiJing HongXun Copyright (c) 2007</p>
     *
     * <p>
     * Company: 北京鸿讯</p>
     *
     * @author 刘社朋
     * @version 2.0
     */
    private class OutputStreamGobbler implements Runnable {

        private String[] inputStrings = null;
        private OutputStream os = null;

        public OutputStreamGobbler(String[] inputStrings, OutputStream os) {
            this.os = os;
            this.inputStrings = inputStrings;
        }

        @Override
        public void run() {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(os)), true);
            int count = inputStrings.length;
            for (int ii = 0; ii < count; ii++) {
                try {
                    writer.println(inputStrings[ii]);
                    writer.flush();
                } catch (Exception ioe) {
                }
            }
        }
    }

    /**
     *
     * <p>
     * Title: 输入流</p>
     *
     * <p>
     * Description: 从进程获取到执行结果或错误信息</p>
     *
     * <p>
     * Copyright: BeiJing HongXun Copyright (c) 2007</p>
     *
     * <p>
     * Company: 北京鸿讯</p>
     *
     * @author 刘社朋
     * @version 2.0
     */
    private class InputStreamGobbler implements Runnable {

        private InputStream is = null;
        private StringBuilder outText = null;
        private boolean errout = false;

        public InputStreamGobbler(InputStream is, StringBuilder outText, boolean errout) {
            this.is = is;
            this.outText = outText;
            this.errout = errout;
        }

        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    if (line.equals("")) {
                        continue;
                    }
                    if (log == null) {
                        outText.append(line);
                        outText.append("\r\n");
                    } else {
                        if (errout) {
                            log.error(line);
                        } else {
                            log.info(line);
                        }
                    }
                }
            } catch (IOException ioe) {
            }
        }
    }
}
