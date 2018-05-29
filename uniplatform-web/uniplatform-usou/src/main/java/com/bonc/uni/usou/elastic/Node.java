package com.bonc.uni.usou.elastic;

import java.util.Date;

/**
 * Created by yedunyao on 2017/8/15.
 */
public class Node {
    private String id;

    private NodeInfo nodeInfo;

    //node stats
    private NodeStats nodeStats;

    public Node (String id, NodeInfo nodeInfo, NodeStats nodeStats) {
        this.id = id;
        this.nodeInfo = nodeInfo;
        this.nodeStats = nodeStats;
    }


    public void setCurrentMaster() {
        nodeInfo.setCurrentMaster();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        return id.equals(node.id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public NodeStats getNodeStats() {
        return nodeStats;
    }

    public void setNodeStats(NodeStats nodeStats) {
        this.nodeStats = nodeStats;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", nodeInfo=" + nodeInfo +
                ", nodeStats=" + nodeStats +
                '}';
    }
}
