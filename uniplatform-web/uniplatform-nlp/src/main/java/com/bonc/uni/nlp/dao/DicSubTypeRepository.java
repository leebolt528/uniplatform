package com.bonc.uni.nlp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.dic.DicSubType;

/**
 * 
 * @author zlq
 *
 */
public interface DicSubTypeRepository extends JpaRepository<DicSubType, String>{

	DicSubType findOneById(String id);

	DicSubType findOneByName(String name);

}
