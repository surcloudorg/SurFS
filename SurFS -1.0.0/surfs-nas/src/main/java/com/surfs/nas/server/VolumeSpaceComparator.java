/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

import java.util.Comparator;

public class VolumeSpaceComparator implements Comparator<Volume> {

    @Override
    public int compare(Volume o1, Volume o2) {
        int space1 = o1.getChecker().getPercent();
        int space2 = o2.getChecker().getPercent();
        return Integer.compare(space2, space1);
    }
}
