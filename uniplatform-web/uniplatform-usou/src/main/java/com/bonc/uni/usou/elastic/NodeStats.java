package com.bonc.uni.usou.elastic;

import java.util.Date;

/**
 * Created by yedunyao on 2017/8/17.
 */
public class NodeStats {

    private String name;

    //nodeStats.jvm.uptime_in_millis
    private String uptime;

    private String heap_used;
    private String heap_committed;
    private String heap_used_percent;
    private String heap_max;

    private String disk_total_in_bytes;
    private String disk_free_in_bytes;
    private String disk_used_percent;

    private String cpu;
    private String load_average;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getHeap_used() {
        return heap_used;
    }

    public void setHeap_used(String heap_used) {
        this.heap_used = heap_used;
    }

    public String getHeap_committed() {
        return heap_committed;
    }

    public void setHeap_committed(String heap_committed) {
        this.heap_committed = heap_committed;
    }

    public String getHeap_used_percent() {
        return heap_used_percent;
    }

    public void setHeap_used_percent(String heap_used_percent) {
        this.heap_used_percent = heap_used_percent;
    }

    public String getHeap_max() {
        return heap_max;
    }

    public void setHeap_max(String heap_max) {
        this.heap_max = heap_max;
    }

    public String getDisk_total_in_bytes() {
        return disk_total_in_bytes;
    }

    public void setDisk_total_in_bytes(String disk_total_in_bytes) {
        this.disk_total_in_bytes = disk_total_in_bytes;
    }

    public String getDisk_free_in_bytes() {
        return disk_free_in_bytes;
    }

    public void setDisk_free_in_bytes(String disk_free_in_bytes) {
        this.disk_free_in_bytes = disk_free_in_bytes;
    }

    public String getDisk_used_percent() {
        return disk_used_percent;
    }

    public void setDisk_used_percent(String disk_used_percent) {
        this.disk_used_percent = disk_used_percent;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getLoad_average() {
        return load_average;
    }

    public void setLoad_average(String load_average) {
        this.load_average = load_average;
    }

    @Override
    public String toString() {
        return "NodeStats{" +
                "name='" + name + '\'' +
                ", uptime='" + uptime + '\'' +
                ", heap_used='" + heap_used + '\'' +
                ", heap_committed='" + heap_committed + '\'' +
                ", heap_used_percent='" + heap_used_percent + '\'' +
                ", heap_max='" + heap_max + '\'' +
                ", disk_total_in_bytes='" + disk_total_in_bytes + '\'' +
                ", disk_free_in_bytes='" + disk_free_in_bytes + '\'' +
                ", disk_used_percent='" + disk_used_percent + '\'' +
                ", cpu='" + cpu + '\'' +
                ", load_average='" + load_average + '\'' +
                '}';
    }
}
