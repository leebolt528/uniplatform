package com.bonc.uni.nlp.dao.classify;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.classify.Classify;
import com.bonc.uni.nlp.entity.classify.ClassifyObject;

public interface ClassifyRepository extends JpaRepository<Classify, String> {

	Classify findOneByName(String classifyName);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update Classify c set c.name = ?1 where c.id = ?2")
	void updateClassifyName(String newName, String classifyId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update Classify c set c.isUsed = ?1 where c.id = ?2")
	void updateIsUsed(boolean isUsed, String classifyId);
	
	@Query("select c from Classify c where c.name like ?1")
	List<Classify> findAllByNameLike(String keyWord);
	
	List<Classify> findByIdIn(String[] classifiesId, Sort sort);

	/**
	 * @param classifyId
	 * @return
	 */
	List<ClassifyObject> findAllById(String classifyId);
	
	@Query("select new Classify(id, name) from Classify")
	List<Classify> findAllClassifyName(Sort sort);
}
