package com.bonc.uni.nlp.entity.model; 
/** 
* @author : GaoQiuyuer 
* @version: 2018年1月4日 下午8:08:09 
*/
public class ServerInformation {

	/**
	 * 服务器ip
	 */
	private String ip;
	/**
	 * ssh端口
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getSshPort() {
		return sshPort;
	}

	public void setSshPort(String sshPort) {
		this.sshPort = sshPort;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "ServerInformation [ip=" + ip + ", sshPort=" + sshPort + ", username=" + username + ", password="
				+ password + "]";
	}



}
 