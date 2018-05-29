package com.bonc.uni.usou.service.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yedunyao on 2017/8/25.
 */
public class IndexServiceTest {

    static {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));
    }

    IndexService indexService = new IndexService();

    @Test
    public void getIndexShards() throws ResponseException {

        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("").getPath();
        LogManager.init(new Log4jTraceParameters("uniplatform", logManagerPath));

        String host = "http://172.16.3.54:19200";
        String index = "logs";
        String shards = HttpRequest.doGet(host + "/_cat/shards/" + index + "?format=json", "");
        System.out.println(shards);

        //System.out.println(transCatTojson(shards));
    }

    /**
     * 将ES cat查询的结果转成json
     * @param value
     * @return
     */
    public static String transCatTojson(String value) {

        String[] arr = value.split("\n");
        int len = arr.length;

        if (len > 1) {
            List<Map<String, String>> list = new ArrayList<>(len - 1);

            String[] keys = arr[0].split("\\s+");
            int keysLen = keys.length;

            for (int i = 1; i < len; i++) {
                String[] values = arr[i].split("\\s+");

                //将属性及其对应的值放入到map中
                Map<String, String> map = new HashMap<>(keysLen);
                for (int j = 0; j < values.length; j++) {
                    map.put(keys[j], values[j]);
                }

                list.add(map);
            }
            System.out.println("list-->" + list);
            return JSON.toJSONString(list);
        }
        return null;
    }


    @org.junit.Test
    public void testGetAllIndices() throws Exception {
        String host = "http://172.16.3.54:19200";
        String indices = HttpRequest.doGet(host + "/_cat/indices?format=json&h=index,status", "");
        String result = "[{\"index\":\"logs\",\"status\":\"open\"},{\"index\":\"conch_crawl_v1\",\"status\":\"open\"}]";
        System.out.println(indices);
    }

    @Test
    public void testPutMapping() throws Exception {
        String host = "http://172.16.3.56:30102";
        String body = "{\n" +
                "\n" +
                "        \"type1\":{\n" +
                "            \"properties\":{\n" +
                "                \"hobby\":{\n" +
                "                    \"type\":\"keyword\"\n" +
                "                },\n" +
                "                \"id\":{\n" +
                "                    \"type\":\"integer\"\n" +
                "                },\n" +
                "                \"name\":{\n" +
                "                    \"type\":\"text\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"type2\":{\n" +
                "            \"properties\":{\n" +
                "                \"id\":{\n" +
                "                    \"type\":\"integer\"\n" +
                "                },\n" +
                "                \"name\":{\n" +
                "                    \"type\":\"text\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    \n" +
                "}";
        String result = HttpRequest.doPost(host + "/test3/_mapping/type1,type2", body);
        System.out.println(result);
    }

    @Test
    public void testRefresh() throws Exception {
        String host = "http://172.16.3.56:30102";
        String payLoad = "news-2017";
        IndexService indexService = new IndexService();
        String refresh = indexService.refresh(host, "", payLoad);
        System.out.println(refresh);
    }

    @Test
    public void testOptimize() throws Exception {
        String host = "http://172.16.3.56:30102";
        String payLoad = "news-2017";
        IndexService indexService = new IndexService();
        String refresh = indexService.optimize(host, "", payLoad);
        System.out.println(refresh);
    }

    @Test
    public void testGetSetting() throws Exception {
        String host = "http://172.16.3.56:30102";
        String index = "news-2017";
        String setting = indexService.getSetting(host, "", index);
        System.out.println(setting);
    }

    @Test
    public void testChangeableSettings() {
        String setting = "{\n" +
                "    \"settings\":{\n" +
                "        \"index\":{\n" +
                "            \"creation_date\":\"1504700639215\",\n" +
                "            \"number_of_shards\":\"5\",\n" +
                "            \"number_of_replicas\":\"0\",\n" +
                "            \"uuid\":\"6J9UAXuyTZWlx5RnSIzUAQ\",\n" +
                "            \"version\":{\n" +
                "                \"created\":\"5050099\"\n" +
                "            },\n" +
                "            \"provided_name\":\"geo\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        JsonParser parser = new JsonParser(setting);
        JSONObject obj = (JSONObject)parser.keys("");
        JSONObject settingsObj = (JSONObject)parser.keys("settings");
        JSONObject indexObj = (JSONObject)parser.keys("settings.index");

        obj.remove("settings");

        settingsObj.remove("index");

        indexObj.remove("creation_date");
        indexObj.remove("number_of_shards");
        indexObj.remove("uuid");
        indexObj.remove("version");
        indexObj.remove("provided_name");

        settingsObj.put("index", indexObj);

        obj.put("settings", settingsObj);

        System.out.println(JSON.toJSONString(obj));
    }

    @Test
    public void testMultyUpdateMapping() throws Exception {
        String host = "http://172.16.3.56:30102";
        String index = "log2";
        String body = "{\n" +
                "    \"mappings\":{\n" +
                "        \"log10\":{\n" +
                "            \"properties\":{\n" +
                "                \"sentence\":{\n" +
                "                    \"type\":\"string\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "\t\t\"log12\":{\n" +
                "            \"properties\":{\n" +
                "                \"sentence\":{\n" +
                "                    \"type\":\"text\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "\t\t\"log13\":{\n" +
                "            \"properties\":{\n" +
                "                \"sentence\":{\n" +
                "                    \"type\":\"text\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +

                "    }\n" +
                "}";
        String result = indexService.multyUpdateMapping(host, "", index, body);
        System.out.println(result);
    }

    @Test
    public void testJson() throws Exception {
        String body = "{\n" +
                "    \"mappings\":{\n" +
                "        \"log0\":{\n" +
                "            \"properties\":{\n" +
                "                \"sentence\":{\n" +
                "                    \"type\":\"text\"\n" +
                "                },\n" +
                "\t\t\t\t\"name\" : {\n" +
                "\t\t\t\t\t\"type\":\"text\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"value\" : {\n" +
                "\t\t\t\t\t\"type\":\"text\"\n" +
                "\t\t\t\t}\n" +
                "            }\n" +
                "        },\n" +
                "\t\t\"log4\":{\n" +
                "            \"properties\":{\n" +
                "                \"sentence\":{\n" +
                "                    \"type\":\"keyword\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "    }\n" +
                "}";

        JSONObject jsonObject = JSON.parseObject(body);
        System.out.println(jsonObject.toString());
    }

    @Test
    public void testJson2() throws Exception {
        String master = "master";
        String data = "data";
        JSONArray arr = new JSONArray();
        arr.add(master);
        arr.add(data);
        System.out.println(JSON.toJSONString(arr));
        JSONObject obj = new JSONObject();
        obj.put("roles", arr);
        System.out.println(obj.toString());
    }

    @Test
    public void testReSetting() throws Exception {
        String setting = "{\n" +
                //"    \"settings\":{\n" +
                "        \"index\":{\n" +
                "            \"creation_date\":\"1503887821850\",\n" +
                //"            \"number_of_shards\":\"5\",\n" +
               // "            \"number_of_replicas\":\"1\",\n" +
                //"            \"uuid\":\"97pkXPMdRySZEwzRWBwsnA\",\n" +
                "            \"version\":{\n" +
                "                \"created\":\"2040499\"\n" +
                "            }\n" +
                "        },\n" +
                "\"a\":{}" +
                //"    }\n" +
                "}";
        String result = indexService.getRevisableSetting(setting);
        System.out.println(result);
    }

    @Test
    public void testOverviewByName () throws Exception {
        String overviewByName = indexService.overviewByName("http://172.16.3.54:19200", "",
                " Test* ".trim());
        System.out.println(overviewByName);
    }

}
