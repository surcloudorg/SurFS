package com.surfs.nas.transport;

import com.autumn.util.BufferPool;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TcpResponseEncoder extends TcpCommandEncoder {

    private final TcpSession session;

    public TcpResponseEncoder(TcpSession session) {
        this.session = session;
    }

    @Override
    public void sendBuffer(ByteBuffer buf) throws IOException {
        try {
            while (buf.remaining() > 0) {
                session.channel.write(buf);
            }
        } catch (Exception r) {
            throw r;
        } finally {
            BufferPool.freeByteBuffer(buf);
        }

    }

}
