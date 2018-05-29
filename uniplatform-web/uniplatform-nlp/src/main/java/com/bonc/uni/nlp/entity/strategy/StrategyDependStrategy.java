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
* @version: 2017年11月2日 下午1:59:05 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_strategy_strategy_dependency")
public class StrategyDependStrategy {
	@Id
	@GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@GeneratedValue(generator = "uuidGenerator")
	private String id;
	/**
	 *  算法id
	 */
	@Column(name = "strategy_id")
	private String strategyId;
	/**
	 * 策略id
	 */
	@Column(name = "depend_strategy_id")
	private String dependStrategyId;

}
 