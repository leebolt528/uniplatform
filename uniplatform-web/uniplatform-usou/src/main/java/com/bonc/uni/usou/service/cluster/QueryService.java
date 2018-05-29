package com.bonc.uni.usou.service.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.service.EnableInvoke;
import com.bonc.uni.usou.util.CommonEnum;
import com.bonc.uni.usou.util.FormatStringUtil;
import com.bonc.uni.usou.util.connection.HttpEnum;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yedunyao on 2017/8/15.
 */
@Service("queryService")
public class QueryService implements EnableInvoke {

    //*************************** 数据浏览 ***************************

    /**
     * 数据概览
     * @param host
     * @param index
     *          ""
     *          *
     *          index
     *          inde*
     *          index1,index2
     * @return
     * @throws ResponseException
     */
    public String overview(String host, String authInfo, String index) throws ResponseException {
        index = index == null || "".equals(index) ? "*" : index;
        String state = HttpRequest.doGet(host +
                "/_cluster/state/metadata?indices=" + index, authInfo, "");

        String body = "{\"query\":{\"bool\":" +
                "{\"must\":[],\"must_not\":[]," +
                "\"should\":[{\"match_all\":{}}]}}," +
                "\"from\":0,\"size\":20,\"sort\":[]," +
                "\"aggs\":{},\"version\":true}";
        String search = HttpRequest.doGet(host + "/" + index + "/_search", authInfo, body);

        Map<String, JSONObject> map = new HashMap<>(2);
        map.put("state", JSON.parseObject(state));
        map.put("search", JSON.parseObject(search));

        String overview = JSON.toJSONString(map);
        LogManager.debug("Indices overview : " + StringUtil.truncate(overview, CommonEnum.LOGMAXLENGTH));
        return overview;
    }

    public String matchAll(String host, String authInfo) throws ResponseException {
        String body = "{\"query\":{\"bool\":" +
                "{\"must\":[],\"must_not\":[]," +
                "\"should\":[{\"match_all\":{}}]}}," +
                "\"from\":0,\"size\":20,\"sort\":[]," +
                "\"aggs\":{},\"version\":true}";
        String search = HttpRequest.doGet(host + "/_search?pretty", authInfo, body);
        return search;
    }

    //*************************** 基本查询 ***************************

    public String getMetadata(String host, String authInfo) throws ResponseException {
        String state = HttpRequest.doGet(host + "/_cluster/state/metadata", authInfo, "");
        String indexStats = HttpRequest.doGet(host + "/_stats/docs,store", authInfo, "");

        Map<String, JSONObject> map = new HashMap<>(2);
        map.put("state", JSON.parseObject(state));
        map.put("indexStats", JSON.parseObject(indexStats));

        String metadata = JSON.toJSONString(map);
        LogManager.debug("Indices metadata : " + StringUtil.truncate(metadata, CommonEnum.LOGMAXLENGTH));
        return metadata;
    }


    //*************************** 复合查询 ***************************

    public String complexQuery(String host, String authInfo, String route, String httpMethod, String body) throws ResponseException {
        String url = host + "/" + route;
        String search = null;
        switch (httpMethod) {
            case HttpEnum.GET:
                search = HttpRequest.doGet(url, authInfo, body);
                break;
            case HttpEnum.POST:
                search = HttpRequest.doPost(url, authInfo, body);
                break;
            case HttpEnum.PUT:
                search = HttpRequest.doPut(url, authInfo, body);
                break;
            case HttpEnum.DELETE:
                search = HttpRequest.doDelete(url, authInfo, body);
                break;
            default:
                break;
        }
        Map<String, JSONObject> map = new HashMap<>(1);
        map.put("search", JSON.parseObject(search));
        String result = JSON.toJSONString(map);
        return result;
    }

    //*************************** sql查询 ***************************

    public String getOneIndex(String host, String authInfo) throws ResponseException {
        LogManager.finer("Get one index start");

        String indices = HttpRequest.doGet(host +
                "/_cat/indices?format=json&h=index,status", authInfo, "");

        List<HashMap> list = JSON.parseArray(indices, HashMap.class);

        int len = list.size();
        int openCount = 0;

        LogManager.finer("Cluster indices lenth: " + len);

        for (int i = 0; i < len; i++) {
            HashMap map = list.get(i);
            String status = (String) map.get("status");
            if ("open".equals(status)) {
                openCount++;
            }
        }
        if (openCount <= 0) {
            throw new ResponseException("Cluster has no open index.");
        }
        String index = (String) list.get(0).get("index");
        return index;
    }

    //sql查询
    public String queryBySql (String host, String authInfo, String index, String sql) throws ResponseException {
        //String index = getOneIndex(host);
        String search = HttpRequest.doPost(host + "/_sql?index=" + index, authInfo, sql);
        //String search = HttpRequest.doPost(host + "/_sql", sql);
        Map<String, JSONObject> map = new HashMap<>(1);
        map.put("search", JSON.parseObject(search));
        String result = JSON.toJSONString(map);
        return result;
    }

    //sql解析
    public JSONObject sqlExplain (String host, String authInfo, String index, String sql) throws ResponseException {
        //String index = getOneIndex(host);
        String result = HttpRequest.doPost(host + "/_sql/_explain?index=" + index, authInfo, sql);
        //String result = HttpRequest.doPost(host + "/_sql/_explain", sql);
        return JSON.parseObject(result);
    }

    //*************************** 通用CRUD **************************

    /**
     * 字段查询，支持指定索引及类型
     * @param host
     * @param index
     *          ""
     *          *
     *          _all
     *          index
     *          inde*
     *          index1,index2
     *          index&type
     *          _all&type
     * @param body
     *          {"query":{"bool":{"must":[
     *          {"wildcard":{"content":"8*"}},{"wildcard":{"title":"8*"}}],
     *          "must_not":[],"should":[]}},
     *          "from":0,"size":50,"sort":[],
     *          "aggs":{},"version":true}
     * @return
     * @throws ResponseException
     */
    public String queryWithFields(String host, String authInfo, String index, String body) throws ResponseException {
        String search = HttpRequest.doGet(host + "/" + index + "/_search", authInfo, body);
        Map<String, JSONObject> map = new HashMap<>(1);
        map.put("search", JSON.parseObject(search));
        String result = JSON.toJSONString(map);
        LogManager.debug("Indices search : " + StringUtil.truncate(result, CommonEnum.LOGMAXLENGTH));
        return result;
    }

    /**
     * 通过文章id导出数据
     * @param host
     * @param docIds
     *          index1/type1/doc1, index2/type2/doc2
     * @throws ResponseException
     */
    public String query4DownloadById(String host, String authInfo, String docIds) throws ResponseException {
        String[] docIdArr = StringUtil.split(docIds, ",");
        if (docIdArr.length <= 0) {
            throw new ResponseException("Download failed, reason: no doc is selected.");
        }
        JSONArray array = new JSONArray();
        for (String docId : docIdArr) {
            String result = HttpRequest.doGet(host + "/" + docId, authInfo, "");
            array.add(JSONObject.parseObject(result));
        }
        return JSON.toJSONString(array, true);
    }

    public JSONArray query4Download(String host, String authInfo, String index, String body) throws ResponseException {
        String query = HttpRequest.doGet(host + "/" + index + "/_search?scroll=1m&pretty", authInfo, body);
        JsonParser parser = new JsonParser(query);
        String scroll_id = (String) parser.keys("_scroll_id");
        Object obj = parser.keys("hits.hits");
        if (JsonParser.assertJsonValueEmpty(obj)) {
            return null;
        }
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(obj));

        String result = null;
        String body2 = "{\n" +
                "    \"scroll\" : \"1m\", \n" +
                "    \"scroll_id\" : " + FormatStringUtil.toHttpBody(scroll_id) +
                "}";
        while (true) {
            result = HttpRequest.doGet(host + "/_search/scroll", authInfo, body2);
            JsonParser parser1 = new JsonParser(result);
            Object hits = parser1.keys("hits.hits");
            if (JsonParser.assertJsonValueEmpty(hits)) {
                break;
            } else {
                array.addAll(JSONArray.parseArray(JSON.toJSONString(hits)));
            }

        }
        LogManager.debug("Query for download  : " + StringUtil.truncate(result, CommonEnum.LOGMAXLENGTH));
        return array;
    }

    public JSONObject queryDoc(String host, String authInfo, String index, String type, String id, String routing) throws ResponseException {
        String url = host + "/" + index + "/" + type + "/" + id;
        if (!StringUtil.isEmpty(routing)) {
            url += "?routing=" + routing;
        }

        String search = HttpRequest.doGet(url, authInfo, "");

        return JSON.parseObject(search);
    }

    public String update(String host, String authInfo, String index,
                         String type, String id, String body) throws ResponseException {
        String update = HttpRequest.doPut(host + "/" + index + "/" +
                type + "/" + id + "?refresh", authInfo, body);
        return CommonEnum.SUCCESS;
    }

    public String bulkDeleteByDocIds(String host, String authInfo, String docIds) throws ResponseException {
        String[] docIdArr = StringUtil.split(docIds, ",");
        StringBuilder builder = new StringBuilder("");
        for (String docId : docIdArr) {
            builder.append("{\"delete\" : { \"_index\" :");
            String[] arr = StringUtil.split(docId, "/");
            builder.append(FormatStringUtil.toHttpBody(arr[0]));
            builder.append(", \"_type\" : ");
            builder.append(FormatStringUtil.toHttpBody(arr[1]));
            builder.append(", \"_id\" : ");
            builder.append(FormatStringUtil.toHttpBody(arr[2]));
            builder.append("} }\n");
        }
        String body = builder.toString();
        LogManager.finer(body);
        String result = HttpRequest.doPost(host + "/_bulk?refresh", authInfo, body);
        try {
            JsonParser parser = new JsonParser(result);
            Object errors = parser.keys("errors");
            if (!JsonParser.assertJsonValueEmpty(errors)) {
                boolean error = (boolean)errors;
                if (error) {
                    throw new ResponseException(result);
                }
                if (!error) {
                    //遍历查找删除失败的文档
                    Object items = parser.keys("items");
                    StringBuilder errorBuilder = new StringBuilder("[");
                    if (!JsonParser.assertJsonValueEmpty(items)) {
                        List<HashMap> list = JSON.parseArray(JSON.toJSONString(items), HashMap.class);
                        for (int i = 0; i < list.size() - 1; i++) {
                            HashMap map = list.get(i);
                            JSONObject delete = (JSONObject)map.get("delete");
                            JsonParser itemParser = new JsonParser(delete);
                            int status = (int)itemParser.keys("status");
                            if (200 == status) {
                                break;
                            }
                            errorBuilder.append(JSON.toJSONString(map));
                            errorBuilder.append(",");
                        }
                        HashMap map = list.get(list.size() - 1);
                        JSONObject delete = (JSONObject)map.get("delete");
                        JsonParser itemParser = new JsonParser(delete);
                        int status = (int)itemParser.keys("status");
                        if (200 != status) {
                            errorBuilder.append(JSON.toJSONString(map));
                            errorBuilder.append("]");
                        }
                        String errorDelete = errorBuilder.toString();
                        if (errorDelete.length() > 1) {
                            throw new ResponseException("failed:" + errorDelete);
                        }
                    }
                }
            }
        } catch (ClassCastException e) {
            LogManager.error("Bulk delete by docIds class cast failed.", e);
            throw new ResponseException(result);
        }
        return "Multi delete docs success.";
    }

    public String deleteByDocId(String host, String authInfo, String index,
                                String type, String id) throws ResponseException {
        String delete = HttpRequest.doDelete(host + "/" + index + "/" +
                type + "/" + id + "?refresh", authInfo, "");
        return CommonEnum.SUCCESS;
    }

    //TODO: deleteWithRouting
    public String deleteWithRouting() {
        return null;
    }


}
