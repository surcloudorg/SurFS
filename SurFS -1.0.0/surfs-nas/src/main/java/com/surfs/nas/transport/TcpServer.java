package com.surfs.nas.transport;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.surfs.nas.GlobleProperties;
import com.surfs.nas.server.ServerSourceMgr;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TcpServer extends Thread {

    private static final Logger log = LogFactory.getLogger(TcpServer.class);
    final ServerSourceMgr mgr;
    private TcpMonitor monitor;
    private ServerSocketChannel m_srvSock;
    private boolean exitsign = false;

    public static void setSocketConfig(GlobleProperties cfg, Socket sessSock) throws IOException {
        sessSock.setKeepAlive(true);
        sessSock.setTcpNoDelay(true);
        sessSock.setReuseAddress(true);
        sessSock.setSendBufferSize(cfg.getBlocksize() * 2 * 1024);
        sessSock.setReceiveBufferSize(cfg.getBlocksize() * 2 * 1024);
        sessSock.setSoTimeout(cfg.getConnectTimeout() * 1000);
        sessSock.setSoLinger(true, cfg.getReadTimeout());
        //sessSock.setPerformancePreferences(0, 0, 0);
        sessSock.setTrafficClass(0x02 | 0x08);
    }

    /**
     *
     * @param mgr
     * @throws IOException
     */
    public TcpServer(ServerSourceMgr mgr) throws IOException {
        this.mgr = mgr;
        init();
    }

    /**
     * init
     */
    private void init() throws IOException {
        this.monitor = new TcpMonitor();
        m_srvSock = ServerSocketChannel.open();  // new ServerSocket(ServerSourceMgr.getLocalPort(), 100);
        m_srvSock.configureBlocking(true);
        m_srvSock.socket().setReuseAddress(true);
        m_srvSock.socket().setSoTimeout(mgr.getGlobleProperties().getConnectTimeout() * 1000);
        m_srvSock.socket().bind(new InetSocketAddress(ServerSourceMgr.getLocalPort()));
        log.info("TcpServer start,port:" + ServerSourceMgr.getLocalPort());
    }

    @Override
    public void run() {
        while (!exitsign) {
            try {
                SocketChannel sessSock = m_srvSock.accept();
                TcpSession session = new TcpSession(this, sessSock);
                ThreadPool.pool.execute(session);
            } catch (IOException r) {
                log.error("accept err:" + r.getMessage());
            }
        }
    }

    public void shutdown() {
        exitsign = true;
        try {
            if (m_srvSock != null) {
                m_srvSock.close();
            }
        } catch (Exception ex) {
        }
        if (monitor != null) {
            monitor.shutdown();
        }
        ThreadPool.shutdown();
    }

    /**
     * @return the monitor
     */
    public TcpMonitor getMonitor() {
        return monitor;
    }
}
