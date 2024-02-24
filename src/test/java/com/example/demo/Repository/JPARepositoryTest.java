/**
 * 
 */
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
		assertEquals(data.get(0).get名().strip(), "太郎");
	}

	@Test
	void findByIdTest() {
		Optional<JPAEntity> dataOpt = repository.findById(1);
		JPAEntity data = dataOpt.orElse(null);
		
		assertEquals(data.get誕生日(), 20240101);
		assertEquals(data.get名().strip(), "太郎");
	}
	
	@Test
	void saveTest() {
		assertFalse(repository.existsById(9));
		
		JPAEntity insertEntity = new JPAEntity(9,"テストくん", "テストちゃん", 20200202);
		repository.save(insertEntity);
		assertTrue(repository.existsById(9));
		
		Optional<JPAEntity> insertDataOpt = repository.findById(9);
		JPAEntity insertData = insertDataOpt.orElse(null);
		
		assertEquals(insertData.get姓().strip(), "テストくん");
		assertEquals(insertData.get名().strip(), "テストちゃん");
		assertEquals(insertData.get誕生日(), 20200202);
	}
	
	@Test
	void deleteTest() {
		assertTrue(repository.existsById(1));
		
		repository.deleteById(1);
		
		assertFalse(repository.existsById(1));
	}
}
