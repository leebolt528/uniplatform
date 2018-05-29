package com.bonc.uni.nlp.service.product; 
/** 
* @author : GaoQiuyuer 
* @version: 2017年12月25日 下午5:26:50 
*/
public interface ITextClusterService {

	/**
	 * @param textPath
	 * @return
	 */
	String cluster(String textPath);

	/**
	 * @param textPath
	 * @return
	 */
	String clusterEn(String textPath);

}
 