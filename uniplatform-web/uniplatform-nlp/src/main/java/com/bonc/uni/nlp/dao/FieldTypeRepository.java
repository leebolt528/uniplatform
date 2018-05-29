package com.bonc.uni.nlp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.dic.FieldType;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月11日 下午4:37:50 
*/
public interface FieldTypeRepository extends JpaRepository<FieldType, String>{

	/**
	 * @param subTypeId
	 * @return
	 */
	List<FieldType> findAllBySubTypeId(String subTypeId);

}
 