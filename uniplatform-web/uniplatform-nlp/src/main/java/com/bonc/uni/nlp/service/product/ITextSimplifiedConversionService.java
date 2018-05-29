package com.bonc.uni.nlp.service.product; 
/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午12:04:02 
*/
public interface ITextSimplifiedConversionService {

	/**
	 * @param text
	 * @return
	 */
	String convert2HongKongAndMacaoTraditional(String text);

	/**
	 * @param text
	 * @return
	 */
	String convert2TaiWaiTraditional(String text);

	/**
	 * @param text
	 * @return
	 */
	String simplifiedChinese(String text);
	
	 

}
 