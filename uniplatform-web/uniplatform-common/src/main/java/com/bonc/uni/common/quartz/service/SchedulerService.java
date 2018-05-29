package com.bonc.uni.common.quartz.service;

import com.bonc.uni.common.entity.TaskInfo;

/**
 * @author futao
 * 2017年8月30日
 */
public interface SchedulerService {

	void scheduleAllJob();
	void scheduleJob(TaskInfo info);
	void edit(TaskInfo info);
	void delete(String jobName, String jobGroup);
	void pause(String jobName, String jobGroup);
	void resume(String jobName, String jobGroup);
	public void delete(TaskInfo info);
}
