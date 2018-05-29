package com.bonc.uni.nlp.entity.strategy;

public class StrategyConstant {

	/**
	 * function names
	 */
	public static final String FUNCTION_SEGMENT = "segment";
	public static final String FUNCTION_ABSTRACT = "abstract";
	public static final String FUNCTION_ENTITYEXTRACTION = "entityextraction";
	public static final String FUNCTION_CLASSIFY = "classify";
	public static final String FUNCTION_SENTIMENTANALYSIS = "sentimentanalysis";
	public static final String FUNCTION_WORD2VEC = "word2vec";
	public static final String FUNCTION_CLUSTER = "cluster";
	public static final String FUNCTION_NEWWORD = "newword";
	public static final String FUNCTION_EVENTEXTRACTION = "eventextraction";
	public static final String FUNCTION_PINYIN = "pinyin";
	public static final String FUNCTION_COUPLING = "coupling";
	/**
	 * algorithm names
	 */
	public static final String ALGORITHM_SEGMENT_BASE = "base"; // DIC分词
	public static final String ALGORITHM_SEGMENT_DIC = "dic"; // DIC分词
	public static final String ALGORITHM_SEGMENT_INDEX = "index"; // INDEX分词
	public static final String ALGORITHM_SEGMENT_NLP = "nlp"; // NLP分词
	public static final String ALGORITHM_SEGMENT_TO = "to"; // TO分词
	public static final String ALGORITHM_ABSTRACT_DEFAULT = "abstractDefault"; // 默认摘要关键词
	public static final String ALGORITHM_SUMMARY = "summary"; // 摘要
	public static final String ALGORITHM_KEYWORDS = "keywords"; // 关键词
	public static final String ALGORITHM_ENTITY_DEFAULT = "entityDefault"; // 默认实体抽取
	public static final String ALGORITHM_ENTITY_NAMEPLACEORG = "namePlaceOrg"; // 人名;地名;机构名;职务职称
	public static final String ALGORITHM_ENTITY_NAME = "name"; // 人名
	public static final String ALGORITHM_ENTITY_PLACE = "place"; // 地名
	public static final String ALGORITHM_ENTITY_ORG = "org"; // 机构名
	public static final String ALGORITHM_CLASSIFY_SVM = "svm"; // SVM分类
	public static final String ALGORITHM_CLASSIFY_MAXENT = "maxent"; // 最大熵分类
	public static final String ALGORITHM_CLASSIFY_DECISIONTREE = "decisiontree"; // 决策树分类
	public static final String ALGORITHM_CLASSIFY_BAYES = "bayes"; // 贝叶斯分类
	public static final String ALGORITHM_CLASSIFY_ADABOOST = "adaboost"; // Adaboost分类
	public static final String ALGORITHM_SENTIMENTANALYSIS_DEFAULT = "sentimentAnalysisDefault"; // 默认情感分析
	public static final String ALGORITHM_CLASSIFY_RULE = "rule"; // 规则分类
	public static final String ALGORITHM_WORD2VEC_GOOGLE = "word2vecGoogle"; // google
	public static final String ALGORITHM_WORD2VEC_JAVA = "word2vecJava"; // java
	public static final String ALGORITHM_CLUSTER_DEFAULT = "clusterDefault"; // 默认聚类
	public static final String ALGORITHM_NEWWORD_DEFAULT = "newwordDefault"; // 默认新词发现
	public static final String ALGORITHM_EVENT_DEFAULT = "eventDefault"; // 默认事件抽取
	public static final String ALGORITHM_PINYIN_DEFAULT = "pinyinDefault"; // 默认拼音标注
	public static final String ALGORITHM_COUPLING_DEFAULT = "couplingDefault"; // 默认简繁转换
	
	/**
	 * 各功能默认策略名称
	 */
	public static final String SEGMENT_DEFAULT_STRATEGY = "自动分词系统默认策略";
	public static final String ENTITY_DEFAULT_STRATEGY = "实体抽取系统默认策略";
	
	/**
	 * model names
	 */
	public static final String MODEL_CLASSIFY_DEFAULT = "default"; // adaboost分类
	
	/**
	 * 操作权限
	 */
	public static final String STRATEGY_TYPE_SYSTEM = "system"; // 系统默认
	public static final String STRATEGY_TYPE_CUSTOM = "custom"; // 用户自定义
	
	/**
	 * 功能策略管理导入/导出的xml文件标签
	 */
	public final static String XML_STRATEGY_ROOT = "strategies";
	public final static String XML_STRATEGY = "strategy";
	public final static String XML_STRATEGY_NAME = "name";
	public final static String XML_STRATEGY_FUNCTION = "function";
	public final static String XML_STRATEGY_ALGORITHM = "algorithm";
	public final static String XML_STRATEGY_DEPENDENCY_STRATEGY = "dependencyStrategies";
	public final static String XML_STRATEGY_DICTS = "dicts";
	public final static String XML_STRATEGY_MODEL = "model";
	public final static String XML_STRATEGY_RULE = "rule";
	
	/**
	 * 业务策略管理导入/导出的xml文件标签
	 */
	public final static String XML_BUSINESS_ROOT = "businesses";
	public final static String XML_BUSINESS = "business";
	public final static String XML_BUSINESS_NAME = "name";
	public final static String XML_BUSINESS_NODES = "nodes";
}
