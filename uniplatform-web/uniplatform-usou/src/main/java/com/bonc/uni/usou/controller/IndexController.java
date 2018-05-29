package com.bonc.uni.usou.controller;

import com.bonc.uni.usou.entity.ClusterInfo;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.response.Response;
import com.bonc.uni.usou.service.cluster.IndexService;
import com.bonc.uni.usou.util.ControllerUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by yedunyao on 2017/8/14.
 */
@CrossOrigin
@RestController
@RequestMapping("/usou/cluster")
public class IndexController {

    @Autowired
    private IndexService indexService;

    //***************************** index information ***********************

    @RequestMapping("/{id}/_overview")
    public String overview(@PathVariable int id, HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " index overview start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(indexService, "overview",
                clusterInfo.getUsrls(),clusterInfo.getAuthInfo());
    }

	@RequestMapping("/{id}/_overview/{indexName:.+}")
    public String overviewByIndexName(@PathVariable int id,
                                      @PathVariable String indexName,
                                      HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " index overview by index name start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(indexService,
                "overviewByName", clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), indexName.trim());
    }

    @RequestMapping("/{id}/{index}/_detail")
    public String getIndexDetail(@PathVariable int id, @PathVariable String index,
                                 HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " get index" + index + " detail start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(indexService, "getIndexDetail",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
    }

    @RequestMapping("/{id}/{index}/_shards")
    public String getIndexShards(@PathVariable int id, @PathVariable String index,
                                 HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " get index" + index + " shards start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(indexService, "getIndexShards",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
    }

    //***************************** create、delete ***********************

    @RequestMapping(value = "/{id}/{index}/create", method = RequestMethod.POST)
    public Response create(@PathVariable int id, @PathVariable String index,
                           @RequestParam(value = "body", required = true) String body,
                           HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " create index" + index + " start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result =  (String)ControllerUtil.pollingCluster(indexService, "create",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, body);
        return new Response(Response.SUCCESS, result, "", "");
    }

    @RequestMapping("/{id}/{index}/delete")
    public Response delete(@PathVariable int id, @PathVariable String index,
                         HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " delete index" + index + " start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String) ControllerUtil.pollingCluster(indexService, "delete",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
        return new Response(Response.SUCCESS, result, "", "");
    }

    //***************************** setting、mapping ***********************

    @RequestMapping("/{id}/_cat/indices")
    public Response getAllIndices(@PathVariable int id,
                                  @RequestParam(value = "head", required = false, defaultValue = "") String head,
                                  HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " get all indices start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String h = "," + head;
        Object result = ControllerUtil.pollingCluster(indexService, "getAllIndices",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), h);
        Response response = new Response(Response.SUCCESS, "", result, "");
        return response;
    }


    //获取索引的setting、mapping
	@RequestMapping("/{id}/{index:.+}")
    public String getIndex(@PathVariable int id, @PathVariable String index,
                         HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " get index" + index + " setting、mapping start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(indexService, "getIndex",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
    }

    @RequestMapping("/{id}/{index}/_setting")
    public String getSetting(@PathVariable int id, @PathVariable String index,
                           HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " get index" + index + " setting start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(indexService, "getSetting",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
    }

    @RequestMapping(value = "/{id}/{index}/_setting/update", method = RequestMethod.POST)
    public Response updateSetting(@PathVariable int id, @PathVariable String index,
                                @RequestParam(value = "body", required = true) String body,
                                HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " update index" + index + " setting start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "updateSetting",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, body);
        return new Response(Response.SUCCESS, result, "", "");
    }

    @RequestMapping(value = "/{id}/{index}/{type}/_mapping/put", method = RequestMethod.POST)
    public Response updateMapping(@PathVariable int id, @PathVariable String index,
                                  @PathVariable String type,
                                  @RequestParam(value = "body", required = true) String body,
                                  HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " update index" + index + " mapping start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "updateMapping",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, type, body);
        return new Response(Response.SUCCESS, result, "", "");
    }

    //发送多次请求修改mapping
    @RequestMapping(value = "/{id}/{index}/_mapping/put", method = RequestMethod.POST)
    public Response multyUpdateMapping(@PathVariable int id, @PathVariable String index,
                                       @RequestParam(value = "body", required = true) String body,
                                       HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " multy update index" + index + " mapping start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "multyUpdateMapping",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, body);
        return new Response(Response.SUCCESS, result, "", "");
    }

    @RequestMapping("/{id}/{index}/_mapping")
    public String getMapping(@PathVariable int id, @PathVariable String index,
                             HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " get index" + index + " mapping start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(indexService, "getMapping",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
    }

    //***************************** operate ***********************

    @RequestMapping("/{id}/{index}/_refresh")
    public Response refresh(@PathVariable int id, @PathVariable String index,
                             HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " refresh index" + index + " start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "refresh",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
        return new Response(Response.SUCCESS, result, "", "");
    }

    @RequestMapping("/{id}/{index}/_optimize")
    public Response optimize(@PathVariable int id, @PathVariable String index,
                          HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " optimize index" + index + " start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "optimize",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
        return new Response(Response.SUCCESS, result, "", "");
    }

    /**
     *  压缩索引
     * @param id
     * @param index
     * @param nodeName
     * @param target
     * @param body
     *       "settings": {
            "index.number_of_replicas": 1,
            "index.number_of_shards": 1,
            "index.codec": "best_compression"
            },
            "aliases": {
            "my_search_indices": {}
            }
     * @param session
     * @return
     * @throws ResponseException
     */
    @RequestMapping(value = "/{id}/{index}/_shrink/{nodeName}/{target}", method = RequestMethod.POST)
    public Response shrink(@PathVariable int id, @PathVariable String index,
                         @PathVariable String nodeName, @PathVariable String target,
                         @RequestParam(value = "body", required = true) String body,
                         HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " shrink index" + index + " start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "shrink",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, nodeName, target, body);
        return new Response(Response.SUCCESS, result, "", "");
    }

    @RequestMapping("/{id}/{index}/_flush")
    public Response flush(@PathVariable int id, @PathVariable String index,
                           HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " flush index" + index + " start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "flush",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
        return new Response(Response.SUCCESS, result, "", "");
    }

    @RequestMapping("/{id}/{index}/_clearCache")
    public Response clearCache(@PathVariable int id, @PathVariable String index,
                        HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " clear index" + index + " cache start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "clearCache",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
        return new Response(Response.SUCCESS, result, "", "");
    }

    @RequestMapping("/{id}/{index}/_close")
    public Response close(@PathVariable int id, @PathVariable String index,
                        HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " close index" + index + " start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "close",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
        return new Response(Response.SUCCESS, result, "", "");
    }

    @RequestMapping("/{id}/{index}/_open")
    public Response open(@PathVariable int id, @PathVariable String index,
                        HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " open index" + index + " start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "open",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
        return new Response(Response.SUCCESS, result, "", "");
    }

    //***************************** alias ***********************

    /**
     *
     * @param id
     * @param body
     * @param session
     * @return
     * @throws ResponseException
     */
    @RequestMapping(value = "/{id}/_aliases/add", method = RequestMethod.POST)
    public Response addAlias(@PathVariable int id,
                           @RequestParam(value = "body", required = true) String body,
                           HttpSession session)
            throws ResponseException {
        LogManager.method("--->>Cluster " + id + "add Alias start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "addAlias",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        return new Response(Response.SUCCESS, result, "", "");
    }

    @RequestMapping("/{id}/{index}/_aliases")
    public String getAliases(@PathVariable int id, @PathVariable String index, HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + "get Alias start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(indexService, "getAliases",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index);
    }

    @RequestMapping(value = "/{id}/_aliases/remove", method = RequestMethod.POST)
    public Response removeAlias(@PathVariable int id,
                              @RequestParam(value = "body", required = true) String body,
                              HttpSession session)
            throws ResponseException {
        LogManager.method("--->>Cluster " + id + "remove Alias start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "removeAlias",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        return new Response(Response.SUCCESS, result, "", "");
    }

    //************************* template ***********************

    @RequestMapping(value = "/{id}/_template/{name}/put", method = RequestMethod.POST)
    public Response putTemplates(@PathVariable int id, @PathVariable String name,
                               @RequestParam(value = "body", required = true) String body,
                               HttpSession session) throws ResponseException {

        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "putTemplates",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), name, body);
        return new Response(Response.SUCCESS, result, "", "");
    }

    @RequestMapping("/{id}/_template/{name}")
    public String getTemplates(@PathVariable int id, @PathVariable String name,
                               HttpSession session) throws ResponseException {

        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(indexService, "getTemplates",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), name);
    }

    @RequestMapping("/{id}/_template/{name}/delete")
    public Response deleteTemplates(@PathVariable int id, @PathVariable String name,
                               HttpSession session) throws ResponseException {

        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String)ControllerUtil.pollingCluster(indexService, "deleteTemplates", 
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), name);
        return new Response(Response.SUCCESS, result, "", "");
    }

}
