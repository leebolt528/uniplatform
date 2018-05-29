package com.bonc.uni.dcci.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;
import com.bonc.uni.dcci.util.APIType;

/**
 * api 
 * @author futao
 * 2017年9月22日
 */
@Entity
@Table(name = "`dcci_api_detail`")
public class ApiDetail extends EntityCommon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "`API_ID`", updatable = true, columnDefinition = "bigint(20) DEFAULT 0")
	private int apiId;

	/**
	 * 名
	 */
	@Column(name = "`NAME`")
	private String name;
	
	/**
	 * 地址
	 */
	@Column(name = "`URL`")
	private String url;
	
	/**
	 * 方法
	 */
	@Column(name = "`METHOD`")
	@Enumerated(EnumType.STRING)
	private APIType.Method method;
	
	@Column(name = "`SAMPLE`")
	private String sample;
	
	/**
	 * 负责人
	 */
	@Column(name = "`USER`")
	private String user;
	
	/**
	 * email
	 */
	@Column(name = "`EMAIL`")
	private String email;
	
	/**
	 * 电话
	 */
	@Column(name = "`PHONE`", columnDefinition = "bigint(11) DEFAULT 0")
	private long phone;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public APIType.Method getMethod() {
		return method;
	}

	public void setMethod(APIType.Method method) {
		this.method = method;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getPhone() {
		return phone;
	}

	public void setPhone(long phone) {
		this.phone = phone;
	}

	public int getApiId() {
		return apiId;
	}

	public void setApiId(int apiId) {
		this.apiId = apiId;
	}

	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}
}
