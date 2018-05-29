package com.bonc.uni.usou;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.service.cluster.IndexService;
import com.bonc.uni.usou.service.cluster.QueryService;
import com.bonc.uni.usou.util.FormatStringUtil;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.CDL;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by yedunyao on 2017/8/11.
 */
public class Test {

    static {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));
    }

    @org.junit.Test
    public void testPath() {
        String s = Test.class.getClassLoader().getResource("").getPath();
        System.out.println(s);
        String s1 = ClassLoader.getSystemResource("").getPath();
        System.out.println(s1);
    }

    @org.junit.Test
    public void testLog() {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));
        LogManager.method("start test");
        LogManager.debug("debug");
        LogManager.Exception(new Exception("exception"));
    }

    @org.junit.Test
    public void testEnv() {
        /*Map<String, String> map = System.getenv();
        map.forEach((n, v) -> {
            System.out.println(n + "-->" + v);
        });*/
        String homePath = System.getenv("user.dir");
        System.out.println(homePath);
    }

    private static String getBaseHome() {

        String homePath = System.getenv("user.dir");
        System.out.println("===========================" + homePath);

        if (homePath == null || "".equals(homePath)) {
            homePath = Test.class.getResource("/").getPath();
        }

        return homePath;
    }

    @org.junit.Test
    public void testJsonArr() {
        String text = "[\"elasticsearch\",\"wow\"]";
        ObjectMapper mapper = new ObjectMapper(); //转换器
        try {
            Map m = mapper.readValue(text, Map.class);
            System.out.println(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testTohttpBody() {
        String value = "324";
        System.out.println(FormatStringUtil.toHttpBody(value));
    }

    @org.junit.Test
    public void testJson() {
        String text = "{\n" +
                "      \"task_name\": {\n" +
                "        \"null_value\": \"\",\n" +
                "        \"index\": \"not_analyzed\",\n" +
                "        \"type\": \"string\"\n" +
                "      },\n" +
                "      \"@source\": {\n" +
                "        \"index\": \"not_analyzed\",\n" +
                "        \"type\": \"string\"\n" +
                "      },\n" +
                "      \"keywords\": {\n" +
                "        \"null_value\": \"\",\n" +
                "        \"index\": \"not_analyzed\",\n" +
                "        \"type\": \"string\"\n" +
                "      },\n" +
                "      \"@type\": {\n" +
                "        \"index\": \"not_analyzed\",\n" +
                "        \"type\": \"string\"\n" +
                "      },\n" +
                "      \"author\": {\n" +
                "        \"null_value\": \"\",\n" +
                "        \"index\": \"not_analyzed\",\n" +
                "        \"type\": \"string\"\n" +
                "      }\n" +
                "     \n" +
                "    }";
//        JSON.parseObject(text);
        ObjectMapper mapper = new ObjectMapper(); //转换器
        try {
            Map m = mapper.readValue(text, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void logManager() {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));

    }



    public static String testException() {

        String num = null;
        try {
            String source = "{\n" +
                    "  \"cluster_name\" : \"elasticsearch\",\n" +
                    "  \"status\" : \"yellow\",\n" +
                    "  \"timed_out\" : false,\n" +
                    "  \"number_of_nodes\" : 1,\n" +
                    "  \"number_of_data_nodes\" : 1,\n" +
                    "  \"active_primary_shards\" : 25,\n" +
                    "  \"active_shards\" : 25,\n" +
                    "  \"relocating_shards\" : 0,\n" +
                    "  \"initializing_shards\" : 0,\n" +
                    "  \"unassigned_shards\" : 20,\n" +
                    "  \"delayed_unassigned_shards\" : 0,\n" +
                    "  \"number_of_pending_tasks\" : 0,\n" +
                    "  \"number_of_in_flight_fetch\" : 0,\n" +
                    "  \"task_max_waiting_in_queue_millis\" : 0,\n" +
                    "  \"active_shards_percent_as_number\" : 55.55555555555556\n" +
                    "}";
            JsonParser parser = new JsonParser(source);
            num = (String) parser.keys("number_of_data_nodes");
            System.out.println(num);
        } catch (ClassCastException e) {
            System.out.println("error");
        }

        return num;
    }

    @org.junit.Test
    public void testService() throws ResponseException {

        logManager();

        String[] urls = new String[]{"172.16.3.54:19200"};
        IndexService indexService = new IndexService();

        String methodName = "create";
        String indexName = "test1/test/1";
        String body = "{\n" +
                "\t\"title\" : \"共享宝马\",\n" +
                "\t\"content\" : \"辽宁市共享宝马车\",\n" +
                "\t\"news\" : \"今日头条\"\n" +
                "}";
        /*String r3 = ControllerUtil.pollingCluster(indexService, methodName, urls, indexName,body);
        System.out.println(r3);*/
    }

    @org.junit.Test
    public void testes() throws Exception {
        String host = "http://192.168.32.25:9200";
        String result = HttpRequest.doGet(host, "");
        System.out.println(result);
    }

    @org.junit.Test
    public void testGetDic() throws Exception {
        String host = "http://172.16.3.56:30102/_ansj/dic/get";
        String body = "{\"key\":\"dic\",\"page\":\"1\"}";
        String result = HttpRequest.doPost(host, body);
        System.out.println(result);
    }

    @org.junit.Test
    public void testInsert2() throws Exception {
        String host = "http://172.16.3.56:30102";
        String body = "{\"key\":\"dic\",\"keyword\":\"ceshi\",\"nature\":\"n\",\"freq\":\"3424\"}";
        String result = HttpRequest.doPost(host + "/_ansj/flush/dic/insert", body);
        System.out.println(result);
    }

    @org.junit.Test
    public void testInsert3() throws Exception {
        URL url = new URL("http://172.16.3.56:30102/_ansj/flush/dic/insert");
        String body = "{\"key\":\"dic\",\"keyword\":\"xie\",\"nature\":\"n\",\"freq\":\"3424\"}";
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Accept", "application/json");
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        OutputStreamWriter out = new OutputStreamWriter(
                httpCon.getOutputStream());
        out.write(body);
        out.flush();
        out.close();
        InputStream is = httpCon.getInputStream();
        StringBuilder sBuilder = new StringBuilder("");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String tempStr;
        while ((tempStr = br.readLine()) != null) {
            sBuilder.append(tempStr);
        }
        System.out.println(sBuilder.toString());
    }

    @org.junit.Test
    public void test2csv() throws Exception {
        QueryService service = new QueryService();
        String host = "http://172.16.3.54:19200";
        String body = "{\n" +
                "    \"from\" : 0, \"size\" : 10,\n" +
                "    \"query\" : {\n" +
                "        \"match_all\": {}\n" +
                "    }\n" +
                "}";
        String result = service.queryWithFields(host, "", "news-2017", body);
        System.out.println(result);
        JsonParser parser = new JsonParser(result);
        JSONArray hits = (JSONArray)parser.keys("search.hits.hits");
        System.out.println("hits==========================" + hits);
        System.out.println(CDL.toString(new org.json.JSONArray(JSON.toJSONString(hits))));
    }

    @org.junit.Test
    public void testMapping() throws Exception {
        String host = "http://localhost:9200/logs/chapter/1";
        String body = "{\n" +
                "\n" +
                "\t\"paragraph\" : [\n" +
                "\t\t{\n" +
                "\t\t\"sentence\" : [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"中国\",\n" +
                "\t\t\t\t\"prop\" : \"/n\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"变得\",\n" +
                "\t\t\t\t\"prop\" : \"/v\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"越来越\",\n" +
                "\t\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"强大\",\n" +
                "\t\t\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"，\",\n" +
                "\t\t\t\t\"prop\" : \"/c\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"成为\",\n" +
                "\t\t\t\t\"prop\" : \"/v\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"地球上\",\n" +
                "\t\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"东方\",\n" +
                "\t\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"的\",\n" +
                "\t\t\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"一极\",\n" +
                "\t\t\t\t\t\"prop\" : \"/n\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"。\",\n" +
                "\t\t\t\t\"prop\" : \"/c\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\"sentence\" : [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"东方国信\",\n" +
                "\t\t\t\t\"prop\" : \"/n\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"是\",\n" +
                "\t\t\t\t\"prop\" : \"/v\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"一家\",\n" +
                "\t\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"大数据\",\n" +
                "\t\t\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"公司\",\n" +
                "\t\t\t\t\"prop\" : \"/n\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"word\" : \"。\",\n" +
                "\t\t\t\t\"prop\" : \"/c\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
        String result = HttpRequest.doPut(host, body);
        System.out.println(result);
    }

    @org.junit.Test
    public void test1() {
       String[] arr = {null, null};
        System.out.println(arr == null);
        System.out.println(arr.length);
        System.out.println(Arrays.toString(arr));
    }
}
