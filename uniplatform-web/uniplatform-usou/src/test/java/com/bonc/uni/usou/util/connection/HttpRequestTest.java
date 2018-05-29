package com.bonc.uni.usou.util.connection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.junit.Test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yedunyao on 2017/8/14.
 */
public class HttpRequestTest {

    static{
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));
    }

    @Test
    public void insertData() throws Exception {
        String url = "http://172.16.3.54:19200/mail/documents/1";
        String body = "{\n" +
                "\t\"sentence\" : [\n" +
                "\t\t{\n" +
                "\t\t\t\"word\" : \"东方国信\",\n" +
                "\t\t\t\"prop\" : \"/n\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"word\" : \"是\",\n" +
                "\t\t\t\"prop\" : \"/v\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"word\" : \"一家\",\n" +
                "\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"word\" : \"来自\",\n" +
                "\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"word\" : \"东方\",\n" +
                "\t\t\t\"prop\" : \"/n\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"word\" : \"的\",\n" +
                "\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"word\" : \"大数据\",\n" +
                "\t\t\t\"prop\" : \"/a\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"word\" : \"公司\",\n" +
                "\t\t\t\"prop\" : \"/n\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
        String result = HttpRequest.doPut(url, "", body);
        System.out.println(result);
    }

    @Test
    public void insertData2() throws Exception {
        String url = "http://172.16.3.54:19200/logs/chapter/1";
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
        String result = HttpRequest.doPut(url, "", body);
        System.out.println(result);
    }

    @Test
    public void queryChapter() throws Exception {
        String url = "http://172.16.3.54:19200/logs/_search";
        String body = "";
        String result = HttpRequest.doGet(url, "", body);
        System.out.println(result);
    }

    @Test
    public void querySentence() throws Exception {
        String url = "http://172.16.3.54:19200/logs/_search";
        String body = "{\n" +
                "    \"_source\": \"paragraph.sentence\",\n" +
                "    \"query\" : {\n" +
                "        \"match\" : {\"word\" : \"的\" }\n" +
                "    }\n" +
                "}";
        String result = HttpRequest.doGet(url, "", body);
        System.out.println(result);
    }

    @Test
    public void queryParagraph() throws Exception {
        String url = "http://172.16.3.54:19200/logs/_search";
        String body = "{\n" +
                "    \"_source\": \"paragraph\",\n" +
                "    \"query\" : {\n" +
                "        \"match_all\" : { }\n" +
                "    }\n" +
                "}";
        String result = HttpRequest.doGet(url, "", body);
        System.out.println(result);
    }

    @Test
    public void query() throws Exception {
        String url = "http://172.16.3.54:19200/mail/_search";
        String body = "{\n" +
                "    \"_source\":\"sentence\",\n" +
                "    \"query\":{\n" +
                "        \"nested\":{\n" +
                "            \"path\":\"sentence\",\n" +
                "            \"query\":{\n" +
                "                \"match_all\":{\n" +
               // "                    \"sentence.word\":\"东方\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        String result = HttpRequest.doGet(url, "", body);
        System.out.println(result);
    }

    @Test
    public void parseQueryResult2Json() throws Exception {
        String url = "http://172.16.3.54:19200/_cluster/state/metadata?indices=*";
        String body = "";
        String result = HttpRequest.doGet(url, "", body);
        try {
        JSON.parseObject(result);

        }catch (JSONException e){
            String m = e.getMessage();
            System.out.println(m);
            Pattern compile = Pattern.compile("(\\d+)");
            Matcher matcher = compile.matcher(m);
//            boolean b = matcher.find();
            while (matcher.find()){
                System.out.println(matcher.group());
                System.out.println(result.substring(Integer.parseInt(matcher.group())-800
                        , Integer.parseInt(matcher.group())+70));
                System.out.println(matcher.group());
            }
//            System.out.println(b);
//            String s = matcher.group();
//            System.out.println(s);
        }
    }

    @Test
    public void matchAll() throws Exception {
        String url = "http://172.16.3.54:19200/_search";
        String body = "{\"query\":{\"match_all\":{}}}";
        String result = HttpRequest.doGet(url, "", body);
        System.out.println(result);
    }

    @Test
    public void delete() throws Exception {
        String url = "http://172.16.3.54:19200/news/new/1";
        String body = "";
        String result = HttpRequest.doDelete(url, "", body);
        System.out.println(result);
    }

    @Test
    public void getIndices() throws Exception {
        String url = "http://172.16.3.54:19200/_all";
        String body = "";
        String result = HttpRequest.doGet(url, "", body);
        System.out.println(result);
    }
}
