package com.bonc.uni.nlp.dao.datasource;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.datasource.DataType;

/**
 * @ClassName:DataType
 * @Package:com.bonc.text.repository.datasource
 * @Description:TODO
 * @author:xmy
 */
public interface DataTypeRepository extends JpaRepository<DataType, String>{
	
}
