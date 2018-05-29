package com.bonc.uni.nlp.dao.strategy;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.strategy.Model;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月2日 下午3:05:32 
*/
@Transactional
public interface ModelRepository extends JpaRepository<Model, String>{

	/**
	 * @param modelSvmDefault
	 * @param id
	 * @return
	 */
	Model findOneByNameAndAlgorithmId(String modelSvmDefault, String id);

	/**
	 * @param algorithmId
	 * @return
	 */
	List<Model> findAllByAlgorithmId(String algorithmId);

	/**
	 * @param algorithmId
	 * @return
	 */
	Model findOneByAlgorithmId(String algorithmId);

	/**
	 * @param algorithmId
	 * @param pageable
	 * @return
	 */
	@Query("select m from Model m where m.algorithmId = ?1")
	List<Model> findAllByAlgorithmId(String algorithmId, Pageable pageable);

	/**
	 * @param algorithmId
	 * @param keyword
	 * @param pageable
	 * @return
	 */
	@Query("select m from Model m where algorithmId = ?1 and name like ?2")
	List<Model> findByAlgorithmIdAndNameLike(String algorithmId, String keyword, Pageable pageable);

	/**
	 * @param algorithmId
	 * @return
	 */
	@Query("select count(*) from Model m where m.algorithmId = ?1")
	int countByAlgorithmId(String algorithmId);

	/**
	 * @param algorithmId
	 * @param keyword
	 * @return
	 */
	@Query("select count(*) from Model m where m.algorithmId = ?1 and name like ?2")
	int countByAlgorithmIdAndNameLike(String algorithmId, String keyword);

	/**
	 * @param modelsArr
	 */
	void deleteByIdIn(String[] modelsArr);

	/**
	 * @param id
	 * @return
	 */
	@Query("select count(*) from Model m where m.functionId = ?1")
	int countByFunctionId(String id);

	/**
	 * @param oldDataSetId
	 * @return
	 */
	List<Model> findAllByDataSetId(String oldDataSetId);

	/**
	 * @param functionId
	 * @param pageable
	 * @return
	 */
	List<Model> findAllByFunctionId(String functionId, Pageable pageable);

	/**
	 * @param functionId
	 * @param keyword
	 * @param pageable
	 * @return
	 */
	@Query("select count(*) from Model m where m.functionId = ?1 and name like ?2")
	List<Model> findByFunctionIdAndNameLike(String functionId, String keyword, Pageable pageable);

	/**
	 * @param functionId
	 * @param keyword
	 * @return
	 */
	@Query("select count(*) from Model m where m.functionId = ?1 and name like ?2")
	int countByFunctionIdAndNameLike(String functionId, String keyword);

}
 