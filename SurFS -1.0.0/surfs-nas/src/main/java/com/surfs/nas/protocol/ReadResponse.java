package com.surfs.nas.protocol;

import com.surfs.nas.transport.TcpCommandDecoder;
import com.surfs.nas.transport.TcpCommandEncoder;
import com.surfs.nas.transport.TcpRequest;
import com.surfs.nas.transport.TcpResponse;
import java.io.IOException;

public class ReadResponse extends TcpResponse {

    private String randomName = null;
    private String volumeId = null; //存储在物理存储介质的设备ID
    private byte[] content;
    private int pos;
    private int len;

    public ReadResponse(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    public ReadResponse(TcpRequest req) {
        super(req.getCommandType(), req.getSequence());
    }

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
        randomName = m_in.readString();
        volumeId = m_in.readString();
        this.content = m_in.readBytes();
        this.len = content == null ? 0 : content.length;
        this.pos = 0;
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
        m_out.writeString(randomName);
        m_out.writeString(volumeId);
        m_out.writeBytes(content, pos, len);
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

    /**
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * @return the pos
     */
    public int getPos() {
        return pos;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(int pos) {
        this.pos = pos;
    }

    /**
     * @return the len
     */
    public int getLen() {
        return len;
    }

    /**
     * @param len the len to set
     */
    public void setLen(int len) {
        this.len = len;
    }

    /**
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

}
