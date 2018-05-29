package com.bonc.uni.nlp.entity.rule;

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
 * 节点数据库实体类
 * @author BONC_NLP
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_node")
public class Node {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 模板名称
	 */
	@Column(unique = false, nullable = false)
	private String name;
	/**
	 * 模板创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", unique = false, nullable = true)
	private Date createTime;
	/**
	 *  创建用户id
	 */
	@Column(name="user_id", unique = false, nullable = true)
	private String userId;
	/**
	 * 所属模板id
	 */
	@Column(name="template_id", unique = false, nullable = false)
	private String templateId;

}
