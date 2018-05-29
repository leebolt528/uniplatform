package com.bonc.uni.dcci.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;

/**
 * api错误码
 * @author futao
 * 2017年9月25日
 */
@Entity
@Table(name = "`dcci_api_code`")
public class ApiCode extends EntityCommon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 错误码
	 */
	@Column(name = "`CODE`", unique = true,columnDefinition = "bigint(10) DEFAULT 0")
	private int code;
	
	@Column(name = "`EXPLAIN`")
	private String explain;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}
	
	
}
