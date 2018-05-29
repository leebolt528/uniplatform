package com.bonc.uni.nlp.controller.rule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.entity.rule.RuleType;
import com.bonc.uni.nlp.service.rule.IRuleManagementService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @Title:规则管理
 * @author zlq
 *
 */
@RestController
@RequestMapping(value = "/nlap/admin/ruleMgmt")
public class RuleController {

	@Autowired
	IRuleManagementService ruleMgmtService;

	/**
	 * 初始化所有规则类型
	 * 
	 * @return
	 */
	@RequestMapping(value = "/initRuleType", method = { RequestMethod.GET, RequestMethod.POST })
	public String initRuleType() {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/initRuleType");

		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("msg", "初始化成功");
		rsMap.put("status", "200");
		List<RuleType> classifies = ruleMgmtService.findAllRuleClassify();
		List<Map<String, Object>> ruleClassify = new ArrayList<>();
		for (int i = 0; i < classifies.size(); i++) {
			RuleType classify = classifies.get(i);
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("displayName", classify.getDisplayName());
			tempMap.put("id", classify.getId());
			switch (i) {
			case 0:
				tempMap.put("boolean", true);
				break;
			default:
				tempMap.put("boolean", false);
				break;
			}
			ruleClassify.add(tempMap);
		}
		rsMap.put("ruleType", ruleClassify);

		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/initRuleType");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 根据规则分类过滤模板
	 * 
	 * @param dicTypeId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/filterTemplates", method = { RequestMethod.GET, RequestMethod.POST })
	public String filterTemplate(@RequestParam(value = "dicTypeId", required = false) String ruleTypeId,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/filterTemplate");

		Map<String, Object> rsMap = new HashMap<>();
		List<Map<String, Object>> templates = new ArrayList<>();
		List<Map<String, Object>> pagedTemplates = new ArrayList<>();
		if (null == ruleTypeId || "0".equals(ruleTypeId)) {
			templates = ruleMgmtService.findAllTemplates();
		} else {
			templates = ruleMgmtService.filterByRuleType(ruleTypeId);
		}
		if (pageIndex == null || pageIndex < 1) {
			pageIndex = 1;
		}
		int totalNumber = templates.size();
		int totalPage;
		if (0 == totalNumber) {
			totalPage = 0;
			pagedTemplates = templates;
		} else {
			totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : (totalNumber / pageSize) + 1;
			pagedTemplates = templates.subList(pageSize * (pageIndex - 1),
					((pageSize * pageIndex) > totalNumber ? totalNumber : (pageSize * pageIndex)));
		}
		rsMap.put("totalPage", totalPage);
		rsMap.put("totalNumber", totalNumber);
		rsMap.put("prePage", pageIndex - 1);
		rsMap.put("curPage", pageIndex);
		rsMap.put("nextPage", pageIndex + 1);
		rsMap.put("firstPage", "1");
		rsMap.put("lastPage", totalPage);
		rsMap.put("msg", "过滤成功");
		rsMap.put("status", "200");
		rsMap.put("templates", pagedTemplates);

		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/filterTemplate");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取对应模板下的所有节点
	 * 
	 * @param templateId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/listNodes", method = { RequestMethod.GET, RequestMethod.POST })
	public String listNodes(@RequestParam(value = "templateId", required = true) String templateId,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/listNodes");

		Map<String, Object> rsMap = new HashMap<>();
		List<Map<String, Object>> nodes = ruleMgmtService.findNodesByTemplate(templateId);
		List<Map<String, Object>> pagedNodes = new ArrayList<>();
		if (pageIndex == null || pageIndex < 1) {
			pageIndex = 1;
		}
		int totalNumber = nodes.size();
		int totalPage;
		if (0 == totalNumber) {
			totalPage = 0;
			pagedNodes = nodes;
		} else {
			totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : (totalNumber / pageSize) + 1;
			pagedNodes = nodes.subList(pageSize * (pageIndex - 1),
					((pageSize * pageIndex) > totalNumber ? totalNumber : (pageSize * pageIndex)));
		}
		rsMap.put("totalPage", totalPage);
		rsMap.put("totalNumber", totalNumber);
		rsMap.put("prePage", pageIndex - 1);
		rsMap.put("curPage", pageIndex);
		rsMap.put("nextPage", pageIndex + 1);
		rsMap.put("firstPage", "1");
		rsMap.put("lastPage", totalPage);
		rsMap.put("msg", "查询成功");
		rsMap.put("status", "200");
		rsMap.put("nodes", pagedNodes);

		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/listNodes");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取对应节点下的所有规则
	 * 
	 * @param nodeId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/listRules", method = { RequestMethod.GET, RequestMethod.POST })
	public String listRules(@RequestParam(value = "nodeId", required = true) String nodeId,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/listRules");
		Map<String, Object> rsMap = new HashMap<>();
		List<Map<String, Object>> rules = ruleMgmtService.findRulesByNode(nodeId);
		List<Map<String, Object>> pagedRule = new ArrayList<>();
		if (pageIndex == null || pageIndex < 1) {
			pageIndex = 1;
		}
		int totalNumber = rules.size();
		int totalPage;
		if (0 == totalNumber) {
			totalPage = 0;
			pagedRule = rules;
		} else {
			totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : (totalNumber / pageSize) + 1;
			pagedRule = rules.subList(pageSize * (pageIndex - 1),
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
		rsMap.put("rules", pagedRule);

		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/listRules");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 列出模板对应的所有规则
	 * 
	 * @param nodeId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/listRulesByTemplate", method = { RequestMethod.GET, RequestMethod.POST })
	public String listRulesByTemplate(@RequestParam(value = "templateId", required = true) String templateId,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/listRulesByTemplate");
		Map<String, Object> rsMap = new HashMap<>();
		List<Map<String, Object>> rules = ruleMgmtService.findRulesByTemplate(templateId);
		List<Map<String, Object>> pagedRule = new ArrayList<>();
		if (pageIndex == null || pageIndex < 1) {
			pageIndex = 1;
		}
		int totalPage;
		int totalNumber = rules.size();
		if (0 == totalNumber) {
			totalPage = 0;
			pagedRule = rules;
		} else {
			totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : (totalNumber / pageSize) + 1;
			pagedRule = rules.subList(pageSize * (pageIndex - 1),
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
		rsMap.put("rules", pagedRule);

		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/listRulesByTemplate");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 添加模板
	 * 
	 * @param templateName
	 * @param dicTypeId
	 * @return
	 */
	@RequestMapping(value = "/addTemplate", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String addTemplate(@RequestParam(value = "templateName", required = true) String templateName,
			@RequestParam(value = "dicTypeId", required = true) String ruleTypeId) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/addTemplate");

		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "添加成功！");
		String treatedTemplate = templateName.trim();
		boolean result = ruleMgmtService.addTemplate(ruleTypeId, treatedTemplate);
		if (!result) {
			rsMap.put("status", 400);
			rsMap.put("msg", "模板已经存在！");
		}
		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/addTemplate");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 添加节点
	 * 
	 * @param nodeName
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/addNode", method = { RequestMethod.GET, RequestMethod.POST })
	public String addNode(@RequestParam(value = "nodeName", required = true) String nodeName,
			@RequestParam(value = "templateId", required = true) String templateId) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/addNode");
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "添加成功！");
		String treatedNode = nodeName.trim();
		boolean result = ruleMgmtService.addNode(templateId, treatedNode);
		if (!result) {
			rsMap.put("status", 400);
			rsMap.put("msg", "节点已经存在！");
		}
		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/addNode");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 更新模板名称
	 * 
	 * @param templateName
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/updateTemplate", method = { RequestMethod.GET, RequestMethod.POST })
	public String updateTemplate(@RequestParam(value = "templateName", required = true) String templateName,
			@RequestParam(value = "templateId", required = true) String templateId,
			@RequestParam(value = "dicTypeId", required = true) String ruleTypeId) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/updateTemplate");
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "编辑成功！");
		String treatedTemplate = templateName.trim();
		boolean result = ruleMgmtService.updateTemplate(templateId, treatedTemplate, ruleTypeId);
		if (!result) {
			rsMap.put("status", 400);
			rsMap.put("msg", "模板已经存在！");
		}
		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/updateTemplate");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 更新节点
	 * 
	 * @param nodeId
	 * @param nodeName
	 * @return
	 */
	@RequestMapping(value = "/updateNode", method = { RequestMethod.GET, RequestMethod.POST })
	public String updateNode(@RequestParam(value = "nodeId", required = true) String nodeId,
			@RequestParam(value = "nodeName", required = true) String nodeName,
			@RequestParam(value = "templateId", required = true) String templateId) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/updateNode");
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "编辑成功！");
		String treatedNode = nodeName.trim();
		boolean result = ruleMgmtService.updateNode(nodeId, treatedNode, templateId);
		if (!result) {
			rsMap.put("status", 400);
			rsMap.put("msg", "节点已经存在！");
		}
		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/updateNode");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 更新规则
	 * 
	 * @param ruleId
	 * @param rule
	 * @return
	 */
	@RequestMapping(value = "/updateRule", method = { RequestMethod.GET, RequestMethod.POST })
	public String updateRule(@RequestParam(value = "ruleId", required = true) String ruleId,
			@RequestParam(value = "nodeId", required = true) String nodeId,
			@RequestParam(value = "rule", required = true) String rule) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/updateRule");

		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "修改成功！");
		String treatedRule = rule.trim();
		boolean result = ruleMgmtService.updateRule(nodeId, ruleId, treatedRule);
		if (!result) {
			rsMap.put("status", "400");
			rsMap.put("msg", "规则已经存在！");
		}

		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/updateRule");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 删除模板
	 * 
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/deleteTemplate", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteTemplate(@RequestParam(value = "templateId", required = true) String templateId) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/deleteTemplate");

		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "删除成功！");
		boolean result = ruleMgmtService.deleteTemplate(templateId);
		if (!result) {
			rsMap.put("status", "400");
			rsMap.put("msg", "模板不存在！");
		}

		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/deleteTemplate");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 删除节点
	 * 
	 * @param nodeId
	 * @return
	 */
	@RequestMapping(value = "/deleteNode", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteNode(@RequestParam(value = "nodeId", required = true) String nodeId) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/deleteNode");

		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "删除成功！");
		boolean result = ruleMgmtService.deleteNode(nodeId);
		System.out.println("okokokokokokok");
		if (!result) {
			rsMap.put("status", "400");
			rsMap.put("msg", "节点不存在！");
		}

		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/deleteNode");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 删除规则
	 * 
	 * @param ruleIds
	 * @return
	 */
	@RequestMapping(value = "/deleteRule", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteRule(@RequestParam(value = "ruleId", required = true) String ruleIds) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/deleteRule");

		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "删除成功！");
		boolean result = ruleMgmtService.deleteRule(ruleIds.trim());
		if (!result) {
			rsMap.put("status", "400");
			rsMap.put("msg", "规则数据不存在！");
		}

		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/deleteRule");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 添加规则
	 * 
	 * @param ruleIds
	 * @return
	 */
	@RequestMapping(value = "/addRule", method = { RequestMethod.GET, RequestMethod.POST })
	public String addRule(@RequestParam(value = "nodeId", required = true) String nodeId,
			@RequestParam(value = "rule", required = true) String rule) {
		LogManager.Process("Process in controller: /nlap/admin/ruleMgmt/addRule");

		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "添加成功！");
		String treatedRule = null;
		if (rule != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(rule);
			treatedRule = m.replaceAll("");
		}
		boolean result = ruleMgmtService.addRule(treatedRule.replace(" ", ""), nodeId);
		if (!result) {
			rsMap.put("status", "400");
			rsMap.put("msg", "规则已经存在！");
		}

		LogManager.Process("Process out controller: /nlap/admin/ruleMgmt/addRule");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 上传规则
	 * 
	 * @param file
	 * @param nodeId
	 * @return
	 */
	@RequestMapping(value = "/uploadRule", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String uploadRule(@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "templateId", required = true) String templateId) {
		LogManager.Process("Process in controller : /nlap/admin/ruleMgmt/uploadRule");
		Map<String, Object> rsMap = new HashMap<String, Object>();
		rsMap.put("status", "200");
		rsMap.put("msg", "上传成功");
		if (file.isEmpty() || (!file.getOriginalFilename().endsWith("xml"))) {
			rsMap.put("status", 400);
			rsMap.put("msg", "请上传 xml文件！");
			try {
				file.getInputStream().close();
			} catch (IOException e) {
				LogManager.Exception(e);
			}
			LogManager.Process("Process out controller : /nlap/admin/ruleMgmt/uploadRule");
			return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
		}
		ruleMgmtService.uploadRule(file, templateId);
		try {
			file.getInputStream().close();
		} catch (IOException e) {
			LogManager.Exception(e);
		}
		LogManager.Process("Process out controller : /nlap/admin/ruleMgmt/uploadRule");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);

	}

	/**
	 * 批量导出规则
	 * 
	 * @param ruleIds
	 * @param filePatn
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/export", method = { RequestMethod.GET, RequestMethod.POST })
	public String exportRule2XML(@RequestParam(value = "ruleIds", required = true) String ruleIds,
			@RequestParam(value = "templateId", required = true) String templateId, HttpServletResponse response) {
		LogManager.Process("Process in controller : /nlap/admin/ruleMgmt/exportRule");
		Map<String, Object> rsMap = new HashMap<String, Object>();
		rsMap.put("status", "200");
		rsMap.put("msg", "导出成功");
		boolean result = ruleMgmtService.exportRuleToXml(ruleIds, templateId, response);
		if (!result) {
			rsMap.put("status", "400");
			rsMap.put("msg", "导出失败！");
		}
		LogManager.Process("Process out controller : /nlap/admin/ruleMgmt/exportRule");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 导出模板或者节点
	 * 
	 * @param ruleIds
	 * @param templateId
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/exportTemplateAndNode", method = { RequestMethod.GET, RequestMethod.POST })
	public String exportTemplateAndNode(@RequestParam(value = "templateId", required = true) String templateId,
			@RequestParam(value = "nodeId", required = false) String nodeId, HttpServletResponse response) {
		LogManager.Process("Process in controller : /nlap/admin/ruleMgmt/exportTemplateAndNode");
		Map<String, Object> rsMap = new HashMap<String, Object>();
		rsMap.put("status", "200");
		rsMap.put("msg", "导出成功!");
		boolean result = ruleMgmtService.exportTemplateAndNode(templateId, nodeId, response);
		if (!result) {
			rsMap.put("status", "400");
			rsMap.put("msg", "导出失败！");
		}
		LogManager.Process("Process out controller : /nlap/admin/ruleMgmt/exportTemplateAndNode");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	/**
	 * 检索规则
	 * @param templateId
	 * @param nodeId
	 * @param keyWord
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/searchRule", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchRule(@RequestParam(value = "templateId", required = true) String templateId,
			@RequestParam(value = "nodeId", required = false) String nodeId,
			@RequestParam(value = "keyWord", required = false) String keyWord,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.Process("Process in controller : /nlap/admin/ruleMgmt/searchRule");
		
		
		Map<String, Object> rsMap = new HashMap<String, Object>();
		rsMap.put("status", "200");
		rsMap.put("msg", "搜索成功!");
		List<Map<String, Object>> pagedRules = new ArrayList<>();
		int totalNum = 0;
		if (pageIndex == null || pageIndex < 1) {
			pageIndex = 1;
		}
		List<Map<String, Object>> rules = new ArrayList<>();

		if (null == keyWord || "".equals(keyWord)) {
			if (null == nodeId || "".equals(nodeId)) {
				// 模板下的规则
				rules = ruleMgmtService.findRulesByTemplate(templateId);
			} else {
				// 节点下的规则
				rules = ruleMgmtService.findRulesByNode(nodeId);
			}
			totalNum = rules.size();
		} else {
			Map<String, Object> rulesMap = ruleMgmtService.searchRule(templateId, nodeId, keyWord);
			rules = (List<Map<String, Object>>) rulesMap.get("rules");
			totalNum = (int) rulesMap.get("totalNum");
		}
		if (0 == rules.size()) {
			pagedRules = rules;
		} else {
			pagedRules = rules.subList(pageSize * (pageIndex - 1),
					((pageSize * pageIndex) > totalNum ? totalNum : (pageSize * pageIndex)));
		}
		rsMap.put("rules", pagedRules);
		rsMap.put("totalNum", totalNum);
		LogManager.Process("Process out controller : /nlap/admin/ruleMgmt/searchRule");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	/**
	 * 检索模板
	 * @param ruleTypeId
	 * @param keyWord
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/searchTemplate", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchTemplate(@RequestParam(value = "ruleTypeId", required = true) String ruleTypeId,
			@RequestParam(value = "keyWord", required = false) String keyWord,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.Process("Process in controller : /nlap/admin/ruleMgmt/searchTemplate");
		
		
		Map<String, Object> rsMap = new HashMap<String, Object>();
		rsMap.put("status", "200");
		rsMap.put("msg", "搜索成功!");
		
		List<Map<String, Object>> templates = new ArrayList<>();
		List<Map<String, Object>> pagedTemplates = new ArrayList<>();
		
		int totalNum = 0;
		if (pageIndex == null || pageIndex < 1) {
			pageIndex = 1;
		}

		if (null == keyWord || "".equals(keyWord)) {
			// 全部
			if (null == ruleTypeId || "0".equals(ruleTypeId)) {
				templates = ruleMgmtService.findAllTemplates();
			} else {
				templates = ruleMgmtService.filterByRuleType(ruleTypeId);
			}
			totalNum = templates.size();
		} else {
			Map<String, Object> map = ruleMgmtService.searchTemplate(ruleTypeId, keyWord);
			templates = (List<Map<String, Object>>) map.get("template");
			totalNum = templates.size();
		}
		if (0 == templates.size()) {
			pagedTemplates = templates;
		} else {
			pagedTemplates = templates.subList(pageSize * (pageIndex - 1),
					((pageSize * pageIndex) > totalNum ? totalNum : (pageSize * pageIndex)));
		}
		rsMap.put("template", pagedTemplates);
		rsMap.put("totalNum", totalNum);
		LogManager.Process("Process out controller : /nlap/admin/ruleMgmt/searchTemplate");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
}