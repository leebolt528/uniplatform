package com.bonc.uni.common.util;


import com.alibaba.fastjson.JSONObject;

/**
 * 构造任务频率
 * @author futao
 * 2017年9月8日
 */
public class TaskCronExpBuilder {

	/**
	 * 
	 * everyMinutes:每 几分钟执行. <br/> 
	 */
	public static String everyMinutesPer(int min) {
		return "0 " + min + " 0 * * ?";
	}
	
	/**
	 * everyHourPer:每小时的几分钟执. <br/> 
	 */
	public static String everyHourPer(int hour, int min) {
		return "0 " + min + " " + hour + " * * ?";
	}
	
	/**
	 * everyDayPer:每天第几小时执行. <br/> 
	 */
	public static String everyDayPer(int hour) {
		return " 0 0 " + hour + " * * ?";
	}
	
	/**
	 * everyWeekPer:每周几执行. <br/> 
	 *  1-7
	 */
	public static String everyWeekPer(int week) {
		return "0 0 0 ? * " + week;
	}
	
	/**
	 * everyMonthPer:每月几号执行. <br/> 
	 */
	public static String everyMonthPer(int day) {
		return "0 0 0 " + day + " * ?";
	}
	
	public static void main(String[] args) {
		
		JSONObject json = JSONObject.parseObject("{\"hour\":\"16\",\"min\":\"23\"}");
		int hour = json.getInteger("hour");
		int min = json.getInteger("min");
		System.out.println(TaskCronExpBuilder.everyHourPer(hour, min));
	}
}
