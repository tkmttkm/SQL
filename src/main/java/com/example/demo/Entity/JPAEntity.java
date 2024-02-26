package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPAのテストエンティティ
 * @author Takumi
 *
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "test_table")
public class JPAEntity {
	
	@Id
	private Integer id;
	private String first_name;
	private String last_name;
	private Integer birth_day;
}
