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
