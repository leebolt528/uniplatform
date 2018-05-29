package com.bonc.uni.nlp.service.product; 
/** 
* @author : GaoQiuyuer 
* @version: 2017年12月21日 上午10:15:07 
*/
public interface IAbstractAndKeywordService {

	/**
	 * @param title 文章标题
	 * @param content 文章内容
	 * @param numOfKeywords 关键词个数
	 * @param percent 摘要长度占文章长度百分比
	 * @param numOfAbstracts 摘要句数
	 * @return
	 */
	String getAbstract(String title, String content);

	/**
	 * @param title
	 * @param content
	 * @return
	 */
	String getAbstractEn(String title, String content);

}
 