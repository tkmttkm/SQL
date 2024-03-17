package com.example.demo.Entity;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <pre>
 * テーブルtest_tableのマッピングクラス
 * </pre>
 * @author Takumi
 */
@Getter
@AllArgsConstructor
public class JDBCEntity {
 
	//カラム名
	/** プライマリキー */
	private int id;
	private String first_name;
	private String last_name;
	private int birth_day;

	//カラム名の文字列
	public static final String TEST = "test_table";
	public static final String ID = "id";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String BIRTHDAY = "birth_day";

	/**
	 * <pre>
	 * バッチアップデートなどで、カラム名と挿入したい値の順番を連携するために使用
	 * 同じリストを用いることで順番を対応づける
	 * </pre>
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
