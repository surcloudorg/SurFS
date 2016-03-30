/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import com.surfs.nas.protocol.WriteRequest;
import com.surfs.nas.transport.ThreadPool;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class AsynchWriter {

    private final NasRandomAccessor acccessor;
    private final BlockingQueue<Writer> futures;

    public AsynchWriter(NasRandomAccessor acccessor, int queue) {
        this.acccessor = acccessor;
        this.futures = new LinkedBlockingQueue<>(queue);
    }

    /**
     *
     * @param tr
     * @throws IOException
     */
    public void write(WriteRequest tr) throws IOException {
        synchronized (this) {
            Writer writer = new Writer(tr);
            try {
                futures.put(writer);
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
            writer.future = ThreadPool.pool.submit(writer);
        }
    }

    /**
     *
     * @throws java.io.IOException
     */
    public void join() throws IOException {
        synchronized (this) {
            while (!futures.isEmpty()) {
                List<Writer> writers = new ArrayList<>(futures);
                for (Writer writer : writers) {
                    try {
                        writer.future.get();
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new IOException(ex);
                    }
                }
            }
        }
    }

    private class Writer implements Callable {

        private final WriteRequest request;
        private Future future = null;

        private Writer(WriteRequest request) {
            this.request = request;
        }

        @Override

        public Void call() {
            try {
                acccessor.synchWrite(request, 0);
            } catch (Exception e) {
            }
            futures.remove(this);
            return null;
        }
    }
}
