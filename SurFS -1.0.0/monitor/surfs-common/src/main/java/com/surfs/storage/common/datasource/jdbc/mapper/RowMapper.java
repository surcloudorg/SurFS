package com.surfs.storage.common.datasource.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {

	T mapRow(ResultSet rs) throws SQLException;

}
