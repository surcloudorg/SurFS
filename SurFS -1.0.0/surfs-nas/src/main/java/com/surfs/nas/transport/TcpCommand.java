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
