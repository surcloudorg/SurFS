package com.surfs.nas.util;

import com.surfs.nas.log.Logger;
import com.surfs.nas.transport.ThreadPool;
import java.io.*;
import java.util.concurrent.Callable;

public class ConsoleCommand implements Callable {

    private String[] commands = null;
    private String[] inputStrings = null;
    private Process p = null;
    private StringBuilder outputText = new StringBuilder();
    private StringBuilder errorText = new StringBuilder();
    private Logger log = null;
    private Thread thread = null;

    public ConsoleCommand(String command, String[] inputStrings) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }
        this.commands = new String[1];
        commands[0] = command;
        this.inputStrings = inputStrings;
    }

    public ConsoleCommand(String command) {
        this(command, null);
    }

    public ConsoleCommand(String[] commands) {
        this.commands = commands;
    }

    public ConsoleCommand(String[] commands, String[] inputStrings) {
        this.commands = commands;
        this.inputStrings = inputStrings;
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
        if (p != null) {
            p.destroy();
        }

    }

    /**
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
            ThreadPool.pool.execute(new InputStreamGobbler(p.getInputStream(),
                    outputText, false));
            ThreadPool.pool.execute(new InputStreamGobbler(p.getErrorStream(),
                    errorText, true));
            if (inputStrings != null) {
                ThreadPool.pool.execute(new OutputStreamGobbler(inputStrings, p.getOutputStream()));
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
