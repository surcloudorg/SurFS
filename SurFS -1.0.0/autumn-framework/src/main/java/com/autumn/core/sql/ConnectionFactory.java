package com.autumn.core.sql;

import com.autumn.core.log.LogFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.naming.*;

/**
 * <p>Title: 数据库连接工厂</p>
 *
 * <p>Description:创建，查找，销毁数据源</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ConnectionFactory {

    public static final String systemSourceName = "SystemSource";
    //该哈希表用来保存数据源名和连接池对象的关系表
    private static final HashMap<String, SmartDataSource> connectionPools = new HashMap<String, SmartDataSource>(2, 0.75F);

    /**
     * 获取数据源
     *
     * @param dataSourceName
     * @return SmartDataSource
     * @throws NameNotFoundException
     */
    public static SmartDataSource lookup(String dataSourceName) throws NameNotFoundException {
        SmartDataSource ds;
        synchronized (connectionPools) {
            ds = connectionPools.get(dataSourceName);
            if (ds == null) {
                throw new NameNotFoundException(dataSourceName);
            }
        }
        return ds;
    }

    /**
     * 绑定数据源
     *
     * @param param
     * @return SmartDataSource
     * @throws NameAlreadyBoundException
     * @throws ClassNotFoundException
     */
    public static SmartDataSource bind(ConnectionParam param) throws NameAlreadyBoundException, ClassNotFoundException {
        SmartDataSource source;
        String name = param.getJndi();
        try {
            source = lookup(name);
            throw new NameAlreadyBoundException(name);
        } catch (NameNotFoundException e) {
            source = new SmartDataSource(param);
            try {
                source.initConnection();
            } catch (SortedSQLException ex) {
            }
            synchronized (connectionPools) {
                connectionPools.put(name, source);
                bindContext(name, source);
            }
        }
        return source;
    }

    /**
     * 重新绑定数据源
     *
     * @param param
     * @return SmartDataSource
     * @throws NameAlreadyBoundException
     * @throws ClassNotFoundException
     * @throws SortedSQLException
     */
    public static SmartDataSource rebind(ConnectionParam param) throws NameAlreadyBoundException, ClassNotFoundException, SortedSQLException {
        String name = param.getJndi();
        try {
            SmartDataSource ds = lookup(name);
            if (!ds.getConnParam().equal(param)) {
                unbind(name);
                return bind(param);
            } else {
                ds.setConnParam(param);
                return ds;
            }
        } catch (NameNotFoundException e) {
            return bind(param);
        }
    }

    /**
     * 删除绑定数据源
     *
     * @param dataSourceName
     * @throws NameNotFoundException
     */
    public static void unbind(String dataSourceName) throws NameNotFoundException {
        SmartDataSource dataSource = lookup(dataSourceName);
        dataSource.close();
        synchronized (connectionPools) {
            unbindContext(dataSourceName);
            connectionPools.remove(dataSourceName);
        }
    }

    /**
     * 将连接池绑定到命名空间
     *
     * @param dataSourceName
     * @param obj
     */
    public static void bindContext(String dataSourceName, SmartDataSource obj) {
        try {
            Context ctx = new InitialContext();
            ctx.bind(dataSourceName, obj);
        } catch (NamingException ex) {
            LogFactory.trace("绑定连接池(" + dataSourceName + ")发生错误", ex, ConnectionFactory.class);
        }

    }

    /**
     * 将连接池从命名空间解除绑定
     *
     * @param dataSourceName
     */
    public static void unbindContext(String dataSourceName) {
        try {
            Context ctx = new InitialContext();
            ctx.unbind(dataSourceName);
        } catch (NamingException ex) {
            LogFactory.trace("解除连接池(" + dataSourceName + ")的绑定发生错误", ex, ConnectionFactory.class);
        }
    }

    /**
     * 关闭所有连接池
     */
    public static void stop() {
        List<String> w = new ArrayList<String>(connectionPools.keySet());
        for (String str : w) {
            try {
                unbind(str);
            } catch (NameNotFoundException ex) {
            }
        }
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
        }
    }

    /**
     * @return List<DataSourceImpl>
     */
    public static List<SmartDataSource> getPoolsElement() {
        List<SmartDataSource> list = new ArrayList<SmartDataSource>(connectionPools.values());
        return list;
    }

    /**
     * 获取连接池名
     *
     * @return List<String>
     */
    public static List<String> getPoolsKeys() {
        return new ArrayList<String>(connectionPools.keySet());
    }

    /**
     * 获取连接
     *
     * @param cls
     * @return Connection
     */
    public static Connection getConnect(Class cls) {
        return getConnect(systemSourceName, cls);
    }

    /**
     * 获取连接
     *
     * @param jndi
     * @param cls
     * @return Connection
     */
    public static Connection getConnect(String jndi, Class cls) {
        if (cls == null) {
            LogFactory.error("获取连接池必须签名!");
            return null;
        }
        return getConnect(jndi, cls.getName());
    }

    /**
     * 获取连接
     *
     * @param jndi String
     * @return Connection
     */
    private static Connection getConnect(String jndi, String classname) {
        if (jndi == null) {
            LogFactory.error("连接池名不能为空!", classname);
            return null;
        }
        try {
            SmartDataSource ds = ConnectionFactory.lookup(jndi);
            return ds.getConnection(classname);
        } catch (SortedSQLException ex) {
        } catch (NameNotFoundException e) {
            LogFactory.error("没找到连接池(" + jndi + ")", classname);
        }
        return null;
    }
}
