package com.bonc.uni.nlp.entity.rule;

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
 * 规则分类表对应实体
 * @author zlq
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_rule_type")
public class RuleType {
	
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 规则分类名称
	 */
	@Column(name="display_name", unique = true, nullable = false)
	private String displayName;
	/**
	 * 规则分类英文名称
	 */
	@Column(name="name", unique = true, nullable = false)
	private String name;
	/**
	 * index
	 */
	@Column(name="`index`", unique = true, nullable = false)
	private int index;
}
