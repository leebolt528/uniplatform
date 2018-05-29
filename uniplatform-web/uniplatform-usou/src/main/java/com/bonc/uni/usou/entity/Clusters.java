package com.bonc.uni.usou.entity;

import com.bonc.uni.common.entity.EntityCommon;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by yedunyao on 2017/8/10.
 * 用于对多个集群的信息管理
 */
@Entity
@Table(name = "usou_cluster")
public class Clusters extends EntityCommon {

    @Column(nullable = false)
    private String clusterName;

    @Column(nullable = false, unique = true)
    private String connectName;

    //集群中多个client节点的uri: ip:port,ip:port,...
    @Column(nullable = false)
    private String uri;

    @Column
    private String userName;

    @Column
    private String password;

    /**
     * 集群的状态信息，用于连接时的过滤
     * 0 green,1 yellow,2 red,3 停用
     */
    @Column
    private int state;

    @Transient
    private String health;

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public String getConnectName() {
        return connectName;
    }

    public void setConnectName(String connectName) {
        this.connectName = connectName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Clusters{" +
                "clusterName='" + clusterName + '\'' +
                ", connectName='" + connectName + '\'' +
                ", uri='" + uri + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", state=" + state +
                ", health='" + health + '\'' +
                '}';
    }
}
