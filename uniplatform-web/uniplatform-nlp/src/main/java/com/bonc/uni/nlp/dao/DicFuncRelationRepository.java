package com.bonc.uni.nlp.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.dic.DicFuncRelation;

/**
 * 
 * @author zlq
 *
 */
public interface DicFuncRelationRepository extends JpaRepository<DicFuncRelation, String> {

	List<DicFuncRelation> findAllByDicId(String id);

	DicFuncRelation findOneByDicIdAndFunctionId(String dicTypeId, String functionId);

	/**
	 * @param dicId
	 * @param i
	 * @return
	 */
	List<DicFuncRelation> findAllByDicIdAndStatus(String dicId, int i);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update DicFuncRelation d set d.status = ?1 where d.dicId = ?2 and d.functionId = ?3")
	int updateDicStatus(int dicStatus, String dicId, String functionId);

	/**
	 * 修改词库所有标签
	 * 
	 * @param dicStatus
	 * @param dicId
	 * @return
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update DicFuncRelation d set d.status = ?1 where d.dicId = ?2")
	int updateDicStatus(int dicStatus, String dicId);

	/**
	 * @param dicId
	 * @param string
	 * @return
	 */
	@Modifying
	@Query("delete from DicFuncRelation d where d.dicId = ?1 and d.functionId = ?2")
	int deleteByDicIdAndFunctionId(String dicId, String functionId);

	@Query("select d.dicId from DicFuncRelation d where d.functionId = ?1")
	List<String> findSubTypeIdByFunctionId(String functionId);

	/**
	 * @param functionId
	 * @return
	 */
	List<DicFuncRelation> findAllByFunctionId(String functionId);

}
