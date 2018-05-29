package com.bonc.uni.dcci.entity;


import javax.persistence.*;

import com.bonc.uni.common.entity.EntityCommon;
import com.bonc.uni.dcci.util.MachineType;
import com.bonc.uni.dcci.util.SystemType;

import java.util.Set;

/**
 * 服务器bean
 * @author futao
 * 2017年8月28日
 */
@Entity
@Table(name = "`dcci_server`")
public class Server extends EntityCommon{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 名称
	 */
	@Column(name = "`NAME`")
	private String name;

	/**
	 * 服务器地址
	 */
	@Column(name = "`IP`")
	private String ip;
	
	/**
	 * 机器类型 物理机  虚拟机
	 */
	@Column(name = "`MACHINE_TYPE`")
	@Enumerated(EnumType.STRING)
	private MachineType machineType;
	
	/**
	 * 服务器类型
	 */
	@Column(name = "`SYSTEM_TYPE`")
	@Enumerated(EnumType.STRING)
	private SystemType systemType;
	
	/**
	 * 机器码
	 */
	@Column(name = "`MACHINE_CODE`")
	private String machineCode;
	
	/**
	 * 采集器license
	 */
	@Column(name = "`LICENSE`")
	private String license;
	
	/**
	 * CPU
	 */
	@Column(name = "`CPU`", updatable = false, columnDefinition = "bigint(20) DEFAULT 0")
	private int cpu;
	
	/**
	 * 内存
	 */
	@Column(name = "`MEM`", updatable = false, columnDefinition = "bigint(20) DEFAULT 0")
	private int mem;
	
	/**
	 * 硬盘
	 */
	@Column(name = "`DISK`", updatable = false, columnDefinition = "bigint(20) DEFAULT 0")
	private int disk;
	
	/**
	 * root用户名
	 */
	@Column(name = "`USER`")
	private String user;
	
	/**
	 * root密码
	 */
	@Column(name = "`PWD`")
	private String pwd;

	/**
	 * 机器中的用户
	 */
	/*@OneToMany(targetEntity = ServerPwd.class, cascade={CascadeType.REMOVE},
			fetch=FetchType.LAZY, mappedBy = "server")
	private Set<ServerPwd> serverPwdSet;*/

	/**
	 * 机器中的组件
	 */
	/*@OneToMany(targetEntity = Crawler.class, cascade={CascadeType.REMOVE},
			fetch=FetchType.LAZY, mappedBy = "server")
	private Set<Crawler> crawlerSet;

	public Set<ServerPwd> getServerPwdSet() {
		return serverPwdSet;
	}

	public void setServerPwdSet(Set<ServerPwd> serverPwdSet) {
		this.serverPwdSet = serverPwdSet;
	}

	public Set<Crawler> getCrawlerSet() {
		return crawlerSet;
	}

	public void setCrawlerSet(Set<Crawler> crawlerSet) {
		this.crawlerSet = crawlerSet;
	}*/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public MachineType getMachineType() {
		return machineType;
	}

	public void setMachineType(MachineType machineType) {
		this.machineType = machineType;
	}

	public SystemType getSystemType() {
		return systemType;
	}

	public void setSystemType(SystemType systemType) {
		this.systemType = systemType;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public int getCpu() {
		return cpu;
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public int getMem() {
		return mem;
	}

	public void setMem(int mem) {
		this.mem = mem;
	}

	public int getDisk() {
		return disk;
	}

	public void setDisk(int disk) {
		this.disk = disk;
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
	
}
