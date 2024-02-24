package com.example.demo.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.JDBCEntity;

@Repository
public class NamedJDBCRepository {

	private final String ID = ":id";

	@Autowired
	private NamedParameterJdbcTemplate namedJdbc;

	/**
	 * @return {@link JDBCEntity#テストメンバー テストメンバー}テーブルのデータを全て取得
	 * @throws DataAccessException
	 */
	public List<Map<String, Object>> findAll() throws DataAccessException {
		try {
			List<String> sqlList = new ArrayList<String>();

			sqlList.add("SELECT");
			sqlList.add("*");
			sqlList.add("FROM");
			sqlList.add(JDBCEntity.テストメンバー);

			String sql = String.join(" ", sqlList);

			EmptySqlParameterSource empty = new EmptySqlParameterSource();

			return namedJdbc.queryForList(sql, empty);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "¥r¥n"
					+ e.getStackTrace());
			throw e;
		}
	}

	/**
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public Map<String, Object> findById(int id) throws DataAccessException {
		try {
			List<String> sqlList = new ArrayList<String>();

			sqlList.add("SELECT");
			sqlList.add("*");
			sqlList.add("FROM");
			sqlList.add(JDBCEntity.テストメンバー);
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = " + ID);

			String sql = String.join(" ", sqlList);

			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue(ID.replace(":", ""), id);

			return namedJdbc.queryForMap(sql, params);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "¥r¥n"
					+ e.getStackTrace());
			return new HashMap<String, Object>();
		}
	}

	/**
	 * 指定したIDを更新する
	 * @param id
	 * @param updateDataMap
	 * @return
	 * @throws DataAccessException
	 */
	public int updateById(int id, Map<String, String> updateDataMap) throws DataAccessException {
		try {
			List<String> sqlList = new ArrayList<String>();
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue(getUniqueKey(updateDataMap, ID.replace(":", "")), id);

			sqlList.add("UPDATE");
			sqlList.add(JDBCEntity.テストメンバー);
			sqlList.add("SET");
			sqlList.add(updateSet(updateDataMap, params));
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = " + ID);

			String sql = String.join(" ", sqlList);

			return namedJdbc.update(sql, params);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "¥r¥n"
					+ e.getStackTrace());
			throw e;
		}
	}

	/**
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public int deleteById(int id) throws DataAccessException {
		try {
			List<String> sqlList = new ArrayList<String>();
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue(ID.replace(":", ""), id);

			sqlList.add("DELETE");
			sqlList.add(JDBCEntity.テストメンバー);
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = " + ID);

			String sql = String.join(" ", sqlList);

			return namedJdbc.update(sql, params);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "¥r¥n"
					+ e.getStackTrace());
			throw e;
		}
	}

	public int batchUpdate(List<JDBCEntity> updateList) throws DataAccessException {
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(updateList);

		List<String> sqlList = new ArrayList<String>();

		sqlList.add("UPDATE");
		sqlList.add(JDBCEntity.テストメンバー);
		sqlList.add("SET");
		sqlList.add(batchUpdateSet());
		sqlList.add("WHERE");
		sqlList.add(JDBCEntity.ID + " = " + ID);
		
		String sql = String.join(" ", sqlList);
		
		int[] batchUpdate = namedJdbc.batchUpdate(sql, params);
		int returnCount = 0;
		for(int count : batchUpdate) {
			returnCount += count;
		}
		
		return returnCount;
	}
	
	/**
	 * @param deleteList
	 * @return
	 */
	public int batchDelete(List<JDBCEntity> deleteList) {
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(deleteList);

		List<String> sqlList = new ArrayList<String>();

		sqlList.add("DELETE");
		sqlList.add(JDBCEntity.テストメンバー);
		sqlList.add("WHERE");
		sqlList.add(JDBCEntity.ID + " = " + ID);
		
		String sql = String.join(" ", sqlList);
		
		int[] batchUpdate = namedJdbc.batchUpdate(sql, params);
		int returnCount = 0;
		for(int count : batchUpdate) {
			returnCount += count;
		}
		
		return returnCount;
	}

	/**
	 * カラム名とその値のMapをイコールでつなぐ
	 * @param map
	 * @return
	 */
	private List<String> joinEqual(Map<String, String> map) {
		List<String> list = new ArrayList<String>();

		for (var entry : map.entrySet()) {
			list.add(entry.getKey() + " = " + entry.getValue());
		}

		return list;
	}

	/**
	 * リストにつめた文字列をカンマでつなぐ
	 * @param list
	 * @return
	 */
	private String joinComma(List<String> list) {
		return String.join(", ", list);
	}

	/**
	 * UPDATEのSET句を作成する
	 * @param map
	 * @return
	 */
	private String updateSet(Map<String, String> map, MapSqlParameterSource params) {
		for (var entry : map.entrySet()) {
			params.addValue(entry.getKey(), entry.getValue());
			map.replace(entry.getKey(), ":" + entry.getKey());
		}

		return joinComma(joinEqual(map));
	}

	/**
	 * パラメーターの被りが生じないために採番をつける
	 * @param map
	 * @param key
	 * @return
	 */
	private String getUniqueKey(Map<String, String> map, String key) {
		String uniqueKey = key;

		int index = 0;
		while (map.containsKey(key)) {
			uniqueKey = key + index;
			index++;
		}

		return uniqueKey;
	}

	/**
	 * updateのset句を作成する
	 * {@link JDBCEntity}用
	 * @return
	 */
	private String batchUpdateSet() {
		List<String> batchUpdateSetList = new ArrayList<>();
		
		batchUpdateSetList.add(JDBCEntity.ID + " = :" + JDBCEntity.ID);
		batchUpdateSetList.add(JDBCEntity.BIRTHDAY + " = :" + JDBCEntity.BIRTHDAY);
		batchUpdateSetList.add(JDBCEntity.FIRST_NAME + " = :" + JDBCEntity.FIRST_NAME);
		batchUpdateSetList.add(JDBCEntity.LAST_NAME + " = :" + JDBCEntity.LAST_NAME);
		
		return String.join(", ", batchUpdateSetList);
	}
}
