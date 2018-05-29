package com.bonc.uni.usou.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.common.service.TaskService;
import com.bonc.uni.common.util.PlatformType;
import com.bonc.uni.usou.config.UsouHttpConfiguration;
import com.bonc.uni.usou.entity.Clusters;
import com.bonc.uni.usou.entity.ClustersResponse;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.response.Response;
import com.bonc.uni.usou.service.cluster.ClusterService;
import com.bonc.uni.usou.service.clusters.ClustersService;
import com.bonc.uni.usou.util.CommonEnum;
import com.bonc.uni.usou.util.ControllerUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/usou/clusters")
public class ClustersController {

    @Autowired
    UsouHttpConfiguration usouHttpConfiguration;
    @Autowired
    private ClustersService clustersService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private TaskService taskService;

    @RequestMapping("/overview")
    public ClustersResponse findAll() {
        int count = (int)clustersService.count();
        List<Clusters> findClusters = clustersService.findAll(0, count);
        return new ClustersResponse(findClusters, count);
    }

    @RequestMapping("/overview/{page}")
    public ClustersResponse findAll(@PathVariable int page,
                                    @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                                    @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
                                    @RequestParam(value = "properties", required = false,
                                            defaultValue = "id") String properties) {
        List<Clusters> findClusters = clustersService.findAll(page, size, order, properties);
        long count = clustersService.count();
        return new ClustersResponse(findClusters, count);
    }

    @RequestMapping("/exist/{connectName}")
    public String findByConnectName(@PathVariable String connectName) throws ResponseException {
        Clusters findClusters = clustersService.findByConnectName(connectName);

        if (findClusters != null) {
            throw new ResponseException("ConnectName has already exist");
        }
        return CommonEnum.SUCCESS;
    }

    @RequestMapping("/get/{id}")
    public ClustersResponse findById(@PathVariable int id) throws ResponseException {
        Clusters findClusters = clustersService.findById(id);

        if (findClusters == null) {
            throw new ResponseException("Cluster can not found");
        }
        return new ClustersResponse(findClusters);
    }

    @RequestMapping(value="/save", method= RequestMethod.POST)
    public String saveCluster(@ModelAttribute("clusters")Clusters clusters)
            throws ResponseException {
        LogManager.method("Start save cluster.");

        Clusters save = clustersService.save(clusters);

        LogManager.finer(save.toString());

        if(save != null) {
            return CommonEnum.SUCCESS;
        }
        throw new ResponseException("Save cluster failed");
    }

    @RequestMapping(value="/delete", method= RequestMethod.POST)
    public String delCluster(int id, HttpSession session) throws ResponseException {

        LogManager.method("Start delete cluster.");

        int result = clustersService.delete(id);

        LogManager.finer("Result: delete number" + result);

        if(result == 1) {

            //删除预警
            LogManager.finer("Delete taskInfo by id: " + id);
            taskService.delTaskInfoByBeanId(id, PlatformType.USOU);

            //清除session中缓存的cluster
            ControllerUtil.clearClusterSession(id, session);

            return CommonEnum.SUCCESS;
        }
        throw new ResponseException("Delete cluster failed");
    }

    /**
     *根据id批量删除
     *
     * ids: 1,3,4,5
     */
    @RequestMapping(value="/mulDelete", method= RequestMethod.POST)
    public String mulDelClusters(String ids, HttpSession session) throws ResponseException {

        LogManager.method("Start multi delete clusters.");

        int result = clustersService.deleteByIds(ids);

        if(result >= 1) {
            String[] idArr = ids.split(",");
            for (String id : idArr) {
                //删除预警
                taskService.delTaskInfoByBeanId(Integer.valueOf(id), PlatformType.USOU);

                //清除session中缓存的cluster
                ControllerUtil.clearClusterSession(Integer.valueOf(id), session);
            }

            return CommonEnum.SUCCESS;
        }
        throw new ResponseException("Delete cluster failed");
    }

    @RequestMapping(value="/update", method= RequestMethod.POST)
    public String updateCluster(@ModelAttribute("clusters")Clusters cluster, HttpSession session)
            throws ResponseException {

        LogManager.method("Start update cluster.");

        int result = clustersService.updateCluster(cluster);

        if(result == 1) {
            //清除session中缓存的cluster
            ControllerUtil.clearClusterSession(cluster.getId(), session);

            return CommonEnum.SUCCESS;
        }
        throw new ResponseException("Update cluster failed");
    }

    //---------------------------------图表------------------------------

    //获取多集群中的节点、索引、文档、存储分布
    @RequestMapping("/statistics")
    public Response getClustersStatic() throws ResponseException {

        //设置http请求超时时间为2s， 避免页面刷新慢
        RequestConfig requestConfig = usouHttpConfiguration.getShortRequestConfig();

        //获取健康状态非red即state小于2的集群
        List<Clusters> all = clustersService.findClusterByStateLessThan(2,0, 15);

        JSONArray array = new JSONArray();
        for (Clusters cluster : all) {
            String uri = cluster.getUri();
            String clusterName = cluster.getClusterName();
            String[] urls = uri.split(",");
            String user = cluster.getUserName();
            String password = cluster.getPassword();
            if (StringUtil.isEmpty(user)) {
                user = "";
            }
            if (StringUtil.isEmpty(password)) {
                password = "";
            }
            String authInfo = user + ":" + password;

            JSONObject clustersStatic = null;
            try {
                clustersStatic = (JSONObject) ControllerUtil.pollingCluster(clusterService,
                        "getClusterStatic", urls, authInfo, requestConfig);
                clustersStatic.put("cluster", clusterName);
                array.add(clustersStatic);
            } catch (ResponseException e) {
                e.printStackTrace();
            }
        }
        return new Response(Response.SUCCESS, "", array, null);
    }
}
