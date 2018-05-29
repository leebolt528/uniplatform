package com.bonc.uni.common.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bonc.uni.common.entity.TaskRegistry;


public interface TaskRegistryRepository extends PagingAndSortingRepository<TaskRegistry, Integer>, JpaSpecificationExecutor<TaskRegistry>{

}
