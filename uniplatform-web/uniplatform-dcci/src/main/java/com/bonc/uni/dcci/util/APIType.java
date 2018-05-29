package com.bonc.uni.dcci.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 数据api枚举
 * @author futao
 * 2017年9月22日
 */
public enum APIType {

	VOICE("语音识别"),IMAGE("图像识别"),SOCIAL("社交媒体"),TRAFFIC("交通地理"),METEOROLOGICAL("气象"),SCIENTIFIC("科研"),NEWS("新闻"),OTHER("其它");
	
	private String value;

	private APIType(String value) {
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
		for(APIType craw : APIType.values()) {
			JSONObject json = new JSONObject();
			json.put(craw.toString(), craw.getValue());
			jsons.add(json);
		}
		return jsons;
	}
	
	/**api 状态
	 * @author futao
	 * 2017年9月22日
	 */
	public enum Status{
		
		OPEN("开启"),CLOSE("关闭");
		
		private String value;

		private Status(String value) {
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
			for(APIType.Status craw : APIType.Status.values()) {
				JSONObject json = new JSONObject();
				json.put(craw.toString(), craw.getValue());
				jsons.add(json);
			}
			return jsons;
		}
	}
	
	/**
	 * 单位
	 * @author futao
	 * 2017年9月22日
	 */
	public enum Unit {
		FREQUENCY("次"),DAY("天"),WEEK("周"),MONTH("月"),YEAR("年");
		
		private String value;
		
		private Unit(String value) {
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
			for(APIType.Unit craw : APIType.Unit.values()) {
				JSONObject json = new JSONObject();
				json.put(craw.toString(), craw.getValue());
				jsons.add(json);
			}
			return jsons;
		}
	}
	
	/**
	 * 编程语言
	 * @author futao
	 * 2017年9月25日
	 */
	public enum Language{
		JAVA,PYTHON,GOLANG;
	}
	
	public enum Method{
		GET, POST;
	}
	
	/**
	 * 数据类型
	 * @author futao
	 * 2017年9月25日
	 */
	public enum dataType{
		STRING,INT,BOOLEAN;
	}
}
