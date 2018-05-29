package com.bonc.uni.usou.elastic;

/**
 * Created by yedunyao on 2017/8/14.
 */
public class Index {
    private String name;
    private Shard shard;
    private IndexMetadata metadata;

    //open,close
    private String state = "close";

    private int num_of_shards = 0;
    private int num_of_replicas = 0;
    //priavte Alias alias;

    private int num_docs = 0;
    private int deleted_docs = 0;

    /**
     * 索引的数据量
     * 单位KB,MB,GB
     * */
    private String size_in_bytes;
    private String total_size_in_bytes;

    private Shard[] unassigned;
    private boolean unhealth = false;

    //以“.”或“_”开头的索引名
    private boolean special = false;


}
