package com.bonc.uni.nlp.service.Impl.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.constant.ResourcePathConstant;
import com.bonc.uni.nlp.dao.classify.ClassifyObjectRepository;
import com.bonc.uni.nlp.dao.classify.ClassifyRepository;
import com.bonc.uni.nlp.dao.corpus.CorpusSetRepository;
import com.bonc.uni.nlp.dao.model.DataSetRepository;
import com.bonc.uni.nlp.entity.classify.ClassifyObject;
import com.bonc.uni.nlp.entity.corpus.CorpusSet;
import com.bonc.uni.nlp.entity.model.DataSet;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.corpus.IDataSetService;
import com.bonc.uni.nlp.utils.Encoding;
import com.bonc.uni.nlp.utils.NewZipUtil;
import com.bonc.uni.nlp.utils.ResourceFileUtil;
import com.bonc.uni.nlp.utils.TimeUtil;
import com.bonc.uni.nlp.utils.es.ESManager;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.elasticsearch.esUtil.search.ISearchService;
import com.bonc.usdp.odk.logmanager.LogManager;

@Service
public class DataSetServiceImpl implements IDataSetService {

	@Autowired
	DataSetRepository dataSetRepository;

	@Autowired
	CorpusSetRepository setRepository;

	@Autowired
	ClassifyRepository classifyRepository;

	@Autowired
	ClassifyObjectRepository objectRepository;

	@Autowired
	ClassifyCorpusServiceImpl classifyCorpusServiceImpl;

	public Sort sortByCreateTime = new Sort(Sort.Direction.DESC, "createTime");

	/**
	 * ES 根据语料id编辑数组关系
	 * 
	 * @param map
	 * @param corpusId
	 * @param flag
	 *            true:add false:delete
	 */
	public void updateArrayToES(List<Map<String, String>> list, String corpusId, boolean flag) {
		try {
			ISearchService searchService = ESManager.getInstance().getSearchService();
			searchService.updateArray(SystemConfig.ELASTICSEARCH_INDEX, SystemConfig.ELASTICSEARCH_TYPE, corpusId,
					"dataSet", flag, list);
		} catch (UnknownHostException e) {
			LogManager.Exception("updateArrayToES Exception :", e);
		}

	}
	

	/**
	 * ES 编辑数组关系
	 * 
	 * @param map
	 * @param corpusId
	 * @param flag
	 *            true:add false:delete
	 */
	private void updateArrayByQuery(List<Map<String, String>> list, QueryBuilder query, boolean flag) {
		try {
			ISearchService searchService = ESManager.getInstance().getSearchService();
			searchService.updateArrayByQuery(SystemConfig.ELASTICSEARCH_INDEX, SystemConfig.ELASTICSEARCH_TYPE,
					"dataSet", flag, list, query);
		} catch (UnknownHostException e) {
			LogManager.Exception("updateArrayToES Exception :", e);
		}

	}

	/**
	 * 计算语料数量
	 * 
	 * @param dataSetId
	 *            数据集id
	 * @param objectId
	 *            对象id
	 * @param keyWord
	 *            关键字
	 * @return
	 */
	public int countCorpus(String dataSetId, String objectId, String keyWord) {
		int num = 0;

		ResultSet rs = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();

			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("SELECT count(_id) FROM ").append(SystemConfig.ELASTICSEARCH_TYPE);
			if (null != objectId) {
				sBuilder.append(" WHERE (dataSet.dataSetId = '").append(dataSetId).append("'")
						.append(" AND dataSet.objectId = '" + objectId + "')");
			} else {
				sBuilder.append(" WHERE dataSet.dataSetId = '").append(dataSetId).append("'");
			}
			if (null != keyWord) {
				sBuilder.append(" AND text = '" + keyWord + "'");
			}
			LogManager.debug("Sql of countCorpus in dataSet : " + sBuilder.toString());

			rs = statement.executeQuery(sBuilder.toString());
			while (rs.next()) {
				num = rs.getInt(1);
			}

		} catch (Exception e) {
			LogManager.Exception("countCorpus Exception : ", e);
		} finally {
			ESManager.close(conn, statement, rs);
		}
		return num;
	}

	/**
	 * 获取语料信息
	 * 
	 * @param keyWord
	 *            关键字
	 * @param dataSetId
	 *            数据集id
	 * @param objectId
	 *            对象id
	 * @param needText
	 *            需要文本
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getCorpusInfo(String keyWord, String dataSetId, String objectId, boolean needText,
			Integer pageIndex, Integer pageSize) {
		LogManager.Process("Process in service: the method getCorpusInfo of DataSetServiceImpl");

		List<Map<String, Object>> returnList = new ArrayList<>();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT _id, title,");
		if (needText) {
			sBuilder.append(" text,");
		}
		sBuilder.append(" update_time, create_user, dataSet FROM ").append(SystemConfig.ELASTICSEARCH_TYPE);
		if (null != objectId) {
			sBuilder.append(" WHERE (dataSet.dataSetId = '").append(dataSetId).append("'")
					.append(" AND dataSet.objectId = '" + objectId + "')");
		} else {
			sBuilder.append(" WHERE dataSet.dataSetId = '").append(dataSetId).append("'");
		}
		if (null != keyWord) {
			sBuilder.append(" AND text = '" + keyWord + "'");
		}
		sBuilder.append(" ORDER BY update_time DESC LIMIT ").append((pageIndex - 1) * pageSize).append(",")
				.append(pageSize);
		LogManager.debug(" sql4es :" + sBuilder.toString());

		ResultSet rSet = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();
			rSet = statement.executeQuery(sBuilder.toString());
			while (rSet.next()) {
				Map<String, Object> tempMap = new HashMap<>();
				String corpusId = rSet.getString("_id");
				String objectIdOfCorpus = null;
				ResultSet dataSet = (ResultSet) rSet.getObject("dataSet");
				while (dataSet.next()) {
					if (dataSetId.equals(dataSet.getString("dataSetId"))) {
						objectIdOfCorpus = dataSet.getString("objectId");
					}
				}
				dataSet.close();
				String objectName = objectRepository.getOne(objectIdOfCorpus).getName();
				tempMap.put("_id", corpusId);
				if (needText) {
					tempMap.put("text",
							rSet.getString("text") == null ? "" : rSet.getString("text").replace("''", "'"));
				}
				tempMap.put("title", rSet.getString("title") == null ? "" : rSet.getString("title"));
				tempMap.put("update_time", rSet.getString("update_time") == null ? "" : rSet.getString("update_time"));
				tempMap.put("user", rSet.getString("create_user") == null ? "" : rSet.getString("create_user"));
				tempMap.put("higherLevelId", objectIdOfCorpus);
				tempMap.put("higherLevel", objectName);
				returnList.add(tempMap);
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}

		LogManager.Process("Process out service: the method newGetCorpusInfo of ClassifyCorpusServiceImpl");
		return returnList;
	}

	/**
	 * sql4es 插入数据集语料
	 * 
	 * @param title
	 * @param text
	 * @param corpusSetId
	 * @param objectId
	 * @return
	 */
	public String insertSql(String title, String text, String dataSetId, String objectId) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder sBuilder = new StringBuilder();
		try {
			sBuilder.append("INSERT INTO ").append(SystemConfig.ELASTICSEARCH_TYPE)
					.append(" (title, text, create_user, create_time, update_time, \"dataSet._nested\") VALUES ")
					.append("('").append(title).append("','").append(text).append("','").append("1','")
					.append(formatter.format(TimeUtil.getNowDate())).append("','").append(formatter.format(TimeUtil.getNowDate())).append("','")
					.append("[{\"dataSetId\":\"").append(dataSetId).append("\",").append("\"objectId\":\"")
					.append(objectId).append("\"}]')");
		} catch (Exception e) {
			LogManager.Exception(e);
		}

		return sBuilder.toString();
	}

	/**
	 * 在 ES 根据数据集删除语料
	 * 
	 * @param dataSetId
	 *            数据集id
	 */
	public void deleteCorpusBySetId(String dataSetId) {
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();

			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("DELETE FROM ").append(SystemConfig.ELASTICSEARCH_TYPE)
					.append(" WHERE dataSet.dataSetId = '").append(dataSetId).append("'");
			LogManager.debug("Sql of deleteCorpusBySetId : " + sBuilder.toString());

			statement.executeUpdate(sBuilder.toString());
		} catch (Exception e) {
			LogManager.Exception("deleteCorpusBySetId Exception : ", e);
		} finally {
			ESManager.close(conn, statement, null);
		}

	}

	/**
	 * 在 ES 根据语料id删除语料
	 * 
	 * @param corpusId
	 *            语料id
	 */
	public void deleteCorpusById(String corpusId) {
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();

			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("DELETE FROM ").append(SystemConfig.ELASTICSEARCH_TYPE)
					.append(" WHERE _id = '").append(corpusId).append("'");
			LogManager.debug("Sql of deleteCorpusById : " + sBuilder.toString());
			statement.executeUpdate(sBuilder.toString());
		} catch (Exception e) {
			LogManager.Exception("deleteCorpusById Exception : ", e);
		} finally {
			ESManager.close(conn, statement, null);
		}

	}
	@Override
	public DataSet createDataSet(String name, String corpusTypeId, String corpusSetId, String functionId,
			String classifyId, boolean isUpload) {
		LogManager.process("Process in service: the method createDataSet of DataSetServiceImpl");
		DataSet dataSet = new DataSet();
		dataSet.setName(name);
		dataSet.setCorpusTypeId(corpusTypeId);
		dataSet.setClassifyId(classifyId);
		if (null != corpusSetId || !StringUtil.isEmpty(corpusSetId)) {
			setRepository.getOne(corpusSetId).setUsed(true);;
			dataSet.setCorpusSetId(corpusSetId);
		}
		dataSet.setFunctionId(functionId);
		dataSet.setCreateTime(TimeUtil.getNowDate());
		dataSet.setUpload(isUpload);
		dataSet.setCreateUser("uni");
		dataSetRepository.save(dataSet);

		LogManager.process("Process out service: the method createDataSet of DataSetServiceImpl");
		return dataSet;
	}

	@Override
	public List<Map<String, Object>> getDataSetByFunctionId(String functionId, Integer pageIndex, Integer pageSize) {
		List<Map<String, Object>> returnList = new ArrayList<>();
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sortByCreateTime);
		List<DataSet> dataSets = dataSetRepository.findAllByFunctionId(functionId, pageable);
		for (DataSet dataSet : dataSets) {
			Map<String, Object> tempMap = new HashMap<>();
			if (!dataSet.isUpload()) {
				CorpusSet corpusSet = setRepository.getOne(dataSet.getCorpusSetId());
				tempMap.put("corpusSetId", corpusSet.getId());
				tempMap.put("coupusSetName", corpusSet.getName());
			}
			tempMap.put("status", dataSet.getStatus());
			tempMap.put("isUpload", dataSet.isUpload());
			tempMap.put("dataSetId", dataSet.getId());
			tempMap.put("dataSetName", dataSet.getName());
			tempMap.put("classifyId", dataSet.getClassifyId());
			tempMap.put("classifyName", classifyRepository.getOne(dataSet.getClassifyId()).getName());
			tempMap.put("createTime", dataSet.getCreateTime());
			tempMap.put("corpusNum", countCorpus(dataSet.getId(), null, null));
			returnList.add(tempMap);
		}
		return returnList;
	}

	@Override
	public int countDataByFunctionId(String functionId) {
		return dataSetRepository.countByFunctionId(functionId);
	}

	@Override
	public void updateDataSet(String dataSetId, String newName) {
		dataSetRepository.updateName(newName, dataSetId);
	}

	@Override
	public List<Map<String, Object>> getDataSetDetail(String dataSetId, Integer pageIndex, Integer pageSize) {
		List<Map<String, Object>> returnList = new ArrayList<>();
		DataSet dataSet = dataSetRepository.getOne(dataSetId);
		String classifyId = dataSet.getClassifyId();
		String corpusSetId = null;
		boolean isUpload = dataSet.isUpload();
		if (!isUpload) {
			corpusSetId = dataSet.getCorpusSetId();
		}
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sortByCreateTime);
		List<ClassifyObject> objects = objectRepository.findAllByClassifyId(classifyId, pageable);
		for (ClassifyObject classifyObject : objects) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("objectId", classifyObject.getId());
			tempMap.put("objectName", classifyObject.getName());
			tempMap.put("corpusNum", countCorpus(dataSetId, classifyObject.getId(), null));
			if (!isUpload) {
				tempMap.put("numOfSet",
						classifyCorpusServiceImpl.countCorpus(corpusSetId, classifyObject.getId(), null));
			}
			returnList.add(tempMap);
		}
		return returnList;
	}

	@Override
	public Map<String, Object> getCorpus(String dataSetId, String higherLevelId, boolean needText, Integer pageIndex,
			Integer pageSize) {
		Map<String, Object> returnMap = new HashMap<>();
		int totalNum = 0;
		List<Map<String, Object>> corpus = new ArrayList<>();
		if (StringUtil.isEmpty(higherLevelId) || null == higherLevelId) {
			// 获取数据集下的全部语料
			corpus = getCorpusInfo(null, dataSetId, null, needText, pageIndex, pageSize);
			totalNum =  countCorpus(dataSetId, null, null);
		} else {
			corpus = getCorpusInfo(null, dataSetId, higherLevelId, needText, pageIndex, pageSize);
			totalNum =  countCorpus(dataSetId, higherLevelId, null);
		}
		returnMap.put("corpus", corpus);
		returnMap.put("totalNum", totalNum);
		return returnMap;
	}

	@Override
	public Map<String, Object> getCorpus(String dataSetId, String higherLevelId, String keyWord, boolean needText,
			Integer pageIndex, Integer pageSize) {
		Map<String, Object> returnMap = new HashMap<>();
		List<Map<String, Object>> corpus = new ArrayList<>();
		int totalNum = 0;
		if (StringUtil.isEmpty(higherLevelId) || null == higherLevelId) {
			// 检索数据集下的全部语料
			corpus = getCorpusInfo(keyWord, dataSetId, null, needText, pageIndex, pageSize);
			totalNum = countCorpus(dataSetId, null, keyWord);
		} else {
			// 检索数据集某个对象下的全部语料
			corpus = getCorpusInfo(keyWord, dataSetId, higherLevelId, needText, pageIndex, pageSize);
			totalNum = countCorpus(dataSetId, higherLevelId, keyWord);
		}
		returnMap.put("corpus", corpus);
		returnMap.put("totalNum", totalNum);
		return returnMap;
	}

	/**
	 * 将语料文件读成String
	 * 
	 * @param file
	 * @return
	 */
	private String readCorpusToString(File file) {
		StringBuilder sBuilder = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while (null != (line = reader.readLine())) {
				sBuilder.append(line);
			}
			reader.close();
		} catch (Exception e) {
			LogManager.Exception(e);
		}
		return sBuilder.toString().replace("'", "''");
	}

	@Override
	public void uploadDataSet(MultipartFile multipartFile, String name, String corpusTypeId, String functionId) {
		LogManager.process("Process in service: the method uploadDataSet of DataSetServiceImpl");

		// 清除资源文件
		ResourceFileUtil.clearCorpusFolder();
		ResourceFileUtil.clearZipFolder();
		LogManager.debug("Clear the resource folder");

		// MultipartFile转File
		String fileName = multipartFile.getOriginalFilename();
		String zipFilePath = ResourcePathConstant.ZIP_PATH + File.separator + fileName;
		File zipFile = new File(zipFilePath);
		try {
			multipartFile.transferTo(zipFile);
		} catch (IOException e) {
			LogManager.Exception(e);
		}

		long unZipStartTime = System.currentTimeMillis();
		// zip文件解压
		NewZipUtil.unZiFiles(zipFilePath, ResourcePathConstant.CORPUS_PATH);
		long unZipEndTime = System.currentTimeMillis(); // 解压结束时间
		LogManager.debug("The time of unzip :" + (unZipEndTime - unZipStartTime) + "ms");

		long insertStartTime = System.currentTimeMillis();
		// ES连接准备
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();
		} catch (SQLException e1) {
			LogManager.Exception(e1);
		}

		File file = new File(ResourcePathConstant.CORPUS_PATH);
		String classifyId = null;
		Map<String, List<String>> objectMap = new HashMap<>();
		File[] files = file.listFiles();
		DataSet dataSet = null;
		if (files.length != 1) {
			throw new AdminException("上传失败，压缩文件夹格式错误！");
		}
		for (File classifyFile : files) {
			String classifyFileName = classifyFile.getName();
			LogManager.debug("classifyFileName : " + classifyFileName);
			if (null == classifyRepository.findOneByName(classifyFileName)) {
				throw new AdminException("上传失败，分类体系不存在！");
			}
			classifyId = classifyRepository.findOneByName(classifyFileName).getId();
			File[] objectFiles = classifyFile.listFiles();
			if (0 == objectFiles.length) {
				throw new AdminException("上传失败，无分类体系对象文件夹！");
			}
			// 语料文件总数为0
			int corpusNum = 0;
			for (File objectFile : objectFiles) {
				corpusNum += objectFile.listFiles().length;
			}
			if (0 == corpusNum) {
				throw new AdminException("上传失败，语料数量为0！");
			}
			corpusNum = 0;
			// 创建数据集
			dataSet = createDataSet(name, corpusTypeId, null, functionId, classifyId, true);
			// 遍历对象文件夹，生成对象id - 语料文件 的map
			try {
				for (File objectFile : objectFiles) {

					String objectFileName = objectFile.getName();
					if (null == objectRepository.findOneByNameAndClassifyId(objectFileName, classifyId)) {
						continue;
					}
					String objectId = objectRepository.findOneByNameAndClassifyId(objectFileName, classifyId).getId();
					List<String> corpusIds = new ArrayList<>();
					File[] corpusFiles = objectFile.listFiles();
					// 遍历语料文件
					if (0 != corpusFiles.length) {
						for (File corpusFile : corpusFiles) {
							if (!"UTF-8".equals(Encoding.tryEncoding(corpusFile))
									|| !corpusFile.getName().endsWith("txt")
									|| StringUtil.isEmpty(readCorpusToString(corpusFile))) {
								continue;
							}
							corpusNum++;
							String newSql = insertSql(corpusFile.getName(), readCorpusToString(corpusFile),
									dataSet.getId(), objectId);
							statement.addBatch(newSql);
							corpusFile.delete();
						}
						if (objectMap.containsKey(objectId)) {
							objectMap.get(objectId).addAll(corpusIds);
						} else {
							objectMap.put(objectId, corpusIds);
						}
					}

					objectFile.delete();
				}
				statement.executeBatch();
			} catch (Exception e) {
				LogManager.Exception(e);
			} finally {
				ESManager.close(conn, statement, null);
			}
			classifyRepository.updateIsUsed(true, classifyId);
			classifyFile.delete();
			LogManager.info("语料上传数量 ： " + corpusNum);
		}
		long insertEndTime = System.currentTimeMillis(); // 读取并插入到ES的结束时间

		LogManager.info("读语料并插入到ES的所需时间 ： " + (insertEndTime - insertStartTime) + "ms");
		LogManager.process("Process out service: the method uploadDataSet of DataSetServiceImpl");

	}

	@Override
	public boolean existSameDataSet(String dataSetName, String functionId) {
		if (null == dataSetRepository.findOneByNameAndFunctionId(dataSetName, functionId)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void deleteDataSet(String dataSetId) {
		DataSet dataSet = dataSetRepository.getOne(dataSetId);
		String classifyId = dataSet.getClassifyId();
		
		if (1 == dataSet.getStatus()) {
			new AdminException("该数据集处于使用状态！");
		}
		boolean isUpload = dataSet.isUpload();
		if (isUpload) {
			// 上传的数据集  根据数据集id删除全部语料
			deleteCorpusBySetId(dataSetId);
			dataSetRepository.delete(dataSetId);
			// 处理分类体系的状态
			if (0 == dataSetRepository.countByClassifyId(classifyId)
					&& 0 == setRepository.countByClassifyId(classifyId)) {
				classifyRepository.updateIsUsed(false, classifyId);
			}

		} else {
			// 语料集生成  es编辑数组
			List<Map<String, String>> arr4ES = new ArrayList<>(); 
			for (ClassifyObject object : objectRepository.findAllByClassifyId(classifyId, sortByCreateTime)) {
				Map<String, String> tempMap = new HashMap<>();
				tempMap.put("dataSetId", dataSetId);
				tempMap.put("objectId", object.getId());
				arr4ES.add(tempMap);
			}
			QueryBuilder query = QueryBuilders.nestedQuery("dataSet",
					QueryBuilders.termQuery("dataSet.dataSetId", dataSetId));
			updateArrayByQuery(arr4ES, query, false);

			String corpusSetId = dataSet.getCorpusSetId(); //暂存语料集id
			dataSetRepository.delete(dataSetId);
			// 处理语料集的状态
			if (0 == dataSetRepository.countByCorpusSetId(corpusSetId)) {
				setRepository.updateIsUsed(false, corpusSetId);
			}
		}
	}

	@Override
	public Map<String, List<String>> classifyDataSet(String dataSetId) {
		LogManager.method("Process in service: DataSetServiceImpl/classifyDataSet");

		Map<String, List<String>> returnMap = new HashMap<>();
		String classifyId = dataSetRepository.getOne(dataSetId).getClassifyId();
		List<ClassifyObject> objects = objectRepository.findAllByClassifyId(classifyId, sortByCreateTime);

		ResultSet rSet = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();

			for (ClassifyObject classifyObject : objects) {
				String objectId = classifyObject.getId();
				if (0 == countCorpus(dataSetId, objectId, null)) {
					continue;
				}
				List<String> corpusIds = new ArrayList<>();

				StringBuilder sBuilder = new StringBuilder();
				sBuilder.append("SELECT _id FROM ").append(SystemConfig.ELASTICSEARCH_TYPE)
						.append(" WHERE (dataSet.dataSetId = '").append(dataSetId).append("'")
						.append(" AND dataSet.objectId = '" + objectId + "')");
				LogManager.debug("Sql4es : " + sBuilder.toString());

				rSet = statement.executeQuery(sBuilder.toString());
				while (rSet.next()) {
					corpusIds.add(rSet.getString(1));
				}
				returnMap.put(objectId, corpusIds);
			}
		} catch (Exception e) {
			LogManager.Exception("classifyDataSet : ", e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}

		LogManager.method("Process out service: DataSetServiceImpl/classifyDataSet");
		return returnMap;
	}

	@Override
	public Map<String, List<String>> classifyCorpus(String dataSetId, String[] corpusIdArr) {
		LogManager.method("Process in service: DataSetServiceImpl/classifyCorpus");

		Map<String, List<String>> returnMap = new HashMap<>();
		
		for (String corpus : corpusIdArr) {
			LogManager.debug("corpusId and objectId of arr : " + corpus);
			String[] arr = corpus.split(":");
			String corpusId = arr[0];
			LogManager.debug("corpusId : " + corpusId);
			String objectId = arr[1];
			LogManager.debug("objectId : " + objectId);
			if (returnMap.containsKey(objectId)) {
				returnMap.get(objectId).add(corpusId);
			} else {
				List<String> temp = new ArrayList<>();
				temp.add(corpusId);
				returnMap.put(objectId, temp);
			}
		}
		
		LogManager.method("Process out service: DataSetServiceImpl/classifyCorpus");
		return returnMap;
	}

	@Override
	public Map<String, Object> searchDataSet(String functionId, String keyWord, Integer pageIndex, Integer pageSize) {
		Map<String, Object> returnMap = new HashMap<>();
		List<Map<String, Object>> dataSetInfo = new ArrayList<>();
		keyWord = "%" + keyWord + "%";
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sortByCreateTime);
		List<DataSet> dataSets = dataSetRepository.findAllByNameLike(keyWord, functionId, pageable);

		for (DataSet dataSet : dataSets) {
			Map<String, Object> tempMap = new HashMap<>();
			if (!dataSet.isUpload()) {
				CorpusSet corpusSet = setRepository.getOne(dataSet.getCorpusSetId());
				tempMap.put("corpusSetId", corpusSet.getId());
				tempMap.put("coupusSetName", corpusSet.getName());
			}
			tempMap.put("status", dataSet.getStatus());
			tempMap.put("isUpload", dataSet.isUpload());
			tempMap.put("dataSetId", dataSet.getId());
			tempMap.put("dataSetName", dataSet.getName());
			tempMap.put("classifyId", dataSet.getClassifyId());
			tempMap.put("classifyName", classifyRepository.getOne(dataSet.getClassifyId()).getName());
			tempMap.put("createTime", dataSet.getCreateTime());
			tempMap.put("corpusNum", countCorpus(dataSet.getId(), null, null));
			dataSetInfo.add(tempMap);
		}
		returnMap.put("dataSet", dataSetInfo);
		returnMap.put("totalNum", dataSetRepository.countByNameLike(keyWord, functionId));
		return returnMap;
	}

	@Override
	public Map<String, List<String>> objectCorpus(String dataSetId, String objectId) {
		LogManager.method("Process in service: DataSetServiceImpl/objectCorpus");

		Map<String, List<String>> returnMap = new HashMap<>();
		List<String> corpusIds = new ArrayList<>();

		ResultSet rSet = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();

			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("SELECT _id FROM ").append(SystemConfig.ELASTICSEARCH_TYPE)
					.append(" WHERE (dataSet.dataSetId = '").append(dataSetId).append("'")
					.append(" AND dataSet.objectId = '" + objectId + "')");
			LogManager.debug("Sql4es : " + sBuilder.toString());

			rSet = statement.executeQuery(sBuilder.toString());
			while (rSet.next()) {
				corpusIds.add(rSet.getString(1));
			}
			returnMap.put(objectId, corpusIds);
		} catch (Exception e) {
			LogManager.Exception("objectCorpus Exception :", e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}
		LogManager.method("Process out service: DataSetServiceImpl/objectCorpus");
		return returnMap;
	}

	@Override
	public void removeCorpus(String[] corpusIds, String dataSetId, String corpusSetId) {
		for (String corpus : corpusIds) {
			String[] arr = corpus.split(":");
			String corpusId = arr[0];
			String objectId = arr[1];
			List<Map<String, String>> arr4ES = new ArrayList<>();
			Map<String, String> tempMap = new HashMap<>();
			tempMap.put("dataSetId", dataSetId);
			tempMap.put("objectId", objectId);
			arr4ES.add(tempMap);
			updateArrayToES(arr4ES, corpusId, false);
		}

	}

	@Override
	public void addCorpus(String[] corpusIds, String dataSetId, String corpusSetId) {
		for (String corpus : corpusIds) {
			String[] arr = corpus.split(":");
			String corpusId = arr[0];
			String objectId = arr[1];
			List<Map<String, String>> arr4ES = new ArrayList<>();
			Map<String, String> tempMap = new HashMap<>();
			tempMap.put("dataSetId", dataSetId);
			tempMap.put("objectId", objectId);
			arr4ES.add(tempMap);
			updateArrayToES(arr4ES, corpusId, true);
		}

	}

	@Override
	public void deleteCorpus(String[] corpusIdsArr, String dataSetId) {
		boolean isUpload = dataSetRepository.getOne(dataSetId).isUpload();
		if (isUpload) {
			// 上传的语料集则根据语料id删除语料
			for (String corpusIds : corpusIdsArr) {
				String corpusId = corpusIds.split(":")[0];
				deleteCorpusById(corpusId);
			}
		} else {
			// 从语料集来的则编辑数组 
			for (String corpusIds : corpusIdsArr) {
				String[] corpusIdArr = corpusIds.split(":");
				String corpusId = corpusIdArr[0];
				String objectId = corpusIdArr[1];
				List<Map<String, String>> arr4ES = new ArrayList<>();
				Map<String, String> tempMap = new HashMap<>();
				tempMap.put("dataSetId", dataSetId);
				tempMap.put("objectId", objectId);
				arr4ES.add(tempMap);
				updateArrayToES(arr4ES, corpusId, false);
			}
		}
		
	}

}
