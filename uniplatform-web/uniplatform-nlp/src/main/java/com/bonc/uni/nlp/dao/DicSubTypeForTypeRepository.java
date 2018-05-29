package com.bonc.uni.nlp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bonc.uni.nlp.entity.dic.DicSubTypeForTypeRelation;

/**
 * 
 * @author zlq
 *
 */
public interface DicSubTypeForTypeRepository extends JpaRepository<DicSubTypeForTypeRelation, String>{

	@Query("select dfr.dicSubTypeId from DicSubTypeForTypeRelation dfr where dfr.typeId = ?1")
	List<String> findSubTypeIdByTypeIds(String typeId);

	DicSubTypeForTypeRelation findOneByTypeId(String dicTypeId);

	List<DicSubTypeForTypeRelation> findAllByTypeId(String dicTypeId);

}
