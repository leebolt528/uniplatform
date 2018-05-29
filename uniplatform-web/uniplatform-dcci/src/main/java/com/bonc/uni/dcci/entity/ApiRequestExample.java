package com.bonc.uni.dcci.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.bonc.uni.common.entity.EntityCommon;
import com.bonc.uni.dcci.util.APIType;

/**
 * 请求示例
 * @author futao
 * 2017年9月25日
 */
@Entity
@Table(name = "`dcci_api_request_example`")
public class ApiRequestExample extends EntityCommon {

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
	 * 编程语言
	 */
	@Column(name = "`LANGUAGE`")
	@Enumerated(EnumType.STRING)
	private APIType.Language language;
	
	/**
	 * 代码
	 */
	@Column(name = "CODE",columnDefinition="text")
	private String code;

	public int getDetailId() {
		return detailId;
	}

	public void setDetailId(int detailId) {
		this.detailId = detailId;
	}

	public APIType.Language getLanguage() {
		return language;
	}

	public void setLanguage(APIType.Language language) {
		this.language = language;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
