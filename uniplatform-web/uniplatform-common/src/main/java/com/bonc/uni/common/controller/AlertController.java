package com.bonc.uni.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.common.entity.Alert;
import com.bonc.uni.common.service.AlertService;
import com.bonc.uni.common.util.PlatformType;
import com.bonc.uni.common.util.ResultUtil;

/**
 * 预警结果
 * @author futao
 * 2017年9月8日
 */
@RestController
@RequestMapping("/common/alert")
public class AlertController {

	@Autowired
	AlertService alertService;
	
	@RequestMapping("/list")
	public String list(
			@RequestParam(value = "group", required = true,defaultValue = "DCCI") String group,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		PlatformType type = PlatformType.valueOf(group);
		Page<Alert> pages = alertService.listByPage(type, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}
	
	@RequestMapping("/list/{id}")
	public String listById(
			@PathVariable int id,
			@RequestParam(value = "group", required = true) int group,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		PlatformType type = null;
		switch (group) { 
			case 1: type =  PlatformType.CORPUS;break;
			case 2: type =  PlatformType.DCCI;break;
			case 3: type =  PlatformType.NLP;break;
			case 4: type =  PlatformType.USOU;break;
		}
		Page<Alert> pages = alertService.listByPage(id,type, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}
}
