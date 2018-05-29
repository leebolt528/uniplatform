package com.bonc.uni.nlp.entity.datasource;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

/**
 * @ClassName:DataSource
 * @Package:com.bonc.text.entity.datasource
 * @Description:数据源
 * @author:XMY
 */
@Entity
@Table(name = "nlap_data_source")
public class DataSource {
	/**
	 * @Fields serialVersionUID : TODO
	 */
	// private static final long serialVersionUID = 1L;
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 数据源名称
	 */
	@Column(unique = true, nullable = false)
	private String name;
	/**
	 * 数据源连接ip
	 */
	private String ip;
	/**
	 * 数据源连接端口号
	 */
	private String port;
	/**
	 * 数据源连接用户名
	 */
	@Column(name = "user_name")
	private String username;
	/**
	 * 数据源连接密码
	 */
	private String password;
	/**
	 * 数据源地址
	 */
	private String path;
	/**
	 * 数据源类型
	 */
	@Column(name = "data_source_type_id")
	private String dataSourceType;
	/**
	 * 数据源中的数据类型
	 */
	@Column(name = "data_type_id")
	private String dataType;
	/**
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createTime;
	/**
	 * 最近一次修改时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	private Date updateTime;

	/**
	 * 状态
	 */
	@Column(nullable = false)
	private Integer status;
	/**
	 * 预留扩展属性
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "data_source_id")
	private List<DataSourceSpec> fields;

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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public List<DataSourceSpec> getFields() {
		return fields;
	}

	public void setFields(List<DataSourceSpec> fields) {
		this.fields = fields;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "DataSource [id=" + id + ", name=" + name + ", ip=" + ip + ", port=" + port + ", username=" + username
				+ ", password=" + password + ", path=" + path + ", dataSourceType=" + dataSourceType + ", dataType="
				+ dataType + ", createTime=" + createTime + ", updateTime=" + updateTime + ", status=" + status
				+ ", fields=" + fields + "]";
	}

	

}
