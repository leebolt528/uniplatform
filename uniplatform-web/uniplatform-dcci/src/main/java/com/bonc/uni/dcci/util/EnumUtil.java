package com.bonc.uni.dcci.util;

import net.sf.json.JSONArray;

/**
 * enum 工具类
 * @author futao
 * 2017年9月25日
 */
public class EnumUtil {

	public static JSONArray apiType = APIType.toJSONArray();
	
	public static JSONArray apiType_Status = APIType.Status.toJSONArray();
	
	public static JSONArray apiType_Unit = APIType.Unit.toJSONArray();
	
	public static JSONArray crawlerType = CrawlerType.toJSONArray();
	
	public static JSONArray levelType = LevelType.toJSONArray();
	
	public static JSONArray statusType = StatusType.toJSONArray();
	
	public static JSONArray statusType_TaskAssignType = StatusType.TaskAssignType.toJSONArray();
	
	public static JSONArray statusType_UrlSiteType = StatusType.UrlSiteType.toJSONArray();
	
}
