package com.bonc.uni.usou.service.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yedunyao on 2017/8/15.
 */
public class ClusterServiceTest {

    static {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));
    }

    /**
     * json解析
     * 使用插件jsonFormat生成json对应的实体类
     * 使用fastjson将json封装成对象
     * @throws ResponseException
     */
    /*@Test
    public void getNodeState() throws ResponseException {
        String host = "http://172.16.3.54:19200";
        String result = HttpRequest.doGet(host + "/_nodes/stats/jvm,fs,os,process", "");
        System.out.println(result);

        Json node = JSONObject.parseObject(result, Json.class);
        System.out.println(node);
    }*/

    @Test
    public void getNodeState1() throws ResponseException {
        String host = "http://172.16.3.56:30200";
        String result = HttpRequest.doGet(host + "/_nodes/stats/jvm,fs,os,process", "");
        //System.out.println(result);

        JSONObject json = JSONObject.parseObject(result);
        System.out.println(JSONObject.toJSONString(json, true));
        JSONObject obj = json.getJSONObject("nodes");

        /*for (Object o : obj.keySet()) {
            System.out.println(o);
        }*/
        //System.out.println(obj);
        for (Object o : obj.values()) {
            System.out.println(o.toString());
        }
//        System.out.println(JSONObject.toJSONString(obj, true));
        //String name = obj.getJSONArray()

    }

    @Test
    public void getHealth() throws ResponseException {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("").getPath();
        LogManager.init(new Log4jTraceParameters("uniplatform", logManagerPath));
        String health = HttpRequest.doGet("http://172.16.3.56:30200" + "/_cluster/health", "");

        System.out.println(health);

        JSONObject JsonHealth = JSON.parseObject(health);

        Map<String, JSONObject> map = new HashMap<>(1);
        map.put("health", JsonHealth);


        String clusterDetail = JSON.toJSONString(map);
        System.out.println("******map*****" + clusterDetail);
    }

    @Test
    public void getNodes() throws Exception {
        String host = "http://172.16.3.56:30200";
        ClusterService clusterService = new ClusterService();
        JSONObject clusterNodes = clusterService.getClusterNodes(host, "");
        System.out.println(JSON.toJSONString(clusterNodes));
    }

    @Test
    public void getIndexStats() throws Exception {
        String host = "http://172.16.3.56:30200";
        ClusterService clusterService = new ClusterService();
        JSONObject indexStats = clusterService.getClusterStatic(host, "");
        System.out.println(JSON.toJSONString(indexStats));
    }

    @Test
    public void testGetRoles() throws Exception {
        String host = "http://172.16.3.56:30102";
        ClusterService clusterService = new ClusterService();
        Map<String, JSONArray> roles = clusterService.getRolesCrossESVersion(host, "");
        System.out.println(roles);
        String mergeRoles = clusterService.mergeRoles(host, "");
        System.out.println(mergeRoles);
    }

    @Test
    public void testGetClusterStatic () throws Exception {
        ClusterService clusterService = new ClusterService();
        JSONObject clusterStatic = clusterService.getClusterStatic("http://172.16.3.56:9200", "");
        System.out.println(clusterStatic);
    }

}
