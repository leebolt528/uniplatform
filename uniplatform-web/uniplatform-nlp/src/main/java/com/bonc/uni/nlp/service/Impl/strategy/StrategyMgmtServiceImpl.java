package com.bonc.uni.nlp.service.Impl.strategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.nlp.constant.DicStatusConstant;
import com.bonc.uni.nlp.dao.DicFuncRelationRepository;
import com.bonc.uni.nlp.dao.DicSubTypeForTypeRepository;
import com.bonc.uni.nlp.dao.DicSubTypeRepository;
import com.bonc.uni.nlp.dao.DicTypeRepository;
import com.bonc.uni.nlp.dao.DictionaryRepository;
import com.bonc.uni.nlp.dao.FuncitonRepository;
import com.bonc.uni.nlp.dao.rule.RuleRepository;
import com.bonc.uni.nlp.dao.strategy.AlgorithmRepository;
import com.bonc.uni.nlp.dao.strategy.BusinessNodeRelationRepository;
import com.bonc.uni.nlp.dao.strategy.BusinessNodeRepository;
import com.bonc.uni.nlp.dao.strategy.BusinessRepository;
import com.bonc.uni.nlp.dao.strategy.FunctionDependFunctionRepository;
import com.bonc.uni.nlp.dao.strategy.ModelRepository;
import com.bonc.uni.nlp.dao.strategy.StrategyDependStrategyRepository;
import com.bonc.uni.nlp.dao.strategy.StrategyDicRelationRepository;
import com.bonc.uni.nlp.dao.strategy.StrategyNodeRelationRepository;
import com.bonc.uni.nlp.dao.strategy.StrategyRepository;
import com.bonc.uni.nlp.entity.dic.DicSubType;
import com.bonc.uni.nlp.entity.dic.DicSubTypeForTypeRelation;
import com.bonc.uni.nlp.entity.dic.DicType;
import com.bonc.uni.nlp.entity.dic.Dictionary;
import com.bonc.uni.nlp.entity.dic.Function;
import com.bonc.uni.nlp.entity.rule.Rule;
import com.bonc.uni.nlp.entity.strategy.Algorithm;
import com.bonc.uni.nlp.entity.strategy.Business;
import com.bonc.uni.nlp.entity.strategy.BusinessNode;
import com.bonc.uni.nlp.entity.strategy.BusinessNodeRelation;
import com.bonc.uni.nlp.entity.strategy.FunctionDependFunction;
import com.bonc.uni.nlp.entity.strategy.Model;
import com.bonc.uni.nlp.entity.strategy.Strategy;
import com.bonc.uni.nlp.entity.strategy.StrategyConstant;
import com.bonc.uni.nlp.entity.strategy.StrategyDependStrategy;
import com.bonc.uni.nlp.entity.strategy.StrategyDicRelation;
import com.bonc.uni.nlp.entity.strategy.StrategyNodeRelation;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.Impl.DicStatusServiceImpl;
import com.bonc.uni.nlp.service.strategy.IStrategyMgmtService;
import com.bonc.uni.nlp.utils.MyXmlUtil;
import com.bonc.uni.nlp.utils.redis.publisher.StrategyPublisher;
import com.bonc.usdp.odk.common.string.StringUtil;

/**
 * @author : GaoQiuyuer
 * @version: 2017年11月2日 下午4:43:17
 */
@Service
public class StrategyMgmtServiceImpl implements IStrategyMgmtService {

	@Autowired
	FuncitonRepository functionRepository;
	@Autowired
	StrategyRepository strategyRepository;
	@Autowired
	AlgorithmRepository algorithmRepository;
	@Autowired
	DictionaryRepository dictionaryRepository;
	@Autowired
	DicFuncRelationRepository dicFuncRelationRepository;
	@Autowired
	StrategyDicRelationRepository strategyDicRelationRepository;
	@Autowired
	ModelRepository modelRepository;
	@Autowired
	RuleRepository ruleRepository;
	@Autowired
	DicTypeRepository dicTypeRepository;
	@Autowired
	FunctionDependFunctionRepository functionDependFunctionRepository;
	@Autowired
	DicSubTypeRepository dicSubTypeRepository;
	@Autowired
	StrategyDependStrategyRepository strategyDependStrategyRepository;
	@Autowired
	DicSubTypeForTypeRepository dicSubTypeForTypeRepository;
	@Autowired
	StrategyNodeRelationRepository strategyNodeRelationRepository;
	@Autowired
	BusinessNodeRepository businessNodeRepository;
	@Autowired
	BusinessNodeRelationRepository businessNodeRelationRepository;
	@Autowired
	BusinessRepository businessRepository;

	@Autowired
	DicStatusServiceImpl dicStatusServiceImpl;

	@Override
	public List<Object> listAllStrategies(String functionId, int pageNumber, int pageSize, boolean ascSort) {
		Sort sort = null;
		if (ascSort) {
			sort = new Sort(Sort.Direction.ASC, "createTime");
		} else {
			sort = new Sort(Sort.Direction.DESC, "createTime");
		}
		Pageable pageable = new PageRequest(pageNumber - 1, pageSize, sort);
		List<Strategy> strategiesInfo = new ArrayList<>();
		List<Object> strategiesInfoList = new ArrayList<>();
		Map<String, Object> strategiesInfoMap = new HashMap<>();
		if ("all".equals(functionId)) {
			strategiesInfo = strategyRepository.findAll(pageable).getContent();
		} else {
			strategiesInfo = strategyRepository.findAllByFunctionId(functionId, pageable);
		}
		for (Strategy strategy : strategiesInfo) {
			strategiesInfoMap = new HashMap<>();
			String strategyId = strategy.getId();
			List<String> businessNames = new ArrayList<>();
			List<String> nodeNames = new ArrayList<>();
			if (1 == strategy.getInUsing()) {
				// 该功能策略被那些业务策略使用
				List<StrategyNodeRelation> strategyNodeRelations = strategyNodeRelationRepository
						.findAllByStrategyId(strategyId);
				if (null == strategyNodeRelations) {
					continue;
				}
				for (StrategyNodeRelation strategyNodeRelation : strategyNodeRelations) {
					BusinessNode businessNode = businessNodeRepository.findOne(strategyNodeRelation.getNodeId());
					if (null == businessNode) {
						continue;
					}
					if (1 == businessNode.getInUsing()) {
						List<BusinessNodeRelation> businessNodeRelations = businessNodeRelationRepository
								.findAllByNodeId(businessNode.getId());
						for (BusinessNodeRelation businessNodeRelation : businessNodeRelations) {
							Business business = businessRepository.findOne(businessNodeRelation.getBusinessId());
							if (!businessNames.contains(business.getName())) {
								businessNames.add(business.getName());
							}
						}
					} else {
						nodeNames.add(businessNode.getName());
					}
				}
			}
			strategiesInfoMap.put("businessNames", businessNames);
			strategiesInfoMap.put("nodeNames", nodeNames);
			String funId = strategy.getFunctionId();
			Function function = functionRepository.getOne(funId);
			String funName = function.getDisplayName();
			strategiesInfoMap.put("strategy", strategy);
			strategiesInfoMap.put("functionName", funName);
			strategiesInfoList.add(strategiesInfoMap);
		}

		return strategiesInfoList;
	}

	@Override
	public int getStrategiesNumber(String functionId) {
		int strategiesNumber = 0;
		if ("all".equals(functionId)) {
			strategiesNumber = (int) strategyRepository.count();
		} else {
			strategiesNumber = strategyRepository.count(functionId);
		}

		return strategiesNumber;
	}

	@Override
	public List<Object> listAllStrategiesInfo() {
		List<Strategy> listAllStrategiesInfo = strategyRepository.findAll();
		List<Object> strategiesInfoList = new ArrayList<>();
		Map<String, Object> strategiesInfoMap = new HashMap<>();
		for (Strategy strategy : listAllStrategiesInfo) {
			strategiesInfoMap = new HashMap<>();
			String funId = strategy.getFunctionId();
			Function function = functionRepository.getOne(funId);
			String funName = function.getDisplayName();
			strategiesInfoMap.put("strategy", strategy);
			strategiesInfoMap.put("functionName", funName);
			strategiesInfoList.add(strategiesInfoMap);
		}
		return strategiesInfoList;
	}

	@Override
	public List<Map<String, String>> listAllFunctions(boolean ascSort) {
		Sort sort = null;
		if (ascSort) {
			sort = new Sort(Sort.Direction.ASC, "index");
		} else {
			sort = new Sort(Sort.Direction.DESC, "index");
		}
		List<Function> functions = functionRepository.findAll(sort);
		List<Map<String, String>> functionsList = new ArrayList<>();
		Map<String, String> allFunctions = new HashMap<>();
		allFunctions.put("id", "all");
		allFunctions.put("displayName", "全部");
		functionsList.add(allFunctions);
		for (Function function : functions) {
			allFunctions = new HashMap<>();
			String id = function.getId();
			String displayName = function.getDisplayName();
			allFunctions.put("id", id);
			allFunctions.put("displayName", displayName);
			functionsList.add(allFunctions);
		}

		return functionsList;
	}

	@Override
	public boolean validateStrategyExists(String strategyName) {
		Strategy existedStrategy = strategyRepository.findOneByName(strategyName);

		if (null == existedStrategy)
			return false;

		return true;
	}

	@Override
	public List<Algorithm> listAlgorithm(String functionId) {
		List<Algorithm> algorithmsInfo = algorithmRepository.findAllByFunctionId(functionId);
		return algorithmsInfo;
	}

	@Override
	public List<Model> listModel(String algorithmId) {
		List<Model> models = modelRepository.findAllByAlgorithmId(algorithmId);
		return models;
	}

	@Override
	public List<Rule> listRule(String algorithmId) {
		List<Rule> rules = ruleRepository.findAllByAlgorithmId(algorithmId);
		return rules;
	}

	@Override
	public List<Object> listDependencyStrategy(String functionId) {
		List<Object> strategyList = new ArrayList<>();
		Map<String, Object> strategyMap = new HashMap<>();
		List<FunctionDependFunction> functionDependFunctions = functionDependFunctionRepository
				.findAllByFunctionId(functionId);
		String functionName = null;
		List<Object> dependencyStrategys = new ArrayList<>();
		Map<String, Object> dependencyStrategyMap = new HashMap<>();
		for (FunctionDependFunction functionDependFunction : functionDependFunctions) {
			String dependsFunctionId = functionDependFunction.getDependFunctionId();
			Function dependFunctions = functionRepository.findById(dependsFunctionId);
			functionName = dependFunctions.getDisplayName();
			List<Strategy> dependencyStrategy = strategyRepository.findAllByFunctionId(dependsFunctionId);
			for (Strategy strategy : dependencyStrategy) {
				dependencyStrategyMap = new HashMap<>();
				dependencyStrategyMap.put("依赖策略名称", strategy.getName());
				dependencyStrategyMap.put("依赖策略id", strategy.getId());
				dependencyStrategys.add(dependencyStrategyMap);
			}
			strategyMap.put("dependencyStrategys", dependencyStrategys);
			strategyMap.put("functionName", functionName);
		}
		strategyList.add(strategyMap);

		return strategyList;
	}

	@Override
	public List<DicType> listDicTypes() {
		List<DicType> dicTypes = dicTypeRepository.findAll();
		return dicTypes;
	}

	@Override
	public List<Object> listDic(String functionId, boolean ascSort) {
		Function function = functionRepository.findOne(functionId);
		List<Object> infoList = new ArrayList<>();
		Map<String, Object> infoMap = new HashMap<>();
		// 分词功能(普通-分词；功能-停用词；领域-分词)
		if ("segment".equals(function.getName())) {
			List<DicType> dicTypes = dicTypeRepository.findAll();
			for (DicType dicType : dicTypes) {
				List<Map<String, Object>> dicInfoList = new ArrayList<>();
				Map<String, Object> dicInfoMap = new HashMap<>();
				infoMap = new HashMap<>();
				DicSubType dicSubType = new DicSubType();
				String dicTypeId = dicType.getId();
				if ("function".equals(dicType.getName())) {
					dicSubType = dicSubTypeRepository.findOneByName("stopWords");
				} else {
					dicSubType = dicSubTypeRepository.findOneByName("seg");
				}
				dicInfoMap = new HashMap<>();
				List<Map<String, String>> dicList = new ArrayList<>();
				Map<String, String> dicMap = new HashMap<>();
				dicInfoMap.put("dicSubTypeName", dicSubType.getDisPlayName());
				List<Dictionary> dictionaries = dictionaryRepository.findAllByDicTypeIdAndDicSubTypeId(dicTypeId,
						dicSubType.getId());
				for (Dictionary dictionary : dictionaries) {
					dicMap = new HashMap<>();
					dicMap.put("dicId", dictionary.getId());
					dicMap.put("dicName", dictionary.getName());
					dicList.add(dicMap);
				}
				dicInfoMap.put("词典列表", dicList);
				dicInfoList.add(dicInfoMap);
				infoMap.put("dicTypeName", dicType.getDisplayName());
				infoMap.put("dicList", dicInfoList);
				infoList.add(infoMap);
			}
		} else {
			// 只在功能词库中选择
			List<Map<String, Object>> dicInfoList = new ArrayList<>();
			Map<String, Object> dicInfoMap = new HashMap<>();
			DicType dicType = dicTypeRepository.findOneByName("function");
			infoMap = new HashMap<>();
			infoMap.put("dicTypeName", dicType.getDisplayName());
			String dicTypeId = dicType.getId();
			List<DicSubTypeForTypeRelation> dicSubTypes = dicSubTypeForTypeRepository.findAllByTypeId(dicTypeId);
			for (DicSubTypeForTypeRelation dicSubTypeRelation : dicSubTypes) {
				DicSubType dicSubType = dicSubTypeRepository.findOne(dicSubTypeRelation.getDicSubTypeId());
				dicInfoMap = new HashMap<>();
				List<Map<String, String>> dicList = new ArrayList<>();
				Map<String, String> dicMap = new HashMap<>();
				dicInfoMap.put("dicSubTypeName", dicSubType.getDisPlayName());
				List<Dictionary> dictionaries = dictionaryRepository.findAllByDicTypeIdAndDicSubTypeId(dicTypeId,
						dicSubType.getId());
				for (Dictionary dictionary : dictionaries) {
					dicMap = new HashMap<>();
					dicMap.put("dicId", dictionary.getId());
					dicMap.put("dicName", dictionary.getName());
					dicList.add(dicMap);
				}
				dicInfoMap.put("词典列表", dicList);
				dicInfoList.add(dicInfoMap);
			}
			infoMap.put("dicList", dicInfoList);
			infoList.add(infoMap);
		}

		return infoList;
	}

	@Override
	public int addStrategy(String strategyName, String functionId, String algorithmId, String modelId, String ruleId,
			String dictsId, Integer batch, String dependencyStrategyId) {
		Function existedFunction = functionRepository.findOne(functionId);
		Algorithm existedAlgorithm = algorithmRepository.findOne(algorithmId);

		if (null == existedFunction || null == existedAlgorithm)
			return 0;

		if (StringUtil.isEmpty(strategyName) || validateStrategyExists(strategyName))
			return 2;

		if (!StringUtil.isEmpty(dependencyStrategyId)) {
			Strategy dependencyStrategy = strategyRepository.findOne(dependencyStrategyId);
			// 依赖策略状态修改为使用中
			dependencyStrategy.setInUsing(1);
			strategyRepository.save(dependencyStrategy);

			FunctionDependFunction functionDependFunction = functionDependFunctionRepository
					.findByFunctionId(functionId);
			String dependFunction = functionDependFunction.getDependFunctionId();
			if (!dependencyStrategy.getFunctionId().equals(dependFunction))
				return 3;
		}

		Strategy strategy = new Strategy();
		strategy.setName(strategyName);
		strategy.setFunctionId(functionId);
		strategy.setAlgorithmId(existedAlgorithm.getId());
		strategy.setCreateTime(new Date());
		strategy.setBatch(batch);
		// strategy.setUserId(CurrentUserUtils.getInstance().getUser().getId());

		Model existedmodel = null;
		if (!StringUtil.isEmpty(modelId)) {
			existedmodel = modelRepository.findOne(modelId);
			if (1 == existedAlgorithm.getHasModel()) {
				strategy.setModelId(existedmodel.getId());
				existedmodel.setInUsing(1);
			}
		}

		Rule existedrule = null;
		if (!StringUtil.isEmpty(ruleId)) {
			existedrule = ruleRepository.findOne(ruleId);
			if (1 == existedAlgorithm.getHasRule()) {
				strategy.setRuleId(existedrule.getId());
			}
		}

		if (1 == existedAlgorithm.getHasBatch()) {
			strategy.setBatch(batch);
		}
		strategy.setOperation("custom");
		strategy.setDefaultUse(0);
		strategy.setInUsing(0);
		strategyRepository.save(strategy);

		Strategy strategyNew = strategyRepository.findOneByName(strategyName);
		if (1 == existedAlgorithm.getHasDic()) {
			if (null != dictsId && 0 != dictsId.length()) {
				String[] arrDicIds = dictsId.split(",");
				List<String> dicIds = new ArrayList<>();
				for (String dicId : arrDicIds) {
					dicIds.add(dicId);
				}

				for (String dicId : dicIds) {
					StrategyDicRelation strategyDicRelation = new StrategyDicRelation();
					strategyDicRelation.setDicId(dicId);
					strategyDicRelation.setStrategyId(strategyNew.getId());
					strategyDicRelationRepository.save(strategyDicRelation);

					Dictionary dictionary = dictionaryRepository.findOne(dicId);
					dictionary.setStatus(2);
					dictionaryRepository.save(dictionary);
				}
			}
		}

		if (1 == existedAlgorithm.getHasDependency()) {
			if (!StringUtil.isEmpty(dependencyStrategyId)) {
				StrategyDependStrategy strategyDependStrategy = new StrategyDependStrategy();
				strategyDependStrategy.setStrategyId(strategyNew.getId());
				strategyDependStrategy.setDependStrategyId(dependencyStrategyId);

				strategyDependStrategyRepository.save(strategyDependStrategy);
			}
		}

		return 1;
	}

	@Override
	public int editStrategy(String strategyId, String strategyNewName, String algorithmNewId, String modelId,
			String ruleId, String dictsNewId, Integer batch, String dependencyStrategiesId) {
		Strategy strategy = strategyRepository.findOne(strategyId);
		if (null == strategy) {
			return 0;
		}

		// 先向processor发送消息，删除缓存的策略 TODO
		String strategyName = strategy.getName();
		StrategyPublisher publisher = new StrategyPublisher();
		publisher.publishStrategyDelMsg(strategyName);

		Algorithm algorithm = algorithmRepository.findOne(algorithmNewId);
		if (StrategyConstant.STRATEGY_TYPE_SYSTEM.equals(strategy.getOperation()) || 1 == strategy.getDefaultUse()
				|| 1 == strategy.getInUsing()) {
			return 2;
		}
		// BUG
		if (!strategyNewName.equals(strategy.getName()) && validateStrategyExists(strategyNewName)
				|| StringUtil.isEmpty(strategyNewName))
			return 3;

		strategy.setName(strategyNewName);

		if (null != algorithm) {
			strategy.setAlgorithmId(algorithmNewId);
		}

		int hasModels = algorithm.getHasModel();
		if (1 == hasModels && null != modelRepository.findOne(modelId)) {
			strategy.setModelId(modelId);
		} else {
			strategy.setModelId(null);
		}

		int hasRules = algorithm.getHasRule();
		if (1 == hasRules && null != ruleRepository.findOne(ruleId)) {
			strategy.setRuleId(ruleId);
		} else {
			strategy.setRuleId(null);
		}

		int hasBatch = algorithm.getHasBatch();
		if (1 == hasBatch) {
			strategy.setBatch(batch);
		}

		int hasDicts = algorithm.getHasDic();
		if (1 == hasDicts) {
			List<StrategyDicRelation> strategyDicRelations = strategyDicRelationRepository
					.findAllByStrategyId(strategyId);
			if (!StringUtil.isEmpty(dictsNewId)) {
				// 新添加的词典id
				String[] arrDicIds = dictsNewId.split(",");
				List<String> dicNewIds = new ArrayList<>();
				for (String dicId : arrDicIds) {
					dicNewIds.add(dicId);
				}
				// 数据库要删除的词典id
				List<String> diffDel = new ArrayList<String>();
				List<String> dicIdCurrentList = new ArrayList<>();
				for (StrategyDicRelation strategyDicRelation : strategyDicRelations) {
					dicIdCurrentList.add(strategyDicRelation.getDicId());
				}
				for (String dicCurrentId : dicIdCurrentList) {
					if (!dicNewIds.contains(dicCurrentId)) {
						diffDel.add(dicCurrentId);
					}
				}
				// 数据库要添加的词典id
				List<String> diffAdd = new ArrayList<String>();
				for (String dicNewId : dicNewIds) {
					if (!dicIdCurrentList.contains(dicNewId)) {
						diffAdd.add(dicNewId);
					}
				}
				for (String dicId : diffAdd) {
					StrategyDicRelation strategyDicRelation = new StrategyDicRelation();
					strategyDicRelation.setDicId(dicId);
					strategyDicRelation.setStrategyId(strategyId);

					strategyDicRelationRepository.save(strategyDicRelation);

					Dictionary dictionary = dictionaryRepository.findOne(dicId);
					dictionary.setStatus(2);
				}
				for (String dicId : diffDel) {
					StrategyDicRelation strategyDic = strategyDicRelationRepository.findOneByDicIdAndStrategyId(dicId,
							strategyId);
					strategyDicRelationRepository.delete(strategyDic.getId());
					/**
					 * 更改词典状态（in_using） : true 无应用 false 有应用
					 * 查看有没有别的策略在使用该词库，如果没有则将该词库状态置为0
					 */
					boolean dicStatus = dicStatusServiceImpl.dicIfStartExceptbyName(DicStatusConstant.STRATEGY_DIC,
							dicId);
					Dictionary dictionary = dictionaryRepository.findOne(dicId);
					List<StrategyDicRelation> strategyDicRelationsInfo = new ArrayList<>();
					strategyDicRelationsInfo = strategyDicRelationRepository.findAllByDicId(dicId);
					if (dicStatus && strategyDicRelationsInfo.isEmpty()) {
						if (dicStatusServiceImpl.functionIfCandidateDic(dicId)) {
							dictionary.setStatus(0);
						} else {
							dictionary.setStatus(1);
						}
					} else {
						dictionary.setStatus(2);
					}
					dictionaryRepository.save(dictionary);
				}
			} else if (StringUtil.isEmpty(dictsNewId)) {
				List<String> dicIdCurrentList = new ArrayList<>();
				for (StrategyDicRelation strategyDicRelation : strategyDicRelations) {
					dicIdCurrentList.add(strategyDicRelation.getDicId());
				}
				if (dicIdCurrentList.size() > 0) {
					for (String dicId : dicIdCurrentList) {
						StrategyDicRelation strategyDic = strategyDicRelationRepository
								.findOneByDicIdAndStrategyId(dicId, strategyId);
						strategyDicRelationRepository.delete(strategyDic.getId());
						/**
						 * 更改词典状态（in_using） : true 无应用 false 有应用
						 * 查看有没有别的策略在使用该词库，如果没有则将该词库状态置为0
						 */
						boolean dicStatus = dicStatusServiceImpl.dicIfStartExceptbyName(DicStatusConstant.STRATEGY_DIC,
								dicId);
						Dictionary dictionary = dictionaryRepository.findOne(dicId);
						List<StrategyDicRelation> strategyDicRelationsInfo = new ArrayList<>();
						strategyDicRelationsInfo = strategyDicRelationRepository.findAllByDicId(dicId);
						if (dicStatus && strategyDicRelationsInfo.isEmpty()) {
							if (dicStatusServiceImpl.functionIfCandidateDic(dicId)) {
								dictionary.setStatus(0);
							} else {
								dictionary.setStatus(1);
							}
						} else {
							dictionary.setStatus(2);
						}
						dictionaryRepository.save(dictionary);
					}
				}
			}
		}

		int hasDependency = algorithm.getHasDependency();
		if (1 == hasDependency) {
			if (null != dependencyStrategiesId && 0 != dependencyStrategiesId.length()) {
				String[] ArrDependencyStrategiesId = dependencyStrategiesId.split(",");
				// 新的依赖策略id
				List<String> dependencyStrategiesIdNew = new ArrayList<>();
				for (String dependencyStrategyId : ArrDependencyStrategiesId) {
					dependencyStrategiesIdNew.add(dependencyStrategyId);
				}
				// 现在策略的依赖策略id
				List<StrategyDependStrategy> strategyDependStrategyList = strategyDependStrategyRepository
						.findByStrategyId(strategyId);
				List<String> dependStrategyIdCurrent = new ArrayList<>();
				for (StrategyDependStrategy strategyDepend : strategyDependStrategyList) {
					String depengdStrategyId = strategyDepend.getDependStrategyId();
					dependStrategyIdCurrent.add(depengdStrategyId);
				}
				List<String> delDependStra = new ArrayList<>();
				for (String dependCurrent : dependStrategyIdCurrent) {
					if (!dependencyStrategiesIdNew.contains(dependCurrent)) {
						delDependStra.add(dependCurrent);
					}
				}
				List<String> addDependStra = new ArrayList<>();
				for (String dependNew : dependencyStrategiesIdNew) {
					if (!dependStrategyIdCurrent.contains(dependNew)) {
						addDependStra.add(dependNew);
					}
				}
				// 删除的依赖策略关联
				for (String delDependId : delDependStra) {
					if (!StringUtil.isEmpty(delDependId)) {
						StrategyDependStrategy strategyDependStrategy = strategyDependStrategyRepository
								.findOneByDependStrategyIdAndStrategyId(delDependId, strategyId);
						String dependencyStrategyId = strategyDependStrategy.getId();
						strategyDependStrategyRepository.delete(dependencyStrategyId);
					}
				}
				// 新增的依赖策略关联
				for (String addDependId : addDependStra) {
					if (!StringUtil.isEmpty(addDependId)) {
						// 修改依赖策略状态
						Strategy dependencyStrategy = strategyRepository.findOne(addDependId);
						dependencyStrategy.setInUsing(1);
						strategyRepository.save(dependencyStrategy);

						StrategyDependStrategy strategyDependStrategy = new StrategyDependStrategy();
						strategyDependStrategy.setStrategyId(strategyId);
						strategyDependStrategy.setDependStrategyId(addDependId);
						strategyDependStrategyRepository.save(strategyDependStrategy);
					}
				}
			} else if (StringUtil.isEmpty(dependencyStrategiesId)) {
				// 现在策略的依赖策略id
				List<StrategyDependStrategy> strategyDependStrategyList = strategyDependStrategyRepository
						.findByStrategyId(strategyId);
				List<String> dependStrategyIdCurrent = new ArrayList<>();
				for (StrategyDependStrategy strategyDepend : strategyDependStrategyList) {
					String depengdStrategyId = strategyDepend.getDependStrategyId();
					dependStrategyIdCurrent.add(depengdStrategyId);
				}
				if (dependStrategyIdCurrent.size() > 0) {
					for (String delDependId : dependStrategyIdCurrent) {
						if (!StringUtil.isEmpty(delDependId)) {
							StrategyDependStrategy strategyDependStrategy = strategyDependStrategyRepository
									.findOneByDependStrategyIdAndStrategyId(delDependId, strategyId);
							String dependencyStrategyId = strategyDependStrategy.getId();
							strategyDependStrategyRepository.delete(dependencyStrategyId);

							// 修改依赖策略状态
							List<StrategyDependStrategy> haStrategies = strategyDependStrategyRepository
									.findAllByStrategyId(delDependId);
							if (haStrategies.size()>0) {
								Strategy dependencyStrategy = strategyRepository.findOne(delDependId);
								dependencyStrategy.setInUsing(0);
								strategyRepository.save(dependencyStrategy);
							}
						}
					}
				}
			}
		}

		strategyRepository.save(strategy);
		return 1;
	}

	@Override
	@Transactional
	public boolean deleteStrategy(String strategyIds) {
		String[] arrStrategyIds = strategyIds.split(",");
		List<Strategy> strategies = strategyRepository.findAllByIdIn(arrStrategyIds);
		if (null == strategies || 0 == strategies.size()) {
			throw new AdminException("删除失败，该策略不存在");
		}
		for (Strategy strategy : strategies) {
			if (StrategyConstant.STRATEGY_TYPE_SYSTEM.equals(strategy.getOperation())) {
				continue;
			}
			if (1 == strategy.getDefaultUse()) {
				continue;
			}
			if (1 == strategy.getInUsing()) {
				continue;
			}
			String strategyId = strategy.getId();
			/**
			 * 删除策略词库表相关数据
			 */
			List<StrategyDicRelation> strategyDicRelations = strategyDicRelationRepository
					.findAllByStrategyId(strategyId);
			if (!strategyDicRelations.isEmpty()) {
				for (StrategyDicRelation strategyDicRelation : strategyDicRelations) {
					strategyDicRelationRepository.delete(strategyDicRelation.getId());
					/**
					 * 更改词典状态（in_using） : true 无应用 false 有应用
					 * 查看有没有别的策略在使用该词库，如果没有则将该词库状态置为0
					 */
					String dicId = strategyDicRelation.getDicId();
					if (null != dicId) {
						boolean dicStatus = dicStatusServiceImpl.dicIfStartExceptbyName(DicStatusConstant.STRATEGY_DIC,
								dicId);
						Dictionary dictionary = dictionaryRepository.findOne(dicId);
						List<StrategyDicRelation> strategyDicRelationsInfo = new ArrayList<>();
						strategyDicRelationsInfo = strategyDicRelationRepository.findAllByDicId(dicId);
						if (dicStatus && strategyDicRelationsInfo.isEmpty()) {
							if (dicStatusServiceImpl.functionIfCandidateDic(dicId)) {
								dictionary.setStatus(0);
							} else {
								dictionary.setStatus(1);
							}
						} else {
							dictionary.setStatus(2);
						}
						dictionaryRepository.save(dictionary);
					}
				}
			}
			/**
			 * 删除策略依赖表相关数据
			 */
			List<StrategyDependStrategy> strategyDependStrategys = strategyDependStrategyRepository
					.findAllByStrategyId(strategyId);
			List<String> dependIds = new ArrayList<>();
			if (!strategyDependStrategys.isEmpty()) {
				for (StrategyDependStrategy strategyDependStrategy : strategyDependStrategys) {
					dependIds.add(strategyDependStrategy.getDependStrategyId());
					strategyDependStrategyRepository.delete(strategyDependStrategy.getId());
				}
			}
			// 修改依赖策略状态
			for (String dependId : dependIds) {
				// 修改依赖策略状态
				List<StrategyDependStrategy> haStrategies = strategyDependStrategyRepository
						.findAllByStrategyId(dependId);
				if (haStrategies.size() == 0) {
					Strategy dependencyStrategy = strategyRepository.findOne(dependId);
					dependencyStrategy.setInUsing(0);
					strategyRepository.save(dependencyStrategy);
				}
			}
		
			strategyRepository.delete(strategyId);

			String strategyName = strategy.getName();
			StrategyPublisher publisher = new StrategyPublisher();
			publisher.publishStrategyDelMsg(strategyName);
		}

		return true;
	}

	@Override
	public boolean setAsDefault(String strategyId) {
		Strategy strategy = strategyRepository.findOne(strategyId);
		String functionId = strategy.getFunctionId();
		Strategy beforeDefaultStra = strategyRepository.findOneByFunctionIdAndDefaultUse(functionId, 1);
		/**
		 * 将之前设为默认的策略取消默认
		 */
		if (null != beforeDefaultStra) {
			beforeDefaultStra.setDefaultUse(0);
			strategyRepository.save(beforeDefaultStra);
		}
		strategy.setDefaultUse(1);
		strategyRepository.save(strategy);

		return true;
	}

	@Override
	public String exportStrategies2XMLString(String strategiesId) {
		String[] arrStrategyIds = strategiesId.split(",");
		StringBuilder xmlContent = new StringBuilder();
		xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		xmlContent.append("<" + StrategyConstant.XML_STRATEGY_ROOT + ">\r\n");
		List<Strategy> strategies = strategyRepository.findAllByIdIn(arrStrategyIds);
		for (Strategy strategy : strategies) {
			xmlContent.append("	<" + StrategyConstant.XML_STRATEGY + ">\r\n");
			/**
			 * 策略名称
			 */
			String name = strategy.getName();
			xmlContent.append("		<" + StrategyConstant.XML_STRATEGY_NAME + ">" + name + "</"
					+ StrategyConstant.XML_STRATEGY_NAME + ">\r\n");
			/**
			 * 策略所属功能名称
			 */
			String functionId = strategy.getFunctionId();
			Function functionInfo = functionRepository.findOne(functionId);
			String function = functionInfo.getDisplayName();
			xmlContent.append("		<" + StrategyConstant.XML_STRATEGY_FUNCTION + ">" + function + "</"
					+ StrategyConstant.XML_STRATEGY_FUNCTION + ">\r\n");
			/**
			 * 策略所属算法名称
			 */
			String algorithmId = strategy.getAlgorithmId();
			Algorithm algorithmInfo = algorithmRepository.findOne(algorithmId);
			String algorithm = algorithmInfo.getDisplayName();
			xmlContent.append("		<" + StrategyConstant.XML_STRATEGY_ALGORITHM + ">" + algorithm + "</"
					+ StrategyConstant.XML_STRATEGY_ALGORITHM + ">\r\n");
			/**
			 * 策略的子策略名称
			 */
			if (1 == algorithmInfo.getHasDependency()) {
				String dependencyStrategy = "";
				String strategyId = strategy.getId();
				List<StrategyDependStrategy> dependencyStrategyInfo = strategyDependStrategyRepository
						.findByStrategyId(strategyId);
				if (null != dependencyStrategyInfo) {
					xmlContent.append("		<" + StrategyConstant.XML_STRATEGY_DEPENDENCY_STRATEGY + ">\r\n");
					for (StrategyDependStrategy strategyDependStrategy : dependencyStrategyInfo) {
						xmlContent.append("		  <dependencyStrategy>\r\n");
						FunctionDependFunction functionDependFunctionInfo = functionDependFunctionRepository
								.findByFunctionId(functionId);
						String functionDepenId = functionDependFunctionInfo.getDependFunctionId();
						Function fun = functionRepository.findOne(functionDepenId);
						String functionName = fun.getDisplayName();
						String dependencyStrategyId = strategyDependStrategy.getDependStrategyId();
						Strategy nextStrategyInfo = strategyRepository.findOne(dependencyStrategyId);
						if (null != nextStrategyInfo) {
							dependencyStrategy = nextStrategyInfo.getName();
						}
						xmlContent.append(
								"		    <dependencyFunctionName>" + functionName + "</dependencyFunctionName>\r\n");
						xmlContent.append("		    <dependencyStrategyName>" + dependencyStrategy
								+ "</dependencyStrategyName>\r\n");
						xmlContent.append("		  </dependencyStrategy>\r\n");
					}
					xmlContent.append("		</" + StrategyConstant.XML_STRATEGY_DEPENDENCY_STRATEGY + ">\r\n");
				}
			}
			/**
			 * 策略词库 (分为好多种 )
			 */
			int dicsInfo = algorithmInfo.getHasDic();
			if (1 == dicsInfo) {
				Map<String, Object> singleStrategyInfoMap = this.getSingleStrategyInfo(strategy.getId());
				List<Map<String, String>> singleStrategyInfo = (List<Map<String, String>>) singleStrategyInfoMap
						.get("dicInfo");
				List<Map<String, Map<String, List<String>>>> allDicList = new ArrayList<>();
				Map<String, Map<String, List<String>>> dicMap = new HashMap<>();
				Map<String, List<String>> dicSubMap = new HashMap<>();
				List<String> dicNames = new ArrayList<>();
				for (Map<String, String> singleStrategyMap : singleStrategyInfo) {
					dicMap = new HashMap<>();
					dicSubMap = new HashMap<>();
					String dicTypeName = singleStrategyMap.get("dicTypeName");
					String dicSubTypeName = singleStrategyMap.get("dicSubName");
					String dicName = singleStrategyMap.get("dicName");
					if (dicSubMap.containsKey(dicSubTypeName)) {
						dicNames.add(dicName);
						dicSubMap.put(dicSubTypeName, dicNames);
					} else {
						// 如果没有,new一个新的list
						dicNames = new ArrayList<>();
						dicNames.add(dicName);
						dicSubMap.put(dicSubTypeName, dicNames);
					}
					dicMap.put(dicTypeName, dicSubMap);
					allDicList.add(dicMap);
				}
				// 循环合并key相同的value
				Map<String, List<Map<String, List<String>>>> newMap = new HashMap<>();
				List<Map<String, List<String>>> newList = new ArrayList<>();
				for (Map<String, Map<String, List<String>>> dicInfo : allDicList) {
					for (Entry<String, Map<String, List<String>>> vo : dicInfo.entrySet()) {
						// key相同时候，value放进list里
						String typeName = vo.getKey();
						if (newMap.isEmpty() || newMap.containsKey(typeName)) {
							newList.add(vo.getValue());
							newMap.put(typeName, newList);
						} else {
							newList = new ArrayList<>();
							newList.add(vo.getValue());
							newMap.put(typeName, newList);
						}
					}
				}

				for (Entry<String, List<Map<String, List<String>>>> vo : newMap.entrySet()) {
					String dicTypeName = vo.getKey();
					xmlContent.append("		<dicTypeInfos>\r\n");
					xmlContent.append("		  <dicTypeInfo>\r\n");
					xmlContent.append("		     <dicTypeName>" + dicTypeName + "</dicTypeName>\r\n");
					xmlContent.append("		     <dicSubTypeInfos>\r\n");
					List<Map<String, List<String>>> dicInfo = vo.getValue();
					for (Map<String, List<String>> dicSubInfo : dicInfo) {
						for (Entry<String, List<String>> dics : dicSubInfo.entrySet()) {
							xmlContent.append("		       <dicSubTypeInfo>\r\n");
							String dicSubName = dics.getKey();
							List<String> dicsName = dics.getValue();
							String dicNamesStr = "";
							for (String dicName : dicsName) {
								dicNamesStr += (dicName + ",");
							}
							dicNamesStr = dicNamesStr.substring(0, dicNamesStr.length() - 1);
							xmlContent.append("		         <dicSubTypeName>" + dicSubName + "</dicSubTypeName>\r\n");
							xmlContent.append("		         <dicNames>" + dicNamesStr + "</dicNames>\r\n");
							xmlContent.append("		       </dicSubTypeInfo>\r\n");
						}
					}
					xmlContent.append("		     </dicSubTypeInfos>\r\n");
					xmlContent.append("		  </dicTypeInfo>\r\n");
					xmlContent.append("		</dicTypeInfos>\r\n");
				}
			}
			/**
			 * 策略model
			 */
			String modelName = "";
			int hasModel = algorithmInfo.getHasModel();
			if (1 == hasModel) {
				String modelId = strategy.getModelId();
				Model model = modelRepository.findOne(modelId);
				if (null != model) {
					modelName = model.getName();
				}
			}
			xmlContent.append("		<" + StrategyConstant.XML_STRATEGY_MODEL + ">" + modelName + "</"
					+ StrategyConstant.XML_STRATEGY_MODEL + ">\r\n");
			/**
			 * 策略rule
			 */
			String rulelName = "";
			int hasRule = algorithmInfo.getHasRule();
			if (1 == hasRule) {
				String ruleId = strategy.getRuleId();
				Rule rule = ruleRepository.findOne(ruleId);
				if (null != rule) {
					rulelName = rule.getRule();
				}
			}
			xmlContent.append("		<" + StrategyConstant.XML_STRATEGY_RULE + ">" + rulelName + "</"
					+ StrategyConstant.XML_STRATEGY_RULE + ">\r\n");

			xmlContent.append("	</" + StrategyConstant.XML_STRATEGY + ">\r\n");
		}
		xmlContent.append("</" + StrategyConstant.XML_STRATEGY_ROOT + ">\r\n");
		return xmlContent.toString();

	}

	@Override
	public boolean importStrategies(MultipartFile[] files) {
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
				e.printStackTrace();
			}
			// 解析map
			String strategiesStr = JSON.toJSONString(xmlMap);
			JSONObject strategiesJ = JSONObject.parseObject(strategiesStr);
			if (null == strategiesJ) {
				throw new AdminException("上传失败，非法文件，没有策略信息");
			}
			JSONObject strategies = strategiesJ.getJSONObject("strategies");
			JSON strategyType = (JSON) strategies.get("strategy");
			if (null == strategyType) {
				throw new AdminException("上传失败，非法文件，没有策略信息");
			}
			// 判断类型 是否为list
			String strategiesJsonStr = JSON.toJSONString(strategyType);
			char[] strChar = strategiesJsonStr.substring(0, 1).toCharArray();
			char firstChar = strChar[0];
			if (firstChar == '{') {
				// 单条策略
				JSONObject strategyJson = strategies.getJSONObject("strategy");
				// 策略名称
				String strategyName = strategyJson.getString("name");
				if (null == strategyName || 0 == strategyName.length()) {
					throw new AdminException("上传失败，没有策略名");
				}
				Strategy strategy = new Strategy();
				// 策略名
				strategy.setOperation("custom");
				strategy.setName(strategyName);
				strategy.setCreateTime(new Date());
				boolean existedStrategy = this.validateStrategyExists(strategyName);
				if (existedStrategy) {
					throw new AdminException("上传失败，该策略名" + strategyName + "已存在");
				}
				String functionName = strategyJson.getString("function");
				if (null == functionName || 0 == functionName.length()) {
					throw new AdminException("上传失败，没有功能名");
				}
				// 功能
				Function function = functionRepository.findByDisplayName(functionName);
				if (null == function) {
					throw new AdminException("上传失败，该功能不存在");
				}
				String functionId = function.getId();
				strategy.setFunctionId(functionId);
				// 算法
				String algorithmName = strategyJson.getString("algorithm");
				if (null == algorithmName || 0 == algorithmName.length()) {
					throw new AdminException("上传失败，没有算法名");
				}
				Algorithm existAlgorithm = algorithmRepository.findOneByDisplayName(algorithmName);
				if (null == existAlgorithm) {
					throw new AdminException("上传失败，该算法不存在");
				}
				String algorithmId = existAlgorithm.getId();
				strategy.setAlgorithmId(algorithmId);
				// 模型
				String modelName = strategyJson.getString("modelName");
				if (null != modelName) {
					Model existModel = modelRepository.findOneByNameAndAlgorithmId(modelName, algorithmId);
					if (null != existModel) {
						String modelId = existModel.getId();
						strategy.setModelId(modelId);
					}
				}
				// 规则
				String ruleName = strategyJson.getString("ruleName");
				if (null != ruleName) {
					Rule existRule = ruleRepository.findOneByRule(ruleName);
					if (null != existRule) {
						Rule rule = ruleRepository.findOneByRuleAndAlgorithmId(ruleName, algorithmId);
						String ruleId = rule.getId();
						strategy.setRuleId(ruleId);
					}
				}
				// 批处理
				int existBatch = strategyJson.getIntValue("batch");
				String batchS = Integer.toString(existBatch);
				if (0 != batchS.length()) {
					strategy.setBatch(existBatch);
				}
				strategyRepository.save(strategy);
				// 保存策略信息，获取该条策略id
				Strategy strategyInfo = strategyRepository.findOneByName(strategyName);
				String strategyId = strategyInfo.getId();
				// 依赖策略
				JSONObject dependencyStrategies = strategyJson.getJSONObject("dependencyStrategies");
				if (null != dependencyStrategies) {
					JSON dependencyStrategyJson = (JSON) dependencyStrategies.get("dependencyStrategy");
					String dependencyStrategyStr = JSON.toJSONString(dependencyStrategyJson);
					char[] deStrChar = dependencyStrategyStr.substring(0, 1).toCharArray();
					char deFirstChar = deStrChar[0];
					if (deFirstChar == '{') {
						// 单条依赖策略
						JSONObject dependencyStrategy = dependencyStrategies.getJSONObject("dependencyStrategy");
						String dependencyFunctionName = dependencyStrategy.getString("dependencyFunctionName");
						if (null == dependencyFunctionName) {
							throw new AdminException("上传失败，没有依赖功能");
						}
						Function functionDe = functionRepository.findByDisplayName(dependencyFunctionName);
						if (null == functionDe) {
							throw new AdminException("上传失败，该依赖功能不存在");
						}
						String dependencyStrategyName = dependencyStrategy.getString("dependencyStrategyName");
						Strategy strategyDepend = strategyRepository.findOneByName(dependencyStrategyName);
						if (null == strategyDepend) {
							throw new AdminException("上传失败，该依赖策略不存在");
						}
						String strategyDependId = strategyDepend.getId();
						StrategyDependStrategy strategyDependStrategy = new StrategyDependStrategy();
						strategyDependStrategy.setDependStrategyId(strategyDependId);
						strategyDependStrategy.setStrategyId(strategyId);
						strategyDependStrategyRepository.save(strategyDependStrategy);
					} else if (deFirstChar == '[') {
						// 多条依赖策略
						JSONArray dependencyStrategyArr = dependencyStrategies.getJSONArray("dependencyStrategy");
						for (int j = 0; j < dependencyStrategyArr.size(); j++) {
							JSONObject dependencyStrategy = dependencyStrategyArr.getJSONObject(j);
							String dependencyFunctionName = dependencyStrategy.getString("dependencyFunctionName");
							if (null == dependencyFunctionName) {
								throw new AdminException("上传失败，没有依赖功能");
							}
							Function functionDe = functionRepository.findByDisplayName(dependencyFunctionName);
							if (null == functionDe) {
								throw new AdminException("上传失败，该依赖功能不存在");
							}
							String dependencyStrategyName = dependencyStrategy.getString("dependencyStrategyName");
							Strategy strategyDepend = strategyRepository.findOneByName(dependencyStrategyName);
							if (null == strategyDepend) {
								throw new AdminException("上传失败，该依赖策略不存在");
							}
							String strategyDependId = strategyDepend.getId();
							StrategyDependStrategy strategyDependStrategy = new StrategyDependStrategy();
							strategyDependStrategy.setDependStrategyId(strategyDependId);
							strategyDependStrategy.setStrategyId(strategyId);
							strategyDependStrategyRepository.save(strategyDependStrategy);
						}
					}
				}
				// 词库
				JSONObject strategiesDic = strategyJson.getJSONObject("dicTypeInfos");
				if (null != strategiesDic) {
					JSON dicTypeInfo = (JSON) strategiesDic.get("dicTypeInfo");
					String dicTypeInfoStr = JSON.toJSONString(dicTypeInfo);
					char[] dicStrChar = dicTypeInfoStr.substring(0, 1).toCharArray();
					char dicFirstChar = dicStrChar[0];
					// 单词典类型
					if (dicFirstChar == '{') {
						JSONObject strategyDic = strategiesDic.getJSONObject("dicTypeInfo");
						// 解析词库信息
						String dicTypeName = strategyDic.getString("dicTypeName");
						JSONObject dicSubTypeInfos = strategyDic.getJSONObject("dicSubTypeInfos");
						JSON dicSubTypeInfoJ = (JSON) strategiesDic.get("dicSubTypeInfos");
						String dicSubTypeInfoStr = JSON.toJSONString(dicSubTypeInfoJ);
						char[] dicSubStrChar = dicSubTypeInfoStr.substring(0, 1).toCharArray();
						char dicSubFirstChar = dicSubStrChar[0];
						// 单词典子类型
						if (dicSubFirstChar == '{') {
							JSONObject dicSubTypeInfo = dicSubTypeInfos.getJSONObject("dicSubTypeInfo");
							String dicSubTypeName = dicSubTypeInfo.getString("dicSubTypeName");
							String dicNames = dicSubTypeInfo.getString("dicNames");
							if (!dicNames.contains(",")) {
								Dictionary dicti = dictionaryRepository.findByName(dicNames);
								if (null == dicti) {
									continue;
								}
								Dictionary dictionary = dictionaryRepository.findByName(dicNames);
								String dicId = dictionary.getId();
								StrategyDicRelation sRelation = new StrategyDicRelation();
								sRelation.setDicId(dicId);
								sRelation.setStrategyId(strategyId);
								strategyDicRelationRepository.save(sRelation);
							} else {
								String[] dicNamesArr = dicNames.split(",");
								for (String dicName : dicNamesArr) {
									Dictionary dictionary = dictionaryRepository.findByName(dicName);
									if (null == dictionary) {
										continue;
									}
									String dicId = dictionary.getId();
									StrategyDicRelation sRelation = new StrategyDicRelation();
									sRelation.setDicId(dicId);
									sRelation.setStrategyId(strategyId);
									strategyDicRelationRepository.save(sRelation);
								}
							}
						} else if (dicFirstChar == '[') {
							JSONArray dicSubTypeInfoArr = dicSubTypeInfos.getJSONArray("dicSubTypeInfo");
							for (int k = 0; k < dicSubTypeInfoArr.size(); k++) {
								JSONObject dicSubTypeInfo = dicSubTypeInfoArr.getJSONObject(k);
								String dicSubTypeName = dicSubTypeInfo.getString("dicSubTypeName");
								String dicNames = dicSubTypeInfo.getString("dicNames");
								if (!dicNames.contains(",")) {
									Dictionary dicti = dictionaryRepository.findByName(dicNames);
									if (null == dicti) {
										continue;
									}
									Dictionary dictionary = dictionaryRepository.findByName(dicNames);
									String dicId = dictionary.getId();
									StrategyDicRelation sRelation = new StrategyDicRelation();
									sRelation.setDicId(dicId);
									sRelation.setStrategyId(strategyId);
									strategyDicRelationRepository.save(sRelation);
								} else {
									String[] dicNamesArr = dicNames.split(",");
									for (String dicName : dicNamesArr) {
										Dictionary dictionary = dictionaryRepository.findByName(dicName);
										if (null == dictionary) {
											continue;
										}
										String dicId = dictionary.getId();
										StrategyDicRelation sRelation = new StrategyDicRelation();
										sRelation.setDicId(dicId);
										sRelation.setStrategyId(strategyId);
										strategyDicRelationRepository.save(sRelation);
									}
								}
							}
						}
						// 多词典类型
					} else if (dicFirstChar == '[') {
						JSONArray strategiesDicArr = strategiesDic.getJSONArray("dicTypeInfo");
						for (int j = 0; j < strategiesDicArr.size(); j++) {
							JSONObject strategyDic = strategiesDicArr.getJSONObject(j);
							// 解析词库信息
							String dicTypeName = strategyDic.getString("dicTypeName");
							JSONObject dicSubTypeInfos = strategyDic.getJSONObject("dicSubTypeInfos");
							JSON dicSubTypeInfoJ = (JSON) strategiesDic.get("dicSubTypeInfos");
							String dicSubTypeInfoStr = JSON.toJSONString(dicSubTypeInfoJ);
							char[] dicSubStrChar = dicSubTypeInfoStr.substring(0, 1).toCharArray();
							char dicSubFirstChar = dicSubStrChar[0];
							// 单词典子类型
							if (dicSubFirstChar == '{') {
								JSONObject dicSubTypeInfo = dicSubTypeInfos.getJSONObject("dicSubTypeInfo");
								String dicSubTypeName = dicSubTypeInfo.getString("dicSubTypeName");
								String dicNames = dicSubTypeInfo.getString("dicNames");
								if (!dicNames.contains(",")) {
									Dictionary dicti = dictionaryRepository.findByName(dicNames);
									if (null == dicti) {
										continue;
									}
									Dictionary dictionary = dictionaryRepository.findByName(dicNames);
									String dicId = dictionary.getId();
									StrategyDicRelation sRelation = new StrategyDicRelation();
									sRelation.setDicId(dicId);
									sRelation.setStrategyId(strategyId);
									strategyDicRelationRepository.save(sRelation);
								} else {
									String[] dicNamesArr = dicNames.split(",");
									for (String dicName : dicNamesArr) {
										Dictionary dictionary = dictionaryRepository.findByName(dicName);
										if (null == dictionary) {
											continue;
										}
										String dicId = dictionary.getId();
										StrategyDicRelation sRelation = new StrategyDicRelation();
										sRelation.setDicId(dicId);
										sRelation.setStrategyId(strategyId);
										strategyDicRelationRepository.save(sRelation);
									}
								}
							} else if (dicFirstChar == '[') {
								JSONArray dicSubTypeInfoArr = dicSubTypeInfos.getJSONArray("dicSubTypeInfo");
								for (int k = 0; k < dicSubTypeInfoArr.size(); k++) {
									JSONObject dicSubTypeInfo = dicSubTypeInfoArr.getJSONObject(k);
									String dicSubTypeName = dicSubTypeInfo.getString("dicSubTypeName");
									String dicNames = dicSubTypeInfo.getString("dicNames");
									if (!dicNames.contains(",")) {
										Dictionary dicti = dictionaryRepository.findByName(dicNames);
										if (null == dicti) {
											continue;
										}
										Dictionary dictionary = dictionaryRepository.findByName(dicNames);
										String dicId = dictionary.getId();
										StrategyDicRelation sRelation = new StrategyDicRelation();
										sRelation.setDicId(dicId);
										sRelation.setStrategyId(strategyId);
										strategyDicRelationRepository.save(sRelation);
									} else {
										String[] dicNamesArr = dicNames.split(",");
										for (String dicName : dicNamesArr) {
											Dictionary dictionary = dictionaryRepository.findByName(dicName);
											if (null == dictionary) {
												continue;
											}
											String dicId = dictionary.getId();
											StrategyDicRelation sRelation = new StrategyDicRelation();
											sRelation.setDicId(dicId);
											sRelation.setStrategyId(strategyId);
											strategyDicRelationRepository.save(sRelation);
										}
									}
								}
							}
						}
					}
				}
			} else if (firstChar == '[') {
				// 多条策略
				JSONArray strategiesArr = strategies.getJSONArray("strategy");
				// 解析一条策略 strategy
				for (int i = 0; i < strategiesArr.size(); i++) {
					JSONObject strategyJson = strategiesArr.getJSONObject(i);
					// 策略名称
					String strategyName = strategyJson.getString("name");
					if (null == strategyName || 0 == strategyName.length()) {
						throw new AdminException("上传失败，没有策略名");
					}
					Strategy strategy = new Strategy();
					// 策略名
					strategy.setOperation("custom");
					strategy.setName(strategyName);
					strategy.setCreateTime(new Date());
					boolean existedStrategy = this.validateStrategyExists(strategyName);
					if (existedStrategy) {
						throw new AdminException("上传失败，该策略名" + strategyName + "已存在");
					}
					String functionName = strategyJson.getString("function");
					if (null == functionName || 0 == functionName.length()) {
						throw new AdminException("上传失败，没有功能名");
					}
					// 功能
					Function function = functionRepository.findByDisplayName(functionName);
					if (null == function) {
						throw new AdminException("上传失败，该功能不存在");
					}
					String functionId = function.getId();
					strategy.setFunctionId(functionId);
					// 算法
					String algorithmName = strategyJson.getString("algorithm");
					if (null == algorithmName || 0 == algorithmName.length()) {
						throw new AdminException("上传失败，没有算法名");
					}
					Algorithm existAlgorithm = algorithmRepository.findOneByDisplayName(algorithmName);
					if (null == existAlgorithm) {
						throw new AdminException("上传失败，该算法不存在");
					}
					String algorithmId = existAlgorithm.getId();
					strategy.setAlgorithmId(algorithmId);
					// 模型
					String modelName = strategyJson.getString("modelName");
					if (null != modelName) {
						Model existModel = modelRepository.findOneByNameAndAlgorithmId(modelName, algorithmId);
						if (null != existModel) {
							String modelId = existModel.getId();
							strategy.setModelId(modelId);
						}
					}
					// 规则
					String ruleName = strategyJson.getString("ruleName");
					if (null != ruleName) {
						Rule existRule = ruleRepository.findOneByRule(ruleName);
						if (null != existRule) {
							Rule rule = ruleRepository.findOneByRuleAndAlgorithmId(ruleName, algorithmId);
							String ruleId = rule.getId();
							strategy.setRuleId(ruleId);
						}
					}
					// 批处理
					int existBatch = strategyJson.getIntValue("batch");
					String batchS = Integer.toString(existBatch);
					if (0 != batchS.length()) {
						strategy.setBatch(existBatch);
					}
					strategyRepository.save(strategy);
					// 保存策略信息，获取该条策略id
					Strategy strategyInfo = strategyRepository.findOneByName(strategyName);
					String strategyId = strategyInfo.getId();
					// 依赖策略
					JSONObject dependencyStrategies = strategyJson.getJSONObject("dependencyStrategies");
					if (null != dependencyStrategies) {
						JSON dependencyStrategyJson = (JSON) dependencyStrategies.get("dependencyStrategy");
						String dependencyStrategyStr = JSON.toJSONString(dependencyStrategyJson);
						char[] deStrChar = dependencyStrategyStr.substring(0, 1).toCharArray();
						char deFirstChar = deStrChar[0];
						if (deFirstChar == '{') {
							// 单条依赖策略
							JSONObject dependencyStrategy = dependencyStrategies.getJSONObject("dependencyStrategy");
							String dependencyFunctionName = dependencyStrategy.getString("dependencyFunctionName");
							if (null == dependencyFunctionName) {
								throw new AdminException("上传失败，没有依赖功能");
							}
							Function functionDe = functionRepository.findByDisplayName(dependencyFunctionName);
							if (null == functionDe) {
								throw new AdminException("上传失败，该依赖功能不存在");
							}
							String dependencyStrategyName = dependencyStrategy.getString("dependencyStrategyName");
							Strategy strategyDepend = strategyRepository.findOneByName(dependencyStrategyName);
							if (null == strategyDepend) {
								throw new AdminException("上传失败，该依赖策略不存在");
							}
							String strategyDependId = strategyDepend.getId();
							StrategyDependStrategy strategyDependStrategy = new StrategyDependStrategy();
							strategyDependStrategy.setDependStrategyId(strategyDependId);
							strategyDependStrategy.setStrategyId(strategyId);
							strategyDependStrategyRepository.save(strategyDependStrategy);
						} else if (deFirstChar == '[') {
							// 多条依赖策略
							JSONArray dependencyStrategyArr = dependencyStrategies.getJSONArray("dependencyStrategy");
							for (int j = 0; j < dependencyStrategyArr.size(); j++) {
								JSONObject dependencyStrategy = dependencyStrategyArr.getJSONObject(j);
								String dependencyFunctionName = dependencyStrategy.getString("dependencyFunctionName");
								if (null == dependencyFunctionName) {
									throw new AdminException("上传失败，没有依赖功能");
								}
								Function functionDe = functionRepository.findByDisplayName(dependencyFunctionName);
								if (null == functionDe) {
									throw new AdminException("上传失败，该依赖功能不存在");
								}
								String dependencyStrategyName = dependencyStrategy.getString("dependencyStrategyName");
								Strategy strategyDepend = strategyRepository.findOneByName(dependencyStrategyName);
								if (null == strategyDepend) {
									throw new AdminException("上传失败，该依赖策略不存在");
								}
								String strategyDependId = strategyDepend.getId();
								StrategyDependStrategy strategyDependStrategy = new StrategyDependStrategy();
								strategyDependStrategy.setDependStrategyId(strategyDependId);
								strategyDependStrategy.setStrategyId(strategyId);
								strategyDependStrategyRepository.save(strategyDependStrategy);
							}
						}
					}
					// 词库
					JSONObject strategiesDic = strategyJson.getJSONObject("dicTypeInfos");
					if (null != strategiesDic) {
						JSON dicTypeInfo = (JSON) strategiesDic.get("dicTypeInfo");
						String dicTypeInfoStr = JSON.toJSONString(dicTypeInfo);
						char[] dicStrChar = dicTypeInfoStr.substring(0, 1).toCharArray();
						char dicFirstChar = dicStrChar[0];
						// 单词典类型
						if (dicFirstChar == '{') {
							JSONObject strategyDic = strategiesDic.getJSONObject("dicTypeInfo");
							// 解析词库信息
							String dicTypeName = strategyDic.getString("dicTypeName");
							JSONObject dicSubTypeInfos = strategyDic.getJSONObject("dicSubTypeInfos");
							JSON dicSubTypeInfoJ = (JSON) strategiesDic.get("dicSubTypeInfos");
							String dicSubTypeInfoStr = JSON.toJSONString(dicSubTypeInfoJ);
							char[] dicSubStrChar = dicSubTypeInfoStr.substring(0, 1).toCharArray();
							char dicSubFirstChar = dicSubStrChar[0];
							// 单词典子类型
							if (dicSubFirstChar == '{') {
								JSONObject dicSubTypeInfo = dicSubTypeInfos.getJSONObject("dicSubTypeInfo");
								String dicSubTypeName = dicSubTypeInfo.getString("dicSubTypeName");
								String dicNames = dicSubTypeInfo.getString("dicNames");
								if (!dicNames.contains(",")) {
									Dictionary dicti = dictionaryRepository.findByName(dicNames);
									if (null == dicti) {
										continue;
									}
									Dictionary dictionary = dictionaryRepository.findByName(dicNames);
									String dicId = dictionary.getId();
									StrategyDicRelation sRelation = new StrategyDicRelation();
									sRelation.setDicId(dicId);
									sRelation.setStrategyId(strategyId);
									strategyDicRelationRepository.save(sRelation);
								} else {
									String[] dicNamesArr = dicNames.split(",");
									for (String dicName : dicNamesArr) {
										Dictionary dictionary = dictionaryRepository.findByName(dicName);
										if (null == dictionary) {
											continue;
										}
										String dicId = dictionary.getId();
										StrategyDicRelation sRelation = new StrategyDicRelation();
										sRelation.setDicId(dicId);
										sRelation.setStrategyId(strategyId);
										strategyDicRelationRepository.save(sRelation);
									}
								}
							} else if (dicFirstChar == '[') {
								JSONArray dicSubTypeInfoArr = dicSubTypeInfos.getJSONArray("dicSubTypeInfo");
								for (int k = 0; k < dicSubTypeInfoArr.size(); k++) {
									JSONObject dicSubTypeInfo = dicSubTypeInfoArr.getJSONObject(k);
									String dicSubTypeName = dicSubTypeInfo.getString("dicSubTypeName");
									String dicNames = dicSubTypeInfo.getString("dicNames");
									if (!dicNames.contains(",")) {
										Dictionary dicti = dictionaryRepository.findByName(dicNames);
										if (null == dicti) {
											continue;
										}
										Dictionary dictionary = dictionaryRepository.findByName(dicNames);
										String dicId = dictionary.getId();
										StrategyDicRelation sRelation = new StrategyDicRelation();
										sRelation.setDicId(dicId);
										sRelation.setStrategyId(strategyId);
										strategyDicRelationRepository.save(sRelation);
									} else {
										String[] dicNamesArr = dicNames.split(",");
										for (String dicName : dicNamesArr) {
											Dictionary dictionary = dictionaryRepository.findByName(dicName);
											if (null == dictionary) {
												continue;
											}
											String dicId = dictionary.getId();
											StrategyDicRelation sRelation = new StrategyDicRelation();
											sRelation.setDicId(dicId);
											sRelation.setStrategyId(strategyId);
											strategyDicRelationRepository.save(sRelation);
										}
									}
								}
							}
							// 多词典类型
						} else if (dicFirstChar == '[') {
							JSONArray strategiesDicArr = strategiesDic.getJSONArray("dicTypeInfo");
							for (int j = 0; j < strategiesDicArr.size(); j++) {
								JSONObject strategyDic = strategiesDicArr.getJSONObject(j);
								// 解析词库信息
								String dicTypeName = strategyDic.getString("dicTypeName");
								JSONObject dicSubTypeInfos = strategyDic.getJSONObject("dicSubTypeInfos");
								JSON dicSubTypeInfoJ = (JSON) strategiesDic.get("dicSubTypeInfos");
								String dicSubTypeInfoStr = JSON.toJSONString(dicSubTypeInfoJ);
								char[] dicSubStrChar = dicSubTypeInfoStr.substring(0, 1).toCharArray();
								char dicSubFirstChar = dicSubStrChar[0];
								// 单词典子类型
								if (dicSubFirstChar == '{') {
									JSONObject dicSubTypeInfo = dicSubTypeInfos.getJSONObject("dicSubTypeInfo");
									String dicSubTypeName = dicSubTypeInfo.getString("dicSubTypeName");
									String dicNames = dicSubTypeInfo.getString("dicNames");
									if (!dicNames.contains(",")) {
										Dictionary dicti = dictionaryRepository.findByName(dicNames);
										if (null == dicti) {
											continue;
										}
										Dictionary dictionary = dictionaryRepository.findByName(dicNames);
										String dicId = dictionary.getId();
										StrategyDicRelation sRelation = new StrategyDicRelation();
										sRelation.setDicId(dicId);
										sRelation.setStrategyId(strategyId);
										strategyDicRelationRepository.save(sRelation);
									} else {
										String[] dicNamesArr = dicNames.split(",");
										for (String dicName : dicNamesArr) {
											Dictionary dictionary = dictionaryRepository.findByName(dicName);
											if (null == dictionary) {
												continue;
											}
											String dicId = dictionary.getId();
											StrategyDicRelation sRelation = new StrategyDicRelation();
											sRelation.setDicId(dicId);
											sRelation.setStrategyId(strategyId);
											strategyDicRelationRepository.save(sRelation);
										}
									}
								} else if (dicFirstChar == '[') {
									JSONArray dicSubTypeInfoArr = dicSubTypeInfos.getJSONArray("dicSubTypeInfo");
									for (int k = 0; k < dicSubTypeInfoArr.size(); k++) {
										JSONObject dicSubTypeInfo = dicSubTypeInfoArr.getJSONObject(k);
										String dicSubTypeName = dicSubTypeInfo.getString("dicSubTypeName");
										String dicNames = dicSubTypeInfo.getString("dicNames");
										if (!dicNames.contains(",")) {
											Dictionary dicti = dictionaryRepository.findByName(dicNames);
											if (null == dicti) {
												continue;
											}
											Dictionary dictionary = dictionaryRepository.findByName(dicNames);
											String dicId = dictionary.getId();
											StrategyDicRelation sRelation = new StrategyDicRelation();
											sRelation.setDicId(dicId);
											sRelation.setStrategyId(strategyId);
											strategyDicRelationRepository.save(sRelation);
										} else {
											String[] dicNamesArr = dicNames.split(",");
											for (String dicName : dicNamesArr) {
												Dictionary dictionary = dictionaryRepository.findByName(dicName);
												if (null == dictionary) {
													continue;
												}
												String dicId = dictionary.getId();
												StrategyDicRelation sRelation = new StrategyDicRelation();
												sRelation.setDicId(dicId);
												sRelation.setStrategyId(strategyId);
												strategyDicRelationRepository.save(sRelation);
											}
										}
									}
								}
							}
						}
					}
				}
			}

			// MultipartFile转File
			File fileF = null;
			try {
				fileF = File.createTempFile("tmp", null);
				file.transferTo(fileF);
				fileF.deleteOnExit();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// File file = new File("/home/nsupf/uniplatform/");
			fileF.delete();

		}

		return true;
	}

	@Override
	public Map<String, Object> getSingleStrategyInfo(String strategyId) {
		Map<String, Object> strategyInfoMap = new HashMap<>();
		Strategy strategy = strategyRepository.findOne(strategyId);
		if (null == strategy) {
			throw new AdminException("获取失败，该策略不存在");
		}
		String strategyName = strategy.getName();
		Algorithm algorithm = algorithmRepository.findOne(strategy.getAlgorithmId());
		String algotithmName = algorithm.getDisplayName();
		String algorithmId = strategy.getAlgorithmId();
		Function function = functionRepository.findOne(strategy.getFunctionId());
		String functionName = function.getDisplayName();
		String functionId = strategy.getFunctionId();
		String modelName = null;
		String modelId = null;
		if (1 == algorithm.getHasModel()) {
			modelId = strategy.getModelId();
			Model model = modelRepository.findOne(modelId);
			modelName = model.getName();
		}
		String ruleName = null;
		String ruleId = null;
		if (1 == algorithm.getHasRule()) {
			ruleId = strategy.getRuleId();
			Rule rule = ruleRepository.findOne(ruleId);
			ruleName = rule.getRule();
		}
		List<Object> dicInfo = new ArrayList<>();
		if (1 == algorithm.getHasDic()) {
			// strategyId - dicId - dicSubTypeId
			List<StrategyDicRelation> strategyDicRelations = strategyDicRelationRepository
					.findAllByStrategyId(strategyId);
			if (null != strategyDicRelations && 0 != strategyDicRelations.size()) {
				List<String> dicIds = new ArrayList<>();
				for (StrategyDicRelation strategyDicRelation : strategyDicRelations) {
					String dicId = strategyDicRelation.getDicId();
					dicIds.add(dicId);
				}
				Dictionary dictionary = new Dictionary();
				for (String dicId : dicIds) {
					dictionary = dictionaryRepository.findOne(dicId);
					String dicName = dictionary.getName();
					String dicSubTypeId = dictionary.getDicSubTypeId();
					DicSubType dicSubType = dicSubTypeRepository.findOne(dicSubTypeId);
					String dicSubName = dicSubType.getDisPlayName();
					// 词库类型
					String dicTypeId = dictionary.getDicTypeId();
					DicType dicType = dicTypeRepository.findOne(dicTypeId);
					String typeName = dicType.getDisplayName();
					//
					Map<String, String> dicInfoMap = new HashMap<>();
					dicInfoMap.put("dicTypeId", dicTypeId);
					dicInfoMap.put("dicTypeName", typeName);
					dicInfoMap.put("dicSubTypeId", dicSubTypeId);
					dicInfoMap.put("dicSubName", dicSubName);
					dicInfoMap.put("dicId", dicId);
					dicInfoMap.put("dicName", dicName);
					dicInfo.add(dicInfoMap);
				}
			}
		}
		int batch = 0;
		if (1 == algorithm.getHasBatch()) {
			batch = strategy.getBatch();
		}
		String strategyDependId = null;
		List<Object> strategyDenpendList = new ArrayList<>();
		if (1 == algorithm.getHasDependency()) {
			List<StrategyDependStrategy> strategyDependStrategyList = strategyDependStrategyRepository
					.findByStrategyId(strategyId);
			if (null != strategyDependStrategyList && 0 != strategyDependStrategyList.size()) {
				for (StrategyDependStrategy strategyDependStrategy : strategyDependStrategyList) {
					FunctionDependFunction functionDependFunction = functionDependFunctionRepository
							.findByFunctionId(functionId);
					String dependFunctionId = functionDependFunction.getDependFunctionId();
					Function dependFunction = functionRepository.findById(dependFunctionId);
					String depengdFunctionName = dependFunction.getDisplayName();
					String strategyDependenceId = strategyDependStrategy.getDependStrategyId();
					Strategy strategyDepend = strategyRepository.findOne(strategyDependenceId);
					Map<String, String> strategyDependInfoMap = new HashMap<>();
					if (null != strategyDepend) {
						strategyDependId = strategyDepend.getId();
						strategyDependInfoMap.put("depengdFunctionName", depengdFunctionName);
						strategyDependInfoMap.put("strategyDependId", strategyDependId);
						strategyDenpendList.add(strategyDependInfoMap);
					}
				}
			}
		}
		strategyInfoMap.put("strategyName", strategyName);
		strategyInfoMap.put("algotithmName", algotithmName);
		strategyInfoMap.put("algorithmId", algorithmId);
		strategyInfoMap.put("functionName", functionName);
		strategyInfoMap.put("functionId", functionId);
		strategyInfoMap.put("modelName", modelName);
		strategyInfoMap.put("modelId", modelId);
		strategyInfoMap.put("ruleName", ruleName);
		strategyInfoMap.put("ruleId", ruleId);
		strategyInfoMap.put("dicInfo", dicInfo);
		strategyInfoMap.put("batch", batch);
		strategyInfoMap.put("strategyDenpendList", strategyDenpendList);

		return strategyInfoMap;
	}

	@Override
	public List<Algorithm> listAlgorithmInfo() {
		List<Algorithm> algorithmsInfo = algorithmRepository.findAll();

		return algorithmsInfo;
	}

}
