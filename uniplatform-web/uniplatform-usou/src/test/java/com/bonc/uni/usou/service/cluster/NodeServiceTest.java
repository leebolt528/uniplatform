package com.bonc.uni.usou.service.cluster;

import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.util.FormatStringUtil;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.apache.http.client.config.RequestConfig;
import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yedunyao on 2017/9/13.
 */
public class NodeServiceTest {

    static {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));
    }

    @Test
    public void testMatch() {
        String name = "s";
        String regEx = ".*" + name + ".*";
        Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher("stale");
        boolean b = matcher.matches();
        System.out.println(b);
    }

    @Test
    public void testGetNodeByName() throws Exception {

        String name = "10";
        String regEx = ".*" + name + ".*";
        Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);

        String host = "http://172.16.3.31:30200";

        String nodesStats = HttpRequest.doGet(host + "/_nodes/stats/jvm,fs,os,process", "");

        JsonParser statsParser = new JsonParser(nodesStats);
        Object nodesObj = statsParser.keys("nodes");

        if (JsonParser.assertJsonValueEmpty(nodesObj)) {
            throw new ResponseException("Can not find any node.");
        } else {

            StringBuilder builder = new StringBuilder("{\"nodes\":{");

            statsParser.map("nodes", (Map<String, JSONObject> map, JsonParser traversal) -> {
                System.out.println(map);
                if (map != null) {
                    map.forEach((String key, JSONObject value) -> {
                        traversal.setSource(value);
                        Object nameObj = traversal.keys("name");
                        if (!JsonParser.assertJsonValueEmpty(nameObj)) {
                            String targetName = (String)nameObj;
                            Matcher  matcher = pattern.matcher(targetName);
                            System.out.println(matcher.matches());
                            if (matcher.matches()) {
                                String nodeId = FormatStringUtil.toHttpBody(key);
                                String state = JSONObject.toJSONString(value);
                                builder.append(nodeId);
                                builder.append(":");
                                builder.append(state);
                                builder.append(",");
                            }
                        }

                    });
                    System.out.println(builder.toString());
                    int index = builder.lastIndexOf(",");
                    System.out.println(index);

                    if (index > 0) {
                        builder.delete(index, index + 1);
                    }
                    builder.append("}");
                    builder.append("}");
                }
            });

            String result = builder.toString();
            System.out.println(result);
        }
    }


    @Test
    public void getNodeStatsCount () throws Exception {
        //设置http请求超时时间为2s， 避免页面刷新慢
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(2 * 1000)
                .setConnectionRequestTimeout(2 * 1000)
                .setSocketTimeout(2 * 1000)
                .build();

        NodeService nodeService = new NodeService();
        JSONObject nodeStatsCount = nodeService.getNodeStatsCount("http://172.16.3.54:19200", "", requestConfig);
        System.out.println(nodeStatsCount);
    }

}
