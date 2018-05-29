package com.bonc.uni.nlp.entity.task;

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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @ClassName:Task
 * @Package:com.bonc.text.entity.task
 * @Description:任务
 * @author:xmy
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_task")
public class Task {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 任务名称
	 */
	@Column(unique = true, nullable = false)
	private String name;
	/**
	 * 任务类型，分为：预定义（true）；自定义（false）
	 */
	private boolean predefined;
	/**
	 * 要处理的数据源
	 */
	@Column(name = "data_source_id")
	private String dataSource;
	/**
	 * 和数据源绑定的业务策略(如果是预定义任务，则必须有，且必定不能有plugin)
	 */
	@Column(name = "business_id")
	private String business;
	/**
	 * 和数据源绑定的plugin(如果是自定义任务，则必须有)
	 */
	@Column(name = "plugin_id")
	private String plugin;
	/**
	 * 任务的状态
	 */
	@Column(name = "task_status")
	private int taskStatus = 0;
	/**
	 * 执行类型，分类：实时（realtime）、定时（timing）、周期（cycle）
	 */
	@Column(name = "execute_type_id")
	private String executeType;
	/**
	 * 执行时间
	 */
	@Column(name = "execute_time")
	private String executeTime;
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
	 * 预留扩展字段
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id")
	private List<TaskSpec> fields;
	/**
	 * 处理完后对数据的处理方式
	 */
	@Column(name = "processed_data_operation_id")
	private String processedDataOperation;


	public List<TaskSpec> getFields() {
		return fields;
	}

	public void setFields(List<TaskSpec> fields) {
		this.fields = fields;
	}

}
