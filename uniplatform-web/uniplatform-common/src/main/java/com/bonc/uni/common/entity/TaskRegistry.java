package com.bonc.uni.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.bonc.uni.common.util.PlatformType;

/**
 * 任务注册
 * @author futao
 * 2017年8月30日
 */
@Entity
@Table(name = "`common_task_registry`")
public class TaskRegistry extends EntityCommon {

	private static final long serialVersionUID = 1L;

	/**
	 * 任务名称
	 */
	@NotEmpty(message = "任务名称不为空")
	@Column(name = "`TASK_NAME`")
	private String taskName;

	/**
	 * 服务名称
	 */
	@Column(name = "`SERVICE_NAME`")
	private String serviceName;

	/**
	 * 任务描述
	 */
	@Column(name = "`DESCRIPTION`")
	private String description;
	
	/**任务分组*/
	@Column(name = "`GROUP`")
	@Enumerated(EnumType.STRING)
	private PlatformType group;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PlatformType getGroup() {
		return group;
	}

	public void setGroup(PlatformType group) {
		this.group = group;
	}

}