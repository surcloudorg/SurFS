/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import java.util.Collection;

public class NodeSelectorLoop extends NodeSelector {

    public NodeSelectorLoop(ClientSourceMgr mgr) {
        super(mgr);
        length = mgr.getServerMap().size();
    }

    private Node[] nodes = null;
    private int index = 0;
    private int length = 0;

    @Override
    protected synchronized Node select() {
        if (length == 0) {
            length = mgr.getServerMap().size();
        }
        Node node = null;
        for (int ii = 0; ii < length; ii++) {
            node = getNext();
            if (node == null) {
                return null;
            } else {
                if (node.isReady()) {
                    return node;
                }
            }
        }
        return node;
    }

    /**
     *
     * @return Node
     */
    private Node getNext() {
        if (nodes == null) {
            if (mgr.getServerMap().isEmpty()) {
                return null;
            }
            Collection<Node> col = mgr.getServerMap().values();
            nodes = new Node[col.size()];
            nodes = col.toArray(nodes);
            index = 0;
            length = nodes.length;
        }
        Node node = nodes[index++];
        if (index >= nodes.length) {
            nodes = null;
        }
        return node;
    }

}
