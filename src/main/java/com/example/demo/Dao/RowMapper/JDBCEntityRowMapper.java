package com.example.demo.Dao.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.demo.Entity.JDBCEntity;

/**
 * @author Takumi
 * <pre>
 * {@link JDBCEntity}のフィールドとマッピングをするためのクラス
 * {@link #mapRow(ResultSet, int)}で対応するカラムと値を設定する（{@code override}使用
 * </pre>
 */
public class JDBCEntityRowMapper implements RowMapper<JDBCEntity> {

	/**
	 * <pre>
	 *　引数にテーブルのカラム名を渡し、型を合わせることで
	 *　マッピングされる
	 * </pre>
	 */
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
