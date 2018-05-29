package com.bonc.uni.dcci.entity;


import com.bonc.uni.dcci.util.MachineType;
import com.bonc.uni.dcci.util.SystemType;

import java.io.Serializable;

/**
 * 查询机器时不查询密码
 */
public class ServerWithoutPwd implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private int id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 服务器地址
	 */
	private String ip;
	
	/**
	 * 机器类型 物理机  虚拟机
	 */
	private MachineType machineType;
	
	/**
	 * 服务器类型
	 */
	private SystemType systemType;
	
	/**
	 * 机器码
	 */
	private String machineCode;
	
	/**
	 * 采集器license
	 */
	private String license;
	
	/**
	 * CPU
	 */
	private int cpu;
	
	/**
	 * 内存
	 */
	private int mem;
	
	/**
	 * 硬盘
	 */
	private int disk;
	
	/**
	 * root用户名
	 */
	private String user;

	public ServerWithoutPwd (Server server) {
		this.id = server.getId();
		this.name = server.getName();
		this.ip = server.getIp();
		this.machineType = server.getMachineType();
		this.systemType = server.getSystemType();
		this.machineCode = server.getMachineCode();
		this.license = server.getLicense();
		this.cpu = server.getCpu();
		this.mem = server.getMem();
		this.disk = server.getDisk();
		this.user = server.getUser();
	}

	public ServerWithoutPwd(int id, String name, String ip, MachineType machineType, SystemType systemType, String machineCode, String license, int cpu, int mem, int disk, String user) {
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.machineType = machineType;
		this.systemType = systemType;
		this.machineCode = machineCode;
		this.license = license;
		this.cpu = cpu;
		this.mem = mem;
		this.disk = disk;
		this.user = user;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

}
