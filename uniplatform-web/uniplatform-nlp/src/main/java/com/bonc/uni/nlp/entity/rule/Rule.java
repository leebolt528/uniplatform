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
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 规则数据库表对应实体类
 * @author zlq
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_rule")
public class Rule {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 规则
	 */
	@Column(nullable = false)
	private String rule;
	/**
	 * 规则创建时间
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
	 * 所属节点id
	 */
	@Column(name="node_id", unique = false, nullable = false)
	private String nodeId;
	/**
	 * 算法id
	 */
	@Column(name = "algorithm_id")
	private String algorithmId;
}
