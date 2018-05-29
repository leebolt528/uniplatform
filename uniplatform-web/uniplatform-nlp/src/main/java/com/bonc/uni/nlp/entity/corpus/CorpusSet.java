package com.bonc.uni.nlp.entity.corpus;



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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_corpus_set")
public class CorpusSet {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 语料集名称
	 */
	@Column(name = "name")
	private String name;
	/**
	 * 功能名称
	 */
	@Column(name = "function_id")
	private String functionId;
	/**
	 * 对应分类体系id
	 */
	@Column(name = "classify_id")
	private String classifyId;
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
	/**
	 * 是否被使用
	 */
	@Column(name = "is_used")
	private boolean isUsed = false;
}
