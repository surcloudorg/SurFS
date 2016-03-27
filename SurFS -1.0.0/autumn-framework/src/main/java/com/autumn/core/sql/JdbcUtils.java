/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.sql;

import com.autumn.core.log.LogFactory;
import com.autumn.util.IOUtils;
import com.autumn.util.TextUtils;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * <p>
 * Title: JDBC操作工具</p>
 *
 * <p>
 * Description: 关闭连接，获取记录列值,设置参数...</p>
 *
 * <p>
 * Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>
 * Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public abstract class JdbcUtils {

    private static final Map<Class, Integer> javaTypeToSqlTypeMap = new HashMap<Class, Integer>(32);
    protected static final int TYPE_UNKNOWN = Integer.MIN_VALUE;

    static {
        //javaTypeToSqlTypeMap.put(boolean.class, new Integer(Types.BOOLEAN));
        //javaTypeToSqlTypeMap.put(Boolean.class, new Integer(Types.BOOLEAN));
        javaTypeToSqlTypeMap.put(byte.class, Types.TINYINT);
        javaTypeToSqlTypeMap.put(Byte.class, Types.TINYINT);
        javaTypeToSqlTypeMap.put(short.class, Types.SMALLINT);
        javaTypeToSqlTypeMap.put(Short.class, Types.SMALLINT);
        javaTypeToSqlTypeMap.put(int.class, Types.INTEGER);
        javaTypeToSqlTypeMap.put(Integer.class, Types.INTEGER);
        javaTypeToSqlTypeMap.put(long.class, Types.BIGINT);
        javaTypeToSqlTypeMap.put(Long.class, Types.BIGINT);
        javaTypeToSqlTypeMap.put(BigInteger.class, Types.BIGINT);
        javaTypeToSqlTypeMap.put(float.class, Types.FLOAT);
        javaTypeToSqlTypeMap.put(Float.class, Types.FLOAT);
        javaTypeToSqlTypeMap.put(double.class, Types.DOUBLE);
        javaTypeToSqlTypeMap.put(Double.class, Types.DOUBLE);
        javaTypeToSqlTypeMap.put(BigDecimal.class, Types.DECIMAL);
        javaTypeToSqlTypeMap.put(java.sql.Date.class, Types.DATE);
        javaTypeToSqlTypeMap.put(java.sql.Time.class, Types.TIME);
        javaTypeToSqlTypeMap.put(java.sql.Timestamp.class, Types.TIMESTAMP);
        javaTypeToSqlTypeMap.put(Blob.class, Types.BLOB);
        javaTypeToSqlTypeMap.put(Clob.class, Types.CLOB);
    }

    /**
     * 获取java类型对应的sql.type
     *
     * @param javaType
     * @return int
     */
    public static int javaTypeToSqlParameterType(Class javaType) {
        Integer sqlType = javaTypeToSqlTypeMap.get(javaType);
        if (sqlType != null) {
            return sqlType;
        }
        if (Number.class.isAssignableFrom(javaType)) {
            return Types.NUMERIC;
        }
        if (JdbcUtils.isStringValue(javaType)) {
            return Types.VARCHAR;
        }
        if (JdbcUtils.isDateValue(javaType) || Calendar.class.isAssignableFrom(javaType)) {
            return Types.TIMESTAMP;
        }
        return TYPE_UNKNOWN;
    }

    /**
     * 设置null参数值
     *
     * @param ps
     * @param paramIndex
     * @param sqlType
     * @throws SQLException
     */
    public static void setNull(PreparedStatement ps, int paramIndex, int sqlType)
            throws SQLException {
        if (sqlType == TYPE_UNKNOWN) {
            boolean useSetObject = false;
            sqlType = Types.NULL;
            try {
                DatabaseMetaData dbmd = ps.getConnection().getMetaData();
                String databaseProductName = dbmd.getDatabaseProductName();
                String jdbcDriverName = dbmd.getDriverName();
                if (databaseProductName.startsWith("Informix") || jdbcDriverName.startsWith("Microsoft SQL Server")) {
                    useSetObject = true;
                } else if (databaseProductName.startsWith("DB2") || jdbcDriverName.startsWith("jConnect") || jdbcDriverName.startsWith("SQLServer") || jdbcDriverName.startsWith("Apache Derby")) {
                    sqlType = Types.VARCHAR;
                }
            } catch (Throwable ex) {
            }
            if (useSetObject) {
                ps.setObject(paramIndex, null);
            } else {
                ps.setNull(paramIndex, sqlType);
            }
        } else {
            ps.setNull(paramIndex, sqlType);
        }
    }

    /**
     * 设置参数值
     *
     * @param ps
     * @param paramIndex
     * @param inValue
     * @param sqlType
     * @throws SQLException
     */
    public static void setValue(PreparedStatement ps, int paramIndex, Object inValue, int sqlType) throws SQLException {
        if ((sqlType == Types.VARCHAR || sqlType == Types.LONGVARCHAR) && isStringValue(inValue.getClass())) {// ||(sqlType == Types.CLOB )
            ps.setString(paramIndex, inValue.toString());
        } else if (sqlType == Types.DECIMAL || sqlType == Types.NUMERIC) {
            if (inValue instanceof BigDecimal) {
                ps.setBigDecimal(paramIndex, (BigDecimal) inValue);
            } else {
                ps.setObject(paramIndex, inValue, sqlType);
            }
        } else if (sqlType == Types.DATE) {
            if (inValue instanceof java.util.Date) {
                if (inValue instanceof java.sql.Date) {
                    ps.setDate(paramIndex, (java.sql.Date) inValue);
                } else {
                    ps.setDate(paramIndex, new java.sql.Date(((java.util.Date) inValue).getTime()));
                }
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setDate(paramIndex, new java.sql.Date(cal.getTime().getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue, Types.DATE);
            }
        } else if (sqlType == Types.TIME) {
            if (inValue instanceof java.util.Date) {
                if (inValue instanceof java.sql.Time) {
                    ps.setTime(paramIndex, (java.sql.Time) inValue);
                } else {
                    ps.setTime(paramIndex, new java.sql.Time(((java.util.Date) inValue).getTime()));
                }
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setTime(paramIndex, new java.sql.Time(cal.getTime().getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue, Types.TIME);
            }
        } else if (sqlType == Types.TIMESTAMP) {
            if (inValue instanceof java.util.Date) {
                if (inValue instanceof java.sql.Timestamp) {
                    ps.setTimestamp(paramIndex, (java.sql.Timestamp) inValue);
                } else {
                    ps.setTimestamp(paramIndex, new java.sql.Timestamp(((java.util.Date) inValue).getTime()));
                }
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime().getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue, Types.TIMESTAMP);
            }
        } else if (sqlType == TYPE_UNKNOWN) {
            if (JdbcUtils.isStringValue(inValue.getClass())) {
                ps.setString(paramIndex, inValue.toString());
            } else if (JdbcUtils.isDateValue(inValue.getClass())) {
                ps.setTimestamp(paramIndex, new java.sql.Timestamp(((java.util.Date) inValue).getTime()));
            } else if (inValue instanceof Calendar) {
                Calendar cal = (Calendar) inValue;
                ps.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime().getTime()), cal);
            } else {
                ps.setObject(paramIndex, inValue);
            }
        } else {
            ps.setObject(paramIndex, inValue, sqlType);
        }
    }

    /**
     * 创建物理连接
     *
     * @param driver
     * @param url
     * @param user
     * @param password
     * @return Connection
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection getConnect(String driver, String url, String user, String password) throws SQLException, ClassNotFoundException {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * 关闭记录集
     *
     * @param resultset ResultSet
     */
    public static void closeResultset(ResultSet resultset) {
        try {
            if (resultset != null) {
                resultset.close();
            }
        } catch (SQLException e1) {
            LogFactory.trace("关闭记录集出现错误！", e1, JdbcUtils.class);
        } catch (Throwable ex) {
            LogFactory.trace("关闭记录集出现运行时错误！", ex, JdbcUtils.class);
        }
    }

    /**
     * 关闭声明
     *
     * @param statement Statement
     */
    public static void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e1) {
            LogFactory.trace("关闭数据库声明出现错误！", e1, JdbcUtils.class);
        } catch (Throwable ex) {
            LogFactory.trace("关闭数据库声明出现运行时错误！", ex, JdbcUtils.class);
        }
    }

    /**
     * 关闭连接
     *
     * @param conn Connection
     */
    public static void closeConnect(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e1) {
            LogFactory.trace("关闭数据库连接出现错误！", e1, JdbcUtils.class);
        } catch (Throwable ex) {
            LogFactory.trace("关闭数据库连接出现运行时错误！", ex, JdbcUtils.class);
        }
    }

    /**
     * 获取记录集某列的值(指定返回类型)
     *
     * @param rs
     * @param index
     * @param requiredType 返回类型
     * @return Object<requiredType>
     * @throws SQLException
     */
    public static Object getResultSetValue(ResultSet rs, int index, Class requiredType) throws SQLException {
        if (requiredType == null) {
            return getResultSetValue(rs, index);
        }
        Object value;
        boolean wasNullCheck = false;
        if (String.class.equals(requiredType)) {
            value = rs.getString(index);
        } else if (boolean.class.equals(requiredType) || Boolean.class.equals(requiredType)) {
            value = rs.getBoolean(index);
            wasNullCheck = true;
        } else if (byte.class.equals(requiredType) || Byte.class.equals(requiredType)) {
            value = rs.getByte(index);
            wasNullCheck = true;
        } else if (short.class.equals(requiredType) || Short.class.equals(requiredType)) {
            value = rs.getShort(index);
            wasNullCheck = true;
        } else if (int.class.equals(requiredType) || Integer.class.equals(requiredType)) {
            value = rs.getInt(index);
            wasNullCheck = true;
        } else if (long.class.equals(requiredType) || Long.class.equals(requiredType)) {
            value = rs.getLong(index);
            wasNullCheck = true;
        } else if (float.class.equals(requiredType) || Float.class.equals(requiredType)) {
            value = rs.getFloat(index);
            wasNullCheck = true;
        } else if (double.class.equals(requiredType) || Double.class.equals(requiredType) || Number.class.equals(requiredType)) {
            value = rs.getDouble(index);
            wasNullCheck = true;
        } else if (byte[].class.equals(requiredType)) {
            value = rs.getBytes(index);
        } else if (java.sql.Date.class.equals(requiredType)) {
            value = rs.getDate(index);
        } else if (java.sql.Time.class.equals(requiredType)) {
            value = rs.getTime(index);
        } else if (java.sql.Timestamp.class.equals(requiredType) || java.util.Date.class.equals(requiredType)) {
            value = rs.getTimestamp(index);
        } else if (BigDecimal.class.equals(requiredType)) {
            value = rs.getBigDecimal(index);
        } else if (Blob.class.equals(requiredType)) {
            value = rs.getBlob(index);
        } else if (Clob.class.equals(requiredType)) {
            value = rs.getClob(index);
        } else {// Some unknown type desired -> rely on getObject.
            value = getResultSetValue(rs, index);
        }
        if (wasNullCheck && value != null && rs.wasNull()) {
            value = null;
        }
        return value;
    }

    /**
     * 获取记录集某列的值
     *
     * @param rs
     * @param index 列号
     * @return Object
     * @throws SQLException
     */
    public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;
        if (obj != null) {
            className = obj.getClass().getName();
        }
        if (className != null && ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className))) {
            obj = rs.getTimestamp(index);
        } else if (className != null && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if (obj != null && obj instanceof java.sql.Date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                obj = rs.getTimestamp(index);
            }
        }
        return obj;
    }

    /**
     * 获取记录集某列的值
     *
     * @param rs
     * @param colname
     * @return String
     * @throws SQLException
     * @throws java.io.IOException
     */
    public static String getResultSetStringValue(ResultSet rs, String colname) throws SQLException, IOException {
        return getResultSetStringValue(rs, rs.findColumn(colname));

    }

    /**
     * 获取记录集某列的值
     *
     * @param rs
     * @param index
     * @return String
     * @throws SQLException
     * @throws IOException
     */
    public static String getResultSetStringValue(ResultSet rs, int index) throws SQLException, IOException {
        Object obj = getResultSetValue(rs, index);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return TextUtils.Date2String((Date) obj);
        } else if (obj instanceof Clob) {
            return readStringClob((Clob) obj);
        } else if (obj instanceof Blob) {
            byte[] bytes = readBytesBlob((Blob) obj);
            return new String(bytes);
        } else if (obj instanceof Boolean) {
            if ((Boolean) obj) {
                return "1";
            } else {
                return "0";
            }
        } else {
            return obj.toString();
        }
    }

    /**
     * 是否支持批量操作
     *
     * @param con
     * @return boolean
     */
    public static boolean supportsBatchUpdates(Connection con) {
        try {
            DatabaseMetaData dbmd = con.getMetaData();
            if (dbmd != null) {
                if (dbmd.supportsBatchUpdates()) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            LogFactory.trace("检查JDBC是否支持批量操作遇到错误！", ex, JdbcUtils.class);
        }
        return false;
    }

    /**
     * 读取Blob
     *
     * @param content
     * @return byte[]
     * @throws SQLException
     * @throws IOException
     */
    public static byte[] readBytesBlob(Blob content) throws SQLException,
            IOException {
        if (content == null) {
            return null;
        }
        InputStream is = content.getBinaryStream();
        return IOUtils.read(is);
    }

    /**
     * 读取Clob
     *
     * @param content
     * @return String
     * @throws SQLException
     * @throws IOException
     */
    public static String readStringClob(Clob content) throws SQLException, IOException {
        if (content == null) {
            return null;
        }
        Reader reader = content.getCharacterStream();
        BufferedReader in = new BufferedReader(reader);
        StringBuilder rc = new StringBuilder();
        char[] chars = new char[256];
        int count;
        while ((count = in.read(chars)) > 0) {
            rc.append(chars, 0, count);
        }
        return rc.toString();
    }

    /**
     * 数据类型是否为数字
     *
     * @param sqlType
     * @return boolean
     */
    public static boolean isNumeric(int sqlType) {
        return Types.BIT == sqlType || Types.BIGINT == sqlType || Types.DECIMAL == sqlType
                || Types.DOUBLE == sqlType || Types.FLOAT == sqlType || Types.INTEGER == sqlType
                || Types.NUMERIC == sqlType || Types.REAL == sqlType || Types.SMALLINT == sqlType
                || Types.TINYINT == sqlType;
    }

    /**
     * 检查给定值是否是字符串类型
     *
     * @param inValueType
     * @return boolean
     */
    public static boolean isStringValue(Class inValueType) {
        return (CharSequence.class.isAssignableFrom(inValueType)
                || StringWriter.class.isAssignableFrom(inValueType));
    }

    /**
     * 检查给定值是否是日期类型
     *
     * @param inValueType
     * @return boolean
     */
    public static boolean isDateValue(Class inValueType) {
        return (java.util.Date.class.isAssignableFrom(inValueType)
                && !(java.sql.Date.class.isAssignableFrom(inValueType)
                || java.sql.Time.class.isAssignableFrom(inValueType)
                || java.sql.Timestamp.class.isAssignableFrom(inValueType)));
    }
}
