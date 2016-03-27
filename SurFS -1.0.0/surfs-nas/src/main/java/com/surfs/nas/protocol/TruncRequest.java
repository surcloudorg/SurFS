/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.surfs.nas.error.SessionTimeoutException;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.TcpCommandDecoder;
import com.surfs.nas.transport.TcpCommandEncoder;
import com.surfs.nas.transport.TcpCommandType;
import com.surfs.nas.transport.TcpRequest;
import com.surfs.nas.transport.TcpResponse;
import java.io.IOException;

public class TruncRequest extends TcpRequest {

    private boolean create;
    private long parentId;
    private long fileId;
    private long length;

    public TruncRequest() {
        super(TcpCommandType.TRUNC);
    }

    public TruncRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public String toString() {
        return "parentId=".concat(String.valueOf(parentId))
                .concat(",fileId=").concat(String.valueOf(fileId))
                .concat(",length=").concat(String.valueOf(getLength()));
    }

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
        this.create = m_in.readBoolean();
        this.parentId = m_in.readLong();
        this.fileId = m_in.readLong();
        this.length = m_in.readLong();
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
        m_out.writeBoolean(create);
        m_out.writeLong(parentId);
        m_out.writeLong(fileId);
        m_out.writeLong(length);
    }

    @Override
    public void run() {
        TcpResponse tr;
        for (;;) {
            try {
                RandomAccessAction action = this.getServerSourceMgr().getTcpActionMgr().putTcpAction(parentId, fileId);
                tr = action.trunc(this);
                break;
            } catch (SessionTimeoutException ste) {
            } catch (Throwable e) {
                tr =new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e));
                break;
            }
        }
        this.getSession().sendMessage(tr);
    }

    /**
     * @return the length
     */
    public long getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(long length) {
        this.length = length;
    }

    /**
     * @return the create
     */
    public boolean isCreate() {
        return create;
    }

    /**
     * @param create the create to set
     */
    public void setCreate(boolean create) {
        this.create = create;
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

}
