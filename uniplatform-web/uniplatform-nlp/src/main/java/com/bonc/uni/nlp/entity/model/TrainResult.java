package com.bonc.uni.nlp.entity.model;

/** 
* @author : GaoQiuyuer 
* @version: 2018年1月6日 下午6:11:43 
*/
public class TrainResult {

	/**
	 * 模型是否存在
	 */
	private boolean exist = false;
	/**
	 * 模型名称
	 */
	private String modelName;
	/**
	 * 模型存放路径
	 */
	private String modelPath;
	/**
	 * 服务器信息
	 */
	private ServerInformation serverInfo;

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	public ServerInformation getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(ServerInformation serverInfo) {
		this.serverInfo = serverInfo;
	}



}
 