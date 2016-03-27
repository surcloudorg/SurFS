/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.server.VolumeDirectoryMaker;
import com.surfs.nas.server.VolumeInfo;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.OriginRequest;
import com.surfs.nas.transport.OriginResponse;
import com.surfs.nas.transport.TcpCommandType;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InitVolumeRequest extends OriginRequest {

    private static final Logger log = LogFactory.getLogger(InitVolumeRequest.class);

    public InitVolumeRequest() {
        super(TcpCommandType.SYS_VOLUME_INIT);
    }

    public InitVolumeRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public void run() {
        String path = this.getString();
        try {
          
            VolumeDirectoryMaker maker = getServerSourceMgr().getVolumeScaner().getVolumeDirectoryMaker(path);
            if (maker == null) {
                ErrorResponse tr = new ErrorResponse(this, new FileNotFoundException(""));
                this.getSession().sendMessage(tr);
            } else {
                maker.startMake();
                VolumeInfo info = maker.getInfo();
                OriginResponse tr =new OriginResponse(this);
                tr.setObject(info);
                this.getSession().sendMessage(tr);
            }
        } catch (Throwable e) {
            this.getSession().sendMessage(new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e)));
        }
    }

}
