package com.example.demo.Dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.example.demo.Dao.RowMapper.JDBCEntityRowMapper;
import com.example.demo.Entity.JDBCEntity;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

/**
 * @author Takumi
 * JdbcTemplateを使用するクラス
 */
@Repository
@RequiredArgsConstructor
public class JDBCTempDao {

	private final JdbcTemplate jdbcTemp;

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

			return jdbcTemp.queryForList(sql);
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
	 * 指定したIDを更新する
	 * @param id
	 * @param updateDataMap
	 * @return
	 * @throws DataAccessException
	 */
	public int updateById(int id, Map<String, String> updateDataMap) throws DataAccessException {
		try {

			Map<String, String> otherMap = new TreeMap<String, String>();
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
	 * @param id
	 * @return
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
	 * @param updateList
	 * @return
	 * @throws DataAccessException
	 */
	public int batchUpdate(List<JDBCEntity> updateList) throws DataAccessException {
		try {
			List<Map<String, String>> otherMapList = new ArrayList<>();
			for (JDBCEntity updateData : updateList) {
				Map<String, String> otherMap = new TreeMap<>();
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
	 * @param deleteList
	 * @return
	 * @throws DataAccessException
	 */
	public int batchDelete(List<JDBCEntity> deleteList) throws DataAccessException {
		try {
			List<Map<String, String>> whereMapList = new ArrayList<>();
			Map<String, Integer> sortMap = new TreeMap<String, Integer>();
			sortMap.put(JDBCEntity.ID, 1);
			for (JDBCEntity updateData : deleteList) {
				Map<String, String> otherMap = new TreeMap<>();
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

	public DataSource getDataSource() {
		return jdbcTemp.getDataSource();
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

			return jdbcTemp.query(sql, new JDBCEntityRowMapper());
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
	 * カラム名とその値のMapをイコールでつなぐ
	 * @param map
	 * @return
	 */
	private List<String> joinEqual(Map<String, String> map) {
		List<String> list = new ArrayList<String>();

		for (var entry : map.entrySet()) {
			list.add(entry.getKey() + " = ?");
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
	private String updateSet(Map<String, String> map) {
		return joinComma(joinEqual(map));
	}

	/**
	 * updateのset句を作成する
	 * {@link JDBCEntity}用
	 * @return
	 */
	private String batchUpdateSet() {
		List<String> batchUpdateSetList = new ArrayList<>();

		for (String columnName : GetSetQueryList_forBatchUpdate()) {
			batchUpdateSetList.add(columnName + " = ?");
		}

		return String.join(", ", batchUpdateSetList);
	}

	/**
	 * @return
	 */
	private List<String> GetSetQueryList_forBatchUpdate() {
		List<String> setQueryList = new ArrayList<String>();

		setQueryList.add(JDBCEntity.ID);
		setQueryList.add(JDBCEntity.FIRST_NAME);
		setQueryList.add(JDBCEntity.LAST_NAME);
		setQueryList.add(JDBCEntity.BIRTHDAY);

		return setQueryList;
	}

	/**
	 * @param ps
	 * @param column_valueMap カラムと代入する値
	 * @param column_sortNoMap カラムの値の代入の順番
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
				ps.setString(column_sortNoMap.get(JDBCEntity.BIRTHDAY), column_valueMap.get(JDBCEntity.ID));
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
	 * @param ps
	 * @param data
	 * @param column_sortNoMap
	 */
	private void SetPreparedStatement_forBatchUpdate(PreparedStatement ps, JDBCEntity data,
			Map<String, Integer> column_sortNoMap) {
		try {
			for (String columnName : GetSetQueryList_forBatchUpdate()) {
				if (columnName.equals(JDBCEntity.ID)) {
					if (data.getId() == 0) {
						continue;
					}
					ps.setInt(column_sortNoMap.get(JDBCEntity.ID), data.getId());
				}
				if (columnName.equals(JDBCEntity.BIRTHDAY)) {
					if (data.getBirth_day() == 0) {
						continue;
					}
					ps.setInt(column_sortNoMap.get(JDBCEntity.BIRTHDAY), data.getBirth_day());
				}
				if (columnName.equals(JDBCEntity.FIRST_NAME)) {
					if (StringUtils.isBlank(data.getFirst_name())) {
						continue;
					}
					ps.setString(column_sortNoMap.get(JDBCEntity.FIRST_NAME), data.getLast_name());
				}
				if (columnName.equals(JDBCEntity.LAST_NAME)) {
					if (StringUtils.isBlank(data.getLast_name())) {
						continue;
					}
					ps.setString(column_sortNoMap.get(JDBCEntity.LAST_NAME), data.getFirst_name());
				}
			}
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage() + "\r\n" + e.getStackTrace());
		} catch (SQLException e) {
			System.out.println(e.getMessage() + "\r\n" + e.getStackTrace());
		}
	}

	/**
	 * @param ps
	 * @param otherMap
	 * @param index
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	private void setPreparedStatementOther(PreparedStatement ps, Map<String, String> otherMap, Integer index)
			throws NumberFormatException, SQLException {
		try {
			index++;
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

	private boolean existKey(Map<String, String> map, String key) {
		return map.containsKey(key);
	}

	private Map<String, Integer> GetColumn_sortNoMap(Map<String, String> column_valueMap) {
		Map<String, Integer> returnMap = new TreeMap<>();
		Integer index = 1;
		for (var map : column_valueMap.entrySet()) {
			returnMap.put(map.getKey(), index);
			index++;
		}
		return returnMap;
	}

	/**
	 * @return
	 */
	private Map<String, Integer> GetColumn_sortNoMap_forBatchUpdate() {
		Map<String, Integer> returnMap = new TreeMap<>();
		Integer index = 1;
		for (String columnName : GetSetQueryList_forBatchUpdate()) {
			returnMap.put(columnName, index);
			index++;
		}
		return returnMap;
	}

	/**
	 * @param updateDataMap
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
	 * @param updateList
	 * @param otherList where句などに使う値を格納する
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