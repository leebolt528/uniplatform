package com.bonc.uni.common.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bonc.uni.common.dao.TaskInfoRepository;
import com.bonc.uni.common.dao.TaskRegistryRepository;
import com.bonc.uni.common.entity.TaskInfo;
import com.bonc.uni.common.entity.TaskRegistry;
import com.bonc.uni.common.jpa.Specification.SpecificationUtil;
import com.bonc.uni.common.quartz.service.SchedulerService;
import com.bonc.uni.common.service.AlertService;
import com.bonc.uni.common.service.TaskService;
import com.bonc.uni.common.util.PlatformType;

/**
 * TaskService 实现
 * @author futao
 * 2017年8月30日
 */
@Service("taskService")
public class TaskServiceImpl implements TaskService {

	@Autowired
	TaskInfoRepository taskInfoRepository;
	
	@Autowired
	TaskRegistryRepository taskRegistryRepository;
	
	@Autowired
	SchedulerService schedulerService;
	
	@Autowired
	AlertService alertService;
	
	@Override
	public TaskInfo addTaskInfo(TaskInfo taskInfo) {
		TaskInfo add = taskInfoRepository.save(taskInfo);
		schedulerService.scheduleJob(add);
		return add;
	}
	
	@Override
	public TaskRegistry addTaskRegistry(TaskRegistry taskRegistry) {
		return taskRegistryRepository.save(taskRegistry);
	}
	
	@Override
	public boolean delTaskRegistryById(Integer id) {
		try {
			taskRegistryRepository.delete(id);
		}catch(Exception e) {
			
			return false;
		}
		return true;
	}
	
	@Override
	public boolean delTaskInfoById(Integer id) {
		try {
			TaskInfo taskInfo = getTaskInfoById(id);
			taskInfoRepository.delete(taskInfo.getId());
			alertService.delByInfo(taskInfo.getId());
			schedulerService.delete(taskInfo);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean delTaskInfoByBeanId(int beanId,PlatformType group) {
		try {
			Specification<TaskInfo> spec = SpecificationUtil.<TaskInfo>and().ep("group", group).ep("beanId", beanId).build();
			List<TaskInfo> taskInfos = taskInfoRepository.findAll(spec);
			Iterator<TaskInfo> its = taskInfos.iterator();
			while(its.hasNext()) {
				TaskInfo taskInfo = its.next();
				delTaskInfoById(taskInfo.getId());
			}
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	@Override
	public boolean updTaskRegistry(TaskRegistry taskRegistry) {
		try {
			TaskRegistry upd = getTaskRegistryById(taskRegistry.getId());
			if(null != upd) {
				upd.setTaskName(taskRegistry.getTaskName());
				upd.setServiceName(taskRegistry.getServiceName());
				upd.setDescription(taskRegistry.getDescription());
				taskRegistryRepository.save(upd);
				return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean updTaskInfoSchedule(TaskInfo taskInfo) {
		try {
			TaskInfo upd = updTaskInfoN(taskInfo);
			schedulerService.edit(upd);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean updTaskInfo(TaskInfo taskInfo) {
		try {
			TaskInfo upds = getTaskInfoById(taskInfo.getId());
			if(null != upds) {
				upds.setName(taskInfo.getName());
				upds.setFrequency(taskInfo.getFrequency());
				upds.setLastRuntime(taskInfo.getLastRuntime());
				upds.setCost(taskInfo.getCost());
				upds.setReceivers(taskInfo.getReceivers());
				upds.setThreshold(taskInfo.getThreshold());
				upds.setResult(taskInfo.getResult());
				taskInfoRepository.save(upds);
				return true;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		return false;
	}
	
	@Override
	public TaskInfo updTaskInfoN(TaskInfo taskInfo) {
		try {
			TaskInfo upds = getTaskInfoById(taskInfo.getId());
			if(null != upds) {
				upds.setName(taskInfo.getName());
				upds.setFrequency(taskInfo.getFrequency());
				upds.setLastRuntime(taskInfo.getLastRuntime());
				upds.setCost(taskInfo.getCost());
				upds.setReceivers(taskInfo.getReceivers());
				upds.setThreshold(taskInfo.getThreshold());
				upds.setResult(taskInfo.getResult());
				return taskInfoRepository.save(upds);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	@Override
	public TaskRegistry getTaskRegistryById(Integer id) {
		
		return taskRegistryRepository.findOne(id);
		
	}
	
	@Override
	public TaskRegistry getTaskRegistryByService(String serviceName) {
		Specification<TaskRegistry> spec = SpecificationUtil.<TaskRegistry>and().ep("serviceName", serviceName).build();
		return taskRegistryRepository.findOne(spec);
		
	}
	
	@Override
	public TaskInfo getTaskInfoById(Integer id) {
		
		return taskInfoRepository.findOne(id);
		
	}
	
	@Override
	public List<TaskInfo> listAllTaskInfo(String state){
		Specification<TaskInfo> specification = SpecificationUtil.<TaskInfo>and().ep("state", state).build();
		return (List<TaskInfo>) taskInfoRepository.findAll(specification);
	}
	
	@Override
	public Page<TaskInfo> listAllTaskInfo(String state,PlatformType group,int pageNum, int pageSize){
		Specification<TaskInfo> specification = SpecificationUtil.<TaskInfo>and().ep("state", state).ep("group", group).build();
		return taskInfoRepository.findAll(specification,new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public Page<TaskInfo> listAllTaskInfo(int beanId,String state,PlatformType group,int pageNum, int pageSize){
		Specification<TaskInfo> specification = SpecificationUtil.<TaskInfo>and().ep("state", state).ep("group", group).ep("beanId", beanId).build();
		return taskInfoRepository.findAll(specification,new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public List<TaskRegistry> listAllTaskRegistry(PlatformType group){
		Specification<TaskRegistry> specification = SpecificationUtil.<TaskRegistry>and().ep(group != PlatformType.ALL,"group", group).build();
		return taskRegistryRepository.findAll(specification);
	}
	
	
}
