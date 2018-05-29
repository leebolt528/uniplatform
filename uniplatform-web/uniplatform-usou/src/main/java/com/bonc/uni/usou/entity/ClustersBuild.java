package com.bonc.uni.usou.entity;

/**
 * Created by yedunyao on 2017/8/23.
 */

public class ClustersBuild {

    private int id;

    private String clusterName;

    private String connectName;

    //集群中多个client节点的uri: ip:port,ip:port,...
    private String uri;

    //private String userName;

    private String health;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getConnectName() {
        return connectName;
    }

    public void setConnectName(String connectName) {
        this.connectName = connectName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    /*public String getUserName() {
        return userName;
    }*/

    /*public void setUserName(String userName) {
        this.userName = userName;
    }*/

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    @Override
    public String toString() {
        return "ClustersBuild{" +
                "id=" + id +
                ", clusterName='" + clusterName + '\'' +
                ", connectName='" + connectName + '\'' +
                ", uri='" + uri + '\'' +
                //", userName='" + userName + '\'' +
                ", health='" + health + '\'' +
                '}';
    }
}
