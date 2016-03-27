/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

public class VolumeInfo implements Comparable {

    private String path;
    private String volumeID;
    private int progress = 0;
    private int status = -1;
    private boolean initialized = false;

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the volumeID
     */
    public String getVolumeID() {
        return volumeID;
    }

    /**
     * @param volumeID the volumeID to set
     */
    public void setVolumeID(String volumeID) {
        this.volumeID = volumeID;
    }

    /**
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }

    /**
     * @param progress the progress to set
     */
    public void setProgress(int progress) {
        this.progress = progress;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized the initialized to set
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public int compareTo(Object o) {
        VolumeInfo inf = (VolumeInfo) o;
        return getPath().compareTo(inf.getPath());
    }
}
