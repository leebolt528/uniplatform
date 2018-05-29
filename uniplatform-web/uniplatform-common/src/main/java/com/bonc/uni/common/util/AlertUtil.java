package com.bonc.uni.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.common.entity.TaskArgument;
import com.google.common.base.Joiner;

/**
 * 预警工具类
 * @author futao
 * 2017年9月1日
 * 
 * receivers 
 * {"message":"[{\"user\":\"xxx\",\"value\":\"12324354353545\"}]","email":"[{\"user\":\"xxxx\",\"value\":\"@bonc.com.cn\"}]"}
 */
public class AlertUtil {
	
	public static List<TaskArgument> strToBean(String strs){
		List<TaskArgument> lists = JSONObject.parseArray(strs, TaskArgument.class);
		return lists;
	}
	
	public static Map<String,TaskArgument> strToMap(String strs){
		Map<String,TaskArgument> maps = new HashMap<String,TaskArgument>();
		List<TaskArgument> lists = JSONObject.parseArray(strs, TaskArgument.class);
		Iterator<TaskArgument> its = lists.iterator();
		while(its.hasNext()) {
			TaskArgument taskArgument = its.next();
			maps.put(taskArgument.getName(), taskArgument);
		}
		return maps;
	}
	
	public static String listToStr(List<TaskArgument> lists) {
		String strs = JSONArray.toJSONString(lists);
		return strs;
	}

	public static List<String> parseReceivers(String pushBy, String receiversStr) {
		JSONArray receivers = JSONObject.parseObject(receiversStr).getJSONArray(pushBy);
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < receivers.size(); i++) {
			list.add(((JSONObject) receivers.get(i)).getString("value"));
		}
		return list;
	}

	public static Map<String, List<String>> getPushBy(String receivers) {
		Map<String, List<String>> pushBy = new HashMap<String, List<String>>();
		List<String> mobiles = AlertUtil.parseReceivers("message", receivers);
		if (null != mobiles && mobiles.size() > 0) {
			pushBy.put("短信", mobiles);
		}

		List<String> wechats = AlertUtil.parseReceivers("wechat", receivers);
		if (null != wechats && wechats.size() > 0) {
			pushBy.put("微信", wechats);
		}

		List<String> emails = AlertUtil.parseReceivers("email", receivers);
		if (null != emails && emails.size() > 0) {
			pushBy.put("邮箱", emails);
		}
		return pushBy;
	}
	
	public static String parseThresholdOper(String key, String thresholdStr) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		try {
			JSONObject job = JSONObject.parseObject(thresholdStr);
			JSONObject newJob = job.getJSONObject(key);
			return (null != newJob) ? newJob.getString("operator") : null;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Object parseThresholdValue(String key, String thresholdStr) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		try {
			JSONObject job = JSONObject.parseObject(thresholdStr);
			JSONObject newJob = job.getJSONObject(key);
			return (null != newJob) ? newJob.get("value") : null;
			
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取接受者的类型
	 * 
	 * @param receiversStr
	 *            {"wechart":"[{\"user\":\"\",\"value\":\"18511863382\"}]","message":"[{\"user\":\"王老三\",\"value\":\"18511863382\"}]"}
	 * @return wechart,message
	 */
	public static String getAllKeys(String receiversStr) {
		try {
			JSONObject receiversJson = JSONObject.parseObject(receiversStr);
			if (StringUtils.isNotBlank(receiversStr)) {
				List<String> lists = new ArrayList<String>();
				Set<String> keys = receiversJson.keySet();
				Iterator<String> its = keys.iterator();
				while (its.hasNext()) {
					String key = its.next();
					ReceiverType receiverType = ReceiverType.valueOf(key);
					lists.add(receiverType.getDescription());
				}
				Joiner joiner = Joiner.on(",").skipNulls();
				String keyStr = joiner.join(lists);
				return keyStr;
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}

	/**
	 * 获取接收者的json数组
	 * 
	 * @param receiversStr
	 * @return
	 */
	public static JSONArray getReceiversToList(String receiversStr) {
		JSONArray jsons = new JSONArray();
		try {
			JSONObject receiversJson = JSONObject.parseObject(receiversStr);
			for (ReceiverType receiverType : ReceiverType.values()) {
				JSONObject json = new JSONObject();
				JSONArray receivers = receiversJson.getJSONArray(receiverType.name());
				if (null == receivers) {
					receivers = new JSONArray();
				}
				json.put("type", receiverType.name());
				json.put("values", receivers);
				jsons.add(json);
			}
		} catch (Exception e) {
			return jsons;
		}
		return jsons;
	}

	/**
	 * 接收者枚举类
	 * 
	 * @author futao
	 *
	 */
	public enum ReceiverType {
		message("短信"), email("邮箱"), wechart("微信");

		private String description;

		ReceiverType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}
	
	
	
	public static void main(String args[]) {
		JSONArray aa = new JSONArray();
		JSONObject bb = new JSONObject();
		bb.put("user", "futao");
		bb.put("value", "futao@bonc.com.cn");
		aa.add(bb);
		System.out.println(aa);
		JSONObject cc = new JSONObject();
		cc.put("email", aa);
		System.out.println(cc);
		JSONObject receivers = JSONObject.parseObject("{\"email\":[{\"user\":\"futao\",\"value\":\"futao@bonc.com.cn\"}]}");
		System.out.println(receivers);
		JSONArray receiverss = receivers.getJSONArray("email");
		System.out.println(receiverss);
		List<String> emails = AlertUtil.parseReceivers("email", "{\"email\":[{\"user\":\"futao\",\"value\":\"futao@bonc.com.cn\"}]}");
		System.out.println(emails.size());
	}
}
