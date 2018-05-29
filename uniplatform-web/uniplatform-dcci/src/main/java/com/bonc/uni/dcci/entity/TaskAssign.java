package com.bonc.uni.dcci.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;
import com.bonc.uni.common.entity.user.SysUser;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 分配任务给用户
 * @author futao
 * 2017年9月12日
 */
@Entity
@Table(name = "`dcci_task_assign`")
public class TaskAssign  extends EntityCommon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * TaskManage id
	 */
	@Column(name = "`TASK_MANAGER`")
	private int taskManage;
	
	/**
	 * 用户id
	 */
	@Column(name = "`USER_ID`")
	public Integer userId;
	
	/**
	 * 用户名称
	 */
	//@Column(name = "`USER_NAME`")
	/*@Transient
	public String userName;*/
	
	/**
	 * 采集器id
	 */
	@Column(name = "`CRAWLER_ID`")
	private int crawlerId;
	
	/**
	 * 分配站点总数
	 */
	@Column(name = "`SITE_ASSIGN`",columnDefinition = "bigint(5) DEFAULT 0")
	private int siteAssign;
	
	/**
	 * 完成站点数
	 */
	@Column(name = "`SITE_COMPLETE`",columnDefinition = "bigint(5) DEFAULT 0")
	private int siteComplete;
	
	/**
	 * 失败站点数
	 */
	@Column(name = "`SITE_FAIL`",columnDefinition = "bigint(5) DEFAULT 0")
	private int siteFail;
	
	/**
	 * 截止日期
	 */
	@Column(name = "`DEADLINE`", columnDefinition = "bigint(20) DEFAULT 0")
	private long deadline;
	
	/**
	 * 0:未开始
	 * 1:开始配置
	 * 2:任务完成
	 * 
	 */
	@Column(name = "`SUCCESS`", columnDefinition = "bigint(1) DEFAULT 0")
	private int success;
	
	/**
	 * 任务完成的上传配置，此配置用于检查采集器删除的采集点
	 * 0：未检查
	 * 1：检查完成
	 */
	@Column(name = "`CHECK`", columnDefinition = "bigint(1) DEFAULT 0")
	private int check;
	
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = SysUser.class)
	// @ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(name = "`USER_ID`", insertable = false, updatable = false,
			foreignKey = @ForeignKey(name = "FK_DCCI_TASKASSIGN_SYSUSER_ID"))
	private SysUser sysUser;
	
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = TaskManage.class)
	@JoinColumn(name = "`TASK_MANAGER`", insertable = false, updatable = false,foreignKey = @ForeignKey(name = "FK_DCCI_TASKASSIGN_TASKMANAGE_ID"))
	private TaskManage manage;
	
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Crawler.class)
	@JoinColumn(name = "`CRAWLER_ID`", insertable = false, updatable = false,foreignKey = @ForeignKey(name = "FK_DCCI_TASKASSIGN_CRAWLER_ID"))
	private Crawler crawler;
	
	/**
	 * 转成json时防止无限循环
	 * @param
	 */
	@JsonBackReference
	public void setOrganization(SysUser sysUser) {
		this.sysUser = sysUser;
	}
	@JsonBackReference
	public void setOrganization(TaskManage manage) {
		this.manage = manage;
	}
	@JsonBackReference
	public void setOrganization(Crawler crawler) {
		this.crawler = crawler;
	}
	
	public Crawler getCrawler() {
		return crawler;
	}
	public void setCrawler(Crawler crawler) {
		this.crawler = crawler;
	}
	public TaskManage getManage() {
		return manage;
	}

	public void setManage(TaskManage manage) {
		this.manage = manage;
	}

	public SysUser getSysUser() {
		return sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}


	public int getTaskManage() {
		return taskManage;
	}

	public void setTaskManage(int taskManage) {
		this.taskManage = taskManage;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public int getSiteAssign() {
		return siteAssign;
	}

	public void setSiteAssign(int siteAssign) {
		this.siteAssign = siteAssign;
	}

	public int getSiteComplete() {
		return siteComplete;
	}

	public void setSiteComplete(int siteComplete) {
		this.siteComplete = siteComplete;
	}

	public int getSiteFail() {
		return siteFail;
	}

	public void setSiteFail(int siteFail) {
		this.siteFail = siteFail;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getCrawlerId() {
		return crawlerId;
	}

	public void setCrawlerId(int crawlerId) {
		this.crawlerId = crawlerId;
	}

	public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
	}
	
}
