package com.bonc.uni.usou.service.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.util.FormatStringUtil;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yedunyao on 2017/9/4.
 */
public class QueryServiceTest {

    static {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));
    }

    QueryService queryService = new QueryService();

    @Test
    public void testBulkDelete() throws Exception {
        String host = "http://172.16.3.56:30102";
        String docIds = "news-2017/news/5,news-2017/news/4";
        String result = queryService.bulkDeleteByDocIds(host, "", docIds);
        System.out.println(result);
    }

    @Test
    public void insert() throws Exception {
        String host = "http://172.16.3.54:19200/news-2017/news/5";
        String body = "{\n" +
                "\t\"title\" : \"共享宝马\",\n" +
                "\t\"content\" : \"辽宁市共享宝马车\",\n" +
                "\t\"news\" : \"今日头条\"\n" +
                "}";
        System.out.println(body);
        String result = HttpRequest.doPut(host, body);
        System.out.println(result);
    }

    @Test
    public void testBulk() throws Exception {
        String host = "http://172.16.3.56:30102/_bulk";
        String body = "{\"delete\" : " +
                "{ \"_index\" :\"news-2017\", \"_type\" : \"news\", \"_id\" : \"1\"} } \n";
        System.out.println(body);
        String result = HttpRequest.doPost(host, body);
        System.out.println(result);
        try {
            JsonParser parser = new JsonParser(result);
            Object errors = parser.keys("errors");
            if (!JsonParser.assertJsonValueEmpty(errors)) {
                boolean error = (boolean)errors;
                if (error) {
                    throw new ResponseException(result);
                }
                if (!error) {
                    System.out.println("error false");
                    //遍历查找删除失败的文档
                    Object items = parser.keys("items");
                    StringBuilder errorBuilder = new StringBuilder("[");
                    if (!JsonParser.assertJsonValueEmpty(items)) {
                        System.out.println("items");
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
                        System.out.println("================map" + map.toString());
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
                        System.out.println(errorDelete);
                    }
                }
            }
        } catch (ClassCastException e) {
            LogManager.error("Bulk delete by docIds class cast failed.", e);
            ResponseException responseException =  new ResponseException(result);
            responseException.addSuppressed(e);
            throw responseException;
        }

    }

    @Test
    public void testDownload() throws Exception {
        String host = "http://172.16.3.54:19200";
        String body = "{\"query\":{\"bool\":" +
                "{\"must\":[],\"must_not\":[]," +
                "\"should\":[{\"match_all\":{}}]}}," +
                "\"from\":0,\"size\":50,\"sort\":[],\"aggs\":{}," +
                "\"version\":true}";
        String result = queryService.queryWithFields(host, "", "_all", body);
        System.out.println(result);
        JsonParser jsonParser = new JsonParser(result);
        Object value = jsonParser.keys("search.hits.hits");
        //System.out.println("======================================" + (String)value);
        if (JsonParser.assertJsonValueEmpty(value)) {
            System.out.println("query hits nothing.");
        }
    }

    @Test
    public void testSql() throws Exception {
        String host = "http://172.16.3.56:30102/_sql/?index=news-2017";
        String payLoad = "select * from news";
        String index = "";
        String result = HttpRequest.doPost(host, payLoad);
        System.out.println(result);
    }

    @Test
    public void testSql1() throws Exception {
        /*String host = "http://172.16.3.56:30102";
        String payLoad = "select * from news-2017";
        String result = queryService.queryBySql(host, payLoad);
        System.out.println(result);*/
    }

    @Test
    public void testExplain() throws Exception {
        /*String host = "http://172.16.3.56:30102";
        String sql = "select * from news-2017";
        JSONObject result = queryService.sqlExplain(host, sql);
        System.out.println(JSON.toJSONString(result));*/
    }

    @Test
    public void testSendPayload() throws Exception {
        String host = "http://172.16.3.56:30102/_sql";
        String payLoad = "select * from news-2017";
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(host);
        //post.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        /*post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
        post.setHeader("Accept-Language","zh-cn,zh;q=0.8");
        post.setHeader("Accept-Encoding","gzip, deflate, sdch");*/
        post.setHeader("Content-Type","application/json; charset=UTF-8");
        /*post.setHeader("Host","www.lszfgjj.gov.cn");
        post.setHeader("Connection","keep-alive");
        post.setHeader("Referer","http://www.lszfgjj.gov.cn/GuestWebGjj/default.aspx");*/
        HttpEntity entity = new StringEntity(payLoad, "utf-8");
        post.setEntity(entity);
        response = client.execute(post);
        HttpEntity resEntity = response.getEntity();
        String resStr = EntityUtils.toString(resEntity, Charset.forName("utf-8"));
        System.out.println(resStr);
    }

    @Test
    public void getOneIndex() throws ResponseException {
        String host = "http://172.16.3.56:30102";
        String indices = HttpRequest.doGet(host +
                "/_cat/indices?format=json&h=index,status", "");
        System.out.println(indices);
        List<HashMap> list = JSON.parseArray(indices, HashMap.class);
        int len = list.size();
        System.out.println(len);
        int openCount = 0;
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
        System.out.println(list.get(0).get("index"));
    }

    @Test
    public void testQuertScroll() throws Exception {
        String host = "http://172.16.3.56:30102";
        String index = "_all";
        String body = "{\n" +
                "    \"size\": 10,\n" +
                "    \"query\": {\n" +
                "        \"match_all\" : {}\n" +
                "        }\n" +
                "    }\n" +
                "}";
        String query = HttpRequest.doGet(host + "/" + index + "/_search?scroll=1m&pretty", body);
        System.out.println(query);
        JsonParser parser = new JsonParser(query);
        String scroll_id = (String) parser.keys("_scroll_id");
        Object obj = parser.keys("hits.hits");
        if (JsonParser.assertJsonValueEmpty(obj)) {
            System.out.println("empty");
        }
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(obj));

        String result = null;
        String body2 = "{\n" +
                "    \"scroll\" : \"1m\", \n" +
                "    \"scroll_id\" : " + FormatStringUtil.toHttpBody(scroll_id) +
                "}";
        while (true) {
            result = HttpRequest.doGet(host + "/_search/scroll", body2);
            JsonParser parser1 = new JsonParser(result);
            Object hits = parser1.keys("hits.hits");
            //System.out.println(JSON.toJSONString(hits));
            if (JsonParser.assertJsonValueEmpty(hits)) {
                System.out.println("end");
                break;
            } else {
                array.addAll(JSONArray.parseArray(JSON.toJSONString(hits)));
            }

        }
        System.out.println(query);
        System.out.println(JSON.toJSONString(array));
    }

    @Test
    public void test() throws Exception {
        String host = "http://172.16.3.56:30102";
        String docIds = "news-2017/mynew/3,news-2017/mynew/1,news-2017/news/1";
        String array = queryService.query4DownloadById(host, "", docIds);
        System.out.println(array);
    }

}
