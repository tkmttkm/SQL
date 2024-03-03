package com.example.demo.Dao.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.demo.Entity.JDBCEntity;

/**
 * @author Takumi
 * Entityとテーウルをマッピングする
 */
public class JDBCEntityRowMapper implements RowMapper<JDBCEntity> {

	@Override
	public JDBCEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new JDBCEntity(
				rs.getInt(JDBCEntity.ID),
				rs.getString(JDBCEntity.FIRST_NAME),
				rs.getString(JDBCEntity.LAST_NAME),
				rs.getInt(JDBCEntity.BIRTHDAY)
				);
	}

}
