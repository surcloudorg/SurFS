/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.transport;

public class TcpRequestDecoder extends TcpCommandDecoder {

    private final TcpSession session;

    TcpRequestDecoder(TcpSession session) {
        this.session = session;
    }

    @Override
    public void decode() throws Throwable {
        byte cmdtype = this.readByte();
        int sequence = this.readInt();
        TcpRequest request = TcpRequest.newInstance(cmdtype,sequence);
        request.read(this);
        request.setSession(session);
        request.setServerSourceMgr(session.server.mgr);
        ThreadPool.pool.execute(request);
    }
}
