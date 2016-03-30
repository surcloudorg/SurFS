/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

import java.io.Serializable;
import net.sf.json.JSONObject;

public class VolumeProperties implements Serializable {

    private static final long serialVersionUID = 2014340294152205474L;

    private String volumeID = null;
    private int priority = 5;
    private int fileHandleNum = 2000;
    private String serverHost = null;

    @Override
    public String toString() {
        JSONObject obj = JSONObject.fromObject(this);
        return obj.toString();
    }

    /**
     * @return the VolumeID
     */
    public String getVolumeID() {
        return volumeID;
    }

    /**
     * @param VolumeID the VolumeID to set
     */
    public void setVolumeID(String VolumeID) {
        this.volumeID = VolumeID;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return the serverHost
     */
    public String getServerHost() {
        return serverHost;
    }

    /**
     * @param serverHost the serverHost to set
     */
    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    /**
     * @return the filehandleNum
     */
    public int getFileHandleNum() {
        return fileHandleNum;
    }

    /**
     * @param filehandleNum the filehandleNum to set
     */
    public void setFileHandleNum(int filehandleNum) {
        this.fileHandleNum = filehandleNum > 10000 ? 10000
                : (filehandleNum < 1000 ? 1000 : filehandleNum);
    }
}
