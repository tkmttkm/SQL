package com.example.demo.Dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
class NamedJDBCDaoTest {

	@Autowired
	private NamedJDBCDao dao;

	/**
	 * <pre>
	 * テーブルデータを{@code List<Map<String, Object>>}ですべて取得
	 * </pre>
	 */
	@Test
	void testFindAll() {
		List<Map<String, Object>> allData = dao.findAll();
		assertTrue(allData.size() == 4);
	}

	/**
	 * <pre>
	 * 取得したいデータのid（プライマリキー）を渡してデータを取得する
	 * </pre>
	 */
	@Test
	void testFindById() {
		Map<String, Object> data = dao.findById(1);

		//取得データの確認
		assertEquals(data.get(JDBCEntity.BIRTHDAY), 20240101);
		assertEquals(data.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(data.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");
	}

	/**
	 * <pre>
	 * 更新したいデータの{@code Map<String, String}を作成。
	 * key:カラム名 value:更新したい値
	 * 引数にid（プライマリキー）とこの{@code Map<STring, String>}を渡すことでデータを更新
	 * </pre>
	 */
	@Test
	void testUpdateById() {
		//更新前のデータ確認
		Map<String, Object> beforeData = dao.findById(1);
		assertEquals(beforeData.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(beforeData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");

		//データの更新
		Map<String, String> updateMap = new HashMap<>();
		updateMap.put(JDBCEntity.FIRST_NAME, "更新した");
		updateMap.put(JDBCEntity.LAST_NAME, "太郎くん");
		int updateCount = dao.updateById(1, updateMap);
		assertTrue(updateCount == 1);

		//更新データの確認
		Map<String, Object> updateData = dao.findById(1);
		assertEquals(updateData.get(JDBCEntity.FIRST_NAME).toString().strip(), "更新した");
		assertEquals(updateData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎くん");
	}

	/**
	 * <pre>
	 * 挿入したいデータの{@code Map<String, String>}を作成。
	 * 引数にこの{@code Map<String, String>}を渡すことでデータを挿入する。
	 * </pre>
	 */
	@Test
	void testInsert() {
		//挿入データの準備
		Map<String, String> insertData = new LinkedHashMap<>();
		insertData.put(JDBCEntity.ID, "10");
		insertData.put(JDBCEntity.FIRST_NAME, "インサート");
		insertData.put(JDBCEntity.LAST_NAME, "太郎");
		insertData.put(JDBCEntity.BIRTHDAY, "20200202");

		//データの挿入
		dao.insert(insertData);

		//素運輸データの確認
		Map<String, Object> data = dao.findById(10);
		assertEquals(data.get(JDBCEntity.FIRST_NAME), "インサート");
		assertEquals(data.get(JDBCEntity.LAST_NAME), "太郎");
		assertEquals(data.get(JDBCEntity.BIRTHDAY), 20200202);
	}

	/**
	 * <pre>
	 * データの削除
	 *引数に削除したいデータのid（プライマリキー）を渡すことでデータを削除する
	 * </pre>
	 */
	@Test
	void testDelete() {
		//削除前のデータ確認
		Map<String, Object> beforeData = dao.findById(1);
		assertEquals(beforeData.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(beforeData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");

		//データの削除
		int deleteCount = dao.deleteById(1);
		assertTrue(deleteCount == 1);

		//削除確認
		List<Map<String, Object>> deleteData = dao.findAll();
		assertFalse(deleteData.contains(beforeData));
	}

	/**
	 * <pre>
	 * データの一括更新
	 * {@code List<JDBCEntity>}に更新したいデータをセットし、引数に渡すことで
	 * データを一括更新する
	 * </pre>
	 */
	@Test
	void testBatchUpdate() {
		//更新データのセット
		List<JDBCEntity> entityList = new ArrayList<>();
		entityList.add(new JDBCEntity(1, "Junit", "たのすいーーーー", 20240202));
		entityList.add(new JDBCEntity(2, "Junit", "楽しいねえ", 20240203));

		//データの更新
		int updateCount = dao.batchUpdate(entityList);
		assertEquals(updateCount, 2);

		//更新データの確認
		Map<String, Object> afterId1 = dao.findById(1);
		assertEquals(afterId1.get(JDBCEntity.FIRST_NAME.toString().strip()), "Junit");
		assertEquals(afterId1.get(JDBCEntity.LAST_NAME.toString().strip()), "たのすいーーーー");
		assertEquals(Integer.parseInt(afterId1.get(JDBCEntity.BIRTHDAY).toString()), 20240202);

		Map<String, Object> afterId2 = dao.findById(2);
		assertEquals(Integer.parseInt(afterId1.get(JDBCEntity.BIRTHDAY).toString()), 20240202);
		assertEquals(afterId2.get(JDBCEntity.FIRST_NAME.toString().strip()), "Junit");
		assertEquals(afterId2.get(JDBCEntity.LAST_NAME.toString().strip()), "楽しいねえ");
		assertEquals(Integer.parseInt(afterId2.get(JDBCEntity.BIRTHDAY).toString()), 20240203);
	}

	/**
	 * <pre>
	 * データの一括挿入
	 * {@code List<JDBCEntity>}に挿入したいデータをセットすることで、
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
		assertEquals(insertCount, 2);

		//挿入データの確認
		Map<String, Object> insertData1 = dao.findById(10);
		assertEquals(insertData1.get(JDBCEntity.FIRST_NAME.toString().strip()), "Junit");
		assertEquals(insertData1.get(JDBCEntity.LAST_NAME.toString().strip()), "たのすいーーーー");
		assertEquals(Integer.parseInt(insertData1.get(JDBCEntity.BIRTHDAY).toString()), 20240202);

		Map<String, Object> insertData2 = dao.findById(20);
		assertEquals(insertData2.get(JDBCEntity.FIRST_NAME.toString().strip()), "Junit");
		assertEquals(insertData2.get(JDBCEntity.LAST_NAME.toString().strip()), "楽しいねえ");
		assertEquals(Integer.parseInt(insertData2.get(JDBCEntity.BIRTHDAY).toString()), 20240203);
	}

	/**
	 * <pre>
	 * データの一括削除
	 * {@code List<JDBCEntity>}に削除したいデータをセットし、引数に渡すことで
	 * データを一括削除する。
	 * セットする値はid（プライマリキー）のみでOK
	 * </pre>
	 */
	@Test
	void testBatchDelete() {
		//削除したいデータのidをセット
		List<JDBCEntity> entityList = new ArrayList<>();
		entityList.add(new JDBCEntity(1, null, null, 0));
		entityList.add(new JDBCEntity(2, null, null, 0));

		//データの一括削除
		int deleteCount = dao.batchDelete(entityList);
		assertEquals(deleteCount, 2);

		//データの削除確認
		Map<String, Object> afterId1 = dao.findById(1);
		assertTrue(afterId1.size() == 0);
		Map<String, Object> afterId2 = dao.findById(2);
		assertTrue(afterId2.size() == 0);
	}

	/**
	 * <pre>
	 * テーブルの前データを{@code List<JDBCEntity>}として取得する
	 * </pre>
	 */
	@Test
	void testGetAllJDBCEEntity() {
		//データの取得
		List<JDBCEntity> dataList = dao.getAllJDBCEntity();
		assertTrue(dataList.size() == 4);

		//ここでは、idが２のデータのみを確認する。（本来ならば前データを確認するべき）
		List<JDBCEntity> IdTwoData = dataList.stream().filter(data -> data.getId() == 2)
				.collect(Collectors.toList());
		assertTrue(IdTwoData.size() == 1);

		//データは必ず1つ
		JDBCEntity twoData = IdTwoData.get(0);

		//取得データの確認
		assertEquals(twoData.getId(), 2);
		assertEquals(twoData.getBirth_day(), 20240101);
		assertEquals(twoData.getFirst_name(), "テスト");
		assertEquals(twoData.getLast_name(), "二郎");
	}

	/**
	 * <pre>
	 * 取得したいデータのid（プライマリキー）を渡すことで、
	 * データを{@link JDBCEntity}で取得
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
	 * 削除したいテーブルの名前を渡すことでテーブルを削除する
	 * ここでは、テーブルを削除してしまい、JUnitが通らなくなるのでDisabled
	 * </pre>
	 */
	@Test
	@Disabled
	void testDrop() {
		dao.executeDrop(JDBCEntity.TEST);
		assertThrows(DataAccessException.class, () -> {
			dao.getAllJDBCEntity();
		});
	}

	/**
	 * <pre>
	 * テーブルの作成
	 * {@code Map<String, String>}でkeyにカラム名、valueに型やNULL許容などをセット、
	 * また{@code List<String>}にプライマリキーにしたいカラム名をセットし、
	 * 引数に作成したいテーブル名とともにこの二つを渡すことで、テーブルを作成する
	 * </pre>
	 */
	@Test
	void testExecuteCreate() {
		Map<String, String> columnInfo = new HashMap<>();
		columnInfo.put("id", "INT");
		columnInfo.put("name", "char(50)");
		columnInfo.put("comment", "char(50)");
		columnInfo.put("message", "char(100)");

		List<String> primaryList = new ArrayList<>();
		primaryList.add("id");
		primaryList.add("name");

		dao.executeCreate("CREATE_TABLE", columnInfo, primaryList);

		List<String> tableNameList = new createTableTest().GetTableNams();
		assertTrue(tableNameList.contains("CREATE_TABLE"));
	}
}

/**
 * @author Takumi
 * {@link NamedJDBCDaoTest#testExecuteCreate()}のテーブル作成確認用クラス
 */
class createTableTest {

	private final JdbcTemplate jdbc;

	/**
	 * h2データベースの設定
	 */
	public createTableTest() {
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
	public List<String> GetTableNams() {
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
