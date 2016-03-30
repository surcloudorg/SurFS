/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.surfs.nas.log.LogFactory;
import com.surfs.nas.log.Logger;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.LongResponse;
import com.surfs.nas.transport.TcpCommandDecoder;
import com.surfs.nas.transport.TcpCommandEncoder;
import com.surfs.nas.transport.TcpCommandType;
import com.surfs.nas.transport.TcpRequest;
import com.surfs.nas.transport.TcpResponse;
import java.io.IOException;

public class CloseRequest extends TcpRequest {

    private static final Logger log = LogFactory.getLogger(CloseRequest.class);

    private long parentId;
    private long fileId;

    public CloseRequest() {
        super(TcpCommandType.CLOSE);
    }

    public CloseRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public void run() {
        TcpResponse tr;
        try {
            RandomAccessAction action = this.getServerSourceMgr().getTcpActionMgr().getTcpAction(parentId, fileId);
            if (action == null) {
                tr = new LongResponse(this);
            } else {
                tr = action.complete(this);
            }
        } catch (Throwable e) {
            tr = new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e));
        }
        this.getSession().sendMessage(tr);
    }

    /**
     * @return the parentId
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the fileId
     */
    public long getFileId() {
        return fileId;
    }

    /**
     * @param fileId the fileId to set
     */
    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
        this.parentId = m_in.readLong();
        this.fileId = m_in.readLong();
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
        m_out.writeLong(parentId);
        m_out.writeLong(fileId);
    }

}
