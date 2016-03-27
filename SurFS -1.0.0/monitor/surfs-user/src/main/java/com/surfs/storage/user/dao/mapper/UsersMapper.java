/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.user.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.surfs.storage.common.datasource.jdbc.mapper.RowMapper;
import com.surfs.storage.user.model.Users;

public class UsersMapper implements RowMapper<Users> {

	@Override
	public Users mapRow(ResultSet rs) throws SQLException {
		Users users = new Users();
		int columnCount = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = rs.getMetaData().getColumnName(i);
			if ("usersId".equals(columnName))
				users.setUsersId(rs.getInt("usersId"));
			if ("usersName".equals(columnName))
				users.setUsersName(rs.getString("usersName"));
			if ("pwd".equals(columnName))
				users.setPwd(rs.getString("pwd"));
			if ("realName".equals(columnName))
				users.setRealName(rs.getString("realName"));
			if ("createTime".equals(columnName))
				users.setCreateTime(rs.getTimestamp("createTime"));
			if ("comment".equals(columnName))
				users.setComment(rs.getString("comment"));
			if ("permission".equals(columnName))
				users.setPermission(rs.getString("permission"));
		}
		return users;
	}
	
}
