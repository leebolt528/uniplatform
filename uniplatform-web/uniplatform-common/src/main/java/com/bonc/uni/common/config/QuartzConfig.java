package com.bonc.uni.common.config;

import java.io.IOException;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * 定时任务quartz
 * @author futao
 * 2017年8月29日
 */
@Configuration
public class QuartzConfig {

	@Bean
	public Scheduler scheduler() throws IOException, SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		/*JobDetail jobDetail = JobBuilder.newJob(SimpleDemoJob.class)
			      .withIdentity("job1", "group1")
			      .build();

			  // Trigger the job to run now, and then repeat every 40 seconds
			  Trigger trigger = TriggerBuilder.newTrigger()
			      .withIdentity("trigger1", "group1")
			      .startNow()
			            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
			              .withIntervalInSeconds(40)
			              .repeatForever())            
			      .build();*/
		
		scheduler.start();
		//scheduler.scheduleJob(jobDetail, trigger);
		return scheduler;
	}
}
