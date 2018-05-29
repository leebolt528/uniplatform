package com.bonc.uni.common.quartz.service;

import com.bonc.uni.common.entity.TaskInfo;

/**
 * 具体执行接口
 * @author futao
 * 2017年8月30日
 */
public interface JobService {
	
	String EXECUTE_SUCCESS = "success";
	String EXECUTE_FAILURE = "fail";
	String LOW = "LOW";
	String MEDIUM = "MEDIUM";
	String HIGH = "HIGH";

	String execute(TaskInfo taskInfo);
}
