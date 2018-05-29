package com.bonc.uni.dcci.entity;

import com.bonc.uni.common.entity.EntityCommon;
import com.bonc.uni.dcci.util.ComponentType;
import com.bonc.uni.dcci.util.CrawlerType;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

/**采集器
 * @author futao
 * 2017年9月6日
 */
@Entity
@Table(name = "`dcci_crawler`")
public class Crawler extends EntityCommon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 组件类型
	 */
	@Column(name = "`COMPONENT_TYPE`")
	@Enumerated(EnumType.STRING)
	private ComponentType componentType ;
	
	/**
	 * 服务器id
	 */
	@Column(name = "`SERVER_ID`", updatable = false, columnDefinition = "int(11) DEFAULT 0")
	private int serverId;
	
	/**
	 * 名称
	 */
	@Column(name = "`NAME`")
	private String name;
	
	/**
	 * 端口
	 */
	@Column(name = "`PORT`")
	private String port;
	
	/**
	 * 路径
	 */
	@Column(name = "`PATH`")
	private String path;
	
	/**
	 * 存储路径
	 */
	@Column(name = "`STORAGE_PATH`")
	private String storagePath;
	
	/**
	 * 类型
	 * 国内新闻、国内论坛
	 */
	@Column(name = "`TYPE`")
	@Enumerated(EnumType.STRING)
	private CrawlerType type;
	
	/**
	 * 最大采集点
	 */
	@Column(name = "`MAX_SITE_NUM`", columnDefinition = "bigint(20) DEFAULT 0")
	private int maxSiteNum;
	
	/**
	 * 采集点
	 */
	@Column(name = "`SITE_NUM`",columnDefinition = "bigint(20) DEFAULT 0")
	private int siteNum;

	/**
	 * 服务器ip
	 */
	@Column(name = "`IP`")
	private String ip;

	/**
	 * 机器外键
	 */
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Server.class)
	@JoinColumn(name = "SERVER_ID", insertable = false, updatable = false,
			foreignKey = @ForeignKey(name = "FK_DCCI_CRAWLER_SERVER_ID"))
	private Server server;


	/**
	 * 转成json时防止无限循环
	 * @param
	 */
	@JsonBackReference
	public void setOrganization(Server server) {
		this.server = server;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public ComponentType getComponentType() {
		return componentType;
	}

	public void setComponentType(ComponentType componentType) {
		this.componentType = componentType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public int getMaxSiteNum() {
		return maxSiteNum;
	}

	public void setMaxSiteNum(int maxSiteNum) {
		this.maxSiteNum = maxSiteNum;
	}

	public int getSiteNum() {
		return siteNum;
	}

	public void setSiteNum(int siteNum) {
		this.siteNum = siteNum;
	}

	public CrawlerType getType() {
		return type;
	}

	public void setType(CrawlerType type) {
		this.type = type;
	}
	
}
