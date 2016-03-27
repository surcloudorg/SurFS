/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.server;

import com.surfs.nas.error.VolumeBusyException;
import com.surfs.nas.error.VolumeNotFoundException;
import java.io.IOException;

public abstract class VolumeSelector {

    protected ServerSourceMgr mgr = null;
    protected boolean hasSpace = true;

    public VolumeSelector(ServerSourceMgr mgr) {
        this.mgr = mgr;
    }

    public abstract void spaceChange();

    /**
     *
     * @return node
     * @throws VolumeBusyException
     */
    public abstract Volume getVolume() throws VolumeBusyException;

    /**
     *
     * @param volID
     * @return Volume
     * @throws IOException
     */
    public final Volume getVolume(String volID) throws IOException {
        Volume vol = mgr.getVolumeMap().get(volID);
        if (vol == null) {
            mgr.getVolumeScaner().restart();
            vol = mgr.getVolumeMap().get(volID);
        }
        if (vol == null) {
            throw new VolumeNotFoundException("");
        }
        if (vol.isOffline()) {
            throw new VolumeBusyException("");
        }
        return vol;
    }
    
    /**
     * @return the hasSpace
     */
    public boolean hasSpace() {
        return hasSpace;
    }

}
