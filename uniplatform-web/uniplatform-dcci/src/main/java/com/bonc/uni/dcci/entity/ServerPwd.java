package com.bonc.uni.dcci.entity;

import javax.persistence.*;

import com.bonc.uni.common.entity.EntityCommon;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 服务器非root密码管理
 * @author futao
 * 2017年9月5日
 */
@Entity
@Table(name = "`dcci_server_pwd`")
public class ServerPwd extends EntityCommon{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 服务器id
	 */
	@Column(name = "`SERVER_ID`", updatable = false, columnDefinition = "INT(11) NOT NULL")
	private int serverId;

	/**
	 * 服务器用户名
	 */
	@Column(name = "`USER`")
	private String user;
	
	/**
	 * 服务器密码
	 */
	@Column(name = "`PWD`")
	private String pwd;
	
	/**
	 * 使用者
	 */
	@Column(name = "`OPERATOR`")
	private String operator;
	
	/**
	 * 用途
	 */
	@Column(name = "`PURPOSE`")
	private String purpose;

	/**
	 * 机器外键
	 */
	/*@ManyToOne(optional = false, fetch = FetchType.EAGER, targetEntity = Server.class)
	@JoinColumn(name = "SERVER_ID", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_DCCI_SERVER_PWD_SERVER_ID"))
	private Server server;*/

	/**
	 * 转成json时防止无限循环
	 * @param server
	 */
	/*@JsonBackReference
	public void setOrganization(Server server) {
		this.server = server;
	}*/

    /*public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }*/

    public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
}
