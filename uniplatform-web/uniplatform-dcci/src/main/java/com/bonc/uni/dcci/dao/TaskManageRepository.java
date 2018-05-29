package com.bonc.uni.dcci.dao;

import com.bonc.uni.dcci.util.StatusType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bonc.uni.dcci.entity.TaskManage;



/**
 * 任务管理
 * @author futao
 * 2017年9月11日
 */
public interface TaskManageRepository extends PagingAndSortingRepository<TaskManage, Integer>, JpaSpecificationExecutor<TaskManage>{

    public long countByStatus (StatusType statusType);

}
