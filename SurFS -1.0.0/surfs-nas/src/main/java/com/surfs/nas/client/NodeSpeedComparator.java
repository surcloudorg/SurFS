/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

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
