package com.bonc.uni.common.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.uni.common.entity.Alert;


/**
 * alert 预警 jpa
 * @author futao
 * 2017年9月1日
 */
public interface AlertRepository extends PagingAndSortingRepository<Alert, Integer>, JpaSpecificationExecutor<Alert>{

	@Transactional
    @Modifying
    @Query("DELETE FROM Alert WHERE infoId = ?1")
    int delete(int infoId);
}
