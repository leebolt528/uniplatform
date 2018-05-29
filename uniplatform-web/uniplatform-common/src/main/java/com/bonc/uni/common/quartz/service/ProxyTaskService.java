package com.bonc.uni.common.quartz.service;

import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bonc.uni.common.entity.TaskInfo;
import com.bonc.uni.common.entity.TaskRegistry;
import com.bonc.uni.common.service.TaskService;
import com.bonc.uni.common.util.SpringContextUtil;

/**
 * 代理任务
 * @author futao
 * 2017年8月30日
 */
public class ProxyTaskService implements Job{
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		long start = System.currentTimeMillis();
		
		TaskInfo taskInfo = (TaskInfo) context.getTrigger().getJobDataMap().get(TaskInfo.class.getSimpleName());
		TaskRegistry taskRegistry = (TaskRegistry) context.getTrigger().getJobDataMap().get(TaskRegistry.class.getSimpleName());
		JobService jobService = SpringContextUtil.getBean(taskRegistry.getServiceName());
		String result = jobService.execute(taskInfo);
		long cost = System.currentTimeMillis() - start;
		saveRunResult(taskInfo,cost,result);
	}
	
	private void saveRunResult(TaskInfo taskInfo , long cost, String result) {
		try {
			TaskService taskService = SpringContextUtil.getBean("taskService");
			TaskInfo taskInfoN = taskService.getTaskInfoById(taskInfo.getId());
			if(null != taskInfoN) {
				taskInfoN.setCost(cost);
				taskInfoN.setResult(result);
				taskInfoN.setLastRuntime(Calendar.getInstance().getTimeInMillis());
				taskService.updTaskInfo(taskInfoN);
			}
			System.out.println("result................."+cost+result);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
