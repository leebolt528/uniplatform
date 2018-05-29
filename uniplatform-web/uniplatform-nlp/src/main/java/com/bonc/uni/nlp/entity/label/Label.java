package com.bonc.uni.nlp.entity.label;

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
* @version: 2017年12月5日 下午2:46:36 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_label")
public class Label {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 标签名称
	 */
	@Column(nullable = false)
	private String name;
	/**
	 * 创建者id
	 */
	@Column(name = "user_id")
	private int userId;
	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	private Date createTime;
	/**
	 * 更新时间
	 */
	@Column(name = "update_time")
	private Date updateTime;
	/**
	 * 使用状态
	 */
	@Column(name = "label_status")
	private Integer status = 0;
}
 