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
 * <pre>
 * {@link NamedParameterJdbcTemplate}を使用して
 * DB接続するクラス
 * :パラメータ名 を設定し、
 * {@link MapSqlParameterSource}などを使用することで、
 * パラメータをマッピング
 *</pre>
 */
@Repository
public class NamedJDBCDao {

	@Autowired
	private NamedParameterJdbcTemplate namedJdbc;

	/**
	 * <pre>
	 * テーブルのデータを{@code List<Map<String, Object>>}ですべて取得。
	 * {@code Map<String, Object>}のkeyはカラム名、valueは取得データ
	 * {@link NamedParameterJdbcTemplate#queryForList(String, SqlParameterSource)}使用
	 * </pre>
	 * @return {@link JDBCEntity#TEST TEST}テーブルのデータを{@code List<Map<String, Object>>}で取得
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
	 * <pre>
	 * 取得したいデータを
	 * 引数にid（プライマリキー）を渡すことで、取得
	 * 型は{@code Map<String, Object>}でkeyはカラム名、valueは取得データ
	 * {@link NamedParameterJdbcTemplate#queryForMap(String, SqlParameterSource)}使用
	 * </pre>
	 * @param id 取得したいデータおid（プライマリキー）
	 * @return idを渡したデータの{@code Map<String, Object>}
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
	 * <pre>
	 * 指定したid（プライマリキー）のデータを更新する
	 * 引数にid（プライマリキー）とkeyにカラム名、valueに更新したい値を入れた{@code Map<String, String}を渡すことで
	 * テーブルを更新する
	 * {@link NamedParameterJdbcTemplate#update(String, SqlParameterSource)}使用
	 * </pre>
	 * @param id
	 * @param updateDataMap
	 * @return　更新数
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
	 * <pre>
	 * データを挿入する
	 * 引数に{@code Map<STring, String>} key:カラム名 value:挿入したい値
	 * を渡すことでデータを挿入する
	 * {@link NamedParameterJdbcTemplate#update(String, SqlParameterSource)}使用
	 * </pre>
	 * @param insertDataMap key:カラム名 value:挿入したい値
	 * @return 挿入数
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
	 * <pre>
	 * 引数に削除したいデータのid（プライマリキー）を渡すことでデータを削除する
	 * {@link NamedParameterJdbcTemplate#update(String, SqlParameterSource)}使用
	 * </pre>
	 * @param id 削除したいデータのid
	 * @return 削除数
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
	 * <pre>
	 * データの一括更新
	 * 引数に更新したいデータの{@code List<JDBCEntity>}を渡すことで
	 * データを一括更新する
	 * {@link NamedParameterJdbcTemplate#batchUpdate(String, SqlParameterSource[])}使用
	 * </pre>
	 * @param updateList 更新したいデータのリスト
	 * @return 更新数
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
	 * <pre>
	 * データの一括挿入
	 * 引数に挿入したいデータの{@code List<JDBCEntity>}を渡すことで、データを一括挿入する
	 * {@link NamedParameterJdbcTemplate#batchUpdate(String, SqlParameterSource[])}使用
	 * </pre>
	 * @param insertList 挿入したいデータのリスト
	 * @return 挿入数
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
	 * <pre>
	 * データの一括削除
	 * 引数に削除したいデータの{@code List<JDBCEntity>}を渡すことでデータを一括削除する
	 * この引数にセットする値はid（プライマリキー）のみでOK
	 * {@link NamedParameterJdbcTemplate#batchUpdate(String, SqlParameterSource[])}使用
	 * </pre>
	 * @param deleteList 削除したいデータのリスト（セットする値はidにみでOK）
	 * @return 削除数
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
	 * <pre>
	 * テーブルないのデータを{@code List<JDBCEntity>}で取得。
	 * {@link NamedParameterJdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper)}使用
	 * </pre>
	 * @return テーブル内の全データ
	 * @throws DataAccessException
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
	 * <pre>
	 * 引数に取得したいデータのid（プライマリキー）を渡すことで
	 * データを{@code List<JDBCEntity>}で取得
	 * {@link NamedParameterJdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper)}使用
	 * </pre>
	 * @param id 取得したいデータのid
	 * @return 渡したidのデータ
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
	 * <pre>
	 * {@link NamedParameterJdbcTemplate#getJdbcOperations()#executeDrop(String)}を使用してテーブルを作成する
	 * 引数に作成したいテーブル名、作成したいテーブル名と型、NULL許容などの{@code Map<String, String>}、プライマリキーに設定したい{@code List<String>}を渡す
	 * ※SQLインジェクション対策ができないため推奨はできない
	 * </pre>
	 * @param tableName テーブル名
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
	 * <pre>
	 * {@link NamedParameterJdbcTemplate#getJdbcOperations()#executeDrop(String)}を用いてテーブルを削除する
	 * 引数に削除したいテーブルの名前を渡す
	 * ※SQLインジェクション対策ができないため推奨できない
	 * </pre>
	 * @param tableName テーブル名
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
	 * <pre>
	 * カラム名とその値のMapをイコールでつなぐ
	 * </pre>
	 * @param map
	 * @return カラム名 = 値, カラム名 = 値, ....
	 */
	private List<String> joinEqual(Map<String, String> map) {
		List<String> list = new ArrayList<String>();

		for (var entry : map.entrySet()) {
			list.add(entry.getKey() + " = " + entry.getValue());
		}

		return list;
	}

	/**
	 * <pre> 
	 * リストにつめた文字列をカンマでつなぐ
	 * </pre>
	 * @param list
	 * @return 値, 値, 値, ...
	 */
	private String joinComma(List<String> list) {
		return String.join(", ", list);
	}

	/**
	 * <pre>
	 * UPDATEのSET句を作成する
	 * {@code Map<String, Object>}key:カラム名 value:更新したい値
	 * を key:カラム名 value: :カラム名
	 * に変更する
	 * </pre>
	 * @param map key:カラム名 value:更新したい値
	 * @return カラム名 = :カラム名, カラム名 = :カラム名, ....
	 */
	private String updateSet(Map<String, String> map, MapSqlParameterSource params) {
		for (var entry : map.entrySet()) {
			params.addValue(entry.getKey(), entry.getValue());
			map.replace(entry.getKey(), ":" + entry.getKey());
		}

		return joinComma(joinEqual(map));
	}
	
	/**
	 * <pre>
	 * インサート文の(...) VALUES (...)を作成する
	 * </pre>
	 * @param column_valueMap keu:カラム名 value:値
	 * @return （カラム名, カラム名, ...) VALUES (:カラム名, :カラム名, :カラム名, ...)
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
	 * <pre>
	 * パラメーターの被りが生じないために採番をつける 
	 * </pre>
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
	 * <pre>
	 * updateのset句を作成する
	 * {@link JDBCEntity}のフィールドを使用
	 * </pre>
	 * @return id = :id, birth_day = :birth_day, first_name = :first_name, last_name = :last_name
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
	 * <pre>
	 * バッチインサート用の(...) VALUES (...)を作成
	 * </pre>
	 * @return (カラム名, カラム名, ... ) VALUES (:カラム名, :カラム名, ...)
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
