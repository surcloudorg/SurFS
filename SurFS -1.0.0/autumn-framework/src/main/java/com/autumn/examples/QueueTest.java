package com.autumn.examples;

import com.autumn.core.jms.Buffer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title: 测试Buffer</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class QueueTest {

    public static void main(String[] args) throws Exception {
        if (true) {
            return;
        }       
        Buffer<String> buf = new Buffer<String>("buffertest", 100, 1024 * 1024,1024 * 1024);
        Writer w = new Writer(buf);
        w.start();
        w = new Writer(buf);
        w.start();
        w = new Writer(buf);
        w.start();
        w = new Writer(buf);
        w.start();
        w = new Writer(buf);
        w.start();
        Reader r = new Reader(buf);
        r.start();
        r = new Reader(buf);
        r.start();
        r = new Reader(buf);
        r.start();
        r = new Reader(buf);
        r.start();
        r = new Reader(buf);
        r.start();

    }

    static class Writer extends Thread {

        Buffer<String> buf = null;

        Writer(Buffer<String> buf) {
            this.buf = buf;
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    buf.add(System.currentTimeMillis() + "警告：sun.management.ManagementFactory 是 Sun 的专用 API，可能会在未来版本中删除");
                    sleep(10);
                } catch (InterruptedException ex) {
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(QueueTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    static class Reader extends Thread {

        Buffer<String> buf = null;

        Reader(Buffer<String> buf) {
            this.buf = buf;
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    String s = buf.take();
                    System.out.println(s);
                    sleep(1000);
                } catch (InterruptedException ex) {
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(QueueTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
