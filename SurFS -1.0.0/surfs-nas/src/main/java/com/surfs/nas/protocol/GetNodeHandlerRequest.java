/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.surfs.nas.log.LogFactory;
import com.surfs.nas.log.Logger;
import com.surfs.nas.server.Volume;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.LongResponse;
import com.surfs.nas.transport.OriginRequest;
import com.surfs.nas.transport.TcpCommandType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetNodeHandlerRequest extends OriginRequest {

    private static final Logger log = LogFactory.getLogger(GetNodeHandlerRequest.class);

    public GetNodeHandlerRequest() {
        super(TcpCommandType.SYS_NODE_HANDLER);
    }

    public GetNodeHandlerRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public void run() {
        try {
            
            List<Volume> lists = new ArrayList<>(getServerSourceMgr().getVolumeMap().values());
            long freenum = 0;
            for (Volume volume : lists) {
                if (volume.isReady()) {
                    if (volume.getHandlers() != null) {
                        freenum = freenum + volume.getHandlers().remainingCapacity();
                    }
                }
            }
            LongResponse tr =new LongResponse(this);
            tr.setValue(freenum);
            this.getSession().sendMessage(tr);
        } catch (Throwable e) {
            this.getSession().sendMessage(new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e)));
        }
    }

}
