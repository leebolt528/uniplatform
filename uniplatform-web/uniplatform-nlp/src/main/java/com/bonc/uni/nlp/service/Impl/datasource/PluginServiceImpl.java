package com.bonc.uni.nlp.service.Impl.datasource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.dao.plugin.PluginRepository;
import com.bonc.uni.nlp.dao.plugin.PluginTypeRepository;
import com.bonc.uni.nlp.dao.task.TaskRepository;
import com.bonc.uni.nlp.entity.plugin.Plugin;
import com.bonc.uni.nlp.entity.plugin.PluginType;
import com.bonc.uni.nlp.entity.task.Task;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.datasource.IPluginService;
import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.common.file.FileUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @ClassName:PluginServiceImpl
 * @Package:com.bonc.text.service.impl
 * @Description:TODO
 * @author:xmy
 * @date:2017年8月13日 上午11:18:02
 */
@Service
public class PluginServiceImpl implements IPluginService {

	@Autowired
	PluginRepository pluginRepository;
	@Autowired
	PluginTypeRepository pluginTypeRepository;
	@Autowired
	TaskRepository taskRepository;

	private static String PLUGIN_SAVE_PATH;

	static {
		try {
			PLUGIN_SAVE_PATH = PathUtil.getResourcesPath() + File.separator + "plugin";
			File pluginDir = new File(PLUGIN_SAVE_PATH);
			if (!pluginDir.exists()) {
				pluginDir.mkdirs();
			}
		} catch (PathNotFoundException e) {
			LogManager.Exception(e);
		}
	}

	@Override
	public void initPluginType() {
		List<PluginType> pluginTypes = pluginTypeRepository.findAll();
		if (null == pluginTypes || 0 == pluginTypes.size() || pluginTypes.size() < 2) {
			PluginType pluginType1 = new PluginType();
			pluginType1.setName("jobClient");
			PluginType pluginType2 = new PluginType();
			pluginType2.setName("taskTracker");

			pluginTypeRepository.save(pluginType1);
			pluginTypeRepository.save(pluginType2);
		}
	}

	@Override
	public synchronized boolean addPlugin(String name, MultipartFile file, String classNames) {
		if (StringUtil.isEmpty(name) || StringUtil.isEmpty(classNames)) {
			throw new AdminException("添加失败，名称和类名不能为空");
		}

		if (pluginRepository.findOneByName(name) != null) {
			throw new AdminException("添加失败，该名称plugin已存在");
		}

		if (null == file) {
			throw new AdminException("添加失败，请上传plugin文件");
		}

		Plugin plugin = new Plugin();
		/**
		 * 解析classMap
		 */
		String[] classNamesArray = classNames.split(";");

		if (null == classNamesArray || classNamesArray.length == 0) {
			throw new AdminException("添加失败，className不能为空");
		}

		Map<String, String> classMap = new HashMap<>();
		for (String className : classNamesArray) {
			String[] terms = className.split(":");
			if (terms.length != 2) {
				throw new AdminException("添加失败，className格式错误");
			}
			classMap.put(terms[0], terms[1]);
		}
		plugin.setClassMap(classMap);

		/**
		 * 解析上传文件
		 */
		String fileName = file.getOriginalFilename();

		if (StringUtil.isEmpty(fileName)) {
			throw new AdminException("添加失败，文件不能为空");
		}

		/**
		 * 创建名为name的文件夹，存放文件fileName
		 */
		String path = PLUGIN_SAVE_PATH + File.separator + name;
		File pathDir = new File(path);
		if (!pathDir.exists()) {
			pathDir.mkdirs();
		}
		String savePath = path + File.separator + fileName;
		File saveFile = new File(savePath);
		if (saveFile.exists()) {
			throw new AdminException("添加失败，该文件已存在");
		}
		try (FileOutputStream out = new FileOutputStream(saveFile)) {
			byte[] datas = file.getBytes();
			out.write(datas);
		} catch (IOException e) {
			LogManager.Exception(e);
		}

		plugin.setName(name);
		plugin.setSavePath(savePath);
		plugin.setFileName(fileName);
		Date now = new Date();
		plugin.setUploadTime(now);
		plugin.setUpdateTime(now);
		plugin.setStatus(0);

		pluginRepository.save(plugin);

		return true;
	}

	@Override
	public boolean editPlugin(String id, String name, MultipartFile file, String classNames) {
		Plugin plugin = pluginRepository.findOne(id);
		if (plugin.getStatus()>0) {
			throw new AdminException("修改失败，plugin状态为启动");
		}
		if (StringUtil.isEmpty(classNames)) {
			throw new AdminException("修改失败，className不能为空");
		}

		/**
		 * 解析classMap
		 */
		String[] classNamesArray = classNames.split(";");

		if (null == classNamesArray || classNamesArray.length == 0) {
			throw new AdminException("修改失败，className不能为空");
		}

		Map<String, String> classMap = new HashMap<>();
		for (String className : classNamesArray) {
			String[] terms = className.split(":");
			if (terms.length != 2) {
				throw new AdminException("添加失败，className格式错误");
			}
			classMap.put(terms[0], terms[1]);
		}
		plugin.setClassMap(classMap);

		/**
		 * 解析文件
		 */
		File saveFile;
		String savePath;
		String fileName = null;

		if (file != null) {
			// 删除原文件
			String oldPath = plugin.getSavePath();
			File oldFile = new File(oldPath);
			if (oldFile.isFile() && oldFile.exists()) {
				oldFile.delete();
			}

			// 保存新文件
			fileName = file.getOriginalFilename();
			savePath = PLUGIN_SAVE_PATH + File.separator + name + File.separator + fileName;
			saveFile = new File(savePath);
			try (FileOutputStream out = new FileOutputStream(saveFile)) {
				byte[] datas = file.getBytes();
				out.write(datas);
			} catch (IOException e) {
				LogManager.Exception(e);
			}
			plugin.setSavePath(savePath);
			
			plugin.setFileName(fileName);
		}

		plugin.setName(name);
		plugin.setUploadTime(new Date());
		plugin.setUpdateTime(new Date());

		pluginRepository.save(plugin);

		return true;
	}



	@Override
	public boolean deletePlugins(String ids) {
		String[] idAll = ids.split(",");
		if (idAll == null || idAll.length < 1) {
			return false;
		}
		List<Plugin> pluginDel = new ArrayList<>();
		List<Plugin> plugins = pluginRepository.findAllByIdIn(idAll);
		for (Plugin plugin : plugins) {
			if (0 == plugin.getStatus()) {
				pluginDel.add(plugin);
			}
		}
		for (Plugin plugin : pluginDel) {
			pluginRepository.delete(plugin);
			String path = PLUGIN_SAVE_PATH + File.separator + plugin.getName();
			File pathDir = new File(path);
			if (!pathDir.exists()) {
				pathDir.mkdirs();
			}
			FileUtil.deleteDirectory(path);
		}
		
		return true;
	}

	@Override
	public List<Object> listPlugin(String searchWord, int pageNumber, int pageSize, boolean ascSort) {
		Sort sort = null;
		if (ascSort) {
			sort = new Sort(Sort.Direction.ASC, "updateTime");
		} else {
			sort = new Sort(Sort.Direction.DESC, "updateTime");
		}
		Pageable pageable = new PageRequest(pageNumber - 1, pageSize, sort);
		List<Plugin> plugins = new ArrayList<>();
		if (StringUtil.isEmpty(searchWord)) {
			plugins = pluginRepository.findAll(pageable).getContent();
		} else {
			searchWord = "%" + searchWord + "%";
			plugins = pluginRepository.findAllByNameLike(searchWord, pageable);
		}
		// 获取使用该plugin的任务
		List<Object> pluginList = new ArrayList<>();
		Map<String, Object> pluginInfo = new HashMap<>();
		for (Plugin plugin : plugins) {
			List<String> taskNames = new ArrayList<>();
			pluginInfo = new HashMap<>();
			if (1 == plugin.getStatus()) {
				List<Task> tasks = taskRepository.findAllByPlugin(plugin.getId());
				for (Task task : tasks) {
					taskNames.add(task.getName());
				}
			}
			pluginInfo.put("plugin", plugin);
			pluginInfo.put("tasks", taskNames);
			pluginList.add(pluginInfo);
		}
		return pluginList;
	}

	@Override
	public Long countPlugin() {
		Long count = pluginRepository.count();
		return count;
	}

	@Override
	public List<PluginType> listPluginType() {
		List<PluginType> listType = pluginTypeRepository.findAll();
		return listType;
	}

	/**
	 * 判断plugin是否被task使用
	 */
	@Override
	public boolean notUsed(String id) {
		
		if (!taskRepository.findAllByPlugin(id).isEmpty()) {
			throw new AdminException("该plugin已被task使用");
		}
		return true;
	}

}
