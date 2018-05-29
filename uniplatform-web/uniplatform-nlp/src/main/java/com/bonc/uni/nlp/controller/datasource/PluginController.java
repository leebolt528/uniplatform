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
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.dao.plugin.PluginRepository;
import com.bonc.uni.nlp.entity.plugin.Plugin;
import com.bonc.uni.nlp.entity.plugin.PluginType;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.datasource.IPluginService;
import com.bonc.uni.nlp.utils.CurrentUserUtils;
import com.bonc.uni.nlp.utils.UserLoggerUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logger.Logger;
import com.bonc.usdp.odk.logger.entity.LogType;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @ClassName:PluginController
 * @Package:com.bonc.text.controller.admin
 * @Description:Plugin管理Controller
 * @author:xmy
 */

@Controller
@RequestMapping(value = "/nlap/admin/plugin")
public class PluginController {

	@Autowired
	IPluginService pluginService;
	@Autowired
	PluginRepository pluginPepository;

	@PostConstruct
	public void init() {
		pluginService.initPluginType();
	}

	/**
	 * 
	 * @param id
	 * @param name
	 *            名称
	 * @param className
	 *            全类名
	 * @param savePath
	 *            保存路径
	 * @param uploadTime
	 *            上传时间
	 * @param updateTime
	 *            最近修改时间
	 * @return
	 */
	@RequestMapping(value = "/add", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String addPlugin(String pluginName, @RequestParam("file") MultipartFile file, String classNames) {
		LogManager.Process("Process in controller: /nlap/admin/plugin/add");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "plugin添加成功");

		try {
			pluginService.addPlugin(pluginName, file, classNames);
		} catch (AdminException e) {
			LogManager.Exception(e);
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}
		LogManager.Process("Process out controller: /nlap/admin/plugin/add");
		return JSON.toJSONString(map);

	}

	@RequestMapping(value = "/notUsed", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String notUsed(String id) {
		LogManager.Process("Process in controller: /nlap/admin/plugin/notUsed");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("message", "该plugin没有被使用");

		try {
			pluginService.notUsed(id);
		} catch (AdminException e) {
			LogManager.Exception(e);
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}
		LogManager.Process("Process out controller: /nlap/admin/plugin/notUsed");
		return JSON.toJSONString(map);
	}

	@RequestMapping(value = "/edit", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String editPlugin(String pId, String pluginName,
			@RequestParam(value = "file", required = false) MultipartFile file, String classNames) {
		LogManager.Process("Process in controller: /nlap/admin/plugin/edit");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "plugin修改成功");

		try {
			pluginService.editPlugin(pId, pluginName, file, classNames);
		} catch (AdminException e) {
			Logger.logInfo(CurrentUserUtils.getInstance().getUser().getUserName(), LogType.OPERATION, "修改plugin失败",
					UserLoggerUtil.getCurrentTime());
			LogManager.Exception(e);
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}
		LogManager.Process("Process out controller: /nlap/admin/plugin/edit");
		return JSON.toJSONString(map);
	}

	@RequestMapping(value = "/deletes", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String deletePlugins(String ids) {
		LogManager.Process("Process in controller: /nlap/admin/plugin/deletes");
		Map<String, Object> map = new HashMap<>();
		if (StringUtil.isEmpty(ids)) {
			map.put("status", 200);
			map.put("msg", "Plugin删除成功");
			return JSON.toJSONString(map);
		}
		if (!pluginService.deletePlugins(ids)) {

			map.put("status", 400);
			map.put("msg", "Plugin启动状态不能删除失败");
		} else {
			map.put("status", 200);
			map.put("msg", "Plugin删除成功");
		}
		LogManager.Process("Process out controller: /nlap/admin/plugin/delets");
		return JSON.toJSONString(map);
	}

	@RequestMapping(value = "/query", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String queryPlugin(String pId) {
		LogManager.Process("Process in controller: /nlap/admin/plugin/query");
		Map<String, Object> map = new HashMap<>();
		Plugin result = pluginPepository.findOne(pId);
		map.put("status", 200);
		map.put("msg", "Plugin信息查询成功");
		map.put("result", result);
		LogManager.Process("Process out controller: /nlap/admin/plugin/query");
		return JSON.toJSONString(map);
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String listPlugin(String searchWord, int pageIndex, int pageSize, boolean ascSort) {
		LogManager.Process("Process in controller: /nlap/admin/plugin/list");
		Map<String, Object> map = new HashMap<>();
		List<Object> result = pluginService.listPlugin(searchWord, pageIndex, pageSize, ascSort);
		Long count = pluginService.countPlugin();
		map.put("count", count);
		map.put("status", 200);
		map.put("msg", "plugin列表信息查询成功");
		map.put("result", result);
		LogManager.Process("Process out controller: /nlap/admin/plugin/list");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/pluginType/list")
	@ResponseBody
	public String listPluginType() {
		LogManager.Process("Process in controller: /admin/dataSource/add");
		Map<String, Object> map = new HashMap<>();
		List<PluginType> result = pluginService.listPluginType();
		map.put("status", 200);
		map.put("msg", "plugin总数查询成功");
		map.put("result", result);

		LogManager.Process("Process in controller: /admin/dataSource/add");
		return JSON.toJSONString(map);

	}
}
