package com.bonc.uni.dcci.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 采集类型
 * @author futao
 * 2017年9月7日
 */
public enum CrawlerType {

	DOMESTIC_NEWS("国内新闻"),DOMESTIC_BLOG("国内博客"),DOMESTIC_FORUM("国内论坛"),DOMESTIC_ELECTRONIC("国内电子报"),DOMESTIC_PHONE("国内手机新闻"),
	BIDDING_NEWS("招投标新闻"),DOMESTIC_RETAILERS("国内电商"),HONGKONG_MACAO_TAIWAN_NEWS("港澳台新闻"),HONGKONG_MACAO_TAIWAN_FORUM("港澳台论坛"),
	DOMESTIC_VIDEO("国内视频"),DOMESTIC_WEIXIN("国内微信"),ABROAD_FORUM("国外论坛"),ABROAD_BLOG("国外博客"),ABROAD_NEWS("国外新闻");
	
	private String value;

	private CrawlerType(String value) {
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
		for(CrawlerType craw : CrawlerType.values()) {
			JSONObject json = new JSONObject();
			json.put(craw.toString(), craw.getValue());
			jsons.add(json);
		}
		return jsons;
	}
}
