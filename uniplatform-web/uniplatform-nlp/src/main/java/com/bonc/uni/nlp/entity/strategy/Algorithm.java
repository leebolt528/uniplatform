package com.bonc.uni.nlp.entity.strategy;

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
* @version: 2017年11月2日 上午11:23:33 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_algorithm")
public class Algorithm {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 算法名称
	 */
	@Column(unique = true, nullable = false)
	private String name;
	/**
	 *  算法展示名称
	 */
	@Column(name="display_name" ,unique = false, nullable = false)
	private String displayName;
	/**
	 * 功能id
	 */
	@Column(name = "function_id")
	private String functionId;
	/**
	 * 是否有模板
	 */
	@Column(name = "has_model")
	private int hasModel = 0;
	/**
	 * 是否有规则
	 */
	@Column(name = "has_rule")
	private int hasRule = 0;
	/**
	 * 是否有词库
	 */
	@Column(name = "has_dic")
	private int hasDic = 0;
	/**
	 * 是否批处理
	 */
	@Column(name = "has_batch")
	private int hasBatch = 0;
	/**
	 * 是否有依赖算法
	 */
	@Column(name = "has_dependency")
	private int hasDependency = 0;

}
