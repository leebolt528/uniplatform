package com.bonc.uni.dcci.service;

import java.util.List;

import com.bonc.uni.dcci.entity.ServerModel;
import com.bonc.uni.dcci.util.MachineType;
import org.springframework.data.domain.Page;

import com.bonc.uni.dcci.entity.Server;
import com.bonc.uni.dcci.entity.ServerPwd;
import com.bonc.uni.dcci.util.SystemType;

/**
 * 服务器接口定义
 * @author futao
 * 2017年8月28日
 */
public interface ServerService {

	/**
	 * 根据ip查找server
	 * @param ip
	 * @return
	 */
	public List<Server> findByIp (String ip);

	public List<Server> findByIp (Server server);

	/**
	 * 添加服务器
	 * @param server
	 * @return
	 */
	boolean addServer(Server server);
	
	
	/**
	 * 列出所有
	 * @return
	 */
	List<Server> listAll();

	/**
	 * 统计服务器个数
	 * @return
	 */
	public long countServer();

	/**
	 * 修改
	 * @param server
	 * @return
	 */
	boolean updServer(Server server);
	
	/**
	 * 通过id获取实体
	 * @param id
	 * @return
	 */
	Server getServerById(Integer id);
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	boolean delServerById(Integer id);
	
	/**
	 * 翻页
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<Server> pageList(String ip, SystemType systemType, MachineType machineType, int pageNum, int pageSize);
	
	/**
	 * 添加密码
	 * @param serverPwd
	 * @return
	 */
	boolean addServerPwd(ServerPwd serverPwd);
	
	/**
	 * 修改
	 * @param serverPwd
	 * @return
	 */
	boolean updServerPwd(ServerPwd serverPwd);
	
	/**
	 * 获取
	 * @param id
	 * @return
	 */
	ServerPwd getServerPwdById(Integer id);
	
	/**删除
	 * @param id
	 * @return
	 */
	boolean delServerPwdById(Integer id);
	
	/**列表
	 * @param serverId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<ServerPwd> pageServerPwds(int serverId,int pageNum, int pageSize);

	/**
	 * 获取部分字段
	 * @return
	 */
	public List<ServerModel> findAllServerModel();
	
	/**
	 * 通过serverId获取
	 * @param serverId
	 * @return
	 */
	List<ServerPwd> listByServer(int serverId);
	
	/**
	 * 通过serverId删除
	 * @param serverId
	 * @return
	 */
	boolean delServerPwdByServerId(int serverId);
}
