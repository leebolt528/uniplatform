package com.bonc.uni.common.quartz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.uni.common.entity.Alert;
import com.bonc.uni.common.entity.TaskInfo;
import com.bonc.uni.common.service.AlertService;
import com.bonc.uni.common.util.PlatformType;

@Service("jobService1")
public class JobService1 implements JobService{

	@Autowired
	AlertService alertService;
	
	@Override
	public String execute(TaskInfo taskInfo) {
		System.out.println("TaskService1..........."+taskInfo.getId());
		try {
			Thread.sleep(1000);
			
			Alert alert = new Alert();
			alert.setInfoName(taskInfo.getName());
			alert.setGroup(PlatformType.DCCI);
			alert.setInfoId(taskInfo.getId());
			alert.setLevel("1");
			alert.setMessage(taskInfo.getDescription());
			alert.setReceiver(taskInfo.getReceivers());
			alertService.saveAndSend(alert);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return EXECUTE_SUCCESS;
	}

}
 