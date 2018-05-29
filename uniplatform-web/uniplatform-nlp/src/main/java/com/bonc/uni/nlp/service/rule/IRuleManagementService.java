package com.bonc.uni.nlp.service.rule;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.entity.rule.RuleType;

@Service
@Transactional
public interface IRuleManagementService {
	/**
	 * 获取所有规则分类
	 * @return
	 */
	List<RuleType> findAllRuleClassify();
	/**
	 * 获取所有模板
	 * @return
	 */
	List<Map<String, Object>> findAllTemplates();
	/**
	 * 获取对应词库类型的所有模板
	 * @param dicTypeId
	 * @return
	 */
	List<Map<String, Object>> filterByRuleType(String ruleTypeId);
	/**
	 * 获取对应模板下的所有节点
	 * @param templateId
	 * @return
	 */
	List<Map<String, Object>> findNodesByTemplate(String templateId);
	/**
	 * 获取对应节点下的所有规则
	 * @param nodeId
	 * @return
	 */
	List<Map<String, Object>> findRulesByNode(String nodeId);
	/**
	 * 获取模板下的所有规则
	 * @param templateId
	 * @return
	 */
	List<Map<String, Object>> findRulesByTemplate(String templateId);
	/**
	 * 搜索规则
	 * @param templateId
	 * @return
	 */
	Map<String, Object> searchRule(String templateId, String nodeId, String keyWord);
	/**
	 * 搜索模板
	 * @param templateId
	 * @return
	 */
	Map<String, Object> searchTemplate(String ruleTypeId, String keyWord);
	/**
	 * 更新规则
	 * @param ruleId
	 * @param rule
	 * @return
	 */
	boolean updateRule(String nodeId, String ruleId, String rule);
	/**
	 * 删除规则
	 * @param ruleId
	 * @return
	 */
	boolean deleteRule(String ruleId);
	/**
	 * 添加规则
	 * @param rule
	 * @return
	 */
	boolean addRule(String rule, String nodeId);
	/**
	 * 上传
	 * @param file
	 * @param nodeId
	 */
	void uploadRule(MultipartFile file, String templateId);
	/**
	 * 添加模板
	 * @param dicTypeId
	 * @param templateName
	 * @return
	 */
	boolean addTemplate(String ruleTypeId, String templateName);
	/**
	 * 添加节点
	 * @param templateId
	 * @param nodeName
	 * @return
	 */
	boolean addNode(String templateId, String nodeName);
	/**
	 * 编辑模板
	 * @param templateId
	 * @param templateName
	 * @return
	 */
	boolean updateTemplate(String templateId, String templateName, String ruleTypeId);
	/**
	 * 编辑节点
	 * @param nodeId
	 * @param nodeName
	 * @return
	 */
	boolean updateNode(String nodeId, String nodeName, String templateId);
	/**
	 * 删除节点
	 * @param nodeId
	 * @return
	 */
	boolean deleteNode(String nodeId);
	/**
	 * 删除模板
	 * @param templateId
	 * @return
	 */
	boolean deleteTemplate(String templateId);
	/**
	 * 导出规则
	 * @param ruleIds
	 * @param filePath
	 * @return
	 */
	boolean exportRuleToXml(String ruleIds, String templateId, HttpServletResponse response);
	/**
	 * 导出整个模板或者节点
	 * @param templateId
	 * @param nodeId
	 */
	boolean exportTemplateAndNode(String templateId, String nodeId, HttpServletResponse response);
	}
