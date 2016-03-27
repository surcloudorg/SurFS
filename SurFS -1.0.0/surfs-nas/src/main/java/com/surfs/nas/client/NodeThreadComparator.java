/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.client;

import java.util.Comparator;


public class NodeThreadComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        int num1 = o1.chargeGetter == null ? 0 : o1.chargeGetter.getServerFreeThreadNum();
        int num2 = o2.chargeGetter == null ? 0 : o2.chargeGetter.getServerFreeThreadNum();
        return Integer.compare(num2, num1);
    }

}
