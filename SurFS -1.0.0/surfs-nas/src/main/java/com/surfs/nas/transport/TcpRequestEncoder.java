/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.transport;

import com.surfs.nas.util.BufferPool;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TcpRequestEncoder extends TcpCommandEncoder {

    private final TcpClient client;

    public TcpRequestEncoder(TcpClient client) {
        this.client = client;
    }

    @Override
    public void sendBuffer(ByteBuffer buf) throws IOException {
        try {
            while (buf.remaining() > 0) {
                client.channel.write(buf);
            }
        } catch (IOException r) {
            throw r;
        } finally {
            BufferPool.freeByteBuffer(buf);
        }
    }
}
