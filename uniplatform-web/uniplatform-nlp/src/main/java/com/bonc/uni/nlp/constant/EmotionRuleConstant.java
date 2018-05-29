package com.bonc.uni.nlp.constant;
/**
 * @ClassName:EmotionRuleConstant
 * @Package:com.bonc.uni.nlp.constant
 * @Description:情感规则常量
 */
public class EmotionRuleConstant {
	/**
	 * 创建的默认模板名称
	 */
	public final static String TEMPLATE_DEFAULT_NAME = "template";
	/**
	 * redis存储的前缀，存储格式为：前缀 + 模板名称 + 规则（词）
	 */
	public final static String REDIS_PREFIX = "usdp_emotion_rule_";
	
}
