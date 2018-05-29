package com.bonc.uni.nlp.service.product; 
/** 
* @author : GaoQiuyuer 
* @version: 2017年12月21日 下午2:34:06 
*/
public interface IEntityExtractionService {

	/**
	 * @param text
	 * @return 
	 */
	String extractionEntity(String text);
	
	String extractionEntityEn(String text);

}
 