package com.bonc.uni.nlp.entity.classify;


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
 * 分类体系对应实体
 * @author zlq
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_classify_object")
public class ClassifyObject {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 分类对象名称
	 */
	@Column(unique = false, nullable = false)
	private String name;
	/**
	 * 所属分类体系id
	 */
	@Column(name="classify_id", unique = false, nullable = false)
	private String classifyId;
	/**
	 * 分类体系节点创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", unique = false, nullable = true)
	private Date createTime;
	/**
	 *  创建用户id
	 */
	@Column(name="create_user", unique = false, nullable = true)
	private String createUser;
	
    public ClassifyObject(String id, String name) {  
       super();  
       this.id = id;
       this.name = name;
   }  
}
