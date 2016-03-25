package com.surfs.nas.transport;

public class TcpRequestDecoder extends TcpCommandDecoder {

    private final TcpSession session;

    TcpRequestDecoder(TcpSession session) {
        this.session = session;
    }

    @Override
    public void decode() throws Throwable {
        byte cmdtype = this.readByte();
        int sequence = this.readInt();
        TcpRequest request = TcpRequest.newInstance(cmdtype,sequence);
        request.read(this);
        request.setSession(session);
        request.setServerSourceMgr(session.server.mgr);
        ThreadPool.pool.execute(request);
    }
}
