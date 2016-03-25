package com.surfs.nas.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NodeSelectorDynamic extends NodeSelector {

    private Node[] nodes = null;
    private int index = 0;
    private int length = 0;
    private int minNum = 0;
    private int curNum = -1;
    private int count = 0;
    private Node lastnode = null;

    public NodeSelectorDynamic(ClientSourceMgr mgr) {
        super(mgr);
    }

    @Override
    protected synchronized Node select() {
        if (length == 0) {
            length = mgr.getServerMap().size();
        }
        if (length > 1 && count >= 10) {
            nodes = null;
        }
        Node node = null;
        for (int ii = 0; ii < length; ii++) {
            node = getNext();
            if (node == null) {
                return null;
            } else {
                if (node.isReady() && curNum >= minNum) {
                    curNum--;
                    break;
                } else {
                    index++;
                    curNum = -1;
                    if (index >= nodes.length) {
                        nodes = null;
                    }
                }
            }
        }
        if (lastnode == null) {
            lastnode = node;
            count = 1;
        } else {
            if (lastnode == node) {
                count++;
            } else {
                lastnode = node;
                count = 1;
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
            List<Node> col = new ArrayList<>(mgr.getServerMap().values());
            Collections.sort(col, new NodeThreadComparator());
            nodes = new Node[col.size()];
            nodes = col.toArray(nodes);
            index = 0;
            count = 0;
            length = nodes.length;
            minNum = nodes[length - 1].chargeGetter.getServerFreeThreadNum();
            curNum = -1;
        }
        Node node = nodes[index];
        if (curNum < 0) {
            curNum = node.chargeGetter.getServerFreeThreadNum();
        }
        return node;
    }
}
