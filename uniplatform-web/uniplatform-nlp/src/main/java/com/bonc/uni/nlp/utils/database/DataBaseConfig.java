package com.bonc.uni.nlp.utils.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.bonc.uni.nlp.constant.SystemConstant;
import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

public class DataBaseConfig {

	/**
	 * druid 配置
	 */
	public static Map<String, String> dataBaseProp = new HashMap<>();
	
	public static String JDBC_DRIVER;
	public static String JDBC_URL;
	public static String DB_USERNAME;
	public static String DB_PASSWORD;
	public static String DRUID_INITIAL_SIZE; // 初始化时建立物理连接的个数。
	public static String DRUID_MAXACTIVE; // 最大连接池数量
	public static String DRUID_MINIDLE; // 最小连接池数量
	public static String DRUID_MAXWAIT; // 获取连接时最大等待时间，单位毫秒。
	
	private final static String JDBC_DRIVER_PROP = "mysql.drive";
	private final static String JDBC_URL_PROP = "mysql.url";
	private final static String DB_USERNAME_PROP = "mysql.username";
	private final static String DB_PASSWORD_PROP = "mysql.password";
	private final static String DRUID_INITIAL_SIZE_PROP = "druid.datasource.initialSize";
	private final static String DRUID_MAXACTIVE_PROP = "druid.datasource.maxActive";
	private final static String DRUID_MINIDLE_PROP = "druid.datasource.minIdle";
	private final static String DRUID_MAXWAIT_PROP = "druid.datasource.maxWait";
	
	static {
		String propPath = null;
		try {
			propPath = PathUtil.getConfigPath() + File.separator + SystemConstant.APPLICATION;
		} catch (PathNotFoundException e1) {
			LogManager.Exception(e1);
		}
		Properties prop = new Properties();
		try (FileInputStream in = new FileInputStream(propPath)){
			prop.load(in);
			
			String tmpDriver = prop.getProperty(JDBC_DRIVER_PROP, "com.mysql.jdbc.Driver");
			JDBC_DRIVER = StringUtil.isNotEmpty(tmpDriver) ? tmpDriver : "com.mysql.jdbc.Driver";
			
			String tmpUrl = prop.getProperty(JDBC_URL_PROP, 
					"jdbc:mysql://127.0.0.1:3306/text?useUnicode=true&characterEncoding=utf8");
			JDBC_URL = StringUtil.isNotEmpty(tmpUrl) ?
					tmpUrl : "jdbc:mysql://127.0.0.1:3306/text?useUnicode=true&characterEncoding=utf8";
			
			String tmpUsername = prop.getProperty(DB_USERNAME_PROP, "root");
			DB_USERNAME = StringUtil.isNotEmpty(tmpUsername) ? tmpUsername : "root";
			
			String tmpPassword = prop.getProperty(DB_PASSWORD_PROP, "root");
			DB_PASSWORD = StringUtil.isNotEmpty(tmpPassword) ? tmpPassword : "root";
			
			String tmpInitialSize = prop.getProperty(DRUID_INITIAL_SIZE_PROP, "3");
			DRUID_INITIAL_SIZE = StringUtil.isNotEmpty(tmpInitialSize) ? tmpInitialSize : "3";
			
			String tmpMaxActive = prop.getProperty(DRUID_MAXACTIVE_PROP, "10");
			DRUID_MAXACTIVE = StringUtil.isNotEmpty(tmpMaxActive) ? tmpMaxActive : "10";
			
			String tmpMinIdle = prop.getProperty(DRUID_MINIDLE_PROP, "0");
			DRUID_MINIDLE = StringUtil.isNotEmpty(tmpMinIdle) ? tmpMinIdle : "0";
			
			String tmpMaxWait = prop.getProperty(DRUID_MAXWAIT_PROP, "60000");
			DRUID_MAXWAIT = StringUtil.isNotEmpty(tmpMaxWait) ? tmpMaxWait : "60000";
			
			
			dataBaseProp.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, JDBC_DRIVER);
			dataBaseProp.put(DruidDataSourceFactory.PROP_URL, JDBC_URL);
			dataBaseProp.put(DruidDataSourceFactory.PROP_PASSWORD, DB_USERNAME);
			dataBaseProp.put(DruidDataSourceFactory.PROP_USERNAME, DB_PASSWORD);

			dataBaseProp.put(DruidDataSourceFactory.PROP_INITIALSIZE, DRUID_INITIAL_SIZE);
			dataBaseProp.put(DruidDataSourceFactory.PROP_MAXACTIVE, DRUID_MAXACTIVE);
			dataBaseProp.put(DruidDataSourceFactory.PROP_MINIDLE, DRUID_MINIDLE);
			dataBaseProp.put(DruidDataSourceFactory.PROP_MAXWAIT, DRUID_MAXWAIT);
		} catch (IOException e) {
			LogManager.Exception(e);
		}
	}
	
}
