package com.bonc.uni.nlp.dao.strategy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.strategy.StrategyNodeRelation;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月10日 上午11:43:06 
*/
public interface StrategyNodeRelationRepository extends JpaRepository<StrategyNodeRelation, String>{

	/**
	 * @param strategyId
	 * @return
	 */
	List<StrategyNodeRelation> findAllByStrategyId(String strategyId);

	/**
	 * @param nodeId
	 * @return
	 */
	List<StrategyNodeRelation> findAllByNodeId(String nodeId);

}
 