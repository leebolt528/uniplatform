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
* @version: 2017年11月10日 上午11:36:10 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_strategy_node_relation")
public class StrategyNodeRelation {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 * 业务策略节点id
	 */
	@Column(name = "node_id")
	private String nodeId;
	/**
	 * 功能策略id
	 */
	@Column(name = "strategy_id")
	private String strategyId;
	/**
	 * 功能策略顺序
	 */
	@Column(name="strategy_index" ,nullable = false)
	private Integer strategyIndex;

}
 
 