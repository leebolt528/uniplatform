package com.bonc.uni.nlp.service.strategy;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月10日 上午11:21:52 
*/
public interface IBusinessMgmtService {

	Map<String, Object> listBusinessNode();
	
	/**
	 * @param nodeName 节点名称
	 * @return true 存在 false 不存在
	 */
	boolean validateNodeExists(String nodeName);
	
	/**
	 * @param nodeName 节点名称
	 * @param strategyIds 功能策略id
	 * @return 
	 */
	boolean addBusinessNode(String nodeName, String strategyIds);

	/**
	 * @param nodesId
	 * @return 
	 */
	int deleteBusinessNode(String nodesId);
	
	/**
	 * @param keyword 关键字
	 * @param pageIndex 页数
	 * @param pageSize 每页的条数
	 * @return
	 */
	Map<String, Object> listBusiness(String keyword, int pageIndex, int pageSize, boolean ascSort);

	/**
	 * @param businessName 业务策略名称
	 * @return true 存在 false 不存在
	 */
	boolean validateBusinessExists(String businessName);
	
	/**
	 * @param businessName
	 * @param nodeIds
	 * @return 
	 */
	boolean addBusiness(String businessName, String nodeIds);

	/**
	 * @param businessId
	 * @param newBusinessName
	 * @param newNodeIds
	 * @return 
	 */
	boolean editBusiness(String businessId, String newBusinessName, String newNodeIds);

	/**
	 * @param businessesId
	 * @return
	 */
	boolean deleteBusinesses(String businessesId);

	/**
	 * @param businessesId
	 * @return
	 */
	String exportBusinesses(String businessesId);

	/**
	 * @return 
	 * 
	 */
	List<Map<String, String>> listStrategies();

	/**
	 * @param businessId
	 * @return 
	 */
	List<Map<String, String>> businessInfo(String businessId);

	/**
	 * @param files
	 * @return
	 */
	boolean importBusinesses(MultipartFile[] files);

}
 