package com.bonc.uni.nlp.constant;
/**
 * @ClassName:DictionaryConstant
 * @Package:com.bonc.uni.nlp.constant
 * @Description:TODO
 */
public class DictionaryConstant {

	/**
	 * 词典类型：普通类型
	 */
	public static final int DICT_TYPE_ORDINARY = 0;
	/**
	 * 词库类型：Map类型
	 */
	public static final int DICT_TYPE_MAP = 1;
	/**
	 * 所有词库
	 */
	public static final String FUNCTION_TYPE_ALL = "all";
	/**
	 * 自动分词
	 */
	public static final String FUNCTION_TYPE_SEGMENT = "segment";
	/**
	 * 实体抽取
	 */
	public static final String FUNCTION_TYPE_ENTITY = "entityextraction";
		/**
	 *情感分析
	 */
	public static final String FUNCTION_TYPE_SENTIMENTANALYSIS = "sentimentanalysis";
	
	/**
	 * redis中词的前缀，词的存储结构为  prefix + dictId + wordId
	 */
	public static final String REDIS_WORD_MAP_PREFIX = "xnlp_word_map_";
	/**
	 * redis中词的名称
	 */
	public static final String 	REDIS_WORD_MAP_KEY = "KEY";
	/**
	 * redis中词的名称
	 */
	public static final String 	REDIS_WORD_MAP_VALUE = "value";

	/**
	 * redis中词的前缀，词的存储结构为  prefix + dictId + wordId
	 */
	public static final String REDIS_WORD_PREFIX = "xnlp_word_";
	/**
	 * redis中词的名称
	 */
	public static final String REDIS_WORD_NAME = "name";

	/**
	 * redis中词的值
	 */
	public static final String REDIS_WORD_VALUE = "value";
	/**
	 * redis中词的词性
	 */
	public static final String REDIS_WORD_NATURE = "nature";
	/**
	 * redis中词的词频
	 */
	public static final String REDIS_WORD_FREQUENCY = "frequency";
	
	
}
