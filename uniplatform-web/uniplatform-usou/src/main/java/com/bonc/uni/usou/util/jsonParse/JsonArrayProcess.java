package com.bonc.uni.usou.util.jsonParse;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by yedunyao on 2017/8/16.
 */
public interface JsonArrayProcess {
    void process(Map<String, JSONObject> map, JsonParser traversal);
}
