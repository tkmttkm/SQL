package com.example.demo.Repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.JPAEntity;

/**
 * {@link JPARepository}のテスト
 * @author Takumi
 *
 */
@SpringBootTest
@Transactional
class JPARepositoryTest {

	@Autowired
	private JPARepository repository;

	@Test
	void findAllTest() {
		List<JPAEntity> data = repository.findAll();
		
		assertEquals(data.size(), 4);
		assertEquals(data.get(0).getId(), 1);
		assertEquals(data.get(0).getFirst_name().strip(), "テスト");
		assertEquals(data.get(0).getLast_name().strip(), "太郎");
		assertEquals(data.get(0).getBirth_day(), 20240101);
	}

	@Test
	void findByIdTest() {
		Optional<JPAEntity> dataOpt = repository.findById(1);
		JPAEntity data = dataOpt.orElse(null);
		
		assertEquals(data.getId(), 1);
		assertEquals(data.getFirst_name().strip(), "テスト");
		assertEquals(data.getLast_name().strip(), "太郎");
		assertEquals(data.getBirth_day(), 20240101);
	}
	
	@Test
	void saveTest() {
		assertFalse(repository.existsById(9));
		
		JPAEntity insertEntity = new JPAEntity(9,"テストくん", "テストちゃん", 20200202);
		repository.save(insertEntity);
		
		assertTrue(repository.existsById(9));
		
		Optional<JPAEntity> insertDataOpt = repository.findById(9);
		JPAEntity insertData = insertDataOpt.orElse(null);
		
		assertEquals(insertData.getFirst_name().strip(), "テストくん");
		assertEquals(insertData.getLast_name().strip(), "テストちゃん");
		assertEquals(insertData.getBirth_day(), 20200202);
	}
	
	@Test
	void deleteTest() {
		assertTrue(repository.existsById(1));
		
		repository.deleteById(1);
		
		assertFalse(repository.existsById(1));
	}
}
