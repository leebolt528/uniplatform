package com.bonc.uni.usou.service.cluster;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by yedunyao on 2017/8/31.
 */
/*@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = PortalsTest.class)
@WebAppConfiguration*/
public class AnalyzeServiceTest {

    static {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));
    }


    AnalyzeService service = new AnalyzeService();

    @Test
    public void testAnalyzeWithAnalyzer() throws Exception {
        String host = "http://172.16.3.54:19200";
        String analyzer = "whitespace";
        String text = "The quick brown fox.";
        JSONObject jsonObject = service.analyzeWithAnalyzer(host, "",  analyzer, text);
        System.out.println(JSONObject.toJSONString(jsonObject));
    }

    @Test
    public void testAnalyzeWithField() throws Exception {
        String host = "http://172.16.3.54:30200";
        String analyzer = "whitespace";
        String text = "The quick brown fox.";
        JSONObject jsonObject = service.analyzeWithField(host, "", "news-test1", "fl_content", text);
        System.out.println(JSONObject.toJSONString(jsonObject));
    }

    //========================== 词库管理 ==========================
    @Test
    public void dicList() throws Exception {
        JSONArray o = service.dicList("http://localhost:9200", "", "{type:\"dic\"}");
        System.out.println(o.toJSONString());
    }
    
    @Test
    public void dicAdd() throws Exception {
        String dicAdd = service.dicAdd("http://172.16.3.54:9203",  "","{type:\"dic\",dic:\"user_dic3\"}");
        System.out.println(dicAdd);
    }
    
  //========================== 词条管理 ==========================
    @Test
    public void wordBatchDelete() throws Exception {
//        String wordBatchDelete = service.wordBatchDelete("http://172.16.3.54:30200", "{type:\"synonyms\",dic:\"2018--update--client-synonyms\",word:\"人丛,人群,人海,人流,人潮#﻿众人,人人,人们\"}");
        String result2 = service.wordBatchDelete("http://172.16.3.54:30200",  "","{type:\"dic\",dic:\"2018--update--client-dic\",word:\"阿斯,美国\"}");
//        String wordBatchDelete = service.wordBatchDelete("http://172.16.3.54:9203", "{type:\"dic\",dic:\"user_dic\",word:\"这些\"}");
//        System.out.println(wordBatchDelete);
        System.out.println(result2);
    }

    @Test
    public void wordList() throws Exception {
        JSONObject jsonObject = service.wordList("http://172.16.3.54:30200", "", "{type:\"dic\",dic:\"2018--update--client-dic\",page:\"0\"}");
        System.out.println(jsonObject);
    }
    
    @Test
    public void wordSearch() throws Exception {
//    	JSONObject jsonObject = service.wordSearch("http://172.16.3.54:30200", "{type:\"synonyms\",dic:\"2018-1-21同义词测试\",word:\"我\"}");
    	JSONObject jsonObject = service.wordSearch("http://172.16.3.54:30200", "", "{type:\"dic\",dic:\"2018--update--client-dic\",word:\"阿三\"}");
    	
//    	JSONObject jsonObject = service.wordSearch("http://172.16.3.54:30200", "{type:\"stop\",dic:\"2018--update--client-stop\",word:\"一些\"}");
    	
//    	JSONObject jsonObject = service.wordSearch("http://172.16.3.54:30200", "{type:\"synonyms\",dic:\"2018--update--client-synonyms\",word:\"大\"}");
    	
//    	JSONObject jsonObject = service.wordSearch("http://172.16.3.54:30200", "{type:\"synonyms\",dic:\"2018-1-21同义词测试\",word:\"\"}");
        System.out.println(jsonObject);
    }

    @Test
    public void wordAdd() throws Exception {
//        String wordAdd = service.wordAdd("http://172.16.3.54:9203", "{type:\"dic\",dic:\"user_dic\",word:\"测试新增词\",freq:\"100\",nature:\"n\"}");
//    	String wordAdd = service.wordAdd("http://172.16.3.54:30200", "{type:\"stop\",dic:\"kkkkk\",word:\"a，an，the\",freq:\"\",nature:\"\"}");
//    	String wordAdd = service.wordAdd("http://172.16.3.54:30200", "{type:\"synonyms\",dic:\"test-dic026\",word:\"北京,帝都,皇城\",freq:\"\",nature:\"\"}");
    	String wordAdd = service.wordAdd("http://172.16.3.54:30200",  "","{type:\"synonyms\",dic:\"test-dic026\",word:\"北京,帝都,皇城\"}");
//    	String wordAdd = service.wordAdd("http://172.16.3.54:30200", "{type:\"synonyms\",dic:\"test-dic026\",word:\"北京，帝都，皇城\"}");
        System.out.println(wordAdd);
    }
    
    @Test
    public void wordDelete() throws Exception {
//        String wordDelete = service.wordDelete("http://172.16.3.54:9203", "{type:\"dic\",dic:\"user_dic\",word:\"测试新增词\"}");
    	String wordDelete = service.wordDelete
//    			("http://172.16.3.54:30200", "{type:\"synonyms\",dic:\"2018--update--client-synonyms\",word:\"我,咱,俺,余,吾,予,侬,咱家,本人,身,个人,人家,斯人\"}");
//    			("http://172.16.3.54:30200", "{type:\"dic\",dic:\"2018--update--client-dic\",word:\"中国\"}");
    	("http://172.16.3.54:30200",  "","{type:\"dic\",dic:\"2018--update--client-dic\",word:\"美国\"}");
        System.out.println(wordDelete);
    }

    @Test
    public void uploadWordFileDIC() throws Exception {
    	FileInputStream fileinput = new FileInputStream("C:/Users/lnovo/Desktop/dic_example_file.txt");
        String result = service.uploadWordFile("http://172.16.3.54:30200", "", "{type:\"dic\",dic:\"2018--update--client-dic\"}",fileinput);
        System.out.println(result);
    }
    
    @Test
    public void uploadWordFileSTOP() throws Exception {
    	FileInputStream fileinput = new FileInputStream("C:/Users/lnovo/Desktop/stop_example_file.txt");
        String result = service.uploadWordFile("http://172.16.3.54:30200", "", "{type:\"stop\",dic:\"2018--update--client-stop\"}",fileinput);
        System.out.println(result);
    }

    
    @Test
    public void uploadWordFileSYNONYMS() throws Exception {
    	FileInputStream fileinput = new FileInputStream("C:/Users/lnovo/Desktop/synonyms_example_file.txt");
        String result = service.uploadWordFile("http://172.16.3.54:30200",  "","{type:\"synonyms\",dic:\"2018--update--client-synonyms\"}",fileinput);
        System.out.println(result);
    }


}
