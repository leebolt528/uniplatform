package com.bonc.uni.nlp.service.Impl.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.bonc.uni.nlp.dao.FuncitonRepository;
import com.bonc.uni.nlp.dao.classify.ClassifyObjectRepository;
import com.bonc.uni.nlp.dao.classify.ClassifyRepository;
//import com.bonc.uni.nlp.dao.corpus.CorpusDatasetRelationRepository;
import com.bonc.uni.nlp.dao.corpus.CorpusTypeRepository;
import com.bonc.uni.nlp.dao.model.DataSetRepository;
import com.bonc.uni.nlp.dao.strategy.AlgorithmRepository;
import com.bonc.uni.nlp.dao.strategy.ModelRepository;
import com.bonc.uni.nlp.entity.dic.Function;
import com.bonc.uni.nlp.entity.model.DataSet;
import com.bonc.uni.nlp.entity.model.ModelInfo;
import com.bonc.uni.nlp.entity.model.ServerInformation;
import com.bonc.uni.nlp.entity.model.TrainResult;
import com.bonc.uni.nlp.entity.strategy.Algorithm;
import com.bonc.uni.nlp.entity.strategy.Model;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.exception.BoncException;
import com.bonc.uni.nlp.service.Impl.corpus.DataSetServiceImpl;
import com.bonc.uni.nlp.service.model.IModelMgmtService;
import com.bonc.uni.nlp.utils.NewZipUtil;
import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.uni.nlp.utils.model.IModelTrainer;
import com.bonc.uni.nlp.utils.model.ModelTrainerFactory;
import com.bonc.uni.nlp.utils.redis.publisher.ModelPublisher;
import com.bonc.usdp.odk.common.exception.ConnectionException;
import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.common.file.FileUtil;
import com.bonc.usdp.odk.common.net.SSHUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年11月21日 下午7:51:17
 */
@Service
public class ModelMgmtServiceImpl implements IModelMgmtService {

	@Autowired
	FuncitonRepository funcitonRepository;
	@Autowired
	ModelRepository modelRepository;
	@Autowired
	AlgorithmRepository algorithmRepository;
	@Autowired
	DataSetRepository dataSetRepository;
	@Autowired
	ClassifyRepository classifyRepository;
	@Autowired
	CorpusTypeRepository corpusTypeRepository;
	@Autowired
	ClassifyObjectRepository classifyObjectRepository;

	@Autowired
	DataSetServiceImpl dataSetServiceImpl;

	private static String MODEL_SAVE_PATH;

	static {
		try {
			MODEL_SAVE_PATH = PathUtil.getResourcesPath() + File.separator + "model";
			File pluginDir = new File(MODEL_SAVE_PATH);
			if (!pluginDir.exists()) {
				pluginDir.mkdirs();
			}
		} catch (PathNotFoundException e) {
			LogManager.Exception(e);
		}
	}

	@Override
	public List<Object> listHasModelFunctions() {
		List<Algorithm> algorithmsN = algorithmRepository.findAllByHasModel(1);
		List<Algorithm> algorithms = new ArrayList<>();
		for (Algorithm algorithm : algorithmsN) {
			if (!"rule".equals(algorithm.getName())) {
				algorithms.add(algorithm);
			}
		}
		LinkedHashMap<String, List<Algorithm>> functionAlgorithms = new LinkedHashMap<>();
		for (Algorithm alg : algorithms) {
			String functionId = alg.getFunctionId();
			List<Algorithm> algs = functionAlgorithms.get(functionId);
			if (null != algs) {
				functionAlgorithms.get(functionId).add(alg);
			} else {
				algs = new ArrayList<>();
				algs.add(alg);
				functionAlgorithms.put(functionId, algs);
			}
		}
		List<Object> functions = new ArrayList<>();
		Map<String, Object> infos = new HashMap<>();
		for (Entry<String, List<Algorithm>> entry : functionAlgorithms.entrySet()) {
			Function function = funcitonRepository.findOne(entry.getKey());
			List<Algorithm> algorithmsList = new ArrayList<>();
			List<Map<String, Object>> algorithmInfoList = new ArrayList<>();
			if (null != function) {
				int modelsNumber = modelRepository.countByFunctionId(function.getId());
				infos = new HashMap<>();
				algorithmsList = entry.getValue();
				Map<String, Object> modelInfoMap = new HashMap<>();
				modelInfoMap.put("id", "all");
				modelInfoMap.put("displayName", "全部");
				modelInfoMap.put("functionId", function.getId());
				modelInfoMap.put("modelsNum", modelsNumber);
				algorithmInfoList.add(modelInfoMap);
				for (Algorithm algorithm : algorithmsList) {
					// 该算法下模型的数量
					modelInfoMap = new HashMap<>();
					List<Model> models = modelRepository.findAllByAlgorithmId(algorithm.getId());
					modelInfoMap.put("id", algorithm.getId());
					modelInfoMap.put("displayName", algorithm.getDisplayName());
					modelInfoMap.put("functionId", algorithm.getFunctionId());
					modelInfoMap.put("modelsNum", models.size());
					algorithmInfoList.add(modelInfoMap);
				}
				String functionName = function.getDisplayName();
				infos.put("modelsNumber", modelsNumber);
				infos.put("functionName", functionName);
				infos.put("algorithmsList", algorithmInfoList);
				functions.add(infos);
			}
		}

		return functions;
	}

	@Override
	public List<Object> listModelsByAlgorithm(String keyword, String functionId, String algorithmId, int pageIndex,
			int pageSize, boolean ascSort) {
		if (!StringUtil.isEmpty(algorithmId)) {
			Algorithm algorithm = algorithmRepository.findOne(algorithmId);
			if (null == algorithm) {
				throw new AdminException("获取模型列表失败，该算法不存在");
			}
		}
		Sort sort = null;
		if (ascSort) {
			sort = new Sort(Sort.Direction.ASC, "createTime");
		} else {
			sort = new Sort(Sort.Direction.DESC, "createTime");
		}
		List<Model> models = new ArrayList<>();
		List<Object> modelInfos = new ArrayList<>();
		Map<String, Object> modelInfosMap = new HashMap<>();
		int modelNumbers = 0;
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
		if (StringUtil.isEmpty(algorithmId)) {
			if (StringUtil.isEmpty(keyword)) {
				models = modelRepository.findAllByFunctionId(functionId, pageable);
				modelNumbers = modelRepository.countByFunctionId(functionId);
			} else {
				keyword = "%" + keyword + "%";
				models = modelRepository.findByFunctionIdAndNameLike(functionId, keyword, pageable);
				modelNumbers = modelRepository.countByFunctionIdAndNameLike(functionId, keyword);
			}
		} else {
			if (StringUtil.isEmpty(keyword)) {
				models = modelRepository.findAllByAlgorithmId(algorithmId, pageable);
				modelNumbers = modelRepository.countByAlgorithmId(algorithmId);
			} else {
				keyword = "%" + keyword + "%";
				models = modelRepository.findByAlgorithmIdAndNameLike(algorithmId, keyword, pageable);
				modelNumbers = modelRepository.countByAlgorithmIdAndNameLike(algorithmId, keyword);
			}
		}
		List<Object> modelInfo = new ArrayList<>();
		Map<String, Object> modelInfoMap = new HashMap<>();
		for (Model model : models) {
			modelInfoMap = new HashMap<>();
			String dataSetId = model.getDataSetId();
			String algorithmIdInfo = model.getAlgorithmId();
			Algorithm algorithm = algorithmRepository.findOne(algorithmIdInfo);
			String dataSetName = null;
			if (null != dataSetId) {
				DataSet dataSet = dataSetRepository.findOne(dataSetId);
				dataSetName = dataSet.getName();
			}
			modelInfoMap.put("dataSetName", dataSetName);
			modelInfoMap.put("algorithmName", algorithm.getDisplayName());
			modelInfoMap.put("modelInfo", model);
			modelInfo.add(modelInfoMap);
		}
		modelInfosMap = new HashMap<>();
		modelInfosMap.put("modelInfo", modelInfo);
		modelInfosMap.put("modelNumbers", modelNumbers);
		modelInfos.add(modelInfosMap);

		return modelInfos;
	}

	@Override
	public boolean addModel(String functionId, String algorithmId, String modelName, String dataSetId) {
		Algorithm algorithm = algorithmRepository.findOne(algorithmId);
		if (null == algorithm) {
			throw new AdminException("添加模型失败，该算法不存在");
		}
		Function function = funcitonRepository.findOne(functionId);
		if (null == function) {
			throw new AdminException("添加模型失败，该功能不存在");
		}
		// 判断该算法下此模型名称是否存在
		Model exisitedModel = modelRepository.findOneByNameAndAlgorithmId(modelName, algorithmId);
		if (null != exisitedModel) {
			throw new AdminException("添加失败，该算法下此模型名称已经存在");
		}

		Model model = new Model();
		model.setName(modelName);
		model.setFunctionId(functionId);
		model.setAlgorithmId(algorithmId);
		model.setDataSetId(dataSetId);
		model.setCreateTime(new Date());
		model.setInUsing(0);
		model.setModelStatus(0);
		model.setOperation("custom");
		// model.setUserId(CurrentUserUtils.getInstance().getUser().getId());

		modelRepository.save(model);

		DataSet dataSet = dataSetRepository.findOne(dataSetId);
		dataSet.setStatus(1);
		dataSetRepository.save(dataSet);

		return true;
	}

	@Override
	public boolean editModel(String modelId, String algorithmId, String newModelName, String newDataSetId) {
		Algorithm algorithm = algorithmRepository.findOne(algorithmId);
		if (null == algorithm) {
			throw new AdminException("添加模型失败，该算法不存在");
		}
		// 判断该算法下此模型名称是否存在
		Model model = modelRepository.findOne(modelId);
		if (1 == model.getInUsing()) {
			throw new AdminException("修改失败，该模型正在使用中");
		}
		if ("system".equals(model.getOperation())) {
			throw new AdminException("修改失败，该模型为系统模型");
		}
		String modelName = model.getName();
		Model exisitedModel = modelRepository.findOneByNameAndAlgorithmId(newModelName, algorithmId);
		if (!modelName.equals(newModelName) && null != exisitedModel) {
			throw new AdminException("修改失败，该算法下此模型名称已经存在");
		}
		String oldDataSetId = model.getDataSetId();
		model.setName(newModelName);
		model.setAlgorithmId(algorithmId);
		if (!oldDataSetId.equals(newDataSetId)) {
			model.setDataSetId(newDataSetId);
			model.setModelStatus(0);
			// 删除原模型的模型文件
			if (model.getModelStatus() == 2 || model.getModelStatus() == 4) {
				// 删除该模型下的文件
				String path = model.getSavepath();
				FileUtil.deleteDirectory(path);

				Function function = funcitonRepository.findOne(model.getFunctionId());
				ModelInfo modelInfo = new ModelInfo();
				modelInfo.setModelName(model.getName());
				modelInfo.setFunctionName(function.getName());
				modelInfo.setAlgorithmName(algorithm.getName());
				ModelPublisher modelPublisher = new ModelPublisher();
				modelPublisher.publishModel(modelInfo, true);
			}
		}
		model.setCreateTime(new Date());
		modelRepository.save(model);

		DataSet dataSet = dataSetRepository.findOne(newDataSetId);
		dataSet.setStatus(1);
		dataSetRepository.save(dataSet);

		List<Model> modelExist = modelRepository.findAllByDataSetId(oldDataSetId);
		if (modelExist.size() == 0) {
			DataSet dataSetOld = dataSetRepository.findOne(oldDataSetId);
			dataSetOld.setStatus(0);
			dataSetRepository.save(dataSetOld);
		}

		return true;
	}

	@Override
	public List<DataSet> listDataSet(String functionId) {
		Function function = funcitonRepository.findOne(functionId);
		if (null == function) {
			throw new AdminException("获取数据集列表失败，该算法不存在");
		}
		List<DataSet> dataSetsList = new ArrayList<>();
		List<DataSet> dataSets = dataSetRepository.findAllByFunctionId(functionId);
		for (DataSet dataSet : dataSets) {
			if (dataSetServiceImpl.countCorpus(dataSet.getId(), null, null) > 0) {
				dataSetsList.add(dataSet);
			}
		}

		return dataSetsList;
	}

	@Override
	public boolean deleteModels(String modelsId) {
		String[] modelsArr = modelsId.split(",");
		List<String> modelIds = new ArrayList<>();
		for (String modelId : modelsArr) {
			Model modelInfo = modelRepository.findOne(modelId);
			// 没有在使用中
			if (!"system".equals(modelInfo.getOperation()) && 0 == modelInfo.getInUsing()) {
				// 训练中和下发中不能删除
				if (1 != modelInfo.getModelStatus() && 3 != modelInfo.getModelStatus()) {
					modelIds.add(modelId);
				}
			}
		}
		for (String modelId : modelIds) {
			Model model = modelRepository.findOne(modelId);
			String oldDataSetId = model.getDataSetId();
			modelRepository.delete(model);

			List<Model> modelExist = modelRepository.findAllByDataSetId(oldDataSetId);
			if (modelExist.size() == 0) {
				DataSet dataSetOld = dataSetRepository.findOne(oldDataSetId);
				dataSetOld.setStatus(0);
				dataSetRepository.save(dataSetOld);
			}
			
			if (model.getModelStatus() == 2 || model.getModelStatus() == 4) {
				// 删除该模型下的文件
				String path = model.getSavepath();
				FileUtil.deleteDirectory(path);

				Function function = funcitonRepository.findOne(model.getFunctionId());
				Algorithm algorithm = algorithmRepository.findOne(model.getAlgorithmId());
				ModelInfo modelInfo = new ModelInfo();
				modelInfo.setModelName(model.getName());
				modelInfo.setFunctionName(function.getName());
				modelInfo.setAlgorithmName(algorithm.getName());
				ModelPublisher modelPublisher = new ModelPublisher();
				modelPublisher.publishModel(modelInfo, true);
			}
		}

		return true;
	}

	@Override
	public boolean trainModel(String modelId) {
		Model model = modelRepository.findOne(modelId);
		if (null == model) {
			throw new AdminException("训练失败，该模型不存在");
		}
		String dataSetId = model.getDataSetId();
		DataSet dataSet = dataSetRepository.findOne(dataSetId);
		dataSet.setStatus(1);
		dataSetRepository.save(dataSet);

		// 训练中
		model.setModelStatus(1);
		modelRepository.save(model);

		Function function = funcitonRepository.findOne(model.getFunctionId());
		Algorithm algorithm = algorithmRepository.findOne(model.getAlgorithmId());
		String algorithmName = algorithm.getName();
		String functionName = function.getName();
		IModelTrainer modelTrainer;
		String modelInfo = null;
		TrainResult trainResult = null;
		try {
			modelTrainer = ModelTrainerFactory.getInstance().getModelTrainer(model, algorithmName);
			String resultJson = modelTrainer.trainModel(model, dataSetId);

			modelInfo = JSON.parseObject(resultJson).getString("result");
			LogManager.method("modelInfo processor INFO:" + modelInfo);

			trainResult = JSON.parseObject(modelInfo, TrainResult.class);
		} catch (BoncException e1) {
			// 训练失败
			Model modelE = modelRepository.findOne(modelId);
			modelE.setModelStatus(6);
			modelRepository.save(modelE);
			LogManager.Exception(e1);
		}

		ServerInformation serverInfo = trainResult.getServerInfo();
		String userName = serverInfo.getUsername();
		String password = serverInfo.getPassword();
		String host = serverInfo.getIp();
		int port = Integer.parseInt(serverInfo.getSshPort());
		String remoteFile = trainResult.getModelPath();

		String modelName = model.getName();
		String localDir = MODEL_SAVE_PATH + File.separator + functionName + File.separator + algorithmName
				+ File.separator + modelName;
		// 判断该文件夹下是否存在文件，存在的话删除该文件夹以及文件夹里所有文件
		File file = new File(localDir);
		if (file.exists()) {
			FileUtil.deleteDirectory(localDir);
		}
		file.mkdirs();
		// 重新添加训练完成的模型文件
		try {
			List<String> files = SSHUtil.getFileList(userName, password, host, port, remoteFile);
			for (String filePath : files) {
				File fileInfo = new File(localDir + File.separator + new File(filePath).getName());
				OutputStream os = new FileOutputStream(fileInfo);
				SSHUtil.getFile(userName, password, host, port, filePath, os);
			}
		} catch (ConnectionException e) {
			LogManager.Exception(e);
		} catch (PathNotFoundException e) {
			LogManager.Exception(e);
		} catch (IOException e) {
			LogManager.Exception(e);
		}

		// 模型训练完成修改状态
		Model modelN = modelRepository.findOne(modelId);
		modelN.setModelStatus(2);
		modelN.setSavepath(localDir);
		modelN.setTrainedTime(new Date());
		modelRepository.save(modelN);

		// 数据集状态修改为可编辑状态
		DataSet dataSetN = dataSetRepository.findOne(dataSetId);
		dataSetN.setStatus(0);
		dataSetRepository.save(dataSetN);

		return true;
	}

	@Override
	public void applyModel(String modelId) {
		Model model = modelRepository.findOne(modelId);
		if (null == model) {
			throw new AdminException("应用失败，该模型不存在");
		}
		// 已下发的删除原文件
		if (4 == model.getModelStatus()) {
			String path = model.getSavepath();
			FileUtil.deleteDirectory(path);
		}
		// 下发中
		model.setModelStatus(3);
		modelRepository.save(model);

		// redis下发消息
		String modelPath = model.getSavepath();
		Function function = funcitonRepository.findOne(model.getFunctionId());
		Algorithm algorithm = algorithmRepository.findOne(model.getAlgorithmId());

		ModelInfo modelInfo = new ModelInfo();
		modelInfo.setModelName(model.getName());
		modelInfo.setFunctionName(function.getName());
		modelInfo.setAlgorithmName(algorithm.getName());
		modelInfo.setModelPath(modelPath);
		ModelPublisher modelPublisher = new ModelPublisher();
		try {
			modelPublisher.publishModel(modelInfo, false);
		} catch (AdminException e) {
			// 下发失败
			Model modelE = modelRepository.findOne(modelId);
			modelE.setModelStatus(5);
			modelRepository.save(modelE);
		}

		// 下发完成
		Model modelN = modelRepository.findOne(modelId);
		modelN.setModelStatus(4);
		modelRepository.save(modelN);
	}

	@Override
	public void downloadModel(String modelIds, String fileName, HttpServletResponse response) {
		// 多个文件压缩，单个文件不压缩
		String[] modelIdArr = modelIds.split(",");
		String modelPath = "";
		List<String> modelIdsList = new ArrayList<>();
		for (String modelId : modelIdArr) {
			Model model = modelRepository.findOne(modelId);
			if (model.getModelStatus() >= 2) {
				modelIdsList.add(modelId);
			}
		}
		// 判断选中的模型是否全不能下载
		for (String modelId : modelIdsList) {
			int modelNum = 0;
			Model model = modelRepository.findOne(modelId);
			if (2 != model.getModelStatus() && 4 != model.getModelStatus()) {
				modelNum += 1;
			}
			if (modelNum == modelIdArr.length) {
				throw new AdminException("下载失败，当前选中的模型均不符合下载条件");
			}
		}
		if (modelIdsList.size() > 1) {
			// 批量下载
			Model model = null;
			List<String> functionNames = new ArrayList<>();
			List<String> algorithmNames = new ArrayList<>();
			List<String> modelNamesList = new ArrayList<>();
			for (String modelId : modelIdsList) {
				model = modelRepository.findOne(modelId);
				modelNamesList.add(model.getName());
				// 得到该模型所有路径的上一级路径
				String functionId = model.getFunctionId();
				Function function = funcitonRepository.findOne(functionId);
				String functionName = function.getName();
				if (!functionNames.contains(functionName)) {
					functionNames.add(functionName);
				}
				String algorithmId = model.getAlgorithmId();
				Algorithm algorithm = algorithmRepository.findOne(algorithmId);
				String algorithmName = algorithm.getName();
				if (!algorithmNames.contains(algorithmName)) {
					algorithmNames.add(algorithmName);
				}
			}
			// 添加功能一级文件夹
			if (functionNames.size() == 1) {
				if (algorithmNames.size() > 1) {
					modelPath = MODEL_SAVE_PATH + File.separator + functionNames.get(0);
				} else {
					modelPath = MODEL_SAVE_PATH + File.separator + functionNames.get(0) + File.separator
							+ algorithmNames.get(0);
				}
			} else {
				modelPath = MODEL_SAVE_PATH;
			}

			if (null != modelPath && !modelPath.isEmpty()) {
				modelPath.replace("/", "\\");
				File dir = new File(modelPath);
				ZipOutputStream out;
				try {
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition", "attachment; filename=" + "models.zip");
					out = new ZipOutputStream(response.getOutputStream());
					com.bonc.uni.nlp.utils.ZipUtil.doCompress(dir, out, modelNamesList);
					out.close();
				} catch (IOException e) {
					LogManager.Exception(e);
				}
			}
		} else if (modelIdsList.size() == 1) {
			// 下载单个模型
			Model model = modelRepository.findOne(modelIdsList.get(0));
			modelPath = model.getSavepath();
			if (null != modelPath && !modelPath.isEmpty()) {
				modelPath.replace("/", "\\");
				File dir = new File(modelPath);
				try {
					if (dir.exists()) {
						if (dir.isDirectory()) {
							File[] files = dir.listFiles();
							if (files.length > 1 || files.length == 1 && files[0].isDirectory()) {
								response.setContentType("APPLICATION/OCTET-STREAM");
								response.addHeader("Content-Disposition", "attachment;filename=\""
										+ new String((model.getName() + ".zip").getBytes("GBK"), "ISO8859_1") + "\"");
								ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
								com.bonc.uni.nlp.utils.ZipUtil.doCompress(dir, out);
								out.close();
							} else if (files.length == 1 && !files[0].isDirectory()) {
								String algorithmId = model.getAlgorithmId();
								Algorithm algorithm = algorithmRepository.findOne(algorithmId);
								Function function = funcitonRepository.findOne(algorithm.getFunctionId());
								String functionName = function.getName();
								String algorithmName = algorithm.getName();
								String singleFileName = files[0].getName();
								String fileType = singleFileName.substring(singleFileName.lastIndexOf("."));
								String name = singleFileName.substring(0, singleFileName.lastIndexOf("."));
								String modelName = model.getName();
								response.setContentType("application/x-msdownload");
								response.addHeader("Content-Disposition", "attachment;filename=\""
										+ new String((name + fileType).getBytes("GBK"), "ISO8859_1") + "\"");
								String fullFileName = MODEL_SAVE_PATH + File.separator + functionName + File.separator
										+ algorithmName + File.separator + modelName + File.separator + singleFileName;
								FileInputStream inStream = new FileInputStream(fullFileName);
								ServletOutputStream outputStream = response.getOutputStream();
								byte[] b = new byte[1024];
								int len;
								while ((len = inStream.read(b)) > 0) {
									outputStream.write(b, 0, len);
								}
								inStream.close();
							}
							if (files.length == 0) {
								throw new AdminException("下载失败，该模型下没有文件");
							}
						}
					}
				} catch (Exception e) {
					LogManager.Exception(e);
				}
			}
		} else {
			throw new AdminException("没有可下载的模型");
		}
	}

	@Override
	public void uploadModel(String modelName, String algorithmId, MultipartFile[] files) {
		String saveFileDir = null;
		Algorithm algorithm = algorithmRepository.findOne(algorithmId);
		Function function = funcitonRepository.findOne(algorithm.getFunctionId());
		String functionName = function.getName();
		String algorithmName = algorithm.getName();
		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				continue;
			}
			if (null == algorithm) {
				throw new AdminException("上传模型失败，该算法不存在");
			}
			// 判断该算法下此模型名称是否存在
			Model exisitedModel = modelRepository.findOneByNameAndAlgorithmId(modelName, algorithmId);
			if (null != exisitedModel) {
				throw new AdminException("上传失败，该算法下此模型名称已经存在");
			}
			String fileName = file.getOriginalFilename();
			// 创建名为functionName-algorithmName-modelName的文件夹，存放model文件
			saveFileDir = MODEL_SAVE_PATH + File.separator + functionName + File.separator + algorithmName
					+ File.separator + modelName;
			File pathDir = new File(saveFileDir);
			if (!pathDir.exists()) {
				pathDir.mkdirs();
			}
			int index = fileName.lastIndexOf(".");
			String fileType = fileName.substring(index);
			if (".zip".equals(fileType) || ".rar".equals(fileType)) {
				String tempZipSaveFileDir = MODEL_SAVE_PATH + File.separator + functionName + File.separator
						+ algorithmName + File.separator + "tempModelZip";
				String tempSaveFileDir = MODEL_SAVE_PATH + File.separator + functionName + File.separator
						+ algorithmName + File.separator + "tempModel";

				File tempFileDir = new File(tempZipSaveFileDir);
				if (!tempFileDir.exists()) {
					tempFileDir.mkdirs();
				}
				if (!modelName.equals(fileName.substring(0, fileName.lastIndexOf(".")))) {
					String tempStrFileOld = tempZipSaveFileDir + File.separator + fileName;
					File zipFile = new File(tempStrFileOld);
					
					FileOutputStream out = null;
					InputStream inStream = null;
					try {
						byte[] b = new byte[1024];
						int len;
						out = new FileOutputStream(zipFile);
						inStream = file.getInputStream();
						while ((len = inStream.read(b)) > 0) {
							out.write(b, 0, len);
						}
					} catch (IOException e) {
						LogManager.Exception(e);
					} finally{
						try {
							if(null != out){
								out.close();
							}
							if(null != inStream){
								inStream.close();
							}
						} catch (IOException e) {
							LogManager.Exception(e);
						}
					}
					try {
						NewZipUtil.unZiFiles(tempStrFileOld, tempSaveFileDir);
						String tempSaveFileDirPath = tempSaveFileDir + File.separator + fileName.substring(0, index);
						com.bonc.uni.nlp.utils.FileUtil.copyFolder(tempSaveFileDirPath, saveFileDir);
						com.bonc.uni.nlp.utils.FileUtil.deleteDirectory(tempSaveFileDir, true);
						FileUtil.deleteDirectory(tempZipSaveFileDir);
					} catch (Exception e) {
						LogManager.Exception(e);
					}
				}
			} else {
				byte[] b = new byte[1024];
				int len;
				try {
					saveFileDir = MODEL_SAVE_PATH + File.separator + functionName + File.separator + algorithmName
							+ File.separator + modelName + File.separator + fileName;
					pathDir = new File(saveFileDir);
					FileOutputStream out = new FileOutputStream(pathDir);
					InputStream inStream = file.getInputStream();
					while ((len = inStream.read(b)) > 0) {
						out.write(b, 0, len);
					}
					out.flush();
					out.close();
					inStream.close();
				} catch (Exception e) {
					LogManager.Exception(e);
				}
			}
		}
		String savePath = MODEL_SAVE_PATH + File.separator + functionName + File.separator + algorithmName
				+ File.separator + modelName;
		Model model = new Model();
		model.setName(modelName);
		model.setAlgorithmId(algorithmId);
		model.setCreateTime(new Date());
		model.setFunctionId(algorithm.getFunctionId());
		model.setSavepath(savePath);
		model.setOperation("custom_upload");
		modelRepository.save(model);
	}

}
