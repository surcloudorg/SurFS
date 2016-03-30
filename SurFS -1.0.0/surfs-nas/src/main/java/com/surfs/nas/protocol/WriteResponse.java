/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.protocol;

import com.surfs.nas.transport.TcpCommandDecoder;
import com.surfs.nas.transport.TcpCommandEncoder;
import com.surfs.nas.transport.TcpRequest;
import com.surfs.nas.transport.TcpResponse;
import java.io.IOException;

public class WriteResponse extends TcpResponse {

    private String randomName = null;
    private String volumeId = null; //存储在物理存储介质的设备ID

    public WriteResponse(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    public WriteResponse(TcpRequest req) {
        super(req.getCommandType(), req.getSequence());
    }

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
        //this.length = m_in.readLong();
        randomName = m_in.readString();
        volumeId = m_in.readString();
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
        //m_out.writeLong(length);
        m_out.writeString(randomName);
        m_out.writeString(volumeId);
    }

    /**
     * @return the randomName
     */
    public String getRandomName() {
        return randomName;
    }

    /**
     * @param randomName the randomName to set
     */
    public void setRandomName(String randomName) {
        this.randomName = randomName;
    }

    /**
     * @return the volumeId
     */
    public String getVolumeId() {
        return volumeId;
    }

    /**
     * @param volumeId the volumeId to set
     */
    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }
}
