package com.bonc.uni.nlp.entity.corpus;


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
@Table(name = "nlap_corpus_type")
public class CorpusType {

	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 语料类型英文名称
	 */
	@Column(name = "`name`", unique = true, nullable = true)
	private String name;
	/**
	 * 语料类型中文名称
	 */
	@Column(name = "display_name", unique = true, nullable = true)
	private String displayName;
	/*
	 * index
	 */
	@Column(name = "`index`", unique = true, nullable = true)
	private String index;
}
