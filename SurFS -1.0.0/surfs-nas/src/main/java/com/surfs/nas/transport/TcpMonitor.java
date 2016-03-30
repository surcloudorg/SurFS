/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.transport;

import com.surfs.nas.util.TextUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.log4j.Logger;

public class TcpMonitor {

    private static final Logger log = Logger.getLogger(TcpMonitor.class);
    private final Queue<TcpSession> socks = new ConcurrentLinkedQueue<>();

    /**
     *
     * @param sock
     */
    public void addSock(TcpSession sock) {
        int size = socks.size();
        if (size >= 10000) {
            log.fatal("connection number to reach the upper limit:10000");
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
