package com.bonc.uni.nlp.service.product; 
/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午7:30:14 
*/
public interface ITextRelationExtractService {

	/**
	 * <p>Title: getPersonToPersonRelation</p>
	 * <p>Description: 获取人和人的关系</p>
	 * @param text
	 * @return
	 */
	String getPersonToPersonRelation(String text);
	/**
	 * <p>Title: getOrgToPersonSingleSentencePredict</p>
	 * <p>Description: 获取人和机构的关系</p>
	 * @param text
	 * @return
	 */
	String getOrgToPersonSingleSentencePredict(String text);
	/**
	 * <p>Title: getOrgToOrgSingleSentencePredict</p>
	 * <p>Description: 获取机构和机构的关系</p>
	 * @param text
	 * @return
	 */
	String getOrgToOrgSingleSentencePredict(String text);

}
 