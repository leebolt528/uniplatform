package com.bonc.uni.common.entity;

/**
 * 任务参数
 * @author futao
 * 2017年9月4日
 */
public class TaskArgument {

	/**
	 * 名称
	 */
	private String name;
	/**
	 * 值
	 */
	private String value;
	/**
	 * 操作符
	 */
	private String operator;
	
	public TaskArgument() {
		super();
	}
	public TaskArgument(String name, String value, String operator) {
		super();
		this.name = name;
		this.value = value;
		this.operator = operator;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	
}
