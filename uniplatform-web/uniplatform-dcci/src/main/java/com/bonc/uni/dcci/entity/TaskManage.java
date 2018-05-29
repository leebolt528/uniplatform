package com.bonc.uni.dcci.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;
import com.bonc.uni.dcci.util.CrawlerType;
import com.bonc.uni.dcci.util.LevelType;
import com.bonc.uni.dcci.util.StatusType;

/**
 * 任务
 * @author futao
 * 2017年9月11日
 */
@Entity
@Table(name = "`dcci_task_manage`")
public class TaskManage extends EntityCommon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@Column(name = "`NAME`")
	private String name;
	
	/**
	 * 采集类型
	 */
	@Column(name = "`TYPE`")
	@Enumerated(EnumType.STRING)
	private CrawlerType type;
	
	/**
	 * 优先级
	 */
	@Column(name = "`LEVEL_TYPE`")
	@Enumerated(EnumType.STRING)
	private LevelType levelType;
	
	/**
	 * 截止日期
	 */
	@Column(name = "`DEADLINE`",columnDefinition = "bigint(20) DEFAULT 0")
	private long deadline;
	
	/**
	 * 描述
	 */
	@Column(name = "`DESCRIPTION`")
	private String description;
	
	/**
	 * 上传站点总数
	 */
	@Column(name = "`SITE_TOTAL`", columnDefinition = "bigint(20) DEFAULT 0")
	private long siteTotal;
	
	/**
	 * 重复站点总数
	 */
	@Column(name = "`SITE_REPEAT`", columnDefinition = "bigint(20) DEFAULT 0")
	private long siteRepeat;
	
	/**
	 * 采集状态，  
	 */
	@Column(name = "`STATUS`")
	@Enumerated(EnumType.STRING)
	private StatusType status;
	
	/**
	 * TaskRelation id
	 */
	@Column(name = "`TASK_RELATION`", updatable = false, columnDefinition = "bigint(20) DEFAULT 0")
	private int taskRelation;
	
	/**
	 * 任务分配方式
	 */
	@Column(name = "`ASSIGN_TYPE`")
	@Enumerated(EnumType.STRING)
	private StatusType.TaskAssignType assignType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CrawlerType getType() {
		return type;
	}

	public void setType(CrawlerType type) {
		this.type = type;
	}

	public LevelType getLevelType() {
		return levelType;
	}

	public void setLevelType(LevelType levelType) {
		this.levelType = levelType;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getSiteTotal() {
		return siteTotal;
	}

	public void setSiteTotal(long siteTotal) {
		this.siteTotal = siteTotal;
	}

	public long getSiteRepeat() {
		return siteRepeat;
	}

	public void setSiteRepeat(long siteRepeat) {
		this.siteRepeat = siteRepeat;
	}

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	public int getTaskRelation() {
		return taskRelation;
	}

	public void setTaskRelation(int taskRelation) {
		this.taskRelation = taskRelation;
	}

	public StatusType.TaskAssignType getAssignType() {
		return assignType;
	}

	public void setAssignType(StatusType.TaskAssignType assignType) {
		this.assignType = assignType;
	}
}
