package com.bonc.uni.dcci.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bonc.uni.dcci.entity.DataApi;

/**
 * 数据apidao
 * @author futao
 * 2017年9月22日
 */
public interface DataApiRepository extends PagingAndSortingRepository<DataApi, Integer>, JpaSpecificationExecutor<DataApi>{

}
