package com.bonc.uni.nlp.entity.dic;

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
* @version: 2017年10月26日 上午9:30:49 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_function")
public class Function {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 标签名称
	 */
	@Column(unique = true, nullable = false)
	private String name;
	/**
	 *  标签展示名称
	 */
	@Column(name="display_name" ,unique = true, nullable = false)
	private String displayName;
	/**
	 * index
	 */
	@Column(name="function_index" ,unique = true, nullable = false)
	private Integer index;
	/**
	 * 语料类型id
	 */
	@Column(name="corpus_type_id", unique = false, nullable = true)
	private String corpusTypeId;
}
 