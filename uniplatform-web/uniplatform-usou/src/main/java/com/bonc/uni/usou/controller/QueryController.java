package com.bonc.uni.usou.controller;

import com.alibaba.fastjson.JSON;
import com.bonc.uni.usou.entity.ClusterInfo;
import com.bonc.uni.usou.exception.ConnectionException;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.response.Response;
import com.bonc.uni.usou.service.cluster.QueryService;
import com.bonc.uni.usou.util.ControllerUtil;
import com.bonc.uni.usou.util.FileUtils;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

/**
 * Created by yedunyao on 2017/8/29.
 */
@CrossOrigin
@RestController
@RequestMapping("/usou/cluster")
public class QueryController {

    @Autowired
    private QueryService queryService;

    //*************************** 数据浏览 ***************************

    @RequestMapping("/{id}/_search")
    public String overview(@PathVariable int id, HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " search overview start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(queryService, "overview", clusterInfo.getUsrls(),
                clusterInfo.getAuthInfo(), new String("*"));
    }

    //*************************** 基本查询 ***************************

    @RequestMapping("/{id}/_search/metadata")
    public String getMetadata(@PathVariable int id, HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " search getMetadata start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(queryService, "getMetadata",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo());
    }

    //*************************** 复合查询 ***************************

    @RequestMapping(value = "/{id}/_search/complex", method = RequestMethod.POST)
    public String complexQuery(@PathVariable int id,
                               @RequestParam String route,
                               @RequestParam String method,
                               @RequestParam String body,
                               HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " complex query start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(queryService, "complexQuery",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), route, method, body);
    }

    //*************************** sql查询 ***************************

    @RequestMapping(value = "/{id}/_sql", method = RequestMethod.POST)
    public String queryBySql(@PathVariable int id,
                             @RequestParam String index,
                             @RequestParam(value = "sql", required = true) String sql,
                             HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " query by sql start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(queryService,
                "queryBySql", clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, sql);
    }

    @RequestMapping(value = "/{id}/_sql/explain", method = RequestMethod.POST)
    public Response sqlExplain(@PathVariable int id,
                               @RequestParam String index,
                               @RequestParam(value = "sql", required = true) String sql,
                               HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " sql explain start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        Object result = ControllerUtil.pollingCluster(queryService,
                "sqlExplain", clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, sql);
        return new Response(Response.SUCCESS, "", result, "");
    }

    //*************************** 通用操作 ***************************

    @RequestMapping(value = "/{id}/_search/common", method = RequestMethod.POST)
    public String queryWithFields(@PathVariable int id,
                                  @RequestParam(value = "index", required = true) String index,
                                  @RequestParam(value = "body", required = true) String body,
                                  HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " search queryWithFields start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        return (String)ControllerUtil.pollingCluster(queryService, "queryWithFields",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, body);
    }

    @RequestMapping(value = "/{id}/{index}/{type}/{docId}", method = RequestMethod.GET)
    public Response queryDoc(@PathVariable int id, @PathVariable String index,
                             @PathVariable String type, @PathVariable String docId,
                             @RequestParam(value = "routing", required = false) String routing,
                             HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " query doc start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        Object queryDoc = ControllerUtil.pollingCluster(queryService, "queryDoc",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, type, docId, routing == null ? "" : routing);
        return new Response(Response.SUCCESS, "", queryDoc, "");
    }

    @RequestMapping(value = "/{id}/{index}/{type}/{docId}/_update", method = RequestMethod.POST)
    public Response update(@PathVariable int id, @PathVariable String index,
                         @PathVariable String type, @PathVariable String docId,
                         @RequestParam(value = "body", required = true) String body,
                         HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " update data start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String) ControllerUtil.pollingCluster(queryService, "update",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, type, docId, body);
        return new Response(Response.SUCCESS, result, "", "");
    }

    /**
     * bulk删除多个文档
     * @param id
     * @param docIds
     *          index1/type1/id1,index2/type2/id2
     * @return
     * @throws ConnectionException
     */
    @RequestMapping(value = "/{id}/_muldelete")
    public Response bulkDeleteByDocIds(@PathVariable int id,
                                       @RequestParam(value = "docIds", required = true) String docIds,
                                       HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " delete by doc id start");
        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String result = (String) ControllerUtil.pollingCluster(queryService, "bulkDeleteByDocIds",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), docIds);
        return new Response(Response.SUCCESS, result, "", "");
    }

    //根据docId导出文件
    @RequestMapping(value = "/{id}/downloadByDocId", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadByDocId(@PathVariable int id,
                                                  @RequestParam(value = "docIds", required = true) String docIds,
                                                  HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " download json start");

        HttpHeaders h = new HttpHeaders();
        h.add("Content-Type", "text/json; charset=UTF-8");
        String fileName = "data" + FileUtils.genFileNameByDate() + ".txt";
        h.setContentDispositionFormData("filename", fileName);

        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String data = (String) ControllerUtil.pollingCluster(queryService,
                "query4DownloadById",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), docIds);

        ResponseEntity<byte[]> responseEntity = null;
        try {
            responseEntity = new ResponseEntity<>(data.getBytes("UTF-8"), h, HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            ResponseException exception = new ResponseException("Download json file failed, " +
                    "because can not cast query result to bytes unsupported encoding UTF-8.");
            exception.addSuppressed(e);
            LogManager.Exception(exception);
            throw exception;
        }
        return responseEntity;
    }

    //导出json文件
    @RequestMapping(value = "/{id}/downloadJson", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadJson(@PathVariable int id,
                                               @RequestParam String index,
                                               @RequestParam String body,
                                               HttpSession session) throws ResponseException {
        LogManager.method("--->>Cluster " + id + " download json start");

        HttpHeaders h = new HttpHeaders();
        h.add("Content-Type", "text/json; charset=UTF-8");
        String fileName = "data" + FileUtils.genFileNameByDate() + ".txt";
        h.setContentDispositionFormData("filename", fileName);

        ClusterInfo clusterInfo = ControllerUtil.getClusterInfoFromSession(id, session);
        String data = "";
        Object result = ControllerUtil.pollingCluster(queryService,
                "query4Download",
                clusterInfo.getUsrls(), clusterInfo.getAuthInfo(), index, body);

        ResponseEntity<byte[]> responseEntity = null;
        try {
            if (result == null) {
                data = "";
            } else {
                data = JSON.toJSONString(result, true);
            }

            responseEntity = new ResponseEntity<>(data.getBytes("UTF-8"), h, HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            ResponseException exception = new ResponseException("Download json file failed, " +
                    "because can not cast query result to bytes unsupported encoding UTF-8.");
            exception.addSuppressed(e);
            LogManager.Exception(exception);
            throw exception;
        }
        return responseEntity;
    }

    //导出json文件 第二种方法
    /*@RequestMapping(value = "/{id}/download", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "text/html;charset=UTF-8")
    public String dataDownload(@PathVariable int id,
                               @RequestParam String index,
                               @RequestParam String body,
                               HttpSession session,
                               HttpServletResponse response) throws ResponseException {

        LogManager.Process("Process in controller: data download");
        OutputStream out = null;

        String[] urls = ControllerUtil.getUriFromSession(id, session);
        String result = (String) ControllerUtil.pollingCluster(queryService,
                "query4Download", urls, index, body);

        String fileName = "data" + FileUtils.genFileNameByDate() + ".txt";

        response.setContentType("application/x-msdownload");
        response.addHeader("Content-Disposition",
                "attachment;filename=\"" + fileName + "\"");

        try {
            //判断查询结果是否有数据
            JsonParser jsonParser = new JsonParser(result);
            Object value = jsonParser.keys("hits.hits");
            if (JsonParser.assertJsonValueEmpty(value)) {
                throw new ResponseException("Download json file failed, because query do not hits data.");
            }
            //截取hit中的数据转成string
            result = JSON.toJSONString(value, true);

            out = response.getOutputStream();
            out.write(result.getBytes());
        } catch (IOException e) {
            throw new ResponseException("Failed to download");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LogManager.Exception("DicManagementController dicWordsDownload exception : ", e);
                }
            }
        }

        return null;
    }*/
}
