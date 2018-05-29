package com.bonc.uni.nlp.utils.redis.entity;

/**
 * @Author Chris
 * @Description Redis发布的词库(对应admin表中的数据)
 * @Date 2017年11月30日 下午6:56:02
 */
public class RedisPublishDic {
	/**
	 * 词库id
	 */
	private String dicId;
	/**
	 * 词库名称
	 */
	private String dicName;
	/**
	 * 词库类型：普通词库、功能词库、领域词库
	 */
	private String dicType;
	/**
	 * 词库应用到的子类型
	 */
	private String dicSubType;
	/**
	 * 词库所应用到的功能
	 */
	private String dicFunction;
	/**
	 * 词库格式
	 */
	private String dicFormat;
	/**
	 * 加载到的策略名称
	 */
	private String strategyName;

	public String getDicName() {
		return dicName;
	}

	public void setDicName(String dicName) {
		this.dicName = dicName;
	}

	public String getDicType() {
		return dicType;
	}

	public void setDicType(String dicType) {
		this.dicType = dicType;
	}

	public String getDicSubType() {
		return dicSubType;
	}

	public void setDicSubType(String dicSubType) {
		this.dicSubType = dicSubType;
	}

	public String getDicFunction() {
		return dicFunction;
	}

	public void setDicFunction(String dicFunction) {
		this.dicFunction = dicFunction;
	}

	public String getDicId() {
		return dicId;
	}

	public void setDicId(String dicId) {
		this.dicId = dicId;
	}

	public String getDicFormat() {
		return dicFormat;
	}

	public void setDicFormat(String dicFormat) {
		this.dicFormat = dicFormat;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	@Override
	public String toString() {
		return "RedisPublishDic [dicId=" + dicId + ", dicName=" + dicName + ", dicType=" + dicType + ", dicSubType="
				+ dicSubType + ", dicFunction=" + dicFunction + ", dicFormat=" + dicFormat + ", strategyName="
				+ strategyName + "]";
	}

}
