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
