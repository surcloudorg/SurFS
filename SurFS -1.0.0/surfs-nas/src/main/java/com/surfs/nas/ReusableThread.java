/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

import com.surfs.nas.transport.ThreadPool;

public abstract class ReusableThread extends Thread {

    private final Object lock = new Object();
    private long errWaitTime = 15000;

    public ReusableThread(long errWaitTime) {
        this(errWaitTime, null);
    }

    public ReusableThread(long errWaitTime, String threadName) {
        this.errWaitTime = errWaitTime;
        this.setDaemon(true);
        this.setName(threadName == null ? this.getClass().getSimpleName() : threadName);
    }

    public void restart() {
        if (!this.isAlive()) {
            ThreadPool.pool.execute(this);
        } else {
            if (this.getState() == State.WAITING) {
                synchronized (lock) {
                    lock.notify();
                }
            }
        }
    }

    public abstract void doTask() throws Throwable;

    protected boolean exiting = false;
    protected Thread currentThread = null;

    public void terminate() {
        exiting = true;
        if (currentThread != null) {
            currentThread.interrupt();
            try {
                currentThread.join();
            } catch (InterruptedException ex) {
            }
            currentThread = null;
        }
    }

    @Override
    public final void run() {
        currentThread = Thread.currentThread();
        while (!exiting) {
            try {
                doTask();
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                break;
            } catch (Throwable ex) {
                try {
                    sleep(errWaitTime);
                } catch (InterruptedException ex1) {
                    break;
                }
            }
        }
    }
}
