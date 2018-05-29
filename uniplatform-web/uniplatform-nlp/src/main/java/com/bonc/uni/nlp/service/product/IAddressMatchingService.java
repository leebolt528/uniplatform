package com.bonc.uni.nlp.service.product; 
/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午6:32:27 
*/
public interface IAddressMatchingService {

	/**
	 * @param strName
	 * @param strColumn
	 * @return
	 */
	String upsertExcelDataToEs(String path, int column);

	/**
	 * @param strName
	 * @param strColumn
	 * @return
	 */
	String upsertMySQLDataToEs(String strName, String strColumn);

	/**
	 * @param sourceAddresses
	 * @param isEdit
	 * @return
	 */
	String getMultiAddressMatchResultFromEs(String sourceAddresses);

}
 