package com.bonc.uni.nlp.dao.datasource;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.datasource.DataSourceType;


/**
 * @ClassName:DataSourceType
 * @Package:com.bonc.text.repository.datasource
 * @Description:TODO
 * @author:xmy
 */
public interface DataSourceTypeRepository extends JpaRepository<DataSourceType, String>{
	
}
