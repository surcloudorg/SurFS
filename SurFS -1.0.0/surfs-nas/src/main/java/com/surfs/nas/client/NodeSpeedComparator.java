package com.surfs.nas.client;

import java.util.Comparator;


public class NodeSpeedComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        float speed1 = o1.testSpeedHandler == null ? 0 : o1.testSpeedHandler.getSpeed();
        float speed2 = o2.testSpeedHandler == null ? 0 : o2.testSpeedHandler.getSpeed();
        return Float.compare(speed2, speed1);
    }

}
