/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class DataSourceChecker extends TimerTask {

    private SmartDataSource sds = null;

    protected DataSourceChecker(SmartDataSource sds) {
        this.sds = sds;
    }

    @Override
    public void run() {
        int freecount = sds.getFreeConCount();
        List<ProxyConnection> tmpconns = new ArrayList<>(sds.conns);
        for (ProxyConnection _conn : tmpconns) {
            long curAccessTime = System.currentTimeMillis();
            if (_conn.isInUse()) {
                if (curAccessTime - _conn.lastAccessTime > sds.getConnParam().getTimeoutValue()) {
                }
            } else {
                if (freecount > sds.getConnParam().getMinConnection() && curAccessTime - _conn.lastAccessTime > 1000 * 60 * 30) {
                    sds.removeConnection(_conn);
                    freecount--;

                }
            }
        }
    }
}
