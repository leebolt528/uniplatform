package com.bonc.uni.nlp.utils;

import java.io.IOException;
import java.util.Properties;

import com.bonc.usdp.odk.logmanager.LogManager;


public class ServerUtil {

	private static Properties pro;

	private ServerUtil(){
		
	}
	
	static {
		pro = new Properties();
		try {
			pro.load(ServerUtil.class.getResourceAsStream("/db.properties"));
		} catch (IOException e) {
			LogManager.Exception(e);
		}
	}

	/*
	 * 获取键值
	 */
	public static String getConfig(String key) {
		String value = pro.getProperty(key);
		return value;
	}

}
