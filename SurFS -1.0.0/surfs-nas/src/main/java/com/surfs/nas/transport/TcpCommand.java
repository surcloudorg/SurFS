/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.transport;

import java.io.IOException;

public abstract class TcpCommand {

    private final byte commandType;
    private final int sequence;

    public TcpCommand(byte commandType, int sequence) {
        this.commandType = commandType;
        this.sequence = sequence;
    }

    /**
     * decode
     *
     * @param m_in
     * @throws java.io.IOException
     */
    protected abstract void read(TcpCommandDecoder m_in) throws IOException;

    /**
     * encode
     *
     * @param m_out
     * @throws java.io.IOException
     */
    protected abstract void write(TcpCommandEncoder m_out) throws IOException;

    /**
     * @return the cmdtype
     */
    public byte getCommandType() {
        return commandType;
    }

    /**
     * @return the seq
     */
    public int getSequence() {
        return sequence;
    }
}
