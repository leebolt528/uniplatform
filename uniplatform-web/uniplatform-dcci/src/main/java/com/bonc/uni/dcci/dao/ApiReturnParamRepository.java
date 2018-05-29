package com.bonc.uni.dcci.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.uni.dcci.entity.ApiReturnParam;

/**
 * 参数请求 dao
 * @author futao
 * 2017年9月25日
 */
public interface ApiReturnParamRepository extends PagingAndSortingRepository<ApiReturnParam, Integer>, JpaSpecificationExecutor<ApiReturnParam>{

	@Transactional
    @Modifying
    @Query("DELETE FROM ApiReturnParam WHERE detailId = ?1")
    int delete(int detailId);
}
