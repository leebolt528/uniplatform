package com.bonc.uni.nlp.dao.strategy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.strategy.StrategyDicRelation;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月2日 下午5:09:17 
*/
public interface StrategyDicRelationRepository extends JpaRepository<StrategyDicRelation, String>{

	/**
	 * @param strategyId
	 * @return
	 */
	List<StrategyDicRelation> findAllByStrategyId(String strategyId);

	/**
	 * @param dicId
	 * @param strategyId
	 */
	@Query("delete from StrategyDicRelation where dicId = ?1 and strategyId = ?2")
	void deleteByDicIdAndStrategyId(String dicId, String strategyId);

	/**
	 * @param dicId
	 * @return
	 */
	StrategyDicRelation findOneByDicId(String dicId);

	/**
	 * @param arrStrategyIds
	 * @return
	 */
	List<StrategyDicRelation> findAllByStrategyIdIn(String[] arrStrategyIds);

	List<StrategyDicRelation> findAllByDicId(String dicId);

	/**
	 * @param dicId
	 * @param strategyId
	 * @return
	 */
	StrategyDicRelation findOneByDicIdAndStrategyId(String dicId, String strategyId);

}
 