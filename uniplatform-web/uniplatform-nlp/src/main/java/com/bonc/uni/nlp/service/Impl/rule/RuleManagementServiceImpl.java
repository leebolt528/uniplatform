package com.bonc.uni.nlp.service.Impl.rule;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.dao.rule.NodeRepository;
import com.bonc.uni.nlp.dao.rule.RuleTypeRepository;
import com.bonc.uni.nlp.dao.rule.RuleRepository;
import com.bonc.uni.nlp.dao.rule.TemplateRepository;
import com.bonc.uni.nlp.entity.rule.Node;
import com.bonc.uni.nlp.entity.rule.Rule;
import com.bonc.uni.nlp.entity.rule.RuleType;
import com.bonc.uni.nlp.entity.rule.Template;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.rule.IRuleManagementService;
import com.bonc.uni.nlp.utils.MyXmlUtil;
import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.uni.nlp.utils.TimeUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import com.ibm.icu.impl.duration.TimeUnit;

@Service
@Transactional
public class RuleManagementServiceImpl implements IRuleManagementService {

	@Autowired
	RuleTypeRepository ruleTypeRepository;
	
	@Autowired
	TemplateRepository templateRepository;
	
	@Autowired
	NodeRepository nodeRepository;
	
	@Autowired
	RuleRepository ruleRepository;

	private static final String XML_TEMPLATES = "templates";

	private static final String XML_TEMPLATE = "template";

	private static final String XML_RULE_TYPE = "ruleType";

	private static final String XML_TEMPLATE_NAME = "templateName";

	private static final String XML_NODES = "nodes";

	private static final String XML_NODE = "node";

	private static final String XML_NODE_NAME = "nodeName";

	private static final String XML_RULES = "rules";

	private static final String XML_RULE = "rule";
	
	private Sort sortByCreateTime = new Sort(Sort.Direction.DESC, "createTime");

	private List<Map<String, Object>> filterTemplates(String ruleTypeId) {
		List<Map<String, Object>> rsList = new ArrayList<>();
		List<Template> templates = new ArrayList<>();
		if (null == ruleTypeId) {
			templates = templateRepository.findAll();
		} else {
			RuleType ruleType = ruleTypeRepository.findOneById(ruleTypeId);
			if (null == ruleType) {
				return rsList;
			}
			templates = templateRepository.findAllByRuleTypeId(ruleTypeId);
		}
		sortTemplate(templates);
		for (Template template : templates) {
			Map<String, Object> tempMap = new HashMap<>();
			RuleType ruleType = ruleTypeRepository.getOne(template.getRuleTypeId());
			tempMap.put("templateId", template.getId());
			tempMap.put("templateName", template.getName());
			tempMap.put("createTime", template.getCreateTime());
			tempMap.put("nodeNum", countRuleByTemplate(template.getId()));
			tempMap.put("ruleType", ruleType.getDisplayName());
			rsList.add(tempMap);
		}
		return rsList;
	}

	private int countRuleByTemplate(String tempalteId) {
		int count = 0;
		List<Node> nodes = nodeRepository.findAllByTemplateId(tempalteId);
		for (Node node : nodes) {
			String nodeId = node.getId();
			count += ruleRepository.countByNodeId(nodeId);
		}
		return count;
	}
	@Override
	public List<Map<String, Object>> findAllTemplates() {
		LogManager.Process("Process in service: the method findAllTemplates of RuleManagementServiceImpl");
		LogManager.Process("Process out service: the method findAllTemplates of RuleManagementServiceImpl");
		return filterTemplates(null);
	}

	@Override
	public List<Map<String, Object>> filterByRuleType(String ruleTypeId) {
		LogManager.Process("Process in service: the method filterByDicType of RuleManagementServiceImpl");
		LogManager.Process("Process out service: the method filterByDicType of RuleManagementServiceImpl");
		return filterTemplates(ruleTypeId);
	}

	@Override
	public List<Map<String, Object>> findNodesByTemplate(String templateId) {
		List<Map<String, Object>> rsList = new ArrayList<>();
		List<Node> nodes = new ArrayList<>();
		Template template = templateRepository.findOneById(templateId);
		if (null == template) {
			return rsList;
		}
		nodes = nodeRepository.findAllByTemplateId(templateId, sortByCreateTime);
		sortNode(nodes);
		for (Node node : nodes) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("nodeId", node.getId());
			tempMap.put("nodeName", node.getName());
			tempMap.put("createTime", node.getCreateTime());
			tempMap.put("templateId", templateId);
			rsList.add(tempMap);
		}
		return rsList;
	}

	@Override
	public List<Map<String, Object>> findRulesByNode(String nodeId) {
		List<Map<String, Object>> rsList = new ArrayList<>();
		List<Rule> rules = new ArrayList<>();
		Node node = nodeRepository.findOneById(nodeId);
		if (null == node) {
			return rsList;
		}
		rules = ruleRepository.findAllByNodeId(nodeId, sortByCreateTime);
		for (Rule rule : rules) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("ruleId", rule.getId());
			tempMap.put("rule", rule.getRule());
			tempMap.put("createTime", rule.getCreateTime());
			tempMap.put("nodeId", nodeId);
			tempMap.put("nodeName", node.getName());
			rsList.add(tempMap);
		}
		return rsList;
	}

	@Override
	public boolean updateRule(String nodeId, String ruleId, String newRule) {
		LogManager.Process("Process in service: the method updateRule of RuleManagementServiceImpl");
		Rule rule = ruleRepository.findOneByRuleAndNodeId(newRule.trim(), nodeId);
		if (null != rule) {
			LogManager.Error("Rule already exits!");
			LogManager.Process("Process out service: the method updateRule of RuleManagementServiceImpl");
			return false;
		}
		// String userId = CurrentUserUtils.getInstance().getUser().get(id);
		ruleRepository.updateRule(newRule.trim(), "1", ruleId);
		LogManager.Process("Process out service: the method updateRule of RuleManagementServiceImpl");
		return true;
	}

	@Override
	public boolean deleteRule(String ruleIds) {
		LogManager.Process("Process in service: the method deleteRule of RuleManagementServiceImpl");
		String[] ruleIdArr = ruleIds.split(",");
		for (String ruleId : ruleIdArr) {
			Rule rule = ruleRepository.getOne(ruleId);
			if (null == rule) {
				LogManager.Error("Rule is empty!");
				continue;
			}
			ruleRepository.delete(ruleId);
		}

		LogManager.Debug("Delete rules success!");
		LogManager.Process("Process out service: the method deleteRule of RuleManagementServiceImpl");
		return true;
	}

	@Override
	public boolean addRule(String rule, String nodeId) {
		LogManager.Process("Process in service: the method addRule of RuleManagementServiceImpl");
		if (null != ruleRepository.findOneByRuleAndNodeId(rule.trim(), nodeId)) {
			LogManager.Debug("Rule already exits!");
			LogManager.Process("Process out service: the method addRule of RuleManagementServiceImpl");
			return false;
		}
        
		Rule ruleEntity = new Rule();
		ruleEntity.setNodeId(nodeId);
		ruleEntity.setRule(rule.trim());
		ruleEntity.setCreateTime(TimeUtil.getNowDate());
		ruleEntity.setUserId("uni");
		ruleRepository.save(ruleEntity);
		LogManager.Process("Process out service: the method addRule of RuleManagementServiceImpl");
		return true;
	}

	@Override
	public void uploadRule(MultipartFile file, String templateId) {
		LogManager.Process("Process in service: the method uploadRule of RuleManagementServiceImpl");

		Map<String, Object> xmlMap = new HashMap<>();
//		SysUser user = CurrentUserUtils.getInstance().getUser();
//		String userId = String.valueOf(user.getId());
		
		// MultipartFile转File
		try {
			String fileName = file.getOriginalFilename();
			String xmlFilePath = PathUtil.getResourcesPath() + File.separator + fileName;
			File xmlFile = new File(xmlFilePath);
			file.transferTo(xmlFile);
			xmlMap = MyXmlUtil.XmlToMap(xmlFile);
			xmlFile.delete();
		} catch (Exception e) {
			LogManager.Exception(e);
		}


		// 读取 <templates>
		if (null == xmlMap.get(XML_TEMPLATES)) {
			throw new AdminException("上传文件格式不正确，缺少<templates>标签，请参照模板！");
		}
		Map<String, Object> templateMap = (Map<String, Object>) xmlMap.get(XML_TEMPLATES);

		// 读取 <template>
		if (null == templateMap.get(XML_TEMPLATE)) {
			throw new AdminException("上传文件格式不正确，缺少<template>标签，请参照模板！");
		}
		List<Map<String, Object>> templateList = new ArrayList<>();
		if (templateMap.get(XML_TEMPLATE) instanceof java.util.List) {
			templateList.addAll((List<Map<String, Object>>) templateMap.get(XML_TEMPLATE));
		} else {
			templateList.add((Map<String, Object>) templateMap.get(XML_TEMPLATE));
		}
		for (Map<String, Object> map : templateList) {
			// 读取 <ruleType>
//			if (null == map.get(XML_RULE_TYPE)) {
//				throw new AdminException("上传文件格式不正确，缺少<ruleType>标签，请参照模板！");
//			}
//			String ruleTypeName = String.valueOf(map.get(XML_RULE_TYPE));
//			if (StringUtil.isEmpty(ruleTypeName)) {
//				throw new AdminException("上传文件格式不正确，<ruleType>不能为空，请参照模板！");
//			}
//			if (null == ruleTypeRepository.findOneByDisplayName(ruleTypeName)) {
//				throw new AdminException("分类规则不存在，请检查！");
//			}
//			RuleType ruleType = ruleTypeRepository.findOneByDisplayName(ruleTypeName);

			// 读取 <templateName>
//			if (null == map.get(XML_TEMPLATE_NAME)) {
//				throw new AdminException("上传文件格式不正确，缺少<templateName>标签，请参照模板！");
//			}
//			String templateName = String.valueOf(map.get(XML_TEMPLATE_NAME));
//			if (StringUtil.isEmpty(templateName)) {
//				throw new AdminException("上传文件格式不正确，<templateName>不能为空，请参照模板！");
//			}
//			Template template = new Template();
//			if (null != templateRepository.findOneByNameAndRuleTypeId(templateName, ruleType.getId())) {
//				// 同名模板已经存在
//				template = templateRepository.findOneByName(templateName);
//			} else {
//				template.setCreateTime(new Date());
//				template.setName(templateName);
//				template.setRuleTypeId(ruleType.getId());
//				templateRepository.save(template);
//			}

			// 读取 <nodes>
			if (null == map.get(XML_NODES)) {
				throw new AdminException("上传文件格式不正确，缺少<nodes>标签，请参照模板！");
			}
			Map<String, Object> nodeMap = (Map<String, Object>) map.get(XML_NODES);

			// 读取 <node>
			if (null == nodeMap.get(XML_NODE)) {
				throw new AdminException("");
			}
			List<Map<String, Object>> nodeList = new ArrayList<>();
			if (nodeMap.get(XML_NODE) instanceof java.util.List) {
				nodeList.addAll((List<Map<String, Object>>) nodeMap.get(XML_NODE));
			} else {
				nodeList.add((Map<String, Object>) nodeMap.get(XML_NODE));
			}
			for (Map<String, Object> node : nodeList) {
		        Date date = new Date();
		        Calendar cal = Calendar.getInstance();
		        cal.setTime(date);//date 换成已经已知的Date对象
		        cal.add(Calendar.HOUR_OF_DAY, 8);// after 8 hour
		        
				// 读取 <nodeName>
				if (null == node.get(XML_NODE_NAME)) {
					throw new AdminException();
				}
				String nodeName = String.valueOf(node.get(XML_NODE_NAME));
				if (StringUtil.isEmpty(nodeName)) {
					throw new AdminException("上传文件格式不正确，<nodeName>不能为空，请参照模板！");
				}
				Node nodeEntity = new Node();
				if (null != nodeRepository.findOneByNameAndTemplateId(nodeName, templateId)) {
					nodeEntity = nodeRepository.findOneByNameAndTemplateId(nodeName, templateId);
				} else {
					nodeEntity.setCreateTime(cal.getTime());
					nodeEntity.setName(nodeName.trim());
					nodeEntity.setTemplateId(templateId);
					nodeEntity.setUserId("uni");
					nodeRepository.save(nodeEntity);
				}

				// 读取 <rules>
				if (null == node.get(XML_RULES)) {
					throw new AdminException();
				}
				Map<String, Object> rulesMap = (Map<String, Object>) node.get(XML_RULES);

				// 读取 <rule>
				if (null == rulesMap.get(XML_RULE)) {
					throw new AdminException();
				}
				List<Object> ruleList = new ArrayList<>();
				if ( rulesMap.get(XML_RULE) instanceof java.util.List) {
					ruleList.addAll((List<Object>) rulesMap.get(XML_RULE));
				} else {

					ruleList.add(rulesMap.get(XML_RULE));
				}
				
				for (Object rule : ruleList) {
					String ruleStr = String.valueOf(rule);
					if (StringUtil.isEmpty(ruleStr)
							|| null != ruleRepository.findOneByRuleAndNodeId(ruleStr, nodeEntity.getId())) {
						continue;
					}
					Rule ruleEntity = new Rule();
					ruleEntity.setCreateTime(cal.getTime());
					ruleEntity.setNodeId(nodeEntity.getId());
					ruleEntity.setRule(ruleStr);
					ruleEntity.setUserId("uni");
					ruleRepository.save(ruleEntity);
				}
			}
		}
	}

	@Override
	public boolean addTemplate(String ruleTypeId, String templateName) {
		LogManager.Process("Process in service: the method addTemplate of RuleManagementServiceImpl");
		if (null != templateRepository.findOneByNameAndRuleTypeId(templateName, ruleTypeId)) {
			LogManager.Debug("Template already exits!");
			LogManager.Process("Process out service: the method addTemplate of RuleManagementServiceImpl");
			return false;
		}
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//date 换成已经已知的Date对象
        cal.add(Calendar.HOUR_OF_DAY, 8);// before 8 hour

		// SysUser user = CurrentUserUtils.getInstance().getUser();
		Template template = new Template();
		template.setName(templateName);
		template.setCreateTime(cal.getTime());
		template.setRuleTypeId(ruleTypeId);
		template.setUserId("uni");
		templateRepository.save(template);
		LogManager.Process("Process out service: the method addTemplate of RuleManagementServiceImpl");
		return true;
	}

	@Override
	public boolean addNode(String templateId, String nodeName) {
		LogManager.Process("Process in service: the method addNode of RuleManagementServiceImpl");
		if (null != nodeRepository.findOneByNameAndTemplateId(nodeName, templateId)) {
			LogManager.Debug("Node already exits!");
			LogManager.Process("Process out service: the method addNode of RuleManagementServiceImpl");
			return false;
		}
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//date 换成已经已知的Date对象
        cal.add(Calendar.HOUR_OF_DAY, 8);// after 8 hour
        
		// SysUser user = CurrentUserUtils.getInstance().getUser();
		Node node = new Node();
		node.setName(nodeName);
		node.setCreateTime(cal.getTime());
		node.setTemplateId(templateId);
		node.setUserId("uni");
		nodeRepository.save(node);
		LogManager.Process("Process out service: the method addNode of RuleManagementServiceImpl");
		return true;

	}

	@Override
	public boolean updateTemplate(String templateId, String templateName, String ruleTypeId) {
		LogManager.Process("Process in service: the method updateTemplate of RuleManagementServiceImpl");
		Template template = templateRepository.findOneByNameAndRuleTypeId(templateName, ruleTypeId);
		if (null != template) {
			LogManager.Error("Template already exits!");
			LogManager.Process("Process out service: the method updateTemplate of RuleManagementServiceImpl");
			return false;
		}
		// String userId = CurrentUserUtils.getInstance().getUser().get(id);
		templateRepository.updateTemplate(templateName, "1", templateId);
		LogManager.Process("Process out service: the method updateTemplate of RuleManagementServiceImpl");
		return true;
	}

	@Override
	public boolean updateNode(String nodeId, String nodeName, String templateId) {
		LogManager.Process("Process in service: the method updateNode of RuleManagementServiceImpl");
		Node node = nodeRepository.findOneByNameAndTemplateId(nodeName, templateId);
		if (null != node) {
			LogManager.Error("Node already exits!");
			LogManager.Process("Process out service: the method updateNode of RuleManagementServiceImpl");
			return false;
		}
		// String userId = CurrentUserUtils.getInstance().getUser().get(id);
		nodeRepository.updateNode(nodeName, "1", nodeId);
		LogManager.Process("Process out service: the method updateNode of RuleManagementServiceImpl");
		return true;
	}

	@Override
	public boolean deleteNode(String nodeId) {
		LogManager.Process("Process in service: the method deleteNode of RuleManagementServiceImpl");
		Node node = nodeRepository.getOne(nodeId);
		if (null == node) {
			LogManager.Error("The node does not exist!");
			LogManager.Process("Process out service: the method deleteNode of RuleManagementServiceImpl");
			return false;
		}
		// 先删除节点下面的所有规则
		ruleRepository.deleteByNodeId(nodeId);
		// 删除自身
		nodeRepository.delete(nodeId);
		return true;
	}

	@Override
	public boolean deleteTemplate(String templateId) {
		LogManager.Process("Process in service: the method deleteTemplate of RuleManagementServiceImpl");
		Template template = templateRepository.getOne(templateId);
		if (null == template) {
			LogManager.Error("The template does not exist!");
			LogManager.Process("Process out service: the method deleteTemplate of RuleManagementServiceImpl");
			return false;
		}
		// 先删除模板下面的所有节点
		List<Node> nodes = nodeRepository.findAllByTemplateId(templateId, sortByCreateTime);
		if (!CollectionUtils.isEmpty(nodes)) {
			for (Node node : nodes) {
				this.deleteNode(node.getId());
			}
		}
		templateRepository.delete(templateId);
		LogManager.Process("Process out service: the method deleteTemplate of RuleManagementServiceImpl");
		return true;
	}
	/**
	 * 导出
	 * @param templateId
	 * @param rules
	 * @param response
	 * @return
	 */
	private boolean exportRules(String templateId, List<Rule> rules,  HttpServletResponse response) {
		Map<String, List<Rule>> groupedRule = groupRuleByNodeId(rules);
		OutputStream out = null;
		try {
			response.reset();
			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String((templateRepository.getOne(templateId).getName() +".xml").getBytes("GBK"), "ISO8859_1"));
			out = response.getOutputStream();
			StringBuilder xmlContent = new StringBuilder();
			xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
			xmlContent.append("<" + XML_TEMPLATES + ">\r\n");
			xmlContent.append("	<" + XML_TEMPLATE + ">\r\n");
			xmlContent.append("		<" + XML_RULE_TYPE + ">"
					+ ruleTypeRepository.getOne(templateRepository.getOne(templateId).getRuleTypeId()).getDisplayName()
					+ "</" + XML_RULE_TYPE + ">\r\n");
			xmlContent.append("		<" + XML_TEMPLATE_NAME + ">" + templateRepository.findOneById(templateId).getName()
					+ "</" + XML_TEMPLATE_NAME + ">\r\n");
			xmlContent.append("		<" + XML_NODES + ">\r\n");
			for (Map.Entry<String, List<Rule>> entry : groupedRule.entrySet()) {
				String nodeId = entry.getKey();
				xmlContent.append("			<" + XML_NODE + ">\r\n");
				xmlContent.append("				<" + XML_NODE_NAME + ">" + nodeRepository.getOne(nodeId).getName()
						+ "</" + XML_NODE_NAME + ">\r\n");
				List<Rule> ruleList = entry.getValue();
				xmlContent.append("				<" + XML_RULES + ">\r\n");
				for (Rule rule : ruleList) {
					xmlContent.append("					<" + XML_RULE + ">" + rule.getRule() + "</" + XML_RULE + ">\r\n");
				}
				xmlContent.append("				</" + XML_RULES + ">\r\n");
				xmlContent.append("			</" + XML_NODE + ">\r\n");
			}
			xmlContent.append("		</" + XML_NODES + ">\r\n");
			xmlContent.append("	</" + XML_TEMPLATE + ">\r\n");
			xmlContent.append("</" + XML_TEMPLATES + ">\r\n");
			out.write(xmlContent.toString().getBytes());
			LogManager.Debug("Download the rule.");

		} catch (IOException e) {
			LogManager.Exception("ExportRuleController exception : ", e);
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LogManager.Exception("ExportRuleController exception : ", e);
					return false;
				}
			}
		}
		return true;
	}
	@Override
	public boolean exportRuleToXml(String ruleIds, String templateId, HttpServletResponse response) {
		LogManager.Process("Process in service: the method exportRule of RuleManagementServiceImpl");

		List<Rule> rules = new ArrayList<>();
		String[] ruleIdArr = ruleIds.split(",");
		for (String ruleId : ruleIdArr) {
			Rule rule = ruleRepository.getOne(ruleId);
			if (null == rule) {
				LogManager.Error("Rule is empty! id is " + ruleId);
				continue;
			}
			rules.add(rule);
		}
		LogManager.Process("Process out service: the method exportRule of RuleManagementServiceImpl");
		return exportRules(templateId, rules, response);
	}

	/**
	 * 将规则按节点进行分类
	 * 
	 * @return
	 */
	private Map<String, List<Rule>> groupRuleByNodeId(List<Rule> rules) {
		Map<String, List<Rule>> rsMap = new HashMap<>();
		try {
			for (Rule rule : rules) {
				String nodeId = rule.getNodeId();
				if (rsMap.containsKey(nodeId)) {
					rsMap.get(nodeId).add(rule);
				} else {
					List<Rule> tempList = new ArrayList<>();
					tempList.add(rule);
					rsMap.put(nodeId, tempList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsMap;
	}

	@Override
	public List<RuleType> findAllRuleClassify() {
		LogManager.Process("Process in service: the method findAllRuleClassify of RuleManagementServiceImpl");

		Sort sort = new Sort(Sort.Direction.ASC, "index");
		List<RuleType> classifies = ruleTypeRepository.findAll(sort);
		if (classifies.isEmpty()) {
			LogManager.Debug("The ruleClassify id empty!");
		}

		LogManager.Process("Process out service: the method findAllRuleClassify of RuleManagementServiceImpl");
		return classifies;
	}

	@Override
	public List<Map<String, Object>> findRulesByTemplate(String templateId) {
		LogManager.Process("Process in service: the method findRulesByTemplate of RuleManagementServiceImpl");
		List<Map<String, Object>> rsList = new ArrayList<>();
		Template template = templateRepository.findOne(templateId);
		if (null == template) {
			LogManager.debug("Template does not exit!");
			return rsList;
		}
		List<Node> nodes = nodeRepository.findAllByTemplateId(templateId, sortByCreateTime);
		for (Node node : nodes) {
			List<Rule> rules = ruleRepository.findAllByNodeId(node.getId(), sortByCreateTime);
			for (Rule rule : rules) {
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("ruleId", rule.getId());
				tempMap.put("rule", rule.getRule());
				tempMap.put("createTime", rule.getCreateTime());
				tempMap.put("nodeId", node.getId());
				tempMap.put("nodeName", node.getName());
				rsList.add(tempMap);
			}
		}
		LogManager.Process("Process out service: the method findRulesByTemplate of RuleManagementServiceImpl");
		return rsList;
	}

	private void sortTemplate(List<Template> templates) {
		Collections.sort(templates, new Comparator<Template>() {
			@Override
			public int compare(Template t1, Template t2) {
				Date dt1 = t1.getCreateTime();
				Date dt2 = t2.getCreateTime();
				if (dt1.getTime() > dt2.getTime()) {
					return -1;
				} else if (dt1.getTime() < dt2.getTime()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
	}

	private void sortNode(List<Node> nodes) {
		Collections.sort(nodes, new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				Date dt1 = n1.getCreateTime();
				Date dt2 = n2.getCreateTime();
				if (dt1.getTime() > dt2.getTime()) {
					return -1;
				} else if (dt1.getTime() < dt2.getTime()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
	}

	@Override
	public boolean exportTemplateAndNode(String templateId, String nodeId, HttpServletResponse response) {
		List<Rule> rules = new ArrayList<>();
 		if (null == nodeId || StringUtil.isEmpty(nodeId)) {
			//导出整个模板
 			List<Node> ndoes = nodeRepository.findAllByTemplateId(templateId, sortByCreateTime);
 			for (Node node : ndoes) {
 				rules.addAll(ruleRepository.findAllByNodeId(node.getId(), sortByCreateTime));
			}
		} else {
			//导出节点
			rules = ruleRepository.findAllByNodeId(nodeId, sortByCreateTime);
		}
		return exportRules(templateId, rules, response);
	}

	@Override
	public Map<String, Object> searchRule(String templateId, String nodeId, String keyWord) {
		Map<String, Object> returnMap = new HashMap<>();
		keyWord = "%" + keyWord + "%";

		List<Map<String, Object>> ruleInfo = new ArrayList<>();
		List<Node> nodes = new ArrayList<>();
		List<String> nodeIds = new ArrayList<>();
		if (null == nodeId || "".equals(nodeId)) {
			// 在模板下搜索
			 nodes = nodeRepository.findAllByTemplateId(templateId, sortByCreateTime);
		} else {
			// 在节点下搜索
			nodes.add(nodeRepository.getOne(nodeId));
		}
		for (Node node : nodes) {
			nodeIds.add(node.getId());
		}
		List<Rule> rules = ruleRepository.findAllByRuleLike(keyWord, nodeIds, sortByCreateTime);
		for (Rule rule : rules) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("ruleId", rule.getId());
			tempMap.put("rule", rule.getRule());
			tempMap.put("createTime", rule.getCreateTime());
			tempMap.put("nodeId", nodeId);
			tempMap.put("nodeName", nodeRepository.getOne(rule.getNodeId()).getName());
			ruleInfo.add(tempMap);
		}
		returnMap.put("rules", ruleInfo);
		returnMap.put("totalNum", ruleRepository.countByRuleLike(keyWord, nodeIds));
		return returnMap;
	}

	@Override
	public Map<String, Object> searchTemplate(String ruleTypeId, String keyWord) {
		Map<String, Object> returnMap = new HashMap<>();
		keyWord = "%" + keyWord + "%";
		
		List<Map<String, Object>> templateInfo = new ArrayList<>();
		List<String> ruleTypeIds = new ArrayList<>();
		if (null == ruleTypeId || "0".equals(ruleTypeId)) {
			// 全部模板中检索
			List<RuleType> ruleTypes = ruleTypeRepository.findAll();
			for (RuleType ruleType : ruleTypes) {
				ruleTypeIds.add(ruleType.getId());
			}
		} else {
			ruleTypeIds.add(ruleTypeId);
		}
		List<Template> templates = templateRepository.findAllByNameLike(keyWord, ruleTypeIds);

		sortTemplate(templates);
		
		for (Template template : templates) {
			Map<String, Object> tempMap = new HashMap<>();
			RuleType ruleType = ruleTypeRepository.getOne(template.getRuleTypeId());
			tempMap.put("templateId", template.getId());
			tempMap.put("templateName", template.getName());
			tempMap.put("createTime", template.getCreateTime());
			tempMap.put("nodeNum", nodeRepository.findAllByTemplateId(template.getId(), sortByCreateTime).size());
			tempMap.put("ruleType", ruleType.getDisplayName());
			templateInfo.add(tempMap);
		}
		returnMap.put("template", templateInfo);
		returnMap.put("totalNum", templateRepository.countByNameLike(keyWord, ruleTypeIds));
		return returnMap;
	}
}
