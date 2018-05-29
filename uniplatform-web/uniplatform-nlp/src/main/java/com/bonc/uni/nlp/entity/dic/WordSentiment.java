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


/**
 * @ClassName:Word
 * @Package:com.bonc.text.entity.dictionary
 * @Description:TODO
 * @author:Chris
 * @date:2017年4月12日 下午12:02:21
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_resource_word_sentiment")
public class WordSentiment {

	
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 词名称
	 */
	private String word = "";
	/**
	 * 词性
	 */
	private String nature = "";
	/**
	 * 评分
	 */
	@Column(name = "word_grade", unique = false, nullable = true)
	private Double grade;
	/**
	 * 是否启用,默认为:未启用
	 */
	@Column(name = "word_status")
	private Integer wordStatus = 1;
	/**
	 * 所属词典
	 */
	@Column(name = "dic_id")
	private String dictionaryId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time" ,unique = false, nullable = false)
	private Date createTime = new Date();
}
