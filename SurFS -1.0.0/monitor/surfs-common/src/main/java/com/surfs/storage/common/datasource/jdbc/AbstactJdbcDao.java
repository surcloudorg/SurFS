/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
