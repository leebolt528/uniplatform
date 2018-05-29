package com.bonc.uni.nlp.entity.plugin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

/**
 * @ClassName:Plugin
 * @Package:com.bonc.text.entity.plugin
 * @Description:插件
 * @author:xmy
 */
@Entity
@Table(name = "nlap_plugin")
public class Plugin {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 名称
	 */
	@Column(unique = true, nullable = false)
	private String name;
	/**
	 * 保存路径
	 */
	@Column(name = "save_path", nullable = false)
	private String savePath;
	/**
	 * 上传时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "upload_time")
	private Date uploadTime;
	/**
	 * 最近修改时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	private Date updateTime;
	/**
	 * plugin的类型和对应的全类名
	 * key:类型，value:类名
	 */
	@ElementCollection
    @CollectionTable(name="nlap_plugin_type_class")
	@MapKeyColumn(name = "plugin_class_name")
    @Column(name = "plugin_type")
	private Map<String, String> classMap;
	/**
	 * 状态
	 */
	@Column(nullable = false)
	private Integer status;
	/**
	 * 预留扩展属性
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "plugin_id")
	private List<PluginSpec> fields;
	
	/**
	 * plugin文件名
	 */
	@Column(name = "filename")
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

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


	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public List<PluginSpec> getFields() {
		return fields;
	}

	public void setFields(List<PluginSpec> fields) {
		this.fields = fields;
	}

	public Map<String, String> getClassMap() {
		return classMap;
	}

	public void setClassMap(Map<String, String> classMap) {
		this.classMap = classMap;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Plugin [id=" + id + ", name=" + name + ", savePath=" + savePath + ", uploadTime=" + uploadTime
				+ ", updateTime=" + updateTime + ", classMap=" + classMap + ", status=" + status + ", fields=" + fields
				+ ", fileName=" + fileName + "]";
	}



}
