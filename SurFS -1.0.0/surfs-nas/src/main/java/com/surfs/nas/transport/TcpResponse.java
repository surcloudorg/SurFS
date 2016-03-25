package com.surfs.nas.transport;

import com.surfs.nas.protocol.ReadResponse;
import com.surfs.nas.protocol.WriteResponse;
import static com.surfs.nas.transport.TcpCommandType.*;
import java.io.IOException;

public abstract class TcpResponse extends TcpCommand {

    public TcpResponse(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    public static TcpResponse newInstance(byte cmdid, int sequence) throws IOException {
        switch (cmdid) {
            case DELETE:
                return new NullResponse(cmdid, sequence);
            case WRITE:
                return new WriteResponse(cmdid, sequence);
            case READ:
                return new ReadResponse(cmdid, sequence);
            case TRUNC:
                return new WriteResponse(cmdid, sequence);
            case QUOTA:
                return new NullResponse(cmdid, sequence);
            case CLOSE:
                return new LongResponse(cmdid, sequence);
            case ERROR:
                return new ErrorResponse(cmdid, sequence);
            //system cmd    
            case SYS_SPEED_TEST:
                return new LongResponse(cmdid, sequence);
            case SYS_NODE_HANDLER:
                return new LongResponse(cmdid, sequence);
            case SYS_VOLUME_HANDLER:
                return new OriginResponse(cmdid, sequence);
            case SYS_VOLUME_SET:
                return new NullResponse(cmdid, sequence);
            case SYS_NODE_SET:
                return new NullResponse(cmdid, sequence);
            case SYS_VOLUME_SPACE_GET:
                return new OriginResponse(cmdid, sequence);
            case SYS_VOLUME_SCAN:
                return new NullResponse(cmdid, sequence);
            case SYS_VOLUME_SCAN_INIT:
                return new OriginResponse(cmdid, sequence);
            case SYS_VOLUME_INIT:
                return new OriginResponse(cmdid, sequence);
            case SYS_VOLUME_LS:
                return new OriginResponse(cmdid, sequence);
            case SYS_ACTIVE_TEST:
                return new NullResponse(cmdid, sequence);
        }
        throw new IOException("Invalid command:" + cmdid);
    }
}
