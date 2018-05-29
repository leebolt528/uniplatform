package com.bonc.uni.nlp.dao.strategy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.strategy.FunctionDependFunction;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月3日 上午9:41:51 
*/
public interface FunctionDependFunctionRepository extends JpaRepository<FunctionDependFunction, String>{

	/**
	 * @param id
	 * @return
	 */
	FunctionDependFunction findByFunctionId(String id);

	/**
	 * @param functionId
	 * @return
	 */
	List<FunctionDependFunction> findAllByFunctionId(String functionId);

}
 