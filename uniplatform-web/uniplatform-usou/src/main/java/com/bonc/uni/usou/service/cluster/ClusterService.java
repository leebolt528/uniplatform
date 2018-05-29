package com.bonc.uni.usou.service.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.service.EnableInvoke;
import com.bonc.uni.usou.util.CommonEnum;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yedunyao on 2017/8/15.
 */
@Service("clusterService")
public class ClusterService implements EnableInvoke {


    @Autowired
    NodeService nodeService;

    /**
     * 查询指定集群的基本信息
     * @param host
     */
    public String getClusterDetail(String host, String authInfo) throws ResponseException {

        String indexStats = HttpRequest.doGet(host + "/_stats/docs,store", authInfo, "");
        String nodesStats = HttpRequest.doGet(host + "/_nodes/stats/jvm,fs,os,process", authInfo, "");
        String settings = HttpRequest.doGet(host + "/_cluster/settings", authInfo, "");
        String aliases = HttpRequest.doGet(host + "/_aliases", authInfo, "");
        String health = HttpRequest.doGet(host + "/_cluster/health", authInfo, "");
        String nodes = mergeRoles(host, authInfo);
        String state = HttpRequest.doGet(host
                + "/_cluster/state/master_node,routing_table,blocks/", authInfo, "");
        String main = HttpRequest.doGet(host + "/", authInfo, "");

        Map<String, JSONObject> map = new HashMap<>(8);
        map.put("indexStats", JSON.parseObject(indexStats));
        map.put("nodesStats", JSON.parseObject(nodesStats));
        map.put("settings", JSON.parseObject(settings));
        map.put("aliases", JSON.parseObject(aliases));
        map.put("health", JSON.parseObject(health));
        map.put("nodes", JSON.parseObject(nodes));
        map.put("state", JSON.parseObject(state));
        map.put("main", JSON.parseObject(main));

        String clusterDetail = JSON.toJSONString(map);
        LogManager.debug("Cluster overview : " + StringUtil.truncate(clusterDetail, CommonEnum.LOGMAXLENGTH));
        return clusterDetail;
    }

    //跨版本从node info中的节点设置中获取角色信息
    public Map<String, JSONArray> getRolesCrossESVersion(String host, String authInfo)
            throws ResponseException {
        String nodeInfo = HttpRequest.doGet(host + "/_nodes", authInfo, "");
        Map<String, JSONArray> nodeMap = new HashMap<>();
        JsonParser parser = new JsonParser(nodeInfo);
        parser.map("nodes", (map, traversal) -> {
            map.forEach((key, value) -> {
                traversal.setSource(value);
                Object versionObj = traversal.keys("version");
                boolean data = true;
                boolean master = true;
                if (!JsonParser.assertJsonValueEmpty(versionObj)) {
                    String versionSeries = null;
                    try {
                        versionSeries = ((String) versionObj).substring(0, 4);

                        //仅支持5.5系列
                        if (!"5.5.".equals(versionSeries)) {
                            Object dataObj = traversal.keys("settings.node.data");
                            Object masterObj = traversal.keys("settings.node.master");
                            if (!JsonParser.assertJsonValueEmpty(dataObj)) {
                                data = new Boolean((String) dataObj);
                            }
                            if (!JsonParser.assertJsonValueEmpty(masterObj)) {
                                master = new Boolean((String) masterObj);
                            }
                        }
                    } catch(StringIndexOutOfBoundsException e) {

                    }

                }
                JSONArray roles = new JSONArray();
                if (data) {
                    roles.add("data");
                }
                if (master) {
                    roles.add("master");
                }
                nodeMap.put(key, roles);
            });
        });
        return nodeMap;
    }

    //将角色信息统一成5.5版本，显示到node info的roles属性中
    public String mergeRoles(String host, String authInfo) throws ResponseException {
        String nodes = HttpRequest.doGet(host + "/_nodes/_all/os,jvm", authInfo, "");
        Map<String, JSONArray> nodeMap = getRolesCrossESVersion(host, authInfo);
        JsonParser parser = new JsonParser(nodes);
        parser.map("nodes", (map, traversal) -> {
            map.forEach((key, value) -> {
                traversal.setSource(value);
                Object versionObj = traversal.keys("version");
                if (!JsonParser.assertJsonValueEmpty(versionObj)) {
                    String versionSeries = null;
                    try {
                        versionSeries = ((String) versionObj).substring(0, 4);

                        //仅支持5.5系列
                        if (!"5.5.".equals(versionSeries)) {
                            Object roleObj = traversal.keys("roles");
                            if (JsonParser.assertJsonValueEmpty(roleObj)) {
                                JSONArray roles = nodeMap.get(key);
                                value.put("roles", roles);
                            }
                        }
                    } catch(StringIndexOutOfBoundsException e) {

                    }
                }
            });
        });
        return parser.getSource().toString();
    }

    //查看集群健康状态
    public JSONObject getClusterHealth(String host, String authInfo) throws ResponseException {
        String health = HttpRequest.doGet(host + "/_cluster/health", authInfo, "");
        return JSON.parseObject(health);
    }

    public JSONObject getClusterHealth(RequestConfig requestConfig, String host, String authInfo) throws ResponseException {
        String health = HttpRequest.doGet(requestConfig, host + "/_cluster/health", authInfo, "");
        return JSON.parseObject(health);
    }

    //获取集群版本信息
    public String getClusterVersion(String host, String authInfo) throws ResponseException {
        String cluster = HttpRequest.doGet(host, authInfo, "");
        JsonParser parser = new JsonParser(cluster);
        String version = (String) parser.keys("version.number");
        return version;
    }

    //获取集群中节点数
    public JSONObject getClusterNodes(String host, String authInfo) throws ResponseException {
        JSONObject health = getClusterHealth(host, authInfo);
        JsonParser parser = new JsonParser(health);
        Object number_of_nodes = parser.keys("number_of_nodes");
        StringBuilder builder = new StringBuilder("{\"number_of_nodes\":");
        builder = appendByAssertJsonExit(builder, number_of_nodes, 0);
        builder.append("}");
        return JSON.parseObject(builder.toString());
    }

    public JSONObject getClusterNodes(String host, String authInfo, RequestConfig requestConfig) throws ResponseException {
        JSONObject health = getClusterHealth(requestConfig, host, authInfo);
        JsonParser parser = new JsonParser(health);
        Object number_of_nodes = parser.keys("number_of_nodes");
        StringBuilder builder = new StringBuilder("{\"number_of_nodes\":");
        builder = appendByAssertJsonExit(builder, number_of_nodes, 0);
        builder.append("}");
        return JSON.parseObject(builder.toString());
    }

    //获取集群中索引数、文档数、存储容量
    public JSONObject getIndexStats(String host, String authInfo) throws ResponseException {
        String indexStats = HttpRequest.doGet(host + "/_stats/docs,store", authInfo, "");
        StringBuilder builder = new StringBuilder("{\"number_of_docs\":");

        JsonParser parser = new JsonParser(indexStats);
        Object number_of_docs = parser.keys("_all.primaries.docs.count");
        Object size_of_store = parser.keys("_all.total.store.size_in_bytes");
        String[] indices = parser.getArrayKeys("indices");
        int number_of_index = null == indices ? 0 : indices.length;

        builder = appendByAssertJsonExit(builder, number_of_docs, 0);
        builder.append(",");
        builder.append("\"size_of_store\":");
        builder = appendByAssertJsonExit(builder, size_of_store, 0);
        builder.append(",");
        builder.append("\"number_of_index\":");
        builder.append(number_of_index);
        builder.append("}");

        return JSON.parseObject(builder.toString());
    }

    public JSONObject getIndexStats(String host, String authInfo, RequestConfig requestConfig) throws ResponseException {
        String indexStats = HttpRequest.doGet(requestConfig, host + "/_stats/docs,store", authInfo, "");
        StringBuilder builder = new StringBuilder("{\"number_of_docs\":");

        JsonParser parser = new JsonParser(indexStats);
        Object number_of_docs = parser.keys("_all.primaries.docs.count");
        Object size_of_store = parser.keys("_all.total.store.size_in_bytes");
        String[] indices = parser.getArrayKeys("indices");
        int number_of_index = null == indices ? 0 : indices.length;

        builder = appendByAssertJsonExit(builder, number_of_docs, 0);
        builder.append(",");
        builder.append("\"size_of_store\":");
        builder = appendByAssertJsonExit(builder, size_of_store, 0);
        builder.append(",");
        builder.append("\"number_of_index\":");
        builder.append(number_of_index);
        builder.append("}");

        return JSON.parseObject(builder.toString());
    }

    //获取集群中的节点、索引、文档、存储
    public JSONObject getClusterStatic(String host, String authInfo) throws ResponseException {
        JSONObject nodesJson = getClusterNodes(host, authInfo);
        JSONObject indexStatsJson = getIndexStats(host, authInfo);
        JSONObject nodeStatsCount = nodeService.getNodeStatsCount(host, authInfo);
        indexStatsJson.put("number_of_nodes", nodesJson.get("number_of_nodes"));
        indexStatsJson.put("indication_rate_count", nodeStatsCount);
        return indexStatsJson;
    }

    //获取集群中的节点、索引、文档、存储
    public JSONObject getClusterStatic(String host, String authInfo, RequestConfig requestConfig) throws ResponseException {
        JSONObject nodesJson = getClusterNodes(host, authInfo, requestConfig);
        JSONObject indexStatsJson = getIndexStats(host, authInfo, requestConfig);
        JSONObject nodeStatsCount = nodeService.getNodeStatsCount(host, authInfo, requestConfig);
        indexStatsJson.put("number_of_nodes", nodesJson.get("number_of_nodes"));
        indexStatsJson.put("indication_rate_count", nodeStatsCount);
        return indexStatsJson;
    }

    public JSONArray getShardsDetail(String host, String authInfo, String index, String shardId) throws ResponseException {
        String shards = HttpRequest.doGet(host + "/" + index
                + "/_stats?level=shards&human", authInfo, "");

        //获取指定的索引分片信息
        JsonParser parser = new JsonParser(shards);
        Object shardsArr = parser.keys("indices." + index + ".shards." + shardId);
        if (JsonParser.assertJsonValueEmpty(shardsArr)) {
            throw new ResponseException("Can not find shard " + shardId + " from index " + index);
        }

        return JSON.parseArray(JSON.toJSONString(shardsArr));
    }

    public StringBuilder appendByAssertJsonExit(StringBuilder builder,
                                                Object value, int defaultValue) {

        String result = String.valueOf(value);
        if (JsonParser.assertJsonValueEmpty(value)) {
            builder.append(defaultValue);
        } else {
            builder.append(result);
        }
        return builder;
    }

}