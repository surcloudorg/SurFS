/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;

public abstract class JdbcTemplate {

    public static String ROWSET_IMPL_CLASS = "com.sun.rowset.CachedRowSetImpl";
    private DataSource dataSource = null;
    private static final ThreadLocal<List<Parameter>> params = new ThreadLocal<>();
    private int fetchSize = 0;
    private int maxRows = 0;
    private int queryTimeout = 0;
    private int fetchDirection = 0;

    /**
     *
     * @param datasourcename
     * @throws javax.naming.NameNotFoundException
     */
    public JdbcTemplate(String datasourcename) throws NameNotFoundException {
        this.dataSource = ConnectionFactory.lookup(datasourcename);
    }

    /**
     *
     * @param dataSource
     */
    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     *
     * @param cls Class
     * @throws java.lang.Exception
     */
    public void addNullParameter(Class cls) throws Exception {
        if (cls == null) {
            addParameter(JdbcUtils.TYPE_UNKNOWN, null);
        } else {
            addParameter(JdbcUtils.javaTypeToSqlParameterType(cls), null);
        }
    }

    /**
     *
     * @param value
     */
    public void addParameter(Object... value) {
        for (Object obj : value) {
            addParameter(obj);
        }
    }

    /**
     *
     * @param value
     */
    public void addParameter(Object value) {
        if (value == null) {
            addParameter(JdbcUtils.TYPE_UNKNOWN, value);
        } else {
            addParameter(JdbcUtils.javaTypeToSqlParameterType(value.getClass()), value);
        }
    }

    /**
     *
     *
     * @return List
     */
    private List<Parameter> getParams() {
        return getParams(true);
    }

    /**
     *
     * @param create
     * @return List
     */
    private List<Parameter> getParams(boolean create) {
        List<Parameter> ps = params.get();
        if (ps == null) {
            if (create) {
                ps = new ArrayList<>();
                params.set(ps);
            }
        }
        return ps;
    }

    /**
     *
     *
     * @param type
     * @param value
     */
    public void addParameter(int type, Object value) {
        Parameter p = new Parameter(type, value);
        List<Parameter> ps = getParams();
        ps.add(p);
    }

    public void clearParameters() {
        List<Parameter> ps = getParams(false);
        if (ps != null) {
            ps.clear();
        }
    }

    /**
     *
     * @param con
     * @param sql
     * @param param
     * @return PreparedStatement
     * @throws SQLException
     */
    protected PreparedStatement createPreparedStatement(Connection con, String sql, List<Parameter> param) throws SQLException {
        return createPreparedStatement(con, sql, false, param);
    }

    /**
     *
     * @param con
     * @param sql
     * @param autoGeneratedKeys
     * @param param
     * @return PreparedStatement
     * @throws SQLException
     */
    protected PreparedStatement createPreparedStatement(Connection con, String sql, boolean autoGeneratedKeys, List<Parameter> param) throws SQLException {
        PreparedStatement ps = null;
        try {
            if (autoGeneratedKeys) {
                ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            } else {
                ps = con.prepareStatement(sql);
            }
            if (fetchSize > 0) {
                ps.setFetchSize(fetchSize);
            }
            if (fetchDirection > 0) {
                ps.setFetchDirection(fetchDirection);
            }
            if (queryTimeout > 0) {
                ps.setQueryTimeout(queryTimeout);
            }
            if (maxRows > 0) {
                ps.setMaxRows(maxRows);
            }
            if (param != null) {
                for (int ii = 0, count = param.size(); ii < count; ii++) {
                    Parameter p = param.get(ii);
                    if (p.value == null) {
                        JdbcUtils.setNull(ps, ii + 1, p.type);
                    } else {
                        JdbcUtils.setValue(ps, ii + 1, p.value, p.type);
                    }
                }
            }
            return ps;
        } catch (SQLException r) {
            JdbcUtils.closeStatement(ps);
            throw r;
        }
    }

    /**
     *
     * @return Connection
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
        if (dataSource instanceof SmartDataSource) {
            SmartDataSource ds = (SmartDataSource) dataSource;
            return ds.getConnection(JdbcTemplate.class.getName());
        } else {
            return dataSource.getConnection();
        }
    }

    /**
     *
     * @param sql
     * @return ResultSet
     * @throws com.surfs.nas.sql.SortedSQLException
     */
    public ResultSet query(String sql) throws SortedSQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = createPreparedStatement(con, sql, getParams(false));
            ResultSet rs = ps.executeQuery();
            CachedRowSet cacherowset = (CachedRowSet) Class.forName(ROWSET_IMPL_CLASS).newInstance();
            cacherowset.populate(rs);
            return cacherowset;
        } catch (Exception r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(con, r);
        } finally {
            clearParameters();
            JdbcUtils.closeStatement(ps);
            JdbcUtils.closeConnect(con);
        }
    }

    /**
     *
     * @param sql
     * @return int
     * @throws SortedSQLException
     */
    public int update(String sql) throws SortedSQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = createPreparedStatement(con, sql, getParams(false));
            return ps.executeUpdate();
        } catch (Exception r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(con, r);
        } finally {
            clearParameters();
            JdbcUtils.closeStatement(ps);
            JdbcUtils.closeConnect(con);
        }
    }

    /**
     *
     * @param sql
     * @return long
     * @throws com.surfs.nas.sql.SortedSQLException
     */
    public long insert(String sql) throws SortedSQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = createPreparedStatement(con, sql, true, getParams(false));
            int rc = ps.executeUpdate();
            if (rc < 1) {
                return 0;
            } else {
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                return rs.getLong(1);
            }
        } catch (Exception r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(con, r);
        } finally {
            clearParameters();
            JdbcUtils.closeStatement(ps);
            JdbcUtils.closeConnect(con);
        }
    }

    /**
     * @return the fetchSize
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * @param fetchSize the fetchSize to set
     */
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * @return the maxRows
     */
    public int getMaxRows() {
        return maxRows;
    }

    /**
     * @param maxRows the maxRows to set
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    /**
     * @return the queryTimeout
     */
    public int getQueryTimeout() {
        return queryTimeout;
    }

    /**
     * @param queryTimeout the queryTimeout to set
     */
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    /**
     * @return the fetchDirection
     */
    public int getFetchDirection() {
        return fetchDirection;
    }

    /**
     * @param fetchDirection the fetchDirection to set
     */
    public void setFetchDirection(int fetchDirection) {
        if (fetchDirection == ResultSet.FETCH_FORWARD || fetchDirection == ResultSet.FETCH_REVERSE || fetchDirection == ResultSet.FETCH_UNKNOWN) {
            this.fetchDirection = fetchDirection;
        }
    }

    public class Parameter {

        public Integer type = null;
        public Object value = null;

        public Parameter(int t, Object obj) {
            type = t;
            value = obj;
        }
    }
}
