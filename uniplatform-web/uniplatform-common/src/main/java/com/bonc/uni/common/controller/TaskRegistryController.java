package com.bonc.uni.common.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.common.entity.TaskRegistry;
import com.bonc.uni.common.service.TaskService;
import com.bonc.uni.common.util.PlatformType;
import com.bonc.uni.common.util.ResultUtil;

/**
 * 任务注册
 * @author futao
 * 2017年9月7日
 */
@RestController
@RequestMapping("/common/task/registry")
public class TaskRegistryController {

	@Autowired
	TaskService taskService;
	
	@RequestMapping("/list")
	public String list(@RequestParam(value = "group", required = false, defaultValue = "ALL") String group) {
		PlatformType platformType = PlatformType.valueOf(group);
		List<TaskRegistry> lists = taskService.listAllTaskRegistry(platformType);
		return ResultUtil.success("请求成功", lists);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("taskRegistry")TaskRegistry taskRegistry) {
		taskRegistry.setGroup(PlatformType.USOU);
		taskRegistry.setTaskName("es集群监控");
		taskRegistry.setServiceName("colonyService");
		taskRegistry.setDescription("描述...............");
		TaskRegistry save = taskService.addTaskRegistry(taskRegistry);
		if (null == save) {
			return ResultUtil.addError();
		} else {
			return ResultUtil.addSuccess();
		}
	}
	
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable int id) {
		boolean del = taskService.delTaskRegistryById(id);
		if(del) {
			return ResultUtil.delSuccess();
		}else {
			return ResultUtil.delError();
		}
	}
	
	
}
