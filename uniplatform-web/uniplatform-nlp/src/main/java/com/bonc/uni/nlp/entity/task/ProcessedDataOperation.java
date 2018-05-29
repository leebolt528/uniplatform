package com.bonc.uni.nlp.entity.task;
/**
 * @ClassName:DataProcessType
 * @Package:com.bonc.text.entity.task
 * @Description:数据处理方式
 * @author:Chris
 * @date:2017年8月11日 下午2:41:47
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "nlap_processed_data_operation")
public class ProcessedDataOperation {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;

	private String name;

	@Column(name="display_name")
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
		return "DataProcessWay [id=" + id + ", name=" + name + ", displayName=" + displayName + "]";
	}

}
