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
* @version: 2017年10月25日 下午5:27:42 
*/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_dic_type")
public class DicType {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 词库类型名称
	 */
	@Column(unique = true, nullable = false)
	private String name;
	/**
	 *  词库类型展示名称
	 */
	@Column(name="display_name" ,unique = true, nullable = false)
	private String displayName;
	
	@Column(name="type_index" ,unique = true, nullable = false)
	private Integer index;
}

 