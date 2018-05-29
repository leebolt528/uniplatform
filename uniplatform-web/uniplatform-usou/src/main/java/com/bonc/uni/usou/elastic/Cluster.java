package com.bonc.uni.usou.elastic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.util.jsonParse.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yedunyao on 2017/8/14.
 */
public class Cluster {

    //this.created_at = new Date().getTime();

    //访问的client节点的名称
    private String clientName;

    // Cluster Health(/_cluster/health)
    private ClusterHealth health;

    //cluster name
    private String name;
    private String master_node;

    //private boolean disableAllocation = false;

    private String settings;

    private List<Node> nodes;

    private String indices;
    private String specialIndices = "0";
    private int closedIndices = 0;
    private String num_docs;
    private String total_size_in_bytes;
    private String total_indices;

    //this.changes = null;
    public static void main(String[] args) {

    }
    public Cluster(String health, String state, String indexStats, String nodesStats,
                   String aliases, String nodes, String main) {

        this.health = new ClusterHealth(health);

        JsonParser stateParser = new JsonParser(state);
        this.name = (String) stateParser.keys("cluster_name");
        this.master_node = (String) stateParser.keys("master_node");
        stateParser.map("blocks.indices", (Map<String, JSONObject> map, JsonParser traversal) -> {
            if (map != null) {
                map.forEach((String key, JSONObject value) -> {
                    traversal.setSource(value);
                    if (traversal.keys(key + ".4") != "" ) {
                        this.closedIndices++;
                    }
                });
            }
        });


        JsonParser mainParser = new JsonParser(main);
        this.clientName = (String)mainParser.keys(name);

        JsonParser nodesStatsParser = new JsonParser(nodesStats);
        Map<String, JSONObject> nodesStatsMap = nodesStatsParser.getArrayMaps("nodes");

        JsonParser nodesParser = new JsonParser(nodes);
        nodesParser.map("nodes", (map, traversal) -> {
            if (map != null) {
                this.nodes = new ArrayList<>(map.size());
                map.forEach((String nodeId, JSONObject value) -> {
                    JSONObject nodesStatsJson = nodesStatsMap.get(nodeId);
                    NodeStats nodeStats = JSON.parseObject(
                            nodesStatsJson.toJSONString(), NodeStats.class);
                    traversal.setSource(value);
                    NodeInfo nodeInfo = (NodeInfo) traversal.keys(nodeId);
                    Node node = new Node(nodeId, nodeInfo, nodeStats);
                    this.nodes.add(node);
                });
            }
        });
    }

}
