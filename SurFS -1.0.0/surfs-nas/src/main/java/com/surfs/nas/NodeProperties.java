/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas;

import java.io.Serializable;
import net.sf.json.JSONObject;

public class NodeProperties implements Serializable {

    private static final long serialVersionUID = 2014340294152205474L;

    private String serverHost = null;
    private int port = 8080;
    private String backupList = null;

    @Override
    public String toString() {
        JSONObject obj = JSONObject.fromObject(this);
        return obj.toString();
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
     * @return the httpPort
     */
    public int getPort() {
        return port;
    }

    /**
     * @param httpPort the httpPort to set
     */
    public void setPort(int httpPort) {
        this.port = httpPort;
    }

    /**
     * @return the backupList
     */
    public String getBackupList() {
        return backupList;
    }

    /**
     * @param backupList the backupList to set
     */
    public void setBackupList(String backupList) {
        this.backupList = backupList;
    }
}
