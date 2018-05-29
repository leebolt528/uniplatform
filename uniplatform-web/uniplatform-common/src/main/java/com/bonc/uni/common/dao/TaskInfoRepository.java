package com.bonc.uni.common.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bonc.uni.common.entity.TaskInfo;


/**
 * @author futao
 * 2017年8月30日
 */
public interface TaskInfoRepository extends PagingAndSortingRepository<TaskInfo, Integer>, JpaSpecificationExecutor<TaskInfo>{

}
