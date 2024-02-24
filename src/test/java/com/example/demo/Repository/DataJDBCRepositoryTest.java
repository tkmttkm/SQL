package com.example.demo.Repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.DataJDBCEntity;

@SpringBootTest
@Transactional
class DataJDBCRepositoryTest {

	@Autowired
	private DataJDBCRepository repository;

	@Test
	void testFindAll() {
		Iterable<DataJDBCEntity> allData = repository.findAll();
		List<DataJDBCEntity> alldataList = new ArrayList<DataJDBCEntity>();
		
		Iterator<DataJDBCEntity> iterator = allData.iterator();
		while(iterator.hasNext()) {
			alldataList.add(iterator.next());
		}
		
		assertEquals(alldataList.size(), 4);
	}
	
	@Test
	void testFindById() {
		Optional<DataJDBCEntity> dataOpt = repository.findById(1);
		DataJDBCEntity data = dataOpt.isPresent() ? dataOpt.get() : null;
		
		assertEquals(data.get姓().strip(), "テスト");
		assertEquals(data.get名().strip(), "太郎");
		assertEquals(data.get誕生日(), 20240101);
	}
	
	@Test
	void testDeleteById() {
		assertTrue(repository.existsById(1));
		
		repository.deleteById(1);
		
		assertFalse(repository.existsById(1));
	}
	
	@Test
	void testSave() {
		Optional<DataJDBCEntity> beforeDataOpt = repository.findById(1);
		DataJDBCEntity beforeData = beforeDataOpt.isPresent() ? beforeDataOpt.get() : null;
		
		assertEquals(beforeData.get姓().strip(), "テスト");
		assertEquals(beforeData.get名().strip(), "太郎");
		assertEquals(beforeData.get誕生日(), 20240101);
		
		DataJDBCEntity data = new DataJDBCEntity(1, "テストテスト", "save太郎くん", 20240214);
		repository.save(data);
		
		Optional<DataJDBCEntity> updatedOpt = repository.findById(1);
		DataJDBCEntity updatedData = updatedOpt.isPresent() ? updatedOpt.get() : null;
		
		assertEquals(updatedData.get姓().strip(), "テストテスト");
		assertEquals(updatedData.get名().strip(), "save太郎くん");
		assertEquals(updatedData.get誕生日(), 20240214);
	}

}
