/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import com.surfs.nas.log.LogFactory;
import com.surfs.nas.log.Logger;
import com.surfs.nas.protocol.GetNodeHandlerRequest;
import com.surfs.nas.transport.LongResponse;
import java.io.IOException;
import static java.lang.Thread.sleep;


public class NodeChargeGetter extends Thread {

    private static final Logger log = LogFactory.getLogger(NodeChargeGetter.class);
    private Node node = null;
    private int serverFreeThreadNum = 1;

    /**
     *
     * @param node
     */
    public NodeChargeGetter(Node node) {
        this.node = node;
        this.setName(NodeChargeGetter.class.getSimpleName());
        this.setDaemon(true);
    }

    private void get() throws IOException {
        try {
            GetNodeHandlerRequest tr = new GetNodeHandlerRequest();
            LongResponse response = (LongResponse) node.tcpclient.get(tr);
            serverFreeThreadNum = (int) response.getValue();
            if (serverFreeThreadNum < 1) {
                serverFreeThreadNum = 1;
            }
        } catch (Exception r) {
            throw r instanceof IOException ? (IOException) r : new IOException(r);
        }
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            if (!node.tcpclient.isDestory()) {
                try {
                    get();
                    node.setState(true);
                    sleep(1000 * 60);
                } catch (InterruptedException ie) {
                    break;
                } catch (Exception ex) {
                    try {
                        sleep(node.getGlobleProperties().getErrRetryInterval() * 1000);
                    } catch (InterruptedException ex1) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * @return the serverFreeThreadNum
     */
    public int getServerFreeThreadNum() {
        return serverFreeThreadNum;
    }

}
