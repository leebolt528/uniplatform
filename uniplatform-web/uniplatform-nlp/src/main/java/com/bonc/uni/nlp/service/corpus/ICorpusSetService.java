package com.bonc.uni.nlp.service.corpus;

import java.util.List;
import java.util.Map;

import com.bonc.uni.nlp.entity.corpus.CorpusSet;



public interface ICorpusSetService {

	/**
	 * 添加语料集
	 * @param name
	 * @param functionId
	 * @param classifyId
	 */
	void addCorpusSet(String name, String functionId, String classifyId);
	/**
	 * 删除语料集
	 * @param corpusSetId
	 */
	void deleteCorpusSet(String corpusSetId);
	/**
	 * 修改语料集
	 * @param corpusSetId
	 * @param corpusSetName
	 */
	void updateCorpusSet(String functionId, String corpusSetId, String corpusSetName, String classifyId);
	/**
	 * 获取功能下的全部语料集
	 * @param functionId
	 * @return
	 */
	List<Map<String, Object>> getCorpusSetByFunction(String functionId, int pageIndex, int pageSize);
	/**
	 * 检索语料集
	 * @param functionId
	 * @param keyWord
	 * @return
	 */
	Map<String, Object> searchCorpusSet(String functionId, String keyWord, Integer pageIndex, Integer pageSize);
	/**
	 * 获取功能下语料集的总数
	 * @param functionId
	 * @return
	 */
	int getCorpusSetNumByFunction(String functionId);
	/**
	 * 将语料集下的全部语料进行分类
	 * @param corpusSetI
	 * @return
	 */
	Map<String, List<String>> classifyCorpusByCorpusSet(String corpusSetId);
	/**
	 * 将语料集下的语料根据对象进行分组
	 * @param corpusSetI
	 * @return
	 */
	Map<String, List<String>> classifyCorpusByObject(String corpusSetId, String objectId);
	/**
	 * 从语料集中获取随机数量的语料
	 * @param corpusSetId
	 * @param corpusNum
	 * @return
	 */
	void getRandomCorpusFromSet(String dataSetId, String corpusSetId, String classifyId, Integer corpusNum);
	/**
	 * 根据分类体系过滤语料集
	 * @param classifyId
	 * @return
	 */
	List<CorpusSet> getSetByClassifyId(String classifyId);
	/**
	 * 根据语料id获取语料详情
	 * @param corpusId
	 * @return
	 */
	Map<String, String> getCorpusByCorpusId(String corpusId);
}
