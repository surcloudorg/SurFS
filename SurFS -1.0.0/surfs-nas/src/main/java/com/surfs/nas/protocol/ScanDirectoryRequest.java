/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.surfs.nas.log.LogFactory;
import com.surfs.nas.log.Logger;
import com.surfs.nas.server.VolumeInfo;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.OriginRequest;
import com.surfs.nas.transport.OriginResponse;
import com.surfs.nas.transport.TcpCommandType;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ScanDirectoryRequest extends OriginRequest {

    private static final Logger log = LogFactory.getLogger(ScanDirectoryRequest.class);

    public ScanDirectoryRequest() {
        super(TcpCommandType.SYS_VOLUME_SCAN_INIT);
    }

    public ScanDirectoryRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public void run() {
        try {
            
            List<VolumeInfo> list = getServerSourceMgr().getVolumeScaner().listVolume();
            OriginResponse tr =new OriginResponse(this);
            Collections.sort(list);
            tr.setObjects(list);
            this.getSession().sendMessage(tr);
        } catch (Throwable e) {
            this.getSession().sendMessage(new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e)));
        }
    }

}
