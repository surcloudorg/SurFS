/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.transport;

import com.surfs.nas.util.BufferPool;
import com.surfs.nas.GlobleProperties;
import com.surfs.nas.client.Node;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

public class TcpClient extends Thread {

    private static final Logger log = Logger.getLogger(TcpClient.class);

    SocketChannel channel;
    final Map<Integer, TcpRequest> requestMap = new ConcurrentHashMap<>();
    private final Node node;
    private TcpResponseDecoder m_in = null;
    private TcpRequestEncoder m_out = null;
    private int status = 0;

    public TcpClient(Node node) {
        this.node = node;
    }

    /**
     *
     * @return
     */
    public boolean isConnected() {
        synchronized (this) {
            if (status == 0) {
                ThreadPool.pool.execute(this);
                status = 1;
            }
            try {
                if (m_out == null) {
                    this.wait(getGlobleProperties().getReadTimeout() * 1000);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return m_out != null;
        }
    }

    /**
     *
     * @param tr
     * @return
     * @throws IOException
     */
    public TcpResponse get(TcpRequest tr) throws IOException {
        if (!getNode().isReady()) {
            throw new SocketTimeoutException("connect failed!");
        }
        if (!isConnected()) {
            throw new SocketTimeoutException("connect timeout!");
        }
        requestMap.put(tr.getSequence(), tr);
        synchronized (this) {
            m_out.addTcpResponse(tr, getGlobleProperties().getReadTimeout());
        }
        try {
            return tr.getResponse(getGlobleProperties().getReadTimeout() * 1000);
        } finally {
            requestMap.remove(tr.getSequence());
        }
    }

    public boolean isDestory() {
        return status == 2;
    }


    public void destory() {
        synchronized (this) {
            status = 2;
        }
        close();
    }

    @Override
    public void run() {
        int readtimeout = 0;
        log.info("[" + getNode().getNodeProperties().getServerHost() + "]connector start...");
        ByteBuffer buf = null;
        while (status == 1) {
            try {
                connect();
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
                } else if (count == 0) {
                    throw new SocketTimeoutException("no data!");
                } else {
                    close();
                }
            } catch (SocketTimeoutException se) {
                readtimeout = readtimeout + getGlobleProperties().getReadTimeout() * 1000;
                if (readtimeout > 1000 * 60 * 3) {
                    log.error("[" + getNode().getNodeProperties().getServerHost() + "]read timeout:" + se.getMessage());
                    close();
                }
            } catch (InterruptedException ex) {
                break;
            } catch (Throwable e) {
                log.error("[" + getNode().getNodeProperties().getServerHost() + "]read err:" + e.getMessage());
                close();
            }
        }
        BufferPool.freeByteBuffer(buf);
        close();
        log.info("[" + getNode().getNodeProperties().getServerHost() + "]connector closed!");
    }

    /**
     * Close the packet handler
     */
    public void close() {
        synchronized (this) {
            if (channel != null) {
                try {
                    channel.close();
                } catch (Exception ex) {
                }
                channel = null;
            }
            if (m_out != null) {
                m_out.shutdown();
                m_out = null;
            }
            if (m_in != null) {
                m_in.shutdown();
                m_in = null;
            }
        }
        List<TcpRequest> ls = new ArrayList<>(requestMap.values());
        for (TcpRequest tr : ls) {
            tr.weekup();
        }
    }

    /**
     * connect
     */
    private void connect() throws IOException, InterruptedException {
        synchronized (this) {
            if (channel == null) {
                try {
                    channel = SocketChannel.open();
                    channel.configureBlocking(true);
                    SocketAddress socketAddress = new InetSocketAddress(getNode().getNodeProperties().getServerHost(), getNode().getNodeProperties().getPort());
                    channel.connect(socketAddress);
                    TcpServer.setSocketConfig(getGlobleProperties(), channel.socket());
                    m_in = new TcpResponseDecoder(this);
                    m_out = new TcpRequestEncoder(this);
                    ThreadPool.pool.execute(m_in);
                    ThreadPool.pool.execute(m_out);
                    log.info("[" + getNode().getNodeProperties().getServerHost() + "]connect successfully!");
                    getNode().setState(true);
                    this.notify();
                } catch (Throwable e) {
                    channel = null;
                    getNode().setState(false);
                    sleep(getGlobleProperties().getConnectTimeout() * 1000);
                    throw e instanceof IOException ? (IOException) e : new IOException(e);
                }
            }
        }
    }

    public GlobleProperties getGlobleProperties() {
        return getNode().getGlobleProperties();
    }

    /**
     * @return the node
     */
    public Node getNode() {
        return node;
    }
}
