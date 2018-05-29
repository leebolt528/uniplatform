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
import com.bonc.uni.nlp.dao.datasource.DataSourceTypeRepository;
import com.bonc.uni.nlp.dao.datasource.DataTypeRepository;
import com.bonc.uni.nlp.dao.task.TaskRepository;
import com.bonc.uni.nlp.entity.datasource.DataSource;
import com.bonc.uni.nlp.entity.datasource.DataSourceType;
import com.bonc.uni.nlp.entity.datasource.DataType;
import com.bonc.uni.nlp.entity.task.Task;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.datasource.IDataSourceService;
import com.bonc.usdp.odk.common.string.StringUtil;

/**
 * @ClassName:DataSourceServiceImpl
 * @Package:com.bonc.text.service.impl
 * @Description:TODO
 * @author:xmy
 */
@Service
public class DataSourceServiceImpl implements IDataSourceService {

	@Autowired
	DataSourceRepository dataSourceRepository;
	@Autowired
	DataSourceTypeRepository dataSourceTypeRepository;
	@Autowired
	DataTypeRepository dataTypeRepository;
	@Autowired
	TaskRepository taskRepository;

	@Override
	public boolean addDataSource(String name, String ip, String port, String username, String password, String path,
			String dataSourceType, String dataType) {

		DataSource ds = new DataSource();
		if (StringUtil.isEmpty(name)) {
			throw new AdminException("添加失败，名称不能为空");
		} else if (dataSourceRepository.findOneByName(name) != null) {
			throw new AdminException("添加失败，该名称已经存在");
		}

		ds.setName(name);
		ds.setIp(ip);
		ds.setPort(port);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setPath(path);
		ds.setDataSourceType(dataSourceType);
		ds.setDataType(dataType);
		ds.setCreateTime(new Date());
		ds.setUpdateTime(new Date());
		ds.setStatus(0);

		dataSourceRepository.save(ds);

		return true;
	}

	@Override
	public boolean editDataSource(String id, String name, String ip, String port, String username, String password,
			String path, String dataSourceType, String dataType) {
		DataSource dS = dataSourceRepository.findOne(id);
		String oldName = dS.getName();
		Integer status = dS.getStatus();
		if (status > 0) {
			throw new AdminException("修改失败，数据源状态为启动");
		}
		if (StringUtil.isEmpty(name)) {
			throw new AdminException("修改失败，名称不能为空");
		}
		if (!name.equals(oldName)) {
			if (dataSourceRepository.findOneByName(name) != null) {
				throw new AdminException("修改失败，该名称已经存在");
			}
			dS.setName(name);
		}

		dS.setIp(ip);
		dS.setPort(port);
		dS.setUsername(username);
		dS.setPassword(password);
		dS.setPath(path);
		dS.setDataSourceType(dataSourceType);
		dS.setDataType(dataType);
		dS.setCreateTime(new Date());
		dS.setUpdateTime(new Date());

		dataSourceRepository.save(dS);

		return true;
	}

	@Override
	public boolean deleteDataSource(String ids) {
		String[] idAll = ids.split(",");
		if (idAll == null || idAll.length < 1) {
			return false;
		}
		List<String> delIds = new ArrayList<>();
		for (String id : idAll) {
			DataSource dataSource = dataSourceRepository.findOne(id);
			if (0 == dataSource.getStatus()) {
				delIds.add(id);
			}
		}
		for (String delId : delIds) {
			DataSource dataSource = dataSourceRepository.findOne(delId);
			dataSourceRepository.delete(dataSource);
		}
		
		return true;
	}

	@Override
	public List<Map<String,Object>> listDataSources(String searchWord, int pageNumber, int pageSize, boolean ascSort) {
		Sort sort = null;
		if (ascSort) {
			sort = new Sort(Sort.Direction.ASC, "createTime");
		} else {
			sort = new Sort(Sort.Direction.DESC, "createTime");
		}
		List<Map<String,Object>> dataSourceslist =new ArrayList<>();
		Pageable pageable = new PageRequest(pageNumber - 1, pageSize, sort);
		List<DataSource> sources = new ArrayList<>();
		if (StringUtil.isEmpty(searchWord)) {
			sources = dataSourceRepository.findAll(pageable).getContent();
			for (DataSource dataSource : sources) {
				
				Map<String,Object> dataSourcesmap =new HashMap<>();
				
				String dataSourceTypeId = dataSource.getDataSourceType();
				DataSourceType dataSourceType = dataSourceTypeRepository.findOne(dataSourceTypeId);
				dataSourcesmap.put("dataSourceType", dataSourceType);
				
				String dataTypeId = dataSource.getDataType();
				DataType dataType = dataTypeRepository.findOne(dataTypeId);
				dataSourcesmap.put("dataType", dataType);
				
				dataSourcesmap.put("dataSource", dataSource);
				// 获取使用该数据源的任务
				List<String> taskNames = new ArrayList<>();
				if (1 == dataSource.getStatus()) {
					List<Task> tasks = taskRepository.findAllByDataSource(dataSource.getId());
					for (Task task : tasks) {
						taskNames.add(task.getName());
					}
				}
				dataSourcesmap.put("tasks", taskNames);
				dataSourceslist.add(dataSourcesmap);
			}
		} else {
			searchWord = "%" + searchWord + "%";
			sources = dataSourceRepository.findAllByNameLike(searchWord, pageable);
			for (DataSource dataSource : sources) {
				Map<String,Object> dataSourcesmap =new HashMap<>();
				String dataSourceTypeId = dataSource.getDataSourceType();
				DataSourceType dataSourceType = dataSourceTypeRepository.findOne(dataSourceTypeId);
				dataSourcesmap.put("dataSourceType", dataSourceType);
				String dataTypeId = dataSource.getDataType();
				DataType dataType = dataTypeRepository.findOne(dataTypeId);
				dataSourcesmap.put("dataType", dataType);
				
				dataSourcesmap.put("dataSource", dataSource);
				// 获取使用该数据源的任务
				List<String> taskNames = new ArrayList<>();
				if (1 == dataSource.getStatus()) {
					List<Task> tasks = taskRepository.findAllByDataSource(dataSource.getId());
					for (Task task : tasks) {
						taskNames.add(task.getName());
					}
				}
				dataSourcesmap.put("tasks", taskNames);
				dataSourceslist.add(dataSourcesmap);
			}
		}
		return dataSourceslist;

	}

	@Override
	public void initDataSourceType() {
		List<DataSourceType> types = dataSourceTypeRepository.findAll();
		if (null == types || 0 == types.size() || types.size() < 3) {
			DataSourceType dataSourceType1 = new DataSourceType();
			dataSourceType1.setName("SystemFile");
			dataSourceType1.setDisplayName("系统文件");

			DataSourceType dataSourceType2 = new DataSourceType();
			dataSourceType2.setName("ES");
			dataSourceType2.setDisplayName("ES数据库");

			DataSourceType dataSourceType3 = new DataSourceType();
			dataSourceType3.setName("HDFS");
			dataSourceType3.setDisplayName("HDFS");
			List<DataSourceType> list = new ArrayList<>();
			list.add(dataSourceType1);
			list.add(dataSourceType2);
			list.add(dataSourceType3);

			dataSourceTypeRepository.save(list);
		}

	}

	@Override
	public void initDataType() {
		List<DataType> types = dataTypeRepository.findAll();
		if (null == types || 0 == types.size() || types.size() < 3) {
			DataType dataType1 = new DataType();
			dataType1.setName("txt");
			dataType1.setDisplayName("txt");

			DataType dataType2 = new DataType();
			dataType2.setName("doc");
			dataType2.setDisplayName("doc");

			DataType dataType3 = new DataType();
			dataType3.setName("exel");
			dataType3.setDisplayName("exel");

			List<DataType> list = new ArrayList<>();
			list.add(dataType1);
			list.add(dataType2);
			list.add(dataType3);

			dataTypeRepository.save(list);
		}

	}

	@Override
	public Long countDataSource() {
		Long count = dataSourceRepository.count();
		return count;
	}

}
