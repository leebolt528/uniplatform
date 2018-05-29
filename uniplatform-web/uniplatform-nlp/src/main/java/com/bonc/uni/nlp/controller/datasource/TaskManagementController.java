package com.bonc.uni.nlp.controller.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.entity.datasource.DataSource;
import com.bonc.uni.nlp.entity.plugin.Plugin;
import com.bonc.uni.nlp.entity.strategy.Business;
import com.bonc.uni.nlp.entity.task.ProcessedDataOperation;
import com.bonc.uni.nlp.entity.task.Task;
import com.bonc.uni.nlp.entity.task.TaskExecuteType;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.datasource.ITaskService;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @ClassName:TaskController
 * @Package:com.bonc.text.controller.admin
 * @Description:任务管理Controller
 * @author:xmy
 */

@Controller
@RequestMapping(value = "/nlap/admin/task")
public class TaskManagementController {
	@Autowired
	ITaskService taskService;

	@PostConstruct
	public void init() {
		taskService.initTaskExecuteType();
		taskService.initProcessedDataOperation();
	}

	/**
	 * taskType为预定义类型时候：查询和数据源绑定的业务策略
	 * 
	 * @return
	 */
	@RequestMapping(value = "/business", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String businessStrategy() {
		LogManager.Process("Process in controller: /nlap/admin/task/business");
		Map<String, Object> map = new HashMap<>();
		List<Business> businesses = taskService.listBusiness();
		map.put("status", 200);
		map.put("msg", "业务策略查询成功");
		map.put("result", businesses);
		LogManager.Process("Process out controller: /nlap/admin/task/business");
		return JSON.toJSONString(map);
	}

	/**
	 * taskType为自定义类型时候，查询plugin
	 * 
	 * @return
	 */
	@RequestMapping(value = "/plugin", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String pluginStrategy() {
		LogManager.Process("Process in controller: /nlap/admin/task/plugin");
		Map<String, Object> map = new HashMap<>();
		List<Plugin> plugins = taskService.listPlugin();
		map.put("status", 200);
		map.put("msg", "plugin查询成功");
		map.put("result", plugins);
		LogManager.Process("Process out controller: /nlap/admin/task/plugin");
		return JSON.toJSONString(map);

	}

	/**
	 * 
	 * @Title: addTask
	 * @Author:Gao Qiuyue
	 * @param id
	 * @param name
	 *            任务名称
	 * @param predefined
	 *            任务类型，分为：预定义（true）；自定义（false）
	 * @param dataSource
	 *            要处理的数据源
	 * @param business
	 *            和数据源绑定的业务策略(如果是预定义任务，则必须有，且必定不能有plugin)
	 * @param plugin
	 *            和数据源绑定的plugin(如果是自定义任务，则必须有)
	 * @param executeType
	 *            执行类型，分类：实时（realtime）、定时（timing）、周期（cycle）
	 * @param executeTime
	 *            执行时间
	 * @param pluginName
	 *            plugin名称
	 * @param createTime
	 *            创建时间
	 * @param updateTime
	 *            最近一次修改时间
	 * @return
	 */
	@RequestMapping(value = "/add", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String addTask(String taskName, String predefined, String processedDataOperationId, String dataSourceId,
			@RequestParam(value = "businessId", required = false) String businessId,
			@RequestParam(value = "pluginId", required = false) String pluginId, String executeType,
			@RequestParam(value = "executeTime", required = false) String executeTime) {
		LogManager.Process("Process in controller: /nlap/admin/task/add");

		boolean prede = "true".equals(predefined);
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "任务添加成功");

		try {
			taskService.addTask(taskName, prede, processedDataOperationId, dataSourceId, businessId, pluginId,
					executeType, executeTime);
		} catch (AdminException e) {
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}
		LogManager.Process("Process out controller: /nlap/admin/task/add");
		return JSON.toJSONString(map);
	}

	/**
	 * 修改任务
	 * 
	 * @param tID
	 * @param taskName
	 * @param executeType
	 * @param dataSourceName
	 * @param businessName
	 * @param pluginName
	 * @param executeTime
	 * @return
	 */
	@RequestMapping(value = "/edit", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String editTask(String tID, String taskName, boolean predefined, String dataSourceId,
			@RequestParam(value = "businessId", required = false) String businessId,
			@RequestParam(value = "pluginId", required = false) String pluginId) {
		LogManager.Process("Process in controller: /nlap/admin/task/edit");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "任务修改成功");

		try {
			taskService.editTask(tID, taskName, predefined, dataSourceId, businessId, pluginId);
		} catch (Exception e) {
			LogManager.Exception(e);
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}
		LogManager.Process("Process out controller: /nlap/admin/task/edit");
		return JSON.toJSONString(map);

	}

	@RequestMapping(value = "/deletes", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String deleteTask(String ids) {
		LogManager.Process("Process in controller: /nlap/admin/task/deletes");
		Map<String, Object> map = new HashMap<>();
		if (StringUtil.isEmpty(ids)) {
			map.put("status", 200);
			map.put("msg", "任务删除成功");
			return JSON.toJSONString(map);
		}
		if (!taskService.deleteTask(ids)) {
			map.put("status", 400);
			map.put("msg", "任务删除失败");
		} else {
			map.put("status", 200);
			map.put("msg", "任务删除成功");
		}
		LogManager.Process("Process out controller: /nlap/admin/task/deletes");
		return JSON.toJSONString(map);
	}

	@RequestMapping(value = "/query", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String queryTask(String tID) {
		LogManager.Process("Process in controller: /nlap/admin/task/query");
		Map<String, Object> map = new HashMap<>();
		Task result = taskService.queryTask(tID);
		map.put("status", 200);
		map.put("msg", "任务信息查询成功");
		map.put("result", result);
		LogManager.Process("Process out controller: /nlap/admin/task/query");
		return JSON.toJSONString(map);
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String listTasks(int pageNumber, int pageSize, boolean ascSort) {
		LogManager.Process("Process in controller: /nlap/admin/task/list");
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> result = new ArrayList<>();
		Long count = taskService.countTask();
		map.put("count", count);
		map.put("status", 200);
		map.put("msg", "任务信息列表查询成功");

		try {
			result = taskService.listTasks(pageNumber, pageSize, ascSort);
			map.put("result", result);
		} catch (AdminException e) {
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller: /nlap/admin/task/list");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/taskExecuteType/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String listTaskeExecuteType() {
		LogManager.Process("Process in controller: /nlap/admin/task/taskExecuteType/list");
		Map<String, Object> map = new HashMap<>();
		List<TaskExecuteType> executeTypes = taskService.listTaskExecuteType();
		map.put("status", 200);
		map.put("msg", "执行类型查询成功");
		map.put("result", executeTypes);
		LogManager.Process("Process out controller: /nlap/admin/task/taskExecuteType/list");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/dataProcessType/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String listDataProcessType() {
		LogManager.Process("Process in controller: /nlap/admin/task/dataProcessType/list");
		Map<String, Object> map = new HashMap<>();
		List<ProcessedDataOperation> types = taskService.listDataProcessType();
		map.put("status", 200);
		map.put("msg", "原数据处理方式");
		map.put("result", types);
		LogManager.Process("Process out controller: /nlap/admin/task/dataProcessType/list");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/dataSource/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String listDataSource() {
		LogManager.Process("Process in controller: /nlap/admin/task/dataSource/list");
		Map<String, Object> map = new HashMap<>();
		List<DataSource> sources = taskService.listDataSource();
		map.put("status", 200);
		map.put("msg", "数据源查询成功");
		map.put("result", sources);
		LogManager.Process("Process out controller: /nlap/admin/task/dataSource/list");
		return JSON.toJSONString(map);
	}

	@RequestMapping(value = "/execute", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String executeTask(String taskId) {
		LogManager.Process("Process in controller: /nlap/admin/task/execute");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		try {
			int status = taskService.executeTask(taskId);
			if (1 == status) {
				map.put("msg", "任务执行中");
			}else {
				map.put("msg", "任务停止");
			}
		} catch (Exception e) {
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}

		LogManager.Process("Process out controller: /nlap/admin/task/execute");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

}
