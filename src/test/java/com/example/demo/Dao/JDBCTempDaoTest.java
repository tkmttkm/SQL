package com.example.demo.Dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.JDBCEntity;

/**
 * @author Takumi
 */
@SpringBootTest
@Transactional
class JDBCTempDaoTest {

	@Autowired
	private JDBCTempDao dao;

	/**
	 * <pre>
	 * {@link JdbcTemplate#queryForMap(String)}i
	 * にてテーブルデータすべて取得
	 * </pre>
	 */
	@Test
	void testFindAll() {
		List<Map<String, Object>> dataList = dao.findAll();
		assertTrue(dataList.size() == 4);
	}

	/**
	 * <pre>
	 * {@link JdbcTemplate#queryForMap(String, Object...)}
	 * にてプライマリキー（id）を渡し取得したいデータを1つ取得
	 * </pre>
	 */
	@Test
	void testFindById() {
		Map<String, Object> data = dao.findById(1);

		assertEquals(data.get(JDBCEntity.BIRTHDAY), 20240101);
		assertEquals(data.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(data.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");
	}

	/**
	 * <pre>
	 * {@link JdbcTemplate#update(String, org.springframework.jdbc.core.PreparedStatementSetter)}
	 * にて更新したいデータのプライマリキー（id）を渡すことで、データを更新
	 * </pre>
	 */
	@Test
	void testUpdateById() {
		//更新前のデータ取得
		Map<String, Object> beforeData = dao.findById(1);
		assertEquals(beforeData.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(beforeData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");

		//データ更新
		Map<String, String> updateMap = new TreeMap<>();
		updateMap.put(JDBCEntity.FIRST_NAME, "更新した");
		updateMap.put(JDBCEntity.LAST_NAME, "太郎くん");
		int updateCount = dao.updateById(1, updateMap);
		assertTrue(updateCount == 1);

		//更新後のデータ確認
		Map<String, Object> updateData = dao.findById(1);
		assertEquals(updateData.get(JDBCEntity.FIRST_NAME).toString().strip(), "更新した");
		assertEquals(updateData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎くん");
	}

	/**
	 * <pre>
	 * {@link JdbcTemplate#update(String, org.springframework.jdbc.core.PreparedStatementSetter)}
	 * を用いてデータを挿入
	 * {@code Map<Strng, String>}にkey:カラム名 value:挿入したい値
	 * をセットし、引数に渡すことで挿入
	 * </pre>
	 */
	@Test
	void testInsert() {
		//素運輸データの準備
		Map<String, String> insertData = new LinkedHashMap<>();
		insertData.put(JDBCEntity.ID, "10");
		insertData.put(JDBCEntity.FIRST_NAME, "インサート");
		insertData.put(JDBCEntity.LAST_NAME, "太郎");
		insertData.put(JDBCEntity.BIRTHDAY, "20200202");
		//データの挿入
		dao.insert(insertData);

		//データの挿入確認
		Map<String, Object> data = dao.findById(10);
		assertEquals(data.get(JDBCEntity.FIRST_NAME), "インサート");
		assertEquals(data.get(JDBCEntity.LAST_NAME), "太郎");
		assertEquals(data.get(JDBCEntity.BIRTHDAY), 20200202);
	}

	/**
	 * <pre>
	 * {@link JdbcTemplate#update(String, Object...)}
	 * を用いてデータの削除
	 * 引数に削除したデータのid（プライマリキー）を渡すことで
	 * データを削除する
	 * </pre>
	 */
	@Test
	void testDeleteById() {
		//削除前のデータの存在確認
		Map<String, Object> beforeData = dao.findById(1);
		assertEquals(beforeData.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(beforeData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");
		assertEquals(beforeData.get(JDBCEntity.BIRTHDAY), 20240101);

		//データの削除
		int deleteCount = dao.deleteById(1);
		assertTrue(deleteCount == 1);

		//データの削除確認
		List<Map<String, Object>> deleteData = dao.findAll();
		assertFalse(deleteData.contains(beforeData));
	}

	/**
	 * <pre>
	 * {@link JdbcTemplate#batchUpdate(String, org.springframework.jdbc.core.BatchPreparedStatementSetter)}
	 * を用いてデータの一括更新
	 * {@code List<JDBCEntity>}に更新したいデータをセットすることで
	 * データを一括更新する
	 * </pre>
	 */
	@Test
	void testBatchUpdate() {
		//更新データのセット
		List<JDBCEntity> entityList = new ArrayList<>();
		entityList.add(new JDBCEntity(1, "Junit", "たのすいーーーー", 20240202));
		entityList.add(new JDBCEntity(2, "Junit", "楽しいねえ", 20240203));

		//データの一括更新
		int updateCount = dao.batchUpdate(entityList);

		//データの更新確認
		assertEquals(updateCount, 2);

		Map<String, Object> afterId1 = dao.findById(1);
		assertEquals(afterId1.get(JDBCEntity.FIRST_NAME.toString().strip()), "Junit");
		assertEquals(afterId1.get(JDBCEntity.LAST_NAME.toString().strip()), "たのすいーーーー");
		assertEquals(Integer.parseInt(afterId1.get(JDBCEntity.BIRTHDAY).toString()), 20240202);

		Map<String, Object> afterId2 = dao.findById(2);
		assertEquals(afterId2.get(JDBCEntity.FIRST_NAME.toString().strip()), "Junit");
		assertEquals(afterId2.get(JDBCEntity.LAST_NAME.toString().strip()), "楽しいねえ");
		assertEquals(Integer.parseInt(afterId2.get(JDBCEntity.BIRTHDAY).toString()), 20240203);
	}

	/**
	 * <pre>
	 * {@link JdbcTemplate#batchUpdate(String, org.springframework.jdbc.core.BatchPreparedStatementSetter)}
	 * を用いることでデータを一括更新する
	 * {@code List<JDBCEntity>}に挿入したいデータをセットすることで
	 * データを一括挿入する
	 * </pre>
	 */
	@Test
	void testBatchInsert() {
		//挿入データのセット
		List<JDBCEntity> entityList = new ArrayList<>();
		entityList.add(new JDBCEntity(10, "Junit", "たのすいーーーー", 20240202));
		entityList.add(new JDBCEntity(20, "Junit", "楽しいねえ", 20240203));
		//データの一括挿入
		int insertCount = dao.batchInsert(entityList);

		//データの挿入確認
		assertEquals(insertCount, 2);

		var insertData1 = dao.findById(10);
		assertEquals(insertData1.get(JDBCEntity.FIRST_NAME.toString().strip()), "Junit");
		assertEquals(insertData1.get(JDBCEntity.LAST_NAME.toString().strip()), "たのすいーーーー");
		assertEquals(Integer.parseInt(insertData1.get(JDBCEntity.BIRTHDAY).toString()), 20240202);

		var insertData2 = dao.findById(20);
		assertEquals(insertData2.get(JDBCEntity.FIRST_NAME.toString().strip()), "Junit");
		assertEquals(insertData2.get(JDBCEntity.LAST_NAME.toString().strip()), "楽しいねえ");
		assertEquals(Integer.parseInt(insertData2.get(JDBCEntity.BIRTHDAY).toString()), 20240203);
	}

	/**
	 * {@link JdbcTemplate#batchUpdate(String, org.springframework.jdbc.core.BatchPreparedStatementSetter)}
	 * を用いることでデータを一括削除する
	 * {@code List<JDBCEntity>}に削除したいデータをセットすることで
	 * データを一括削除する
	 * セットする値はid（プライマリキー）のみでOK
	 */
	@Test
	void testBatchDelete() {
		//削除したいデータのidをセット
		List<JDBCEntity> entityList = new ArrayList<>();
		entityList.add(new JDBCEntity(1, null, null, 0));
		entityList.add(new JDBCEntity(2, null, null, 0));
		//データの一括削除
		int deleteCount = dao.batchDelete(entityList);

		//データの削除確認
		assertEquals(deleteCount, 2);

		Map<String, Object> afterId1 = dao.findById(1);
		assertTrue(afterId1.size() == 0);

		Map<String, Object> afterId2 = dao.findById(2);
		assertTrue(afterId2.size() == 0);
	}

	/**
	 * <pre>
	 * {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper)}
	 * を用いることで、テーブル内のデータを{@link JDBCEntity}のフィールでのセットした状態で取得
	 * </pre>
	 */
	@Test
	void testGetAllJDBCEEntity() {
		//データの取得
		List<JDBCEntity> dataList = dao.getAllJDBCEntity();

		//取得データの確認
		assertTrue(dataList.size() == 4);

		//本来は全データ確認した方が良いが、ここではidが2のデータのみ確認
		List<JDBCEntity> IdTwoData = dataList.stream().filter(data -> data.getId() == 2).collect(Collectors.toList());
		assertTrue(IdTwoData.size() == 1);
		//確実に取得データは1つなのでget(0)する
		JDBCEntity twoData = IdTwoData.get(0);

		assertEquals(twoData.getId(), 2);
		assertEquals(twoData.getBirth_day(), 20240101);
		assertEquals(twoData.getFirst_name(), "テスト");
		assertEquals(twoData.getLast_name(), "二郎");
	}

	/**
	 * <pre>
	 * {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper)}
	 * を用いて、引数に取得したいデータのid（プライマリキー）を渡すことで、
	 * テーブル内のデータを{@link JDBCEntity}のフィールでのセットした状態で取得
	 * </pre>
	 */
	@Test
	void testGetJDBCEntityById() {
		//データの取得
		JDBCEntity data = dao.getJDBCEntityById(2);
		//取得データの確認
		assertEquals(data.getId(), 2);
		assertEquals(data.getBirth_day(), 20240101);
		assertEquals(data.getFirst_name(), "テスト");
		assertEquals(data.getLast_name(), "二郎");
	}

	/**
	 * <pre>
	 * {@link JdbcTemplate#execute(String)}
	 * を用いることでテーブルを削除
	 * 引数に削除したいテーブル名を渡すことでテーブルを削除する
	 * 
	 * テストは通っているが
	 * テーブルを消してしまうので、Disabled
	 * </pre>
	 */
	@Test
	@Disabled
	void testDrop() {
		//テーブルの削除
		dao.executeDrop(JDBCEntity.TEST);

		//削除したテーブルを取得しようとすることで、テーブルの削除確認
		assertThrows(DataAccessException.class, () -> {
			dao.getAllJDBCEntity();
		});
	}

	/**
	 * <pre>
	 * {@link JdbcTemplate#execute(String)}
	 * を用いてテーブルを作成する
	 * 第一引数に作成したいテーブル名、第二引数にカラム、第三引数にプライマリキーに設定したいカラム
	 * を渡す
	 * 第二引数のカラムは、key:カラム名、value:型やNULL許容など
	 * をセットする
	 * </pre>
	 */
	@Test
	void testExecuteCreate() {
		//カラムのセット
		Map<String, String> columnInfo = new HashMap<>();
		columnInfo.put("id", "INT");
		columnInfo.put("name", "char(50)");
		columnInfo.put("comment", "char(50)");
		columnInfo.put("message", "char(100)");
		//プライマリキーのセット
		List<String> primaryList = new ArrayList<>();
		primaryList.add("id");
		primaryList.add("name");
		//テーブル作成
		dao.executeCreate("CREATE_TABLE", columnInfo, primaryList);
		//テーブルの作成確認
		List<String> tableNameList = new createTable().GetTableNames();
		assertTrue(tableNameList.contains("CREATE_TABLE"));
	}
}

/**
 * @author Takumi
 * {@link JDBCTempDaoTest#testExecuteCreate()}のテーブル作成確認用クラス
 */
class createTable {

	private final JdbcTemplate jdbc;

	/**
	 * h2データベースの設定
	 */
	public createTable() {
		jdbc = new JdbcTemplate();
		jdbc.setDataSource(DataSourceBuilder.create()
				.driverClassName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy")
				.url("jdbc:log4jdbc:h2:mem:hogedb")
				.username("sa")
				.password("")
				.build());
	}

	/**
	 * @return　存在するテーブル名のリスト
	 */
	public List<String> GetTableNames() {
		List<Map<String, Object>> tableData = jdbc.queryForList("SELECT"
				+ " TBL.TABLE_NAME AS TABLE_NAME "
				+ "FROM"
				+ " INFORMATION_SCHEMA.TABLES AS TBL "
				+ "WHERE"
				+ " TBL.TABLE_SCHEMA =  SCHEMA()");

		List<String> tableNameList = new ArrayList<>();
		for (Map<String, Object> data : tableData) {
			tableNameList.add(data.get("TABLE_NAME").toString());
		}

		return tableNameList;

	}

}
