package com.bonc.uni.nlp.controller.strategy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.strategy.IBusinessMgmtService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年11月10日 上午11:20:55
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/business/mgmt")
public class BusinessManagementController {

	@Autowired
	IBusinessMgmtService businessMgmtService;

	/**
	 * 业务策略节点列表
	 */
	@RequestMapping(value = "/node/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String listBusinessNode() {
		LogManager.Process("Process in : /nlap/admin/business/mgmt/node/list");
		Map<String, Object> nodeMap = new HashMap<>();
		nodeMap.put("status", 200);
		nodeMap.put("msg", "业务策略节点列表查询成功");
		Map<String, Object> businessNodes = businessMgmtService.listBusinessNode();
		nodeMap.put("businessNodes", businessNodes);

		LogManager.Process("Process out : /nlap/admin/business/mgmt/node/list");
		return JSON.toJSONString(nodeMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 功能策略节点列表
	 */
	@RequestMapping(value = "/strategy/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String listStrategies() {
		LogManager.Process("Process in : /nlap/admin/business/mgmt/strategy/list");
		List<Map<String, String>> listStrategies = businessMgmtService.listStrategies();
		Map<String, Object> strategiesMap = new HashMap<>();
		strategiesMap.put("status", 200);
		strategiesMap.put("msg", "功能策略列表查询成功");
		strategiesMap.put("listStrategies", listStrategies);

		LogManager.Process("Process out : /nlap/admin/business/mgmt/strategy/list");
		return JSON.toJSONString(strategiesMap, SerializerFeature.DisableCircularReferenceDetect);

	}

	/**
	 * 增加业务策略节点
	 */
	@RequestMapping(value = "/node/add", method = { RequestMethod.GET, RequestMethod.POST })
	public String addBusinessNode(String nodeName, String strategyIds) {
		LogManager.Process("Process in : /nlap/admin/business/mgmt/node/add");
		Map<String, Object> addNodeMap = new HashMap<>();
		addNodeMap.put("status", 200);
		addNodeMap.put("msg", "添加业务策略节点成功");
		try {
			businessMgmtService.addBusinessNode(nodeName, strategyIds);
		} catch (AdminException e) {
			addNodeMap.put("status", 400);
			addNodeMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/business/mgmt/node/add");
		return JSON.toJSONString(addNodeMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 删除业务策略节点
	 */
	@RequestMapping(value = "/node/del", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteBusinessNode(String nodesId) {
		LogManager.Process("Process in : /nlap/admin/business/mgmt/node/del");
		Map<String, Object> deleteNodeMap = new HashMap<>();
		try {
			deleteNodeMap.put("status", 200);
			deleteNodeMap.put("msg", "删除节点成功");
			int operation = businessMgmtService.deleteBusinessNode(nodesId);
			if (0 == operation) {
				deleteNodeMap.put("status", 400);
				deleteNodeMap.put("msg", "删除节点失败，该节点不存在");
			} else if (2 == operation) {
				deleteNodeMap.put("status", 400);
				deleteNodeMap.put("msg", "删除节点失败，该节点被业务策略使用中");
			}
		} catch (AdminException e) {
			LogManager.Exception("delete node Exception", e);
		}

		LogManager.Process("Process out : /nlap/admin/business/mgmt/node/del");
		return JSON.toJSONString(deleteNodeMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 业务策略列表
	 */
	@RequestMapping(value = "/business/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String listBusiness(String keyword, int pageIndex, int pageSize, boolean ascSort) {
		LogManager.Process("Process in : /nlap/admin/business/mgmt/business/list");
		Map<String, Object> businessesMap = new HashMap<>();
		businessesMap.put("status", 200);
		businessesMap.put("msg", "业务策略列表查询成功");
		Map<String, Object> businesses = businessMgmtService.listBusiness(keyword, pageIndex, pageSize, ascSort);
		businessesMap.put("businesses", businesses);

		LogManager.Process("Process out : /nlap/admin/business/mgmt/business/list");
		return JSON.toJSONString(businessesMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 增加业务策略
	 */
	@RequestMapping(value = "/business/add", method = { RequestMethod.GET, RequestMethod.POST })
	public String addBusiness(String businessName, String nodeIds) {
		LogManager.Process("Process in : /nlap/admin/business/mgmt/business/add");
		Map<String, Object> addBusinessesMap = new HashMap<>();
		addBusinessesMap.put("status", 200);
		addBusinessesMap.put("msg", "业务策略添加成功");
		try {
			businessMgmtService.addBusiness(businessName, nodeIds);
		} catch (Exception e) {
			addBusinessesMap.put("status", 400);
			addBusinessesMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/business/mgmt/business/add");
		return JSON.toJSONString(addBusinessesMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 修改业务策略
	 */
	@RequestMapping(value = "/business/edit", method = { RequestMethod.GET, RequestMethod.POST })
	public String editBusiness(String businessId, String newBusinessName, String newNodeIds) {
		LogManager.Process("Process in : /nlap/admin/business/mgmt/business/edit");
		System.out.println("newNodeIds" + newNodeIds);
		Map<String, Object> editBusinessesMap = new HashMap<>();
		editBusinessesMap.put("status", 200);
		editBusinessesMap.put("msg", "业务策略修改成功");
		try {
			businessMgmtService.editBusiness(businessId, newBusinessName, newNodeIds);
		} catch (AdminException e) {
			editBusinessesMap.put("status", 400);
			editBusinessesMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/business/mgmt/business/edit");
		return JSON.toJSONString(editBusinessesMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 删除业务策略
	 */
	@RequestMapping(value = "/business/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteBusinesses(String businessesId) {
		LogManager.Process("Process in : /nlap/admin/business/mgmt/business/delete");
		Map<String, Object> delBusinessesmap = new HashMap<>();
		delBusinessesmap.put("status", 200);
		delBusinessesmap.put("msg", "业务策略删除成功");
		try {
			businessMgmtService.deleteBusinesses(businessesId);
		} catch (AdminException e) {
			delBusinessesmap.put("status", 400);
			delBusinessesmap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/business/mgmt/business/delete");
		return JSON.toJSONString(delBusinessesmap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/businesses/export", method = { RequestMethod.GET, RequestMethod.POST })
	public synchronized String exportStrategies(String businessesId, HttpServletResponse response) {
		LogManager.Process("Process in : /nlap/admin/business/mgmt/businesses/export");
		OutputStream out = null;
		try {
			if (null == businessesId || 0 == businessesId.length()) {
				Map<String, Object> map = new HashMap<>();
				map.put("status", "400");
				map.put("msg", "请选择要导出的业务策略");
				return JSON.toJSONString(map);
			}

			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition",
					"attachment;filename=\"" + new String(("business.xml").getBytes("GBK"), "ISO8859_1") + "\"");
			out = response.getOutputStream();

			String content = businessMgmtService.exportBusinesses(businessesId);

			out.write(content.getBytes());

			LogManager.Debug("Export strategies.");
		} catch (IOException e) {
			LogManager.Exception(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LogManager.Exception(e);
				}
			}
		}
		LogManager.Process("Process out : /nlap/admin/business/mgmt/businesses/export");
		return null;
	}
	
	@RequestMapping(value = "/businesses/import", method = {RequestMethod.GET, RequestMethod.POST})
	public String importStrategies(MultipartFile[] files){
		LogManager.Process("Process in : /nlap/admin/business/mgmt/businesses/import");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "导入成功");
		try {
			businessMgmtService.importBusinesses(files);
		} catch (Exception e) {
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}
		
		LogManager.Process("Process out : /nlap/admin/business/mgmt/businesses/import");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/single/business/info", method = { RequestMethod.GET, RequestMethod.POST })
	public String businessInfo(String businessId) {
		LogManager.Process("Process in : /nlap/admin/business/mgmt/single/business/info");
		Map<String, Object> ruleMap = new HashMap<>();
		List<Map<String, String>> nodeInfoList = businessMgmtService.businessInfo(businessId);
		if (null == nodeInfoList) {
			ruleMap.put("status", 400);
			ruleMap.put("msg", "查询失败，该业务策略不存在");
		}
		ruleMap.put("status", 200);
		ruleMap.put("msg", "查询业务策略信息成功");
		ruleMap.put("nodeInfoList", nodeInfoList);

		LogManager.Process("Process out : /nlap/admin/business/mgmt/single/business/info");
		return JSON.toJSONString(ruleMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	
}
