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
* @author : GaoQiuyuer 
* @version: 2017年10月25日 下午5:32:06 
*/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_dictionary")
public class Dictionary {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 词库创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time" ,unique = false, nullable = false)
	private Date createTime;
	/**
	 * 词库名称
	 */
	@Column(unique = false, nullable = false)
	private String name;
	/**
	 *  创建用户id
	 */
	@Column(name="user_id" ,unique = false, nullable = false)
	private int userId;
	/**
	 *  词库格式（map，ordinary···）
	 */
	@Column(unique = false, nullable = false)
	private String format;
	/**
	 *  词库类型id
	 */
	@Column(name="dic_type_id" ,unique = false, nullable = false)
	private String dicTypeId;
	/**
	 *  词库子类型id
	 */
	@Column(name="dic_sub_type_id" ,unique = false, nullable = false)
	private String dicSubTypeId;
	/**
	 *  领域词库词典类型id
	 */
	@Column(name = "field_type_id")
	private String fieldTypeId;
	
	@Column(name="status" ,unique = false, nullable = false)
	private Integer status=0;



}
 