package com.surfs.nas.transport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    public static final int threadnum = 3000;
    public static final ExecutorService pool = Executors.newFixedThreadPool(threadnum);

    public static void stopThread(Thread t) {
        if (t != null) {
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void shutdown() {
        pool.shutdown();
    }
}
