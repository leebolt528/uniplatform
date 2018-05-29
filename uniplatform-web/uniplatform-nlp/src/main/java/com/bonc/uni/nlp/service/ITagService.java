package com.bonc.uni.nlp.service;

import java.util.List;
import java.util.Map;

import com.bonc.uni.nlp.entity.dic.DicType;

/** 
* @author : GaoQiuyuer 
* @version: 2017年10月25日 下午4:29:27 
*/
public interface ITagService {

	/**
	 * @return 
	 * 
	 */
	List<DicType> getTagsList();

	/**
	 * @param typeId 词库类型id
	 */
	int getDicNumbers(String typeId);

	/**
	 * @param typeId
	 * @return
	 */
	List<String> getFunctionIds(String typeId);

	/**
	 * @param string 功能id
	 * @return
	 */
	String getFunction(String string);
	/**
	 * 获取词典已启用功能
	 * @param dicId
	 * @return
	 */
	List<Map<String, String>> getEnabledFunction(String dicId);
	/**
	 * 获取词典未启用功能
	 * @param dicId
	 * @return
	 */
	List<Map<String, String>> getDisabledFunction(String dicId);
	/**
	 * 对选择的功能进行词典启用
	 * @param dicId
	 * @param selectedFunctions
	 */
	void enableDic(String dicId, String[] selectedFunctions);
	/**
	 * 获取子类型
	 * @param dicId
	 * @param selectedFunctions
	 */
	List<Map<String, Object>> getSubTypes(String id);
	
	/**
	 * 获取全部子类型
	 * @param dicId
	 * @param selectedFunctions
	 */
	Map<String, Object> getSubTypes();
	/**
	 * 停用所有标签
	 * @param dicId
	 * @param selectedFunctions
	 */
	void disableAllFunction(String dicId);
	
}
 