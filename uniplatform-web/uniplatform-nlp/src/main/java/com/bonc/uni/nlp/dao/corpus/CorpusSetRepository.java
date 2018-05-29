package com.bonc.uni.nlp.dao.corpus;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bonc.uni.nlp.entity.corpus.CorpusSet;


public interface CorpusSetRepository extends JpaRepository<CorpusSet, String> {

	CorpusSet findOneByNameAndFunctionId(String name, String functionId);
	
	List<CorpusSet> findAllByFunctionId(String functionId, Pageable pageable);
	
	List<CorpusSet> findAllByFunctionId(String functionId);
	
	@Query("select c from CorpusSet c where c.name like ?1 AND c.functionId = ?2")
	List<CorpusSet> findAllByNameLike(String keyWord, String functionId, Pageable pageable);
	
	@Query("select count(c) from CorpusSet c where c.name like ?1 AND c.functionId = ?2")
	int countByNameLike(String keyWord, String functionId);
	
	List<CorpusSet> findAllByClassifyId(String classifyId, Sort sort);

	@Query(nativeQuery = true, value = "SELECT c.name FROM nlap_corpus_set c WHERE c.classify_id = :classifyId")
	List<String> findNameByClassifyId(@Param("classifyId") String classifyId);
	
	
	int countByFunctionId(String functionId);
    /**
     * 编辑语料集
     */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update CorpusSet c set c.name = ?2 where c.id = ?1")
	int updateCorpusSet(String corpusSetId, String corpusSetName);
    /**
     * 编辑语料集状态
     */
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update CorpusSet c set c.isUsed = ?1 where c.id = ?2")
	int updateIsUsed(boolean isUsed, String corpusSetId);
	/**
	 * 分类体系下的语料集数量
	 */
	int countByClassifyId(String classifyId);
}
