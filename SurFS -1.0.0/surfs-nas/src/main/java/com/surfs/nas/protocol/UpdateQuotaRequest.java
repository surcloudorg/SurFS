/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.surfs.nas.log.LogFactory;
import com.surfs.nas.log.Logger;
import com.surfs.nas.server.AsychQuotaUpdater;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.NullResponse;
import com.surfs.nas.transport.TcpCommandDecoder;
import com.surfs.nas.transport.TcpCommandEncoder;
import com.surfs.nas.transport.TcpCommandType;
import com.surfs.nas.transport.TcpRequest;
import java.io.IOException;

public class UpdateQuotaRequest extends TcpRequest {

    private static final Logger log = LogFactory.getLogger(UpdateQuotaRequest.class);
    private long dirid;

    public UpdateQuotaRequest() {
        super(TcpCommandType.QUOTA);
    }

    public UpdateQuotaRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
        this.dirid = m_in.readLong();
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
        m_out.writeLong(dirid);
    }

    @Override
    public String toString() {
        return "dirid=".concat(String.valueOf(getDirid()));
    }

    @Override
    public void run() {
        try {
             
            AsychQuotaUpdater.startUpdate(getServerSourceMgr(), dirid);
            this.getSession().sendMessage(new NullResponse(this));
        } catch (Throwable e) {
            this.getSession().sendMessage(new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e)));
        }
    }

    /**
     * @return the dirid
     */
    public long getDirid() {
        return dirid;
    }

    /**
     * @param dirid the dirid to set
     */
    public void setDirid(long dirid) {
        this.dirid = dirid;
    }

}
