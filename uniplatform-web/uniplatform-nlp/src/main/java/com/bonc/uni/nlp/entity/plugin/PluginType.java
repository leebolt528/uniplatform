package com.bonc.uni.nlp.entity.plugin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @ClassName:PluginType
 * @Package:com.bonc.text.entity.plugin
 * @Description:plugin的类型
 * @author:xmy
 */
@Entity
@Table(name = "nlap_plugin_type")
public class PluginType {

	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 类型名称，jobClient/taskTracker
	 */
	private String name;

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
	
}
