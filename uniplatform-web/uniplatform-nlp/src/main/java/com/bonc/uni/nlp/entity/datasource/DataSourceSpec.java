package com.bonc.uni.nlp.entity.datasource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @ClassName:DataSourceSpec
 * @Package:com.bonc.text.entity.datasource
 * @Description:数据源扩展属性
 * @author:xmy
 */
@Entity
@Table(name = "nlap_data_source_spec")
public class DataSourceSpec {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 参数名
	 */
	@Column(name = "paramter_name")
	private String paramterName;
	/**
	 * 参数值
	 */
	@Column(name = "paramter_value")
	private String paramterValue;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParamterName() {
		return paramterName;
	}

	public void setParamterName(String paramterName) {
		this.paramterName = paramterName;
	}

	public String getParamterValue() {
		return paramterValue;
	}

	public void setParamterValue(String paramterValue) {
		this.paramterValue = paramterValue;
	}

	@Override
	public String toString() {
		return "DataSourceSpec [id=" + id + ", paramterName=" + paramterName + ", paramterValue=" + paramterValue + "]";
	}

}
