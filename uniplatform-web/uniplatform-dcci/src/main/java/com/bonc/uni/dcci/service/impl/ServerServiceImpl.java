package com.bonc.uni.dcci.service.impl;

import com.bonc.uni.common.jpa.Specification.SpecificationUtil;
import com.bonc.uni.dcci.dao.ServerPwdRepository;
import com.bonc.uni.dcci.dao.ServerRepository;
import com.bonc.uni.dcci.entity.Server;
import com.bonc.uni.dcci.entity.ServerModel;
import com.bonc.uni.dcci.entity.ServerPwd;
import com.bonc.uni.dcci.service.CrawlerService;
import com.bonc.uni.dcci.service.ServerService;
import com.bonc.uni.dcci.util.MachineType;
import com.bonc.uni.dcci.util.SystemType;
import com.bonc.usdp.odk.common.security.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("serverService")
public class ServerServiceImpl implements ServerService{
	
	@Autowired
	ServerRepository serverRepository;
	
	@Autowired
	ServerPwdRepository serverPwdRepository;

	@Autowired
	CrawlerService crawlerService;

	@Override
	public List<Server> findByIp (String ip) {
		return serverRepository.findByIp(ip);
	}

	@Override
	public List<Server> findByIp (Server server) {
		return serverRepository.findByIp(server.getIp());
	}

	@Override
	public boolean addServer(Server server) {
		try {
            String passwd = server.getPwd();
            String md5Psswd = MD5Util.md5(passwd);
            server.setPwd(md5Psswd);

            serverRepository.save(server);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
				
	}
	
	@Override
	public boolean addServerPwd(ServerPwd serverPwd) {
		try {
            String passwd = serverPwd.getPwd();
            String md5Psswd = MD5Util.md5(passwd);
            serverPwd.setPwd(md5Psswd);

			serverPwdRepository.save(serverPwd);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
				
	}
	
	@Override
	public List<Server> listAll() {
		List<Server> servers = (List<Server>) serverRepository.findAll();
		return servers;
	}

	/**
	 * 获取部分字段
	 * @return
	 */
	@Override
	public List<ServerModel> findAllServerModel() {
		List<ServerModel> serverModels = (List<ServerModel>) serverRepository.findAllServerModel();
		return serverModels;
	}

	@Override
	public long countServer() {
		long count = serverRepository.count();
		return count;
	}
	
	@Override
	public boolean updServer(Server server) {
		try {
			Server upds = getServerById(server.getId());
			if(null != upds) {
				upds.setName(server.getName());
				upds.setIp(server.getIp());
				upds.setMachineType(server.getMachineType());
				upds.setSystemType(server.getSystemType());
				upds.setMachineCode(server.getMachineCode());
				upds.setLicense(server.getLicense());

                String passwd = server.getPwd();
                String md5Psswd = MD5Util.md5(passwd);

				upds.setPwd(md5Psswd);
				upds.setUser(server.getUser());
				serverRepository.save(upds);
				return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		return false;
	}
	
	@Override
	public boolean updServerPwd(ServerPwd serverPwd) {
		try {
			ServerPwd upds = getServerPwdById(serverPwd.getId());
			if(null != upds) {
				upds.setOperator(serverPwd.getOperator());
				upds.setPurpose(serverPwd.getPurpose());

                String passwd = serverPwd.getPwd();
                String md5Psswd = MD5Util.md5(passwd);

				upds.setPwd(md5Psswd);
				upds.setUser(serverPwd.getUser());
				serverPwdRepository.save(upds);
				return true;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public Server getServerById(Integer id) {
		
		return serverRepository.findOne(id);
		
	}
	
	@Override
	public ServerPwd getServerPwdById(Integer id) {
		
		return serverPwdRepository.findOne(id);
		
	}
	
	@Override
	public boolean delServerById(Integer id) {
		try {
			//删除机器中的所有用户server_pwd
			delServerPwdByServerId(id);

			//删除机器中的所有组件crawler
			crawlerService.delCrawlerByServerId(id);

			serverRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean delServerPwdById(Integer id) {
		try {
			serverPwdRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public List<ServerPwd> listByServer(int serverId) {
		Specification<ServerPwd> specification = SpecificationUtil.<ServerPwd>and()
				.ep("serverId", serverId).build();
		List<ServerPwd> servers = serverPwdRepository.findAll(specification);
		return servers;
	}
	
	@Override
	public boolean delServerPwdByServerId(int serverId) {
		try {
			List<ServerPwd> servers = listByServer(serverId);
			serverPwdRepository.delete(servers);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public Page<Server> pageList(String ip, SystemType systemType, MachineType machineType, int pageNum, int pageSize) {
        String trimIp = ip.trim();

	    Specification<Server> specification = SpecificationUtil.<Server>and()
                .like("ip", "%"+ trimIp +"%")
				.ep(systemType!=SystemType.ALL,"systemType", systemType)
				.ep(machineType!=MachineType.ALL,"machineType", machineType)
				.orderByDESC("id")
                .build();
		return serverRepository.findAll(specification, new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public Page<ServerPwd> pageServerPwds(int serverId,int pageNum, int pageSize) {
		Specification<ServerPwd> specification = SpecificationUtil.<ServerPwd>and()
				.ep("serverId", serverId).orderByDESC("id")
                .build();
		return serverPwdRepository.findAll(specification, new PageRequest(pageNum - 1, pageSize));
	}

}
