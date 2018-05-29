package com.bonc.uni.usou.elastic;

/**
 * Created by yedunyao on 2017/8/15.
 */
public class NodeInfo {

    private String name;
    private String elasticVersion;
    private String jvmVersion;
    //进程句柄数
    private int availableProcessors;
    private String transportAddress;
    private String host;

    private boolean master = true;
    private boolean data = true;
    private boolean client = true;
    private boolean current_master = false;

    public void setCurrentMaster() {
        this.current_master = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElasticVersion() {
        return elasticVersion;
    }

    public void setElasticVersion(String elasticVersion) {
        this.elasticVersion = elasticVersion;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public String getTransportAddress() {
        return transportAddress;
    }

    public void setTransportAddress(String transportAddress) {
        this.transportAddress = transportAddress;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    public boolean isClient() {
        return client;
    }

    public void setClient(boolean client) {
        this.client = client;
    }

    public boolean isCurrent_master() {
        return current_master;
    }

    public void setCurrent_master(boolean current_master) {
        this.current_master = current_master;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "name='" + name + '\'' +
                ", elasticVersion='" + elasticVersion + '\'' +
                ", jvmVersion='" + jvmVersion + '\'' +
                ", availableProcessors=" + availableProcessors +
                ", transportAddress='" + transportAddress + '\'' +
                ", host='" + host + '\'' +
                ", master=" + master +
                ", data=" + data +
                ", client=" + client +
                ", current_master=" + current_master +
                '}';
    }
}
