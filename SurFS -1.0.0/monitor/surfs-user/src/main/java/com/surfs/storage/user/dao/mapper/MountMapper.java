/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.surfs.storage.user.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.surfs.storage.common.datasource.jdbc.mapper.RowMapper;
import com.surfs.storage.user.model.Mount;

public class MountMapper implements RowMapper<Mount> {

	@Override
	public Mount mapRow(ResultSet rs) throws SQLException {
		Mount mount = new Mount();
		mount.setMountId(rs.getInt("mountId"));
		mount.setPath(rs.getString("path"));
		mount.setQuota(rs.getLong("quota"));
		mount.setCreateTime(rs.getTimestamp("createTime"));
		return mount;
	}

}
