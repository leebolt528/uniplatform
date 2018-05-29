package com.bonc.uni.nlp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.dic.DicType;

/** 
* @author : GaoQiuyuer 
* @version: 2017年10月25日 下午6:15:27 
*/
public interface DicTypeRepository extends JpaRepository<DicType, String> {

	DicType findOneById(String dicTypeId);

	DicType findOneByName(String string);
}
 