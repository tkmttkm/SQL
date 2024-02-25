package com.example.demo.Repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.JDBCEntity;

@SpringBootTest
@Transactional
class NamedJDBCTest {

	@Autowired
	private NamedJDBCRepository repository;

	@Test
	void testFindAll() {
		List<Map<String, Object>> allData = repository.findAll();

		assertTrue(allData.size() == 4);
	}

	@Test
	void testFindById() {
		Map<String, Object> data = repository.findById(1);

		assertEquals(data.get(JDBCEntity.BIRTHDAY), 20240101);
	}

	@Test
	void testUpdate() {
		Map<String, Object> beforeData = repository.findById(1);
		assertEquals(beforeData.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(beforeData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");
		
		Map<String, String> updateMap = new HashMap<>();
		updateMap.put(JDBCEntity.FIRST_NAME, "更新した");
		updateMap.put(JDBCEntity.LAST_NAME, "太郎くん");
		int updateCount = repository.updateById(1, updateMap);
		assertTrue(updateCount == 1);
		
		Map<String, Object> updateData = repository.findById(1);
		assertEquals(updateData.get(JDBCEntity.FIRST_NAME).toString().strip(), "更新した");
		assertEquals(updateData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎くん");
	}
	
	@Test
	void testDelete() {
		Map<String, Object> beforeData = repository.findById(1);
		assertEquals(beforeData.get(JDBCEntity.FIRST_NAME).toString().strip(), "テスト");
		assertEquals(beforeData.get(JDBCEntity.LAST_NAME).toString().strip(), "太郎");
		
		int deleteCount = repository.deleteById(1);
		assertTrue(deleteCount == 1);
		
		List<Map<String, Object>> deleteData = repository.findAll();
		assertFalse(deleteData.contains(beforeData));
	}
	
	@Test
	void batchUpdateTest() {
		List<JDBCEntity> entityList = new ArrayList<>();
		entityList.add(new JDBCEntity(1, "Junit", "たのすいーーーー", 20240202));
		entityList.add(new JDBCEntity(2, "Junit", "たのすいーーーー!!!!!!!!", 20240203));
		
		int updateCount = repository.batchUpdate(entityList);
		assertEquals(updateCount, 2);
		var afterId1 = repository.findById(1);
		var afterId2 = repository.findById(2);
		assertTrue("たのすいーーーー".equals(afterId1.get(JDBCEntity.LAST_NAME.toString())));
		assertTrue("たのすいーーーー!!!!!!!!".equals(afterId2.get(JDBCEntity.LAST_NAME.toString())));
	}
	
	@Test
	void batchDeleteTest() {
		List<JDBCEntity> entityList = new ArrayList<>();
		entityList.add(new JDBCEntity(1, null, null, 0));
		entityList.add(new JDBCEntity(2, null, null, 0));
		
		int deleteCount = repository.batchDelete(entityList);
		assertEquals(deleteCount, 2);
		var afterId1 = repository.findById(1);
		var afterId2 = repository.findById(2);
		assertTrue(afterId1.size() == 0);
		assertTrue(afterId2.size() == 0);
	}
}
