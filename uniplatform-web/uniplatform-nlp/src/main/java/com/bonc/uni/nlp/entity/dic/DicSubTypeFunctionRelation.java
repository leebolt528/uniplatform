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
* @version: 2017年10月26日 上午10:21:21 
*/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_subtype_function_relation")
public class DicSubTypeFunctionRelation {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 词库类型id
	 */
	@Column(name="dic_subtype_id" ,unique = false, nullable = false)
	private String dicSubTypeId;
	/**
	 *  功能类型id
	 */
	@Column(name="function_id" , nullable = false)
	private String functionId;

}
 