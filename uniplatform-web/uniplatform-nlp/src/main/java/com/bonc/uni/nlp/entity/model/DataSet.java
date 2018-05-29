package com.bonc.uni.nlp.entity.model;


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
* 数据集  nlap_dataset 实体类
* @author : GaoQiuyuer 
* @version: 2017年11月22日 下午5:00:08 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_dataset")
public class DataSet {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 数据集名称
	 */
	@Column(nullable = false)
	private String name;
	/**
	 * 语料类型id
	 */
	@Column(name = "corpus_type_id")
	private String corpusTypeId;
	/**
	 * 语料集id
	 */
	@Column(name = "corpus_set_id")
	private String corpusSetId;
	/**
	 * 功能id
	 */
	@Column(name= "function_id")
	private String functionId;
	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	private Date createTime;
	/**
	 * 创建用户
	 */
	@Column(name = "create_user")
	private String createUser;
	
	@Column(name = "classify_id")
	private String classifyId;
	
	@Column(name = "is_upload")
	private boolean isUpload = false;
	
	@Column(name = "data_status")
	private int status = 0;
}