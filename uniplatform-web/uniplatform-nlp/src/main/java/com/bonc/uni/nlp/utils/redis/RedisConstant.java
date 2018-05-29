package com.bonc.uni.nlp.utils.redis;

public class RedisConstant {

	public static final String DISTRIBUTED = "distributed";
	
	public static final String STANDALONE = "standalone";
	
	/**
	 * redis订阅频道-词库启用
	 */
	public static final String DIC_START_REDIS_SUBSCRIBE_CHANNEL = "nlap_dic_start_channel";
	/**
	 * redis订阅频道-词库停用
	 */
	public static final String DIC_STOP_REDIS_SUBSCRIBE_CHANNEL = "nlap_dic_stop_channel";
	
	/**
	 * redis订阅频道-策略删除
	 */
	public static final String STRATEGY_DEL_REDIS_SUBSCRIBE_CHANNEL = "nlap_strategy_delete_channel";
	
	/**
	 * redis订阅频道-模型上传
	 */
	public static final String MODEL_UPLOAD_REDIS_SUBSCRIBE_CHANNEL = "nlap_model_upload_channel";
	/**
	 * redis订阅频道-模型删除
	 */
	public static final String MODEL_DEL_REDIS_SUBSCRIBE_CHANNEL = "nlap_model_delete_channel";
	
}
