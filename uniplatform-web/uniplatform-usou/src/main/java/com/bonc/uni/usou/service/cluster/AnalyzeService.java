package com.bonc.uni.usou.service.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.response.Response;
import com.bonc.uni.usou.service.EnableInvoke;
import com.bonc.uni.usou.util.FormatStringUtil;
import com.bonc.uni.usou.util.connection.HttpRequest;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by yedunyao on 2017/8/29.
 */
@Service("analyzeService")
public class AnalyzeService implements EnableInvoke {


    //************************* 分词器测试 ********************

    public JSONObject analyzeWithAnalyzer(String host, String authInfo, String analyzer, String text)
            throws ResponseException {
        String body = "{\n" +
                "  \"analyzer\": " +
                FormatStringUtil.toHttpBody(analyzer) +
                ", \n" +
                "  \"text\":     " +
                FormatStringUtil.toHttpBody(text) +
                "}";
        String analyze = HttpRequest.doGet(host + "/_analyze", authInfo, body);
        return JSON.parseObject(analyze);
    }

    public JSONObject analyzeWithField(String host, String authInfo, String index,
                                   String field, String text) throws ResponseException {
        String body = "{\n" +
                "  \"field\": " +
                FormatStringUtil.toHttpBody(field) +
                ", \n" +
                "  \"text\":     " +
                FormatStringUtil.toHttpBody(text) +
                "}";
        String analyze = HttpRequest.doGet(host + "/" + index + "/_analyze", authInfo, body);
        return JSON.parseObject(analyze);
    }

    //========================== 词库管理 ==========================

    //添加词库
    // _ansj/api/dic/add -d '{type:"dic",dic:"user_dic"}'
    public String dicAdd(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_ansj/api/dic/add", authInfo, body);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LogManager.Exception(e);
        }
        return JSON.parseObject(result).getString("message");
    }

    //删除词库
    // _ansj/api/dic/delete -d '{type:"dic",dic:"user_dic"}'
    public String dicDelete(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_ansj/api/dic/delete", authInfo, body);
        JSONObject jsonObject = JSON.parseObject(result);
        String status = jsonObject.getString("status");

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LogManager.Exception(e);
        }

        if (Response.SUCCESS.equals(status)) {
            return "Delete dic success";
        }
        return "Faild to delete dic";
    }

    //更新词库
    // _ansj/api/dic/update -d '{type:"dic",dic:"user2_dic",update:"user_dic"}'
    public String dicUpdate(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_ansj/api/dic/update", authInfo, body);
        JSONObject jsonObject = JSON.parseObject(result);
        String status = jsonObject.getString("status");

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LogManager.Exception(e);
        }

        if (Response.SUCCESS.equals(status)) {
            return "Update dic success";
        }
        return "Faild to update dic";
    }

    //启用词库
    // _ansj/api/dic/enable -d '{type:"dic",dic:"user_dic"}'
    public String dicEnable(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_ansj/api/dic/enable", authInfo, body);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LogManager.Exception(e);
        }
        return JSON.parseObject(result).getString("message");
    }

    //禁用词库
    // _ansj/api/dic/disable -d '{type:"dic",dic:"user_dic"}'
    public String dicDisable(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_ansj/api/dic/disable", authInfo, body);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LogManager.Exception(e);
        }
        return JSON.parseObject(result).getString("message");
    }

    //获取词库列表
    // _ansj/api/dic/list -d '{type:"dic"}'
    public JSONArray dicList(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_ansj/api/dic/list", authInfo, body);
        return JSON.parseObject(result).getJSONArray("data");
    }

    //========================== 词管理 ==========================

    //添加词
    // _ansj/api/word/add -d '{type:"dic",dic:"user_dic",word:"上海上",freq:"100",nature:"n"}'
    public String wordAdd(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_ansj/api/word/add", authInfo, body);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LogManager.Exception(e);
        }
        return JSON.parseObject(result).getString("message");
    }

    //删除词
    // _ansj/api/word/delete -d '{type:"dic",dic:"user_dic",word:"北上以为"}'
    public String wordDelete(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_ansj/api/word/delete", authInfo, body);
        JSONObject jsonObject = JSON.parseObject(result);
        String status = jsonObject.getString("status");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LogManager.Exception(e);
        }
        if (Response.SUCCESS.equals(status)) {
            return "Delete word success";
        }
        return "Faild to delete word";
    }
    
    //批量删除词
    // _ansj/api/word/delete -d '{type:"dic",dic:"user_dic",word:"北上,以为,围场"}'
    public String wordBatchDelete(String host, String authInfo, String body) throws ResponseException {
	    	int wordBegin = body.lastIndexOf("word:")+6;
	    	int wordEnd = body.lastIndexOf("}")-1;
	    	String words = body.substring(wordBegin, wordEnd);
	    	String[] temp;
	    	if(!body.contains("type:\"synonyms\"")){
	    		temp = words.split(",");
	    	}else{
	    		temp = words.split("#");
	    	}
	    	int sum=0;
	    	String newbody="";
	    	for(int i = 0;i<temp.length;i++){
	    		newbody = body.substring(0, wordBegin) + temp[i] + body.substring(wordEnd,body.length());
	    		
	    		String result = HttpRequest.doPost(host + "/_ansj/api/word/delete", authInfo, newbody);
	    		newbody = "";
	            JSONObject jsonObject = JSON.parseObject(result);
	            String status = jsonObject.getString("status");
	            if (Response.SUCCESS.equals(status)) {
	            	sum++;
	            }
	    	}
	        try {
	            TimeUnit.SECONDS.sleep(1);
	        } catch (InterruptedException e) {
	            LogManager.Exception(e);
	        }
	        if (sum == temp.length) {
	            return "Delete word success";
	        }
	        return "Faild to delete word";
        
    }

    //搜索词
    // _ansj/api/word/search -d '{type:"dic",dic:"user_dic",word:"中国"}'
    public JSONObject wordSearch(String host, String authInfo, String body) throws ResponseException {
    	String result = HttpRequest.doPost(host + "/_ansj/api/word/search", authInfo, body);
    	if(body.contains("type:\"dic\"")){
	        String a = JSON.parseObject(result).getString("data");
	        JSONArray dataarray = JSON.parseArray(a);
	        JSONObject newdata=JSONObject.parseObject(result);
	        newdata.put("count", dataarray.size());
	        newdata.remove("data");
	        String[] datavalue=new String[dataarray.size()];
	        for(int i = 0 ; i < dataarray.size();i++){
	        	JSONObject oneword = JSONObject.parseObject(dataarray.get(i).toString());
	        	String word =oneword.get("word").toString()+","+oneword.get("nature").toString()+","+oneword.get("freq").toString();
	        	datavalue[i]=word;
	        }
	        newdata.put("data",datavalue);
	        return newdata;
    	}else{
    	    return JSONObject.parseObject(result);
    	}
    }

    //获取词列表
    // _ansj/api/word/list -d '{type:"dic",dic:"user_dic",page:"0"}'
    public JSONObject wordList(String host, String authInfo, String body) throws ResponseException {
        String result = HttpRequest.doPost(host + "/_ansj/api/word/list", authInfo, body);
        return JSONObject.parseObject(result);
    }
    
    //上传词文件
    //_ansj/api/word/list -d '{type:"dic",dic:"user_dic"}'
    public String uploadWordFile(String host, String authInfo, String body, FileInputStream inputStream) throws ResponseException {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            int size = inputStream.available();
            if (size <= 0) {
                throw new ResponseException("Failed to upload file,because file is empty");
            }
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            int count = 0;
            while((line = bufferedReader.readLine()) != null){
                if ("".equals(line.replace("\\s+", ""))) {
                    continue;
                }
                //将当前行转为DefaultDic对象
                String bodydata = FormatStringUtil.parseToJsonCommonDic(body, line);
                try {
                	wordAdd(host, authInfo, bodydata);
                } catch (ResponseException e) {
                    count++;
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                LogManager.Exception(e);
            }
            if (count > 0) {
                throw new ResponseException(count + " lines insert failed, because line(s) are not illegal.");
            }

        }  catch (UnsupportedEncodingException e) {
            LogManager.Exception(e);
            ResponseException responseException =
                    new ResponseException("Unsupported encoding, file's encoding should be UTF-8.");
            responseException.addSuppressed(e);
            throw responseException;
        } catch (IOException e) {
            LogManager.Exception(e);
            ResponseException responseException =
                    new ResponseException("Failed to read file.");
            responseException.addSuppressed(e);
            throw responseException;
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    LogManager.Exception(e);
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LogManager.Exception(e);
                }
            }
        }
        return "Upload file success.";
    }
    
    
}
