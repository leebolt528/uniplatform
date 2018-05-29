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
@Table(name = "nlap_classify")
public class Classify {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 分类体系名称
	 */
	@Column(unique = true, nullable = false)
	private String name;
	/**
	 * 分类体系创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", unique = false, nullable = true)
	private Date createTime;
	/**
	 *  创建用户id
	 */
	@Column(name="create_user", unique = false, nullable = true)
	private String createUser;
	/**
	 * 分类体系使用状态啊
	 */
	@Column(name="is_used")
	private boolean isUsed = false;
	
    public Classify(String id, String name) {  
        super();  
        this.id = id;
        this.name = name;
    } 
}
