package com.bonc.uni.nlp.dao.strategy;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.strategy.Business;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月10日 上午11:26:32 
*/
public interface BusinessRepository extends JpaRepository<Business, String>{

	/**
	 * @param keyword
	 * @param pageable
	 * @return
	 */
	@Query("select b from Business b where b.name like ?1")
	List<Business> findAllByNameLike(String keyword, Pageable pageable);

	/**
	 * @param keyword
	 * @return
	 */
	@Query("select count(*) from Business b where b.name like ?1")
	int count(String keyword);

	/**
	 * @param businessName
	 * @return
	 */
	Business findOneByName(String businessName);

	/**
	 * @param arrBusinessesId
	 */
	void deleteByIdIn(String[] arrBusinessesId);

	/**
	 * @param arrBusinessesId
	 * @return
	 */
	List<Business> findAllByIdIn(String[] arrBusinessesId);

	/**
	 * @return
	 */
	@Query("select b from Business b order by createTime DESC")
	List<Business> findAllByTime();

}
 