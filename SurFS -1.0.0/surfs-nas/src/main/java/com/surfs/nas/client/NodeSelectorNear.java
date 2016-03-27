/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NodeSelectorNear extends NodeSelector {

    private static final float usual_speed = 200;
    private static final long interval = 1000 * 60 * 60;

    private Node node = null;
    private final long startTime = System.currentTimeMillis();

    public NodeSelectorNear(ClientSourceMgr mgr) {
        super(mgr);
    }

    @Override
    protected synchronized Node select() {
        if (node == null) {
            if (mgr.getServerMap().isEmpty()) {
                return null;
            }
            List<Node> list = new ArrayList(mgr.getServerMap().values());
            Collections.sort(list, new NodeSpeedComparator());
            Node n = list.get(0);
            if (n.testSpeedHandler.getSpeed() > usual_speed) {
                node = n;
            } else {
                if (System.currentTimeMillis() - startTime > interval) {
                    for (Node nd : list) {
                        if (nd.testSpeedHandler != null) {
                            nd.testSpeedHandler.restart();
                        }
                    }
                }
                for (Node nd : list) {
                    if (nd.isReady()) {
                        return nd;
                    }
                }
            }
            return n;
        }
        if (!node.isReady()) {
            return getBackupNode();
        } else {
            return node;
        }
    }

    /**
     *
     * @return
     */
    private Node getBackupNode() {
        String iplist = node.getNodeProperties().getBackupList();
        if (iplist != null) {
            String[] ips = iplist.split(",");
            for (String ip : ips) {
                Node n = mgr.getServerMap().get(ip);
                if (n != null && n.isReady()) {
                    return n;
                }
            }
        }
        return node;
    }

}
