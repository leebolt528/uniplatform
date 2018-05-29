package com.bonc.uni.usou.service.task;

import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.common.entity.Alert;
import com.bonc.uni.common.entity.TaskArgument;
import com.bonc.uni.common.entity.TaskInfo;
import com.bonc.uni.common.quartz.service.JobService;
import com.bonc.uni.common.service.AlertService;
import com.bonc.uni.common.service.TaskService;
import com.bonc.uni.common.util.AlertUtil;
import com.bonc.uni.common.util.PlatformType;
import com.bonc.uni.usou.entity.Clusters;
import com.bonc.uni.usou.service.clusters.ClustersService;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.usdp.odk.common.string.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 集群预警 仅适用5版本
 * 
 * @author futao 2017年9月4日
 * 
 *         threshold示例
 *         [{"name":"cpu","operator":">=","value":"20"},{"name":"mem","operator":">=","value":"20"},{"name":"health","operator":">=","value":"2"},{"name":"fs","operator":">=","value":"90"},{"name":"num","operator":">=","value":"3"}]
 *			{"email":[{"user":"xxx","value":"xxxx@bonc.com.cn"}]}
 */
@Service("colonyService")
public class ColonyServiceImpl implements JobService {

	@Autowired
	AlertService alertService;

	@Autowired
	ClustersService clustersService;
	
	@Autowired
	TaskService taskService;

	@Override
	public String execute(TaskInfo taskInfo) {
		try {
			TaskInfo  taskInfon = taskService.getTaskInfoById(taskInfo.getId());
			Map<String, TaskArgument> mapTasks = AlertUtil.strToMap(taskInfon.getThreshold());
			TaskArgument healthTaskArgument = mapTasks.get("health");
			TaskArgument cpuTaskArgument = mapTasks.get("cpu");
			TaskArgument fsTaskArgument = mapTasks.get("fs");
			TaskArgument memTaskArgument = mapTasks.get("mem");
			TaskArgument numTaskArgument = mapTasks.get("num");
			int healthNum = Integer.parseInt(healthTaskArgument.getValue());
			int cpuNum = Integer.parseInt(cpuTaskArgument.getValue());
			int fsNum = Integer.parseInt(fsTaskArgument.getValue());
			int memNum = Integer.parseInt(memTaskArgument.getValue());
			int num = Integer.parseInt(numTaskArgument.getValue());
			Map<String, Integer> fsMaps = new HashMap<String, Integer>();
			Map<String, Integer> cpuMaps = new HashMap<String, Integer>();
			Map<String, Integer> memMaps = new HashMap<String, Integer>();

			//获取集群地址
			Clusters clusters = clustersService.getById(taskInfon.getBeanId());
			if(null == clusters) {
				System.out.println("获取集群Clusters失败");
				return EXECUTE_FAILURE;
			}
			String url = "http://" + clusters.getUri();
			String user = clusters.getUserName();
			String password = clusters.getPassword();
			if (StringUtil.isEmpty(user)) {
				user = "";
			}
			if (StringUtil.isEmpty(password)) {
				password = "";
			}
			String authInfo = user + ":" + password;

			//http请求 获取监控值
			String health = getHealth(url, authInfo);
			if(null == health) {
				crateAlert(taskInfon, "集群访问失败",HIGH);
			}else {
				int status = Status.valueOf(health).value;
				
				//http请求
				JSONObject nodes = getJFO(url, authInfo);
				Set<String> sets = nodes.keySet();
				Iterator<String> its = sets.iterator();

				while (its.hasNext()) {
					JSONObject node = nodes.getJSONObject(its.next());
					String ip = node.getString("ip");
					JSONObject os = node.getJSONObject("os");
					JSONObject jvm = node.getJSONObject("jvm");
					JSONObject fs = node.getJSONObject("fs");
					float percentFs = percentFs(fs);
					int ipercentFs = Math.round(percentFs * 100);
					int percentCpu = percentCpu(os);
					int percentMem = percentMem(jvm);
					if (ipercentFs >= fsNum) {
						fsMaps.put(ip, ipercentFs);
					}
					if (percentCpu >= cpuNum) {
						cpuMaps.put(ip, percentCpu);
					}
					if (percentMem >= memNum) {
						memMaps.put(ip, percentMem);
					}
				}
				String lineSeparator = System.getProperty("line.separator");
				StringBuilder msg = new StringBuilder();
				boolean alert = false;
				if (status >= healthNum) {
					alert = true;
					msg.append("集群状态").append(health).append(lineSeparator);
				}
				if (fsMaps.size() >= num) {
					alert = true;
					msg.append("硬盘使用率超过").append(fsNum).append("%的数量是").append(fsMaps.size()).append("，地址分别是[");
					Iterator<String> fsits = fsMaps.keySet().iterator();
					while (fsits.hasNext()) {
						String ip = fsits.next();
						msg.append("{ip:").append(ip).append("使用率:").append(fsMaps.get(ip)).append("}");
					}
					msg.append("]").append(lineSeparator);
				}
				if (cpuMaps.size() >= num) {
					alert = true;
					msg.append("CPU使用率超过").append(fsNum).append("%的数量是").append(cpuMaps.size()).append("，地址分别是[");
					Iterator<String> fsits = cpuMaps.keySet().iterator();
					while (fsits.hasNext()) {
						String ip = fsits.next();
						msg.append("{ip:").append(ip).append("使用率:").append(cpuMaps.get(ip)).append("}");
					}
					msg.append("]").append(lineSeparator);
				}
				if (memMaps.size() >= num) {
					alert = true;
					msg.append("内存使用率超过").append(fsNum).append("%的数量是").append(memMaps.size()).append("，地址分别是[");
					Iterator<String> fsits = memMaps.keySet().iterator();
					while (fsits.hasNext()) {
						String ip = fsits.next();
						msg.append("{ip:").append(ip).append("使用率:").append(memMaps.get(ip)).append("}");
					}
					msg.append("]").append(lineSeparator);
				}
				if (alert) {
					crateAlert(taskInfon, msg.toString(),MEDIUM);
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			return EXECUTE_FAILURE;
		}
		return EXECUTE_SUCCESS;
	}
	
	

	/**获取健康值
	 * @param host
	 * @return
	 */
	private static String getHealth(String host, String authInfo) {
		String result = null;
		try {
			String health = HttpRequest.doGet(host + "/_cluster/health", authInfo, "");
			JSONObject json = JSONObject.parseObject(health);
			result = json.getString("status");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取jvm,fs,os
	 * @param host
	 * @return
	 */
	private static JSONObject getJFO(String host, String authInfo) {
		JSONObject nodes = null;
		try {
			String strs = HttpRequest.doGet(host + "/_nodes/stats/jvm,fs,os", authInfo, "");
			JSONObject json = JSONObject.parseObject(strs);
			nodes = json.getJSONObject("nodes");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodes;
	}

	/**
	 * 集群状态
	 * 
	 * @author futao 2017年9月5日
	 */
	enum Status {
		green(1), yellow(2), red(3);
		private int value;

		private Status(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

	}

	private static int percentMem(JSONObject jvm) {
		JSONObject mem = jvm.getJSONObject("mem");
		int percent = mem.getIntValue("heap_used_percent");
		return percent;
	}

	private static int percentCpu(JSONObject os) {
		JSONObject cpu = os.getJSONObject("cpu");
		int percent = cpu.getIntValue("percent");
		return percent;
	}

	/**
	 * @param fs
	 * @return
	 */
	private static float percentFs(JSONObject fs) {
		JSONObject total = fs.getJSONObject("total");
		long total_in_bytes = total.getLongValue("total_in_bytes");
		long available_in_bytes = total.getLongValue("available_in_bytes");
		long used_in_bytes = total_in_bytes - available_in_bytes;
		float percent = (float) used_in_bytes / total_in_bytes;
		return percent;
	}

	private void crateAlert(TaskInfo taskInfo, String msg,String level) {
		Alert alert = new Alert();
		alert.setInfoId(taskInfo.getId());
		alert.setInfoName(taskInfo.getName());
		alert.setGroup(PlatformType.USOU);
		alert.setReceiver(taskInfo.getReceivers());
		alert.setMessage(msg.toString());
		alert.setLevel(level);
		alertService.saveAndSend(alert);
	}

}
