/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.block.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.surfs.storage.block.model.BlockUser;
import com.surfs.storage.common.datasource.jdbc.mapper.RowMapper;
public class BlockUserMapper implements RowMapper<BlockUser> {

	@Override
	public BlockUser mapRow(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		BlockUser blockUser = new BlockUser();
		int columnCount = rs.getMetaData().getColumnCount();
		for(int i=1;i<=columnCount;i++){
			String columnName = rs.getMetaData().getColumnName(i);	
			if("userId".equals(columnName)){
				blockUser.setUserId(rs.getInt("userId"));
			}
			if("userName".equals(columnName)){
				blockUser.setUserName(rs.getString("userName"));
			}
			if("passWord".equals(columnName)){
				blockUser.setPassWord(rs.getString("passWord"));
			}
			if("realName".equals(columnName)){
				blockUser.setRealName(rs.getString("realName"));
			}
			if("comment".equals(columnName)){
				blockUser.setComment(rs.getString("comment"));
			}
			if("createTime".equals(columnName)){
				blockUser.setCreateTime(rs.getTimestamp("createTime"));
			}
			if("target".equals(columnName)){
				blockUser.setTarget(rs.getString("target"));
			}
		}
		return blockUser;
	}

}
