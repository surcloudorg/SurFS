/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.VolumeProperties;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.NullResponse;
import com.surfs.nas.transport.OriginRequest;
import com.surfs.nas.transport.TcpCommandType;
import java.io.IOException;

public class SetVolumeRequest extends OriginRequest {

    private static final Logger log = LogFactory.getLogger(SetVolumeRequest.class);

    public SetVolumeRequest() {
        super(TcpCommandType.SYS_VOLUME_SET);
    }

    public SetVolumeRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public void run() {
        VolumeProperties vol = getObject(VolumeProperties.class);
        try {
            
            getServerSourceMgr().updateVolumeProperties(vol);
            this.getSession().sendMessage(new NullResponse(this));
        } catch (Throwable e) {
            this.getSession().sendMessage(new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e)));
        }
    }

}
