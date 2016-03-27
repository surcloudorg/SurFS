/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

import java.io.Serializable;
import net.sf.json.JSONObject;

public class NasMeta implements Serializable {

    private static final long serialVersionUID = 2014340294152205470L;

    private long parentId = 0L;
    private long fileId = 0L;
    private String fileName = "";
    private String randomName = "";
    private String volumeId = "";
    private long length = 0;
    private long lastModified = 0;

    @Override
    public String toString() {
        JSONObject obj = JSONObject.fromObject(this);
        return obj.toString();
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the volumeId
     */
    public String getVolumeId() {
        return volumeId;
    }

    /**
     * @param volumeId the volumeId to set
     */
    public synchronized void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    /**
     * @return the randomName
     */
    public String getRandomName() {
        return randomName == null ? "" : randomName;
    }

    /**
     * @param randomName the randomName to set
     */
    public synchronized void setRandomName(String randomName) {
        this.randomName = randomName == null ? "" : randomName;
    }

    /**
     * @return the fileId
     */
    public long getFileId() {
        return fileId;
    }

    /**
     * @param fileId the fileId to set
     */
    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    /**
     * @return the parentId
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the length
     */
    public long getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public synchronized void setLength(long length) {
        this.length = length;
    }

    /**
     * @return the lastModified
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public synchronized void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
