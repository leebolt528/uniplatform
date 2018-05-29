package com.bonc.uni.usou.util.connection;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.bonc.uni.usou.exception.ResponseException;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.apache.http.client.config.RequestConfig;

/**
 * Created by yedunyao on 2017/8/11.
 */
public class HttpRequest {

    /**
     * @param url 请求url
     * @return String response data
     */
    public static String doGet(String url, String authInfo, String body) throws ResponseException {
        String resStr = HttpRequestUtil.requestWithJson(HttpEnum.GET, url, authInfo, body);
        assertError4ES(resStr);
        return resStr;
    }

    public static String doGet(RequestConfig requestConfig, String url, String authInfo, String body) throws ResponseException {
        String resStr = HttpRequestUtil.requestWithJson(requestConfig, HttpEnum.GET, url, authInfo, body);
        assertError4ES(resStr);
        return resStr;
    }

    public static String doPost(String url, String authInfo, String body) throws ResponseException {
        String resStr = HttpRequestUtil.requestWithJson(HttpEnum.POST, url, authInfo, body);
        assertError4ES(resStr);
        return resStr;
    }


    public static String doPut(String url, String authInfo, String body) throws ResponseException {
        String resStr = HttpRequestUtil.requestWithJson(HttpEnum.PUT, url, authInfo, body);
        assertError4ES(resStr);
        return resStr;
    }

    public static String doDelete(String url, String authInfo, String body) throws ResponseException {
        String resStr = HttpRequestUtil.requestWithJson(HttpEnum.DELETE, url, authInfo, body);
        assertError4ES(resStr);
        return resStr;
    }

    public static String doGet(String url, String body) throws ResponseException {
        String resStr = HttpRequestUtil.requestWithJson(HttpEnum.GET, url, body);
        assertError4ES(resStr);
        return resStr;
    }

    public static String doGet(RequestConfig requestConfig, String url, String body) throws ResponseException {
        String resStr = HttpRequestUtil.requestWithJson(requestConfig, HttpEnum.GET, url, body);
        assertError4ES(resStr);
        return resStr;
    }

    public static String doPost(String url, String body) throws ResponseException {
        String resStr = HttpRequestUtil.requestWithJson(HttpEnum.POST, url, body);
        assertError4ES(resStr);
        return resStr;
    }


    public static String doPut(String url, String body) throws ResponseException {
        String resStr = HttpRequestUtil.requestWithJson(HttpEnum.PUT, url, body);
        assertError4ES(resStr);
        return resStr;
    }

    public static String doDelete(String url, String body) throws ResponseException {
        String resStr = HttpRequestUtil.requestWithJson(HttpEnum.DELETE, url, body);
        assertError4ES(resStr);
        return resStr;
    }

    private static void assertError4ES(String resStr) throws ResponseException {

        //如果返回结果为空，则表示集群出现异常
        if (StringUtil.isEmpty(resStr)) {
            throw new ResponseException("response is empty,maybe cluster can not be connected.");
        }

        //判断ES的返回结果是否是error
        try {
            JsonParser parser = new JsonParser(resStr);
            Object error = parser.keys("error");
            if (!JsonParser.assertJsonValueEmpty(error)) {
                String type = (String) parser.keys("error.type");
                String reason = (String) parser.keys("error.reason");
                String root_cause = ((JSONArray) parser.keys("error.root_cause")).toString();
                ResponseException e =  new ResponseException(
                        "ES response exception:" + type +
                        ", reason:" + reason + ", root_cause:" + root_cause);
                LogManager.Exception(e);
                throw e;
            }
        } catch (ClassCastException e) {
            LogManager.finer("Http respronse result is json array object");
            LogManager.Exception(e);
        } catch (JSONException e) {
            LogManager.finer("Http response is not json.");
            LogManager.Exception(e);
        }

    }

}
