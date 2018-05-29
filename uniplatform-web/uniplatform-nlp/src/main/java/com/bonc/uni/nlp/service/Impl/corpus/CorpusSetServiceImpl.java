package com.bonc.uni.nlp.service.Impl.corpus;


import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.dao.classify.ClassifyObjectRepository;
import com.bonc.uni.nlp.dao.classify.ClassifyRepository;
import com.bonc.uni.nlp.dao.corpus.CorpusSetRepository;
import com.bonc.uni.nlp.dao.model.DataSetRepository;
import com.bonc.uni.nlp.entity.classify.ClassifyObject;
import com.bonc.uni.nlp.entity.corpus.CorpusSet;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.corpus.ICorpusSetService;
import com.bonc.uni.nlp.utils.TimeUtil;
import com.bonc.uni.nlp.utils.es.ESManager;
import com.bonc.usdp.odk.elasticsearch.esUtil.search.ISearchService;
import com.bonc.usdp.odk.logmanager.LogManager;

@Service
public class CorpusSetServiceImpl implements ICorpusSetService {

	
	@Autowired
	ClassifyCorpusServiceImpl classifyCorpusServiceImpl;
	
	@Autowired
	CorpusSetRepository corpusSetRepository;
	
	@Autowired
	DataSetRepository dataSetRepository;
	
	@Autowired
	ClassifyObjectRepository objectRepository;
	
	@Autowired
	ClassifyRepository classifyRepository;
	
	public Sort sortByCreateTime = new Sort(Sort.Direction.DESC, "createTime");

	
	/**
	 * 获取选定数量的语料id
	 * @param corpusSetId
	 * 			语料集id
	 * @param objectId
	 * 			对象id
	 * @param corpusNum
	 * 			输入的语料数量
	 * @return
	 */
	private List<String> selectIdBySetAndObject(String corpusSetId, String objectId, Integer corpusNum) {
		List<String> corpusIds = new ArrayList<>();
		
		ResultSet rs = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
	        statement = conn.createStatement();

	        StringBuilder sBuilder = new StringBuilder();
	        sBuilder.append("SELECT _id FROM ").append(SystemConfig.ELASTICSEARCH_TYPE)
				.append(" WHERE (corpusSet.corpusSetId = '").append(corpusSetId).append("'")
				.append(" AND corpusSet.objectId = '").append(objectId).append("')")
				.append(" LIMIT 0,").append(corpusNum);
	        LogManager.debug("Sql of selectIdBySetAndObject : " + sBuilder.toString());
	        
	        rs = statement.executeQuery(sBuilder.toString());
			while (rs.next()) {
				corpusIds.add(rs.getString(1));
			}
		} catch (Exception e) {
			LogManager.Exception("selectIdBySetAndObject Exception : ", e);
		} finally {
			ESManager.close(conn, statement, rs);
		}
		return corpusIds;
	}
		
	@Override
	public void addCorpusSet(String name, String functionId, String classifyId) {
		if (existSameNameInFunction(functionId, name)) {
			throw new AdminException("该功能下已经存在同名语料集！");
		}
		CorpusSet corpusSet = new CorpusSet();
		corpusSet.setName(name);
		corpusSet.setFunctionId(functionId);
		corpusSet.setClassifyId(classifyId);
		corpusSet.setCreateTime(TimeUtil.getNowDate());
		corpusSet.setCreateUser("uni");
		corpusSetRepository.save(corpusSet);
		classifyRepository.updateIsUsed(true, classifyId);
	}
	/**
	 * 判断某一功能下是否存在同名语料集
	 * @param functionId
	 * @param name
	 * @return
	 */
	private boolean existSameNameInFunction(String functionId, String name) {
		if (null != corpusSetRepository.findOneByNameAndFunctionId(name, functionId)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Map<String, Object>> getCorpusSetByFunction(String functionId, int pageIndex, int pageSize) {
		LogManager.method("Process in service: CorpusSetServiceImpl/getCorpusSetByFunction");

		List<Map<String, Object>> returnList = new ArrayList<>();
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sortByCreateTime);
		List<CorpusSet> corpusSets = corpusSetRepository.findAllByFunctionId(functionId, pageable);
		for (CorpusSet corpusSet : corpusSets) {
			Map<String, Object> tempMap = new HashMap<>();
			String corpusSetId = corpusSet.getId();
			String classifyId = corpusSet.getClassifyId();

			List<String> objectIds = new ArrayList<>();
			List<ClassifyObject> classifyObjects = objectRepository.findAllByClassifyId(classifyId, sortByCreateTime);
			for (ClassifyObject classifyObject : classifyObjects) {
				objectIds.add(classifyObject.getId());
			}
			tempMap.put("id", corpusSetId);
			tempMap.put("name", corpusSet.getName());
			tempMap.put("functionId", corpusSet.getFunctionId());
			tempMap.put("createTime", corpusSet.getCreateTime());
			tempMap.put("classifyId", classifyId);
			tempMap.put("isUsed", corpusSet.isUsed());
			tempMap.put("classifyName", classifyRepository.getOne(classifyId).getName());
			tempMap.put("corpusNum", classifyCorpusServiceImpl.countCorpus(corpusSetId, null, null));
			returnList.add(tempMap);

		}

		LogManager.method("Process out service: CorpusSetServiceImpl/getCorpusSetByFunction");
		return returnList;
	}
	
	@Override
	public int getCorpusSetNumByFunction(String functionId) {
		return corpusSetRepository.countByFunctionId(functionId);
	}
	
	// TODO: 是否删除ES中的语料
	@Override
	public void deleteCorpusSet(String corpusSetId) {
		if (corpusSetRepository.getOne(corpusSetId).isUsed()) {
			new AdminException("有数据集引用该语料集，删除失败！");
		}
		String classifyId = corpusSetRepository.getOne(corpusSetId).getClassifyId();
		classifyCorpusServiceImpl.deleteCorpusBySetId(corpusSetId);
		corpusSetRepository.delete(corpusSetId);
		if (0 == corpusSetRepository.countByClassifyId(classifyId)) {
			//同名分类体系下语料集数量为0
			classifyRepository.updateIsUsed(false, classifyId);
		}
	}
	@Override
	public void updateCorpusSet(String functionId, String corpusSetId, String corpusSetName, String classifyId) {
		CorpusSet corpusSet = corpusSetRepository.getOne(corpusSetId);
		String oldClassifyId = corpusSet.getClassifyId();
		if (existSameNameInFunction(functionId, corpusSetName)) {
			// 与别的语料集同名
			if (!corpusSetId.equals(corpusSetRepository.findOneByNameAndFunctionId(corpusSetName, functionId).getId())) {
				throw new AdminException("该功能下已经存在同名语料集！");
			}
		}
		corpusSet.setName(corpusSetName);
		corpusSetRepository.save(corpusSet);
		if (!oldClassifyId.equals(classifyId)) {
			// 更改了分类体系
			corpusSet.setClassifyId(classifyId);
			corpusSetRepository.save(corpusSet);
			classifyRepository.updateIsUsed(true, classifyId);
			if (0 == corpusSetRepository.countByClassifyId(oldClassifyId)
					&& 0 ==dataSetRepository.countByClassifyId(oldClassifyId)) {
				//同名分类体系下语料集数量为0
				classifyRepository.updateIsUsed(false, oldClassifyId);
			}
		}
	}

	@Override
	public Map<String, List<String>> classifyCorpusByCorpusSet(String corpusSetId) {
		Map<String, List<String>> returnMap = new HashMap<>();
		String classifyId = corpusSetRepository.getOne(corpusSetId).getClassifyId();
		List<ClassifyObject> objects = objectRepository.findAllByClassifyId(classifyId, sortByCreateTime);

		ResultSet rSet = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
			statement = conn.createStatement();

			for (ClassifyObject classifyObject : objects) {
				String objectId = classifyObject.getId();
				if (0 == classifyCorpusServiceImpl.countCorpus(corpusSetId, objectId, null)) {
					continue;
				}
				List<String> corpusIds = new ArrayList<>();

				StringBuilder sBuilder = new StringBuilder();
				sBuilder.append("SELECT _id FROM ").append(SystemConfig.ELASTICSEARCH_TYPE)
						.append(" WHERE (corpusSet.corpusSetId = '").append(corpusSetId).append("'")
						.append(" AND corpusSet.objectId = '" + objectId + "')");
				rSet = statement.executeQuery(sBuilder.toString());
				while (rSet.next()) {
					corpusIds.add(rSet.getString(1));
				}
				returnMap.put(objectId, corpusIds);
			}

		} catch (Exception e) {
			LogManager.Exception("classifyCorpusByCorpusSet : ", e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}

		return returnMap;
	}
	
	private void addArrayToES(List<Map<String, String>> list, String corpusId) {
		try {
			ISearchService searchService = ESManager.getInstance().getSearchService();
			searchService.updateArray(SystemConfig.ELASTICSEARCH_INDEX, SystemConfig.ELASTICSEARCH_TYPE, corpusId,
					"dataSet", true, list);
		} catch (UnknownHostException e) {
			LogManager.Exception("addArrayToES Exception :", e);
		}

	}
	@Override
	public void getRandomCorpusFromSet(String dataSetId, String corpusSetId, String classifyId,
			Integer inputNum) {
		LogManager.method("Process in service: CorpusSetServiceImpl/getRandomCorpusFromSet");

		long addDataSetStartTime = System.currentTimeMillis();

		try {
			List<ClassifyObject> objects = objectRepository.findAllByClassifyId(classifyId, sortByCreateTime);

			for (ClassifyObject classifyObject : objects) {
				String objectId = classifyObject.getId();

				int numOfSet = classifyCorpusServiceImpl.countCorpus(corpusSetId, objectId, null);  // 语料集对象下的总数量
				if (0 == numOfSet) {
					continue;
				}
				int operateNum = 0; // 最终操作的语料数量

				if (numOfSet < inputNum) {
					operateNum = numOfSet;
				} else {
					operateNum = inputNum;
				}

				List<Map<String, String>> list = new ArrayList<>();
				Map<String, String> dataSetMap = new HashMap<>(); // 插入的数组对象
				dataSetMap.put("dataSetId", dataSetId);
				dataSetMap.put("objectId", objectId);
				list.add(dataSetMap);
				List<String> corpusIds = selectIdBySetAndObject(corpusSetId, objectId, operateNum); // 进行插入对象数组的语料id
				for (String corpusId : corpusIds) {
					addArrayToES(list, corpusId);
				}
			}
		} catch (Exception e) {
			LogManager.Exception("getRandomCorpusFromSet Exception :", e);
		}
		long addDataSetEndTime = System.currentTimeMillis();
		LogManager.debug("The time of add dataSet : " + (addDataSetEndTime - addDataSetStartTime) + "ms");
		corpusSetRepository.updateIsUsed(true, corpusSetId);
		LogManager.method("Process out service: CorpusSetServiceImpl/getRandomCorpusFromSet");
	}

	@Override
	public List<CorpusSet> getSetByClassifyId(String classifyId) {
		return corpusSetRepository.findAllByClassifyId(classifyId, sortByCreateTime);
	}
	@Override
	public Map<String, List<String>> classifyCorpusByObject(String corpusSetId, String objectId) {
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
				.append(" WHERE (corpusSet.corpusSetId = '").append(corpusSetId).append("'")
				.append(" AND corpusSet.objectId = '" + objectId + "')");
	        LogManager.debug("classifyCorpusByObject sql4es : " + sBuilder.toString());
	        rSet = statement.executeQuery(sBuilder.toString());
	        while (rSet.next()) {
			corpusIds.add(rSet.getString(1));
	        }
	        returnMap.put(objectId, corpusIds);
		} catch (Exception e) {
			LogManager.Exception("classifyCorpusByObject Exception :" , e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}
		return returnMap;
	}
	@Override
	public Map<String, Object> searchCorpusSet(String functionId, String keyWord, Integer pageIndex, Integer pageSize) {
		Map<String, Object> returnMap = new HashMap<>();
		List<Map<String, Object>> corpusInfo = new ArrayList<>();
		keyWord = "%" + keyWord + "%";
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sortByCreateTime);
		List<CorpusSet> corpusSets = corpusSetRepository.findAllByNameLike(keyWord, functionId, pageable);
		for (CorpusSet corpusSet : corpusSets) {
			Map<String, Object> tempMap = new HashMap<>();
			String corpusSetId = corpusSet.getId();
			String classifyId = corpusSet.getClassifyId();

			List<String> objectIds = new ArrayList<>();
			List<ClassifyObject> classifyObjects = objectRepository.findAllByClassifyId(classifyId, sortByCreateTime);
			for (ClassifyObject classifyObject : classifyObjects) {
				objectIds.add(classifyObject.getId());
			}
			tempMap.put("id", corpusSetId);
			tempMap.put("name", corpusSet.getName());
			tempMap.put("functionId", corpusSet.getFunctionId());
			tempMap.put("createTime", corpusSet.getCreateTime());
			tempMap.put("classifyId", classifyId);
			tempMap.put("isUsed", corpusSet.isUsed());
			tempMap.put("classifyName", classifyRepository.getOne(classifyId).getName());
			tempMap.put("corpusNum", classifyCorpusServiceImpl.countCorpus(corpusSetId, null, null));
			corpusInfo.add(tempMap);

		}
		returnMap.put("corpusSet", corpusInfo);
		returnMap.put("totalNum", corpusSetRepository.countByNameLike(keyWord, functionId));
		return returnMap;
	}
	/**
	 * 根据语料id获取详情
	 */
	@Override
	public Map<String, String> getCorpusByCorpusId(String corpusId) {
		Map<String, String> returnMap = new HashMap<>();
		
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT title, text FROM ")
				.append(SystemConfig.ELASTICSEARCH_TYPE).append(" WHERE _id = '")
				.append(corpusId).append("'");
		LogManager.info("sql4es : " + sBuilder);
		ResultSet rSet = null;
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ESManager.getConnection(SystemConfig.ELASTICSEARCH_INDEX);
	        statement = conn.createStatement();			
	        rSet = statement.executeQuery(sBuilder.toString());
			while (rSet.next()) {
				returnMap.put("text", rSet.getString("text") == null ? "" : rSet.getString("text").replace("''", "'"));
				returnMap.put("title", rSet.getString("title") == null ? "" : rSet.getString("title"));
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			ESManager.close(conn, statement, rSet);
		}

		return returnMap;
	}

}
