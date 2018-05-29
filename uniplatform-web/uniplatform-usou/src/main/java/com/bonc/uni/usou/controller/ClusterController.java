package com.bonc.uni.usou.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.entity.ClusterInfo;
import com.bonc.uni.usou.entity.Clusters;
import com.bonc.uni.usou.exception.ConnectionException;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.response.Response;
import com.bonc.uni.usou.service.cluster.ClusterService;
import com.bonc.uni.usou.service.clusters.ClustersService;
import com.bonc.uni.usou.util.CommonEnum;
import com.bonc.uni.usou.util.ControllerUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
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
public class ClusterController {

    @Autowired
    private ClustersService clustersService;
    @Autowired
    private ClusterService clusterService;

    @RequestMapping("/{id}")
    public String overview(@PathVariable int id, HttpSession session) throws ResponseException {

        LogManager.method("--->>Cluster overview start");

        Clusters clusters = clustersService.findClustersById(id);
        if (clusters == null) {
            throw new ResponseException("Cluster is not found.");
        }

        String uri = clusters.getUri();
        String[] urls = StringUtil.split(uri, ",");

        String user = clusters.getUserName();
        String password = clusters.getPassword();

        ClusterInfo clusterInfo = new ClusterInfo(urls, user, password);

        String key = CommonEnum.CLUSTERIDPREFIX + id + CommonEnum.CLUSTERIDSUFFIX;

        if (session.getAttribute(key) == null) {
            session.setAttribute(key, clusterInfo);
            session.setMaxInactiveInterval(60 * 60 * 60);
        }

        String url = null;
        for(int i = 0; i < urls.length; i++) {
            url = urls[i];
            String host = "http://" + url;
            String result = null;
            try {
                result = clusterService.getClusterDetail(host, clusterInfo.getAuthInfo());
                return result;
            } catch (ConnectionException e) {
                if (StringUtil.isEmpty(result)) {
                    throw new ConnectionException("response is empty,cluster maybe has an error.");
                }
            }
        }

        throw new ResponseException("Cluster connect failed.");
    }

    @RequestMapping("/{id}/health")
    public Response getClusterHealth(@PathVariable int id, HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " get cluster health start");
        /*String[] urls = ControllerUtil.getUriFromSession(id, session);
        String authInfo = ControllerUtil.getAuthInfoFromSession(id, session);*/
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        JSONObject result =  (JSONObject) ControllerUtil.pollingCluster(clusterService,
                "getClusterHealth", clusterInfo.getUsrls(), clusterInfo.getAuthInfo());
        Response response = new Response(Response.SUCCESS, "", result, "");
        return response;
    }

    @RequestMapping("/{id}/{index}/{shardId}")
    public Response getShardsDetail(@PathVariable int id, @PathVariable String index,
                                    @PathVariable String shardId, HttpSession session)
            throws ResponseException {

        LogManager.method("--->>Cluster " + id + " get shards detail start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        JSONArray result =  (JSONArray) ControllerUtil.pollingCluster(clusterService,
                "getShardsDetail", clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, shardId);
        Response response = new Response(Response.SUCCESS, "", result, "");
        return response;
    }

}
