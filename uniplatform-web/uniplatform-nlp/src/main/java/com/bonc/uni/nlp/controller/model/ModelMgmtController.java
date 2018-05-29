package com.bonc.uni.nlp.controller.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.bonc.uni.nlp.dao.strategy.AlgorithmRepository;
import com.bonc.uni.nlp.dao.strategy.ModelRepository;
import com.bonc.uni.nlp.entity.model.DataSet;
import com.bonc.uni.nlp.entity.strategy.Algorithm;
import com.bonc.uni.nlp.entity.strategy.Model;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.model.IModelMgmtService;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年11月21日 下午7:47:19
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/model/mgmt")
public class ModelMgmtController {
	
	@Autowired
	IModelMgmtService modelMgmtService;
	@Autowired
	ModelRepository modelRepository;
	@Autowired
	AlgorithmRepository algorithmRepository;

	@RequestMapping(value = "/functions/algorithms/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String listFunctions() {
		LogManager.Process("Process in : /nlap/admin/model/mgmt/functions/algorithms/list");
		Map<String, Object> map = new HashMap<>();
		map.put("status", 200);
		map.put("msg", "获取功能算法列表成功");

		try {
			List<Object> functions = modelMgmtService.listHasModelFunctions();
			map.put("functions", functions);
		} catch (AdminException e) {
			map.put("status", 400);
			map.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/model/mgmt/functions/algorithms/list");
		return JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/models", method = { RequestMethod.GET, RequestMethod.POST })
	public String listModels(@RequestParam(value = "keyword", required = false) String keyword, String functionId,
			String algorithmId, int pageIndex, int pageSize, boolean ascSort) {
		LogManager.Process("Process in : /nlap/admin/model/mgmt/models");
		Map<String, Object> modelsMap = new HashMap<>();
		modelsMap.put("status", 200);
		modelsMap.put("msg", "获取模型列表成功");

		try {
			List<Object> models = modelMgmtService.listModelsByAlgorithm(keyword, functionId, algorithmId, pageIndex, pageSize,
					ascSort);
			modelsMap.put("models", models);
		} catch (Exception e) {
			e.printStackTrace();
			modelsMap.put("status", 400);
			modelsMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/model/mgmt/models");
		return JSON.toJSONString(modelsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/dataSet/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String listDataSet(String functionId) {
		LogManager.Process("Process in : /nlap/admin/model/mgmt/dataSet/list");
		Map<String, Object> dataSetmap = new HashMap<>();
		dataSetmap.put("status", 200);
		dataSetmap.put("msg", "获取数据集列表成功");

		try {
			List<DataSet> models = modelMgmtService.listDataSet(functionId);
			dataSetmap.put("models", models);
		} catch (AdminException e) {
			dataSetmap.put("status", 400);
			dataSetmap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/model/mgmt/dataSet/list");
		return JSON.toJSONString(dataSetmap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/model/add", method = { RequestMethod.GET, RequestMethod.POST })
	public String addModel(String functionId, String algorithmId, String modelName, String dataSetId) {
		LogManager.Process("Process in : /nlap/admin/model/mgmt/model/add");
		Map<String, Object> addModelMap = new HashMap<>();
		addModelMap.put("status", 200);
		addModelMap.put("msg", "模型添加成功");

		try {
			modelMgmtService.addModel(functionId, algorithmId, modelName, dataSetId);
		} catch (AdminException e) {
			addModelMap.put("status", 400);
			addModelMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/model/mgmt/model/add");
		return JSON.toJSONString(addModelMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/model/edit", method = { RequestMethod.GET, RequestMethod.POST })
	public String editModel(String modelId, String algorithmId, String newModelName, String newDataSetId) {
		LogManager.Process("Process in : /nlap/admin/model/mgmt/model/edit");
		Map<String, Object> editModelMap = new HashMap<>();
		editModelMap.put("status", 200);
		editModelMap.put("msg", "模型修改成功");

		try {
			modelMgmtService.editModel(modelId, algorithmId, newModelName, newDataSetId);
		} catch (AdminException e) {
			editModelMap.put("status", 400);
			editModelMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/model/mgmt/model/edit");
		return JSON.toJSONString(editModelMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/model/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public String removeModel(String modelsId) {
		LogManager.Process("Process in : /nlap/admin/model/mgmt/model/delete");
		Map<String, Object> deleteModelMap = new HashMap<>();
		deleteModelMap.put("status", 200);
		deleteModelMap.put("msg", "模型删除成功");

		try {
			modelMgmtService.deleteModels(modelsId);
		} catch (AdminException e) {
			deleteModelMap.put("status", 400);
			deleteModelMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/model/mgmt/model/delete");
		return JSON.toJSONString(deleteModelMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/model/train", method = { RequestMethod.GET, RequestMethod.POST })
	public String trainModel(String modelId) {
		LogManager.Process("Process in : /nlap/admin/model/mgmt/model/train");
		Map<String, Object> trainModelMap = new HashMap<>();
		trainModelMap.put("status", 200);
		Model model = modelRepository.findOne(modelId);
		Algorithm algorithm = algorithmRepository.getOne(model.getAlgorithmId());
		trainModelMap.put("msg", algorithm.getDisplayName() + "下的" + model.getName() + "模型训练成功");

		try {
			modelMgmtService.trainModel(modelId);
		} catch (AdminException e) {
			trainModelMap.put("status", 400);
			trainModelMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/model/mgmt/model/train");
		return JSON.toJSONString(trainModelMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/model/apply", method = { RequestMethod.GET, RequestMethod.POST })
	public String applyModel(String modelId) {
		LogManager.Process("Process in : /nlap/admin/model/mgmt/model/apply");
		Map<String, Object> applyModelMap = new HashMap<>();
		applyModelMap.put("status", 200);
		applyModelMap.put("msg", "模型应用成功");

		try {
			modelMgmtService.applyModel(modelId);
		} catch (AdminException e) {
			applyModelMap.put("status", 400);
			applyModelMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/model/mgmt/model/apply");
		return JSON.toJSONString(applyModelMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/model/download", method = { RequestMethod.GET, RequestMethod.POST })
	public synchronized String downloadModel(String modelIds, String fileName, HttpServletResponse response) {
		LogManager.Process("Process in : /nlap/admin/model/mgmt/model/download");
		Map<String, Object> downloadModelMap = new HashMap<>();
		downloadModelMap.put("status", 200);
		downloadModelMap.put("msg", "模型下载成功");
		try {
			modelMgmtService.downloadModel(modelIds, fileName, response);
		} catch (AdminException e) {
			downloadModelMap.put("status", 400);
			downloadModelMap.put("msg", e.getMessage());
			return JSON.toJSONString(downloadModelMap, SerializerFeature.DisableCircularReferenceDetect);
		}

		LogManager.Process("Process out : /nlap/admin/model/mgmt/model/download");
		return null;
	}

	@RequestMapping(value = "/model/upload", method = { RequestMethod.GET, RequestMethod.POST })
	public String uploadModelToAlgorithm(String modelName, String algorithmId, MultipartFile[] files) {
		LogManager.Process("Process in : /nlap/admin/model/mgmt/model/upload");
		Map<String, Object> uploadModelMap = new HashMap<>();
		uploadModelMap.put("status", 200);
		uploadModelMap.put("msg", "模型上传成功");

		try {
			modelMgmtService.uploadModel(modelName, algorithmId, files);
		} catch (AdminException e) {
			uploadModelMap.put("status", 400);
			uploadModelMap.put("msg", e.getMessage());
		}

		LogManager.Process("Process out : /nlap/admin/model/mgmt/model/upload");
		return JSON.toJSONString(uploadModelMap, SerializerFeature.DisableCircularReferenceDetect);
	}

}
