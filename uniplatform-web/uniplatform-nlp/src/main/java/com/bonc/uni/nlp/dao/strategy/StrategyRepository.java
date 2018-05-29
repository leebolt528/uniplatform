package com.bonc.uni.nlp.dao.strategy;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.strategy.Strategy;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月2日 下午2:44:45 
*/
@Transactional
public interface StrategyRepository extends JpaRepository<Strategy, String>{

	/**
	 * @param nodeName
	 * @return
	 */
	Strategy findOneByName(String nodeName);

	/**
	 * @param searchWord
	 * @param pageable
	 * @return
	 */
	@Query("select s from Strategy s where s.functionId = ?1")
	List<Strategy> findAllByFunctionId(String functionId, Pageable pageable);

	/**
	 * @param searchWord
	 * @return
	 */
	@Query("select count(*) from Strategy s where s.functionId = ?1")
	int count(String functionId);

	/**
	 * @param dependsFunctionId 依赖的功能id
	 * @return
	 */
	List<Strategy> findAllByFunctionId(String dependsFunctionId);

	/**
	 * @param arrStrategyIds
	 * @return
	 */
	List<Strategy> findAllByIdIn(String[] arrStrategyIds);

	/**
	 * @param arrStrategyIds
	 */
	void deleteByIdIn(String[] arrStrategyIds);

	/**
	 * @param functionId
	 * @param i
	 * @return
	 */
	Strategy findOneByFunctionIdAndDefaultUse(String functionId, int i);

	/**
	 * @return
	 */
	@Query("select s from Strategy s ORDER BY createTime DESC")
	List<Strategy> find();

}
 