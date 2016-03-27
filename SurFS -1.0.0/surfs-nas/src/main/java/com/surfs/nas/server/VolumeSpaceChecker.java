/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import java.io.File;
import java.io.IOException;
import net.sf.json.JSONObject;


public class VolumeSpaceChecker extends Thread {

    private static final Logger log = LogFactory.getLogger(VolumeSpaceChecker.class);
    Volume volume = null;
    private long freeSpace = 0;
    private long totalSpace = 0;
    private int percent = 100;

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("freeSpace", this.freeSpace);
        obj.put("totalSpace", this.totalSpace);
        obj.put("percent", this.percent);
        return obj.toString();
    }

    VolumeSpaceChecker(Volume volume) throws IOException {
        this.volume = volume;
        this.setDaemon(true);
        this.setName(VolumeSpaceChecker.class.getSimpleName() + "-" + volume.getVolumeProperties().getVolumeID());
        this.check();
    }

    /**
     *
     * @throws IOException
     */
    public synchronized final void check() throws IOException {
        File f = volume.path;
        long l = f.getTotalSpace();
        if (l > 0) {
            totalSpace = l;
            freeSpace = f.getUsableSpace();
            if (freeSpace <= volume.getGlobleProperties().getSpaceThresholdSize() * 1024l * 1024l * 1024l) {
                volume.setSpaceWarn(true);
            } else {
                volume.setSpaceWarn(false);
            }
        } else {
            volume.getServerSourceMgr().getVolumeScaner().restart();
            throw new IOException("");
        }
    }

    public void shutdown() {
        exiting = true;
        if (currentThread != null) {
            currentThread.interrupt();
        }
    }

    private boolean exiting = false;
    private Thread currentThread = null;

    @Override
    public void run() {
        currentThread = Thread.currentThread();
        while (!exiting) {
            try {
                sleep(volume.getServerSourceMgr().getGlobleProperties().getCheckSpaceInterval() * 60 * 1000);
                check();
                volume.getServerSourceMgr().getSelector().spaceChange();
            } catch (InterruptedException e) {
                break;
            } catch (Throwable e) {
            }
        }
    }

    /**
     * @return the spacePercent
     */
    public int getPercent() {
        return percent;
    }

    /**
     * @param spacePercent the spacePercent to set
     */
    public void setPercent(int spacePercent) {
        this.percent = spacePercent;
    }

    /**
     * @return the freeSpace
     */
    public long getFreeSpace() {
        return freeSpace;
    }

    /**
     * @return the totalSpace
     */
    public long getTotalSpace() {
        return totalSpace;
    }
}
