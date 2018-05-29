package com.bonc.uni.dcci.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;

/**
 * 站点管理
 * @author futao
 * 2017年9月5日
 */
@Entity
@Table(name = "`dcci_site`", indexes = {@Index(columnList = "`URL_HASH`")})
public class Site extends EntityCommon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 采集器id
	 */
	@Column(name = "`CRAWLER_ID`",columnDefinition = "bigint(20) DEFAULT 0")
	private int crawlerId;

	/**
	 * 站点名
	 */
	@Column(name = "`NAME`")
	private String name;
	
	/**
	 * 板块
	 */
	@Column(name = "`BOARD`")
	private String board;
	
	/**
	 * 类别
	 */
	@Column(name = "`CATEGORY`")
	private String category;
	
	/**
	 * 媒体属性
	 */
	@Column(name = "`MEDIA_PRO`")
	private String mediaPro;
	
	/**
	 * 媒体类型
	 */
	@Column(name = "`MEDIA_TYPE`")
	private String mediaType;
	
	/**
	 * 行业类型
	 */
	@Column(name = "`INDUSTRY`")
	private String industry;
	
	/**
	 * 地址
	 */
	@Column(name = "`URL`")
	private String url;
	
	/**
	 * 采集状态，  1:采集  2 删除
	 */
	@Column(name = "`STATUS`",columnDefinition = "bigint(1) DEFAULT 1")
	private int status;
	
	/**
	 * 单个站点配置
	 */
	@Column(name = "`CONFIG`", columnDefinition = "TEXT")
	private String config;
	
	@Column(name = "`URL_HASH`")
	private String urlHash;

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getMediaPro() {
		return mediaPro;
	}

	public void setMediaPro(String mediaPro) {
		this.mediaPro = mediaPro;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCrawlerId() {
		return crawlerId;
	}

	public void setCrawlerId(int crawlerId) {
		this.crawlerId = crawlerId;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getUrlHash() {
		return urlHash;
	}

	public void setUrlHash(String urlHash) {
		this.urlHash = urlHash;
	}
	
}
