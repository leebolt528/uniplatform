package com.bonc.uni.nlp.utils.redis.entity;

import com.bonc.uni.nlp.entity.model.ServerInformation;

/** 
* @author : GaoQiuyuer 
* @version: 2018年1月4日 下午7:40:39 
*/
public class RedisPublishModel {
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
 