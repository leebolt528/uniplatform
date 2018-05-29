package com.bonc.uni.dcci.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;
import com.bonc.uni.dcci.util.StatusType;

/**
 * 上传任务的站点
 * @author futao
 * 2017年9月12日
 */
@Entity
@Table(name = "`dcci_task_site`", indexes = {@Index(columnList = "`URL_HASH`")})
public class TaskSite extends EntityCommon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * TaskRelation id
	 */
	@Column(name = "`TASK_RELATION`", updatable = false, columnDefinition = "bigint(20) DEFAULT 0")
	private int taskRelation;

	/**
	 * 用户id
	 */
	@Column(name = "`USER_ID`", columnDefinition = "bigint(20) DEFAULT 0")
	public int userId;
	
	/**
	 * 名称
	 */
	@Column(name = "`NAME`")
	private String name;
	
	/**
	 * 板块
	 */
	@Column(name = "`BOARD`")
	private String board;
	
	/**
	 * 地址
	 */
	@Column(name = "`URL`")
	private String url;
	
	@Column(name = "`URL_HASH`")
	private String urlHash;
	
	/**
	 * 状态  分为:url重复  正在配置
	 */
	@Column(name = "`STATUS`")
	@Enumerated(EnumType.STRING)
	private StatusType.UrlSiteType status;

	public int getTaskRelation() {
		return taskRelation;
	}

	public void setTaskRelation(int taskRelation) {
		this.taskRelation = taskRelation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBoard() {
		return board;
	}

	public void setBoard(String board) {
		this.board = board;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlHash() {
		return urlHash;
	}

	public void setUrlHash(String urlHash) {
		this.urlHash = urlHash;
	}

	public StatusType.UrlSiteType getStatus() {
		return status;
	}

	public void setStatus(StatusType.UrlSiteType status) {
		this.status = status;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
