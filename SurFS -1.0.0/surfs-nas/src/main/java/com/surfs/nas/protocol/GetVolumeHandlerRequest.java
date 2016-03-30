/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.surfs.nas.log.LogFactory;
import com.surfs.nas.log.Logger;
import com.surfs.nas.server.HandleProgress;
import com.surfs.nas.server.Volume;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.OriginRequest;
import com.surfs.nas.transport.OriginResponse;
import com.surfs.nas.transport.TcpCommandType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class GetVolumeHandlerRequest extends OriginRequest {

    private static final Logger log = LogFactory.getLogger(GetVolumeHandlerRequest.class);

    public GetVolumeHandlerRequest() {
        super(TcpCommandType.SYS_VOLUME_HANDLER);
    }

    public GetVolumeHandlerRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public void run() {
        String volid = this.getString();
        try {
           
            Volume vol = getServerSourceMgr().getSelector().getVolume(volid);
            BlockingQueue<HandleProgress> queue = vol.getHandlers();
            OriginResponse tr = new OriginResponse(this);
            List<HandleProgress> list = new ArrayList(queue);
            Collections.sort(list);
            tr.setObjects(list);
            this.getSession().sendMessage(tr);
        } catch (Throwable e) {
            this.getSession().sendMessage(new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e)));
        }
    }

}
