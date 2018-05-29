package com.bonc.uni.usou.controller;

import com.bonc.uni.usou.entity.ClusterInfo;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.response.Response;
import com.bonc.uni.usou.service.cluster.ESUserService;
import com.bonc.uni.usou.util.ControllerUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@CrossOrigin
@RestController
@RequestMapping("/usou/cluster")
public class ESUserController {
	@Autowired
	private ESUserService esuserService;

	@RequestMapping("/{id}/_privilege/list")
	public Response privileges(@PathVariable int id, HttpSession session) {
		LogManager.method("--->>Cluster " + id + " get privilege list start");
		try {
			ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

			return new Response(Response.SUCCESS, "",
					(String) ControllerUtil.pollingCluster(esuserService, "privilegeList",
							clusterInfo.getUsrls(), clusterInfo.getAuthInfo()), "");
		} catch (Exception e) {
			return new Response(Response.FAIL, e.getMessage(), "", "");
		}
	}

	@RequestMapping("/{id}/_role/list")
	public Response roles(@PathVariable int id, HttpSession session) {
		LogManager.method("--->>Cluster " + id + " get role list start");
		try {
			ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
			return new Response(Response.SUCCESS, "",
					(String) ControllerUtil.pollingCluster(esuserService, "roleList",
							clusterInfo.getUsrls(), clusterInfo.getAuthInfo()), "");
		} catch (Exception e) {
			return new Response(Response.FAIL, e.getMessage(), "", "");
		}
	}

	@RequestMapping(value = "/{id}/_role/{rolename}/get")
	public Response getRole(@PathVariable int id, @PathVariable String rolename, HttpSession session)
			throws ResponseException {
		LogManager.method("--->>Cluster " + id + " get role" + rolename + " start");
		try {
			ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
			String result = (String) ControllerUtil.pollingCluster(esuserService, "roleGet",
					clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), rolename);
			return new Response(Response.SUCCESS, "", result, "");
		} catch (Exception e) {
			return new Response(Response.FAIL, e.getMessage(), "", "");
		}
	}

	@RequestMapping(value = "/{id}/_role/{rolename}/delete")
	public Response deleteRole(@PathVariable int id, @PathVariable String rolename, HttpSession session)
			throws ResponseException {
		LogManager.method("--->>Cluster " + id + " delete role" + rolename + " start");
		try {
			ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
			String result = (String) ControllerUtil.pollingCluster(esuserService, "roleDelete",
					clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), rolename);
			return new Response(Response.SUCCESS, result, "", "");
		} catch (Exception e) {
			return new Response(Response.FAIL, e.getMessage(), "", "");
		}
	}

	@RequestMapping(value = "/{id}/_role/{rolename}/create", method = RequestMethod.POST)
	public Response createRole(@PathVariable int id, @PathVariable String rolename,
			@RequestParam(value = "clusterpri", required = true) String clusterPrivilege,
			@RequestParam(value = "indexpattern", required = true) String indexPattern,
			@RequestParam(value = "indexpri", required = true) String indexPrivilege, HttpSession session)
			throws ResponseException {
		LogManager.method("--->>Cluster " + id + " create role" + rolename + " start");
		try {
			ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
			String result = (String) ControllerUtil.pollingCluster(esuserService, "roleCreate",
					clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), rolename,
					clusterPrivilege, indexPattern, indexPrivilege);
			return new Response(Response.SUCCESS, result, "", "");
		} catch (Exception e) {
			return new Response(Response.FAIL, e.getMessage(), "", "");
		}
	}

	@RequestMapping(value = "/{id}/_role/{rolename}/update", method = RequestMethod.POST)
	public Response updateRole(@PathVariable int id, @PathVariable String rolename,
			@RequestParam(value = "clusterpri", required = true) String clusterPrivilege,
			@RequestParam(value = "indexpattern", required = true) String indexPattern,
			@RequestParam(value = "indexpri", required = true) String indexPrivilege, HttpSession session)
			throws ResponseException {
		LogManager.method("--->>Cluster " + id + " update role" + rolename + " start");
		try {
			ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
			String result = (String) ControllerUtil.pollingCluster(esuserService, "roleUpdate",
					clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), rolename,
					clusterPrivilege, indexPattern, indexPrivilege);
			return new Response(Response.SUCCESS, result, "", "");
		} catch (Exception e) {
			return new Response(Response.FAIL, e.getMessage(), "", "");
		}
	}

	@RequestMapping("/{id}/_user/list")
	public Response userList(@PathVariable int id, HttpSession session) throws ResponseException {
		LogManager.method("--->>Cluster " + id + " index overview start");
		try {
			ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
			return new Response(Response.SUCCESS, "",
					(String) ControllerUtil.pollingCluster(esuserService, "userList",
							clusterInfo.getUsrls(), clusterInfo.getAuthInfo()), "");
		} catch (Exception e) {
			return new Response(Response.FAIL, e.getMessage(), "", "");
		}
	}

	@RequestMapping(value = "/{id}/_user/{username}/get")
	public Response getUser(@PathVariable int id, @PathVariable String username, HttpSession session)
			throws ResponseException {
		LogManager.method("--->>Cluster " + id + " get user" + username + " start");
		try {
			ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
			String result = (String) ControllerUtil.pollingCluster(esuserService, "userGet",
					clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), username);
			return new Response(Response.SUCCESS, "", result, "");
		} catch (Exception e) {
			return new Response(Response.FAIL, e.getMessage(), "", "");
		}
	}

	@RequestMapping(value = "/{id}/_user/{username}/delete")
	public Response deleteUser(@PathVariable int id, @PathVariable String username, HttpSession session) {
		LogManager.method("--->>Cluster " + id + " delete user" + username + " start");
		try {
			ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
			String result = (String) ControllerUtil.pollingCluster(esuserService, "userDelete",
					clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), username);
			return new Response(Response.SUCCESS, result, "", "");
		} catch (Exception e) {
			return new Response(Response.FAIL, e.getMessage(), "", "");
		}
	}

	@RequestMapping(value = "/{id}/_user/{username}/create", method = RequestMethod.POST)
	public Response createUser(@PathVariable int id, @PathVariable String username,
			@RequestParam(value = "passwd", required = true) String passwd,
			@RequestParam(value = "roles", required = true) String roles, HttpSession session) {
		LogManager.method("--->>Cluster " + id + " create user" + username + " start");
		try {
			ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
			String result = (String) ControllerUtil.pollingCluster(esuserService, "userCreate",
					clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), username, passwd, roles);
			return new Response(Response.SUCCESS, result, "", "");
		} catch (Exception e) {
			return new Response(Response.FAIL, e.getMessage(), "", "");
		}

	}

	@RequestMapping(value = "/{id}/_user/{username}/update", method = RequestMethod.POST)
	public Response updateUser(@PathVariable int id, @PathVariable String username,
			@RequestParam(value = "passwd", required = true) String passwd,
			@RequestParam(value = "roles", required = true) String roles, HttpSession session)
			throws ResponseException {
		LogManager.method("--->>Cluster " + id + " create index" + username + " start");
		ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
		String result = (String) ControllerUtil.pollingCluster(esuserService, "userUpdate",
				clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), username, passwd, roles);
		return new Response(Response.SUCCESS, result, "", "");
	}

}
