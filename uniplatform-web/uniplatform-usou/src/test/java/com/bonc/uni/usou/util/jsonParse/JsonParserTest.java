package com.bonc.uni.usou.util.jsonParse;

import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by yedunyao on 2017/8/15.
 */

public class JsonParserTest {
    JsonParser parser;

    static {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));
    }


    @Test
    public void test() {
        String key = "node.void.cha";
        key = StringUtil.substring(key, ".");
        System.out.println(key);
    }

    @Test
    public void key() throws ResponseException {
        String host = "http://172.16.3.54:19200";

        String state = HttpRequest.doGet(host
                + "/_cluster/state/master_node,routing_table,blocks/", "", "");


        parser = new JsonParser(state);
        Object name = parser.keys("blocks");
        if ("{}".equals(name.toString())) {
            System.out.println("空");
        }
        System.out.println(name);
        //System.out.println(parser.getPrettySource());
    }

    @Test
    public void getArrayMaps() throws ResponseException {
        String host = "http://172.16.3.56:30200";

        String nodes = HttpRequest.doGet(host + "/_nodes/_all/os,jvm", "", "");

        /*parser = new JsonParser(nodes);
        Map<String, JSONObject> map = parser.getArrayMaps("nodes");
        System.out.println(map.size());
        System.out.println(map);*/

    }

    @Test
    public void map() throws ResponseException {
        String host = "http://172.16.3.56:30200";
        String nodes = HttpRequest.doGet(host + "/_nodes/_all/os,jvm", "", "");
        System.out.println(nodes);
        parser = new JsonParser(nodes);
        System.out.println(parser.getPrettySource());

        parser.map("nodes", (Map<String, JSONObject> map, JsonParser traversal) -> {
            map.forEach((key, value) -> {
                traversal.setSource(value);
                Object res = traversal.keys("jvm");
                System.out.println(res);
            });
        });
    }

    @Test
    public void node() throws ResponseException {
        //List<Node> nodess;
        String host = "http://172.16.3.56:30200";
        String nodesStats = HttpRequest.doGet(host + "/_nodes/stats/jvm,fs,os,process", "", "");
        String nodes = HttpRequest.doGet(host + "/_nodes/_all/os,jvm", "", "");


        JsonParser nodesStatsParser = new JsonParser(nodesStats);
    }

    @Test
    public void map1() {

        String nodes = "{\"000Fi5lZSFGfEmPbWVM2zA\":{\n" +
                "\t\t\t\"build_hash\":\"260387d\",\n" +
                "\t\t\t\"host\":\"172.16.3.56\",\n" +
                "\t\t\t\"ip\":\"172.16.3.56\",\n" +
                "\t\t\t\"jvm\":{\n" +
                "\t\t\t\t\"gc_collectors\":[\n" +
                "\t\t\t\t\t\"ParNew\",\n" +
                "\t\t\t\t\t\"ConcurrentMarkSweep\"\n" +
                "\t\t\t\t],\n" +
                "\t\t\t\t\"mem\":{\n" +
                "\t\t\t\t\t\"direct_max_in_bytes\":33085390848,\n" +
                "\t\t\t\t\t\"heap_init_in_bytes\":33285996544,\n" +
                "\t\t\t\t\t\"heap_max_in_bytes\":33085390848,\n" +
                "\t\t\t\t\t\"non_heap_init_in_bytes\":2555904,\n" +
                "\t\t\t\t\t\"non_heap_max_in_bytes\":0\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t\"name\":\"hot_node_11\"\n" +
                "\t\t},\n" +
                "\t\t\"83-FjjUGQ1ab1cgQq4RCkg\":{\n" +
                "\t\t\t\"build_hash\":\"260387d\",\n" +
                "\t\t\t\"host\":\"172.16.3.54\",\n" +
                "\t\t\t\"ip\":\"172.16.3.54\",\n" +
                "\t\t\t\"jvm\":{\n" +
                "\t\t\t\t\"gc_collectors\":[\n" +
                "\t\t\t\t\t\"ParNew\",\n" +
                "\t\t\t\t\t\"ConcurrentMarkSweep\"\n" +
                "\t\t\t\t],\n" +
                "\t\t\t\t\"mem\":{\n" +
                "\t\t\t\t\t\"direct_max_in_bytes\":33085390848,\n" +
                "\t\t\t\t\t\"heap_init_in_bytes\":33285996544,\n" +
                "\t\t\t\t\t\"heap_max_in_bytes\":33085390848,\n" +
                "\t\t\t\t\t\"non_heap_init_in_bytes\":2555904,\n" +
                "\t\t\t\t\t\"non_heap_max_in_bytes\":0\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t\"name\":\"hot_node_00\"\n" +
                "\t\t}}";


        ObjectMapper mapper = new ObjectMapper(); //转换器
        try {
            Map m = mapper.readValue(nodes, Map.class);
            Map m1 = (Map) m.get("000Fi5lZSFGfEmPbWVM2zA");
            System.out.println(m1.get("jvm"));
            //System.out.println(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
