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
 * 模板数据库实体类
 * @author zlq
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_template")
public class Template {
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
	 * 所属规则类型
	 */
	@Column(name="rule_type_id", unique = false, nullable = false)
	private String ruleTypeId;
}
