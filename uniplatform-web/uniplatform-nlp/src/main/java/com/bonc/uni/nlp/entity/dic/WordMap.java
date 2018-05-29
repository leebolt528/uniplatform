package com.bonc.uni.nlp.entity.dic;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_resource_word_map")
public class WordMap {

	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 词名称
	 */
	@Column(name="word_key" ,unique = false, nullable = false)
	private String wordKey;
	/**
	 * Map类型的词value
	 */
	@Column(name="word_value" ,unique = false, nullable = false)
	private String wordValue;
	/**
	 * 是否启用,默认为:未启用
	 */
	@Column(name="word_status" ,unique = false, nullable = false)
	private int wordStatus = 1;
	/**
	 * 所属词典
	 */
	@Column(name = "dic_id")
	private String dictionaryId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time" ,unique = false, nullable = false)
	private Date createTime = new Date();
}
