package com.bonc.uni.usou.service.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.service.EnableInvoke;
import com.bonc.uni.usou.util.CommonEnum;
import com.bonc.uni.usou.util.FormatStringUtil;
import com.bonc.uni.usou.util.connection.HttpEnum;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.uni.usou.util.connection.HttpRequestUtil;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yedunyao on 2017/8/17.
 */
@Service("indexService")
public class IndexService implements EnableInvoke {

    @Autowired
    ClusterService clusterService;

    //***************************** index information ***********************

    //索引信息概览
    public String overview(String host, String authInfo) throws ResponseException {

        String indexStats = HttpRequest.doGet(host + "/_stats/docs,store", authInfo, "");
        String state = HttpRequest.doGet(host
                + "/_cluster/state/metadata,routing_table/", authInfo, "");


        Map<String, JSONObject> map = new HashMap<>(2);
        map.put("indexStats", JSON.parseObject(indexStats));
        map.put("state", JSON.parseObject(state));

        String overview = JSON.toJSONString(map);
        LogManager.debug("Index overview : " + StringUtil.truncate(overview, CommonEnum.LOGMAXLENGTH));

        return overview;
    }

    /**
     * 根据索引名称获取索引信息概览
     * @param host
     * @param indexName
     *          ""  查询所有
     *          name
     *          name1,name2
     *          n*e 模糊查询
     * @return
     * @throws ResponseException
     */
    public String overviewByName(String host, String authInfo, String indexName) throws ResponseException {

        String indexStats = HttpRequest.doGet(host + "/_stats/docs,store", authInfo, "");
        String state = HttpRequest.doGet(host
                + "/_cluster/state/metadata,routing_table/", authInfo, "");

        String filteredStats = filterIndexJsonByName(indexName, indexStats);

        JsonParser parser = new JsonParser(state);
        String routing_table = ((JSONObject) parser.keys("routing_table")).toJSONString();
        String metadata = ((JSONObject) parser.keys("metadata")).toJSONString();

        String filteredRouting = filterIndexJsonByName(indexName, routing_table);
        String filteredMetadata = filterIndexJsonByName(indexName, metadata);

        JSONObject stateJsonObject = JSON.parseObject(state);
        stateJsonObject.put("routing_table", JSON.parseObject(filteredRouting));
        stateJsonObject.put("metadata", JSON.parseObject(filteredMetadata));

        Map<String, JSONObject> map = new HashMap<>(3);
        map.put("state", stateJsonObject);
        map.put("indexStats", JSON.parseObject(filteredStats));

        String overview = JSON.toJSONString(map);

        LogManager.debug("Index overview : " + StringUtil.truncate(overview, CommonEnum.LOGMAXLENGTH));

        return overview;
    }

    /**
     * 创建忽略大小写的pattern
     * @param name
     * @return
     */
    public Pattern createPatternCaseInsensitive(String name) {
        String regEx = ".*" + name + ".*";
        Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        return pattern;
    }

    /**
     * 从cluster stats、cluster state的json串中过滤匹配name的节点
     * @param name
     * @param json
     * @return
     */
    public String filterIndexJsonByName(String name, String json) throws ResponseException {
        Pattern pattern = createPatternCaseInsensitive(name);

        JsonParser parser = new JsonParser(json);
        Object nodesObj = parser.keys("indices");

        if (JsonParser.assertJsonValueEmpty(nodesObj)) {
            throw new ResponseException("Can not find any indices.");
        }

        //将过滤获得的字符串重新拼成json
        StringBuilder builder = new StringBuilder("{\"indices\":{");

        parser.map("indices", (Map<String, JSONObject> map, JsonParser traversal) -> {
            if (map != null) {
                map.forEach((String key, JSONObject value) -> {
                    traversal.setSource(value);
                        Matcher matcher = pattern.matcher(key);
                        if (matcher.matches()) {
                            String indexName = FormatStringUtil.toHttpBody(key);
                            String state = JSONObject.toJSONString(value);
                            builder.append(indexName);
                            builder.append(":");
                            builder.append(state);
                            builder.append(",");
                        }
                });

                int index = builder.lastIndexOf(",");
                if (index > 0) {
                    builder.delete(index, index + 1);
                }
                builder.append("}");
                builder.append("}");
            }
        });

        String result = builder.toString();
        return result;
    }

    //获取所有索引
    public JSONArray getAllIndices(String host, String authInfo, String h) throws ResponseException {
        String indices = HttpRequest.doGet(host + "/_cat/indices?format=json&h=index,status" + h, authInfo, "");
        JSONArray jsonArray = JSON.parseArray(indices);
        String result = JSON.toJSONString(jsonArray);
        LogManager.debug("Get all indices : " + StringUtil.truncate(result, CommonEnum.LOGMAXLENGTH));
        return jsonArray;
    }


    /**
     * 查询指定的索引信息
     * @param host
     * @param index 索引名称
     */
    public String getIndexDetail(String host, String authInfo, String index) throws ResponseException {

        String health = HttpRequest.doGet(host + "/_cluster/health/" + index, authInfo, "");
        String stats = HttpRequest.doGet(host + "/" + index + "/_stats", authInfo, "");


        Map<String, JSONObject> map = new HashMap<>(2);
        map.put("health", JSON.parseObject(health));
        map.put("stats", JSON.parseObject(stats));

        String indexDetail = JSON.toJSONString(map);
        LogManager.finer("Index detail : " + StringUtil.truncate(indexDetail, CommonEnum.LOGMAXLENGTH));

        return indexDetail;
    }

    public String getIndexShards(String host, String authInfo, String index) throws ResponseException {
        String shards = HttpRequest.doGet(host + "/_cat/shards/" + index + "?format=json", authInfo, "");
        return shards;
    }

    //***************************** create、delete ***********************

    public String create(String host, String authInfo, String indexName, String body) throws ResponseException {
        String result = HttpRequest.doPut(host + "/" + indexName + "/", authInfo, body);
        return result;
    }

    //_all name name1,name2 n_m*
    public String delete(String host, String authInfo, String indexName) throws ResponseException {
        String result = HttpRequest.doDelete(host + "/" + indexName + "/", authInfo, "");
        return result;
    }

    //***************************** setting、mapping ***********************

    //获取索引的setting、mapping
    public String getIndex(String host, String authInfo, String indexName) throws ResponseException {
        String result = HttpRequest.doGet(host + "/" + indexName + "/", authInfo, "");
        return result;
    }

    //_all name name1,name2 n_m*
    public String getSetting(String host, String authInfo, String indexName) throws ResponseException {
        String result = HttpRequest.doGet(host + "/" + indexName + "/_settings", authInfo, "");
        return result;
    }

    /**
     * 获取可以修改的setting
     * 去除settings index中creation_date、number_of_shards、uuid、version、provided_name属性
     * @param setting
     * @return
     * @throws ResponseException
     */
    public String getRevisableSetting(String setting) throws ResponseException {

        JsonParser parser = null;
        try {
            parser = new JsonParser(setting);
        } catch (JSONException e) {
            throw new ResponseException("Request body is not json.");
        }
        JSONObject obj = (JSONObject)parser.keys("");
        //JSONObject settingsObj = (JSONObject)parser.keys("settings");
        JSONObject indexObj = (JSONObject)parser.keys("index");

        if (JsonParser.assertJsonValueEmpty(indexObj)) {
            throw new ResponseException("Request body missing \"settings.index\"");
        }

        obj.remove("index");

        indexObj.remove("creation_date");
        indexObj.remove("number_of_shards");
        indexObj.remove("uuid");
        indexObj.remove("version");
        indexObj.remove("provided_name");

        obj.put("index", indexObj);

        return JSON.toJSONString(obj);
    }

    public String updateSetting(String host, String authInfo, String indexName, String body) throws ResponseException {
        String revisableSetting = getRevisableSetting(body);
        String result = HttpRequest.doPut(host + "/" + indexName + "/_settings", authInfo, revisableSetting);
        return result;
    }

    public String updateMapping(String host, String authInfo, String indexName,
                                String type, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/" + indexName + "/_mapping/" + type, authInfo, body);
        return result;
    }

    //发送多次请求修改mapping
    public String multyUpdateMapping(String host, String authInfo, String indexName,
                                     String body) throws ResponseException {
        /*StringBuilder errorBuilder = new StringBuilder("{");
        StringBuilder reasonBuilder = new StringBuilder("[");
        int count = 0;*/
        StringBuilder reasonBuilder = new StringBuilder("");

        //如果mappings不是JSONObject则抛出异常
        JSONObject mappings = null;
        try {
            mappings = JSON.parseObject(body);
        } catch (JSONException e) {
            throw new ResponseException("Put mappings failed, reason: " + e.getMessage());
        } catch (ClassCastException e) {
            throw new ResponseException("Put mappings failed, reason: mapping is not JSONObject");
        }

        JsonParser jsonParser = new JsonParser(body);
        String[] types = jsonParser.getArrayKeys("mappings");
        for (String type : types) {
            Object obj = jsonParser.keys("mappings." + type);
            String prop = JSON.toJSONString(obj);
            String resStr = "";

            resStr = HttpRequestUtil.requestWithJson(HttpEnum.POST,
                    host + "/" + indexName + "/_mapping/" + type, prop);

            //如果返回结果为空，则表示集群出现异常
            if (StringUtil.isEmpty(resStr)) {
                throw new ResponseException("response is empty,maybe cluster can not be connected.");
            }

            //判断ES的返回结果是否是error
            JsonParser parser = new JsonParser(resStr);
            Object error = parser.keys("error");
            if (!JsonParser.assertJsonValueEmpty(error)) {
                String errorType = (String) parser.keys("error.type");
                String reason = (String) parser.keys("error.reason");
                //count++;
                reasonBuilder.append("{");
                reasonBuilder.append("\"type\":");
                reasonBuilder.append(FormatStringUtil.toHttpBody(type));
                reasonBuilder.append(", \"error\":");
                reasonBuilder.append(FormatStringUtil.toHttpBody(errorType));
                reasonBuilder.append(", \"reason\":");
                reasonBuilder.append(FormatStringUtil.toHttpBody(reason));
                //reasonBuilder.append("},");
                reasonBuilder.append("}");
                JSONObject jsonObject = JSON.parseObject(reasonBuilder.toString());
                throw new ResponseException(jsonObject.toString());
            }

        }
        return "Put mappings success.";
    }


    public String getMapping(String host, String authInfo, String indexName) throws ResponseException {
        String result = HttpRequest.doGet(host + "/" + indexName + "/_mapping", authInfo, "");
        return result;
    }

    //***************************** operate ***********************

    //multi name1,name2
    public String refresh(String host, String authInfo, String indexName) throws ResponseException {
        String result = HttpRequest.doPost(host + "/" + indexName + "/_refresh", authInfo, "");
        return result;
    }

    //5.5弃用
    public String optimize(String host, String authInfo, String indexName) throws ResponseException {

        String version = clusterService.getClusterVersion(host, authInfo);
        if (!"2.4.4".equals(version)) {
            throw new ResponseException("Index optimise is not supported by current version.");
        }

        String result = HttpRequest.doPost(host + "/" + indexName + "/_optimize", authInfo, "");
        return result;
    }

    //prepare shrink index
    public String prepareShrink(String host, String authInfo, String source, String nodeName) throws ResponseException {

        //判断索引健康状态
        //如果索引健康状态不是green，则无法压缩索引
        String health = HttpRequest.doGet("host" + "/_cluster/health/" + source, authInfo, "");
        JsonParser parser = new JsonParser(health);
        String status = (String) parser.keys("status");
        if (!"green".equals(status)) {
            throw new ResponseException("Can not shrink index, " +
                    "beacause index " + source + "'s health status is not green.");
        }

        //指定复制索引的node
        String body = "{\n" +
                "  \"settings\": {\n" +
                "    \"index.routing.allocation.require._name\": " +
                transValue(nodeName) + ", \n" +
                "    \"index.blocks.write\": true \n" +
                "  }" + "}";
        String result = HttpRequest.doPut(host + "/" + source + "/_settings", authInfo, body);
        return result;
    }

    /**
     *
     * @param host
     * @param source
     * @param nodeName
     * @param target
     * @param body
     *          "settings": {
    "index.number_of_replicas": 1,
    "index.number_of_shards": 1,
    "index.codec": "best_compression"
    },
    "aliases": {
    "my_search_indices": {}
    }
     * @return
     * @throws ResponseException
     */
    //shrink index
    public String shrink(String host, String authInfo, String source, String nodeName,
                         String target, String body) throws ResponseException {
        prepareShrink(host, authInfo, source, nodeName);

        String result = HttpRequest.doPost(host + "/" + source + "/_shrink/" + target + "/", authInfo, body);
        return result;
    }

    public String flush(String host, String authInfo, String indexName) throws ResponseException {
        String result = HttpRequest.doPost(host + "/" + indexName + "/_flush", authInfo, "");
        return result;
    }

    //_cache/clear
    public String clearCache(String host, String authInfo, String indexName) throws ResponseException {
        String result = HttpRequest.doPost(host + "/" + indexName + "/_cache/clear", authInfo, "");
        return result;
    }

    //close open
    public String close(String host, String authInfo, String indexName) throws ResponseException {
        String result = HttpRequest.doPost(host + "/" + indexName + "/_close", authInfo, "");
        return result;
    }

    public String open(String host, String authInfo, String indexName) throws ResponseException {
        String result = HttpRequest.doPost(host + "/" + indexName + "/_open", authInfo, "");
        return result;
    }

    //***************************** alias ***********************

    //添加别名
    public String addAlias(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_aliases", authInfo, body);
        return result;
    }

    public String getAliases(String host, String authInfo, String index) throws ResponseException {
        String result = HttpRequest.doGet(host + "/" + index + "/_aliases/", authInfo, "");
        return result;
    }

    public String removeAlias(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_aliases", authInfo, body);
        return result;
    }

    //************************* template ***********************

    //templates
    public String putTemplates(String host, String authInfo, String name, String body) throws ResponseException {
        String result = HttpRequest.doPut(host + "/_template/" + name, authInfo, body);
        return result;
    }

    ////_all name name1,name2 n_m*
    public String getTemplates(String host, String authInfo, String name) throws ResponseException {
        String result = HttpRequest.doGet(host + "/_template/" + name, authInfo, "");
        return result;
    }

    public String deleteTemplates(String host, String authInfo, String name) throws ResponseException {
        String result = HttpRequest.doDelete(host + "/_template/" + name, authInfo, "");
        return result;
    }


    //*********************** util ***********************

    /**
     *
     * @param value
     * @return  \"value"\   [\"value1,value2\"]
     */
    public static String transValue(String value) {
        String[] values = StringUtil.split(value, ",");
        StringBuilder builder = new StringBuilder();
        int length = values.length;
        if (length == 1) {
            builder.append("\"");
            builder.append(value);
            builder.append("\"");
        } else {
            builder.append("[\"");
            for (int i = 0; i < length - 1; i++) {
                builder.append(values[i]);
                builder.append("\", ");
            }
            builder.append(values[length - 1]);
            builder.append("\"]");
        }
        return builder.toString();
    }
}
