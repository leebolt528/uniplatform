package com.bonc.uni.dcci.util;

/**
 * 组件类型枚举
 */
public enum ComponentType {

	CRAWLER("采集器");

	private String value;

	private ComponentType() {
	}

	private ComponentType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
