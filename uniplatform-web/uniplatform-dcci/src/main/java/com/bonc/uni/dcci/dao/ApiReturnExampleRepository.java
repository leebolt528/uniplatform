package com.bonc.uni.dcci.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.uni.dcci.entity.ApiReturnExample;

/**
 * 请求示例 dao
 * @author futao
 * 2017年9月25日
 */
public interface ApiReturnExampleRepository extends PagingAndSortingRepository<ApiReturnExample, Integer>, JpaSpecificationExecutor<ApiReturnExample>{
 
	@Transactional
    @Modifying
    @Query("DELETE FROM ApiReturnExample WHERE detailId = ?1")
    int delete(int detailId);
}
