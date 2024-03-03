package com.example.demo.Repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Dao.JDBCTempDao;
import com.example.demo.Entity.JDBCEntity;

@SpringBootTest
@Transactional
class JDBCTempDaoTest {
	
	@Autowired
	private JDBCTempDao dao;

	@Test
	void testFindAll() {
		var dataList = dao.findAll();
		assertTrue(dataList.size() == 4);
	}

	@Test
	void testFindById() {
		var data = dao.findById(1);
		
		assertEquals(data.get(JDBCEntity.BIRTHDAY), 20240101);
	}

	@Test
	void testUpdateById() {
		Map<String, Object> beforeData = dao.findById(1);
		assertEquals(beforeData.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(beforeData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");
		
		Map<String, String> updateMap = new TreeMap<>();
		updateMap.put(JDBCEntity.FIRST_NAME, "更新した");
		updateMap.put(JDBCEntity.LAST_NAME, "太郎くん");
		int updateCount = dao.updateById(1, updateMap);
		assertTrue(updateCount == 1);
		
		Map<String, Object> updateData = dao.findById(1);
		assertEquals(updateData.get(JDBCEntity.FIRST_NAME).toString().strip(), "更新した");
		assertEquals(updateData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎くん");
	}

	@Test
	void testDeleteById() {
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
		assertTrue("たのすいーーーー".equals(afterId1.get(JDBCEntity.FIRST_NAME.toString().strip())));
		assertTrue("楽しいねえ".equals(afterId2.get(JDBCEntity.FIRST_NAME.toString().strip())));
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
}
