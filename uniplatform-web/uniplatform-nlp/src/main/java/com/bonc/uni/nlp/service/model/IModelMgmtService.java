package com.bonc.uni.nlp.service.model;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.entity.model.DataSet;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月21日 下午7:50:50 
*/
public interface IModelMgmtService {

	List<Object> listHasModelFunctions();
	
	/**
	 * @param algorithmId 算法id
	 * @param algorithmId 
	 * @param pageIndex 页数
	 * @param pageSize 每页模型的条数
	 * @param ascSort 排序
	 * @return
	 */
	List<Object> listModelsByAlgorithm(String keyword, String functionId, String algorithmId, int pageIndex, int pageSize, boolean ascSort);

	/**
	 * @param functionId 功能id
	 * @param algorithmId 算法id
	 * @param modelName 模型名称
	 * @param dataSetId 数据集id
	 * @return 
	 */
	boolean addModel(String functionId, String algorithmId, String modelName, String dataSetId);

	/**
	 * @param functionId 功能id
	 * @param algorithmId 算法id
	 * @param newModelName 新模型名称
	 * @param newDataSetId 新数据集id
	 * @return 
	 */
	boolean editModel(String modelId, String algorithmId, String newModelName, String newDataSetId);

	List<DataSet> listDataSet(String functionId);

	boolean deleteModels(String modelsId);

	boolean trainModel(String modelId);

	/**
	 * @param modelId
	 */
	void applyModel(String modelId);
	
	/**
	 * @param modelId 模型id
	 * @param fileName 文件名
	 * @param response 
	 */
	void downloadModel(String modelIds, String fileName, HttpServletResponse response);
	
	/**
	 * @param modelName 模型名称
	 * @param algorithmId 算法id
	 * @param file 上传的文件（压缩包/单个文件）
	 */
	void uploadModel(String modelName, String algorithmId, MultipartFile[] files);

}
 