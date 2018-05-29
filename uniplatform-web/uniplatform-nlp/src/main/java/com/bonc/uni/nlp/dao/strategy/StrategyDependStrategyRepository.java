package com.bonc.uni.nlp.dao.strategy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.strategy.StrategyDependStrategy;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月2日 下午2:03:14 
*/
public interface StrategyDependStrategyRepository extends JpaRepository<StrategyDependStrategy, String>{

	/**
	 * @param strategyId
	 * @return
	 */
	List<StrategyDependStrategy> findByStrategyId(String strategyId);

	/**
	 * @param arrStrategyIds
	 * @return
	 */
	List<StrategyDependStrategy> findAllByStrategyIdIn(String[] arrStrategyIds);

	/**
	 * @param delDependId
	 * @param strategyId
	 * @return
	 */
	StrategyDependStrategy findOneByDependStrategyIdAndStrategyId(String delDependId, String strategyId);

	/**
	 * @param strategyId
	 * @return
	 */
	List<StrategyDependStrategy> findAllByStrategyId(String strategyId);

}
 