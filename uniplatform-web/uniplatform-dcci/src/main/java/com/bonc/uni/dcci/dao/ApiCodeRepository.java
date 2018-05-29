package com.bonc.uni.dcci.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bonc.uni.dcci.entity.ApiCode;



/**
 * api错误码
 * @author futao
 * 2017年9月25日
 */
public interface ApiCodeRepository extends PagingAndSortingRepository<ApiCode, Integer>, JpaSpecificationExecutor<ApiCode>{

}
