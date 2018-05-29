package com.bonc.uni.nlp.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bonc.uni.nlp.entity.dic.Function;

/** 
* @author : GaoQiuyuer 
* @version: 2017年10月26日 上午9:39:37 
*/
public interface FuncitonRepository extends JpaRepository<Function, String>{

	@Query("select f.displayName from Function f where f.id = ?1")
	List<Function> findAllByDicTypeId(String typeId);

	@Query("select f.displayName from Function f where f.id = ?1")
	String findAllById(String functionId);

	Function findByName(String functionSegment);

	Function findByDisplayName(String functionSegment);
	
	Function findById(String dependsFunctionId);

	/**
	 * 根据类型id获取全部功能id
	 * @param corpusTypeId
	 * @return
	 */
	@Query(nativeQuery = true, value = "SELECT f.id FROM nlap_function f WHERE f.corpus_type_id = :corpusTypeId")
	List<String> findIdByCorpusTypeId(@Param("corpusTypeId") String corpusTypeId);
	
	/**
	 * 获取全部功能id
	 * @param corpusTypeId
	 * @return
	 */
	@Query(nativeQuery = true, value = "SELECT f.id FROM nlap_function f")
	List<String> findAllId();
	
	List<Function> findAllByCorpusTypeId(String corpusTypeId, Sort sort);

}
 