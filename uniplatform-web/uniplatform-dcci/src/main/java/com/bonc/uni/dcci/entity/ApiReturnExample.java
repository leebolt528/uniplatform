package com.bonc.uni.dcci.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;

@Entity
@Table(name = "`dcci_api_return_example`")
public class ApiReturnExample extends EntityCommon {

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
	 * 示例
	 */
	@Column(name = "EXAMPLE",columnDefinition="text")
	private String example;

	public int getDetailId() {
		return detailId;
	}

	public void setDetailId(int detailId) {
		this.detailId = detailId;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}
	
}
