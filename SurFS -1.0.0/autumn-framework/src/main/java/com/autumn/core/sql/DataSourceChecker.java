package com.autumn.core.sql;

import com.autumn.core.log.LogFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * <p>Title:DataSource检查</p>
 *
 * <p>Description: 空闲连接超过MinConnection,清除 占用时间超过getTimeoutValue，清除</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class DataSourceChecker extends TimerTask {

    private SmartDataSource sds = null;

    protected DataSourceChecker(SmartDataSource sds) {
        this.sds = sds;
    }

    @Override
    public void run() {
        int freecount = sds.getFreeConCount();
        List<ProxyConnection> tmpconns = new ArrayList<ProxyConnection>(sds.conns);
        for (ProxyConnection _conn : tmpconns) {
            long curAccessTime = System.currentTimeMillis();
            if (_conn.isInUse()) {
                if (curAccessTime - _conn.lastAccessTime > sds.getConnParam().getTimeoutValue()) {
                    LogFactory.warn("{0}占用连接池({1})超过{2}ms！",
                            new Object[]{_conn.getClassName(), sds.getConnParam().getJndi(), sds.getConnParam().getTimeoutValue()},
                            _conn.getClassName());
                }
            } else {
                if (freecount > sds.getConnParam().getMinConnection() && curAccessTime - _conn.lastAccessTime > 1000 * 60 * 30) {
                    sds.removeConnection(_conn);//空闲30分钟释放
                    freecount--;
                    LogFactory.error("连接池({0})释放一个空闲连接(空闲30分钟)！",
                            new Object[]{sds.getConnParam().getJndi()}, DataSourceChecker.class);
                }
            }
        }
    }
}
