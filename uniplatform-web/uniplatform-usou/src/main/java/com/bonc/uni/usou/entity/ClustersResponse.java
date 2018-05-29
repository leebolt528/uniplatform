package com.bonc.uni.usou.entity;

import java.util.List;

/**
 * Created by yedunyao on 2017/8/10.
 */
public class ClustersResponse {

    private Clusters singleCluster;

    private List<Clusters> clusters;

    private long count;

    public ClustersResponse() {

    }

    public ClustersResponse(Clusters singleCluster) {
        this.singleCluster = singleCluster;
    }

    public ClustersResponse(List<Clusters> clusters, long count) {
        this.clusters = clusters;
        this.count = count;
    }

    public void setClusters(List<Clusters> clusters) {
        this.clusters = clusters;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<Clusters> getClusters() {
        return this.clusters;
    }

    public long getCount() {
        return count;
    }

    public Clusters getSingleCluster() {
        return singleCluster;
    }

    public void setSingleCluster(Clusters singleCluster) {
        this.singleCluster = singleCluster;
    }

    @Override
    public String toString() {
        return "ClustersResponse{" +
                "singleCluster=" + singleCluster +
                ", clusters=" + clusters +
                ", count=" + count +
                '}';
    }
}
