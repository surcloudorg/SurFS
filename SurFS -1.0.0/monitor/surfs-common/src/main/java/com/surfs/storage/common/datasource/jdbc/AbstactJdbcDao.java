package com.surfs.storage.common.datasource.jdbc;

import java.util.List;

import com.surfs.storage.common.datasource.jdbc.mapper.RowMapper;

public interface AbstactJdbcDao {
	
	public Object insert(String poolName, String sql, Object... params) throws Exception;
	
	public int update(String poolName, String sql, Object... params) throws Exception;
	
	public int delete(String poolName, String sql, Object... params) throws Exception;
	
	public <T> T queryForObject(String poolName, String sql, RowMapper<T> mapper, Object... params) throws Exception;
	
	public <T> List<T> queryForList(String poolName, String sql, RowMapper<T> mapper, Object... params) throws Exception;

}
