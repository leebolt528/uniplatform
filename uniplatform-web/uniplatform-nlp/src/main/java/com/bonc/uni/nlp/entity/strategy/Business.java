package com.bonc.uni.nlp.entity.strategy;

import java.util.Date;

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
* @version: 2017年11月10日 上午11:23:12 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_business")
public class Business {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 业务策略名称
	 */
	@Column(name = "name")
	private String name;
	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	private Date createTime;
	/**
	 * 是否在使用中
	 */
	@Column(name = "in_using")
	private int inUsing = 0;
	/**
	 * 创建者
	 */
	@Column(name = "user_id")
	private int userId;

}
 