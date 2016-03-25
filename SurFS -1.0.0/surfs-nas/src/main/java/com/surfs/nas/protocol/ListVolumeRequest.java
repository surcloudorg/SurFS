package com.surfs.nas.protocol;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.transport.ErrorResponse;
import com.surfs.nas.transport.OriginRequest;
import com.surfs.nas.transport.OriginResponse;
import com.surfs.nas.transport.TcpCommandType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListVolumeRequest extends OriginRequest {

    private static final Logger log = LogFactory.getLogger(ListVolumeRequest.class);

    public ListVolumeRequest() {
        super(TcpCommandType.SYS_VOLUME_LS);
    }

    public ListVolumeRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }
    
    @Override
    public void run() {
        try {
            
            List<String> ls = new ArrayList<>(getServerSourceMgr().getVolumeMap().keySet());
            OriginResponse tr = new OriginResponse(this);
            tr.setObjects(ls);
            this.getSession().sendMessage(tr);
        } catch (Throwable e) {
            this.getSession().sendMessage(new ErrorResponse(this, e instanceof IOException ? (IOException) e : new IOException(e)));
        }
    }

}
