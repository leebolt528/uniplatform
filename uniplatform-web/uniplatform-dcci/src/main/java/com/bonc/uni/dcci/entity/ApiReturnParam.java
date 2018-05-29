package com.bonc.uni.dcci.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;

/**
 * API  返回参数
 * @author futao
 * 2017年9月25日
 */
@Entity
@Table(name = "`dcci_api_return_param`")
public class ApiReturnParam extends EntityCommon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * detailId
	 */
	@Column(name = "`DETAIL_ID`", updatable = true, columnDefinition = "bigint(20) DEFAULT 0")
	private int detailId;
	
	/**
	 * 名
	 */
	@Column(name = "`NAME`")
	private String name;
	
	/**
	 * 请求类型
	 * string,int boolean
	 */
	@Column(name = "`TYPE`")
	private String type;
	
	/**
	 * 示例
	 */
	@Column(name = "`EXAMPLE`")
	private String example;
	
	/**
	 * 描述
	 */
	@Column(name = "`DESCRIPTION`")
	private String description;

	public int getDetailId() {
		return detailId;
	}

	public void setDetailId(int detailId) {
		this.detailId = detailId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
