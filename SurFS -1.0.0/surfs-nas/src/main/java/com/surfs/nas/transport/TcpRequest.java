/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.transport;

import com.surfs.nas.protocol.ActiveTestRequest;
import com.surfs.nas.protocol.CloseRequest;
import com.surfs.nas.protocol.DeleteRequest;
import com.surfs.nas.protocol.GetNodeHandlerRequest;
import com.surfs.nas.protocol.GetSpaceRequest;
import com.surfs.nas.protocol.GetVolumeHandlerRequest;
import com.surfs.nas.protocol.InitVolumeRequest;
import com.surfs.nas.protocol.ListVolumeRequest;
import com.surfs.nas.protocol.ReadRequest;
import com.surfs.nas.protocol.ScanDirectoryRequest;
import com.surfs.nas.protocol.ScanVolumeRequest;
import com.surfs.nas.protocol.SetNodeRequest;
import com.surfs.nas.protocol.SetVolumeRequest;
import com.surfs.nas.protocol.TestSpeedRequest;
import com.surfs.nas.protocol.TruncRequest;
import com.surfs.nas.protocol.UpdateQuotaRequest;
import com.surfs.nas.protocol.WriteRequest;
import com.surfs.nas.server.ServerSourceMgr;
import static com.surfs.nas.transport.TcpCommandType.CLOSE;
import static com.surfs.nas.transport.TcpCommandType.DELETE;
import static com.surfs.nas.transport.TcpCommandType.QUOTA;
import static com.surfs.nas.transport.TcpCommandType.READ;
import static com.surfs.nas.transport.TcpCommandType.SYS_ACTIVE_TEST;
import static com.surfs.nas.transport.TcpCommandType.SYS_NODE_HANDLER;
import static com.surfs.nas.transport.TcpCommandType.SYS_NODE_SET;
import static com.surfs.nas.transport.TcpCommandType.SYS_SPEED_TEST;
import static com.surfs.nas.transport.TcpCommandType.SYS_VOLUME_HANDLER;
import static com.surfs.nas.transport.TcpCommandType.SYS_VOLUME_INIT;
import static com.surfs.nas.transport.TcpCommandType.SYS_VOLUME_LS;
import static com.surfs.nas.transport.TcpCommandType.SYS_VOLUME_SCAN;
import static com.surfs.nas.transport.TcpCommandType.SYS_VOLUME_SCAN_INIT;
import static com.surfs.nas.transport.TcpCommandType.SYS_VOLUME_SET;
import static com.surfs.nas.transport.TcpCommandType.SYS_VOLUME_SPACE_GET;
import static com.surfs.nas.transport.TcpCommandType.TRUNC;
import static com.surfs.nas.transport.TcpCommandType.WRITE;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TcpRequest extends TcpCommand implements Runnable {

    private static final AtomicInteger sequenceMaker = new AtomicInteger(0);
    private ServerSourceMgr serverSourceMgr;
    private TcpSession session;
    private TcpResponse response;

    public TcpRequest(byte commandType, int sequence) {
        super(commandType, sequence);
    }

    public TcpRequest(byte commandType) {
        super(commandType, sequenceMaker.incrementAndGet());
    }

    public static TcpRequest newInstance(byte cmdid, int sequence) throws IOException {
        switch (cmdid) {
            case DELETE:
                return new DeleteRequest(cmdid, sequence);
            case WRITE:
                return new WriteRequest(cmdid, sequence);
            case READ:
                return new ReadRequest(cmdid, sequence);
            case TRUNC:
                return new TruncRequest(cmdid, sequence);
            case QUOTA:
                return new UpdateQuotaRequest(cmdid, sequence);
            case CLOSE:
                return new CloseRequest(cmdid, sequence);
            //system cmd    
            case SYS_SPEED_TEST:
                return new TestSpeedRequest(cmdid, sequence);
            case SYS_NODE_HANDLER:
                return new GetNodeHandlerRequest(cmdid, sequence);
            case SYS_VOLUME_HANDLER:
                return new GetVolumeHandlerRequest(cmdid, sequence);
            case SYS_VOLUME_SET:
                return new SetVolumeRequest(cmdid, sequence);
            case SYS_NODE_SET:
                return new SetNodeRequest(cmdid, sequence);
            case SYS_VOLUME_SPACE_GET:
                return new GetSpaceRequest(cmdid, sequence);
            case SYS_VOLUME_SCAN:
                return new ScanVolumeRequest(cmdid, sequence);
            case SYS_VOLUME_SCAN_INIT:
                return new ScanDirectoryRequest(cmdid, sequence);
            case SYS_VOLUME_INIT:
                return new InitVolumeRequest(cmdid, sequence);
            case SYS_VOLUME_LS:
                return new ListVolumeRequest(cmdid, sequence);
            case SYS_ACTIVE_TEST:
                return new ActiveTestRequest(cmdid, sequence);
        }
        throw new IOException("Invalid command:" + cmdid);
    }

    /**
     * @return the serverSourceMgr
     */
    public ServerSourceMgr getServerSourceMgr() {
        return serverSourceMgr;
    }

    /**
     * @param serverSourceMgr the serverSourceMgr to set
     */
    public void setServerSourceMgr(ServerSourceMgr serverSourceMgr) {
        this.serverSourceMgr = serverSourceMgr;
    }

    /**
     * @return the session
     */
    public TcpSession getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(TcpSession session) {
        this.session = session;
    }

    void weekup() {
        synchronized (this) {
            this.notify();
        }
    }

    /**
     * @param wait
     * @return the response
     * @throws java.io.IOException
     */
    public TcpResponse getResponse(int wait) throws IOException {
        try {
            synchronized (this) {
                if (response == null) {
                    this.wait(wait);
                    if (response == null) {
                        throw new SocketTimeoutException("Read timed out");
                    } else {
                        if (response instanceof ErrorResponse) {
                            ErrorResponse err = (ErrorResponse) response;
                            throw err.getException();
                        }
                    }
                }
            }
        } catch (InterruptedException ex) {
            throw new SocketTimeoutException(ex.getMessage());
        }
        return response;
    }

    public TcpResponse getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(TcpResponse response) {
        synchronized (this) {
            this.response = response;
            this.notify();
        }
    }
}
