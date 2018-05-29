package com.bonc.uni.dcci.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 优先级别
 * @author futao
 * 2017年9月11日
 */
public enum LevelType {
	NOEMERGENCY("不紧急"),COMMONLY("一般"),EMERGENCY("紧急"),VERYEMERGENCY("非常紧急");
	
	private String value;

	private LevelType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static JSONArray toJSONArray() {
		JSONArray jsons  = new JSONArray();
		for(LevelType craw : LevelType.values()) {
			JSONObject json = new JSONObject();
			json.put(craw.toString(), craw.getValue());
			jsons.add(json);
		}
		return jsons;
	}
}
