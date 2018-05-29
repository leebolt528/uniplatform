package com.bonc.uni.nlp.controller.strategy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.entity.dic.DicType;
import com.bonc.uni.nlp.entity.rule.Rule;
import com.bonc.uni.nlp.entity.strategy.Algorithm;
import com.bonc.uni.nlp.entity.strategy.Model;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.strategy.IStrategyMgmtService;
import com.bonc.uni.nlp.service.strategy.IStrategyService;
import com.bonc.uni.nlp.utils.UserLoggerUtil;
import com.bonc.usdp.odk.logger.Logger;
import com.bonc.usdp.odk.logger.entity.LogType;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年11月1日 下午4:56:26
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/strategy/mgmt")
public class StrategyManagementController {
	@Autowired
	IStrategyService strategyService;
	@Autowired
	IStrategyMgmtService strategyMgmtService;

	@PostConstruct
	public void init() { 
		strategyService.initFunction(); 
		strategyService.initAlgorithm(); 
		strategyService.initDefaultStrategy(); 
		strategyService.initFunctionDependency(); 
	}

	/**
	 * 校验策略名称是否存在
	 */
	@RequestMapping(value = "/strategy/validate")
	public String validateStrategyExists(String strategyName) {
		Logger.logInfo("admin", LogType.OPERATION, "判断策略名称是否已经存在", UserLoggerUtil.getCurrentTime());
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/strategy/validate");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		if (strategyMgmtService.validateStrategyExists(strategyName)) {
			map.put("status", 400);
			map.put("msg", "该策略名称已存在");
		} else {
			map.put("status", 200);
			map.put("msg", "该策略名称不存在，可以新增");
		}
		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/strategy/validate");
		return JSON.toJSONString(map);
	}

	/**
	 * 获取策略列表
	 */
	@RequestMapping(value = "/strategies", method = { RequestMethod.GET, RequestMethod.POST })
	public String listAllStrategies(String functionId, int pageIndex, int pageSize, boolean ascSort) {
		Logger.logInfo("admin", LogType.OPERATION, "获取策略列表", UserLoggerUtil.getCurrentTime());
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/strategies");

		Map<String, Object> strategiesMap = new HashMap<>();
		strategiesMap.put("status", 200);
		strategiesMap.put("msg", "策略列表查询成功");
		List<Object> strategies = new ArrayList<>();
		try {
			strategies = strategyMgmtService.listAllStrategies(functionId, pageIndex, pageSize, ascSort);
		} catch (AdminException e) {
			strategiesMap.put("status", 400);
			strategiesMap.put("msg", e.getMessage());
		}
		int strategiesNumber = strategyMgmtService.getStrategiesNumber(functionId);
		strategiesMap.put("strategyInfo", strategies);
		strategiesMap.put("strategiesNumber", strategiesNumber);

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/strategies");
		return JSON.toJSONString(strategiesMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取策略列表信息
	 */
	@RequestMapping(value = "/strategies/info", method = { RequestMethod.GET, RequestMethod.POST })
	public String listAllStrategiesInfo() {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/strategies/info");
		Map<String, Object> strategiesInfoMap = new HashMap<>();
		strategiesInfoMap.put("status", 200);
		strategiesInfoMap.put("msg", "策略列表信息查询成功");
		List<Object> strategiesInfo = strategyMgmtService.listAllStrategiesInfo();
		strategiesInfoMap.put("strategiesInfo", strategiesInfo);

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/strategies/info");
		return JSON.toJSONString(strategiesInfoMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取功能列表
	 */
	@RequestMapping(value = "/functions", method = { RequestMethod.GET, RequestMethod.POST })
	public String listAllFunctions(boolean ascSort) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/functions");

		Map<String, Object> functionMap = new HashMap<>();
		functionMap.put("status", 200);
		functionMap.put("msg", "功能列表查询成功");
		List<Map<String, String>> functions = strategyMgmtService.listAllFunctions(ascSort);
		functionMap.put("functions", functions);

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/functions");
		return JSON.toJSONString(functionMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取算法列表
	 */
	@RequestMapping(value = "/algorithms", method = { RequestMethod.GET, RequestMethod.POST })
	public String listAlgorithm(String functionId) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/algorithms");

		Map<String, Object> algorithmMap = new HashMap<>();
		algorithmMap.put("status", 200);
		algorithmMap.put("msg", "算法列表查询成功");
		if (null == functionId) {
			algorithmMap.put("status", 400);
			algorithmMap.put("msg", "查询算法失败，该功能不存在");
		}
		List<Algorithm> algorithms = strategyMgmtService.listAlgorithm(functionId);
		algorithmMap.put("algorithms", algorithms);
		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/algorithms");
		return JSON.toJSONString(algorithmMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取算法列表信息
	 */
	@RequestMapping(value = "/algorithms/info", method = { RequestMethod.GET, RequestMethod.POST })
	public String listAlgorithmInfo() {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/algorithms/info");
		List<Algorithm> algorithmsInfo = strategyMgmtService.listAlgorithmInfo();
		Map<String, Object> algorithmMap = new HashMap<>();
		algorithmMap.put("status", 200);
		algorithmMap.put("msg", "算法列表信息查询成功");
		algorithmMap.put("algorithmsInfo", algorithmsInfo);

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/algorithms/info");
		return JSON.toJSONString(algorithmMap, SerializerFeature.DisableCircularReferenceDetect);

	}

	/**
	 * 获取模型列表信息
	 */
	@RequestMapping(value = "/models", method = { RequestMethod.GET, RequestMethod.POST })
	public String listModels(String algorithmId) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/models");
		Map<String, Object> modelMap = new HashMap<>();
		if (null == algorithmId) {
			modelMap.put("status", 400);
			modelMap.put("msg", "查询模型列表失败，该算法不存在");
		}
		List<Model> modelList = strategyMgmtService.listModel(algorithmId);
		modelMap.put("status", 200);
		modelMap.put("msg", "根据算法查询模型列表成功");
		modelMap.put("info", modelList);

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/models");
		return JSON.toJSONString(modelMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 查询规则列表
	 */
	@RequestMapping(value = "/rules", method = { RequestMethod.GET, RequestMethod.POST })
	public String listRules(String algorithmId) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/rules");
		Map<String, Object> ruleMap = new HashMap<>();
		if (null == algorithmId) {
			ruleMap.put("status", 400);
			ruleMap.put("msg", "查询规则列表失败，该算法不存在");
		}
		List<Rule> ruleList = strategyMgmtService.listRule(algorithmId);
		ruleMap.put("status", 200);
		ruleMap.put("msg", "根据算法查询规则列表成功");
		ruleMap.put("info", ruleList);

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/rules");
		return JSON.toJSONString(ruleMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取词库类型列表（普通，功能，领域）
	 */
	@RequestMapping(value = "/dic/types", method = { RequestMethod.GET, RequestMethod.POST })
	public String listDicTypes() {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/dic/types");
		Map<String, Object> dicTypesMap = new HashMap<>();
		List<DicType> dicTypesList = strategyMgmtService.listDicTypes();
		dicTypesMap.put("status", 200);
		dicTypesMap.put("msg", "查询词库类型列表成功");
		dicTypesMap.put("info", dicTypesList);

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/dic/types");
		return JSON.toJSONString(dicTypesMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 查询依赖策略列表
	 */
	@RequestMapping(value = "/dependency/strategy", method = { RequestMethod.GET, RequestMethod.POST })
	public String listDependencyStrategy(String functionId) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/dependency/strategy");
		Map<String, Object> ruleMap = new HashMap<>();
		if (null == functionId) {
			ruleMap.put("status", 400);
			ruleMap.put("msg", "查询依赖策略列表失败，该功能不存在");
		}
		List<Object> dependencyStrategyList = strategyMgmtService.listDependencyStrategy(functionId);
		ruleMap.put("status", 200);
		ruleMap.put("msg", "根据功能查询依赖策略列表成功");
		ruleMap.put("info", dependencyStrategyList);

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/dependency/strategy");
		return JSON.toJSONString(ruleMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 查询词库列表
	 */
	@RequestMapping(value = "/dics", method = { RequestMethod.GET, RequestMethod.POST })
	public String listDics(String functionId, boolean ascSort) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/dics");
		Map<String, Object> ruleMap = new HashMap<>();
		try {
			if (null == functionId) {
				ruleMap.put("status", 400);
				ruleMap.put("msg", "查询词库列表失败，该功能不存在");
			}
			List<Object> dicsList = strategyMgmtService.listDic(functionId, ascSort);
			ruleMap.put("status", 200);
			ruleMap.put("msg", "查询词库列表成功");
			ruleMap.put("info", dicsList);
		} catch (Exception e) {
			LogManager.Exception("select strategy dicInfo Exception", e);
		}

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/dics");
		return JSON.toJSONString(ruleMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 添加策略
	 */
	@RequestMapping(value = "/strategy/add", method = { RequestMethod.GET, RequestMethod.POST })
	public String addStrategy(String strategyName, String functionId, String algorithmId,
			@RequestParam(value = "modelId", required = false) String modelId,
			@RequestParam(value = "ruleId", required = false) String ruleId,
			@RequestParam(value = "dictsId", required = false) String dictsId, Integer batch,
			@RequestParam(value = "dependencyStrategyId", required = false) String dependencyStrategyId) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/strategy/add");
		Map<String, Object> addStrategymap = new HashMap<>();
		try {
			int operation = strategyMgmtService.addStrategy(strategyName, functionId, algorithmId, modelId, ruleId,
					dictsId, batch, dependencyStrategyId);

			if (1 == operation) {
				addStrategymap.put("status", 200);
				addStrategymap.put("msg", "策略添加成功");
			} else if (0 == operation) {
				addStrategymap.put("status", 400);
				addStrategymap.put("msg", "策略添加失败,所选的功能或算法不存在");
			} else if (2 == operation) {
				addStrategymap.put("status", 400);
				addStrategymap.put("msg", "策略添加失败,策略名为空或者已存在");
			} else if (3 == operation) {
				addStrategymap.put("status", 400);
				addStrategymap.put("msg", "策略添加失败,子策略和添加的策略不属于同一功能");
			}
		} catch (Exception e) {
			LogManager.Exception("add strategy Exception", e);
		}

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/strategy/add");
		return JSON.toJSONString(addStrategymap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 编辑策略
	 */
	@RequestMapping(value = "/strategy/edit", method = { RequestMethod.GET, RequestMethod.POST })
	public String editStrategy(String strategyId, String strategyNewName, String algorithmNewId,
			@RequestParam(value = "modelId", required = false) String modelId,
			@RequestParam(value = "ruleId", required = false) String ruleId,
			@RequestParam(value = "dictsId", required = false) String dictsId,
			@RequestParam(value = "batch", required = false) Integer batch,
			@RequestParam(value = "dependencyStrategyId", required = false) String dependencyStrategyId) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/strategy/edit");
		Map<String, Object> editStrategymap = new HashMap<>();
		try {
			int operation = strategyMgmtService.editStrategy(strategyId, strategyNewName, algorithmNewId, modelId,
					ruleId, dictsId, batch, dependencyStrategyId);
			if (1 == operation) {
				editStrategymap.put("status", 200);
				editStrategymap.put("msg", "策略修改成功");
			} else if (0 == operation) {
				editStrategymap.put("status", 400);
				editStrategymap.put("msg", "策略修改失败,该策略不存在");
			} else if (2 == operation) {
				editStrategymap.put("status", 400);
				editStrategymap.put("msg", "策略修改失败,为默认策略或者在使用中");
			} else if (3 == operation) {
				editStrategymap.put("status", 400);
				editStrategymap.put("msg", "策略修改失败,该策略名为空或者已存在");
			}
		} catch (Exception e) {
			LogManager.Exception("edit strategy Exception", e);
		}

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/strategy/edit");
		return JSON.toJSONString(editStrategymap, SerializerFeature.DisableCircularReferenceDetect);

	}

	/**
	 * 删除策略(可批量删除)
	 */
	@RequestMapping(value = "/strategy/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteStrategy(String strategyId) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/strategy/delete");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "策略删除成功");

		try {
			strategyMgmtService.deleteStrategy(strategyId);
		} catch (AdminException e) {
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/strategy/delete");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 设为默认策略
	 */
	@RequestMapping(value = "/strategy/set/default", method = { RequestMethod.GET, RequestMethod.POST })
	public String setAsDefault(String strategyId) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/strategy/set/default");
		Map<String, Object> map = new HashMap<>();
		try {
			boolean operation = strategyMgmtService.setAsDefault(strategyId);
			if (operation) {
				map.put("status", 200);
				map.put("msg", "策略设为默认成功");
			} else {
				map.put("status", 400);
				map.put("msg", "策略设为默认失败");
			}
		} catch (Exception e) {
			LogManager.Exception("set strategy as default Exception", e);
		}

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/strategy/set/default");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/strategies/export", method = { RequestMethod.GET, RequestMethod.POST })
	public synchronized String exportStrategies2XMLString(String strategiesId, HttpServletResponse response) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/strategies/export");
		OutputStream out = null;
		try {
			if (null == strategiesId || 0 == strategiesId.length()) {
				Map<String, Object> map = new HashMap<>();
				map.put("status", "400");
				map.put("msg", "请选择要导出的策略");
				return JSON.toJSONString(map);
			}

			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition",
					"attachment;filename=\"" + new String(("strategy.xml").getBytes("GBK"), "ISO8859_1") + "\"");
			out = response.getOutputStream();

			String content = strategyMgmtService.exportStrategies2XMLString(strategiesId);

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

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/strategies/export");
		return null;
	}

	@RequestMapping(value = "/strategies/import", method = { RequestMethod.GET, RequestMethod.POST })
	public String importStrategies(MultipartFile[] files) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/strategies/import");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "导入成功");
		try {
			strategyMgmtService.importStrategies(files);
		} catch (AdminException e) {
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/strategies/import");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/single/strategy/info", method = { RequestMethod.GET, RequestMethod.POST })
	public String singleStrategyIfo(String strategyId) {
		LogManager.Process("Process in : /nlap/admin/strategy/mgmt/single/strategy/info");
		Map<String, Object> singleStrategymap = new HashMap<>();

		try {
			singleStrategymap.put("status", 200);
			singleStrategymap.put("msg", "单个策略信息列表获取成功");
			Map<String, Object> singleStrtagyInfo = strategyMgmtService.getSingleStrategyInfo(strategyId);
			singleStrategymap.put("singleStrtagyInfo", singleStrtagyInfo);
		} catch (AdminException e) {
			LogManager.Exception("select single strategy info Exception", e);
		}

		LogManager.Process("Process out : /nlap/admin/strategy/mgmt/single/strategy/info");
		return JSON.toJSONString(singleStrategymap, SerializerFeature.DisableCircularReferenceDetect);
	}

}
