package com.bonc.uni.nlp.dao.strategy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.strategy.BusinessNodeRelation;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月10日 上午11:35:18 
*/
public interface BusinessNodeRelationRepository extends JpaRepository<BusinessNodeRelation, String>{

	/**
	 * @param businessId
	 * @return
	 */
	List<BusinessNodeRelation> findAllByBusinessId(String businessId);

	/**
	 * @param nodeId
	 * @return
	 */
	BusinessNodeRelation findOneByNodeId(String nodeId);

	/**
	 * @param nodeId
	 * @return
	 */
	List<BusinessNodeRelation> findAllByNodeId(String nodeId);

	/**
	 * @param delNodeId
	 * @param businessId
	 * @return
	 */
	BusinessNodeRelation findOneByNodeIdAndBusinessId(String delNodeId, String businessId);


}
 