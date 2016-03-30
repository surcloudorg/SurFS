/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.NullResponse;
import com.surfs.nas.transport.OriginRequest;
import com.surfs.nas.transport.TcpCommandType;
import java.io.IOException;

public class ActiveTestRequest extends OriginRequest {

    public ActiveTestRequest() {
        super(TcpCommandType.SYS_ACTIVE_TEST);
    }

    public ActiveTestRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public void run() {
        try {
            this.getSession().sendMessage(new NullResponse(this));
        } catch (Throwable e) {
            this.getSession().sendMessage(new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e)));
        }
    }

}
