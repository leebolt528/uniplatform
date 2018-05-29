package com.bonc.uni.nlp.service.Impl.strategy;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.nlp.dao.strategy.BusinessNodeRelationRepository;
import com.bonc.uni.nlp.dao.strategy.BusinessNodeRepository;
import com.bonc.uni.nlp.dao.strategy.BusinessRepository;
import com.bonc.uni.nlp.dao.strategy.StrategyNodeRelationRepository;
import com.bonc.uni.nlp.dao.strategy.StrategyRepository;
import com.bonc.uni.nlp.dao.task.TaskRepository;
import com.bonc.uni.nlp.entity.strategy.Business;
import com.bonc.uni.nlp.entity.strategy.BusinessNode;
import com.bonc.uni.nlp.entity.strategy.BusinessNodeRelation;
import com.bonc.uni.nlp.entity.strategy.Strategy;
import com.bonc.uni.nlp.entity.strategy.StrategyConstant;
import com.bonc.uni.nlp.entity.strategy.StrategyNodeRelation;
import com.bonc.uni.nlp.entity.task.Task;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.strategy.IBusinessMgmtService;
import com.bonc.uni.nlp.utils.MyXmlUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年11月10日 上午11:22:34
 */
@Service
public class BusinessMgmtServiceImpl implements IBusinessMgmtService {

	@Autowired
	BusinessRepository businessRepository;
	@Autowired
	BusinessNodeRepository businessNodeRepository;
	@Autowired
	StrategyRepository strategyRepository;
	@Autowired
	StrategyNodeRelationRepository strategyNodeRelationRepository;
	@Autowired
	BusinessNodeRelationRepository businessNodeRelationRepository;
	@Autowired
	TaskRepository taskRepository;

	@Override
	public Map<String, Object> listBusinessNode() {
		Map<String, Object> nodeInfo = new HashMap<>();
		List<BusinessNode> businessNodes = new ArrayList<>();
		int nodeNumber = 0;
		businessNodes = businessNodeRepository.find();
		List<Object> businessNodeInfoList = new ArrayList<>();
		for (BusinessNode businessNode : businessNodes) {
			Map<String, Object> businessNodeInfo = new HashMap<>();
			List<Object> usingBusiness = new ArrayList<>();
			if (1 == businessNode.getInUsing()) {
				String nodeId = businessNode.getId();
				List<BusinessNodeRelation> businessNodeRelations = businessNodeRelationRepository
						.findAllByNodeId(nodeId);
				Map<String, String> businessNameMap = new HashMap<>();
				for (BusinessNodeRelation businessNodeRelation : businessNodeRelations) {
					businessNameMap = new HashMap<>();
					String businessId = businessNodeRelation.getBusinessId();
					Business business = businessRepository.findOne(businessId);
					businessNameMap.put("businssName", business.getName());
					businessNameMap.put("businessId", business.getId());
					usingBusiness.add(businessNameMap);
				}
			}
			businessNodeInfo = new HashMap<>();
			businessNodeInfo.put("usingBusiness", usingBusiness);
			businessNodeInfo.put("nodeId", businessNode.getId());
			businessNodeInfo.put("nodeName", businessNode.getName());
			businessNodeInfo.put("createTime", businessNode.getCreateTime());
			businessNodeInfo.put("status", businessNode.getInUsing());
			businessNodeInfoList.add(businessNodeInfo);
		}
		nodeInfo = new HashMap<>();
		nodeInfo.put("nodeInfo", businessNodeInfoList);
		nodeNumber = (int) businessNodeRepository.count();
		nodeInfo.put("numbers", nodeNumber);

		return nodeInfo;
	}

	@Override
	public boolean validateNodeExists(String nodeName) {
		if (StringUtil.isEmpty(nodeName)) {
			return false;
		}
		BusinessNode existedNode = businessNodeRepository.findOneByName(nodeName);
		if (null != existedNode) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addBusinessNode(String nodeName, String strategyIds) {
		if (null == nodeName || 0 == nodeName.length()) {
			throw new AdminException("添加失败，节点名称为空");
		}
		String[] arrStrategyId = strategyIds.split(",");
		for (String strategyId : arrStrategyId) {
			Strategy existStrategy = strategyRepository.findOne(strategyId);
			if (null == existStrategy) {
				throw new AdminException("添加失败，该功能策略不存在");
			}
		}

		if (validateNodeExists(nodeName)) {
			throw new AdminException("节点名称已存在，请重新输入");
		}

		BusinessNode node = new BusinessNode();
		node.setName(nodeName);
		node.setCreateTime(new Date());
		node.setInUsing(0);

		businessNodeRepository.save(node);

		for (int i = 0; i < arrStrategyId.length; i++) {
			Strategy strategy = strategyRepository.findOne(arrStrategyId[i]);
			strategy.setInUsing(1);
			strategyRepository.save(strategy);

			StrategyNodeRelation strategyNodeRelation = new StrategyNodeRelation();
			BusinessNode nodeInfo = businessNodeRepository.findOneByName(nodeName);
			strategyNodeRelation.setNodeId(nodeInfo.getId());
			strategyNodeRelation.setStrategyId(arrStrategyId[i]);
			strategyNodeRelation.setStrategyIndex(i + 1);

			strategyNodeRelationRepository.save(strategyNodeRelation);
		}

		return true;
	}

	@Override
	public int deleteBusinessNode(String nodesId) {
		String[] arrNodes = nodesId.split(",");
		if (null == nodesId || 0 == nodesId.length()) {
			return 0;
		}

		for (String nodeId : arrNodes) {
			BusinessNode node = businessNodeRepository.findOne(nodeId);
			if (null == node || 1 == node.getInUsing()) {
				return 2; // 被业务策略使用中，不能删除
			}
		}
		// 删除功能策略和节点关联表信息
		List<String> allStrategiesId = new ArrayList<>();
		for (String nodeId : arrNodes) {
			businessNodeRepository.delete(nodeId);
			List<StrategyNodeRelation> strategyNodeRelations = strategyNodeRelationRepository.findAllByNodeId(nodeId);
			for (StrategyNodeRelation strategyNodeRelation : strategyNodeRelations) {
				String strategyNodeRelationId = strategyNodeRelation.getId();
				strategyNodeRelationRepository.delete(strategyNodeRelationId);
				String strategyId = strategyNodeRelation.getStrategyId();
				if (null == strategyId) {
					continue;
				}
				allStrategiesId.add(strategyId);
			}
		}

		// 如果策略没被其他节点用到，则将状态置为0
		for (String strategyId : allStrategiesId) {
			List<StrategyNodeRelation> usingNodes = strategyNodeRelationRepository.findAllByStrategyId(strategyId);
			if (0 == usingNodes.size() || null == usingNodes) {
				Strategy strategy = strategyRepository.findOne(strategyId);
				strategy.setInUsing(0);
				strategyRepository.save(strategy);
			}
		}

		return 1;
	}

	@Override
	public Map<String, Object> listBusiness(String keyword, int pageIndex, int pageSize, boolean ascSort) {
		Sort sort = null;
		if (ascSort) {
			sort = new Sort(Sort.Direction.ASC, "createTime");
		} else {
			sort = new Sort(Sort.Direction.DESC, "createTime");
		}
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
		List<Business> businesses = new ArrayList<>();
		int businessesNumber = 0;
		if (StringUtil.isEmpty(keyword)) {
			businesses = businessRepository.findAll(pageable).getContent();
			businessesNumber = (int) businessRepository.count();
		} else {
			keyword = "%" + keyword + "%";
			businesses = businessRepository.findAllByNameLike(keyword, pageable);
			businessesNumber = businessRepository.count(keyword);
		}
		Map<String, Object> businessMap = new HashMap<>();
		List<Object> businessList = new ArrayList<>();
		for (Business business : businesses) {
			List<String> tasksName = new ArrayList<>();
			String businessId = business.getId();
			if (1 == business.getInUsing()) {
				List<Task> tasks = taskRepository.findAllByBusiness(businessId);
				if (null != tasks) {
					for (Task task : tasks) {
						if (!tasksName.contains(task.getName())) {
							tasksName.add(task.getName());
						}
					}
				}
			}
			businessMap = new HashMap<>();
			businessMap.put("business", business);
			businessMap.put("taskNames", tasksName);
			businessList.add(businessMap);
		}
		Map<String, Object> businessesInfo = new HashMap<>();
		businessesInfo.put("businessInfo", businessList);
		businessesInfo.put("numbers", businessesNumber);

		return businessesInfo;
	}

	@Override
	public boolean validateBusinessExists(String businessName) {
		if (StringUtil.isEmpty(businessName)) {
			return false;
		}
		Business business = businessRepository.findOneByName(businessName);
		if (null != business) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addBusiness(String businessName, String nodeIds) {
		if (null == businessName || 0 == businessName.length()) {
			throw new AdminException("添加失败，业务策略名称为空");
		}
		String[] arrNodeIds = nodeIds.split(",");
		if (validateBusinessExists(businessName)) {
			throw new AdminException("该策略名称已存在，请重新输入");
		}
		Business business = new Business();
		business.setName(businessName);
		business.setCreateTime(new Date());
		business.setInUsing(0);
//		business.setUserId(CurrentUserUtils.getInstance().getUser().getId());
		businessRepository.save(business);

		for (String nodeId : arrNodeIds) {
			// 节点状态置为1
			BusinessNode node = businessNodeRepository.findOne(nodeId);
			if (null != node) {
				node.setInUsing(1);
				businessNodeRepository.save(node);
			}
		}

		// 业务策略节点关系表
		Business existedBusiness = businessRepository.findOneByName(businessName);
		for (String nodeId : arrNodeIds) {
			BusinessNodeRelation businessNodeRelation = new BusinessNodeRelation();
			businessNodeRelation.setBusinessId(existedBusiness.getId());
			businessNodeRelation.setNodeId(nodeId);
			businessNodeRelationRepository.save(businessNodeRelation);
		}

		return true;
	}

	@Override
	public boolean editBusiness(String businessId, String newBusinessName, String newNodeIds) {
		String[] arrNewNodeIds = newNodeIds.split(",");
		List<String> newNodeIdsList = new ArrayList<>();
		for (String arrNewNodeId : arrNewNodeIds) {
			newNodeIdsList.add(arrNewNodeId);
		}
		if (StringUtil.isEmpty(newBusinessName)) {
			throw new AdminException("修改失败，业务策略名称不能为空");
		}
		Business business = businessRepository.findOne(businessId);
		if (null == business) {
			throw new AdminException("修改失败，该业务策略不存在");
		}
		if (!newBusinessName.equals(business.getName()) && validateBusinessExists(newBusinessName)) {
			throw new AdminException("修改失败，该策略名称已存在");
		}

		business.setName(newBusinessName);
		business.setCreateTime(new Date());
		business.setInUsing(0);

		// 业务策略节点关系表
		List<BusinessNodeRelation> businessNodeRelations = businessNodeRelationRepository
				.findAllByBusinessId(businessId);
		List<String> currentNodeIds = new ArrayList<>();
		for (BusinessNodeRelation businessNodeRelation : businessNodeRelations) {
			currentNodeIds.add(businessNodeRelation.getNodeId());
		}

		// 新增的关系
		List<String> addNodeIds = new ArrayList<>();
		for (String newNodeId : newNodeIdsList) {
			if (!currentNodeIds.contains(newNodeId)) {
				addNodeIds.add(newNodeId);
			}
		}
		List<String> delNodeIds = new ArrayList<>();
		// 删除的关系
		for (String currentNodeId : currentNodeIds) {
			if (!newNodeIdsList.contains(currentNodeId)) {
				delNodeIds.add(currentNodeId);
			}
		}
		BusinessNodeRelation businessNodeRelation = new BusinessNodeRelation();
		// 增加新关系
		for (String addNodeId : addNodeIds) {
			businessNodeRelation = new BusinessNodeRelation();
			businessNodeRelation.setBusinessId(businessId);
			businessNodeRelation.setNodeId(addNodeId);

			businessNodeRelationRepository.save(businessNodeRelation);
		}

		// 删除旧关系
		for (String delNodeId : delNodeIds) {
			BusinessNodeRelation businessNodeDel = businessNodeRelationRepository
					.findOneByNodeIdAndBusinessId(delNodeId, businessId);
			String businessNodeDelId = businessNodeDel.getId();
			businessNodeRelationRepository.delete(businessNodeDelId);

			List<BusinessNodeRelation> businessNodeRelationExist = businessNodeRelationRepository
					.findAllByNodeId(delNodeId);
			if (null == businessNodeRelationExist || businessNodeRelationExist.isEmpty()) {
				BusinessNode businessNode = businessNodeRepository.findOne(delNodeId);
				businessNode.setInUsing(0);
				businessNodeRepository.save(businessNode);
			}
		}
		businessRepository.save(business);

		return true;
	}

	@Override
	public boolean deleteBusinesses(String businessesId) {
		String[] arrBusinessesId = businessesId.split(",");
		for (String businessId : arrBusinessesId) {

			Business business = businessRepository.findOne(businessId);
			if (null == business) {
				throw new AdminException("删除失败，该策略不存在");
			}
			// 如果策略被任务使用，则不允许删除
			List<Task> inUsingTasks = taskRepository.findAllByBusiness(businessId);
			if (!inUsingTasks.isEmpty()) {
				continue;
			}

			if (1 == business.getInUsing()) {
				continue;
			}
			// 删除业务策略表中信息 删除业务策略节点关系表中信息，如果该节点没别的策略使用，则状态置为0
			List<BusinessNodeRelation> nodeRelations = businessNodeRelationRepository.findAllByBusinessId(businessId);
			for (BusinessNodeRelation nodeRelation : nodeRelations) {
				String nodeId = nodeRelation.getNodeId();
				String businessNodeRelationId = nodeRelation.getId();
				businessNodeRelationRepository.delete(businessNodeRelationId);

				List<BusinessNodeRelation> businessNodeRelationExist = businessNodeRelationRepository
						.findAllByNodeId(nodeId);
				if (null == businessNodeRelationExist || businessNodeRelationExist.isEmpty()) {
					BusinessNode businessNode = businessNodeRepository.findOne(nodeId);
					businessNode.setInUsing(0);
					businessNodeRepository.save(businessNode);
				}
			}
			businessRepository.delete(businessId);
		}

		return true;
	}

	@Override
	public String exportBusinesses(String businessesId) {
		String[] arrBusinessesId = businessesId.split(",");
		StringBuilder xmlContent = new StringBuilder();
		xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		xmlContent.append("<" + StrategyConstant.XML_BUSINESS_ROOT + ">\r\n");
		List<Business> businesses = businessRepository.findAllByIdIn(arrBusinessesId);
		for (Business business : businesses) {
			xmlContent.append("	<" + StrategyConstant.XML_BUSINESS + ">\r\n");
			/**
			 * 策略名称
			 */
			String name = business.getName();
			xmlContent.append("		<" + StrategyConstant.XML_BUSINESS_NAME + ">" + name + "</"
					+ StrategyConstant.XML_BUSINESS_NAME + ">\r\n");

			String nodeStr = "";
			List<BusinessNodeRelation> businessNodeRelations = businessNodeRelationRepository
					.findAllByBusinessId(business.getId());

			for (BusinessNodeRelation businessNodeRelation : businessNodeRelations) {
				String nodeId = businessNodeRelation.getNodeId();
				BusinessNode businessNode = businessNodeRepository.findOne(nodeId);
				String nodeName = businessNode.getName();
				nodeStr += ("," + nodeName);
			}
			nodeStr = nodeStr.substring(nodeStr.indexOf(",") + 1);
			xmlContent.append("		<" + StrategyConstant.XML_BUSINESS_NODES + ">" + nodeStr + "</"
					+ StrategyConstant.XML_BUSINESS_NODES + ">\r\n");

			xmlContent.append("	</" + StrategyConstant.XML_BUSINESS + ">\r\n");
		}
		xmlContent.append("</" + StrategyConstant.XML_BUSINESS_ROOT + ">\r\n");
		return xmlContent.toString();
	}

	@Override
	public List<Map<String, String>> listStrategies() {
		List<Strategy> listStrategies = strategyRepository.find();
		List<Map<String, String>> strategies = new ArrayList<>();
		for (Strategy strategy : listStrategies) {
			Map<String, String> strategiesMap = new HashMap<>();
			strategiesMap.put("strategyId", strategy.getId());
			strategiesMap.put("strategyName", strategy.getName());
			strategies.add(strategiesMap);
		}
		return strategies;
	}

	@Override
	public boolean importBusinesses(MultipartFile[] files) {
		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				continue;
			}
			String name = file.getOriginalFilename();
			String fileType = name.substring(name.lastIndexOf("."));
			if (!".xml".equals(fileType)) {
				throw new AdminException("上传失败，非法文件，没有策略信息");
			}
			Map<String, Object> xmlMap = null;
			try {
				xmlMap = MyXmlUtil.XmlToMap(file);
			} catch (Exception e) {
				LogManager.Exception(e);
			}
			// 解析map
			String businessesStr = JSON.toJSONString(xmlMap);
			JSONObject businessesJson = JSONObject.parseObject(businessesStr);
			JSONObject businesses = businessesJson.getJSONObject("businesses");
			JSON businessType = (JSON) businesses.get("business");
			// 判断类型 是否为list
			String businessJsonStr = JSON.toJSONString(businessType);
			char[] strChar = businessJsonStr.substring(0, 1).toCharArray();
			char firstChar = strChar[0];
			if (firstChar == '{') {
				// 单条策略
				JSONObject businessJson = businesses.getJSONObject("business");
				// 策略名称
				String businessName = businessJson.getString("name");
				boolean existedBusiness = this.validateBusinessExists(businessName);
				if (existedBusiness) {
					throw new AdminException("该业务策略" + businessName + "已存在，请重新上传");
				}
				Business business = new Business();
				business.setName(businessName);
				business.setInUsing(0);
				business.setCreateTime(new Date());
				businessRepository.save(business);
				// 节点名称
				String nodes = businessJson.getString("nodes");
				if (!nodes.contains(",")) {
					BusinessNode existBusinessNode = businessNodeRepository.findOneByName(nodes);
					if (null == existBusinessNode) {
						throw new AdminException("上传失败，该节点不存在");
					}
					existBusinessNode.setInUsing(1);
					Business businessInfo = businessRepository.findOneByName(businessName);
					String businessId = businessInfo.getId();
					String nodeId = existBusinessNode.getId();

					BusinessNodeRelation businessNodeRelation = new BusinessNodeRelation();
					businessNodeRelation.setBusinessId(businessId);
					businessNodeRelation.setNodeId(nodeId);
					businessNodeRelationRepository.save(businessNodeRelation);
				} else {
					String[] nodeNamesArr = nodes.split(",");
					for (String nodeName : nodeNamesArr) {
						BusinessNode existBusinessNode = businessNodeRepository.findOneByName(nodeName);
						if (null == existBusinessNode) {
							throw new AdminException("上传失败，该节点不存在");
						}
						existBusinessNode.setInUsing(1);
						Business businessInfo = businessRepository.findOneByName(businessName);
						String businessId = businessInfo.getId();
						String nodeId = existBusinessNode.getId();

						BusinessNodeRelation businessNodeRelation = new BusinessNodeRelation();
						businessNodeRelation.setBusinessId(businessId);
						businessNodeRelation.setNodeId(nodeId);
						businessNodeRelationRepository.save(businessNodeRelation);
					}
				}
			} else if (firstChar == '[') {
				// 多条策略
				JSONArray businessesArr = businesses.getJSONArray("business");
				// 解析一条策略 strategy
				for (int i = 0; i < businessesArr.size(); i++) {
					JSONObject businessJson = businessesArr.getJSONObject(i);
					// 策略名称
					String businessName = businessJson.getString("name");
					boolean existedBusiness = this.validateBusinessExists(businessName);
					if (existedBusiness) {
						throw new AdminException("该业务策略" + businessName + "已存在，请重新上传");
					}
					Business business = new Business();
					business.setName(businessName);
					business.setInUsing(0);
					business.setCreateTime(new Date());
					businessRepository.save(business);
					// 节点名称
					String nodes = businessJson.getString("nodes");
					if (!nodes.contains(",")) {
						BusinessNode existBusinessNode = businessNodeRepository.findOneByName(nodes);
						if (null == existBusinessNode) {
							throw new AdminException("上传失败，该节点不存在");
						}
						existBusinessNode.setInUsing(1);
						Business businessInfo = businessRepository.findOneByName(businessName);
						String businessId = businessInfo.getId();
						String nodeId = existBusinessNode.getId();

						BusinessNodeRelation businessNodeRelation = new BusinessNodeRelation();
						businessNodeRelation.setBusinessId(businessId);
						businessNodeRelation.setNodeId(nodeId);
						businessNodeRelationRepository.save(businessNodeRelation);
					} else {
						String[] nodeNamesArr = nodes.split(",");
						for (String nodeName : nodeNamesArr) {
							BusinessNode existBusinessNode = businessNodeRepository.findOneByName(nodeName);
							if (null == existBusinessNode) {
								throw new AdminException("上传失败，该节点不存在");
							}
							existBusinessNode.setInUsing(1);
							Business businessInfo = businessRepository.findOneByName(businessName);
							String businessId = businessInfo.getId();
							String nodeId = existBusinessNode.getId();

							BusinessNodeRelation businessNodeRelation = new BusinessNodeRelation();
							businessNodeRelation.setBusinessId(businessId);
							businessNodeRelation.setNodeId(nodeId);
							businessNodeRelationRepository.save(businessNodeRelation);
						}
					}
				}
			}

		}

		return true;
	}

	@Override
	public List<Map<String, String>> businessInfo(String businessId) {
		Business business = businessRepository.findOne(businessId);
		if (null == business) {
			return null;
		}
		List<BusinessNodeRelation> businessNodeRelations = businessNodeRelationRepository
				.findAllByBusinessId(businessId);
		Map<String, String> nodeInfo = new HashMap<>();
		List<Map<String, String>> nodeInfoList = new ArrayList<>();
		for (BusinessNodeRelation businessNodeRelation : businessNodeRelations) {
			nodeInfo = new HashMap<>();
			String nodeId = businessNodeRelation.getNodeId();
			BusinessNode node = businessNodeRepository.findOne(nodeId);
			String nodeName = node.getName();
			nodeInfo.put("nodeId", nodeId);
			nodeInfo.put("nodeName", nodeName);
			nodeInfoList.add(nodeInfo);
		}
		return nodeInfoList;
	}

}
