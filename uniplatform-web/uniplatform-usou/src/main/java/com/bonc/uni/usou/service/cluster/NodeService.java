package com.bonc.uni.usou.service.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.config.UsouHttpConfiguration;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.service.EnableInvoke;
import com.bonc.uni.usou.util.CommonEnum;
import com.bonc.uni.usou.util.FormatStringUtil;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.common.math.ArithUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yedunyao on 2017/8/15.
 */
@Service("nodeService")
public class NodeService implements EnableInvoke {

    @Autowired
    UsouHttpConfiguration usouHttpConfiguration;

    public String overview(String host, String authInfo) throws ResponseException {
        String nodesStats = HttpRequest.doGet(host + "/_nodes/stats/jvm,fs,os,process", authInfo, "");
        String nodes = HttpRequest.doGet(host + "/_nodes/_all/os,jvm", authInfo, "");
        String state = HttpRequest.doGet(host
                + "/_cluster/state/master_node,routing_table,blocks/", authInfo, "");


        Map<String, JSONObject> map = new HashMap<>(3);
        map.put("nodesStats", JSON.parseObject(nodesStats));
        map.put("nodes", JSON.parseObject(nodes));
        map.put("state", JSON.parseObject(state));

        String nodeOverview = JSON.toJSONString(map);
        LogManager.debug("Node overview: " + StringUtil.truncate(nodeOverview, CommonEnum.LOGMAXLENGTH));
        return nodeOverview;
    }


    /**
     *
     * @param host
     * @param nodeName
     *              ""  查询所有
     *              name
     *              name1,name2
     *              n*e 模糊查询
     * @return
     * @throws ResponseException
     */
    public String getNodeByName(String host, String authInfo, String nodeName) throws ResponseException {

        String nodesStats = "";
        String nodes = "";

        if ("".equals(StringUtil.trim(nodeName))) {
            nodesStats = HttpRequest.doGet(host + "/_nodes/stats/jvm,fs,os,process", authInfo, "");
            nodes = HttpRequest.doGet(host + "/_nodes/_all/os,jvm", authInfo, "");
        } else {
            nodesStats = HttpRequest.doGet(host + "/_nodes/" + nodeName + "/stats/jvm,fs,os,process", authInfo, "");
            nodes = HttpRequest.doGet(host + "/_nodes/" + nodeName + "/os,jvm", authInfo, "");
        }

        String state = HttpRequest.doGet(host
                + "/_cluster/state/master_node,routing_table,blocks/", authInfo, "");

        Map<String, JSONObject> map = new HashMap<>(3);
        map.put("nodes", JSON.parseObject(nodes));
        map.put("nodesStats", JSON.parseObject(nodesStats));
        map.put("state", JSON.parseObject(state));

        String node = JSON.toJSONString(map);
        LogManager.debug("Node search: " + StringUtil.truncate(node, CommonEnum.LOGMAXLENGTH));
        return node;
    }

    public String filterNodeByName(String host, String authInfo, String nodeName) throws ResponseException {
        String nodesStats = HttpRequest.doGet(host + "/_nodes/stats/jvm,fs,os,process", authInfo, "");
        String nodes = HttpRequest.doGet(host + "/_nodes/_all/os,jvm", authInfo, "");
        String state = HttpRequest.doGet(host
                + "/_cluster/state/master_node,routing_table,blocks/", authInfo, "");

        String filteredStats = filterNodeJsonByName(nodeName, nodesStats);
        String filterednodes = filterNodeJsonByName(nodeName, nodes);

        Map<String, JSONObject> map = new HashMap<>(3);
        map.put("nodes", JSON.parseObject(filterednodes));
        map.put("nodesStats", JSON.parseObject(filteredStats));
        map.put("state", JSON.parseObject(state));

        String node = JSON.toJSONString(map);
        LogManager.debug("Node search: " + StringUtil.truncate(node, CommonEnum.LOGMAXLENGTH));
        return node;
    }

    //monitor
    public String nodeMonitor(String host, String authInfo, String nodeId) throws ResponseException {

        String nodesStats = HttpRequest.doGet(host + "/_nodes/" + nodeId + "/stats/?human=true", authInfo, "");
        String nodes = HttpRequest.doGet(host + "/_nodes/" + nodeId + "/", authInfo, "");

        Map<String, JSONObject> map = new HashMap<>(2);
        map.put("nodesStats", JSON.parseObject(nodesStats));
        map.put("nodes", JSON.parseObject(nodes));

        String nodeMonitor = JSON.toJSONString(map);
        LogManager.debug("Node monitor: " + StringUtil.truncate(nodeMonitor, CommonEnum.LOGMAXLENGTH));
        return nodeMonitor;
    }

    /**
     * 创建忽略大小写的pattern
     * @param name
     * @return
     */
    public Pattern createPatternCASEINSENSITIVE(String name) {
        String regEx = ".*" + name + ".*";
        Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        return pattern;
    }

    /**
     * 从node stats、node info的json串中过滤匹配name的节点
     * @param name
     * @param json
     * @return
     */
    public String filterNodeJsonByName(String name, String json) throws ResponseException {
        Pattern pattern = createPatternCASEINSENSITIVE(name);

        JsonParser statsParser = new JsonParser(json);
        Object nodesObj = statsParser.keys("nodes");

        if (JsonParser.assertJsonValueEmpty(nodesObj)) {
            throw new ResponseException("Can not find any node.");
        }

        //将过滤获得的字符串重新拼成json
        StringBuilder builder = new StringBuilder("{\"nodes\":{");

        statsParser.map("nodes", (Map<String, JSONObject> map, JsonParser traversal) -> {
            if (map != null) {
                map.forEach((String key, JSONObject value) -> {
                    traversal.setSource(value);
                    Object nameObj = traversal.keys("name");
                    if (!JsonParser.assertJsonValueEmpty(nameObj)) {
                        String targetName = (String)nameObj;
                        Matcher matcher = pattern.matcher(targetName);
                        if (matcher.matches()) {
                            String nodeId = FormatStringUtil.toHttpBody(key);
                            String state = JSONObject.toJSONString(value);
                            builder.append(nodeId);
                            builder.append(":");
                            builder.append(state);
                            builder.append(",");
                        }
                    }
                });

                int index = builder.lastIndexOf(",");
                if (index > 0) {
                    builder.delete(index, index + 1);
                }
                builder.append("}");
                builder.append("}");
            }
        });

        String result = builder.toString();
        return result;
    }

    public JSONObject getNodeStatsCount (String host, String authInfo) throws ResponseException {
        return getNodeStatsCount(host, authInfo, usouHttpConfiguration.getRequestConfig());
    }

    //获取集群中所有节点cpu、heap、disk占用率信息
    public JSONObject getNodeStatsCount (String host, String authInfo, RequestConfig requestConfig) throws ResponseException {
        String nodeStats = HttpRequest.doGet(requestConfig, host + "/_nodes/stats/jvm,fs,process", authInfo, "");

        JsonParser parser = new JsonParser(nodeStats);

        JSONObject statsCount = new JSONObject(3);

        JSONObject cpu = new JSONObject(3);
        cpu.put("low", 0);
        cpu.put("middle", 0);
        cpu.put("high", 0);
        cpu.put("top", 0);

        JSONObject heap = (JSONObject) cpu.clone();
        JSONObject disk = (JSONObject) cpu.clone();

        statsCount.put("cpu", cpu);
        statsCount.put("heap", heap);
        statsCount.put("disk", disk);

        //遍历所有节点
        parser.map("nodes", (map, traversal) -> {
            if (map != null) {
                map.forEach((key, value) -> {
                    traversal.setSource(value);
                    String cpuPercent = String.valueOf(traversal.keys("process.cpu.percent"));
                    String heapPercent = String.valueOf(traversal.keys("jvm.mem.heap_used_percent"));
                    String diskTotalString = String.valueOf(traversal.keys("fs.total.total_in_bytes"));
                    String diskFreeString = String.valueOf(traversal.keys("fs.total.free_in_bytes"));

                    double diskTotal = Double.valueOf(diskTotalString);
                    double diskFree = Double.valueOf(diskFreeString);
                    double diskUsed = ArithUtil.sub(diskTotal, diskFree);
                    double diskPercent = ArithUtil.div(diskUsed, diskTotal, 2);

                    countNodeStats(Double.valueOf(cpuPercent), Double.valueOf(heapPercent), diskPercent, statsCount);
                });
            }
        });

        return statsCount;
    }

    /**
     * 将节点的cpu、heap、disk的占用率按等级封装成JSONObject
     * @return
     */
    public void countNodeStats (double cpu, double heap, double disk, JSONObject count) {
        countNodeStatsByType("cpu", cpu, count);
        countNodeStatsByType("heap", heap, count);
        countNodeStatsByType("disk", disk, count);
    }

    public void countNodeStatsRate (String type, String key, JSONObject count) {
        JSONObject rateObj = (JSONObject) count.get(type);
        int num = (int) rateObj.get(key);
        rateObj.put(key, ++num);
        count.put(type, rateObj);
    }

    public void countNodeStatsByType (String type, double value, JSONObject count) {
        if (value >= 0 && value < 30) {
            countNodeStatsRate(type, "low", count);
        } else if (value >= 30 && value < 60) {
            countNodeStatsRate(type, "middle", count);
        } else if (value >= 60 && value < 90) {
            countNodeStatsRate(type, "high", count);
        } else if (value >= 90 && value < 100) {
            countNodeStatsRate(type, "top", count);
        }
    }

}
