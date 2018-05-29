package com.bonc.uni.nlp.entity.label;

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
* @version: 2017年12月5日 下午2:49:54 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_label_word_relation")
public class LabelWordRelation {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 标签id
	 */
	@Column(name = "label_id")
	private String labelId;
	/**
	 * 词id
	 */
	@Column(name = "word_id")
	private String wordId;
	/**
	 * 词典id
	 */
	@Column(name = "dic_id")
	private String dicId;
}