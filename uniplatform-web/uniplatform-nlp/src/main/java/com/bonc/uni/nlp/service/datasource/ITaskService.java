package com.bonc.uni.nlp.service.datasource;

import java.util.List;
import java.util.Map;

import com.bonc.uni.nlp.entity.datasource.DataSource;
import com.bonc.uni.nlp.entity.plugin.Plugin;
import com.bonc.uni.nlp.entity.strategy.Business;
import com.bonc.uni.nlp.entity.task.ProcessedDataOperation;
import com.bonc.uni.nlp.entity.task.Task;
import com.bonc.uni.nlp.entity.task.TaskExecuteType;

/**
 * @ClassName:TaskService
 * @Package:com.bonc.text.service
 * @Description:TODO
 * @author:Gao Qiuyue
 * @date:2017年8月13日 下午3:53:36
 */
public interface ITaskService {

	void initTaskExecuteType();

	void initProcessedDataOperation();

	List<Business> listBusiness();

	List<Plugin> listPlugin();

	boolean addTask(String name, boolean predefined, String processedDataOperationId, String dataSourceName,
			String businessName, String pluginName, String executeType, String executeTime);

	boolean deleteTask(String ids);

	Task queryTask(String id);

	/**
	 * 搜索或者获取所有所有任务
	 * 
	 * @return
	 */
	List< Map<String, Object>> listTasks(int pageNumber, int pageSize, boolean ascSort);

	/**
	 * 获取所有任务类型
	 * 
	 * @return
	 */
	List<TaskExecuteType> listTaskExecuteType();

	/**
	 * 获取所有原数据处理模式
	 * 
	 * @return
	 */
	List<ProcessedDataOperation> listDataProcessType();

	/**
	 * 获取任务总数
	 * 
	 * @return
	 */
	Long countTask();

	/**
	 * 获取所有数据源
	 * 
	 * @return
	 */
	List<DataSource> listDataSource();

	/**
	 * 修改任务
	 * 
	 * @param id
	 *            任务Id
	 * @param name
	 *            任务新名称
	 *@param  predefined 预定义（true）；自定义（false）
	 * 
	 * @param dataSourceName
	 *            数据源名称
	 * @param businessName
	 *            任务策略名称
	 * @param pluginName
	 *            plugin名称
	 * @return
	 */

	boolean editTask(String id, String name, boolean predefined, String dataSourceId, String businessId,
			String pluginId);

	/**
	 * @param taskId
	 * @return 
	 */
	int executeTask(String taskId);

}
