package com.bonc.uni.usou.controller;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.common.entity.TaskInfo;
import com.bonc.uni.common.entity.TaskRegistry;
import com.bonc.uni.common.service.TaskService;
import com.bonc.uni.common.util.PlatformType;
import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.common.util.TaskCronExpBuilder;

/**
 * 监控
 * 
 * @author futao 2017年9月4日
 */
@RestController
@RequestMapping("/usou/monitor")
public class MonitorController {

	@Autowired
	TaskService taskService;

	/**
	 * 列表
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/taskInfo/list")
	public String listTaskInfo(
			@RequestParam(value = "cluster", required = true) int cluster,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		Page<TaskInfo> taskInfos = taskService.listAllTaskInfo(cluster,"1", PlatformType.USOU, pageNum, pageSize);
		return ResultUtil.success("请求成功", taskInfos);
	}

	/**
	 * 添加预警
	 * 
	 * @param taskInfo
	 * @return
	 */
	@RequestMapping(value = "/taskInfo/save", method = RequestMethod.POST)
	public String saveTaskInfo(@ModelAttribute("taskInfo") TaskInfo taskInfo) {
		TaskRegistry taskRegistry = taskService.getTaskRegistryByService("colonyService");
		if(null == taskRegistry) {
			return ResultUtil.error("请注册集群服务", null);
		}
		taskInfo.setTaskId(taskRegistry.getId());
		taskInfo.setGroup(PlatformType.USOU);
		taskInfo.setState("1");
		if(StringUtils.isBlank(taskInfo.getFrequency())) {
			return ResultUtil.error("请输入任务频率", null);
		}
		JSONObject json = JSONObject.parseObject(taskInfo.getFrequency());
		if(null == json) {
			return ResultUtil.error("输入频率格式不正确", null);
		}
		int hour = json.getInteger("hour");
		int min = json.getInteger("min");
		taskInfo.setFrequency(TaskCronExpBuilder.everyHourPer(hour, min));
		/*taskInfo.setReceivers("{\"email\":[{\"user\":\"futao\",\"value\":\"futao@bonc.com.cn\"}]}");
		taskInfo.setBeanId(22);
		taskInfo.setThreshold("[{\"name\":\"cpu\",\"operator\":\">=\",\"value\":\"20\"},{\"name\":\"mem\",\"operator\":\">=\",\"value\":\"20\"},{\"name\":\"health\",\"operator\":\">=\",\"value\":\"2\"},{\"name\":\"fs\",\"operator\":\">=\",\"value\":\"90\"},{\"name\":\"num\",\"operator\":\">=\",\"value\":\"3\"}]");
		taskInfo.setName("taskInfo");
		taskInfo.setFrequency("0 0/1 * * * ?");*/
		TaskInfo save = taskService.addTaskInfo(taskInfo);
		if (null == save) {
			return ResultUtil.addError();
		} else {
			return ResultUtil.addSuccess();
		}
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/taskInfo/delete/{id}")
	public String delTaskInfo(@PathVariable int id) {
		boolean del = taskService.delTaskInfoById(id);
		if (true == del) {
			return ResultUtil.delSuccess();
		} else {
			return ResultUtil.delError();
		}
	}
	
	/**修改
	 * @param taskInfo
	 * @return
	 */
	@RequestMapping(value="/taskInfo/update", method= RequestMethod.POST)
	public String updTaskInfo(@ModelAttribute("taskInfo") TaskInfo taskInfo) {
		if(StringUtils.isBlank(taskInfo.getFrequency())) {
			return ResultUtil.error("请输入任务频率", null);
		}
		JSONObject json = JSONObject.parseObject(taskInfo.getFrequency());
		if(null == json) {
			return ResultUtil.error("输入频率格式不正确", null);
		}
		int hour = json.getInteger("hour");
		int min = json.getInteger("min");
		taskInfo.setFrequency(TaskCronExpBuilder.everyHourPer(hour, min));
		boolean upd = taskService.updTaskInfoSchedule(taskInfo);
		if (false == upd) {
			return ResultUtil.updError();
		} else {
			return ResultUtil.updSuccess();
		}
	}
	
	/**
	 * 获取
	 * @param id
	 * @return
	 */
	@RequestMapping("/taskInfo/get/{id}")
	public String getTaskInfo(@PathVariable int id) {
		TaskInfo taskInfo = taskService.getTaskInfoById(id);
		if (null == taskInfo) {
			return ResultUtil.getError();
		} else {
			return ResultUtil.getSuccess(taskInfo);
		}
	}

}
