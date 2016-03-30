/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

import com.surfs.nas.util.TextUtils;
import com.surfs.nas.error.VolumeBusyException;
import com.surfs.nas.GlobleProperties;
import com.surfs.nas.VolumeProperties;
import com.surfs.nas.transport.ThreadPool;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

 
public final class Volume {

    File path = null;
    ServerSourceMgr mgr = null;
    private VolumeSpaceChecker checker = null;

    private VolumeProperties volumeProperties = null;
    private final AtomicBoolean ready = new AtomicBoolean(true);
    private final AtomicBoolean spaceWarn = new AtomicBoolean(false);
    private final AtomicBoolean offline = new AtomicBoolean(false);
    private long errTime = System.currentTimeMillis();
    private BlockingQueue<HandleProgress> handlers = null;

    Volume(ServerSourceMgr mgr, VolumeProperties volumeProperties, File path) throws IOException {
        this.mgr = mgr;
        this.path = path;
        this.setVolumeProperties(volumeProperties);
        this.checker = new VolumeSpaceChecker(this);
    }

    void init() {
        if (!this.checker.isAlive()) {
            ThreadPool.pool.execute(checker);
        }
    }

    /**
     *
     * @param info
     */
    public void delHandler(HandleProgress info) {
        if (info != null) {
            handlers.remove(info);
        }
    }

    /**
     *
     * @param info
     * @throws VolumeBusyException
     */
    public void addHandlerWBlock(HandleProgress info) throws VolumeBusyException {
        try {
            boolean b = handlers.offer(info, getServerSourceMgr().getGlobleProperties().getReadTimeout(), TimeUnit.SECONDS);
            if (!b) {
                throw new VolumeBusyException("");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new VolumeBusyException("");
        }
    }

    /**
     *
     * @return BlockingQueue<>
     */
    public BlockingQueue<HandleProgress> getHandlers() {
        synchronized (this) {
            return handlers;
        }
    }

    /**
     *
     * @return
     */
    public String getErrTime() {
        return TextUtils.Date2String(new Date(errTime));
    }

    /**
     * @return the volumeProperties
     */
    public VolumeProperties getVolumeProperties() {
        return volumeProperties;
    }

    /**
     * @param volumeProperties the volumeProperties to set
     */
    public void setVolumeProperties(VolumeProperties volumeProperties) {
        synchronized (this) {
            if (this.volumeProperties == null || this.volumeProperties.getFileHandleNum() != volumeProperties.getFileHandleNum()) {
                if (handlers == null) {
                    handlers = new ArrayBlockingQueue<>(volumeProperties.getFileHandleNum());
                } else {
                    BlockingQueue queue = new ArrayBlockingQueue<>(volumeProperties.getFileHandleNum());
                    handlers.drainTo(queue, queue.remainingCapacity());
                }
            }
            this.volumeProperties = volumeProperties;
        }
    }

    /**
     * @return the path
     */
    public File getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(File path) {
        this.path = path;
    }

    public void destroy() {
        if (checker != null) {
            checker.shutdown();
        }
    }

    /**
     * @return the ready
     */
    public boolean isReady() {
        return ready.get() && spaceWarn.get() == false && offline.get() == false;
    }

    /**
     * @return the GlobleProperties
     */
    public GlobleProperties getGlobleProperties() {
        return getServerSourceMgr().getGlobleProperties();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.volumeProperties.toString());
        sb.append(",path:").append(getPath().getAbsolutePath());
        sb.append(",statu:").append(isReady() ? "OK" : "ERR");
        sb.append(",lastErrTime:").append(getErrTime()).append("\r\n");
        sb.append("handler total:").append(handlers == null ? 0 : handlers.size()).append("\r\n");
        return sb.toString();
    }

    /**
     * @return the mgr
     */
    public ServerSourceMgr getServerSourceMgr() {
        return mgr;
    }

    /**
     *
     * @param ready
     */
    public void setState(boolean ready) {
        this.ready.set(ready);
        synchronized (this) {
            if (!ready) {
                try {
                    errTime = System.currentTimeMillis();
                    getServerSourceMgr().getVolumeScaner().restart();
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * @return the offline
     */
    public boolean isOffline() {
        return offline.get();
    }

    /**
     * @param offline the offline to set
     */
    public void setOffline(boolean offline) {
        if (offline) {
            mgr.getTcpActionMgr().offline(this);
        }
        this.offline.set(offline);
    }

    public void setSpaceWarn(boolean warn) {
        this.spaceWarn.set(warn);
    }

    /**
     * @return the offline
     */
    public boolean isSpaceWarn() {
        return spaceWarn.get();
    }

    /**
     * @return the checker
     */
    public VolumeSpaceChecker getChecker() {
        return checker;
    }
}
