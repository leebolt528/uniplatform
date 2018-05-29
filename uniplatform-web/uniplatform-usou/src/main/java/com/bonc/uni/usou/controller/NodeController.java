package com.bonc.uni.usou.controller;

import com.bonc.uni.usou.entity.ClusterInfo;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.service.cluster.NodeService;
import com.bonc.uni.usou.util.ControllerUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by yedunyao on 2017/8/14.
 */
@CrossOrigin
@RestController
@RequestMapping("/usou/cluster")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    @RequestMapping("/{id}/_node")
    public String overview(@PathVariable int id, HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " node overview start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(nodeService, "overview",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo());
    }

    /**
     *
     * @param id
     * @param nodeName
     *              ""  查询所有
     *              name
     *              name1,name2
     *              n*e 模糊查询
     * @param session
     * @return
     * @throws ResponseException
     */
    @RequestMapping("/{id}/_node/{nodeName}")
    public String getNodeByName(@PathVariable int id, @PathVariable String nodeName, HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " get node" + nodeName + " start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(nodeService, "filterNodeByName",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), nodeName);
    }

    @RequestMapping("/{id}/_node/{nodeId}/monitor")
    public String nodeMonitor(@PathVariable int id, @PathVariable String nodeId, HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " monitor node" + nodeId + " start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(nodeService, "nodeMonitor",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), nodeId);
    }
}
