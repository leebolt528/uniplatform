package com.bonc.uni.nlp.dao.strategy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.strategy.Algorithm;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月2日 上午11:39:40 
*/
public interface AlgorithmRepository extends JpaRepository<Algorithm, String>{

	/**
	 * @param algorithmSegmentDic
	 * @return
	 */
	Algorithm findOneByName(String algorithmSegmentDic);

	/**
	 * @param id 功能id
	 * @return
	 */
	List<Algorithm> findAllByFunctionId(String id);

	/**
	 * @param algorithmId
	 * @param functionId
	 * @return
	 */
	Algorithm findOneByIdAndFunctionId(String algorithmId, String functionId);

	/**
	 * @param algorithmName
	 * @return
	 */
	Algorithm findOneByDisplayName(String algorithmName);

	/**
	 * @param i
	 * @return
	 */
	List<Algorithm> findAllByHasModel(int i);

}
 