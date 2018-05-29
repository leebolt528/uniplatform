package com.bonc.uni.nlp.entity.strategy;

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
* @version: 2017年11月10日 上午11:31:38 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_business_node_relation")
public class BusinessNodeRelation {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 业务策略id
	 */
	@Column(name = "business_id")
	private String businessId;
	/**
	 * 业务策略节点id
	 */
	@Column(name = "node_id")
	private String nodeId;

}
 