package com.bonc.uni.usou.elastic;

/**
 * Created by yedunyao on 2017/8/14.
 */
public class Shard {

    private String primary;
    private String shard;
    private String state;
    private String node;
    private String index;
    private String id = this.node + '_' + this.shard + '_' + this.index;
}
