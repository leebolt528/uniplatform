package com.bonc.uni.dcci.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.uni.dcci.entity.ApiDetail;


/**
 * dao
 * @author futao
 * 2017年9月22日
 */
public interface ApiDetailRepository extends PagingAndSortingRepository<ApiDetail, Integer>, JpaSpecificationExecutor<ApiDetail>{

	@Transactional
    @Modifying
    @Query("DELETE FROM ApiDetail WHERE apiId = ?1")
    int delete(int apiId);
}
