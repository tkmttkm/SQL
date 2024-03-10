package com.example.demo.Repository;

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

import com.example.demo.Dao.NamedJDBCDao;
import com.example.demo.Entity.JDBCEntity;

@SpringBootTest
@Transactional
class NamedJDBCDaoTest {

	@Autowired
	private NamedJDBCDao dao;

	@Test
	void testFindAll() {
		List<Map<String, Object>> allData = dao.findAll();
		assertTrue(allData.size() == 4);
	}

	@Test
	void testFindById() {
		Map<String, Object> data = dao.findById(1);

		assertEquals(data.get(JDBCEntity.BIRTHDAY), 20240101);
		assertEquals(data.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(data.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");
	}

	@Test
	void testUpdateById() {
		Map<String, Object> beforeData = dao.findById(1);
		assertEquals(beforeData.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(beforeData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");
		
		Map<String, String> updateMap = new HashMap<>();
		updateMap.put(JDBCEntity.FIRST_NAME, "更新した");
		updateMap.put(JDBCEntity.LAST_NAME, "太郎くん");
		int updateCount = dao.updateById(1, updateMap);
		assertTrue(updateCount == 1);
		
		Map<String, Object> updateData = dao.findById(1);
		assertEquals(updateData.get(JDBCEntity.FIRST_NAME).toString().strip(), "更新した");
		assertEquals(updateData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎くん");
	}
	
	@Test
	void testInsert() {
		Map<String, String> insertData = new LinkedHashMap<>();
		insertData.put(JDBCEntity.ID, "10");
		insertData.put(JDBCEntity.FIRST_NAME, "インサート");
		insertData.put(JDBCEntity.LAST_NAME, "太郎");
		insertData.put(JDBCEntity.BIRTHDAY, "20200202");
		
		dao.insert(insertData);
		
		var data = dao.findById(10);
		assertEquals(data.get(JDBCEntity.FIRST_NAME), "インサート");
		assertEquals(data.get(JDBCEntity.LAST_NAME), "太郎");
		assertEquals(data.get(JDBCEntity.BIRTHDAY), 20200202);
	}
	
	@Test
	void testDelete() {
		Map<String, Object> beforeData = dao.findById(1);
		assertEquals(beforeData.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(beforeData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");
		
		int deleteCount = dao.deleteById(1);
		assertTrue(deleteCount == 1);
		
		List<Map<String, Object>> deleteData = dao.findAll();
		assertFalse(deleteData.contains(beforeData));
	}
	
	@Test
	void testBatchUpdate() {
		List<JDBCEntity> entityList = new ArrayList<>();
		entityList.add(new JDBCEntity(1, "Junit", "たのすいーーーー", 20240202));
		entityList.add(new JDBCEntity(2, "Junit", "楽しいねえ", 20240203));
		
		int updateCount = dao.batchUpdate(entityList);
		assertEquals(updateCount, 2);
		var afterId1 = dao.findById(1);
		var afterId2 = dao.findById(2);
		assertEquals(afterId1.get(JDBCEntity.FIRST_NAME.toString().strip()),"Junit");
		assertEquals(afterId1.get(JDBCEntity.LAST_NAME.toString().strip()),"たのすいーーーー");
		assertEquals(Integer.parseInt(afterId1.get(JDBCEntity.BIRTHDAY).toString()),20240202);
		assertEquals(afterId2.get(JDBCEntity.FIRST_NAME.toString().strip()),"Junit");
		assertEquals(afterId2.get(JDBCEntity.LAST_NAME.toString().strip()),"楽しいねえ");
		assertEquals(Integer.parseInt(afterId2.get(JDBCEntity.BIRTHDAY).toString()),20240203);
	}
	
	@Test
	void testBatchInsert() {
		List<JDBCEntity> entityList = new ArrayList<>();
		entityList.add(new JDBCEntity(10, "Junit", "たのすいーーーー", 20240202));
		entityList.add(new JDBCEntity(20, "Junit", "楽しいねえ", 20240203));

		int insertCount = dao.batchInsert(entityList);
		assertEquals(insertCount, 2);
		var insertData1 = dao.findById(10);
		var insertData2 = dao.findById(20);
		assertEquals(insertData1.get(JDBCEntity.FIRST_NAME.toString().strip()), "Junit");
		assertEquals(insertData1.get(JDBCEntity.LAST_NAME.toString().strip()), "たのすいーーーー");
		assertEquals(Integer.parseInt(insertData1.get(JDBCEntity.BIRTHDAY).toString()), 20240202);
		assertEquals(insertData2.get(JDBCEntity.FIRST_NAME.toString().strip()), "Junit");
		assertEquals(insertData2.get(JDBCEntity.LAST_NAME.toString().strip()), "楽しいねえ");
		assertEquals(Integer.parseInt(insertData2.get(JDBCEntity.BIRTHDAY).toString()), 20240203);
	}
	
	@Test
	void testBatchDelete() {
		List<JDBCEntity> entityList = new ArrayList<>();
		entityList.add(new JDBCEntity(1, null, null, 0));
		entityList.add(new JDBCEntity(2, null, null, 0));
		
		int deleteCount = dao.batchDelete(entityList);
		assertEquals(deleteCount, 2);
		var afterId1 = dao.findById(1);
		var afterId2 = dao.findById(2);
		assertTrue(afterId1.size() == 0);
		assertTrue(afterId2.size() == 0);
	}
	
	@Test
	void testGetAllJDBCEEntity() {
		List<JDBCEntity> dataList = dao.getAllJDBCEntity();
		assertTrue(dataList.size() == 4);

		List<JDBCEntity> IdTwoData = dataList.stream().filter(data -> data.getId() == 2)
				.collect(Collectors.toList());
		assertTrue(IdTwoData.size() == 1);

		JDBCEntity twoData = IdTwoData.get(0);

		assertEquals(twoData.getId(), 2);
		assertEquals(twoData.getBirth_day(), 20240101);
		assertEquals(twoData.getFirst_name(), "テスト");
		assertEquals(twoData.getLast_name(), "二郎");
	}

	@Test
	void testGetJDBCEntityById() {
		JDBCEntity data = dao.getJDBCEntityById(2);

		assertEquals(data.getId(), 2);
		assertEquals(data.getBirth_day(), 20240101);
		assertEquals(data.getFirst_name(), "テスト");
		assertEquals(data.getLast_name(), "二郎");
	}
	
	@Test
	@Disabled
	void testDrop() {
		dao.executeDrop(JDBCEntity.TEST);
		assertThrows(DataAccessException.class, () -> {
			dao.getAllJDBCEntity();
		});
		
//		Map<String, String> columnInfo = new HashMap<>();
//		columnInfo.put("id", "INT NOT NULL AUTO_INCREMENT");
//		columnInfo.put("first_name", "VARCHAR(10) NOT NULL");
//		columnInfo.put("last_name", "VARCHAR(10) NOT NULL");
//		columnInfo.put("birth_day", "INT NULL");
//
//		List<String> primaryList = new ArrayList<>();
//		primaryList.add("id");
//
//		//消したテーブルを元に戻す
//		dao.executeCreate("test_table", columnInfo, primaryList);
//		List<JDBCEntity> entityList = new ArrayList<>();
//		entityList.add(new JDBCEntity(1,"テスト", "太郎", 20240101));
//		entityList.add(new JDBCEntity(2,"テスト", "二郎", 20240101));
//		entityList.add(new JDBCEntity(3,"テスト", "三郎", 20240101));
//		entityList.add(new JDBCEntity(4,"テスト", "花子", 20250101));
//
//		int insertCount = dao.batchInsert(entityList);
//		assertEquals(insertCount, 4);
	}

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
