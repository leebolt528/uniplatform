package com.bonc.uni.nlp.service.product; 
/** 
* @author : GaoQiuyuer 
* @version: 2017年12月25日 下午5:02:25 
*/
public interface IRelatedWordsService {

	/**
	 * @param word
	 * @return
	 */
	String getRelatedwords(String modelName, String word);

}
 