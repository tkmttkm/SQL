package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * DataJDBCテスト用
 * @author Takumi
 *
 */
@Entity
@Getter
@Table(name = "テストメンバー")
public class DataJDBCEntity {
	@Id
	private Integer id;
	public DataJDBCEntity(Integer id, String 姓, String 名, Integer 誕生日) {
		this.id = id;
		this.姓 = 姓;
		this.名 = 名;
		this.誕生日 = 誕生日;
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	DataJDBCEntity() {}
	
	private String 姓;
	private String 名;
	private Integer 誕生日;
}
