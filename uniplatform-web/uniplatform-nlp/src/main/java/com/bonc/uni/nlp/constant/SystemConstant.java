package com.bonc.uni.nlp.constant;

/**
 * @ClassName: SystemConstant
 * @Package: com.bonc.uni.nlp.constant
 * @Description: 常量
 * @author: Chris
 * @date: 2018年1月4日 下午6:39:40
 */
public class SystemConstant {
	/**
	 * 系统配置文件名称
	 */
	public static final String APPLICATION = "application-nlap.properties"; 
	/**
	 * admin服务器信息
	 */
	public static final String ADMIN_SERVER_IP = "bonc.usdp.nlap.server.ip";
	public static final String ADMIN_SERVER_SSH_PORT = "bonc.usdp.nlap.server.ssh.port";
	public static final String ADMIN_SERVER_USERNAME = "bonc.usdp.nlap.server.username";
	public static final String ADMIN_SERVER_PASSWORD = "bonc.usdp.nlap.server.password";
	
	/**
	 * ES常量
	 */
	public final static String ELASITCSEARCH_IP = "bonc.elasticsearch.ip";

	public final static String ELASITCSEARCH_PORT = "bonc.elasticsearch.port";

	public final static String ELASTICSEARCH_NAME = "bonc.elasticsearch.name";

	public final static String ELASTICSEARCH_INDEX = "bonc.elasticsearch.index";

	public final static String ELASTICSEARCH_TYPE = "bonc.elasticsearch.type";

	public final static String ELASITCSEARCH = "elasticsearch";

	public final static String ELASITCSEARCH_IP_VALUE = "127.0.0.1";

	public final static String ELASITCSEARCH_PORT_VALUE = "9300";

	public final static String ELASTICSEARCH_INDEX_VALUE = "uniplatform";

	public final static String ELASTICSEARCH_TYPE_VALUE = "corpus";

	/**
	 * processor常量 
	 */
	public final static String PROCESSOR_IP_CONFIG = "bonc.usdp.nlap.processor.ip";
	public final static String PROCESSOR_PORT_CONFIG = "bonc.usdp.nlap.processor.port";
	public final static String PROCESSOR_NAME_CONFIG = "bonc.usdp.nlap.processor.name";
	
	/**
	 * processor常量 (英文)
	 */
	public final static String PROCESSOR_IP_CONFIG_EN = "bonc.usdp.nlap.processor.ip.en";
	public final static String PROCESSOR_PORT_CONFIG_EN = "bonc.usdp.nlap.processor.port.en";
	public final static String PROCESSOR_NAME_CONFIG_EN = "bonc.usdp.nlap.processor.name.en";
	
	/**
	 * 摘要关键词
	 */
	public final static String PROCESSOR_KEYWORD_NUM_CONFIG = "bonc.usdp.nlap.keywords.number"; // 关键词个数
	public final static String PROCESSOR_SUMMARY_PERCENT_CONFIG = "bonc.usdp.nlap.summary.percent"; // 摘要占全文百分比
	public final static String PROCESSOR_SUMMARY_SENTENCE_NUM_CONFIG = "bonc.usdp.nlap.summary.sentence.number"; // 摘要句子个数
	
}
