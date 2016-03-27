/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * <p>Title: JDBC简单框架</p>
 *
 * <p>Description: 执行查询/更新</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public final class JdbcPerformer extends JdbcTemplate {
    

    private Connection connection = null;
    private List<Parameter> params = new LinkedList<Parameter>();//参数

    /**
     * 创建
     *
     * @param connection
     */
    public JdbcPerformer(Connection connection) {
        super((DataSource) null);
        this.connection = connection;
    }

    /**
     * 添加参数
     *
     * @param type 指定参数类型
     * @param value
     */
    @Override
    public void addParameter(int type, Object value) {
        Parameter p = new Parameter(type, value);
        params.add(p);
    }

    /**
     * 清除参数
     */
    @Override
    public void clearParameters() {
        params.clear();
    }

    /**
     * 执行查询
     *
     * @param sql
     * @return ResultSet
     * @throws com.autumn.core.sql.SortedSQLException
     */
    @Override
    public ResultSet query(String sql) throws SortedSQLException {      
        PreparedStatement ps = null;
        try {
            ps = createPreparedStatement(connection, sql,params);
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
     * 执行更新
     *
     * @param sql
     * @return int
     * @throws SQLException
     */
    @Override
    public int update(String sql) throws SortedSQLException {
        PreparedStatement ps = null;
        try {
            ps = createPreparedStatement(connection, sql,params);
            return ps.executeUpdate();
        } catch (Exception r) {
            throw r instanceof SortedSQLException ? (SortedSQLException) r : new SortedSQLException(connection, r);
        } finally {
            clearParameters();
            JdbcUtils.closeStatement(ps);
        }
    }

    /**
     * 执行写入，返回增量值
     *
     * @param sql
     * @return long
     * @throws com.autumn.core.sql.SortedSQLException
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
     * 执行查询
     *
     * @param con
     * @param Sql
     * @return ResultSet
     * @throws SQLException
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
     * 执行更新
     *
     * @param con Connection
     * @param Sql String
     * @return int
     * @throws Exception
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
