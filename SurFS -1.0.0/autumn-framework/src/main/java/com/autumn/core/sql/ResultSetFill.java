package com.autumn.core.sql;

import com.autumn.core.log.LogFactory;
import com.autumn.core.log.Logger;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title: WEB框架</p>
 *
 * <p>Description: 从resultset注入</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class ResultSetFill {

    /**
     * 将记录集组装为类实体
     *
     * @param crs ResultSet 记录集,从当前行组装
     * @param cls Class 要组装的类
     * @return Object 类实体
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    public static Object assemble(ResultSet crs, Class cls) throws InstantiationException, IllegalAccessException, SQLException {
        Object object;
        object = cls.newInstance();
        assemble(crs, object);
        return object;
    }

    /**
     * 将记录集组装为类实体
     *
     * @param crs ResultSet 记录集,从当前行组装
     * @param object Object 要组装的类实体,更新实体参数
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void assemble(ResultSet crs, Object object) throws SQLException {
        ResultSetFill rsf = new ResultSetFill(crs, object);
        rsf.fill();
    }
    private ResultSet rs = null;
    private Object object = null;
    private HashMap<String, Method> methods = new HashMap<String, Method>();
    private Logger log = null;

    public ResultSetFill(ResultSet rs, Object object) {
        this.rs = rs;
        this.object = object;
        fillMethods();
    }

    /**
     * 获取日志
     *
     * @return Logger
     */
    private Logger getLogger() {
        if (log == null) {
            log = LogFactory.getLogger().getLogger(object.getClass());
        }
        return log;
    }

    /**
     * 获取函数
     */
    private void fillMethods() {
        Method[] mym = object.getClass().getDeclaredMethods();
        for (Method m : mym) {
            String namestr = m.getName().toLowerCase();
            if (namestr.startsWith("set")) {
                namestr = namestr.substring(3);
                if (m.getParameterTypes().length == 1
                        && m.getReturnType().getName().equalsIgnoreCase("void")) {
                    methods.put(namestr, m);
                }

            }
        }
    }

    /**
     * 注入
     *
     * @throws SQLException
     */
    public void fill() throws SQLException {
        ResultSetMetaData rsmd = null;
        rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        for (int ii = 1; ii <= count; ii++) {
            String colname = rsmd.getColumnName(ii);
            Method m = methods.get(colname.toLowerCase());
            if (m == null) {
                continue;
            }
            Class type = m.getParameterTypes()[0];
            try {
                setFieldValue(m, type, ii);
            } catch (Exception e) {
                getLogger().error("给[{0}.{1}]赋值失败:{2}", new Object[]{object.getClass().getName(), colname, e});
            }
        }
    }

    /**
     * 给java成员赋值
     *
     * @param m
     * @param typestr
     * @param col
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws SQLException
     * @throws IOException
     */
    private void setFieldValue(Method m, Class typestr, int col) throws
            IllegalAccessException, InvocationTargetException, SQLException, IOException {
        if (typestr == String.class) {
            m.invoke(object, new Object[]{JdbcUtils.getResultSetStringValue(rs, col)});
        } else if (typestr == int.class || typestr == Integer.class) { //int
            m.invoke(object, new Object[]{Integer.valueOf(rs.getInt(col))});
        } else if (typestr == long.class || typestr == Long.class) { //long
            m.invoke(object, new Object[]{Long.valueOf(rs.getLong(col))});
        } else if (typestr == float.class || typestr == Float.class) { //float
            m.invoke(object, new Object[]{Float.valueOf(rs.getFloat(col))});
        } else if (typestr == double.class || typestr == Double.class) { //double
            m.invoke(object, new Object[]{Double.valueOf(rs.getDouble(col))});
        } else if (typestr == boolean.class || typestr == Boolean.class) { //boolean
            m.invoke(object, new Object[]{Boolean.valueOf(rs.getBoolean(col))});
        } else if (typestr == short.class || typestr == Short.class) { //short
            m.invoke(object, new Object[]{Short.valueOf(rs.getShort(col))});
        } else if (typestr == Date.class || typestr == Timestamp.class) { //date
            m.invoke(object, new Object[]{rs.getTimestamp(col)});
        } else if (typestr == BigDecimal.class) { //BigDecimal
            m.invoke(object, new Object[]{rs.getBigDecimal(col)});
        } else if (typestr == BigInteger.class) { //BigInteger
            BigDecimal db = rs.getBigDecimal(col);
            if (db != null) {
                m.invoke(object, new Object[]{db.toBigInteger()});
            } else {
                m.invoke(object, new Object[]{null});
            }
        } else if (typestr == byte[].class || typestr == Byte[].class) {//byte[]
            m.invoke(object, new Object[]{rs.getBytes(col)});
        } else if (typestr == byte.class || typestr == Byte.class) {//byte
            m.invoke(object, new Object[]{Byte.valueOf(rs.getByte(col))});
        } else {
            m.invoke(object, new Object[]{rs.getObject(col)});
        }
    }

    /**
     * javabean
     *
     * @return Object
     */
    public Object getObject() {
        return object;
    }
}
