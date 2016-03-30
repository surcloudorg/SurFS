/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.surfs.nas.log.LogFactory;
import com.surfs.nas.log.Logger;
import com.surfs.nas.NasMeta;
import com.surfs.nas.StorageSources;
import com.surfs.nas.error.FileExistException;
import com.surfs.nas.server.AsychQuotaUpdater;
import com.surfs.nas.server.HandleProgress;
import com.surfs.nas.server.Volume;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.NullResponse;
import com.surfs.nas.transport.TcpCommandDecoder;
import com.surfs.nas.transport.TcpCommandEncoder;
import com.surfs.nas.transport.TcpCommandType;
import com.surfs.nas.transport.TcpRequest;
import java.io.File;
import java.io.IOException;

public class DeleteRequest extends TcpRequest {

    private static final Logger log = LogFactory.getLogger(DeleteRequest.class);
    private NasMeta meta;

    public DeleteRequest() {
        super(TcpCommandType.DELETE);
    }

    public DeleteRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public String toString() {
        return meta.toString();
    }

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
        this.setMeta(new NasMeta());
        getMeta().setParentId(m_in.readLong());
        getMeta().setFileName(m_in.readString());
        getMeta().setFileId(m_in.readLong());
        getMeta().setLength(m_in.readLong());
        getMeta().setVolumeId(m_in.readString());
        getMeta().setRandomName(m_in.readString());
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
        m_out.writeLong(meta.getParentId());
        m_out.writeString(meta.getFileName());
        m_out.writeLong(meta.getFileId());
        m_out.writeLong(meta.getLength());
        m_out.writeString(meta.getVolumeId());
        m_out.writeString(meta.getRandomName());
    }

    @Override
    public void run() {
        try {
            
            Volume volume = this.getServerSourceMgr().getSelector().getVolume(meta.getVolumeId());
            File f = HandleProgress.newFile(volume.getPath(), meta.getRandomName());
            NasMeta nm = StorageSources.getServiceStoragePool().getDatasource()
                    .getNasMetaAccessor().queryNasMeta(meta.getParentId(), meta.getFileId());
            if (nm != null) {
                
                this.getSession().sendMessage(new ErrorResponse(this, new FileExistException("")));
            } else {
                HandleProgress.deleteFile(f);
                AsychQuotaUpdater.startUpdate(this.getServerSourceMgr(), meta.getParentId());
                this.getSession().sendMessage(new NullResponse(this));
            }
        } catch (Throwable ex) {
            this.getSession().sendMessage(new ErrorResponse(this, ex instanceof IOException ? (IOException) ex : new IOException(ex)));
        }
    }

    /**
     * @return the meta
     */
    public NasMeta getMeta() {
        return meta;
    }

    /**
     * @param meta the meta to set
     */
    public void setMeta(NasMeta meta) {
        this.meta = meta;
    }

}
