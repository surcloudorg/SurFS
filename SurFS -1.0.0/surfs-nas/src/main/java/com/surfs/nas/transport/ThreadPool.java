/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

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
