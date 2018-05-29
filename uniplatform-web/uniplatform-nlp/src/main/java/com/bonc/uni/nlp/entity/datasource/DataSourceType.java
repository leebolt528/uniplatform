package com.bonc.uni.nlp.entity.datasource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @ClassName:DataSourceType
 * @Package:com.bonc.text.entity.datasource
 * @Description:数据源类型
 * @author:xmy
 */
@Entity
@Table(name = "nlap_data_source_type")
public class DataSourceType {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 显示名称
	 */
	@Column(name = "display_name")
	private String displayName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "DataSourceType [id=" + id + ", name=" + name + ", displayName=" + displayName + "]";
	}

}
