package com.bonc.uni.nlp.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.bonc.uni.nlp.constant.SystemConstant;
import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
/**
 * @ClassName: SystemConfig
 * @Package: com.bonc.uni.nlp.config
 * @Description: 系统配置
 * @author: Chris
 * @date: 2018年1月4日 下午6:36:11
 */
public class SystemConfig {
	
	/**
	 * admin服务器信息
	 */
	public static String ADMIN_SERVER_IP;
	public static String ADMIN_SERVER_SSH_PORT;
	public static String ADMIN_SERVER_USERNAME;
	public static String ADMIN_SERVER_PASSWORD;

	// ES库相关信息
	public static String ELASTICSEARCH_IP;

	public static int ELASTICSEARCH_PORT;

	public static String ELASTICSEARCH_NAME;

	public static String ELASTICSEARCH_INDEX;

	public static String ELASTICSEARCH_TYPE;
	
	/**
	 * processor连接信息
	 */
	public static String PROCESSOR_SERVER_IP;
	public static String PROCESSOR_SERVER_PORT;
	public static String PROCESSOR_SERVER_NAME;
	public static String PROCESSOR_DOMAIN;
	
	/**
	 * processor连接信息(英文)
	 */
	public static String PROCESSOR_SERVER_IP_EN;
	public static String PROCESSOR_SERVER_PORT_EN;
	public static String PROCESSOR_SERVER_NAME_EN;
	public static String PROCESSOR_DOMAIN_EN;
	
	/**
	 * peocessor摘要关键词的配置
	 */
	public static int PROCESSOR_KEYWORD_NUM; // 关键词个数
	public static int PROCESSOR_SUMMARY_PERCENT; // 摘要占全文百分比
	public static int PROCESSOR_SUMMARY_SENTENCE_NUM; // 摘要句子个数
	

	static {
		Properties properties = new Properties();
		try (InputStream input = new BufferedInputStream(new FileInputStream(
				PathUtil.getConfigPath() + File.separator + SystemConstant.APPLICATION))) {
			properties.load(input);
			/**
			 * admin配置
			 */
			ADMIN_SERVER_IP = properties.getProperty(
					SystemConstant.ADMIN_SERVER_IP, "127.0.0.1");
			ADMIN_SERVER_SSH_PORT = properties.getProperty(
					SystemConstant.ADMIN_SERVER_SSH_PORT, "9187");
			ADMIN_SERVER_USERNAME = properties.getProperty(
					SystemConstant.ADMIN_SERVER_USERNAME, "");
			ADMIN_SERVER_PASSWORD = properties.getProperty(
					SystemConstant.ADMIN_SERVER_PASSWORD, "");
			
			
			/**
			 * ES
			 */
			ELASTICSEARCH_IP = properties.getProperty(
					SystemConstant.ELASITCSEARCH_IP,
					SystemConstant.ELASITCSEARCH_IP_VALUE);
			ELASTICSEARCH_PORT = Integer.parseInt(properties.getProperty(
					SystemConstant.ELASITCSEARCH_PORT,
					SystemConstant.ELASITCSEARCH_PORT_VALUE));
			ELASTICSEARCH_NAME = properties.getProperty(
					SystemConstant.ELASTICSEARCH_NAME,
					SystemConstant.ELASITCSEARCH);
			ELASTICSEARCH_INDEX = properties.getProperty(
					SystemConstant.ELASTICSEARCH_INDEX,
					SystemConstant.ELASTICSEARCH_INDEX_VALUE);
			ELASTICSEARCH_TYPE = properties.getProperty(
					SystemConstant.ELASTICSEARCH_TYPE,
					SystemConstant.ELASTICSEARCH_TYPE_VALUE);
			
			/**
			 * Processor
			 */
			PROCESSOR_SERVER_IP = properties.getProperty(
					SystemConstant.PROCESSOR_IP_CONFIG, "127.0.0.1");
			PROCESSOR_SERVER_PORT = properties.getProperty(
					SystemConstant.PROCESSOR_PORT_CONFIG, "9187");
			PROCESSOR_SERVER_NAME = properties.getProperty(
					SystemConstant.PROCESSOR_NAME_CONFIG, "");
			PROCESSOR_DOMAIN = PROCESSOR_SERVER_IP + ":" + PROCESSOR_SERVER_PORT;
			if (StringUtil.isNotEmpty(PROCESSOR_SERVER_NAME)) {
				PROCESSOR_DOMAIN = PROCESSOR_DOMAIN + "/" + PROCESSOR_SERVER_NAME;
			}
			
			/**
			 * Processor(英文)
			 */
			PROCESSOR_SERVER_IP_EN = properties.getProperty(
					SystemConstant.PROCESSOR_IP_CONFIG_EN, "127.0.0.1");
			PROCESSOR_SERVER_PORT_EN = properties.getProperty(
					SystemConstant.PROCESSOR_PORT_CONFIG_EN, "9187");
			PROCESSOR_SERVER_NAME_EN = properties.getProperty(
					SystemConstant.PROCESSOR_NAME_CONFIG_EN, "");
			PROCESSOR_DOMAIN_EN = PROCESSOR_SERVER_IP_EN + ":" + PROCESSOR_SERVER_PORT_EN;
			if (StringUtil.isNotEmpty(PROCESSOR_SERVER_NAME_EN)) {
				PROCESSOR_DOMAIN_EN = PROCESSOR_DOMAIN_EN + "/" + PROCESSOR_SERVER_NAME_EN;
			}
			
			/**
			 * processor摘要关键词
			 */
			PROCESSOR_KEYWORD_NUM = Integer.parseInt(properties.getProperty(
					SystemConstant.PROCESSOR_KEYWORD_NUM_CONFIG, "10"));
			PROCESSOR_SUMMARY_PERCENT = Integer.parseInt(properties.getProperty(
					SystemConstant.PROCESSOR_SUMMARY_PERCENT_CONFIG, "30"));
			PROCESSOR_SUMMARY_SENTENCE_NUM = Integer.parseInt(properties.getProperty(
					SystemConstant.PROCESSOR_SUMMARY_SENTENCE_NUM_CONFIG, "2"));
		} catch (IOException e) {
			LogManager.Exception(
					"System Config Exception in package config : ", e);
		} catch (Exception e) {
			LogManager.Exception("System Config loaded failed! : ", e);
		}
	}

}
