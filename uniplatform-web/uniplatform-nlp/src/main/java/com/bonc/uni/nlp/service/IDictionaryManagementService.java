package com.bonc.uni.nlp.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.entity.dic.FieldType;
import com.bonc.uni.nlp.entity.dic.WordMap;
import com.bonc.uni.nlp.entity.dic.WordOrdinary;
import com.bonc.uni.nlp.entity.dic.WordSentiment;

/**
 * @ClassName:DictionaryManagerService
 * @Package:com.bonc.text.service
 * @Description:TODO
 * @author:Chris
 * @date:2017年4月12日 下午4:44:48
 */
public interface IDictionaryManagementService {

	List<Map<String, Object>> queryDicByType(String dicTypeId);

	List<Map<String, Object>> queryDicByTypeAndFunction(String dicTypeId, String functionId);

	List<Map<String, Object>> queryAllDic();

	/**
	 * 创建词库
	 * 
	 * @param dictName
	 *            词库名称
	 * @param dictTypeId
	 *            词库类型
	 * @param dicSubTypeId
	 *            词库子类型
	 * @param wordOrdinarys
	 *            通用类型词
	 * @param wordMaps
	 *            map类型词
	 * @param format
	 *            词库中词的类型（map,ordinarys）
	 * @return
	 */
	int addDictionary(String dictName, String dictTypeId, String dicSubTypeId, List<WordOrdinary> wordOrdinarys,
			List<WordMap> wordMaps,List<WordSentiment> wordSentiments, String format, String functionId);

	/**
	 * 删除词典
	 * 
	 * @param dictId
	 * @return
	 */
	boolean removeDictionary(String dictId);

	/**
	 * 创建词库时获取类型与子类型对用关系
	 * 
	 * @return
	 */
	List<Map<String, Object>> intoAddDictionary();

	/**
	 * 修改词库名称
	 * 
	 * @param dictId
	 * @param newDictName
	 * @return
	 */
	int updateDictionaryInfo(String dictId, String newDictName);

	/**
	 * 获取搜索词库总数量
	 * 
	 * @param name
	 * @return
	 */
	int getSearchDictionaryNumber(String name);

	/**
	 * 根据子类型展示词库
	 * @param subTypeId
	 * @return
	 */
	List<Map<String, Object>> queryDicBySubType(String subTypeId);

	/**
	 * 根据类型与子类型展示词库
	 * @param dicTypeId
	 * @param subTypeId
	 * @return
	 */
	List<Map<String, Object>> queryDicByTypeAndSubType(String dicTypeId, String subTypeId);

	/**
	 * 根据功能展示词库
	 * @param functionId
	 * @return
	 */
	List<Map<String, Object>> queryDicByFunction(String functionId);

	/**
	 * 根据功能与子类型展示词库
	 * @param subTypeId
	 * @param functionId
	 * @return
	 */
	List<Map<String, Object>> queryDicBySubTypeAndFunctionl(String subTypeId, String functionId);

	/**
	 * 根据类型功能子类型展示词库
	 * @param dicTypeId
	 * @param functionId
	 * @param subTypeId
	 * @return
	 */
	List<Map<String, Object>> queryDicByTypeAndFunctionAndSubType(String dicTypeId, String functionId,
			String subTypeId);

	List<FieldType> listFieldType(String typeId, String subTypeId);

	List<Map<String, Object>> queryAllDicByKeyWord(String dicTypeId, String keyWord);

	List<Integer> addFieldDictionary(String dicName, String dicTypeid, String dicSubTypeId, String fieldTypeId,
			MultipartFile[] files, String dicFormat, String functionId);

	boolean labelDic(String dicId, String labelIds);

	List<Map<String, String>> labelsInfo(String dicId);

	boolean updateDictionaryName(String dictId, String newDicName);

}
