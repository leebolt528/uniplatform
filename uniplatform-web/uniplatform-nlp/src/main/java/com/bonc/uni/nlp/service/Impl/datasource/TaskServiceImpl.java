package com.bonc.uni.nlp.service.Impl.datasource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bonc.uni.nlp.dao.datasource.DataSourceRepository;
import com.bonc.uni.nlp.dao.plugin.PluginRepository;
import com.bonc.uni.nlp.dao.strategy.BusinessRepository;
import com.bonc.uni.nlp.dao.task.ProcessedDataOperationRepository;
import com.bonc.uni.nlp.dao.task.TaskExecuteTypeRepository;
import com.bonc.uni.nlp.dao.task.TaskRepository;
import com.bonc.uni.nlp.entity.datasource.DataSource;
import com.bonc.uni.nlp.entity.plugin.Plugin;
import com.bonc.uni.nlp.entity.strategy.Business;
import com.bonc.uni.nlp.entity.task.ProcessedDataOperation;
import com.bonc.uni.nlp.entity.task.Task;
import com.bonc.uni.nlp.entity.task.TaskExecuteType;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.datasource.ITaskService;
import com.bonc.usdp.odk.common.string.StringUtil;

/**
 * @ClassName:TaskServiceImpl
 * @Package:com.bonc.text.service.impl
 * @Description:TODO
 * @author:Gao Qiuyue
 * @date:2017年8月13日 下午3:53:57
 */
@Service
public class TaskServiceImpl implements ITaskService {

	@Autowired
	TaskRepository taskRepository;
	@Autowired
	TaskExecuteTypeRepository executeTypeRepository;
	@Autowired
	BusinessRepository businessRepository;
	@Autowired
	PluginRepository pluginRepository;
	@Autowired
	DataSourceRepository dsRepository;
	@Autowired
	ProcessedDataOperationRepository dataProcessRepository;
	@Autowired
	DataSourceRepository dataSourceRepository;

	@Override
	public void initTaskExecuteType() {
		List<TaskExecuteType> taskExecuteTypes = executeTypeRepository.findAll();
		if (null == taskExecuteTypes || 0 == taskExecuteTypes.size() || taskExecuteTypes.size() < 3) {
			TaskExecuteType executeType1 = new TaskExecuteType();
			executeType1.setName("realtime");
			executeType1.setDisplayName("实时");

			TaskExecuteType executeType2 = new TaskExecuteType();
			executeType2.setName("timing");
			executeType2.setDisplayName("定时");

			TaskExecuteType executeType3 = new TaskExecuteType();
			executeType3.setName("cycle");
			executeType3.setDisplayName("周期");

			List<TaskExecuteType> list = new ArrayList<>();
			list.add(executeType1);
			list.add(executeType2);
			list.add(executeType3);
			executeTypeRepository.save(list);
		}
	}

	@Override
	public void initProcessedDataOperation() {
		List<ProcessedDataOperation> dataProcessTypes = dataProcessRepository.findAll();
		if (null == dataProcessTypes || 0 == dataProcessTypes.size() || dataProcessTypes.size() < 4) {
			ProcessedDataOperation dataProcessType1 = new ProcessedDataOperation();
			dataProcessType1.setName("delete");
			dataProcessType1.setDisplayName("删除");

			ProcessedDataOperation dataProcessType2 = new ProcessedDataOperation();
			dataProcessType2.setName("move");
			dataProcessType2.setDisplayName("转移到指定目录");

			ProcessedDataOperation dataProcessType3 = new ProcessedDataOperation();
			dataProcessType3.setName("suffix");
			dataProcessType3.setDisplayName("修改后缀");

			ProcessedDataOperation dataProcessType4 = new ProcessedDataOperation();
			dataProcessType4.setName("redis");
			dataProcessType4.setDisplayName("缓存至redis");

			List<ProcessedDataOperation> list = new ArrayList<>();
			list.add(dataProcessType1);
			list.add(dataProcessType2);
			list.add(dataProcessType3);
			list.add(dataProcessType4);
			dataProcessRepository.save(list);
		}
	}

	@Override
	public List<Business> listBusiness() {
		List<Business> list = businessRepository.findAllByTime();
		return list;
	}

	@Override
	public List<Plugin> listPlugin() {
		List<Plugin> list = pluginRepository.findAllByTime();
		return list;
	}

	@Override
	public boolean addTask(String name, boolean predefined, String processedDataOperationId, String dataSourceId,
			String businessId, String pluginId, String executeTypeId, String executeTime) {
		Task task = new Task();
		TaskExecuteType taskExecuteType = executeTypeRepository.findOne(executeTypeId);
		String executeType = taskExecuteType.getName();
		if (StringUtil.isEmpty(name)) {
			throw new AdminException("添加失败，任务名称为空");
		} else if (taskRepository.findOneByName(name) != null) {
			throw new AdminException("添加失败，任务名称已经存在");
		} else if (!predefined && StringUtil.isEmpty(pluginId)) {
			throw new AdminException("添加失败，plugin不能为空");
		}
		if (!"realtime".equals(executeType) && StringUtil.isEmpty(executeTime)) {
			throw new AdminException("添加失败，执行时间不能为空");
		}
		if (StringUtil.isEmpty(processedDataOperationId)) {
			throw new AdminException("添加失败，原数据处理模式不能为空");
		}

		task.setName(name);
		task.setPredefined(predefined);

		task.setProcessedDataOperation(processedDataOperationId);
		DataSource dSource = dsRepository.findOne(dataSourceId);
		dSource.setStatus(1);
		dsRepository.save(dSource);
		task.setDataSource(dataSourceId);

		if (!StringUtil.isEmpty(businessId)) {
			Business business = businessRepository.findOne(businessId);
			business.setInUsing(1);
			businessRepository.save(business);
			task.setBusiness(businessId);
		}

		if (!StringUtil.isEmpty(pluginId)) {
			Plugin plugin = pluginRepository.findOne(pluginId);
			plugin.setStatus(1);
			pluginRepository.save(plugin);
			task.setPlugin(pluginId);
		}

		task.setExecuteType(executeTypeId);
		if (!StringUtil.isEmpty(executeTime)) {
			task.setExecuteTime(executeTime);
		}

		task.setCreateTime(new Date());
		task.setUpdateTime(new Date());

		taskRepository.save(task);

		return true;
	}

	@Override
	public boolean editTask(String id, String name, boolean predefined, String dataSourceId, String businessId,
			String pluginId) {
		Task task = taskRepository.findOne(id);
		if (null == task) {
			throw new AdminException("修改失败，该任务不存在");
		}
		if (StringUtil.isEmpty(name)) {
			throw new AdminException("修改失败，任务名称为空");
		} else if (!name.equals(task.getName())) {
			if (taskRepository.findOneByName(name) != null) {
				throw new AdminException("修改失败，任务名称已经存在");
			}
		}
		if (!predefined && StringUtil.isEmpty(pluginId)) {
			throw new AdminException("修改失败，plugin不能为空");
		}
		if (predefined && StringUtil.isEmpty(businessId)) {
			throw new AdminException("修改失败，业务策略不能为空");
		}
		String oldBusinessId = task.getBusiness();
		String oldDataSourceId = task.getDataSource();
		String oldPluginId = task.getPlugin();
		
		task.setPredefined(predefined);
		String oldName = task.getName();

		// 自定义
		if (!predefined) {
			// 修改之前为预定义，有business
			if (!StringUtil.isEmpty(oldBusinessId)) {
				Business business = businessRepository.findOne(oldBusinessId);
				// 如果该业务策略未被其他任务使用 状态改为0
				List<Task> tasks = taskRepository.findAllByBusiness(oldBusinessId);
				if (null == tasks|| tasks.isEmpty()) {
					business.setInUsing(0);
				}
				businessRepository.save(business);
			}
			task.setBusiness(null);
		}
		if (predefined) {
			task.setPlugin(null);
		}
		if (!StringUtil.isEmpty(oldBusinessId)) {
			Business oldBusiness = businessRepository.findOne(oldBusinessId);
			String oldBusinessName = oldBusiness.getName();
		}
		
		Plugin oldPlugin = new Plugin();
		String oldBPluginName = null;
		if (!StringUtil.isEmpty(oldPluginId)) {
			oldPlugin = pluginRepository.findOne(oldPluginId);
			oldBPluginName = oldPlugin.getName();
		}

		if (!name.equals(oldName)) {
			task.setName(name);
		}

		if (!dataSourceId.equals(oldDataSourceId)) {
			DataSource dSource = dsRepository.findOne(dataSourceId);
			dSource.setStatus(1);
			dsRepository.save(dSource);
			task.setDataSource(dataSourceId);
		}
		if (!StringUtil.isEmpty(businessId)) {
			if (!businessId.equals(oldBusinessId)) {
				Business business = businessRepository.findOne(businessId);
				business.setInUsing(1);
				businessRepository.save(business);
				task.setBusiness(businessId);
			}
		}
		if (!StringUtil.isEmpty(pluginId)) {
			if (!pluginId.equals(oldPluginId)) {
				Plugin plugin = pluginRepository.findOne(pluginId);
				task.setPlugin(plugin.getId());
				plugin.setStatus(1);
				pluginRepository.save(plugin);
				task.setPlugin(pluginId);
			}
		} else {
			if (!StringUtil.isEmpty(oldPluginId)) {
				task.setPlugin(null);
				task.setPlugin(pluginId);
			}
		}

		task.setCreateTime(new Date());
		task.setUpdateTime(new Date());

		taskRepository.save(task);

		List<Task> dataSourceTask = taskRepository.findAllByDataSource(oldDataSourceId);
		if (null == dataSourceTask|| dataSourceTask.isEmpty()) {
			DataSource dSource = dsRepository.findOne(oldDataSourceId);
			dSource.setStatus(0);
			dsRepository.save(dSource);
		}

		if (!StringUtil.isEmpty(oldBusinessId)) {
			List<Task> businessTask = taskRepository.findAllByBusiness(oldBusinessId);
			if (null == businessTask|| businessTask.isEmpty()) { 
				Business business = businessRepository.findOne(oldBusinessId);
				business.setInUsing(0);
				businessRepository.save(business);
			}
		}
		if (!StringUtil.isEmpty(oldPluginId)) {
			List<Task> pluginTask = taskRepository.findAllByPlugin(oldPluginId);
			if (null == pluginTask|| pluginTask.isEmpty()) {
				Plugin plugin = pluginRepository.findOne(oldPluginId);
				plugin.setStatus(0);
				pluginRepository.save(plugin);
			}
		}

		return true;

	}

	@Override
	public boolean deleteTask(String ids) {
		String[] idAll = ids.split(",");
		if (idAll == null || idAll.length < 1) {
			return false;
		}
		List<Task> taskDel = new ArrayList<>();
		List<Task> tasks = taskRepository.findAllByIdIn(idAll);
		for (Task task : tasks) {
			if (0 == task.getTaskStatus()) {
				taskDel.add(task);
			}
		}
		taskRepository.delete(taskDel);

		List<Business> businessList = new ArrayList<>();
		List<DataSource> dataSourceList = new ArrayList<>();
		List<Plugin> pluginList = new ArrayList<>();
		for (Task task : taskDel) {
			String businessId = task.getBusiness();
			if (!StringUtil.isEmpty(businessId)) {
				List<Task> businessTask = taskRepository.findAllByBusiness(businessId);
				if (businessTask == null || businessTask.isEmpty()) {
					Business business = businessRepository.findOne(businessId);
					business.setInUsing(0);
					businessList.add(business);
				}
			}

			String dataSourceId = task.getDataSource();
			List<Task> dataSourceTask = taskRepository.findAllByDataSource(dataSourceId);
			if (dataSourceTask == null || dataSourceTask.isEmpty()) {
				DataSource dSource = dsRepository.findOne(dataSourceId);
				dSource.setStatus(0);
				dataSourceList.add(dSource);

			}

			String pluginId = task.getPlugin();
			if (!StringUtil.isEmpty(pluginId)) {
				List<Task> pluginTask = taskRepository.findAllByPlugin(pluginId);
				if (pluginTask == null || pluginTask.isEmpty()) {
					Plugin plugin = pluginRepository.findOne(pluginId);
					plugin.setStatus(0);
					pluginList.add(plugin);

				}
			}
		}
		if (!businessList.isEmpty()) {
			businessRepository.save(businessList);
		}
		if (!dataSourceList.isEmpty()) {
			dsRepository.save(dataSourceList);
		}
		if (!pluginList.isEmpty()) {
			pluginRepository.save(pluginList);
		}

		return true;
	}

	@Override
	public Task queryTask(String id) {
		Task task = taskRepository.findOne(id);
		return task;
	}

	@Override
	public List<Map<String, Object>> listTasks(int pageNumber, int pageSize, boolean ascSort) {
		Sort sort = null;
		if (ascSort) {
			sort = new Sort(Sort.Direction.ASC, "createTime");
		} else {
			sort = new Sort(Sort.Direction.DESC, "createTime");
		}
		Pageable pageable = new PageRequest(pageNumber - 1, pageSize, sort);
		List<Map<String, Object>> taskList = new ArrayList<>();
		List<Task> tasks = taskRepository.findAll(pageable).getContent();
		for (Task task : tasks) {
			Map<String, Object> map = new HashMap<>();
			String businessId = task.getBusiness();
			if (null != businessId) {
				Business business = businessRepository.findOne(businessId);
				map.put("business", business);
			}else {
				map.put("business", "");
			}
			String dataSourceId = task.getDataSource();
			DataSource dataSource = dataSourceRepository.findOne(dataSourceId);
			map.put("dataSource", dataSource);
			String pluginId = task.getPlugin();
			if (!StringUtil.isEmpty(pluginId)) {
				Plugin plugin = pluginRepository.findOne(pluginId);
				map.put("plugin", plugin);
			} else {
				map.put("plugin", "");
			}
			String processedDataOperationId = task.getProcessedDataOperation();
			ProcessedDataOperation processedDataOperation = dataProcessRepository.findOne(processedDataOperationId);
			map.put("processedDataOperation", processedDataOperation);
			String executeTypeId = task.getExecuteType();
			TaskExecuteType taskExecuteType = executeTypeRepository.findOne(executeTypeId);
			map.put("taskExecuteType", taskExecuteType);
			map.put("task", task);
			taskList.add(map);
		}

		return taskList;
	}

	@Override
	public List<TaskExecuteType> listTaskExecuteType() {
		List<TaskExecuteType> list = executeTypeRepository.findAll();
		return list;
	}

	@Override
	public List<ProcessedDataOperation> listDataProcessType() {
		List<ProcessedDataOperation> list = dataProcessRepository.findAll();
		return list;
	}

	@Override
	public List<DataSource> listDataSource() {
		List<DataSource> list = dataSourceRepository.findAllByTime();
		return list;
	}

	@Override
	public Long countTask() {
		Long count = taskRepository.count();
		return count;
	}

	@Override
	public int executeTask(String taskId) {
		Task task = taskRepository.findOne(taskId);
		if (null == task) {
			throw new AdminException("执行失败，该任务不存在");
		}
		if (1 == task.getTaskStatus()) {
			task.setTaskStatus(0);
		}else {
			task.setTaskStatus(1);
		}
		
		taskRepository.save(task);

		return task.getTaskStatus();
	}

}
