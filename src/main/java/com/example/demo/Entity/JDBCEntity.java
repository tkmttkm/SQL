package com.example.demo.Entity;

import java.util.Map;
import java.util.TreeMap;

import lombok.Data;

/**
 * JDBCテスト用
 * @author Takumi
 *
 */
@Data
public class JDBCEntity {
	public JDBCEntity(int id, String 姓, String 名, int 誕生日) {
		this.id = id;
		this.姓 = 姓;
		this.名 = 名;
		this.誕生日 = 誕生日;
	}

	private int id;
	private String 姓;
	private String 名;
	private int 誕生日;

	public static final String テストメンバー = "テストメンバー";
	public static final String ID = "id";
	public static final String FIRST_NAME = "姓";
	public static final String LAST_NAME = "名";
	public static final String BIRTHDAY = "誕生日";

	/**
	 * @return
	 */
	public Map<String, String> getCoumn_ValueMap() {
		Map<String, String> map = new TreeMap<String, String>();

		map.put(ID, Integer.valueOf(id).toString());
		map.put(FIRST_NAME, 姓);
		map.put(LAST_NAME, 名);
		map.put(BIRTHDAY, Integer.valueOf(誕生日).toString());

		return map;
	}
}
