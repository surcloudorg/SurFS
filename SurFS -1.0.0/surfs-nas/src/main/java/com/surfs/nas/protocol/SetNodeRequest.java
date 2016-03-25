package com.surfs.nas.protocol;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.NodeProperties;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.NullResponse;
import com.surfs.nas.transport.OriginRequest;
import com.surfs.nas.transport.TcpCommandType;
import java.io.IOException;

public class SetNodeRequest extends OriginRequest {

    private static final Logger log = LogFactory.getLogger(SetNodeRequest.class);

    public SetNodeRequest() {
        super(TcpCommandType.SYS_NODE_SET);
    }

    public SetNodeRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    @Override
    public void run() {
        NodeProperties node = getObject(NodeProperties.class);
        try {
             
            getServerSourceMgr().updateNodeProperties(node);
            this.getSession().sendMessage(new NullResponse(this));
        } catch (Throwable e) {
            this.getSession().sendMessage(new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e)));
        }
    }

}
