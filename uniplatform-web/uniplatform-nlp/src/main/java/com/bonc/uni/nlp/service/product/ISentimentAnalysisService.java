package com.bonc.uni.nlp.service.product;

import java.util.List;

import com.bonc.uni.nlp.entity.product.EmotionResult;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午5:48:05 
*/
public interface ISentimentAnalysisService {

	/**
	 * @param text
	 * @return
	 */
	List<EmotionResult> getEmotionCeils(String text);

	/**
	 * @param text
	 * @return
	 */
	String analysisEmotion(String text);

}
 