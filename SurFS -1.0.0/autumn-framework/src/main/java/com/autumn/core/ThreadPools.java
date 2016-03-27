/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Title: 线程池</p>
 *
 * <p>
 * Description: 线程池</p>
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
public class ThreadPools {

    private static ThreadPools me = null; //创建线程池

    /**
     * 初始化
     */
    public synchronized static void init() {
        if (me == null) {
            me = new ThreadPools();
        }
    }

    /**
     * 启动线程
     *
     * @param obj Runnable
     * @return
     */
    public static Future startThread(Runnable obj) {
        init();
        return me.submit(obj);
    }

    /**
     * 关闭线程池
     */
    public static synchronized void shutdownThreadpools() {
        if (me != null) {
            me.shutdown();
            me = null;
        }
    }

    /**
     * @return the pool
     */
    public static ExecutorService getThreadPools() {
        init();
        return me.pool;
    }

    /**
     * 启动线程
     *
     * @param mycall Callable
     * @return Future
     */
    @SuppressWarnings("unchecked")
    public static Future startThread(Callable mycall) {
        init();
        try {
            Future future = me.submit(mycall);
            return future;
        } catch (Exception e) {
            return null;
        }
    }
    private ExecutorService pool = null;//线程池
    private final Logger log = LogFactory.getLogger(ThreadPools.class);

    public ThreadPools() {
        pool = Executors.newCachedThreadPool();
        log.info("创建线程池!");
    }

    public ThreadPools(int num) {
        pool = Executors.newFixedThreadPool(num);
        log.info("创建线程池，大小：".concat(String.valueOf(num)));
    }

    /**
     * 关闭线程池
     */
    public synchronized void shutdown() {
        if (pool != null) {
            pool.shutdown();
            try {
                pool.awaitTermination(3, TimeUnit.MINUTES);
            } catch (InterruptedException d) {
                Thread.currentThread().interrupt();
            }
            pool = null;
            log.info("线程池安全关闭！");
        }
    }

    /**
     * 启动线程
     *
     * @param mycall Callable
     * @return Future
     */
    public synchronized Future submit(Runnable mycall) {
        try {
            Future future = pool.submit(mycall);
            return future;
        } catch (Exception e) {
            log.error("启动线程错误:" + e.getMessage());
            return null;
        }
    }

    /**
     * 启动线程
     *
     * @param mycall Callable
     * @return Future
     */
    @SuppressWarnings("unchecked")
    public synchronized Future submit(Callable mycall) {
        try {
            Future future = pool.submit(mycall);
            return future;
        } catch (Exception e) {
            log.error("启动线程错误:" + e.getMessage());
            return null;
        }
    }

    /**
     * 关闭线程
     *
     * @param t 需要关闭的线程
     */
    public static void stopThread(Thread t) {
        stopThread(t, 0);
    }

    /**
     * 关闭线程
     *
     * @param t 需要关闭的线程
     * @param w 等待关闭
     */
    public static void stopThread(Thread t, long w) {
        if (t != null) {
            t.interrupt();
            try {
                if (w > 0) {
                    t.join(w);
                } else {
                    t.join();
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
