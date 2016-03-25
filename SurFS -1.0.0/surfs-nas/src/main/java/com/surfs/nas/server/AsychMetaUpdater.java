package com.surfs.nas.server;

import com.surfs.nas.StorageSources;
import com.surfs.nas.NasMeta;
import com.surfs.nas.transport.ThreadPool;
import java.io.File;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class AsychMetaUpdater implements Callable {

    private final NasMeta nasmeta;
    private final File file;
    private Future future = null;
    private final ServerSourceMgr mgr;

    public AsychMetaUpdater(ServerSourceMgr mgr, NasMeta nasmeta, File file) {
        this.mgr = mgr;
        this.nasmeta = nasmeta;
        this.file = file;
    }

    public long curLength() {
        return nasmeta.getLength();
    }

    /**
     *
     * @param force 
     */
    public void start(boolean force) {
        synchronized (this) {
            if (future != null && (!future.isDone())) {
                if (force) {
                    future.cancel(true);
                    future = ThreadPool.pool.submit(this);
                }
            } else {
                future = ThreadPool.pool.submit(this);
            }
        }
    }

    /**
     *
     * @throws IOException
     */
    private void update() throws IOException {
        long len = file.length();
        long lastlength = nasmeta.getLength();
        if (len != lastlength) {
            nasmeta.setLength(len);
            try {
                StorageSources.getServiceStoragePool().getDatasource().getNasMetaAccessor().updateNasMeta(nasmeta, true);
            } catch (IOException e) {
                nasmeta.setLength(lastlength);
                throw e;
            }
            AsychQuotaUpdater.startUpdate(mgr, nasmeta.getParentId());
        }
    }

    @Override
    public Object call() {
        for (;;) {
            try {
                update();
                sleep(mgr.getGlobleProperties().getCheckSpaceInterval() * 1000);
                break;
            } catch (InterruptedException ex) {
                break;
            } catch (Throwable ex) {
                try {
                    sleep(mgr.getGlobleProperties().getCheckSpaceInterval() * 1000);
                } catch (InterruptedException ex1) {
                    break;
                }
            }
        }
        return null;
    }
}
