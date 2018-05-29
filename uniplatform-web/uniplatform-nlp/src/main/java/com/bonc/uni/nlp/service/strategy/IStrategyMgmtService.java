package com.bonc.uni.nlp.service.strategy;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.entity.dic.DicType;
import com.bonc.uni.nlp.entity.rule.Rule;
import com.bonc.uni.nlp.entity.strategy.Algorithm;
import com.bonc.uni.nlp.entity.strategy.Model;

/**
 * @author : GaoQiuyuer
 * @version: 2017年11月2日 下午4:43:05
 */
public interface IStrategyMgmtService {

	/**
	 * @param strategyName
	 *            策略名称
	 * @return true：存在；false：不存在
	 */
	boolean validateStrategyExists(String strategyName);

	List<Map<String, String>> listAllFunctions(boolean ascSort);

	/**
	 * @param functionId 功能id
	 * @return
	 */
	List<Algorithm> listAlgorithm(String functionId);

	/**
	 * @param pageSize 页数
	 * @param pageIndex 每一页条数
	 * @param searchWord 关键字
	 * @return
	 */
	List<Object> listAllStrategies(String functionId, int pageIndex, int pageSize, boolean ascSort);

	/**
	 * @param searchWord 关键字
	 * @return
	 */
	int getStrategiesNumber(String functionId);

	/**
	 * @param algorithmId 算法id
	 * @return
	 */
	List<Model> listModel(String algorithmId);

	List<DicType> listDicTypes();

	/**
	 * @param dicTypeId 词典类型id
	 * @param algorithmId 算法id
	 * @return
	 */
	List<Object> listDic(String functionId, boolean ascSort);
	
	/**
	 * @param strategyId 策略id
	 * @return
	 */
	boolean deleteStrategy(String strategyId);
	
	/**
	 * @param strategyId 策略id
	 * @return
	 */
	boolean setAsDefault(String strategyId);
	
	/**
	 * @param algorithmId 算法id
	 * @return
	 */
	List<Rule> listRule(String algorithmId);
	
	/**
	 * @param functionId 功能id
	 * @return
	 */
	List<Object> listDependencyStrategy(String functionId);
	
	/**
	 * @param strategyName 策略名称
	 * @param functionId 功能id
	 * @param algorithmId 算法id
	 * @param modelId 模型id
	 * @param ruleId 规则id
	 * @param dictsId 词典id
	 * @param batch 批处理
	 * @param dependencyStrategy 依赖策略
	 * @return
	 */
	int addStrategy(String strategyName, String functionId, String algorithmId, String modelId, String ruleId,
			String dictsId, Integer batch, String dependencyStrategyId);


	/**
	 * @param strategyId 策略id
	 * @param strategyNewName 策略新名称
	 * @param functionId 功能id
	 * @param algorithmId 算法id
	 * @param modelId 模型id
	 * @param batch 批处理
	 * @param dependencyStrategyId
	 * @return
	 */
	int editStrategy(String strategyId, String strategyNewName, String algorithmNewId, String modelId, String ruleId,
			String dictsId, Integer batch, String dependencyStrategyId);

	/**
	 * @param strategiesId
	 * @return 
	 */
	String exportStrategies2XMLString(String strategiesId);

	/**
	 * @param files
	 * @return
	 */
	boolean importStrategies(MultipartFile[] files);

	/**
	 * @param strategyId
	 * @return 
	 */
	Map<String, Object> getSingleStrategyInfo(String strategyId);

	/**
	 * @return
	 */
	List<Algorithm> listAlgorithmInfo();

	/**
	 * @return
	 */
	List<Object> listAllStrategiesInfo();


}
