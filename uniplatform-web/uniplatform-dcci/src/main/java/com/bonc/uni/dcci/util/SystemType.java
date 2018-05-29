package com.bonc.uni.dcci.util;

/**
 * 操作系统枚举
 * @author futao
 * 2017年8月28日
 */
public enum SystemType {

	WINDOWS(1),LINUX(2),ALL(3);
	
	private int value;

	private SystemType() {
	}

	private SystemType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
