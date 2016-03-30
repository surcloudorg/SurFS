/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

 
import com.surfs.nas.transport.ThreadPool;
import java.io.IOException;
import org.apache.log4j.Logger;

public abstract class ResourcesManager extends Thread {

    private static final Logger log = Logger.getLogger(ResourcesManager.class);
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
