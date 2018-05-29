package com.bonc.uni.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * bean的基础类
 * @author futao
 * 2017年8月28日
 */
@MappedSuperclass
public class EntityCommon implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * id 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "`ID`")
	private int id;

	/**
	 * 创建人
	 */
	@CreatedBy
	@Column(name = "`CREATEDUSER`")
	private String createdUser;

	/**
	 * 创建时间
	 */
	@CreatedDate
	@Column(name = "`CREATEDTIME`", updatable = false, columnDefinition = "bigint(20) DEFAULT 0")
	private long createdTime;

	/**
	 * 最后修改人
	 */
	@LastModifiedBy
	@Column(name = "`LASTMODIFIEDUSER`")
	private String lastModifiedUser;

	/**
	 * 最后修改时间
	 */
	@LastModifiedDate
	@Column(name = "`LASTMODIFIEDTIME`", columnDefinition = "bigint(20) DEFAULT 0")
	private long lastModifiedTime;

	/**
	 * 版本
	 */
	@Version
	@Column(name = "`VERSION`", columnDefinition = "int(11) DEFAULT 0")
	private int version;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public String getLastModifiedUser() {
		return lastModifiedUser;
	}

	public void setLastModifiedUser(String lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
}
