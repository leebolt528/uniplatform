package com.bonc.uni.common.util;

/**
 * 平台类型，数据中心、搜索引擎等
 * @author futao
 * 2017年8月30日
 */
public enum PlatformType {

	/**
	 * 分配代表自然语言处理系统管理平台，自然语言知识库管理平台，搜索引擎管理平台，数据中心管理平台
	 */
	CORPUS(1),DCCI(2),NLP(3),USOU(4),ALL(5);
	
	private int value;

	private PlatformType() {
	}

	private PlatformType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
