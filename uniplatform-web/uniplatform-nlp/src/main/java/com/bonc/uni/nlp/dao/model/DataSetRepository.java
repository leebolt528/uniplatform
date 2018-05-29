package com.bonc.uni.nlp.dao.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bonc.uni.nlp.entity.model.DataSet;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月22日 下午5:02:03 
*/
public interface DataSetRepository extends JpaRepository<DataSet, String>{
	/**
	 * 编辑数据集
	 * @param rule
	 * @param userId
	 * @param ruleId
	 * @return
	 */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update DataSet d set d.name = ?1 where d.id = ?2")
	int updateName(String name, String dataSetId);
	/**
	 * 根据名字和功能查询数据集
	 * @param name
	 * @param functionId
	 * @return
	 */
	DataSet findOneByNameAndFunctionId(String name, String functionId);
	/**
	 * 根据功能获取全部数据集
	 * @param functionId
	 * @param pageable
	 * @return
	 */
	List<DataSet> findAllByFunctionId(String functionId, Pageable pageable);
	/**
	 * 关键字检索数据集
	 * @param keyWord
	 * @param functionId
	 * @param pageable
	 * @return
	 */
	@Query("select d from DataSet d where d.name like ?1 AND d.functionId = ?2")
	List<DataSet> findAllByNameLike(String keyWord, String functionId, Pageable pageable);
	/**
	 * 关键字统计数量
	 * @param keyWord
	 * @param functionId
	 * @return
	 */
	@Query("select count(d) from DataSet d where d.name like ?1 AND d.functionId = ?2")
	int countByNameLike(String keyWord, String functionId);
	/**
	 * 根据功能计算数据集的数量
	 * @param functionId
	 * @return
	 */
	int countByFunctionId(String functionId);
	/**
	 * 根据语料集id计算数量
	 * @param corpusSetId
	 * @return
	 */
	int countByCorpusSetId(String corpusSetId);
	/**
	 * 根据分类体系id计算数量
	 * @param classifyId
	 * @return
	 */
	int countByClassifyId(String classifyId);
	/**
	 * @param functionId
	 * @return
	 */
	@Query("select d from DataSet d where d.functionId = ?1 order by d.createTime DESC")
	List<DataSet> findAllByFunctionId(String functionId);
	/**
	 * 根据分类体系找数据集
	 * @param classifyId
	 * @return
	 */
	@Query(nativeQuery = true, value = "SELECT c.name FROM nlap_dataset c WHERE c.classify_id = :classifyId")
	List<String> findNameByClassifyId(@Param("classifyId") String classifyId);
}
 