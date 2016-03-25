package com.autumn.core.sql;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.TextPrinter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDriver;

/**
 * <p>
 * Title:轻量的DataSource实现</p>
 *
 * <p>
 * Description: 轻量的DataSource实现</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class SmartDataSource implements DataSource {

    private static int LOGIN_TIMEOUT = 30;//登录、访问超时
    protected final List<ProxyConnection> conns = new ArrayList<ProxyConnection>();//链接队列
    private ConnectionParam connParam = null;//链接参数
    private Timer checkTimer = null;//过期检测线程
    private boolean autoCommit = true;

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        getConnParam().setUser(username);
        getConnParam().setPassword(password);
        return getConnection("org.hibernate.*");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection("org.hibernate.*");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return new PrintWriter(new TextPrinter(System.out, LogFactory.getLogger(SmartDataSource.class)));
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return LOGIN_TIMEOUT;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        LOGIN_TIMEOUT = seconds;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 构造函数
     *
     * @param connParam ConnectionParam
     * @throws java.lang.ClassNotFoundException
     */
    public SmartDataSource(ConnectionParam connParam) throws ClassNotFoundException {
        this.connParam = connParam;
        if (this.connParam.getDriver() == null) {//检查驱动是否存在
            Class.forName(EmbeddedDriver.class.getName());
            this.connParam.setTestsql(null);
        } else {
            Class.forName(this.connParam.getDriver());
        }
        DriverManager.setLoginTimeout(LOGIN_TIMEOUT);
        checkTimer = new Timer(true);
        checkTimer.schedule(new DataSourceChecker(this), 0, 1000 * 60);
    }

    /**
     * 初始化连接池
     *
     * @throws com.autumn.core.sql.SortedSQLException
     */
    protected void initConnection() throws SortedSQLException {
        Connection con = null;
        try {
            con = this.getConnection(SmartDataSource.class.getName());
            if (connParam.getTestsql() != null) { //测试语句是否有效
                JdbcPerformer.executeQuery(con, connParam.getTestsql());
            }
            try {
                autoCommit = con.getAutoCommit();
            } catch (SQLException ex) {//可能不支持事务
            }
        } catch (SortedSQLException r) {
            throw r;
        } finally {
            JdbcUtils.closeConnect(con);
        }
    }

    /**
     * 获取连接
     *
     * @param classname
     * @return Connection
     * @throws com.autumn.core.sql.SortedSQLException
     */
    protected Connection getConnection(String classname) throws SortedSQLException {
        Connection conn = getFreeConnection(classname);//首先从连接池中找出空闲的对象
        if (conn != null) {
            return conn;
        }
        synchronized (conns) {
            if (conns.size() >= getConnParam().getMaxConnection()) {
                try {
                    conns.wait(1000 * 30);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    LogFactory.fatal("连接池({0})已满,不能创建连接！", new Object[]{getConnParam().getJndi()}, classname);
                    SortedSQLException se = new SortedSQLException(new SQLException("连接池满,不能创建连接！"));
                    se.exceptionType = SortedSQLException.DataAccessResourceFailure;
                    throw se;
                }
            } else {
                Connection conn2 = null;
                try {
                    conn2 = getConnParam().getConnect();
                } catch (SQLException r) {
                    LogFactory.fatal("连接池({0})建立连接失败：{1}", new Object[]{getConnParam().getJndi(), r}, classname);
                    SortedSQLException se = new SortedSQLException(r);
                    se.exceptionType = SortedSQLException.DataAccessResourceFailure;
                    throw se;
                }
                ProxyConnection _conn = new ProxyConnection(conn2, this);//代理将要返回的连接对象
                _conn.setClassName(classname);
                conns.add(_conn);
                LogFactory.warn("连接池({0})建立新连接！当前连接数：{1}", new Object[]{getConnParam().getJndi(), conns.size()}, classname);
                return _conn.getConnection();
            }
        }
        return getConnection(classname);
    }

    /**
     * 清除连接
     *
     * @param con ProxyConnection
     */
    protected void removeConnection(ProxyConnection con) {
        JdbcUtils.closeConnect(con.getTargetConnection());
        synchronized (conns) {
            conns.remove(con);
        }
    }

    /**
     * 从连接池中取一个空闲的连接
     *
     * @param classname
     * @return Connection
     */
    protected Connection getFreeConnection(String classname) {
        Connection conn;
        ProxyConnection _conn;
        synchronized (conns) {
            for (ProxyConnection pc : conns) {
                _conn = pc;
                if (!_conn.isInUse()) {
                    if (!_conn.isAvalid()) {
                        LogFactory.warn("连接池({0})测试指令：{1}失败！", new Object[]{getConnParam().getJndi(), getConnParam().getTestsql()}, classname);
                        removeConnection(_conn);
                        return getFreeConnection(classname);
                    } else {
                        conn = _conn.getConnection();
                        _conn.setInUse(true);
                        _conn.setClassName(classname);
                        return conn;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取连接总数
     *
     * @return int
     */
    public int getConnectionCount() {
        return conns.size();
    }

    /**
     * 获取正在使用的连接数
     *
     * @return 连接数
     */
    public int getFreeConCount() {
        int count = 0;
        synchronized (conns) {
            for (ProxyConnection pc : conns) {
                if (!pc.isInUse()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 获取正在使用的连接数
     *
     * @return 连接数
     */
    public int getUseConCount() {
        int count = 0;
        synchronized (conns) {
            for (ProxyConnection pc : conns) {
                if (pc.isInUse()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 关闭连接池
     */
    public void close() {
        synchronized (conns) {
            for (ProxyConnection pc : conns) {
                JdbcUtils.closeConnect(pc.getTargetConnection());
            }
        }
        conns.clear();
        if (checkTimer != null) {
            checkTimer.cancel();
        }
    }

    /**
     * 获取连接池参数
     *
     * @return ConnectionParam
     */
    public ConnectionParam getConnParam() {
        return connParam;
    }

    /**
     * 获取所有连接
     *
     * @return List
     */
    public List<ProxyConnection> getConns() {
        return conns;
    }

    /**
     * @param connParam the connParam to set
     */
    public void setConnParam(ConnectionParam connParam) {
        this.connParam = connParam;
    }

    /**
     * @return the autoCommit
     */
    public boolean isAutoCommit() {
        return autoCommit;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
