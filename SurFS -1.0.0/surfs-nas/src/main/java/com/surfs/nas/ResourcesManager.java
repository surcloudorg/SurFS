package com.surfs.nas;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.transport.ThreadPool;
import java.io.IOException;

public abstract class ResourcesManager extends Thread {

    private static final Logger log = LogFactory.getLogger(ResourcesManager.class);
    protected GlobleProperties globleProperties = null;
    protected String version = null;
    protected final NosqlDataSource datasource;

    public ResourcesManager(NosqlDataSource datasource) throws IOException {
        this.datasource = datasource;
        this.setDaemon(true);
        this.setName(ResourcesManager.class.getSimpleName());
    }

    /**
     *
     * @throws IOException
     */
    public final void initialize() throws IOException {
        try {
            load();
            init();
            this.start();
        } catch (IOException r) {
            this.shutdown();
            throw r;
        } catch (Throwable r) {
            this.shutdown();
            throw new IOException(r);
        }
    }

    /**
     *
     * @throws IOException
     */
    protected abstract void init() throws IOException;

    /**
     *
     * @return @throws IOException
     */
    protected abstract boolean load() throws IOException;

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            int interval = getGlobleProperties().getReloadInterval() * 1000;
            try {
                sleep(interval);
                load();
            } catch (InterruptedException ie) {
                break;
            } catch (Throwable ex) {
                log.trace("Load service parameter failed:", ex);
            }
        }
    }

    /**
     * close
     */
    public void shutdown() {
        ThreadPool.stopThread(this);
    }

    /**
     * @return the GlobleProperties
     */
    public GlobleProperties getGlobleProperties() {
        return globleProperties;
    }

    /**
     * @return the datasource
     */
    public NosqlDataSource getDatasource() {
        return datasource;
    }

}
