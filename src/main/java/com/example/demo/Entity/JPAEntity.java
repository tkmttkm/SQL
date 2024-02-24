package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * JPAのテストエンティティ
 * @author Takumi
 *
 */
@Entity
@Data
@Table(name = "テストメンバー")
public class JPAEntity {
	
	@Id
	private Integer id;
	public JPAEntity(Integer id, String 姓, String 名, Integer 誕生日) {
		super();
		this.id = id;
		this.姓 = 姓;
		this.名 = 名;
		this.誕生日 = 誕生日;
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	JPAEntity() {}

	private String 姓;
	private String 名;
	private Integer 誕生日;
}
