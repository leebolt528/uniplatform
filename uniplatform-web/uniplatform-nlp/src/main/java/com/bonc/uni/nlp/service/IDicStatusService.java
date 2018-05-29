package com.bonc.uni.nlp.service;

public interface IDicStatusService {
	/**
	 * 判断词库标签是否在启用
	 * 
	 * @param dicId
	 * @return true 无应用 false 有应用
	 */
	boolean strategyIfStartDic(String dicId);
	/**
	 * 判断词库标签是否在启用
	 * 
	 * @param dicId
	 * @return true 无应用 false 有应用
	 */
	boolean functionIfStartDic(String dicId);
	/**
	 * 判断词库标签是否(候选状态)存在
	 * 
	 * @param dicId
	 * @return true 无应用 false 有应用
	 */
	boolean functionIfCandidateDic(String dicId);

	/**
	 * 判断除了已传入别名外，其他功能是否使用词典
	 * 
	 * @param str
	 *            别名 （DicStatusConstant）；
	 * @param dicId
	 * @return true 无应用 false 有应用
	 */
	boolean dicIfStartExceptbyName(String str, String dicId);

}
