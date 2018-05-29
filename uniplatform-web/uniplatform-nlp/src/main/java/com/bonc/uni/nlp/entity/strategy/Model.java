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
* @version: 2017年11月2日 下午3:03:24 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_model")
public class Model {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 模型名称
	 */
	@Column(nullable = false)
	private String name;
	/**
	 * 算法id
	 */
	@Column(name = "algorithm_id")
	private String algorithmId;
	/**
	 * 功能id
	 */
	@Column(name = "function_id")
	private String functionId;
	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	private Date createTime;
	/**
	 * 训练完成时间
	 */
	@Column(name = "trained_time")
	private Date trainedTime;
	/**
	 * 数据集id 
	 */
	@Column(name = "data_id")
	private String dataSetId;
	/**
	 * 模型状态
	 */
	@Column(name = "model_status")
	private int modelStatus = 2;
	/**
	 * 使用状态
	 */
	@Column(name = "in_using")
	private int inUsing = 0;
	/**
	 * 保存路径
	 */
	@Column(name = "savepath")
	private String savepath;
	/**
	 * 策略状态（system：系统  custom：用户  custom_upload：用户上传）
	 */
	@Column(name = "operation")
	private String operation;
	/**
	 * 创建者
	 */
	@Column(name = "user_id")
	private int userId;
	
}
