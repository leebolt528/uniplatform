package com.bonc.uni.common.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.bonc.uni.common.entity.TaskInfo;
import com.bonc.uni.common.entity.TaskRegistry;
import com.bonc.uni.common.util.PlatformType;

/**
 * TaskInfo接口
 * @author futao
 * 2017年8月30日
 */
public interface TaskService {

	/**
	 * @param taskInfo
	 */
	TaskInfo addTaskInfo(TaskInfo taskInfo);
	/**
	 * @param taskRegistry
	 */
	TaskRegistry addTaskRegistry(TaskRegistry taskRegistry);
	/**
	 * @param id
	 * @return
	 */
	boolean delTaskRegistryById(Integer id);
	/**
	 * @param id
	 * @return
	 */
	boolean delTaskInfoById(Integer id);
	/**
	 * @param id
	 * @return
	 */
	TaskRegistry getTaskRegistryById(Integer id);
	/**
	 * @param id
	 * @return
	 */
	TaskInfo getTaskInfoById(Integer id);
	
	/**
	 * 根据状态获取 
	 * @return
	 */
	List<TaskInfo> listAllTaskInfo(String state);
	
	
	/**
	 * 翻页
	 * @param state
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<TaskInfo> listAllTaskInfo(String state,PlatformType group,int pageNum, int pageSize);
	
	/**
	 * @param taskRegistry
	 * @return
	 */
	boolean updTaskRegistry(TaskRegistry taskRegistry);
	
	/**
	 * @param taskInfo
	 * @return
	 */
	boolean updTaskInfo(TaskInfo taskInfo);
	
	/**
	 * 列出TaskRegistry
	 * @param group
	 * @return
	 */
	List<TaskRegistry> listAllTaskRegistry(PlatformType group);
	
	/**
	 * 修改schedule
	 * @param taskInfo
	 * @return
	 */
	boolean updTaskInfoSchedule(TaskInfo taskInfo);
	
	
	/**
	 * 使用serviceName  获取对象
	 * @param serviceName
	 * @return
	 */
	TaskRegistry getTaskRegistryByService(String serviceName);
	
	/**获取列表
	 * @param beanId
	 * @param state
	 * @param group
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<TaskInfo> listAllTaskInfo(int beanId,String state,PlatformType group,int pageNum, int pageSize);
	
	/**修改，如果失败 回返 null
	 * @param taskInfo
	 * @return
	 */
	TaskInfo updTaskInfoN(TaskInfo taskInfo);
	
	/**
	 * 根据beanId  进行删除
	 * @param beanId
	 * @param group
	 * @return
	 */
	boolean delTaskInfoByBeanId(int beanId,PlatformType group);
	
}
