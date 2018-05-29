package com.bonc.uni.dcci.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 任务状态
 * @author futao
 * 2017年9月11日
 */
public enum StatusType {
	//执行：执行中       撤回：将已分配的任务撤回
	EXECUTE("执行"),UNDISTRIBUTED("未分配"),COMPLETE("完成"),RECALL("撤回");
	
	private String value;

	private StatusType(String value) {
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
		for(StatusType craw : StatusType.values()) {
			JSONObject json = new JSONObject();
			json.put(craw.toString(), craw.getValue());
			jsons.add(json);
		}
		return jsons;
	}
	
	/**
	 * url是否重复
	 * @author futao
	 * 2017年9月12日
	 */
	public enum UrlSiteType{
		REPEAT("url重复"),UNDISTRIBUTED("未分配"),CONFIGURING("正在配置"),FAIL("失败"),COMPLETE("完成");
		
		private String value;

		private UrlSiteType(String value) {
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
			for(UrlSiteType craw : UrlSiteType.values()) {
				JSONObject json = new JSONObject();
				json.put(craw.toString(), craw.getValue());
				jsons.add(json);
			}
			return jsons;
		}
	}
	
	public enum UrlSiteType2{
		ALL,REPEAT,UNDISTRIBUTED,CONFIGURING,FAIL,COMPLETE;
	}
	
	/**
	 * 任务分配方式
	 * @author futao
	 * 2017年9月12日
	 */
	public enum TaskAssignType{
		TOTAL("单一分配"),AVERAGE("平均分配");
		
		private String value;

		private TaskAssignType(String value) {
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
			for(TaskAssignType craw : TaskAssignType.values()) {
				JSONObject json = new JSONObject();
				json.put(craw.toString(), craw.getValue());
				jsons.add(json);
			}
			return jsons;
		}
	}
}
