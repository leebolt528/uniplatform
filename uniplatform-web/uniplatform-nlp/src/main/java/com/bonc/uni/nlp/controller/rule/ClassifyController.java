package com.bonc.uni.nlp.controller.rule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.rule.IClassifyManagementService;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @Title:分类体系Controller
 * @author zlq
 *
 */
@RestController
@RequestMapping(value="/nlap/admin/classifyMgmt")
public class ClassifyController {
	
	@Autowired
	IClassifyManagementService classifyMgmtService;
	
	/**
	 * 初始化所有分类体系
	 * @return
	 */
	@RequestMapping(value="/initClassify", method = {RequestMethod.GET, RequestMethod.POST})
	public String initClassify() {
		LogManager.Process("Process in controller: /nlap/admin/classifyMgmt/initClassify");
		Map<String, Object> rsMap = new HashMap<>();
		List<Map<String, Object>> classifies = classifyMgmtService.findAllClassify();
		rsMap.put("msg", "初始化成功");
		rsMap.put("status", "200");
		rsMap.put("classifies", classifies);
		LogManager.Process("Process out controller: /nlap/admin/classifyMgmt/initClassify");
        return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 创建分类体系
	 * @return
	 */
	@RequestMapping(value="/addClassify", method = {RequestMethod.GET, RequestMethod.POST})
	public String addClassify(@RequestParam(value="classifyName", required=true) String classifyName,
			@RequestParam(value="preClassifyAndObject", required=true) String preClassifyAndObject,
			@RequestParam(value="objects", required=true) String objects) {
		LogManager.Process("Process in controller: /nlap/admin/classifyMgmt/addClassify");
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("msg", "添加成功！");
		rsMap.put("status", "200");
		if (null == classifyName || "".equals(classifyName)) {
			rsMap.put("msg", "添加失败！体系名称不能为空！");
			rsMap.put("status", "400");
		}
		String treatedName = classifyName.trim();
		try {
			boolean result = classifyMgmtService.addClassify(treatedName, preClassifyAndObject, objects);
		} catch (AdminException e) {
			rsMap.put("msg", e.getMessage());
			rsMap.put("status", "400");
		}
		LogManager.Process("Process out controller: /nlap/admin/classifyMgmt/addClassify");
        return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 删除分类体系
	 * @param classifyId
	 * @return
	 */
	@RequestMapping(value="/deleteClassify", method = {RequestMethod.GET, RequestMethod.POST})
	public String deleteClassify(@RequestParam(value="classifyId", required=true) String classifyId) {
		LogManager.Process("Process in controller: /nlap/admin/classifyMgmt/deleteClassify");
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("msg", "删除成功！");
		rsMap.put("status", "200");
		try {
			if (!StringUtil.isEmpty(classifyId)) {
				String[] classifyIds = classifyId.split(",");
				for (String id : classifyIds) {
					classifyMgmtService.deleteClassify(id);
				}
			}
		} catch (AdminException e) {
			rsMap.put("msg", e.getMessage());
			rsMap.put("status", "400");
		}
		LogManager.Process("Process out controller: /nlap/admin/classifyMgmt/deleteClassify");
        return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 编辑分类体系
	 * @param classifyId
	 * @param classifyName
	 * @param addedObjects
	 * @param deletedObjects
	 * @param updateObjects
	 * @param newDependences
	 * @return
	 */
	@RequestMapping(value="/editClassify", method = {RequestMethod.GET, RequestMethod.POST})
	public String editClassify(@RequestParam(value="classifyId", required=true) String classifyId,
			@RequestParam(value="classifyName", required=false) String classifyName,
			@RequestParam(value="addedObjects", required=false) String addedObjects,
			@RequestParam(value="deletedObjects", required=false) String deletedObjects,
			@RequestParam(value="updateObjects", required=false) String updateObjects,
			@RequestParam(value="newDependences", required=false) String newDependences) {
		LogManager.Process("Process in controller: /nlap/admin/classifyMgmt/editClassify");
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("msg", "修改成功！");
		rsMap.put("status", "200");
		boolean result = true;
		try {
			if (StringUtil.isEmpty(classifyName)) {
				rsMap.put("msg", "分类体系名称不能为空！");
				rsMap.put("status", "400");
				LogManager.Process("Process out controller: /nlap/admin/classifyMgmt/editClassify");
		        return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
			}
			String treatedClassifyName = classifyName.trim();
			Boolean existSameClassify = classifyMgmtService.existSameClassify(classifyId, treatedClassifyName);
			if (null != classifyName && !existSameClassify) {
				result = classifyMgmtService.updateClassifyName(classifyId, treatedClassifyName);
			}
			if (null != deletedObjects && !StringUtil.isEmpty(deletedObjects) && result) {
				classifyMgmtService.deleteObjects(classifyId, deletedObjects);
			}
			if (null != updateObjects && !StringUtil.isEmpty(updateObjects) && result) {
				classifyMgmtService.updateObjects(classifyId, updateObjects);
			}
			if (null != addedObjects && !StringUtil.isEmpty(addedObjects) && result) {
				classifyMgmtService.addObjects(classifyId, addedObjects);
			}
			if (null != newDependences && result) {
				classifyMgmtService.updateDependences(classifyId, newDependences);
			}
		} catch (Exception e) {
			rsMap.put("msg", e.getMessage());
			rsMap.put("status", "400");
		}
		
		LogManager.Process("Process out controller: /nlap/admin/classifyMgmt/editClassify");
        return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 所有分类体系
	 * @return
	 */
	@RequestMapping(value="/classifyInfo", method = {RequestMethod.GET, RequestMethod.POST})
	public String classifyInfo(@RequestParam(value="pageIndex", required=true) Integer pageIndex,
			@RequestParam(value="pageSize", required=true) Integer pageSize) {
		LogManager.Process("Process in controller: /nlap/admin/classifyMgmt/classifyInfo");
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("msg", "生成树成功！");
		rsMap.put("status", "200");
		List<Map<String, Object>>  classies = classifyMgmtService.allClassifyInfo();
 		List<Map<String, Object>> pagedClassifies = new ArrayList<>();
    	if(pageIndex == null || pageIndex < 1) {
    		pageIndex = 1;
    	}
    	int totalNumber = classies.size();
    	int totalPage;
    	if (0 == totalNumber) {
    		totalPage = 0;
    		pagedClassifies = classies;
		} else {
	    	totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : (totalNumber / pageSize) + 1;
	    	pagedClassifies = classies.subList(pageSize * (pageIndex - 1), 
	    			((pageSize * pageIndex) > totalNumber ? totalNumber : (pageSize * pageIndex)));
		}
	    rsMap.put("totalPage", totalPage);
	    rsMap.put("totalNumber", totalNumber);
	    rsMap.put("prePage", pageIndex - 1);
	    rsMap.put("curPage", pageIndex);
	    rsMap.put("nextPage", pageIndex + 1);
	    rsMap.put("firstPage", "1");
	    rsMap.put("lastPage", totalPage);
		rsMap.put("msg", "查询成功！");
		rsMap.put("status", "200");
		rsMap.put("data", pagedClassifies);
		LogManager.Process("Process out controller: /nlap/admin/classifyMgmt/classifyInfo");
        return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 生成树
	 * @param classifyId
	 * @return
	 */
	@RequestMapping(value="/generateTree", method = {RequestMethod.GET, RequestMethod.POST})
	public String generateTree(@RequestParam(value="classifyId", required=true) String classifyId) {
		LogManager.Process("Process in controller: /nlap/admin/classifyMgmt/generateTree");
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("msg", "生成树成功！");
		rsMap.put("status", "200");
		rsMap.put("tree", classifyMgmtService.generateTree(classifyId));
		LogManager.Process("Process out controller: /nlap/admin/classifyMgmt/generateTree");
        return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 搜索分类体系
	 * @param classifyId
	 * @return
	 */
	@RequestMapping(value="/searchClassify", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchClassify(@RequestParam(value="keyWord", required=true) String keyWord,
			@RequestParam(value="pageIndex", required=true) Integer pageIndex,
			@RequestParam(value="pageSize", required=true) Integer pageSize) {
		LogManager.Process("Process in controller: /nlap/admin/classifyMgmt/searchClassify");
		Map<String, Object> rsMap = new HashMap<>();
		List<Map<String, Object>> rsList = new ArrayList<>();
 		List<Map<String, Object>> pagedClassifies = new ArrayList<>();
		rsMap.put("msg", "搜索成功！");
		rsMap.put("status", "200");
		if (null == keyWord || "".equals(keyWord)) {
			rsList = classifyMgmtService.allClassifyInfo();
		} else {
			rsList = classifyMgmtService.searchByKeyWord(keyWord.trim());
		}
    	if(pageIndex == null || pageIndex < 1) {
    		pageIndex = 1;
    	}
    	int totalNumber = rsList.size();
    	int totalPage;
    	if (0 == totalNumber) {
    		totalPage = 0;
    		pagedClassifies = rsList;
		} else {
	    	totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : (totalNumber / pageSize) + 1;
	    	pagedClassifies = rsList.subList(pageSize * (pageIndex - 1), 
	    			((pageSize * pageIndex) > totalNumber ? totalNumber : (pageSize * pageIndex)));
		}
	    rsMap.put("totalPage", totalPage);
	    rsMap.put("totalNumber", totalNumber);
	    rsMap.put("prePage", pageIndex - 1);
	    rsMap.put("curPage", pageIndex);
	    rsMap.put("nextPage", pageIndex + 1);
	    rsMap.put("firstPage", "1");
	    rsMap.put("lastPage", totalPage);
		rsMap.put("data", pagedClassifies);
		LogManager.Process("Process out controller: /nlap/admin/classifyMgmt/searchClassify");
        return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 导出分类体系到.xml文件
	 * @param classifyId
	 * @return
	 */
	@RequestMapping(value="/exportToXML", method = {RequestMethod.GET, RequestMethod.POST})
	public String exportClassify2XML(@RequestParam(value="classifyId", required=true) String classifyIds,
			HttpServletResponse response) {
		LogManager.Process("Process in controller: /nlap/admin/classifyMgmt/exportClassify2XML");
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("msg", "导出成功！");
		rsMap.put("status", "200");
		String[] classifyIdArr =  classifyIds.split(",");
		if(null == classifyIds || 0 == classifyIdArr.length){
			Map<String, Object> map = new HashMap<>();
			map.put("status", "400");
			map.put("msg", "请选择要导出的策略!");
			return JSON.toJSONString(map);
		}
		classifyMgmtService.exportClassify2XML(classifyIdArr, response);
		LogManager.Process("Process out controller: /nlap/admin/classifyMgmt/exportClassify2XML");
        return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 导入XML文件到分类体系
	 * @param classifyIds
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/importXML", method = {RequestMethod.GET, RequestMethod.POST})
	public String importXML(@RequestParam(value="file", required=true) MultipartFile file) {
		LogManager.Process("Process in controller: /nlap/admin/classifyMgmt/importXML");
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("msg", "导入成功！");
		rsMap.put("status", "200");
		try {
			if (classifyMgmtService.importXML(file)) {
				rsMap.put("msg", "导入的分类体系已经存在！");
			}
		} catch (Exception e) {
			rsMap.put("msg", e.getMessage());
			rsMap.put("status", "400");
		} finally {
			try {
				file.getInputStream().close();
			} catch (IOException e) {
				LogManager.Exception(e);
			}
		}
		LogManager.Process("Process out controller: /nlap/admin/classifyMgmt/importXML");
        return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
}
