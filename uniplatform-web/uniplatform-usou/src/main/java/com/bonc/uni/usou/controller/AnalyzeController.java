package com.bonc.uni.usou.controller;

import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.entity.ClusterInfo;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.response.Response;
import com.bonc.uni.usou.service.cluster.AnalyzeService;
import com.bonc.uni.usou.service.clusters.ClustersService;
import com.bonc.uni.usou.util.ControllerUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yedunyao on 2017/8/14.
 */
@CrossOrigin
@RestController
@RequestMapping("/usou/cluster")
public class AnalyzeController {

    @Autowired
    private ClustersService clustersService;
    @Autowired
    private AnalyzeService analyzeService;

    //************************* 分词器测试 ********************

    @RequestMapping(value = "/{id}/_analyze/{analyzer}", method = RequestMethod.POST)
    public Response analyzeWithAnalyzer(@PathVariable int id, @PathVariable String analyzer,
                                        @RequestParam String text, HttpSession session)
            throws ResponseException {

        LogManager.method("--->>Cluster " + id + " analyze with analzer start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        JSONObject result =  (JSONObject) ControllerUtil.pollingCluster(analyzeService, "analyzeWithAnalyzer",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), analyzer, text);
        Response response = new Response(Response.SUCCESS, "", result, "");
        return response;
    }

    @RequestMapping(value = "/{id}/_analyze/{index}/{field}", method = RequestMethod.POST)
    public Response analyzeWithField(@PathVariable int id, @PathVariable String index,
                                     @PathVariable String field, @RequestParam String text,
                                     HttpSession session) throws ResponseException {

        LogManager.method("--->>Cluster " + id + " analyze with field start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        JSONObject result =  (JSONObject) ControllerUtil.pollingCluster(analyzeService, "analyzeWithField",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, field, text);
        Response response = new Response(Response.SUCCESS, "", result, "");
        return response;
    }

    //************************* 词典管理 **********************

    @RequestMapping(value = "/{id}/_analyze/dic/add", method = RequestMethod.POST)
    public Response dicAdd(@PathVariable int id, @RequestParam String body,
                           HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        String result = (String)ControllerUtil.pollingCluster(analyzeService, "dicAdd",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, result, "", "");
        return response;
    }

    @RequestMapping(value = "/{id}/_analyze/dic/delete", method = RequestMethod.POST)
    public Response dicDelete(@PathVariable int id, @RequestParam String body,
                              HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        String result = (String)ControllerUtil.pollingCluster(analyzeService, "dicDelete",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, result, "", "");
        return response;
    }

    @RequestMapping(value = "/{id}/_analyze/dic/update", method = RequestMethod.POST)
    public Response dicUpdate(@PathVariable int id, @RequestParam String body,
                              HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        String result = (String)ControllerUtil.pollingCluster(analyzeService, "dicUpdate",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, result, "", "");
        return response;
    }

    @RequestMapping(value = "/{id}/_analyze/dic/enable", method = RequestMethod.POST)
    public Response dicEnable(@PathVariable int id, @RequestParam String body,
                              HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        String result = (String)ControllerUtil.pollingCluster(analyzeService, "dicEnable",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, result, "", "");
        return response;
    }

    @RequestMapping(value = "/{id}/_analyze/dic/disable", method = RequestMethod.POST)
    public Response dicDisable(@PathVariable int id, @RequestParam String body,
                              HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        String result = (String)ControllerUtil.pollingCluster(analyzeService, "dicDisable",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, result, "", "");
        return response;
    }

    @RequestMapping(value = "/{id}/_analyze/dic/list", method = RequestMethod.POST)
    public Response dicList(@PathVariable int id, @RequestParam String body,
                               HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        Object result = ControllerUtil.pollingCluster(analyzeService, "dicList",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, "", result, "");
        return response;
    }

    //************************* 词管理 ************************

    @RequestMapping(value = "/{id}/_analyze/word/add", method = RequestMethod.POST)
    public Response wordAdd(@PathVariable int id, @RequestParam String body,
                           HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        String result = (String)ControllerUtil.pollingCluster(analyzeService, "wordAdd",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, result, "", "");
        return response;
    }

    @RequestMapping(value = "/{id}/_analyze/word/delete", method = RequestMethod.POST)
    public Response wordDelete(@PathVariable int id, @RequestParam String body,
                            HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        String result = (String)ControllerUtil.pollingCluster(analyzeService, "wordDelete",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, result, "", "");
        return response;
    }
    
    @RequestMapping(value = "/{id}/_analyze/word/batchdelete", method = RequestMethod.POST)
    public Response wordBatchDelete(@PathVariable int id, @RequestParam String body,
                            HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        String result = (String)ControllerUtil.pollingCluster(analyzeService, "wordBatchDelete",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, result, "", "");
        return response;
    }

    @RequestMapping(value = "/{id}/_analyze/word/search", method = RequestMethod.POST)
    public Response wordSearch(@PathVariable int id, @RequestParam String body,
                            HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        Object result = ControllerUtil.pollingCluster(analyzeService, "wordSearch",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, "", result, "");
        return response;
    }

    @RequestMapping(value = "/{id}/_analyze/word/list", method = RequestMethod.POST)
    public Response wordList(@PathVariable int id, @RequestParam String body,
                            HttpSession session) throws ResponseException {
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        Object result = ControllerUtil.pollingCluster(analyzeService, "wordList",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body);
        Response response = new Response(Response.SUCCESS, "", result, "");
        return response;
    }
    

    @RequestMapping(value = "/{id}/_analyze/word/upload", method = RequestMethod.POST)
	public Response uploadWordFile(@PathVariable int id, @RequestParam String body,
                               @RequestParam("file") MultipartFile file,
                              HttpSession session) throws ResponseException {

        LogManager.method("--->>Cluster " + id + " upload file start");

        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new ResponseException("Failed to upload file.");
        }
        String result =  (String)ControllerUtil.pollingCluster(analyzeService, "uploadWordFile",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), body, inputStream);
        Response response = new Response(Response.SUCCESS, result, "", "");
        return response;
    }

}
