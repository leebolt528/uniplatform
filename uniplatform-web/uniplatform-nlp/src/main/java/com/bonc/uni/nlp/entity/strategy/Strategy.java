package com.bonc.uni.nlp.entity.strategy;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月2日 下午2:36:44 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_strategy")
public class Strategy {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 策略名称
	 */
	@Column(unique = true, nullable = false)
	private String name;
	/**
	 * 功能id
	 */
	@Column(name = "function_id")
	private String functionId;
	/**
	 * 算法id
	 */
	@Column(name = "algorithm_id")
	private String algorithmId;
	/**
	 * 模型id
	 */
	@Column(name = "model_id")
	private String modelId;
	/**
	 * 模型id
	 */
	@Column(name = "rule_id")
	private String ruleId;
	/**
	 * 是否批处理
	 */
	@Column(name = "batch")
	private int batch;
	/**
	 * 是否为默认策略
	 */
	@Column(name = "default_use")
	private int defaultUse = 0;
	/**
	 * 是否在使用中
	 */
	@Column(name = "in_using")
	private int inUsing = 0;
	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	private Date createTime;
	/**
	 * 策略类型（system:系统    custom:自定义）
	 */
	@Column(name = "operation")
	private String operation;
	/**
	 * 创建者
	 */
	@Column(name = "user_id")
	private int userId;

}
 