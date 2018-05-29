package com.bonc.uni.nlp.service.product; 
/** 
* @author : GaoQiuyuer 
* @version: 2017年12月21日 上午11:47:39 
*/
public interface IAutoWordSegmentService {

	/**
	 * @param text 文章内容
	 * @return 
	 */
	String getSegment(String text);

	/**
	 * @param text
	 * @return
	 */
	String getSegmentEnglish(String text);

}
 