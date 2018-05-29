package com.bonc.uni.usou.elastic;

import com.bonc.uni.usou.util.jsonParse.JsonParser;

/**
 * Created by yedunyao on 2017/8/16.
 */
public class ClusterHealth {
    private String status;
    private String cluster_name;
    private String initializing_shards;
    private String active_primary_shards;
    private String active_shards;
    private String relocating_shards;
    private String unassigned_shards;
    private String number_of_nodes;
    private String number_of_data_nodes;
    private String timed_out;
    private String shards;
    //this.fetched_at = getTimeString(new Date());

    public ClusterHealth(String health) {
        try {
            JsonParser parser = new JsonParser(health);
            this.status = (String) parser.keys("status");
            this.cluster_name = (String) parser.keys("cluster_name");
            this.initializing_shards = (String) parser.keys("initializing_shards");
            this.active_primary_shards = (String) parser.keys("active_primary_shards");
            this.active_shards = (String) parser.keys("active_shards");
            this.relocating_shards = (String) parser.keys("relocating_shards");
            this.unassigned_shards = (String) parser.keys("unassigned_shards");
            this.number_of_nodes = (String) parser.keys("number_of_nodes");
            this.number_of_data_nodes = (String) parser.keys("number_of_data_nodes");
            this.timed_out = (String) parser.keys("timed_out");
            this.shards = this.active_shards + this.relocating_shards +
                    this.unassigned_shards + this.initializing_shards;
        } catch (Exception e) {
            //log
        }

    }
}
