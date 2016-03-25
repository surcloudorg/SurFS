package com.surfs.nas.transport;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import com.autumn.util.TextUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TcpMonitor {

    private static final Logger log = LogFactory.getLogger(TcpMonitor.class);
    private final Queue<TcpSession> socks = new ConcurrentLinkedQueue<>();

    /**
     * 建立连接
     *
     * @param sock
     */
    public void addSock(TcpSession sock) {
        int size = socks.size();
        if (size >= 10000) {
            log.fatal("connection number to reach the upper limit:10000", TcpMonitor.class);
        }
        socks.add(sock);
    }

    /**
     * close
     *
     * @param sock
     */
    public void removeSock(TcpSession sock) {
        socks.remove(sock);
    }

    /**
     * close all connection
     */
    public void shutdown() {
        List<TcpSession> ls = new ArrayList<>(socks);
        for (TcpSession sess : ls) {
            sess.close();
        }
    }

    /**
     * file handler list
     *
     * @return String
     */
    @Override
    public String toString() {
        List<TcpSession> ls = new ArrayList<>(socks);
        StringBuilder sb = new StringBuilder("connection total:");
        sb.append(socks.size()).append("\r\n");
        for (TcpSession hp : ls) {
            sb.append(hp.getRemoteAddress());
            sb.append(",createtime:").append(TextUtils.Date2String(new Date(hp.getCreatetime())));
            sb.append(",activetime:").append(TextUtils.Date2String(new Date(hp.getActivetime())));
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
