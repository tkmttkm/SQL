package com.example.demo.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DataJDBCテスト用
 * @author Takumi
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("TEST")
public class DataJDBCEntity {
	@Id
	private Integer id;

	private String firstName;

	private String lastName;

	private Integer birthDay;
}