package com.bonc.uni.nlp.service;

import java.util.List;

import com.bonc.uni.nlp.entity.dic.DicFuncRelation;
import com.bonc.uni.nlp.entity.dic.Function;

/**
 * @author : GaoQiuyuer
 * @version: 2017年10月30日 下午6:40:19
 */
public interface IDicEditService {

	/**
	 * @param dicId
	 *            词典id
	 * @param tagIdsList
	 *            标签id
	 * @param subtractList
	 * @return
	 */
	int tagged(String dicId, String tagIds);

	/**
	 * @param dicId
	 *            词典id
	 * @return
	 */
	List<DicFuncRelation> getTaggedCurrent(String dicId);

	/**
	 * 修改词库名称
	 * 
	 * @param dictId
	 * @param newDictName
	 * @return
	 */
	int updateDictionaryInfo(String dictId, String newDictName);

	/**
	 * 获取词库已打标签Id
	 * 
	 * @param dicId
	 * @return
	 */
	List<String> getFunctionId(String dicId);

	/**
	 * 获取词库已打标签
	 * 
	 * @param dicId
	 * @return
	 */
	List<Function> getFunction(List<String> dicId);

	/**
	 * 获取词库所有标签
	 * 
	 * @param dicId
	 * @return
	 */
	List<Function> getFunctionAll(String dicId, List<String> functionIds);

	/**
	 * 删除所有标签
	 * 
	 * @param dicId
	 * @return
	 */
	int rmFunctiondAll(String dicId);

}
