package com.bonc.uni.nlp.service.product; 
/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午1:39:27 
*/
public interface ITextSimilarityComparisonService {

	/**
	 * @param text1
	 * @param text2
	 * @return
	 */
	String textSimilarityCompare(String text1, String text2);

	/**
	 * @param text1
	 * @param text2
	 * @return
	 */
	String shortTextSimilarityComparison(String text1, String text2);

}
 