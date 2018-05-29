package com.bonc.uni.dcci.controller;


import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.dcci.entity.Server;
import com.bonc.uni.dcci.entity.ServerModel;
import com.bonc.uni.dcci.entity.ServerPwd;
import com.bonc.uni.dcci.service.ServerService;
import com.bonc.uni.dcci.util.MachineType;
import com.bonc.uni.dcci.util.SystemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务器管理 控制器
 * @author futao
 * 2017年9月5日
 */
@RestController
@RequestMapping("/dcci/server")
public class ServerController {

	@Autowired
	public ServerService serverService;
	
	@RequestMapping("/list")
	public String serverList(@RequestParam(value = "ip", required = false, defaultValue = "") String ip,
							 @RequestParam(value = "group", required = false, defaultValue = "3") int group,
							 @RequestParam(value = "machine", required = false, defaultValue = "3") int machine,
							 @RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
							 @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		
		SystemType systemType = SystemType.ALL;
		if(group == 2) {
			systemType = SystemType.LINUX;
		}else if(group == 1){
			systemType = SystemType.WINDOWS;
		}
		MachineType machineType = MachineType.ALL;
		if(machine == 2) {
			machineType = MachineType.VIRTUAL;
		}else if(machine == 1){
			machineType = MachineType.PHYSICAL;
		}
		Page<Server> servers = serverService.pageList(ip, systemType, machineType, pageNum, pageSize);

		/*List <Server> serverList = servers.getContent();
		long total = servers.getTotalElements();
		int number = servers.getNumber();
		int size = servers.getSize();
		Pageable pageable = new PageRequest(number, size);

		int serverListLen = serverList.size();
		List<ServerWithoutPwd> serverWithoutPwds = new ArrayList <>(serverListLen);

		serverList.forEach(server -> {
			serverWithoutPwds.add(new ServerWithoutPwd(server));
		});

		Page<ServerWithoutPwd> serverWithoutPwdPage =
				new PageImpl <ServerWithoutPwd>(serverWithoutPwds, pageable , total);
		*/
		return ResultUtil.success("请求成功", servers);
	}
	
	@RequestMapping("/pwd/list")
	public String serverPwdList(@RequestParam(value = "serverId", required = true) int serverId,
								@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
								@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		Page<ServerPwd> servers = serverService.pageServerPwds(serverId, pageNum, pageSize);
		return ResultUtil.success("请求成功", servers);
	}

	@RequestMapping("/ip/list")
	public String getIPList() {
		List<ServerModel> allServerModel = serverService.findAllServerModel();
		return ResultUtil.success("请求成功", allServerModel);
	}
	
	@RequestMapping(value="/save",method= RequestMethod.POST)
	public String saveServer(@ModelAttribute("server") Server server) {
		List <Server> serverList = serverService.findByIp(server);

		if (serverList.size() > 0) {
			return ResultUtil.error("添加失败，服务器(ip)已经存在", null);
		}
		boolean bool = serverService.addServer(server);
		if(bool) {
			return ResultUtil.addSuccess();
		}else {
			return ResultUtil.addError();
		}
	}
	
	@RequestMapping(value="/pwd/save",method= RequestMethod.POST)
	public String saveServerPwd(@ModelAttribute("serverPwd") ServerPwd serverPwd) {
		boolean bool = serverService.addServerPwd(serverPwd);
		if(bool) {
			return ResultUtil.addSuccess();
		}else {
			return ResultUtil.addError();
		}
	}
	
	@RequestMapping(value = "/delete/{id}")
	public String delServer(@PathVariable int id) {
		boolean del = serverService.delServerById(id);
		serverService.delServerPwdByServerId(id);
		if(del) {
			return ResultUtil.delSuccess();
		}else {
			return ResultUtil.delError();
		}
	}
	
	@RequestMapping(value = "/pwd/delete/{id}")
	public String delServerPwd(@PathVariable int id) {
		boolean del = serverService.delServerPwdById(id);
		if(del) {
			return ResultUtil.delSuccess();
		}else {
			return ResultUtil.delError();
		}
	}
	
	@RequestMapping(value="/update", method= RequestMethod.POST)
	public String updServer(@ModelAttribute("server") Server server) {
		boolean upd = serverService.updServer(server);
		if (false == upd) {
			return ResultUtil.updError();
		} else {
			return ResultUtil.updSuccess();
		}
	}
	
	@RequestMapping(value="/pwd/update", method= RequestMethod.POST)
	public String updServerPwd(@ModelAttribute("serverPwd") ServerPwd serverPwd) {
		boolean upd = serverService.updServerPwd(serverPwd);
		if (false == upd) {
			return ResultUtil.updError();
		} else {
			return ResultUtil.updSuccess();
		}
	}
}
