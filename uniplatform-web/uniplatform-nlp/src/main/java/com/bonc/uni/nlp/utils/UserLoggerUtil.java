package com.bonc.uni.nlp.utils;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.logger.Logger;
import com.bonc.usdp.odk.logger.entity.DataType;
import com.bonc.usdp.odk.logger.entity.LoggerField;
import com.bonc.usdp.odk.logger.entity.LoggerTable;

/**
 * @ClassName:UserLoggerUtil
 * @Package:com.bonc.text.utils
 * @Description:TODO
 * @author:Gao Qiuyue
 * @date:2017年9月12日 下午5:46:33
 */
public class UserLoggerUtil {
	
	public static void init() {
		LoggerField operation = new LoggerField("operation", DataType.VARCHAR, 3);
		LoggerField startingtime = new LoggerField("startingtime", DataType.DATETIME, 4);
		
		List<LoggerField> systableLoggerField = new ArrayList<>();
		systableLoggerField.add(operation);
		systableLoggerField.add(startingtime);
		
		List<LoggerField> opertableLoggerField = new ArrayList<>();
		opertableLoggerField.add(operation);
		opertableLoggerField.add(startingtime);
		
		List<LoggerField> perfortableLoggerField = new ArrayList<>();
		perfortableLoggerField.add(operation);
		perfortableLoggerField.add(startingtime);
		
		LoggerTable systemTable = new LoggerTable("system_logger", systableLoggerField);
		LoggerTable operationTable = new LoggerTable("opertion_logger", opertableLoggerField);
		LoggerTable performanceTable = new LoggerTable("performance_logger", perfortableLoggerField);
		try {
			Logger.init(PathUtil.getResourcesPath() + File.separator 
					+ "application-userLogger.properties", systemTable, operationTable, performanceTable);
		} catch (PathNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取当前时间
	 * @return 
	 */
	public static Timestamp getCurrentTime(){
		Timestamp timestamp = new Timestamp(new Date().getTime());
		
	    return timestamp;
	}
	
	/**
	 * 获取当前登录用户名
	 */
	public static String getCurrentUser(){
		
		return CurrentUserUtils.getInstance().getUser().getUserName();
		
	}

}
