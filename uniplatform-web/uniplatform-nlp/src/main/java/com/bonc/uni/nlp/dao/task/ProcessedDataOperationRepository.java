package com.bonc.uni.nlp.dao.task;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonc.uni.nlp.entity.task.ProcessedDataOperation;


/**
 * @ClassName:DataProcessTypeRepository
 * @Package:com.bonc.text.repository.task
 * @Description:TODO
 * @author:xmy
 */
public interface ProcessedDataOperationRepository extends JpaRepository<ProcessedDataOperation, String>{

}
