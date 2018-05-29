package com.bonc.uni.nlp.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.dic.DicSubTypeFunctionRelation;

/** 
* @author : GaoQiuyuer 
* @version: 2017年10月26日 上午10:23:54 
*/
public interface DicSubTypeFunctionRelationRepository extends JpaRepository<DicSubTypeFunctionRelation, String>{

	/**
	 * @param typeId
	 * @return
	 */
	@Query("select dfr.functionId from DicSubTypeFunctionRelation dfr where dfr.dicSubTypeId = ?1")
	List<String> findFunctionIds(String subTypeId);

	List<DicSubTypeFunctionRelation> findAllByDicSubTypeId(String dicSubTypeId);

}
 