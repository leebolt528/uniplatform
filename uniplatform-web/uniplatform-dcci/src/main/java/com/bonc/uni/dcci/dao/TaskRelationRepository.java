package com.bonc.uni.dcci.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bonc.uni.dcci.entity.TaskRelation;



/**
 * 任务
 * @author futao
 * 2017年9月11日
 */
public interface TaskRelationRepository extends PagingAndSortingRepository<TaskRelation, Integer>, JpaSpecificationExecutor<TaskRelation>{

}
