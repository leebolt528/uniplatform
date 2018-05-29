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
* @version: 2017年11月2日 下午5:07:00 
*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "nlap_strategy_dic_relation")
public class StrategyDicRelation {
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
	 *  词库id
	 */
	@Column(name = "dic_id")
	private String dicId;

}
