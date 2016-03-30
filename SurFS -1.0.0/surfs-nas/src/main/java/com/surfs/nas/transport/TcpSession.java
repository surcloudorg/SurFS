/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.transport;

import com.surfs.nas.util.BufferPool;
import com.surfs.nas.protocol.TestSpeedRequest;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.apache.log4j.Logger;

public class TcpSession extends Thread {

    private static final Logger log = Logger.getLogger(TcpSession.class);
    final TcpServer server;
    final SocketChannel channel;

    private final String remoteAddress;
    private final long createtime = System.currentTimeMillis();
    private long activetime = System.currentTimeMillis();
    private TcpRequestDecoder m_in;
    private TcpResponseEncoder m_out;
    private TestSpeedRequest.TestSpeedSession testSpeedSession;

    public TcpSession(TcpServer server, SocketChannel channel) throws IOException {
        this.server = server;
        this.channel = channel;
        TcpServer.setSocketConfig(server.mgr.getGlobleProperties(), channel.socket());
        channel.configureBlocking(true);
        this.remoteAddress = channel.getRemoteAddress().toString();
    }

    /**
     *
     * @param resp
     */
    public void sendMessage(TcpResponse resp) {
        try {
            m_out.addTcpResponse(resp, server.mgr.getGlobleProperties().getReadTimeout());
        } catch (IOException r) {//ignore
        }
    }

    private boolean exiting = false;
    private Thread currentThread = null;

    @Override
    public void run() {
        currentThread = Thread.currentThread();
        log.info("[" + getRemoteAddress() + "]connected...");
        server.getMonitor().addSock(this);
        m_in = new TcpRequestDecoder(this);
        ThreadPool.pool.execute(m_in);
        m_out = new TcpResponseEncoder(this);
        ThreadPool.pool.execute(m_out);
        ByteBuffer buf = null;
        int readtimeout = 0;
        while (!exiting) {
            try {
                if (buf == null) {
                    buf = BufferPool.getByteBuffer();
                }
                int count = channel.read(buf);
                if (count > 0) {
                    readtimeout = 0;
                    buf.position(0);
                    buf.limit(count);
                    m_in.addByteBuffer(buf);
                    buf = null;
                    activetime = System.currentTimeMillis();
                } else if (count == 0) {
                    throw new SocketTimeoutException("no data!");
                } else {//-1
                    break;
                }
            } catch (InterruptedException e) {
                break;
            } catch (SocketTimeoutException se) {
                readtimeout = readtimeout + server.mgr.getGlobleProperties().getReadTimeout() * 1000;
                if (readtimeout > 1000 * 60 * 3) {
                    log.error("[" + getRemoteAddress() + "]read timeout:" + se.getMessage());
                    break;
                }
            } catch (Throwable r) {
                log.error("[" + getRemoteAddress() + "]read err:" + r.getMessage());
                break;
            }
        }
        BufferPool.freeByteBuffer(buf);
        close();
        server.getMonitor().removeSock(this);
        m_in.shutdown();
        m_out.shutdown();
        log.info("[" + getRemoteAddress() + "]socket closed!");
    }

    /**
     * Close socket
     */
    public void close() {
        exiting = true;
        if (currentThread != null) {
            currentThread.interrupt();
        }
        if (channel.isConnected()) {
            try {
                channel.close();
            } catch (Exception ex) {
            }
        }
    }

    /**
     * @return the testSpeedSession
     */
    public TestSpeedRequest.TestSpeedSession getTestSpeedSession() {
        return testSpeedSession;
    }

    /**
     * @param testSpeedSession the testSpeedSession to set
     */
    public void setTestSpeedSession(TestSpeedRequest.TestSpeedSession testSpeedSession) {
        this.testSpeedSession = testSpeedSession;
    }

    /**
     * @return the createtime
     */
    public long getCreatetime() {
        return createtime;
    }

    /**
     * @return the activetime
     */
    public long getActivetime() {
        return activetime;
    }

    /**
     * @return the remoteAddress
     */
    public String getRemoteAddress() {
        return remoteAddress;
    }

}
