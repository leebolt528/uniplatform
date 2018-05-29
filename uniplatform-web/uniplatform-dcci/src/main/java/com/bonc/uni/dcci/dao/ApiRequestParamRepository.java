package com.bonc.uni.dcci.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.uni.dcci.entity.ApiRequestParam;

/**
 * 参数请求 dao
 * @author futao
 * 2017年9月25日
 */
public interface ApiRequestParamRepository extends PagingAndSortingRepository<ApiRequestParam, Integer>, JpaSpecificationExecutor<ApiRequestParam>{

	@Transactional
    @Modifying
    @Query("DELETE FROM ApiRequestParam WHERE detailId = ?1")
    int delete(int detailId);
}
