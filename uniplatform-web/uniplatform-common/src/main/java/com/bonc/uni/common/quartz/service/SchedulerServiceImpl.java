package com.bonc.uni.common.quartz.service;

import java.util.Iterator;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.uni.common.entity.TaskInfo;
import com.bonc.uni.common.entity.TaskRegistry;
import com.bonc.uni.common.exception.ServiceException;
import com.bonc.uni.common.service.TaskService;

/**
 * @author futao
 * 2017年8月30日
 */
@Service("schedulerService")
public class SchedulerServiceImpl implements SchedulerService{

	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	TaskService taskService;
	
	@Override
	public void scheduleAllJob() {
		List<TaskInfo> taskInfos = taskService.listAllTaskInfo("1");
		Iterator<TaskInfo> its = taskInfos.iterator();
		while(its.hasNext()) {
			TaskInfo taskInfo = its.next();
			scheduleJob(taskInfo);
		}
	}
	
	@Override
	public void scheduleJob(TaskInfo info) {
		
		try {
			String frequency = info.getFrequency();
			String infoId = String.valueOf(info.getId());
			TaskRegistry taskRegistry = taskService.getTaskRegistryById(info.getTaskId());
			String groupstr = taskRegistry.getGroup().name();
			if (checkExists(infoId, groupstr)) {
		        throw new ServiceException(String.format("Job已经存在, infoId:{%s},groupstr:{%s}", infoId, groupstr));
		    }
			
			TriggerKey triggerKey = TriggerKey.triggerKey(infoId, groupstr);
			JobKey jobKey = JobKey.jobKey(infoId, groupstr);
			
			/*CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime).withSchedule(schedBuilder).build();*/
			CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(frequency).withMisfireHandlingInstructionDoNothing();
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(schedBuilder).build();
		
			trigger.getJobDataMap().put(TaskInfo.class.getSimpleName(), info);
			trigger.getJobDataMap().put(TaskRegistry.class.getSimpleName(), taskRegistry);
			JobDetail jobDetail = JobBuilder.newJob(ProxyTaskService.class).withIdentity(jobKey).build();
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new ServiceException("类名不存在或执行表达式错误");
		}
	}
	
	@Override
	public void edit(TaskInfo info) {
		try {
			String frequency = info.getFrequency();
			String infoId = String.valueOf(info.getId());
			TaskRegistry taskRegistry = taskService.getTaskRegistryById(info.getTaskId());
			String groupstr = taskRegistry.getGroup().name();
			if (checkExists(infoId, groupstr)) {
				TriggerKey triggerKey = TriggerKey.triggerKey(infoId, groupstr);
		        //JobKey jobKey = new JobKey(infoId, groupstr);
		        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(frequency);
		        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
		        
		        //JobDetail jobDetail = scheduler.getJobDetail(jobKey);
		        /*HashSet<Trigger> triggerSet = new HashSet<>();
		    	triggerSet.add(cronTrigger);*/
		        
		    	//scheduler.scheduleJob(jobDetail, triggerSet, true);
		        trigger.getJobDataMap().put(TaskInfo.class.getSimpleName(), info);
				trigger.getJobDataMap().put(TaskRegistry.class.getSimpleName(), taskRegistry);
		    	scheduler.rescheduleJob(triggerKey, trigger);
		    }else {
		    	scheduleJob(info);
		    }
			
		} catch (SchedulerException e) {
			throw new ServiceException("类名不存在或执行表达式错误");
		}
	}
	
	@Override
	public void delete(TaskInfo info) {
		String infoId = String.valueOf(info.getId());
		TaskRegistry taskRegistry = taskService.getTaskRegistryById(info.getTaskId());
		String groupstr = taskRegistry.getGroup().name();
		delete(infoId,groupstr);
	}
	
	@Override
	public void delete(String infoId, String jobGroup){
		JobKey jobKey = new JobKey(infoId, jobGroup);
        try {
			if (checkExists(infoId, jobGroup)) {
			    scheduler.deleteJob(jobKey);
			}
		} catch (SchedulerException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void pause(String infoId, String jobGroup){
		TriggerKey triggerKey = TriggerKey.triggerKey(infoId, jobGroup);
		try {
			if (checkExists(infoId, jobGroup)) {
				scheduler.pauseTrigger(triggerKey);
			}
		} catch (SchedulerException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * 重新开始
	 * @param jobName
	 * @param jobGroup
	 */
	@Override
	public void resume(String infoId, String jobGroup){
		TriggerKey triggerKey = TriggerKey.triggerKey(infoId, jobGroup);
        
        try {
			if (checkExists(infoId, jobGroup)) {
				scheduler.resumeTrigger(triggerKey);
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	/**是否存在
	 * @param jobName
	 * @param jobGroup
	 * @return
	 * @throws SchedulerException
	 */
	private boolean checkExists(String infoId, String jobGroup) throws SchedulerException{
		TriggerKey triggerKey = TriggerKey.triggerKey(infoId, jobGroup);
		return scheduler.checkExists(triggerKey);
	}
	
	public boolean checkExists(TaskInfo info) {
		boolean success = true;
		try {
			if(null != info) {
				String infoId = String.valueOf(info.getId());
				TaskRegistry taskRegistry = taskService.getTaskRegistryById(info.getTaskId());
				String groupstr = taskRegistry.getGroup().name();
				checkExists(infoId,groupstr);
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}
}