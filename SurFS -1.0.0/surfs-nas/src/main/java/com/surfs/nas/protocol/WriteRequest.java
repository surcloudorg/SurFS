package com.surfs.nas.protocol;

import com.surfs.nas.error.SessionTimeoutException;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.TcpCommandDecoder;
import com.surfs.nas.transport.TcpCommandEncoder;
import com.surfs.nas.transport.TcpCommandType;
import com.surfs.nas.transport.TcpRequest;
import com.surfs.nas.transport.TcpResponse;
import java.io.IOException;

public class WriteRequest extends TcpRequest {

    private boolean create;
    private long parentId;
    private long fileId;
    private long offset;
    private byte[] content;
    private int pos;
    private int len;

    public WriteRequest() {
        super(TcpCommandType.WRITE);
    }

    public WriteRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public String toString() {
        return "parentId=".concat(String.valueOf(getParentId()))
                .concat(",fileId=").concat(String.valueOf(getFileId()))
                .concat(",offset=").concat(String.valueOf(offset));
    }

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
        this.create = m_in.readBoolean();
        this.parentId = m_in.readLong();
        this.fileId = m_in.readLong();
        this.offset = m_in.readLong();
        this.content = m_in.readBytes();
        this.len = content == null ? 0 : content.length;
        this.pos = 0;
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
        m_out.writeBoolean(create);
        m_out.writeLong(parentId);
        m_out.writeLong(fileId);
        m_out.writeLong(offset);
        m_out.writeBytes(content, pos, len);
    }

    @Override
    public void run() {
        TcpResponse tr;
        for (;;) {
            try {
                RandomAccessAction action = this.getServerSourceMgr().getTcpActionMgr().putTcpAction(parentId, fileId);
                tr = action.write(this);
                break;
            } catch (SessionTimeoutException ste) {
            } catch (Throwable e) {
                tr = new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e));
                break;
            }
        }
        this.getSession().sendMessage(tr);
    }

    /**
     * @return the offset
     */
    public long getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(long offset) {
        this.offset = offset;
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

    /**
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

}
