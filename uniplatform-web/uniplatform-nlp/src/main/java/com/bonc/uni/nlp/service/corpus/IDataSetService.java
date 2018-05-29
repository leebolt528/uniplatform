package com.bonc.uni.nlp.service.corpus;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.entity.model.DataSet;


public interface IDataSetService {

	/**
	 * 创建一个数据集
	 * @param name
	 * @param corpusTypeId
	 * @param classifyId
	 * @param functionId
	 * @return
	 */
	DataSet createDataSet(String name, String corpusTypeId, String corpusSetId, String functionId, String classifyId, boolean isUpload);
	
	/**
	 * 删除数据集
	 * @param dataSetId
	 */
	void deleteDataSet(String dataSetId);
	/**
	 * 批量删除语料
	 * @param corpusIds
	 * @param dataSetId
	 */
	void deleteCorpus(String[] corpusIds, String dataSetId);
	/**
	 * 编辑数据集
	 * @param dataSetId
	 * @param newName
	 */
	void updateDataSet(String dataSetId, String newName);
	/**
	 * 穿梭框删除语料
	 * @param corpusIds
	 */
	void removeCorpus(String[] corpusIds, String dataSetId, String corpusSetId);
	/**
	 * 穿梭框增加语料
	 * @param corpusIds
	 */
	void addCorpus(String[] corpusIds, String dataSetId, String corpusSetId);

	/**
	 * 根据功能过滤数据集
	 * @param functionId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<Map<String, Object>> getDataSetByFunctionId(String functionId, Integer pageIndex, Integer pageSize);
	/**
	 * 检索数据集
	 * @param functionId
	 * @param keyWord
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	Map<String, Object> searchDataSet(String functionId, String keyWord, Integer pageIndex, Integer pageSize);

	/**
	 * 获取数据集的详细信息
	 * @param dataSetId
	 * @return
	 */
	List<Map<String, Object>> getDataSetDetail(String dataSetId, Integer pageIndex, Integer pageSize);
	/**
	 * 获取数据集下的全部语料
	 */
	Map<String, Object>  getCorpus(String dataSetId, String higherLevelId, boolean needText, Integer pageIndex, Integer pageSize);
	/**
	 * 根据关键字检索
	 */
	Map<String, Object>  getCorpus(String dataSetId, String higherLevelId, String keyWord, boolean needText, Integer pageIndex, Integer pageSize);

	/**
	 * 上传数据集
	 * @param file
	 * @param name
	 * @param corpusTypeId
	 * @param functionId
	 */
	void uploadDataSet(MultipartFile file, String name, String corpusTypeId, String functionId);
	/**
	 * 获取功能下数据集总数
	 * @param functionId
	 * @return
	 */
	int countDataByFunctionId(String functionId);
	
	boolean existSameDataSet(String dataSetName, String functionId);
	/**
	 * 将数据集下的语料按对象进行分类
	 */
	Map<String, List<String>> classifyDataSet(String dataSetId);
	/**
	 * 数据集对象下的全部语料
	 * @param dataSetId
	 * @param objectId
	 * @return
	 */
	Map<String, List<String>> objectCorpus(String dataSetId, String objectId);
	/**
	 * 将批量语料按对象进行分类
	 * @param dataSetId
	 * @param corpusIds
	 * @return
	 */
	Map<String, List<String>> classifyCorpus(String dataSetId, String[] corpusIds);
}