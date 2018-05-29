package com.bonc.uni.usou.util.jsonParse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.usdp.odk.common.string.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by yedunyao on 2017/8/15.
 * 解析ES返回的json对象
 * getPrettyJson   支持输出pretty source
 * keys            支持获取简单或嵌套的属性或对象
 * getArrayKeys    支持返回json中指定对象中的数组的items名称
 * getArrayMaps    支持将json中指定对象中的数组转为map
 * map             支持对json中指定对象中的数组进行自定义遍历
 */

public class JsonParser {

    private JSONObject source;

    /**
     * 用于递归保存source
     * 为了避免多次JSONObject.parseObject反序列化JSONObject对象
     * 使用backup对source备份
     */
    private JSONObject backup;

    public JsonParser() {

    }

    public JsonParser(JSONObject source) {
        this.source = source;
        this.backup = this.source;
    }

    public JsonParser(String source) {
        this.source = JSONObject.parseObject(source);
        this.backup = this.source;
    }

    public JSONObject getSource() {
        return source;
    }

    public String getPrettySource() {
        return JSONObject.toJSONString(source, true);
    }

    public void setSource(JSONObject source) {
        this.source = source;
        this.backup = this.source;
    }

    public String getPrettyJson(JSONObject json) {
        return JSONObject.toJSONString(json, true);
    }

    //判断json属性的value值是否为空
    //string 、int、long、boolean、{} []
    public static boolean assertJsonValueEmpty(Object obj) {
        if (null == obj) {
            return true;
        }

        String result = "";

        if (obj.getClass() == JSONObject.class) {
            result = JSON.toJSONString(obj);
        } else if (obj.getClass() == JSONArray.class) {
            result = JSON.toJSONString(obj);
        } else {
            result = String.valueOf(obj);
        }

        result = result.replaceAll("[　*| *| *|//s*]*", "");

        if ("".equals(result) || "{}".equals(result) || "[]".equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * 获取简单或嵌套属性
     * @param key
     *          "obj"
     *          "prop"
     *          "obj.obj1"
     *          "obj.obj1.prop"
     * @return Object
     **/
    public Object keys(String key) {
        if(StringUtil.isEmpty(key)) {
            return this.backup;
        }
        JSONObject obj = this.backup;

        String[] keys = StringUtil.split(key, ".");

        if(keys.length == 1) {
            //嵌套结束前恢复备份与json同步
            this.backup = this.source;
            Object result = obj.get(key);
            if (assertJsonValueEmpty(result)) {
                return "";
            }
            return result;
        } else {
            //返回结果 null {} 属性、对象、数组
            this.backup = this.backup.getJSONObject(keys[0]);
            JSONObject result = this.backup;
            if (assertJsonValueEmpty(result)) {
                //嵌套结束前恢复备份与json同步
                this.backup = this.source;
                return "";
            }

            key = StringUtil.substring(key, ".");
            return keys(key);
        }
    }

    /**
     * 返回json中指定对象中的数组的items
     * @param key
     *          ""
     *          "obj"
     *          "prop"
     *          "obj.obj1"
     *          "obj.obj1.prop"
     * @return String[]
     **/
    public String[] getArrayKeys(String key) {
        JSONObject obj = this.backup;
        if(!StringUtil.isEmpty(key)) {
            Object tmp = keys(key);
            if (null == tmp || "".equals(tmp)) {
                return null;
            }
            obj = (JSONObject) keys(key);
        }

        if (assertJsonValueEmpty(obj)) {
            return null;
        }

        Set<String> keySet = obj.keySet();
        String[] keys = new String[keySet.size()];
        keys = keySet.toArray(keys);
        return keys;
    }

    public Map<String, JSONObject> getArrayMaps(String key) {
        JSONObject obj = this.backup;
        if(!StringUtil.isEmpty(key)) {
            obj = (JSONObject) keys(key);
        }

        if (assertJsonValueEmpty(obj)) {
            return null;
        }

        Map<String, JSONObject> map = new HashMap<>(obj.keySet().size());
        for(Object o : obj.keySet()) {
            JSONObject value = (JSONObject)obj.get(o);
            map.put(o.toString(), value);
        }
        return map;
    }

    public void map(String key, JsonArrayProcess process) {
        Map<String, JSONObject> map = getArrayMaps(key);
        //创建一个空的对象用于遍历，保持原有的JsonParser对象不变，使其可持续使用
        JsonParser traversal = new JsonParser();
        process.process(map, traversal);
    }
}
