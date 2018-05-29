package com.bonc.uni.dcci.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.uni.dcci.entity.TaskSite;
import com.bonc.uni.dcci.util.StatusType;



/**
 * 任务管理
 * @author futao
 * 2017年9月11日
 */
public interface TaskSiteRepository extends PagingAndSortingRepository<TaskSite, Integer>, JpaSpecificationExecutor<TaskSite>{

	@Transactional
    @Modifying
    @Query("DELETE FROM TaskSite WHERE taskRelation = ?1")
    int delete(int taskRelation);
	
	@Transactional
    @Modifying
    @Query("UPDATE TaskSite t SET t.userId = :userId, t.status = :status  WHERE taskRelation = :taskRelation")
	int updateRecall(@Param("taskRelation") int taskRelation,@Param("userId") int userId,@Param("status") StatusType.UrlSiteType status);
}
