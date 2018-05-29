package com.bonc.uni.common.quartz.service;


import com.bonc.uni.common.util.SpringContextUtil;

/**
 * 初始化定时任务
 * @author futao
 * 2017年8月30日
 */
public class SchedulerInit {
	
	public static void InitScheduler() {
		SchedulerService schedulerService = SpringContextUtil.getBean("schedulerService");
		schedulerService.scheduleAllJob();
	}
}
