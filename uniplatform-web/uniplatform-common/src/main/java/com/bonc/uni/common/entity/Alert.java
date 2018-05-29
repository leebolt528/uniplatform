package com.bonc.uni.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.bonc.uni.common.util.PlatformType;

/**
 * 预警结果
 * @author futao
 * 2017年9月1日
 */
@Entity
@Table(name = "`common_alert`")
public class Alert extends EntityCommon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 预警任务 {@link TaskItem}
	 */
	@Column(name = "`INFO_ID`")
	private int infoId;

	/**
	 * 预警任务名称
	 */
	@Column(name = "`INFO_NAME`", length = 1024)
	private String infoName;

	/**
	 * 预警消息
	 */
	@Column(name = "`MESSAGE`", columnDefinition = "TEXT")
	private String message;

	/**
	 * 接收者
	 */
	@Column(name = "`RECEIVER`", columnDefinition = "TEXT")
	private String receiver;

	/**
	 * HIGH MEDIUM LOW
	 */
	@Column(name = "`LEVEL`")
	private String level;
	
	/**任务分组*/
	@Column(name = "`GROUP`")
	@Enumerated(EnumType.STRING)
	private PlatformType group;
	
	public int getInfoId() {
		return infoId;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}

	public String getInfoName() {
		return infoName;
	}

	public void setInfoName(String infoName) {
		this.infoName = infoName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public PlatformType getGroup() {
		return group;
	}

	public void setGroup(PlatformType group) {
		this.group = group;
	}
	
}
