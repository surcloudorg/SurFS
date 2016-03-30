/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.nas.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;

public final class JdbcPerformer extends JdbcTemplate {

    private Connection connection = null;
    private final List<Parameter> params = new LinkedList<>();

    /**
     *
     * @param connection
     */
    public JdbcPerformer(Connection connection) {
        super((DataSource) null);
        this.connection = connection;
    }

    /**
     *
     * @param type
     * @param value
     */
    @Override
    public void addParameter(int type, Object value) {
        Parameter p = new Parameter(type, value);
        params.add(p);
    }

    @Override
    public void clearParameters() {
        params.clear();
    }

    /**
     *
     * @param sql
     * @return ResultSet
     * @throws com.surfs.nas.sql.SortedSQLException
     */
    @Override
    public ResultSet query(String sql) throws SortedSQLException {
        PreparedStatement ps = null;
        try {
            ps = createPreparedStatement(connection, sql, params);
            ResultSet rs = ps.executeQuery();
            CachedRowSet cacherowset = (CachedRowSet) Class.forName(ROWSET_IMPL_CLASS).newInstance();
            cacherowset.populate(rs);
            return cacherowset;
        } catch (Exception r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(connection, r);
        } finally {
            clearParameters();
            JdbcUtils.closeStatement(ps);
        }
    }

    /**
     *
     * @param sql
     * @return int
     * @throws SortedSQLException
     */
    @Override
    public int update(String sql) throws SortedSQLException {
        PreparedStatement ps = null;
        try {
            ps = createPreparedStatement(connection, sql, params);
            return ps.executeUpdate();
        } catch (Exception r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(connection, r);
        } finally {
            clearParameters();
            JdbcUtils.closeStatement(ps);
        }
    }

    /**
     *
     * @param sql
     * @return long
     * @throws com.surfs.nas.sql.SortedSQLException
     */
    @Override
    public long insert(String sql) throws SortedSQLException {
        PreparedStatement ps = null;
        try {
            ps = createPreparedStatement(connection, sql, true, params);
            int rc = ps.executeUpdate();
            if (rc < 1) {
                return 0;
            } else {
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                return rs.getLong(1);
            }
        } catch (Exception r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(connection, r);
        } finally {
            clearParameters();
            JdbcUtils.closeStatement(ps);
        }
    }

    /**
     *
     * @param con
     * @param Sql
     * @return ResultSet
     * @throws SortedSQLException
     */
    public static ResultSet executeQuery(Connection con, String Sql) throws SortedSQLException {
        PreparedStatement stat = null;
        ResultSet rs;
        try {
            CachedRowSet cacherowset = (CachedRowSet) Class.forName(ROWSET_IMPL_CLASS).newInstance();
            stat = con.prepareStatement(Sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stat.setQueryTimeout(60 * 5);
            rs = stat.executeQuery();
            cacherowset.populate(rs);
            return cacherowset;
        } catch (Exception r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(con, r);
        } finally {
            JdbcUtils.closeStatement(stat);
        }
    }

    /**
     *
     * @param con Connection
     * @param Sql String
     * @return int
     * @throws SQLException
     */
    public static int executeUpdate(Connection con, String Sql) throws SQLException {
        PreparedStatement stat = null;
        int num = 0;
        try {
            stat = con.prepareStatement(Sql);
            stat.setQueryTimeout(60 * 5);
            num = stat.executeUpdate();
        } catch (SQLException r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(con, r);
        } finally {
            JdbcUtils.closeStatement(stat);
        }
        return num;
    }
}
