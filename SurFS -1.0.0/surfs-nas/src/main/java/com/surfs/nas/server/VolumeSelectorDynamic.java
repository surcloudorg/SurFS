/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

import com.surfs.nas.log.LogFactory;
import com.surfs.nas.log.Logger;
import com.surfs.nas.error.VolumeBusyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class VolumeSelectorDynamic extends VolumeSelector {

    private static final Logger log = LogFactory.getLogger(VolumeSelectorDynamic.class);
    private Volume[] vols = null;
    private int index = 0;
    private int length = 0;
    private int curNum = -1;

    public VolumeSelectorDynamic(ServerSourceMgr mgr) {
        super(mgr);
        length = mgr.getVolumeMap().size();
    }

    @Override
    public Volume getVolume() throws VolumeBusyException {
        Volume vol;
        Volume volume = null;
        if (length == 0) {
            length = mgr.getVolumeMap().size();
        }
        for (int ii = 0; ii < length; ii++) {
            vol = getNext();
            if (vol == null) {
                throw new VolumeBusyException("");
            } else {
                if (vol.getVolumeProperties().getFileHandleNum() > 0) {
                    volume = vol;
                }
                if (vol.isReady() && curNum > 0) {
                    curNum--;
                    return vol;
                } else {
                    index++;
                    curNum = -1;
                    if (index >= vols.length) {
                        vols = null;
                    }
                }
            }
        }
        if (volume == null) {
            throw new VolumeBusyException("");
        } else {
            if (volume.isOffline()) {
                throw new VolumeBusyException("");
            }
            return volume;
        }
    }

    /**
     *
     * @return Node
     */
    private Volume getNext() {
        if (vols == null) {
            if (mgr.getVolumeMap().isEmpty()) {
                return null;
            }
            List<Volume> col = new ArrayList<>(mgr.getVolumeMap().values());
            Collections.sort(col, new VolumeSpaceComparator());
            vols = col.toArray(new Volume[col.size()]);
            index = 0;
            length = vols.length;
            curNum = -1;
        }
        Volume vol = vols[index];
        if (curNum < 0) {
            curNum = vol.getChecker().getPercent() / 5;
            curNum = curNum < 1 ? 1 : curNum;
        }
        return vol;
    }

    @Override
    public synchronized void spaceChange() {
        long total = 0;
        Collection<Volume> col = mgr.getVolumeMap().values();
        for (Volume vol : col) {
            total = total + vol.getChecker().getFreeSpace();
        }
        long spaceThresholdSize = mgr.getGlobleProperties().getSpaceThresholdSize()*1024l * 1024l * 1024l * (long)mgr.getVolumeMap().size();
        hasSpace = total > spaceThresholdSize; 
        if (total > 0) {
            col = mgr.getVolumeMap().values();
            for (Volume vol : col) {
                int per = vol.getVolumeProperties().getPriority();
                per = per < 1 ? 1 : (per > 9 ? 9 : per);
                int percent = (int) (vol.getChecker().getFreeSpace() * 100 / total);
                percent = percent + (per - 5) * 10;
                vol.getChecker().setPercent(percent);     
            }
        }
    }
}
