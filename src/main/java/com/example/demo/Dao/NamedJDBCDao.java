package com.example.demo.Dao;

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
import org.springframework.util.CollectionUtils;

import com.example.demo.Dao.RowMapper.JDBCEntityRowMapper;
import com.example.demo.Entity.JDBCEntity;

/**
 * @author Takumi
 * NamedJdbcTemplate
 *
 */
@Repository
public class NamedJDBCDao {

	@Autowired
	private NamedParameterJdbcTemplate namedJdbc;

	/**
	 * @return {@link JDBCEntity#TEST TEST}テーブルのデータを全て取得
	 * @throws DataAccessException
	 */
	public List<Map<String, Object>> findAll() throws DataAccessException {
		try {
			List<String> sqlList = new ArrayList<String>();

			sqlList.add("SELECT");
			sqlList.add("*");
			sqlList.add("FROM");
			sqlList.add(JDBCEntity.TEST);

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
			sqlList.add(JDBCEntity.TEST);
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = :" + JDBCEntity.ID);

			String sql = String.join(" ", sqlList);

			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue(JDBCEntity.ID, id);

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
			params.addValue(getUniqueKey(updateDataMap, JDBCEntity.ID), id);

			sqlList.add("UPDATE");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add("SET");
			sqlList.add(updateSet(updateDataMap, params));
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = :" + JDBCEntity.ID);

			String sql = String.join(" ", sqlList);

			return namedJdbc.update(sql, params);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "¥r¥n"
					+ e.getStackTrace());
			throw e;
		}
	}
	
	/**
	 * @param insertDataMap
	 * @return
	 * @throws DataAccessException
	 */
	public int insert(Map<String, String> insertDataMap) throws DataAccessException {
		try {
			MapSqlParameterSource params = new MapSqlParameterSource();

			List<String> sqlList = new ArrayList<String>();
			sqlList.add("INSERT INTO");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add(insertSet(insertDataMap, params));
			String sql = String.join(" ", sqlList);

			return namedJdbc.update(sql, params);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n"
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
			params.addValue(JDBCEntity.ID, id);

			sqlList.add("DELETE");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = :" + JDBCEntity.ID);

			String sql = String.join(" ", sqlList);

			return namedJdbc.update(sql, params);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "¥r¥n"
					+ e.getStackTrace());
			throw e;
		}
	}

	/**
	 * @param updateList
	 * @return
	 * @throws DataAccessException
	 */
	public int batchUpdate(List<JDBCEntity> updateList) throws DataAccessException {
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(updateList);

		List<String> sqlList = new ArrayList<String>();

		sqlList.add("UPDATE");
		sqlList.add(JDBCEntity.TEST);
		sqlList.add("SET");
		sqlList.add(batchUpdateSet());
		sqlList.add("WHERE");
		sqlList.add(JDBCEntity.ID + " = :" + JDBCEntity.ID);
		
		String sql = String.join(" ", sqlList);
		
		int[] batchUpdate = namedJdbc.batchUpdate(sql, params);
		int returnCount = 0;
		for(int count : batchUpdate) {
			returnCount += count;
		}
		
		return returnCount;
	}
	
	/**
	 * @param insertList
	 * @return
	 * @throws DataAccessException
	 */
	public int batchInsert(List<JDBCEntity> insertList) throws DataAccessException {
		try {
			SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(insertList);

			List<String> sqlList = new ArrayList<String>();

			sqlList.add("INSERT INTO");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add(batchInsertSet());

			String sql = String.join(" ", sqlList);

			int[] batchUpdate = namedJdbc.batchUpdate(sql, params);
			int returnCount = 0;
			for (int count : batchUpdate) {
				returnCount += count;
			}

			return returnCount;
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n" + e.getStackTrace());
			throw e;
		}
	}
	
	/**
	 * @param deleteList
	 * @return
	 */
	public int batchDelete(List<JDBCEntity> deleteList) {
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(deleteList);

		List<String> sqlList = new ArrayList<String>();

		sqlList.add("DELETE");
		sqlList.add(JDBCEntity.TEST);
		sqlList.add("WHERE");
		sqlList.add(JDBCEntity.ID + " = :" + JDBCEntity.ID);
		
		String sql = String.join(" ", sqlList);
		
		int[] batchUpdate = namedJdbc.batchUpdate(sql, params);
		int returnCount = 0;
		for(int count : batchUpdate) {
			returnCount += count;
		}
		
		return returnCount;
	}
	
	/**
	 * @return
	 * @throws DataAccessException
	 * すべてのデータを取得する
	 * 
	 */
	public List<JDBCEntity> getAllJDBCEntity() throws DataAccessException {
		try {
			List<String> sqlList = new ArrayList<String>();

			sqlList.add("SELECT");
			sqlList.add("*");
			sqlList.add("FROM");
			sqlList.add(JDBCEntity.TEST);

			String sql = String.join(" ", sqlList);

			return namedJdbc.query(sql, new JDBCEntityRowMapper());
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n" + e.getStackTrace());
			throw e;
		}
	}
	
	/**
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public JDBCEntity getJDBCEntityById(int id) throws DataAccessException {
		try {
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue(JDBCEntity.ID, id);
			
			List<String> sqlList = new ArrayList<String>();

			sqlList.add("SELECT");
			sqlList.add("*");
			sqlList.add("FROM");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = :" + JDBCEntity.ID);

			String sql = String.join(" ", sqlList);

			List<JDBCEntity> data = namedJdbc.query(sql, params, new JDBCEntityRowMapper());
			return data.get(0);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n" + e.getStackTrace());
			throw e;
		}
	}
	
	/**
	 * SQLインジェクション対策ができないため危険
	 * @param tableName
	 * @param column_columnInfo カラム名とその型やNULL許容などのマップ
	 * @param primaryKeyList プライマリキーのから無名を詰めたリスト
	 * @throws DataAccessException
	 */
	public void executeCreate(String tableName, Map<String, String> column_columnInfo, List<String> primaryKeyList) throws DataAccessException {
		try {
			List<String> sqlList = new ArrayList<>();
			
			sqlList.add("CREATE TABLE");
			sqlList.add(tableName);
			sqlList.add("(");
			
			List<String> columnList = new ArrayList<String>();
			for(var keyValue : column_columnInfo.entrySet()) {
				columnList.add(keyValue.getKey() + " " + keyValue.getValue());
			}
			sqlList.add(String.join(", ", columnList));
			
			if(!CollectionUtils.isEmpty(primaryKeyList)) {
				sqlList.add(", PRIMARY KEY (");
				sqlList.add(String.join(", ", primaryKeyList));
				sqlList.add(")");
			}
			
			sqlList.add(")");
			
			namedJdbc.getJdbcOperations().execute(String.join(" ", sqlList));
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "r\n" + e.getStackTrace());
			throw e;
		}
	}
	
	/**
	 * SQLインジェクション対策ができないため危険
	 * @param tableName
	 * @throws DataAccessException
	 */
	public void executeDrop(String tableName) throws DataAccessException {
		try {
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("tableName", tableName);
			List<String> sqlList = new ArrayList<>();
			
			sqlList.add("DROP TABLE");
			sqlList.add(":tableName");
			
			namedJdbc.getJdbcOperations().execute(String.join(" ", sqlList));
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "r\n" + e.getStackTrace());
			throw e;
		}
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
	 * @param column_valueMap
	 * @return
	 */
	private String insertSet(Map<String, String> column_valueMap, MapSqlParameterSource params) {
		String[] columnArray = new String[column_valueMap.size()];
		String[] valueArray = new String[column_valueMap.size()];
		
		int index = 0;
		for(var column_value : column_valueMap.entrySet()) {
			columnArray[index] = column_value.getKey();
			valueArray[index] = ":" + column_value.getKey();
			params.addValue(column_value.getKey(), column_value.getValue());
			index++;
		}
		
		List<String> insertSqlList = new ArrayList<>();
		insertSqlList.add("(");
		insertSqlList.add(String.join(", ", columnArray));
		insertSqlList.add(") VALUES (");
		insertSqlList.add(String.join(", ", valueArray));
		insertSqlList.add(")");
		
		return String.join(" ", insertSqlList);
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
	
	/**
	 * @return
	 */
	private String batchInsertSet() {
		List<String> batchInsertColumnSetList = new ArrayList<>();
		List<String> batchInsertValueSetList = new ArrayList<>();

		for (String columnName : JDBCEntity.GetSetQueryList_forBatchUpdate()) {
			batchInsertColumnSetList.add(columnName);
			batchInsertValueSetList.add(":" + columnName);
		}
		
		List<String> sql = new ArrayList<>();
		sql.add("(");
		sql.add(String.join(", ", batchInsertColumnSetList));
		sql.add(") VALUES (");
		sql.add(String.join(", ", batchInsertValueSetList));
		sql.add(")");

		return String.join(" ", sql);
	}
}
