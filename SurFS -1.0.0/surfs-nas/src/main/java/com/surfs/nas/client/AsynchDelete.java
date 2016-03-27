/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import com.surfs.nas.NasMeta;
import com.surfs.nas.StoragePool;
import com.surfs.nas.protocol.DeleteRequest;
import com.surfs.nas.transport.TcpClient;
import com.surfs.nas.transport.ThreadPool;
import java.io.IOException;
import org.apache.log4j.Logger;

public class AsynchDelete implements Runnable {

    private static final Logger log = Logger.getLogger(AsynchDelete.class);
    private StoragePool pool;
    private NasMeta meta;

    /**
     *
     * @param pool
     * @param meta
     */
    public static void delete(StoragePool pool, NasMeta meta) {
        if (!meta.getRandomName().isEmpty()) {
            AsynchDelete del = new AsynchDelete();
            del.pool = pool;
            del.meta = meta;
            ThreadPool.pool.execute(del);
        }
    }

    /**
     *
     * @param pool
     * @param meta
     */
    private static void synchDelete(StoragePool pool, NasMeta meta) {
        if (!meta.getRandomName().isEmpty()) {
            try {
                TcpClient tcpclient = pool.getClientSourceMgr().getClientByVolume(meta.getVolumeId());
                DeleteRequest tr =new DeleteRequest();
                tr.setMeta(meta);
                tcpclient.get(tr);
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public void run() {
        synchDelete(pool, meta);
    }

}
