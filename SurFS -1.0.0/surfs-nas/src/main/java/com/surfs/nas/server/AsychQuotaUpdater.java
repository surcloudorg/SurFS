package com.surfs.nas.server;

import com.surfs.nas.StorageSources;
import com.surfs.nas.transport.ThreadPool;
import static java.lang.Thread.sleep;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class AsychQuotaUpdater implements Callable {

    static Map<Long, AsychQuotaUpdater> map = Collections.synchronizedMap(new HashMap<Long, AsychQuotaUpdater>());

    public static synchronized void startUpdate(ServerSourceMgr mgr, long dirid) {
        if (dirid != 0) {
            AsychQuotaUpdater updater = map.get(dirid);
            if (updater == null) {
                updater = new AsychQuotaUpdater(mgr, dirid);
                map.put(dirid, updater);
            }
            updater.start();
        }
    }

    private final long dirid;
    private Future future = null;
    private final ServerSourceMgr mgr;

    AsychQuotaUpdater(ServerSourceMgr mgr, long dirid) {
        this.mgr = mgr;
        this.dirid = dirid;
    }

    /**
     * 开始执行
     *
     */
    public void start() {
        synchronized (this) {
            if (future == null || future.isDone()) {
                future = ThreadPool.pool.submit(this);
            }
        }
    }

    @Override
    public Object call() throws Exception {
        for (;;) {
            try {
                StorageSources.getServiceStoragePool().getDatasource().getNasMetaAccessor().updateDirectory(dirid);
                sleep(mgr.getGlobleProperties().getCheckSpaceInterval() * 2 * 1000);
                break;
            } catch (InterruptedException ex) {
                break;
            } catch (Throwable ex) {
                try {
                    sleep(mgr.getGlobleProperties().getCheckSpaceInterval() * 2 * 1000);
                } catch (InterruptedException ex1) {
                    break;
                }
            }
        }
        return null;
    }

}
