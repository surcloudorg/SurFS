/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.transport;

import java.io.IOException;

public class NullResponse extends TcpResponse {

    public NullResponse(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    public NullResponse(TcpRequest req) {
        super(req.getCommandType(), req.getSequence());
    }

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
    }
}
