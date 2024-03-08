package com.example.demo.Entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JDBCテスト用
 * @author Takumi
 *
 */
@Getter
@AllArgsConstructor
public class JDBCEntity {

	private int id;
	private String first_name;
	private String last_name;
	private int birth_day;

	public static final String TEST = "test_table";
	public static final String ID = "id";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String BIRTHDAY = "birth_day";

	/**
	 * @return
	 */
	public Map<String, String> getCoumn_ValueMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();

		map.put(ID, Integer.valueOf(id).toString());
		map.put(FIRST_NAME, first_name);
		map.put(LAST_NAME, last_name);
		map.put(BIRTHDAY, Integer.valueOf(birth_day).toString());

		return map;
	}
	
	/**
	 * @return
	 */
	public static List<String> GetSetQueryList_forBatchUpdate() {
		List<String> setQueryList = new ArrayList<String>();

		setQueryList.add(JDBCEntity.ID);
		setQueryList.add(JDBCEntity.FIRST_NAME);
		setQueryList.add(JDBCEntity.LAST_NAME);
		setQueryList.add(JDBCEntity.BIRTHDAY);

		return setQueryList;
	}
}
