/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import com.surfs.nas.error.SystemBusyException;
import com.surfs.nas.error.VolumeNotFoundException;
import java.io.IOException;


public abstract class NodeSelector {

    protected ClientSourceMgr mgr = null;
    private String specifiedServer = null;

    public NodeSelector(ClientSourceMgr mgr) {
        this.mgr = mgr;
        this.specifiedServer = System.getProperty("testnode");
    }

    /**
     *
     * @return node
     */
    protected abstract Node select();

    /**
     *
     * @return Node
     * @throws SystemBusyException
     */
    final Node getNode() throws IOException {
        Node node = specifiedServer == null
                ? select()
                : getNode(specifiedServer, true);
        if (node == null) {
            throw new SystemBusyException();
        } else {
            return node;
        }

    }

    /**
     *
     * @param key
     * @param byNode
     * @return
     * @throws IOException
     */
    final Node getNode(String key, boolean byNode) throws IOException {
        return getNode(key, byNode, false);
    }

    /**
     *
     * @param key
     * @param byNode true
     * @return Node
     * @throws IOException
     */
    final Node getNode(String key, boolean byNode, boolean bln) throws IOException {
        if (key == null) {
            throw new VolumeNotFoundException();
        }
        for (int ii = 0; ii < mgr.getGlobleProperties().getErrRetryTimes(); ii++) {
            try {
                ClientSourceMgr zmgr = (ClientSourceMgr) mgr;
                String serverip = byNode ? key : zmgr.volLocationMap.get(key);
                Node node = serverip == null ? null : mgr.getServerMap().get(serverip);
                if (node == null) {
                    boolean b = mgr.load();
                    if (!b) {
                        if (bln) {
                            throw new VolumeNotFoundException();
                        }
                        Thread.sleep((mgr.getGlobleProperties().getErrRetryInterval() * 1000)
                                / mgr.getGlobleProperties().getErrRetryTimes()
                                + mgr.getGlobleProperties().getErrRetryTimes());
                    }
                } else {
                    return node;
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException ie) {
            }
        }
        throw new VolumeNotFoundException();
    }
}
