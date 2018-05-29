package com.bonc.uni.nlp.service.corpus;

import java.util.List;
import java.util.Map;


import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.entity.classify.Classify;


public interface IClassifyCorpusService {

	/**
	 * 获取所有语料类型
	 * @return
	 */
	public List<Map<String, Object>> getCorpusTypes(String tag);
	/**
	 * 获取语料类型下的所有功能
	 * @param corpusTypeId
	 * @return
	 */
	public List<Map<String, Object>> getFunctionsByCorpusType(String corpusTypeId);
	/**
	 * 获取功能对应的详细列表
	 * @param functionId
	 * @return
	 */
	/**
	 * 获取分类体系下的所有对象
	 * @param classifyId
	 * @return
	 */
	public List<Map<String, Object>> getClassifyObject(String classifyId, String corpusSetId);
	/**
	 * 上传语料到分类体系对象下
	 * @param objectId
	 */
	public void uploadCorpusToClassifyObject(String objectId, List<MultipartFile> files, String corpusSetId);
	/**
	 * 上传压缩文件至语料集(新)
	 * @param file
	 * @param classifyId
	 */
	public void newUploadCompressedCorpus(MultipartFile file, String corpusSetId);
	/**
	 * 获取语料集对象下的所有语料
	 * @param objectId
	 * @return
	 */
	public List<Map<String, Object>> getCorpusBySetAndObject( String corpusSetId, String objectId, boolean needText, Integer pageIndex, Integer pageSize);
	/**
	 * 获取语料集下的所有语料
	 * @param corpusSetId
	 * @return
	 */
	public List<Map<String, Object>> getCorpusByCorpusSet(String corpusSetId, boolean needText, Integer pageIndex, Integer pageSize);
	/**
	 * 获取语料集对象下的语料总数
	 * @param corpusSetId
	 * @param objectId
	 * @return
	 */
	public int countCorpusBySetAndObject(String corpusSetId, String objectId);
	/**
	 * 计算语料集下的语料总数
	 * @param corpusSetId
	 * @return
	 */
	public int countCorpusByCorpusSet(String corpusSetId);
	/**
	 * 删除语料
	 * @param higherLevelAndCorpusIds
	 */
	public void deleteCorpus(String higherLevelAndCorpusIds);
	/**
	 * 分类体系下进行语料检索
	 * @param classifyId
	 * @return
	 */
	Map<String, Object> searchCorpusUnderCorpusSet(String keyWord, String corpusSetId, boolean needText, Integer pageIndex, Integer pageSize);
	/**
	 * 分类对象下进行语料检索
	 * @param classifyObjectId
	 * @return
	 */
	Map<String, Object> searchCorpusUnderCorpusSetAndObject(String keyWord, String corpusSetId, String objectId, boolean needTex, Integer pageIndex, Integer pageSize);
	/**
	 * 
	 * @return
	 */
	List<Classify> getAllClassify();
	/**
	 * 将语料按对象进行分类
	 * @param higherLevelAndCorpusIds
	 * @return
	 */
	Map<String, List<String>> classifyCorpus(String higherLevelAndCorpusIds);
	/**
	 * 完成分类的语料生成文件夹
	 * @param classifiedCorpus
	 * @return
	 */
	String generateCorpusFolder(Map<String, List<String>> classifiedCorpus, String classifyId);
	/**
	 * 初始化数据集穿梭框对象下内容
	 * @param dataSetId
	 * @param corpusSetId
	 * @param objectId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	Map<String, Object> getShuttleBoxObject(String dataSetId, String corpusSetId, String objectId, boolean needText, Integer pageIndex, Integer pageSize);
	/**
	 * 根据语料id获取语料内容
	 * @param corpusId
	 * @return
	 */
	String getCorpusText(String corpusId);
	/**
	 * 编辑es中语料集数组
	 * @param nested
	 * @param corpusId
	 * @param falg
	 */
	void updateSetArray(List<Map<String, String>> nested, String corpusId, boolean falg);
}

