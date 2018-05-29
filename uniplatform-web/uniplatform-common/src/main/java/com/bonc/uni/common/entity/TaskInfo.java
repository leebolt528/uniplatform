package com.bonc.uni.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.bonc.uni.common.util.PlatformType;

/**
 * 任务信息
 * @author futao
 * 2017年8月30日
 */
@Entity
@Table(name = "`common_task_info`")
public class TaskInfo extends EntityCommon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 注册任务
	 */
	@Column(name = "`TASK_ID`")
	private int taskId;
	
	/**
	 * 名称
	 */
	@Column(name = "`NAME`")
	private String name;
	
	/**
	 * 频率（cronExpression）
	 */
	@Column(name = "`FREQUENCY`")
	private String frequency;
	
	/**
	 * 上次运行时间
	 */
	@Column(name = "`LAST_RUNTIME`",columnDefinition = "bigint(20) DEFAULT 0")
	private long lastRuntime;
	
	/**
	 * 1：开启  2 关闭
	 * 状态
	 */
	@Column(name = "`STATE`")
	private String state;
	
	/**
	 * 运行时间
	 */
	@Column(name = "`COST`", columnDefinition = "bigint(20) DEFAULT 0")
	private long cost;
	
	/**
	 * 预警接收
	 */
	@Column(name = "`RECEIVERS`", columnDefinition = "TEXT")
	private String receivers;
	
	/**
	 * 描述
	 */
	@Column(name = "`DESCRIPTION`")
	private String description;
	
	/**
	 * 阈值 key：value
	 */
	@Column(name = "`THRESHOLD`", columnDefinition = "TEXT")
	private String threshold;
	
	/**
	 * 运行结果
	 */
	@Column(name = "`RESULT`")
	private String result;
	
	/**任务分组*/
	@Column(name = "`GROUP`")
	@Enumerated(EnumType.STRING)
	private PlatformType group;
	
	/**
	 * 记录其它表的id
	 */
	@Column(name = "`BEAN_ID`", updatable = false, columnDefinition = "bigint(20) DEFAULT 0")
	private int beanId;

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public long getLastRuntime() {
		return lastRuntime;
	}

	public void setLastRuntime(long lastRuntime) {
		this.lastRuntime = lastRuntime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}

	public String getReceivers() {
		return receivers;
	}

	public void setReceivers(String receivers) {
		this.receivers = receivers;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public PlatformType getGroup() {
		return group;
	}

	public void setGroup(PlatformType group) {
		this.group = group;
	}

	public int getBeanId() {
		return beanId;
	}

	public void setBeanId(int beanId) {
		this.beanId = beanId;
	}
	
}