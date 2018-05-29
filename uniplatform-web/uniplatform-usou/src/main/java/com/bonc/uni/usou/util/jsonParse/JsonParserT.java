package com.bonc.uni.usou.util.jsonParse;

/**
 * Created by yedunyao on 2017/9/2.
 */

import com.bonc.usdp.odk.common.string.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

//TODO: fastjson将json转换成parseObject时，如果json中存在 "@" 符号，则会抛出JSONException

/**
 * Created by yedunyao on 2017/8/15.
 * 解析ES返回的json对象
 * getPrettyJson   支持输出pretty source
 * keys            支持获取简单或嵌套的属性或对象
 * getArrayKeys    支持返回json中指定对象中的数组的items名称
 * getArrayMaps    支持将json中指定对象中的数组转为map
 * map             支持对json中指定对象中的数组进行自定义遍历
 */
public class JsonParserT {

    ObjectMapper mapper = new ObjectMapper(); //转换器

    Map source;
    Map backup;

    public JsonParserT() {

    }

    public JsonParserT(Map source) {
        this.source = source;
        this.backup = this.source;
    }

    public JsonParserT(String source) {
        try {
            this.source = mapper.readValue(source, Map.class);
            this.backup = this.source;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map getSource() {
        return source;
    }

    public void setSource(Map source) {
        this.source = source;
        this.backup = this.source;
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
            return null;
        }
        Map obj = this.backup;

        String[] keys = StringUtil.split(key, ".");

        if(keys.length == 1) {
            //嵌套结束前恢复备份与json同步
            this.backup = this.source;
            Object result = obj.get(key);
            if (result == null || "{}".equals(result.toString())) {
                return "";
            }
            return result;
        } else {
            //返回结果 null {} 属性、对象、数组
            try {
                this.backup = (Map)this.backup.get(keys[0]);
            } catch (ClassCastException e) {
                return "";
            }

            String result = this.backup.toString();
            if (result == null || "{}".equals(result)) {
                return "";
            }

            key = StringUtil.substring(key, ".");
            return keys(key);
        }
    }

    public String[] getArrayKeys(String key) {
        Map obj = this.backup;
        if(!StringUtil.isEmpty(key)) {
            obj = (Map) keys(key);
        }

        if ("".equals(obj.toString())) {
            return null;
        }

        Set<String> keySet = obj.keySet();
        String[] keys = new String[keySet.size()];
        keys = keySet.toArray(keys);
        return keys;
    }

   /* public Map<String, JSONObject> getArrayMaps(String key) {
        JSONObject obj = this.backup;
        if(!StringUtil.isEmpty(key)) {
            obj = (JSONObject) keys(key);
        }

        if ("".equals(obj.toString())) {
            return null;
        }

        Map<String, JSONObject> map = new HashMap<>(obj.keySet().size());
        for(Object o : obj.keySet()) {
            JSONObject value = (JSONObject)obj.get(o);
            map.put(o.toString(), value);
        }
        return map;
    }*/

}
