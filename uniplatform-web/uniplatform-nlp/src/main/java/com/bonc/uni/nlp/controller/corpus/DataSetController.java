package com.bonc.uni.nlp.controller.corpus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.constant.ResourcePathConstant;
import com.bonc.uni.nlp.dao.classify.ClassifyObjectRepository;
import com.bonc.uni.nlp.dao.model.DataSetRepository;
import com.bonc.uni.nlp.entity.model.DataSet;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.corpus.IClassifyCorpusService;
import com.bonc.uni.nlp.service.corpus.ICorpusSetService;
import com.bonc.uni.nlp.service.corpus.ICorpusTypeService;
import com.bonc.uni.nlp.service.corpus.IDataSetService;
import com.bonc.uni.nlp.utils.NewZipUtil;
import com.bonc.uni.nlp.utils.es.ESManager;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

@RestController
@RequestMapping(value = "/nlap/admin/corpusMgmt/dataSet")
public class DataSetController {

	@Autowired
	ICorpusSetService corpusSetService;

	@Autowired
	IDataSetService dataSetService;

	@Autowired
	IClassifyCorpusService classifyCorpusService;

	@Autowired
	ClassifyObjectRepository objectRepository;

	@Autowired
	ICorpusTypeService corpusTyleService;

	@Autowired
	DataSetRepository dataSetRepository;

	/**
	 * 手动添加数据集
	 * 
	 * @param name
	 * @param corpusTypeId
	 * @param functionId
	 * @param classifyId
	 * @param corpusSetId
	 * @param random
	 * @param coupusNum
	 * @return
	 */
	@RequestMapping(value = "/addDataSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String addDataSet(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "corpusTypeId", required = false) String corpusTypeId,
			@RequestParam(value = "functionId", required = false) String functionId,
			@RequestParam(value = "classifyId", required = false) String classifyId,
			@RequestParam(value = "corpusSetId", required = false) String corpusSetId,
			@RequestParam(value = "random", required = false) boolean random,
			@RequestParam(value = "corpusNum", required = false) Integer coupusNum) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/addDataset");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "创建成功!");
		String treatedName = name.trim();
		if (dataSetService.existSameDataSet(treatedName, functionId)) {
			returnMap.put("status", "400");
			returnMap.put("msg", "该功能下已经存在同名数据集!");
			return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
		}
		DataSet dataSet = dataSetService.createDataSet(treatedName, corpusTypeId, corpusSetId, functionId, classifyId,
				false);
		try {
			if (random) {
				// 在选择的语料集中随机的抽取一定数量的语料
				corpusSetService.getRandomCorpusFromSet(dataSet.getId(), corpusSetId, classifyId, coupusNum);
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		}

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/addDataset");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);

	}

	/**
	 * 上传一个数据集
	 * 
	 * @param name
	 * @param corpusTypeId
	 * @param functionId
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/uploadDataSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String uploadDataSet(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "corpusTypeId", required = false) String corpusTypeId,
			@RequestParam(value = "functionId", required = false) String functionId,
			@RequestParam(value = "file", required = true) MultipartFile file) {
		LogManager.process("Process in controller: /nlap/admin/corpusMgmt/dataSet/uploadDataSet");

		long uploadStartTime = System.currentTimeMillis();

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "添加成功！");
		
		if (null == name || StringUtil.isEmpty(name)) {
			returnMap.put("status", "400");
			returnMap.put("msg", "数据集名字不能为空！");
			try {
				file.getInputStream().close();
			} catch (IOException e) {
				LogManager.Exception(e);
			}
			return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
		}
		
		if (!file.getOriginalFilename().endsWith("zip") && !file.getOriginalFilename().endsWith("rar")) {
			returnMap.put("status", "400");
			returnMap.put("msg", "请上传zip 或者 rar 文件！");
			try {
				file.getInputStream().close();
			} catch (IOException e) {
				LogManager.Exception(e);
			}
			return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);

		}
		
		String treatedName = name.trim();
		if (dataSetService.existSameDataSet(treatedName, functionId)) {
			returnMap.put("status", "400");
			returnMap.put("msg", "该功能下已经存在同名数据集!");
			try {
				file.getInputStream().close();
			} catch (IOException e) {
				LogManager.Exception(e);
			}
			return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
		}
		try {
			dataSetService.uploadDataSet(file, treatedName, corpusTypeId, functionId);
            Thread.currentThread().sleep(1000);
  			LogManager.debug("uploadDataSet ：等待1s ");
		} catch (Exception e) {
  			LogManager.debug("uploadDataSet ： 上传数据集且线程等待发生异常  :", e);
			returnMap.put("status", "400");
			returnMap.put("msg", e.getMessage());
		}

		long uploadEndTime = System.currentTimeMillis();
		LogManager.info("上传数据集总的时间 ：" + (uploadEndTime - uploadStartTime) + "ms");
		
		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/uploadDataSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 删除一个数据集
	 * 
	 * TODO : 删除从语料集生成而来的数据集
	 * @param dataSetId
	 * @return
	 */
	@RequestMapping(value = "/deleteDataSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteDataSet(@RequestParam(value = "dataSetId", required = true) String dataSetId) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/deleteDataSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "删除成功！");
		try {
			dataSetService.deleteDataSet(dataSetId);
		} catch (AdminException e) {
			returnMap.put("status", "400");
			returnMap.put("msg", e.getMessage());
		}

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/deleteDataSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 编辑一个数据集
	 * 
	 * @param dataSetIdjgjg
	 * 
	 * @return
	 */
	@RequestMapping(value = "/updateDataSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String updateDataSet(@RequestParam(value = "dataSetId", required = true) String dataSetId,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "functionId", required = true) String functionId) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/deleteDataSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "编辑成功！");
		String treatedName = name.trim();
		if (dataSetService.existSameDataSet(treatedName, functionId)) {
			returnMap.put("status", "400");
			returnMap.put("msg", "该功能下已经存在同名数据集!");
			return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
		}
		dataSetService.updateDataSet(dataSetId, treatedName);

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/deleteDataSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 根据功能id过滤数据集
	 * 
	 * @param functionId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/listDataSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String listDataSet(@RequestParam(value = "functionId", required = true) String functionId,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/listDataSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "过滤成功！");
		returnMap.put("dataSet", dataSetService.getDataSetByFunctionId(functionId, pageIndex, pageSize));
		returnMap.put("dataSetNum", dataSetService.countDataByFunctionId(functionId));

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/listDataSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取语料类型和功能列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/listTypeAndFunction", method = { RequestMethod.GET, RequestMethod.POST })
	public String listTypeAndFunction() {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/listTypeAndFunction");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "过滤成功！");
		returnMap.put("data", corpusTyleService.getCorpusTypeAndFunction());

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/listTypeAndFunction");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取数据集详情
	 * 
	 * @param classifyId
	 * @return
	 */
	@RequestMapping(value = "/listDataSetDetail", method = { RequestMethod.GET, RequestMethod.POST })
	public String listDataSetDetail(@RequestParam(value = "dataSetId", required = true) String dataSetId,
			@RequestParam(value = "classifyId", required = true) String classifyId,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/listDataSetDetail");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "过滤成功！");
		if (!dataSetRepository.getOne(dataSetId).isUpload()) {
			returnMap.put("corpusSetId", dataSetRepository.getOne(dataSetId).getCorpusSetId());
		}
		returnMap.put("detail", dataSetService.getDataSetDetail(dataSetId, pageIndex, pageSize));
		returnMap.put("totalNum", objectRepository.countByClassifyId(classifyId));
		returnMap.put("isUpload", dataSetRepository.getOne(dataSetId).isUpload());

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/listDataSetDetail");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取数据集下的语料信息
	 * 
	 * @param dataSetId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/listDataSetCorpus", method = { RequestMethod.GET, RequestMethod.POST })
	public String listDataSetCorpus(@RequestParam(value = "dataSetId", required = true) String dataSetId,
			@RequestParam(value = "objectId", required = false) String higherLevelId,
			@RequestParam(value = "needText", required = true) boolean needText,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/listDataSetDetail");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "过滤成功！");
		Map<String, Object> corpusMap = dataSetService.getCorpus(dataSetId, higherLevelId, needText, pageIndex,
				pageSize);
		returnMap.put("corpus", corpusMap.get("corpus"));
		returnMap.put("totalNum", corpusMap.get("totalNum"));

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/listDataSetDetail");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 搜索语料
	 * 
	 * @param functionId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/searchDataSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchDataSet(@RequestParam(value = "keyWord", required = true) String keyWord,
			@RequestParam(value = "dataSetId", required = true) String dataSetId,
			@RequestParam(value = "objectId", required = false) String higherLevelId,
			@RequestParam(value = "needText", required = true) boolean needText,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/searchDataSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "过滤成功！");

		Map<String, Object> corpusMap = new HashMap<>();
		if (StringUtil.isEmpty(keyWord) || null == keyWord) {
			corpusMap = dataSetService.getCorpus(dataSetId, higherLevelId, needText, pageIndex, pageSize);
		} else {
			corpusMap = dataSetService.getCorpus(dataSetId, higherLevelId, keyWord, needText, pageIndex,
						pageSize);
		}

		returnMap.put("corpous", corpusMap.get("corpus"));
		returnMap.put("totalNum", corpusMap.get("totalNum"));

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/searchDataSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 操作穿梭框
	 * 
	 * TODO: 参数的修改 拼接 corpusId 和 objectId
	 * @param dataSetId
	 * @param corpusSetId
	 * @param addCorpusIds
	 * @param removeCorpusIds
	 * @return
	 */
	@RequestMapping(value = "/operateShuttle", method = { RequestMethod.GET, RequestMethod.POST })
	public String operateShuttle(@RequestParam(value = "dataSetId", required = true) String dataSetId,
			@RequestParam(value = "corpusSetId", required = true) String corpusSetId,
			@RequestParam(value = "addCorpusIds", required = true) String[] addCorpusIds,
			@RequestParam(value = "removeCorpusIds", required = true) String[] removeCorpusIds) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/operateShuttle");
		
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "操作成功！");
		if (0 != addCorpusIds.length && null != addCorpusIds) {
			dataSetService.addCorpus(addCorpusIds, dataSetId, corpusSetId);
		}
		if (0 != removeCorpusIds.length && null != removeCorpusIds) {
			dataSetService.removeCorpus(removeCorpusIds, dataSetId, corpusSetId);
		}
		try {
			Thread.currentThread().sleep(1000);
			LogManager.debug("operateShuttle ： 等待1s");
		} catch (InterruptedException ie) {
			LogManager.Exception("operateShuttle ： 上传数据集线程等待发生异常  :", ie);
		}
		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/operateShuttle");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 批量删除语料
	 * 
	 * TODO : 参数的修改 拼接 corpusId 和 objectId
	 * @param dataSetId
	 * @param corpusIds
	 * @return
	 */
	@RequestMapping(value = "/deleteCorpus", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteCorpus(@RequestParam(value = "dataSetId", required = true) String dataSetId,
			@RequestParam(value = "corpusIds", required = true) String[] corpusIds) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/deleteCorpus");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "删除语料成功！");
		dataSetService.deleteCorpus(corpusIds, dataSetId);
		try {
			Thread.currentThread().sleep(1000);
			LogManager.debug("deleteCorpus ： 等待1s");
		} catch (InterruptedException ie) {
			LogManager.Exception("deleteCorpus ： 上传数据集线程等待发生异常  :", ie);
		}
		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/deleteCorpus");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 下载数据集
	 * 
	 * TODO : 参数修改 corpusId 拼接 objectId
	 * @param dataSetId
	 * @return
	 */
	@RequestMapping(value = "/downloadDataSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String downloadDataSet(@RequestParam(value = "dataSetId", required = true) String dataSetId,
			@RequestParam(value = "corpusIds", required = false) String[] corpusIds, 
			@RequestParam(value = "objectId", required = false) String objectId,
			HttpServletResponse response) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/downloadDataSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "下载成功！");
		Map<String, List<String>> classifiedCorpus = new HashMap<>();
		if (null == corpusIds || 0 == corpusIds.length) {
			if (null == objectId || StringUtil.isEmpty(objectId)) {
				// 下载整个数据集
				LogManager.debug("DataSetController : download the corpus of object : " + objectId);
				classifiedCorpus = dataSetService.classifyDataSet(dataSetId);
			} else {
				// 下载某个对象下的全部语料
				LogManager.debug("DataSetController : download the corpus of dataset : " + dataSetId);
				classifiedCorpus = dataSetService.objectCorpus(dataSetId, objectId);
			}

		} else {
			// 下载选定的语料
			LogManager.debug("DataSetController : download the corpus selected ");
			classifiedCorpus = dataSetService.classifyCorpus(dataSetId, corpusIds);

		}
		String classifyId = dataSetRepository.getOne(dataSetId).getClassifyId();
		String corpusPath = classifyCorpusService.generateCorpusFolder(classifiedCorpus, classifyId);
		LogManager.debug("The savePath of download corpus : " + corpusPath);
		String zipName = dataSetRepository.getOne(dataSetId).getName() + ".zip";
		try {
			String savePath = ResourcePathConstant.ZIP_PATH + File.separator + zipName;
			NewZipUtil.zipFiles(corpusPath, savePath);
			File zipFile = new File(savePath);
			LogManager.debug(" The savePth of download zipFle : " + savePath);
	        response.setContentLength((int) zipFile.length());  
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(zipName.getBytes("GBK"), "ISO8859_1"));
	        FileInputStream fis = new FileInputStream(zipFile);  			
	        BufferedInputStream buff = new BufferedInputStream(fis);  
	        OutputStream myout = response.getOutputStream();// 从response对象中得到输出流,准备下载  
	        byte[] b = new byte[1024];// 相当于我们的缓存  
	        long k = 0;// 该值用于计算当前实际下载了多少字节  
	        while (k < zipFile.length()) {  
	            int j = buff.read(b, 0, 1024);  
	            k += j;  
	            myout.write(b, 0, j);  
	        }  
	        myout.flush();  
	        buff.close();  
	        zipFile.delete();
		} catch (Exception e) {
			LogManager.Exception("DataSetController : downloadDataSet exception : ", e);
		}

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/dataSet/downloadDataSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	
	/**
	 * 检索数据集
	 * 
	 * @param functionId
	 * @param keyWord
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/searchDataSetName", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchDataSet(@RequestParam(value = "functionId", required = true) String functionId,
			@RequestParam(value = "keyWord", required = true) String keyWord,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/searchDataSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "检索成功！");

		List<Map<String, Object>> dataSets = new ArrayList<>();
		int totalNum = 0;
		if (null == keyWord || StringUtil.isEmpty(keyWord)) {
			// 获取全部数据集
			dataSets = dataSetService.getDataSetByFunctionId(functionId, pageIndex, pageSize);
			totalNum = dataSetService.countDataByFunctionId(functionId);
		} else {
			// 进行检索
			Map<String, Object> map = dataSetService.searchDataSet(functionId, keyWord, pageIndex, pageSize);
			dataSets = (List<Map<String, Object>>) map.get("dataSet");
			totalNum = (int) map.get("totalNum");
		}
		returnMap.put("corpus", dataSets);
		returnMap.put("totalNum", totalNum);

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/corpusSet/searchCorpusSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	@RequestMapping(value = "/clearES", method = { RequestMethod.GET, RequestMethod.POST })
	public String clearES() {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/searchDataSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "检索成功！");

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("DELETE FROM ").append(SystemConfig.ELASTICSEARCH_TYPE);
		LogManager.debug(" sql4es :" + sBuilder.toString());

		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();
			statement.executeUpdate(sBuilder.toString());

		} catch (Exception e) {
			LogManager.Exception(e);
		}
		

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/corpusSet/searchCorpusSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	@RequestMapping(value = "/testUpdate", method = { RequestMethod.GET, RequestMethod.POST })
	public String testEncode(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "corpusSetId", required = true) String corpusSetId,
			@RequestParam(value = "objectId", required = true) String objectId) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/searchDataSet");
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();

			// UPDATE index.type SET field1=value, fiedl2='value',
			// "doc.field"=value WHERE condition
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("UPDATE ").append(SystemConfig.ELASTICSEARCH_INDEX).append(".")
					.append(SystemConfig.ELASTICSEARCH_TYPE)
					.append(" SET \"corpusSet.objectId\" = '").append(objectId).append("'")
					.append(" WHERE _id = '").append(id).append("'");
			LogManager.debug("Sql of updateObjectBySetId : " + sBuilder.toString());

			statement.executeUpdate(sBuilder.toString());
		} catch (Exception e) {
			LogManager.Exception("deleteCorpusById Exception : ", e);
		} finally {
			ESManager.close(conn, statement, null);
		}		
		return JSON.toJSONString("true", SerializerFeature.DisableCircularReferenceDetect);
	}
	
	
	
	@RequestMapping(value = "/testBox", method = { RequestMethod.GET, RequestMethod.POST })
	public String testBox(String dataSetId,String corpusSetId) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/dataSet/searchDataSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "检索成功！");
		List<String> returnList = new ArrayList<>();
		StringBuilder sBuilder = new StringBuilder();
		
		sBuilder.append("SELECT _id FROM ").append(SystemConfig.ELASTICSEARCH_TYPE);
		if (null != corpusSetId && !StringUtil.isEmpty(corpusSetId)) {
			sBuilder.append(" WHERE corpusSet.corpusSetId = '").append(corpusSetId).append("'");
		}
		if (null != dataSetId && !StringUtil.isEmpty(dataSetId)) {
			sBuilder.append(" AND dataSet.dataSetId <> '").append(dataSetId).append("'");
		}
		LogManager.debug(" sql4es of initBox:" + sBuilder.toString());

		ResultSet rSet = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();
			rSet = statement.executeQuery(sBuilder.toString());
			while (rSet.next()) {
				String corpusId = rSet.getString("_id");
				returnList.add(corpusId);
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}
		returnMap.put("corpus", returnList);

		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}
}
