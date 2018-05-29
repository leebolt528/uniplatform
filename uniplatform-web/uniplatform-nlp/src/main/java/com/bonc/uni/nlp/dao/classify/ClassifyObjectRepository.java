package com.bonc.uni.nlp.dao.classify;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.classify.ClassifyObject;

public interface ClassifyObjectRepository extends JpaRepository<ClassifyObject, String> {

	ClassifyObject findOneByName(String classifyObjectName);
	
	List<ClassifyObject> findAllByClassifyId(String classifyId, Sort sort);
	
	List<ClassifyObject> findAllByClassifyId(String classifyId, Pageable pageable);
	
	ClassifyObject findOneByNameAndClassifyId(String name, String classifyId);
	
	void deleteByClassifyId(String classifyId);
	
	ClassifyObject findOneByIdAndClassifyId(String id, String classifyId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update ClassifyObject c set c.name = ?1 where c.id = ?2")
	void updateObjectName(String newName, String objectId);
	
	@Query("select o from ClassifyObject o where o.name like ?1")
	List<ClassifyObject> findAllByNameLike(String keyWord);
	
	@Query("select new ClassifyObject(id,name) from ClassifyObject co where co.classifyId = ?1 order by co.createTime")
	List<ClassifyObject> findObjectsIdAndName(String classifyId);
	
	int countByClassifyId(String classifyId);
}
