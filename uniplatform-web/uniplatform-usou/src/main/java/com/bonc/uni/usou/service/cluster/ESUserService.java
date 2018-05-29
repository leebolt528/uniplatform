package com.bonc.uni.usou.service.cluster;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.service.EnableInvoke;
import com.bonc.uni.usou.util.BCrypt;
import com.bonc.uni.usou.util.connection.HttpEnum;
import com.bonc.uni.usou.util.connection.HttpRequestUtil;
import com.bonc.usdp.odk.common.string.StringUtil;

@Service("esserService")
public class ESUserService implements EnableInvoke {

	private final String CONFIGUREURL = "/_searchguard/api/configuration/";
	private final String USERURL = "/_searchguard/api/user/";
	private final String ROLEURL = "/_searchguard/api/roles/";

	public String privilegeList(String host, String authInfo) throws ResponseException {

		String clusterPri[] = { "UNLIMITED", "CLUSTER_ALL", "CLUSTER_COMPOSITE_OPS", "CLUSTER_COMPOSITE_OPS_RO",
				"CLUSTER_MONITOR", "MANAGE_SNAPSHOTS" };
		String indexPri[] = { "UNLIMITED", "INDICES_ALL", "INDICES_MANAGE", "INDICES_MONITOR", "READ", "WRITE",
				"DELETE" };
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("clusterpri", clusterPri);
		jsonObject.put("indexpri", indexPri);
		return jsonObject.toJSONString();
	}

	public String privilegeList_bak(String host, String authInfo) throws ResponseException {
		String url = host + CONFIGUREURL + "actiongroups";
		String result = HttpRequestUtil.requestWithJson(HttpEnum.GET, url, authInfo, "");
		if (result != null) {
			JSONObject jsonObject = JSONObject.parseObject(result);
			if (jsonObject != null) {
				String[] resultArr = jsonObject.keySet().toArray(new String[0]);
				return JSON.toJSONString(resultArr);
			} else {
				throw new ResponseException(result);
			}

		} else {
			throw new ResponseException("no data");
		}
	}

	public String roleList(String host, String authInfo) throws ResponseException {
		String url = host + CONFIGUREURL + "roles";
		String result = HttpRequestUtil.requestWithJson(HttpEnum.GET, url, authInfo, "");
		if (StringUtil.isEmpty(result)) {
			throw new ResponseException("get role list failed");
		}
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject == null) {
			throw new ResponseException("get role list failed");
		}
		JSONArray jsonArray = new JSONArray();
		for (String rolename : jsonObject.keySet()) {
			JSONObject roleJsonObject = new JSONObject();
			roleJsonObject.put("name", rolename);
			JSONObject privilegeJsonObject = jsonObject.getJSONObject(rolename);
			roleJsonObject.put("clusterpri", privilegeJsonObject.get("cluster"));

			if (privilegeJsonObject.containsKey("indices")) {
				JSONObject indexJsonObject = privilegeJsonObject.getJSONObject("indices");
				for (String indexpattern : indexJsonObject.keySet()) {
					roleJsonObject.put("indexpattern", indexpattern);
					roleJsonObject.put("indexpri", indexJsonObject.getJSONObject(indexpattern).get("*"));
				}
			} else {
				roleJsonObject.put("indexpattern", "");
				roleJsonObject.put("indexpri", new String[0]);
			}
			jsonArray.add(roleJsonObject);
		}
		return jsonArray.toJSONString();
	}

	public String roleGet(String host, String authInfo, String rolename) throws ResponseException {
		String url = host + ROLEURL + rolename;
		String result = HttpRequestUtil.requestWithJson(HttpEnum.GET, url, authInfo, "");
		if (StringUtil.isEmpty(result)) {
			throw new ResponseException("get role info failed");
		}
		if (result.contains("NOT_FOUND")) {
			return result;
		}

		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject == null) {
			throw new ResponseException("get role info failed");
		}

		JSONObject roleJsonObject = new JSONObject();
		roleJsonObject.put("name", rolename);
		JSONObject privilegeJsonObject = jsonObject.getJSONObject(rolename);
		roleJsonObject.put("clusterpri", privilegeJsonObject.get("cluster"));

		if (privilegeJsonObject.containsKey("indices")) {
			JSONObject indexJsonObject = privilegeJsonObject.getJSONObject("indices");
			for (String indexpattern : indexJsonObject.keySet()) {
				roleJsonObject.put("indexpattern", indexpattern);
				roleJsonObject.put("indexpri", indexJsonObject.getJSONObject(indexpattern).get("*"));
			}
		}
		return roleJsonObject.toJSONString();
	}

	public String roleCreate(String host, String authInfo, String rolename, String clusterPrivilege, String indexPattern,
			String indexPrivilege)
			throws ResponseException {
		String roleinfo = roleGet(host, authInfo, rolename);
		if (roleinfo != null && !roleinfo.contains("NOT_FOUND")) {
			throw new ResponseException("role " + rolename + " is existed");
		}
		return roleSave(host, authInfo, rolename, clusterPrivilege, indexPattern, indexPrivilege);
	}

	public String roleUpdate(String host, String authInfo, String rolename, String clusterPrivilege, String indexPattern, String indexPrivilege)
			throws ResponseException {
		String roleinfo = roleGet(host, authInfo, rolename);
		if (roleinfo == null || roleinfo.contains("NOT_FOUND")) {
			throw new ResponseException("role " + rolename + " is not existing");
		}
		return roleSave(host, authInfo, rolename, clusterPrivilege, indexPattern, indexPrivilege);
	}

	public String roleSave(String host, String authInfo, String rolename, String clusterPrivilege, String indexPattern,
			String indexPrivilege) throws ResponseException {
		String url = host + ROLEURL + rolename;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("cluster", clusterPrivilege.split(","));
		JSONObject typeJsonObject = new JSONObject();
		typeJsonObject.put("*", indexPrivilege.split(","));
		JSONObject indexJsonObject = new JSONObject();
		indexJsonObject.put(indexPattern, typeJsonObject);

		jsonObject.put("indices", indexJsonObject);
		
		return HttpRequestUtil.requestWithJson(HttpEnum.PUT, url, authInfo, jsonObject.toJSONString());
	}

	public String roleDelete(String host, String authInfo, String rolename) throws ResponseException {
		String url = host + ROLEURL + rolename;
		return HttpRequestUtil.requestWithJson(HttpEnum.DELETE, url, authInfo, "");
	}

	public String userList(String host, String authInfo) throws ResponseException {
		String url = host + CONFIGUREURL + "internalusers";
		String result = HttpRequestUtil.requestWithJson(HttpEnum.GET, url, authInfo, "");
		if (StringUtil.isEmpty(result)) {
			throw new ResponseException("get user list failed");
		}
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject == null) {
			throw new ResponseException("get user list failed");
		}
		JSONArray jsonArray = new JSONArray();
		for (String username : jsonObject.keySet()) {
			JSONObject userJsonObject = new JSONObject();
			userJsonObject.put("name", username);
			JSONObject privilegeJsonObject = jsonObject.getJSONObject(username);
			userJsonObject.put("roles", privilegeJsonObject.get("roles"));

			jsonArray.add(userJsonObject);
		}
		return jsonArray.toJSONString();
	}

	public String userGet(String host, String authInfo, String username) throws ResponseException {
		String url = host + USERURL + username;
		String result = HttpRequestUtil.requestWithJson(HttpEnum.GET, url, authInfo, "");
		if (StringUtil.isEmpty(result)) {
			throw new ResponseException("get user info failed");
		}
		if (result.contains("NOT_FOUND")) {
			return result;
		}

		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject == null) {
			throw new ResponseException("get user list failed");
		}
		JSONObject userJsonObject = new JSONObject();
		userJsonObject.put("name", username);
		JSONObject privilegeJsonObject = jsonObject.getJSONObject(username);
		userJsonObject.put("roles", privilegeJsonObject.get("roles"));
		return userJsonObject.toJSONString();
	}

	public String userCreate(String host, String authInfo, String username, String passwd, String roles) throws ResponseException {
		String userinfo = userGet(host, authInfo, username);
		if (userinfo != null && !userinfo.contains("NOT_FOUND")) {
			throw new ResponseException("user " + username + " is existed");
		}
		return userSave(host, authInfo, username, passwd, roles);
	}

	public String userUpdate(String host, String authInfo, String username, String passwd, String roles) throws ResponseException {
		String url = host + USERURL + username;
		String result = HttpRequestUtil.requestWithJson(HttpEnum.GET, url, authInfo, "");
		if (StringUtil.isEmpty(result) || result.contains("NOT_FOUND")) {
			throw new ResponseException("user " + username + " is not existing");
		}

		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject == null) {
			throw new ResponseException("get user list failed");
		}
		if (StringUtil.isNotEmpty(passwd)) {
			try {
				jsonObject.put("hash", hash(passwd.getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				throw new ResponseException(e.getMessage());
			}
		}

		jsonObject.put("roles", roles.split(","));
		return HttpRequestUtil.requestWithJson(HttpEnum.PUT, url, authInfo, jsonObject.toJSONString());
	}

	public String userSave(String host, String authInfo, String username, String passwd, String roles) throws ResponseException {
		String url = host + USERURL + username;
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("hash", hash(passwd.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new ResponseException(e.getMessage());
		}
		jsonObject.put("roles", roles.split(","));
		return HttpRequestUtil.requestWithJson(HttpEnum.PUT, url, authInfo, jsonObject.toJSONString());
	}

	public String userDelete(String host, String authInfo, String username) throws ResponseException {
		String url = host + "/_searchguard/api/user/" + username;
		return HttpRequestUtil.requestWithJson(HttpEnum.DELETE, url, authInfo, "");
	}

	private String hash(final byte[] clearTextPassword) {
		return BCrypt.hashpw(Objects.requireNonNull(clearTextPassword), BCrypt.gensalt(12));
	}
}
