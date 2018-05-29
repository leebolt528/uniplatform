package com.bonc.uni.nlp.entity.model;

/** 
* @author : GaoQiuyuer 
* @version: 2018年1月4日 下午8:10:48 
*/
public class ModelInfo {
	/**
	 * 是否删除模型
	 */
	private boolean exist = false;
	/**
	 * 模型名称
	 */
	private String modelName;
	/**
	 * 功能名称
	 */
	private String functionName;
	/**
	 * 算法名称
	 */
	private String algorithmName;
	/**
	 * 模型存放路径
	 */
	private String modelPath;
	/**
	 * 服务器ip
	 */
	private String ip;
	/**
	 * 端口号
	 */
	private String sshPort;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * @return the exist
	 */
	public boolean isExist() {
		return exist;
	}
	/**
	 * @param exist the exist to set
	 */
	public void setExist(boolean exist) {
		this.exist = exist;
	}
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}
	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	/**
	 * @return the modelPath
	 */
	public String getModelPath() {
		return modelPath;
	}
	/**
	 * @param modelPath the modelPath to set
	 */
	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * @return the sshPort
	 */
	public String getPort() {
		return sshPort;
	}
	/**
	 * @param sshPort the sshPort to set
	 */
	public void setPort(String sshPort) {
		this.sshPort = sshPort;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the functionName
	 */
	public String getFunctionName() {
		return functionName;
	}
	/**
	 * @param functionName the functionName to set
	 */
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	/**
	 * @return the algorithmName
	 */
	public String getAlgorithmName() {
		return algorithmName;
	}
	/**
	 * @param algorithmName the algorithmName to set
	 */
	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TrainResult [exist=" + exist + ", modelName=" + modelName + ", functionName=" + functionName
				+ ", algorithmName=" + algorithmName + ", modelPath=" + modelPath + ", ip=" + ip + ", sshPort=" + sshPort
				+ ", username=" + username + ", password=" + password + "]";
	}

}
 