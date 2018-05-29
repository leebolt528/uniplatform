package com.bonc.uni.nlp.service.Impl.corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.constant.ResourcePathConstant;
import com.bonc.uni.nlp.dao.FuncitonRepository;
import com.bonc.uni.nlp.dao.classify.ClassifyObjectRepository;
import com.bonc.uni.nlp.dao.classify.ClassifyRepository;
import com.bonc.uni.nlp.dao.corpus.CorpusSetRepository;
import com.bonc.uni.nlp.dao.corpus.CorpusTypeRepository;
import com.bonc.uni.nlp.dao.model.DataSetRepository;
import com.bonc.uni.nlp.entity.classify.Classify;
import com.bonc.uni.nlp.entity.classify.ClassifyObject;
import com.bonc.uni.nlp.entity.corpus.CorpusSet;
import com.bonc.uni.nlp.entity.corpus.CorpusType;
import com.bonc.uni.nlp.entity.dic.Function;
import com.bonc.uni.nlp.entity.model.DataSet;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.corpus.IClassifyCorpusService;
import com.bonc.uni.nlp.utils.Encoding;
import com.bonc.uni.nlp.utils.NewZipUtil;
import com.bonc.uni.nlp.utils.ResourceFileUtil;
import com.bonc.uni.nlp.utils.TimeUtil;
import com.bonc.uni.nlp.utils.es.ESManager;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.elasticsearch.esUtil.search.ISearchService;
import com.bonc.usdp.odk.logmanager.LogManager;

@Service
public class ClassifyCorpusServiceImpl implements IClassifyCorpusService {

	@Autowired
	CorpusTypeRepository corpusTypeRepository;

	@Autowired
	FuncitonRepository functionRepository;

	@Autowired
	ClassifyRepository classifyRepository;

	@Autowired
	ClassifyObjectRepository objectRepository;

	@Autowired
	CorpusSetRepository corpusSetRepository;
	
	@Autowired
	DataSetRepository dataSetRepository;
	
	@Autowired 
	DataSetServiceImpl dataSetServiceImpl;

	public Sort sortByIndex = new Sort(Sort.Direction.ASC, "index");

	public Sort sortByCreateTime = new Sort(Sort.Direction.DESC, "createTime");
	
	/**
	 * Sql4es 插入
	 * 
	 * @param title
	 * @param text
	 * @param corpusSetId
	 * @param objectId
	 * @return
	 */
	public String insertSql(String title, String text, String corpusSetId, String objectId) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder sBuilder = new StringBuilder();
		try {
			sBuilder.append("INSERT INTO ").append(SystemConfig.ELASTICSEARCH_TYPE)
			.append(" (title, text, create_user, create_time, update_time, \"corpusSet._nested\") VALUES ")
			.append("('").append(title).append("','")
			.append(text).append("','").append("1','")
			.append(formatter.format(TimeUtil.getNowDate())).append("','")
			.append(formatter.format(TimeUtil.getNowDate())).append("','")
			.append("[{\"corpusSetId\":\"").append(corpusSetId).append("\",").append("\"objectId\":\"")
			.append(objectId).append("\"}]')");
		} catch (Exception e) {
			LogManager.Exception(e);
		}
		return sBuilder.toString();
	}

	/**
	 * 在 ES 中计算语料数量
	 * 
	 * @param corpusSetId
	 *            语料集id
	 * @param objectId
	 *            对象id
	 * @param keyWord
	 *            关键字
	 * @return
	 */
	public int countCorpus(String corpusSetId, String objectId, String keyWord) {
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
				sBuilder.append(" WHERE (corpusSet.corpusSetId = '").append(corpusSetId).append("'")
						.append(" AND corpusSet.objectId = '" + objectId + "')");
			} else {
				sBuilder.append(" WHERE (corpusSet.corpusSetId = '").append(corpusSetId).append("')");
			}
			if (null != keyWord) {
				sBuilder.append(" AND text = '" + keyWord + "'");
			}
			LogManager.debug("Sql of countCorpus : " + sBuilder.toString());

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
			sBuilder.append("DELETE FROM ").append(SystemConfig.ELASTICSEARCH_TYPE).append(" WHERE _id = '")
					.append(corpusId).append("'");
			LogManager.debug("Sql of deleteCorpusById : " + sBuilder.toString());

			statement.executeUpdate(sBuilder.toString());
		} catch (Exception e) {
			LogManager.Exception("deleteCorpusById Exception : ", e);
		} finally {
			ESManager.close(conn, statement, null);
		}

	}

	/**
	 * 在 ES 根据语料集id删除语料
	 * 
	 * @param corpusId
	 *            语料id
	 */
	public void deleteCorpusBySetId(String corpusSetId) {
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();

			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("DELETE FROM ").append(SystemConfig.ELASTICSEARCH_TYPE)
					.append(" WHERE corpusSet.corpusSetId = '").append(corpusSetId).append("'");
			LogManager.debug("Sql of deleteCorpusBySetId : " + sBuilder.toString());

			statement.executeUpdate(sBuilder.toString());
		} catch (Exception e) {
			LogManager.Exception("deleteCorpusBySetId Exception : ", e);
		} finally {
			ESManager.close(conn, statement, null);
		}

	}

	/**
	 * 在 ES 中获取语料信息
	 * 
	 * @param keyWord
	 *            关键字
	 * @param corpusSetId
	 *            语料集id
	 * @param objectId
	 *            对象id
	 * @param needText
	 *            是否需要文本
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> newGetCorpusInfo(String keyWord, String corpusSetId, String objectId,
			boolean needText, Integer pageIndex, Integer pageSize) {
		LogManager.Process("Process in service: the method newGetCorpusInfo of ClassifyCorpusServiceImpl");

		List<Map<String, Object>> returnList = new ArrayList<>();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT _id, title,");
		if (needText) {
			sBuilder.append(" text,");
		}
		sBuilder.append(" update_time, create_user, corpusSet FROM ").append(SystemConfig.ELASTICSEARCH_TYPE);
		if (null != objectId) {
			sBuilder.append(" WHERE (corpusSet.corpusSetId = '").append(corpusSetId).append("'")
					.append(" AND corpusSet.objectId = '" + objectId + "')");
		} else {
			sBuilder.append(" WHERE corpusSet.corpusSetId = '").append(corpusSetId).append("'");
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
				String objectOfCorpus = null;
				ResultSet corpusSet = (ResultSet) rSet.getObject("corpusSet");
				while (corpusSet.next()) {
					if (corpusSetId.equals(corpusSet.getString("corpusSetId"))) {
						objectOfCorpus = corpusSet.getString("objectId");
						break;
					}
				}
				corpusSet.close();
				String objectName = objectRepository.getOne(objectOfCorpus).getName();
				tempMap.put("_id", corpusId);
				if (needText) {
					tempMap.put("text",
							rSet.getString("text") == null ? "" : rSet.getString("text").replace("''", "'"));
				}
				tempMap.put("title", rSet.getString("title") == null ? "" : rSet.getString("title"));
				tempMap.put("update_time", rSet.getString("update_time") == null ? "" : rSet.getString("update_time"));
				tempMap.put("user", rSet.getString("create_user") == null ? "" : rSet.getString("create_user"));
				tempMap.put("higherLevelId", objectOfCorpus);
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
	 * 获取满足条件的语料集中的语料id
	 * 
	 * 
	 * @return
	 */
	public List<String> getCorpusSetId(String corpusSetId, String objectId) {
		LogManager.Process("Process in service: the method getCorpusSetId of ClassifyCorpusServiceImpl");

		List<String> returnList = new ArrayList<>();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT _id, update_time FROM ").append(SystemConfig.ELASTICSEARCH_TYPE);
		if (null != objectId && !StringUtil.isEmpty(objectId)) {
			sBuilder.append(" WHERE (corpusSet.corpusSetId = '").append(corpusSetId).append("'")
					.append(" AND corpusSet.objectId = '" + objectId + "')");
		} else {
			sBuilder.append(" WHERE corpusSet.corpusSetId = '").append(corpusSetId).append("'");
		}
		sBuilder.append(" ORDER BY update_time ");
		LogManager.debug(" sql4es of getCorpusSetId:" + sBuilder.toString());

		ResultSet rSet = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();
			rSet = statement.executeQuery(sBuilder.toString());
			while (rSet.next()) {
				returnList.add(rSet.getString(1));
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}

		LogManager.Process("Process out service: the method getCorpusSetId of ClassifyCorpusServiceImpl");
		return returnList;
	}
	/**
	 * 获取满足条件的数据集中的语料id
	 * 
	 * 
	 * @return
	 */
	public List<String> getDataSetId(String dataSetId, String objectId) {
		LogManager.Process("Process in service: the method getDataSetId of ClassifyCorpusServiceImpl");

		List<String> returnList = new ArrayList<>();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT _id, update_time FROM ").append(SystemConfig.ELASTICSEARCH_TYPE);
		if (null != objectId && !StringUtil.isEmpty(objectId)) {
			sBuilder.append(" WHERE (dataSet.dataSetId = '").append(dataSetId).append("'")
					.append(" AND dataSet.objectId = '" + objectId + "')");
		} else {
			sBuilder.append(" WHERE dataSet.dataSetId = '").append(dataSetId).append("'");
		}
		sBuilder.append(" ORDER BY update_time ");
		LogManager.debug(" sql4es of getDataSetId:" + sBuilder.toString());

		ResultSet rSet = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();
			rSet = statement.executeQuery(sBuilder.toString());
			while (rSet.next()) {
				returnList.add(rSet.getString(1));
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}

		LogManager.Process("Process out service: the method getDataSetId of ClassifyCorpusServiceImpl");
		return returnList;
	}

	/**
	 * 数据集穿梭框初始化
	 * 
	 * 
	 * @return
	 */
	public Map<String, Object> getBoxInfo(String corpusId, String corpusSetId) {
		LogManager.Process("Process in service: the method getBoxInfo of ClassifyCorpusServiceImpl");

		Map<String, Object> returnMap = new HashMap<>();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT title, update_time, corpusSet FROM ").append(SystemConfig.ELASTICSEARCH_TYPE);
		sBuilder.append(" WHERE _id = '").append(corpusId).append("'");
		LogManager.debug(" sql4es of getBoxInfoSql:" + sBuilder.toString());

		ResultSet rSet = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();
			rSet = statement.executeQuery(sBuilder.toString());
			while (rSet.next()) {
				String objectOfCorpus = null;
				ResultSet corpusSet = (ResultSet) rSet.getObject("corpusSet");
				while (corpusSet.next()) {
					if (corpusSetId.equals(corpusSet.getString("corpusSetId"))) {
						objectOfCorpus = corpusSet.getString("objectId");
						break;
					}
				}
				corpusSet.close();
				String objectName = objectRepository.getOne(objectOfCorpus).getName();
				returnMap.put("_id", corpusId);
				returnMap.put("title", rSet.getString("title") == null ? "" : rSet.getString("title"));
				returnMap.put("higherLevelId", objectOfCorpus);
				returnMap.put("higherLevel", objectName);
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}

		LogManager.Process("Process out service: the method getBoxInfo of ClassifyCorpusServiceImpl");
		return returnMap;
	}

	/**
	 * 计算语料类型下的语料总数
	 * 
	 * @param corpusType
	 */
	private int countCorpusNumByType(CorpusType corpusType, String tag) {
		int corpusNum = 0;
		List<String> functionIds = new ArrayList<>();
		String corpusTypeId = corpusType.getId();
		if ("all".equals(corpusTypeId)) {
			functionIds = functionRepository.findAllId();
		} else {
			functionIds = functionRepository.findIdByCorpusTypeId(corpusType.getId());
		}
		for (String functionId : functionIds) {
			if ("corpusSet".equals(tag)) {
				List<CorpusSet> corpusSets = corpusSetRepository.findAllByFunctionId(functionId);
				for (CorpusSet corpusSet : corpusSets) {
					corpusNum += countCorpus(corpusSet.getId(), null, null);
				}
			} else {
				List<DataSet> dataSets = dataSetRepository.findAllByFunctionId(functionId);
				for (DataSet dataSet : dataSets) {
					corpusNum += dataSetServiceImpl.countCorpus(dataSet.getId(), null, null);
				}
			}

		}
		return corpusNum;
	}

	@Override
	public List<Map<String, Object>> getCorpusTypes(String tag) {
		LogManager.Process("Process in service: the method getCorpusTypes of ClassifyCorpusServiceImpl");

		List<Map<String, Object>> rsList = new ArrayList<>();
		List<CorpusType> corpusTypes = corpusTypeRepository.findAll(sortByIndex);
		for (int i = 0; i < corpusTypes.size(); i++) {
			Map<String, Object> tempMap = new HashMap<>();
			CorpusType corpusType = corpusTypes.get(i);
			tempMap.put("name", corpusType.getDisplayName());
			tempMap.put("id", corpusType.getId());
			if (0 == i) {
				tempMap.put("status", true);
			} else {
				tempMap.put("status", false);
			}
			tempMap.put("corpusNum", countCorpusNumByType(corpusType, tag));
			rsList.add(tempMap);
		}
		
		LogManager.Process("Process out service: the method getCorpusTypes of ClassifyCorpusServiceImpl");
		return rsList;
	}

	@Override
	public List<Map<String, Object>> getFunctionsByCorpusType(String corpusTypeId) {
		List<Map<String, Object>> rsList = new ArrayList<>();
		List<Function> functions = functionRepository.findAllByCorpusTypeId(corpusTypeId, sortByIndex);
		for (int i = 0; i < functions.size(); i++) {
			Map<String, Object> tempMap = new HashMap<>();
			Function function = functions.get(i);
			tempMap.put("id", function.getId());
			tempMap.put("displayName", function.getDisplayName());
			tempMap.put("name", function.getName());
			if (0 == i) {
				tempMap.put("status", true);
			} else {
				tempMap.put("status", false);
			}
			rsList.add(tempMap);
		}
		return rsList;
	}

	@Override
	public List<Map<String, Object>> getClassifyObject(String classifyId, String corpusSetId) {
		List<Map<String, Object>> returnList = new ArrayList<>();
		List<ClassifyObject> objects = objectRepository.findAllByClassifyId(classifyId, sortByCreateTime);
		for (ClassifyObject classifyObject : objects) {
			Map<String, Object> tempMap = new HashMap<>();
			String objectId = classifyObject.getId();
			tempMap.put("id", objectId);
			tempMap.put("name", classifyObject.getName());
			tempMap.put("courpusNum", countCorpus(corpusSetId, objectId, null));
			returnList.add(tempMap);
		}
		return returnList;
	}

	/**
	 * 将 MultipartFile 文件内容读出
	 * 
	 * @param file
	 * @return
	 */
	private String readCorpusToString(MultipartFile file) {
		StringBuilder sBuilder = new StringBuilder();
		try {
			InputStream in = file.getInputStream();
			InputStreamReader inReader = new InputStreamReader(in);
			BufferedReader reader = new BufferedReader(inReader);
			String line = null;
			while (null != (line = reader.readLine())) {
				sBuilder.append(line);
			}
			reader.close();
			inReader.close();
			in.close();
		} catch (Exception e) {
			LogManager.Exception(e);
		}
		return sBuilder.toString().replace("'", "''");
	}

	@Override
	public void uploadCorpusToClassifyObject(String objectId, List<MultipartFile> files, String corpusSetId) {
		LogManager.Process("Process in service: the method uploadCorpusToClassifyObject of ClassifyCorpusServiceImpl");

		Connection conn = null;
		Statement statement = null;
		try {
			// ES连接准备
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();

			for (MultipartFile file : files) {
				if (StringUtil.isEmpty(readCorpusToString(file))) {
					continue;
				}
				String insertSql = insertSql(file.getOriginalFilename(), readCorpusToString(file), corpusSetId,
						objectId);
				statement.addBatch(insertSql);
			}
			statement.executeBatch();
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			ESManager.close(conn, statement, null);
		}

		LogManager.Process("Process out service: the method uploadCorpusToClassifyObject of ClassifyCorpusServiceImpl");

	}

	@Override
	public void deleteCorpus(String higherLevelAndCorpusIds) {
		LogManager.Process("Process in service: the method deleteCorpus of ClassifyCorpusServiceImpl");

		String[] higherLevelAndCorpusArr = higherLevelAndCorpusIds.split(";");
		for (String higherLevelAndCorpusId : higherLevelAndCorpusArr) {
			String[] arr = higherLevelAndCorpusId.split(":");
			if (arr.length < 2) {
				continue;
			}
			String corpusId = arr[1];
			deleteCorpusById(corpusId);
		}
		LogManager.Process("Process out service: the method deleteCorpus of ClassifyCorpusServiceImpl");

	}

	/**
	 * 将语料根据 分类体系对象/数据集 进行分类
	 * 
	 * @param higherLevelAndCorpusIds
	 * @return
	 */
	@Override
	public Map<String, List<String>> classifyCorpus(String higherLevelAndCorpusIds) {
		Map<String, List<String>> returnMap = new HashMap<>();
		String[] higherLevelAndCorpusIdArr = higherLevelAndCorpusIds.split(";");
		for (String higherLevelAndCorpusId : higherLevelAndCorpusIdArr) {
			String[] arr = higherLevelAndCorpusId.split(":");
			if (arr.length != 2) {
				continue;
			}
			String higherLevelId = arr[0];
			String corpusId = arr[1];
			if (!returnMap.keySet().contains(higherLevelId)) {
				List<String> corpusIds = new ArrayList<>();
				corpusIds.add(corpusId);
				returnMap.put(higherLevelId, corpusIds);
			} else {
				List<String> corpusIds = returnMap.get(higherLevelId);
				corpusIds.add(corpusId);
			}
		}
		return returnMap;
	}

	/**
	 * 将分类完成的语料生成临时文件夹
	 * 
	 * @param higherLevelAndCorpus
	 * @return
	 */
	@Override
	public String generateCorpusFolder(Map<String, List<String>> higherLevelAndCorpus, String classifyId) {
		// 清除资源文件夹
		ResourceFileUtil.clearZipFolder();
		ResourceFileUtil.clearCorpusFolder();
		LogManager.debug("generateCorpusFolder of ClassifyCorpusServiceImpl : clear the resource folder");

		String savaPath = null;
		String classifyName = classifyRepository.getOne(classifyId).getName();
		// 生成文件的保存路径： /corpus/分类体系名称
		savaPath = ResourcePathConstant.CORPUS_PATH + File.separator + classifyName;
		File corpusFolder = new File(savaPath);
		if (!corpusFolder.exists()) {
			corpusFolder.mkdir();
		}

		// ES 连接准备
		ResultSet rSet = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();

			for (Map.Entry<String, List<String>> entry : higherLevelAndCorpus.entrySet()) {
				String higherLevelId = entry.getKey();
				String higherLevelName = objectRepository.getOne(higherLevelId).getName();
				LogManager.debug(
						"generateCorpusFolder of ClassifyCorpusServiceImpl : the name of object : " + higherLevelName);

				String higherLevelFolderPath = savaPath + File.separator + higherLevelName;
				File higherLevelFolder = new File(higherLevelFolderPath);
				if (!higherLevelFolder.exists()) {
					higherLevelFolder.mkdir();
				}

				List<String> corpusIds = entry.getValue();
				LogManager.debug("generateCorpusFolder of ClassifyCorpusServiceImpl : corpusNum of object : "
						+ corpusIds.size());

				int index = 0;
				for (String corpusId : corpusIds) {
					StringBuilder sBuilder = new StringBuilder();
					sBuilder.append("SELECT title, text FROM ").append(SystemConfig.ELASTICSEARCH_TYPE)
							.append(" WHERE _id = '").append(corpusId).append("'");
					rSet = statement.executeQuery(sBuilder.toString());
					while (rSet.next()) {
						String text = rSet.getString("text");
						String title = rSet.getString("title");
						String corpusPath = higherLevelFolderPath + File.separator + title;
						File tempFile = new File(corpusPath);
						if (tempFile.exists()) {
							// 存在同名文件
							String fileName = title.substring(0, title.lastIndexOf("."));
							String postfix = title.substring(title.lastIndexOf("."));
							corpusPath = higherLevelFolderPath + File.separator + fileName + "(" + ++index + ")"
									+ postfix;
						}
						corpusTxt(corpusPath, text);
					}
				}
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}
		return ResourcePathConstant.CORPUS_PATH;
	}

	/**
	 * 语料生成.txt文件
	 * 
	 * @param coepusPath
	 * @param text
	 */
	private void corpusTxt(String coepusPath, String text) {
		BufferedWriter bufferedWriter;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(coepusPath));
			bufferedWriter.write(text);
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Classify> getAllClassify() {
		return classifyRepository.findAllClassifyName(sortByCreateTime);
	}

	@Override
	public String getCorpusText(String corpusId) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT text FROM ").append(SystemConfig.ELASTICSEARCH_TYPE).append(" WHERE _id ='")
				.append(corpusId).append("'");
		LogManager.info("sql4es : " + sBuilder);
		ResultSet rSet = null;
		Connection conn = null;
		String text = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();
			rSet = statement.executeQuery(sBuilder.toString());
			while (rSet.next()) {
				text = rSet.getString("text");
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}
		return text;
	}

	@Override
	public Map<String, Object> searchCorpusUnderCorpusSet(String keyWord, String corpusSetId, boolean needText,
			Integer pageIndex, Integer pageSize) {
		LogManager.Process("Process in service: the method searchCorpusUnderCorpusSet of ClassifyCorpusServiceImpl");

		Map<String, Object> returnMap = new HashMap<>();
		List<Map<String, Object>> corpus = new ArrayList<>();
		corpus = newGetCorpusInfo(keyWord, corpusSetId, null, needText, pageIndex, pageSize);
		returnMap.put("corpus", corpus);
		returnMap.put("totalNum", countCorpus(corpusSetId, null, keyWord));

		LogManager.Process("Process out service: the method searchCorpusUnderCorpusSet of ClassifyCorpusServiceImpl");
		return returnMap;
	}

	@Override
	public Map<String, Object> searchCorpusUnderCorpusSetAndObject(String keyWord, String corpusSetId,
			String higherLevelId, boolean needText, Integer pageIndex, Integer pageSize) {
		LogManager.Process(
				"Process in service: the method searchCorpusUnderCorpusSetAndObject of ClassifyCorpusServiceImpl");

		Map<String, Object> returnMap = new HashMap<>();
		List<Map<String, Object>> corpus = new ArrayList<>();
		corpus = newGetCorpusInfo(keyWord, corpusSetId, higherLevelId, needText, pageIndex, pageSize);
		returnMap.put("corpus", corpus);
		returnMap.put("totalNum", countCorpus(corpusSetId, higherLevelId, keyWord));

		LogManager.Process(
				"Process out service: the method searchCorpusUnderCorpusSetAndObject of ClassifyCorpusServiceImpl");
		return returnMap;
	}

	@Override
	public List<Map<String, Object>> getCorpusByCorpusSet(String corpusSetId, boolean needText, Integer pageIndex,
			Integer pageSize) {
		LogManager.Process("Process in service: the method getCorpusByCorpusSet of ClassifyCorpusServiceImpl");

		List<Map<String, Object>> returnList = new ArrayList<>();
		returnList = newGetCorpusInfo(null, corpusSetId, null, needText, pageIndex, pageSize);

		LogManager.Process("Process out service: the method getCorpusByCorpusSet of ClassifyCorpusServiceImpl");
		return returnList;
	}

	@Override
	public int countCorpusByCorpusSet(String corpusSetId) {
		return countCorpus(corpusSetId, null, null);
	}

	@Override
	public List<Map<String, Object>> getCorpusBySetAndObject(String corpusSetId, String objectId, boolean needText,
			Integer pageIndex, Integer pageSize) {
		LogManager.Process("Process in service: the method getCorpusBySetAndObject of ClassifyCorpusServiceImpl");

		List<Map<String, Object>> returnList = new ArrayList<>();
		returnList = newGetCorpusInfo(null, corpusSetId, objectId, needText, pageIndex, pageSize);

		LogManager.Process("Process out service: the method getCorpusBySetAndObject of ClassifyCorpusServiceImpl");
		return returnList;
	}

	@Override
	public int countCorpusBySetAndObject(String corpusSetId, String objectId) {
		return countCorpus(corpusSetId, objectId, null);
	}

	private String readCorpusToString(File file) {
		StringBuilder sBuilder = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while (null != (line = reader.readLine())) {
				sBuilder.append(line);
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				LogManager.Exception(e);
			}
		}
		return sBuilder.toString().replace("'", "''");
	}

	@Override
	public void newUploadCompressedCorpus(MultipartFile multipartFile, String corpusSetId) {
		LogManager.process("Process in service: the method uploadCompressedCorpus of ClassifyCorpusServiceImpl");

		// 清除资源文件夹
		ResourceFileUtil.clearZipFolder();
		ResourceFileUtil.clearCorpusFolder();
		LogManager.debug(" Clear resourceFolder");

		// MultipartFile转File
		String multipartFileName = multipartFile.getOriginalFilename();
		String zipFilePath = ResourcePathConstant.ZIP_PATH + File.separator + multipartFileName;
		File zipFile = new File(zipFilePath);
		try {
			multipartFile.transferTo(zipFile);
		} catch (IOException e) {
			LogManager.Exception(e);
		}

		long startTime = System.currentTimeMillis();
		// 解压至 ../unZip
		NewZipUtil.unZiFiles(zipFilePath, ResourcePathConstant.CORPUS_PATH);
		long unZipEndTime = System.currentTimeMillis();
		LogManager.debug(" Time of unzip " + (unZipEndTime - startTime) + "ms");

		// ES连接准备
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();
		} catch (Exception e1) {
			LogManager.Exception(e1);
		}

		long insertIntoESStartTime = System.currentTimeMillis();
		File file = new File(ResourcePathConstant.CORPUS_PATH);
		String classifyIdOfFile = null;
		Map<String, List<String>> objectMap = new HashMap<>();
		int totalNum = 0;
		File[] files = file.listFiles();
		if (files.length != 1) {
			throw new AdminException("上传失败，压缩文件夹格式错误！");
		}
		for (File classifyFile : files) {
			String classifyFileName = classifyFile.getName();
			LogManager.debug(" Name of classifyFile " + classifyFileName);

			if (null == classifyRepository.findOneByName(classifyFileName)) {
				throw new AdminException("上传失败，分类体系不存在！请检查目录格式！");
			}
			classifyIdOfFile = classifyRepository.findOneByName(classifyFileName).getId();
			String classifyIdOfSet = corpusSetRepository.getOne(corpusSetId).getClassifyId();
			if (!classifyIdOfFile.equals(classifyIdOfSet)) {
				throw new AdminException("上传失败，上传的分类体系与语料集不同！请检查目录格式！");
			}
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
			try {
				// 遍历对象文件夹，生成对象id - 语料文件 的map
				for (File objectFile : objectFiles) {
					String objectFileName = objectFile.getName();
					LogManager.debug(" Name of objectFileName " + objectFileName);

					if (null == objectRepository.findOneByNameAndClassifyId(objectFileName, classifyIdOfSet)) {
						continue;
					}
					String objectId = objectRepository.findOneByNameAndClassifyId(objectFileName, classifyIdOfSet)
							.getId();
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
							totalNum++;
							String newSql = insertSql(corpusFile.getName(), readCorpusToString(corpusFile), corpusSetId,
									objectId);
							statement.addBatch(newSql);

							corpusFile.delete();
						}

						LogManager.debug("corpusNum of object in fact:" + corpusFiles.length);
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
				LogManager.Exception("newUploadCompressedCorpus : 语料集上传压缩文件异常", e);
			} finally {
				ESManager.close(conn, statement, null);
			}

			classifyFile.delete();
			LogManager.debug(" CorpusNum in all : " + totalNum);
		}
		long insertIntoESEndTime = System.currentTimeMillis();
		LogManager.debug(
				"Time of read folder and insert into ES  " + (insertIntoESEndTime - insertIntoESStartTime) + "ms");

		LogManager.process("Process out service: the method uploadCompressedCorpus of ClassifyCorpusServiceImpl");
	}

	@Override
	public Map<String, Object> getShuttleBoxObject(String dataSetId, String corpusSetId, String objectId,
			boolean needText, Integer pageIndex, Integer pageSize) {
		Map<String, Object> returnMap = new HashMap<>();
		
		List<Map<String, Object>> corpus = new ArrayList<>();
		long searchStartTime = System.currentTimeMillis(); // es 查询开始时间
		// 语料集id - 数据集id --> 查信息
		List<String> id4CorpusSet = getCorpusSetId(corpusSetId, objectId);
		System.out.println("id4CorpusSet : " + id4CorpusSet);
		List<String> id4DataSet = getDataSetId(dataSetId, objectId);
		System.out.println("id4DataSet : " + id4DataSet);

		id4CorpusSet.removeAll(id4DataSet);
		for (String corpusId : id4CorpusSet) {
			corpus.add(getBoxInfo(corpusId, corpusSetId));
		}
		long searchEndTime = System.currentTimeMillis(); // es 查询结束时间
		LogManager.info("穿梭框初始化语料ES查询时间 ：" + (searchEndTime - searchStartTime) + "ms");

		returnMap.put("corpus", corpus);
		returnMap.put("totalNum", id4CorpusSet.size());
		return returnMap;
	}

	@Override
	public void updateSetArray(List<Map<String, String>> nested, String corpusId, boolean falg) {
		try {
			ISearchService searchService = ESManager.getInstance().getSearchService();
			searchService.updateArray(SystemConfig.ELASTICSEARCH_INDEX, SystemConfig.ELASTICSEARCH_TYPE, corpusId,
					"corpusSet", falg, nested);
		} catch (UnknownHostException e) {
			LogManager.Exception("addArrayToES Exception :", e);
		}
	}
}
