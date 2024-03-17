package com.example.demo.Dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.example.demo.Dao.RowMapper.JDBCEntityRowMapper;
import com.example.demo.Entity.JDBCEntity;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

/**
 * @author Takumi
 * <pre>
 * JdbcTemplateを使用するクラス
 * パラメータは、?と何番目の?に何を入れるかで指定できる
 * </pre>
 */
@Repository
@RequiredArgsConstructor
public class JDBCTempDao {

	private final JdbcTemplate jdbcTemp;

	/**
	 * <pre>
	 * {@link JDBCEntity#TEST TEST}テーブルのデータを全てを
	 * keyカラム名、valueその値（型Object）で取得
	 * </pre>
	 * @return key:カラム名 value:取得データ の{@code Map<String, Object>}をつめた{@code List} 
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

			return jdbcTemp.queryForList(sql);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n"
					+ e.getStackTrace());
			throw e;
		}
	}

	/**
	 * <pre>
	 * プライマリキーをWHERE句に入れることでデータを1つ取得
	 * </pre>
	 * @param id 取得したいデータのid（プライマリキー）を渡す
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
			sqlList.add(JDBCEntity.ID + " = ?");

			String sql = String.join(" ", sqlList);

			return jdbcTemp.queryForMap(sql, id);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n"
					+ e.getStackTrace());
			return new HashMap<String, Object>();
		}
	}

	/**
	 * <pre>
	 * 更新したいデータのidをWHERE句に渡すことで、1つのデータを更新する
	 * </pre>
	 * @param id 更新したいデータのid（プライマリキー）
	 * @param updateDataMap 更新したいデータ（key:カラム名 value:更新したい値)
	 * @return データを更新した件数
	 * @throws DataAccessException
	 */
	public int updateById(int id, Map<String, String> updateDataMap) throws DataAccessException {
		try {

			Map<String, String> otherMap = new LinkedHashMap<String, String>();
			Integer idInte = Integer.valueOf(id);
			otherMap.put(JDBCEntity.ID, idInte.toString());
			PreparedStatementSetter pss = GetPreparedStatementSetter(updateDataMap, otherMap);

			List<String> sqlList = new ArrayList<String>();
			
			sqlList.add("UPDATE");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add("SET");
			sqlList.add(updateSet(updateDataMap));
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = ?");
			
			String sql = String.join(" ", sqlList);

			return jdbcTemp.update(sql, pss);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n"
					+ e.getStackTrace());
			throw e;
		}
	}
	
	/**
	 * <pre>
	 * データのインサートに使用
	 * </pre>
	 * @param insertDataMap key:カラム名 value:インサートしたい値
	 * @return インサートされた数（基本１）
	 * @throws DataAccessException
	 */
	public int insert(Map<String, String> insertDataMap) throws DataAccessException {
		try {
			PreparedStatementSetter pss = GetPreparedStatementSetter(insertDataMap, null);

			List<String> sqlList = new ArrayList<String>();
			
			sqlList.add("INSERT INTO");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add(insertSet(insertDataMap));
			
			String sql = String.join(" ", sqlList);

			return jdbcTemp.update(sql, pss);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n"
					+ e.getStackTrace());
			throw e;
		}
	}

	/**
	 * <pre>
	 * 削除したいデータのid（プライマリキー）を引数に渡すことでデータを削除する
	 * </pre>
	 * @param id s削除したいデータのid
	 * @return 削除したデータの数（基本１）
	 * @throws DataAccessException
	 */
	public int deleteById(int id) throws DataAccessException {
		try {
			List<String> sqlList = new ArrayList<String>();

			sqlList.add("DELETE");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = ?");

			String sql = String.join(" ", sqlList);

			return jdbcTemp.update(sql, id);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n"
					+ e.getStackTrace());
			throw e;
		}
	}

	/**
	 * <pre>
	 * データの一括アップデートをする
	 * {@link JDCEntity}クラスにプライマリキーと更新したい値を正しくセットし、それを{@code List}煮詰めえることで
	 * そのデータが更新される
	 * </pre>
	 * @param updateList 更新したいデータのリスト
	 * @return 更新した数
	 * @throws DataAccessException
	 */
	public int batchUpdate(List<JDBCEntity> updateList) throws DataAccessException {
		try {
			//WHERE句のid = ?の部分に値を入れるためにotherMapを作成する。
			List<Map<String, String>> otherMapList = new ArrayList<>();
			for (JDBCEntity updateData : updateList) {
				Map<String, String> otherMap = new LinkedHashMap<>();
				var id = Integer.valueOf(updateData.getId());
				otherMap.put(JDBCEntity.ID, id.toString());
				otherMapList.add(otherMap);
			}

			BatchPreparedStatementSetter batchPs = GetBatchPreparedStatementSetter(updateList, otherMapList);

			List<String> sqlList = new ArrayList<String>();

			sqlList.add("UPDATE");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add("SET");
			sqlList.add(batchUpdateSet());
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = ?");

			String sql = String.join(" ", sqlList);

			int[] batchUpdate = jdbcTemp.batchUpdate(sql, batchPs);
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
	 * データの一括インサート
	 * </pre>
	 * @param insertList インサートしたいデータのリスト
	 * @return インサート件数
	 * @throws DataAccessException
	 */
	public int batchInsert(List<JDBCEntity> insertList) throws DataAccessException {
		try {
			BatchPreparedStatementSetter batchPs = GetBatchPreparedStatementSetter(insertList, null);

			List<String> sqlList = new ArrayList<String>();

			sqlList.add("INSERT INTO");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add(batchInsertSet());

			String sql = String.join(" ", sqlList);

			int[] batchUpdate = jdbcTemp.batchUpdate(sql, batchPs);
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
	 * </pre>
	 * @param deleteList
	 * @return
	 * @throws DataAccessException
	 */
	public int batchDelete(List<JDBCEntity> deleteList) throws DataAccessException {
		try {
			List<Map<String, String>> whereMapList = new ArrayList<>();
			Map<String, Integer> sortMap = new LinkedHashMap<String, Integer>();
			sortMap.put(JDBCEntity.ID, 1);
			for (JDBCEntity updateData : deleteList) {
				Map<String, String> otherMap = new LinkedHashMap<>();
				var id = Integer.valueOf(updateData.getId());
				otherMap.put(JDBCEntity.ID, id.toString());
				whereMapList.add(otherMap);
			}

			BatchPreparedStatementSetter batchPs = GetBatchPreparedStatementSetter_forDelete(whereMapList, sortMap);

			List<String> sqlList = new ArrayList<String>();
			sqlList.add("DELETE");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = ?");
			String sql = String.join(" ", sqlList);

			int[] batchUpdate = jdbcTemp.batchUpdate(sql, batchPs);
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
	 * テーブルのすべてのデータを取得する
	 * かつ、取得データを{@link JDBCEntity}の対応するフィールドにセットする
	 * </pre>
	 * @return 取得データを{@link JDBCEntity}のフィールドにセットしたリスト
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

			return jdbcTemp.query(sql, new JDBCEntityRowMapper());
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n" + e.getStackTrace());
			throw e;
		}
	}

	/**
	 * <pre>
	 * 取得したいデータのid（プライマリキー）を引数に渡すことで、データを１件取得する
	 * かつ取得データを{@link JABCEntity}の対応するフィールドにセットする
	 * </pre>
	 * @param id 取得したいデータのid（プライマリキー）
	 * @return 取得したデータを{@link JDBCEntity}の対応するフィールドにセットしたリスト
	 * @throws DataAccessException
	 */
	public JDBCEntity getJDBCEntityById(int id) throws DataAccessException {
		try {
			List<String> sqlList = new ArrayList<String>();

			sqlList.add("SELECT");
			sqlList.add("*");
			sqlList.add("FROM");
			sqlList.add(JDBCEntity.TEST);
			sqlList.add("WHERE");
			sqlList.add(JDBCEntity.ID + " = ?");

			String sql = String.join(" ", sqlList);

			List<JDBCEntity> data = jdbcTemp.query(sql, new JDBCEntityRowMapper(), id);
			return data.get(0);
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "\r\n" + e.getStackTrace());
			throw e;
		}
	}

	/**
	 * <pre>
	 * テーブルの作成で使用
	 * {@link JdbcTemplate#execute(String)}の使用例紹介のために作成したメソッド
	 * ※SQLインジェクション対策ができないため、あまりお勧めできない
	 * </pre>
	 * @param tableName 作成したいテーブルの名前
	 * @param column_columnInfo key:カラム名 value:型やNULL許容など(例 INT NOT NULL)
	 * @param primaryKeyList プライマリキーのカラム名を詰めたリスト
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
			
			jdbcTemp.execute(String.join(" ", sqlList));
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "r\n" + e.getStackTrace());
			throw e;
		}
	}
	
	/**
	 * <pre>
	 * テーブルの削除で使用
	 * {@link JdbcTemplate#execute(String)}の使用例紹介のために作成したメソッド
	 * ※SQLインジェクション対策ができないため、あまりお勧めできない
	 * </pre>
	 * @param tableName 削除したいテーブルの名前
	 * @throws DataAccessException
	 */
	public void executeDrop(String tableName) throws DataAccessException {
		try {
			List<String> sqlList = new ArrayList<>();
			
			sqlList.add("DROP TABLE");
			sqlList.add(tableName);
			
			jdbcTemp.execute(String.join(" ", sqlList));
		} catch (DataAccessException e) {
			System.err.println(e.getMessage() + "r\n" + e.getStackTrace());
			throw e;
		}
	}

	/**
	 * <pre>
	 * カラム名 = ? の形を作る
	 * </pre>
	 * @param map key:カラム名 value:その値
	 * @return カラム名 = ? が詰められたリスト
	 */
	private List<String> joinEqual(Map<String, String> map) {
		List<String> list = new ArrayList<String>();

		for (var entry : map.entrySet()) {
			list.add(entry.getKey() + " = ?");
		}

		return list;
	}

	/**
	 * <pre>
	 * リストにつめた文字列をカンマでつなぐ
	 * アップデートなどで使用
	 * </pre>
	 * @param list カラム名 = ? が詰められたリスト
	 * @return カラム名 = ?, カラム名 = ?, ....
	 */
	private String joinComma(List<String> list) {
		return String.join(", ", list);
	}

	/**
	 * <pre>
	 * UPDATEのSET句を作成する
	 * </pre>
	 * @param map
	 * @return
	 */
	private String updateSet(Map<String, String> map) {
		return joinComma(joinEqual(map));
	}
	
	/**
	 * <pre>
	 * () VALUES ()
	 * の部分を作成する
	 * </pre>
	 * @param column_valueMap key:カラム名 value:インサートしたい値
	 * @return () VALUES ()
	 */
	private String insertSet(Map<String, String> column_valueMap) {
		//挿入する場所を確実に指定するためにあえて配列にする
		String[] columnArray = new String[column_valueMap.size()];
		String[] valueArray = new String[column_valueMap.size()];
		
		//マップの数分ループし、keyのみの配列と、マップの数分の?のみの配列を作成する
		int index = 0;
		for(var column_value : column_valueMap.entrySet()) {
			columnArray[index] = column_value.getKey();
			valueArray[index] = "?";
			index++;
		}
		
		List<String> insertSqlList = new ArrayList<>();
		
		insertSqlList.add("(");
		insertSqlList.add(String.join(", ", columnArray)); //作成した配列を, で繋ぐ
		insertSqlList.add(") VALUES (");
		insertSqlList.add(String.join(", ", valueArray)); //作成した配列を, で繋ぐ
		insertSqlList.add(")");
		
		return String.join(" ", insertSqlList);
	}

	/**
	 * <pre>
	 * updateのset句を作成する
	 * 主にバッチ処理で使用
	 * </pre>
	 * @return カラム名 = ?, カラム名 = ?, ........
	 */
	private String batchUpdateSet() {
		List<String> batchUpdateSetList = new ArrayList<>();

		for (String columnName : JDBCEntity.GetSetQueryList_forBatchUpdate()) {
			batchUpdateSetList.add(columnName + " = ?");
		}

		return String.join(", ", batchUpdateSetList);
	}
	
	/**
	 * <pre>
	 * インサートようのクエリを作成
	 * バッチ処理で使用
	 * </pre>
	 * @return (....) VALUES (.....)
	 */
	private String batchInsertSet() {
		List<String> batchInsertColumnSetList = new ArrayList<>();
		List<String> batchInsertValueSetList = new ArrayList<>();

		for (String columnName : JDBCEntity.GetSetQueryList_forBatchUpdate()) {
			batchInsertColumnSetList.add(columnName);
			batchInsertValueSetList.add("?");
		}
		
		List<String> sql = new ArrayList<>();
		sql.add("(");
		sql.add(String.join(", ", batchInsertColumnSetList));
		sql.add(") VALUES (");
		sql.add(String.join(", ", batchInsertValueSetList));
		sql.add(")");

		return String.join(" ", sql);
	}



	/**
	 * <pre>
	 * ?の何番目になんの値を入れるかをセットする
	 * </pre>
	 * @param ps
	 * @param column_valueMap key:カラム value:代入する値
	 * @param column_sortNoMap key;カラム value:?の何番目に対応するかの数（{@link #GetColumn_sortNoMap(Map)}から取得
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	private void SetPreparedStatement(PreparedStatement ps, Map<String, String> column_valueMap,
			Map<String, Integer> column_sortNoMap) throws NumberFormatException, SQLException {
		try {
			if (existKey(column_valueMap, JDBCEntity.ID)) {
				ps.setInt(column_sortNoMap.get(JDBCEntity.ID), Integer.parseInt(column_valueMap.get(JDBCEntity.ID)));
			}
			if (existKey(column_valueMap, JDBCEntity.BIRTHDAY)) {
				ps.setInt(column_sortNoMap.get(JDBCEntity.BIRTHDAY), Integer.parseInt(column_valueMap.get(JDBCEntity.BIRTHDAY)));
			}
			if (existKey(column_valueMap, JDBCEntity.FIRST_NAME)) {
				ps.setString(column_sortNoMap.get(JDBCEntity.FIRST_NAME), column_valueMap.get(JDBCEntity.FIRST_NAME));
			}
			if (existKey(column_valueMap, JDBCEntity.LAST_NAME)) {
				ps.setString(column_sortNoMap.get(JDBCEntity.LAST_NAME), column_valueMap.get(JDBCEntity.LAST_NAME));
			}
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage() + "\r\n" + e.getStackTrace());
		} catch (SQLException e) {
			System.out.println(e.getMessage() + "\r\n" + e.getStackTrace());
		}
	}

	/**
	 * <pre>
	 * 何番目の?になんの値をセットする指定するメソッド
	 * batch系で使用
	 * </pre>
	 * @param ps
	 * @param data インサートしたいデータ
	 * @param column_sortNoMap key:カラム名 value:?の何番目にそのデータを入れるか
	 */
	private void SetPreparedStatement_forBatchUpdate(PreparedStatement ps, JDBCEntity data,
			Map<String, Integer> column_sortNoMap) {
		try {
			for (String columnName : JDBCEntity.GetSetQueryList_forBatchUpdate()) {
				if (columnName.equals(JDBCEntity.ID)) {
					if (data.getId() != 0) {
						ps.setInt(column_sortNoMap.get(JDBCEntity.ID), data.getId());
					}
					continue;
				}
				if (columnName.equals(JDBCEntity.BIRTHDAY)) {
					if (data.getBirth_day() != 0) {
						ps.setInt(column_sortNoMap.get(JDBCEntity.BIRTHDAY), data.getBirth_day());
					}
					continue;
				}
				if (columnName.equals(JDBCEntity.FIRST_NAME)) {
					if (StringUtils.isNotBlank(data.getFirst_name())) {
						ps.setString(column_sortNoMap.get(JDBCEntity.FIRST_NAME), data.getFirst_name());
					}
					continue;
				}
				if (columnName.equals(JDBCEntity.LAST_NAME)) {
					if (StringUtils.isNotBlank(data.getLast_name())) {
						ps.setString(column_sortNoMap.get(JDBCEntity.LAST_NAME), data.getLast_name());
					}
					continue;
				}
			}
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage() + "\r\n" + e.getStackTrace());
		} catch (SQLException e) {
			System.out.println(e.getMessage() + "\r\n" + e.getStackTrace());
		}
	}

	/**
	 * <pre>
	 * 追加で?に値をセットする際に使用（where句の値のセットなど）
	 * </pre>
	 * @param ps
	 * @param otherMap 追加で値をセットする
	 * @param index 続きの番号
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	private void setPreparedStatementOther(PreparedStatement ps, Map<String, String> otherMap, Integer index)
			throws NumberFormatException, SQLException {
		try {
			//ここで採番して始める
			index++;
			//マップの数分回し、keyの存在確認をしながらセットする
			for (var columnName_Value : otherMap.entrySet()) {
				if (columnName_Value.getKey().equals(JDBCEntity.ID)) {
					Integer idInte = Integer.parseInt(otherMap.get(JDBCEntity.ID));
					if (idInte.intValue() == 0) {
						continue;
					}
					ps.setInt(index, idInte.intValue());
				}
				if (columnName_Value.getKey().equals(JDBCEntity.BIRTHDAY)) {
					Integer birthDayInte = Integer.parseInt(otherMap.get(JDBCEntity.BIRTHDAY));
					if (birthDayInte.intValue() == 0) {
						continue;
					}
					ps.setInt(index, birthDayInte.intValue());
				}
				if (columnName_Value.getKey().equals(JDBCEntity.FIRST_NAME)) {
					if (StringUtils.isBlank(otherMap.get(JDBCEntity.FIRST_NAME))) {
						continue;
					}
					ps.setString(index, otherMap.get(JDBCEntity.FIRST_NAME));
				}
				if (columnName_Value.getKey().equals(JDBCEntity.LAST_NAME)) {
					if (StringUtils.isBlank(otherMap.get(JDBCEntity.LAST_NAME))) {
						continue;
					}
					ps.setString(index, otherMap.get(JDBCEntity.LAST_NAME));
				}
			}
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage() + "\r\n" + e.getStackTrace());
		} catch (SQLException e) {
			System.out.println(e.getMessage() + "\r\n" + e.getStackTrace());
		}
	}

	/**
	 * <pre>
	 * {@code Map<String, String>}のkeyの存在確認をする
	 * </pre>
	 * @param map keuの存在確認がしたいMap
	 * @param key 存在確認したいkey
	 * @return keyが存在したらtrue
	 */
	private boolean existKey(Map<String, String> map, String key) {
		return map.containsKey(key);
	}

	/**
	 * <pre>
	 * ?の何番目に何を入れるかをセットする際に、
	 * カラム名と何番目かを対応させておく
	 * </pre>
	 * @param column_valueMap key:カラム名 value:その値
	 * @return key:カラム名 value:?の何番目にセットするかの数
	 */
	private Map<String, Integer> GetColumn_sortNoMap(Map<String, String> column_valueMap) {
		Map<String, Integer> returnMap = new LinkedHashMap<>();
		Integer index = 1;
		for (var map : column_valueMap.entrySet()) {
			returnMap.put(map.getKey(), index);
			index++;
		}
		return returnMap;
	}

	/**
	 * <pre>
	 * カラム名と?との対応づけを確実にするために、奈良み順を指定しているメソッドを使用して
	 * key:カラム名 value:何番目の?に値を入れるか
	 * の{@code Map}を作成する
	 * 並び順は{@link JDBCEntity#GetSetQueryList_forBatchUpdate()}で取得
	 * </pre>
	 * @return
	 */
	private Map<String, Integer> GetColumn_sortNoMap_forBatchUpdate() {
		Map<String, Integer> returnMap = new LinkedHashMap<>();
		Integer index = 1;
		for (String columnName : JDBCEntity.GetSetQueryList_forBatchUpdate()) {
			returnMap.put(columnName, index);
			index++;
		}
		return returnMap;
	}

	/**
	 * <pre>
	 * SQLインジェクション対策
	 * ?のところの何番目になんの値を入れるかをセットしていく
	 * </pre>
	 * @param updateDataMap 更新したいデータ（key:カラム名 value:更新したい値）
	 * @return
	 */
	private PreparedStatementSetter GetPreparedStatementSetter(Map<String, String> updateDataMap,
			Map<String, String> otherMap) {
		return new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				Map<String, Integer> sortMap = GetColumn_sortNoMap(updateDataMap);
				SetPreparedStatement(ps, updateDataMap, sortMap);
				if (otherMap != null) {
					if (otherMap.size() != 0) {
						setPreparedStatementOther(ps, otherMap, updateDataMap.size());
					}
				}
			}
		};
	}

	/**
	 * <pre>
	 * バッチアップデートなどで使用
	 * 引数に渡されたデータが入ったリストを元に、?の何番目に何を入れるかをセットする
	 * </pre>
	 * @param updateList
	 * @param otherList where句などに使う値を格納する
	 * @return
	 */
	private BatchPreparedStatementSetter GetBatchPreparedStatementSetter(List<JDBCEntity> updateList,
			List<Map<String, String>> otherMapList) {
		return new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map<String, Integer> sortMap = GetColumn_sortNoMap_forBatchUpdate();
				SetPreparedStatement_forBatchUpdate(ps, updateList.get(i), sortMap);
				if (otherMapList != null) {
					if (otherMapList.size() != 0) {
						if (otherMapList.size() == updateList.size()) {
							setPreparedStatementOther(ps, otherMapList.get(i), sortMap.size());
						} else {
							throw new SQLException("otherMapListが正しく設定されていません。");
						}
					}
				}
			}

			@Override
			public int getBatchSize() {
				// バッチのサイズを返す
				return updateList.size();
			}
		};
	}

	/**
	 * <pre>
	 * 一括削除で使用
	 * where句の何番目の?になんの値を入れるかを設定する
	 * </pre>
	 * @param whereMapList key:カラム名 value:where句に入れる値
	 * @param sortMap key:カラム名 value:?の何番目に入れるかの数
	 * @return
	 */
	private BatchPreparedStatementSetter GetBatchPreparedStatementSetter_forDelete(
			List<Map<String, String>> whereMapList, Map<String, Integer> sortMap) {
		return new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				SetPreparedStatement(ps, whereMapList.get(i), sortMap);
			}

			@Override
			public int getBatchSize() {
				return whereMapList.size();
			}
		};
	}

}
