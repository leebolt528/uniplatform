package com.bonc.uni.nlp.controller.datasource;

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
import com.bonc.uni.nlp.dao.datasource.DataSourceRepository;
import com.bonc.uni.nlp.dao.datasource.DataSourceTypeRepository;
import com.bonc.uni.nlp.dao.datasource.DataTypeRepository;
import com.bonc.uni.nlp.entity.datasource.DataSourceType;
import com.bonc.uni.nlp.entity.datasource.DataType;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.datasource.IDataSourceService;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @ClassName:DataSourceController
 * @Package:com.bonc.text.controller.admin
 * @Description:数据源管理
 * @author:xmy
 */

@Controller
@RequestMapping(value = "/nlap/admin/dataSource")
public class DataSourceController {

	@Autowired
	IDataSourceService dataSourceService;
	@Autowired
	DataSourceRepository dataSourceRepository;
	@Autowired
	DataSourceTypeRepository dataSourceTypeRepository;
	@Autowired
	DataTypeRepository dataTypeRepository;

	@PostConstruct
	public void init() {
		dataSourceService.initDataSourceType();
		dataSourceService.initDataType();
	}

	/**
	 * 
	 * @Title: addDataSource
	 * @Author:Gao Qiuyue
	 * @param id
	 * @param names
	 *            数据源名称
	 * @param ip
	 *            数据源连接ip
	 * @param port
	 *            数据源连接端口号
	 * @param username
	 *            数据源连接用户名
	 * @param password
	 *            数据源连接密码
	 * @param path
	 *            数据源地址
	 * @param dataSourceType
	 *            数据源类型
	 * @param dataType
	 *            数据源中的数据类型
	 * @param createTime
	 *            创建时间
	 * @param updateTime
	 *            最近一次修改时间
	 * @return
	 */
	@RequestMapping(value = "/add", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String addDataSource(String name, String ip, String port, String username, String password, String path,
			String dataSourceType, String dataType) {
		LogManager.Process("Process in controller: /admin/dataSource/add");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "数据源添加成功");

		try {
			dataSourceService.addDataSource(name, ip, port, username, password, path, dataSourceType, dataType);
		} catch (AdminException e) {
			LogManager.Exception(e);
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}
		LogManager.Process("Process out controller: /admin/dataSource/add");
		return JSON.toJSONString(map);
	}

	@RequestMapping(value = "/edit", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String editDataSource(String id, String name, String ip, String port, String username, String password,
			String path, String dataSourceType, String dataType) {
		LogManager.Process("Process in controller: /admin/dataSource/edit");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "数据源修改成功");

		try {
			dataSourceService.editDataSource(id, name, ip, port, username, password, path, dataSourceType, dataType);
		} catch (AdminException e) {
			LogManager.Exception(e);
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}
		LogManager.Process("Process out controller: /admin/dataSource/edit");
		return JSON.toJSONString(map);

	}


	@RequestMapping(value = "/deletes", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String deleteDataSources(String ids) {
		LogManager.Process("Process in controller: /admin/dataSource/deletes");
		Map<String, Object> map = new HashMap<>();
		if (StringUtil.isEmpty(ids)) {
			map.put("status", 200);
			map.put("msg", "数据源删除成功");
			return JSON.toJSONString(map);
		}
		if (!dataSourceService.deleteDataSource(ids)) {
			map.put("status", 400);
			map.put("msg", "数据源删除失败数据源状态为启动");
		} else {
			map.put("status", 200);
			map.put("msg", "数据源删除成功");
		}
		LogManager.Process("Process out controller: /admin/dataSource/delete");
		return JSON.toJSONString(map);
	}

	/**
	 * 搜索 查询所有数据源信息
	 * 
	 * @return
	 * @Title: searchDataSourceInfo
	 * @Author:Gao Qiuyue
	 */
	@RequestMapping(value = "/dataList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String listDataSource(@RequestParam(value = "searchWord", required = false) String searchWord, int pageIndex,
			int pageSize, boolean ascSort) {
		LogManager.Process("Process in controller: /admin/dataSource/dataList");
		Map<String, Object> map = new HashMap<>();
		List< Map<String, Object>> result = dataSourceService.listDataSources(searchWord, pageIndex, pageSize, ascSort);
		Long count = dataSourceService.countDataSource();
		map.put("count", count);
		map.put("status", 200);
		map.put("msg", "数据源列表信息查询成功");
		map.put("result", result);
		LogManager.Process("Process out controller: /admin/dataSource/dataList");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 查询表中数据源类型DataSourceType
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dsType/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String listDataSourceType() {
		LogManager.Process("Process in controller: /admin/dataSource/dsType/list");
		Map<String, Object> map = new HashMap<>();
		List<DataSourceType> list = dataSourceTypeRepository.findAll();
		map.put("status", 200);
		map.put("msg", "获取数据源类型成功");
		map.put("result", list);
		LogManager.Process("Process out controller: /admin/dataSource/dsType/list");
		return JSON.toJSONString(map);
	}

	/**
	 * 查询表中数据源内的数据类型DataType
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dataType/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String listDataType() {
		LogManager.Process("Process in controller: /admin/dataSource/dataType/list");
		Map<String, Object> map = new HashMap<>();
		List<DataType> list = dataTypeRepository.findAll();
		map.put("status", 200);
		map.put("msg", "获取数据源内的数据类型成功");
		map.put("result", list);
		LogManager.Process("Process out controller: /admin/dataSource/dataType/list");
		return JSON.toJSONString(map);
	}


}
