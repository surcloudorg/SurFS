package com.surfs.nas.transport;

import java.io.IOException;

public class LongResponse extends TcpResponse {

    private long value = 0;

    public LongResponse(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    public LongResponse(TcpRequest req) {
        super(req.getCommandType(), req.getSequence());
    }

    /**
     * @return the length
     */
    public long getValue() {
        return value;
    }

    /**
     * @param length the length to set
     */
    public void setValue(long length) {
        this.value = length;
    }

    @Override
    protected void read(TcpCommandDecoder m_in) throws IOException {
        this.value = m_in.readLong();
    }

    @Override
    protected void write(TcpCommandEncoder m_out) throws IOException {
        m_out.writeLong(value);
    }

}
