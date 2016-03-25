package com.surfs.storage.common.datasource.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.autumn.core.sql.JdbcUtils;
import com.surfs.nas.client.Setup;
import com.surfs.nas.mysql.MysqlDataSource;
import com.surfs.storage.common.datasource.jdbc.mapper.RowMapper;

@Repository
public class JdbcDao implements AbstactJdbcDao {

	@Override
	public Object insert(String poolName, String sql, Object... params) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection(poolName);
			ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < params.length; i++) {
				if (params[i] instanceof String)
					ps.setString(i+1, (String) params[i]);
				else if (params[i] instanceof Integer)
					ps.setInt(i+1, (Integer) params[i]);
				else if (params[i] instanceof Long)
					ps.setLong(i+1, (Long) params[i]);
				else if (params[i] instanceof Timestamp)
					ps.setTimestamp(i+1, (Timestamp) params[i]);
			}
			ps.execute();
			rs = ps.getGeneratedKeys();
			if (rs.next())
				return rs.getObject(1);
			return null;
		} catch (Exception e) {
			throw e;
		} finally {
			JdbcUtils.closeResultset(rs);
			JdbcUtils.closeStatement(ps);
			JdbcUtils.closeConnect(conn);
		}
	}

	@Override
	public int update(String poolName, String sql, Object... params) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection(poolName);
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				if (params[i] instanceof String)
					ps.setString(i+1, (String) params[i]);
				else if (params[i] instanceof Integer)
					ps.setInt(i+1, (Integer) params[i]);
				else if (params[i] instanceof Long)
					ps.setLong(i+1, (Long) params[i]);
				else if (params[i] instanceof Timestamp)
					ps.setTimestamp(i+1, (Timestamp) params[i]);
			}
			return ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			JdbcUtils.closeStatement(ps);
			JdbcUtils.closeConnect(conn);
		}
	}

	@Override
	public int delete(String poolName, String sql, Object... params) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection(poolName);
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				if (params[i] instanceof String)
					ps.setString(i+1, (String) params[i]);
				else if (params[i] instanceof Integer)
					ps.setInt(i+1, (Integer) params[i]);
				else if (params[i] instanceof Long)
					ps.setLong(i+1, (Long) params[i]);
				else if (params[i] instanceof Timestamp)
					ps.setTimestamp(i+1, (Timestamp) params[i]);
			}
			return ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			JdbcUtils.closeStatement(ps);
			JdbcUtils.closeConnect(conn);
		}
	}

	@Override
	public <T> T queryForObject(String poolName, String sql, RowMapper<T> mapper,
			Object... params) throws Exception {
		List<T> list = queryForList(poolName, sql, mapper, params);
		if (list.size() > 1) {
			throw new Exception("Too Many Result!");
		}
		return list.get(0);
	}

	@Override
	public <T> List<T> queryForList(String poolName, String sql,
			RowMapper<T> mapper, Object... params) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<T> list = new ArrayList<>();
		try {
			conn = getConnection(poolName);
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				if (params[i] instanceof String)
					ps.setString(i+1, (String) params[i]);
				else if (params[i] instanceof Integer)
					ps.setInt(i+1, (Integer) params[i]);
				else if (params[i] instanceof Long)
					ps.setLong(i+1, (Long) params[i]);
				else if (params[i] instanceof Timestamp)
					ps.setTimestamp(i+1, (Timestamp) params[i]);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				T t = mapper.mapRow(rs);
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			throw e;
		} finally {
			JdbcUtils.closeResultset(rs);
			JdbcUtils.closeStatement(ps);
			JdbcUtils.closeConnect(conn);
		}
	}

	private Connection getConnection(String poolName) throws IOException {
		MysqlDataSource datasource = (MysqlDataSource) (new Setup(poolName)
				.getDataSource());
		return datasource.getConnection();
	}
	
}
