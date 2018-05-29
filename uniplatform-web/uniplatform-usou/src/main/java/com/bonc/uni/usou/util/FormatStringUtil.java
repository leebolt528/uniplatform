package com.bonc.uni.usou.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.usou.util.jsonParse.JsonParser;
import com.bonc.usdp.odk.common.string.StringUtil;

/**
 * Created by yedunyao on 2017/8/29.
 */
public class FormatStringUtil {

    /**
     * 将value值转为http请求中body的json格式
     * @param value
     * @return  \"value"\   [\"value1,value2\"]
     */
    public static String toHttpBody(String value) {
        String[] values = StringUtil.split(value, ",");
        StringBuilder builder = new StringBuilder();
        int length = values.length;
        if (length == 1) {
            builder.append("\"");
            builder.append(value);
            builder.append("\"");
        } else {
            builder.append("[\"");
            for (int i = 0; i < length - 1; i++) {
                builder.append(values[i]);
                builder.append("\", ");
            }
            builder.append(values[length - 1]);
            builder.append("\"]");
        }
        return builder.toString();
    }


    //将index&type 转成index/type
    public static String parseToURL(String index, String pattern) {
        if (StringUtil.isEmpty(index)) {
            index = "_all";
        }
        int len = index.length();
        int pos = index.indexOf(pattern);
        if (pos == 0) {
            String indexName = "_all";
            String type = index.substring(pos + 1);
            index = indexName + "/" + type;
        } else if (pos > 0 && pos < (len - 1)) {
            String indexName = index.substring(0, pos);
            String type = index.substring(pos + 1);
            index = indexName + "/" + type;
        } else if (pos >= len -1) {
            index = index.substring(0, pos);
        }
        return index;
    }


    //将文件中的数据转变成CommonDic对于的json格式
    public static String parseToJsonCommonDic(String key, String value) {
    	String[] values = value.split("\\s+");
    	StringBuilder builder = new StringBuilder(key.replaceAll("}", ""));
    	int len  = values.length;
    	if (len == 1 && key.contains("\"dic\"")) {
    		builder.append(",word:");
	        builder.append(FormatStringUtil.toHttpBody(values[0]));
	        builder.append(",freq:\"\",nature:\"\"}");
	    } else if (key.contains("\"stop\"")) {
	        builder.append(",word:");
	        builder.append(FormatStringUtil.toHttpBody(values[0]));
	        builder.append(",freq:\"\",nature:\"\"}");
	    } else if (key.contains("\"synonyms\"")) {
	    	builder.append(",word:");
			String word="";
	    	for(String a  : values) {
	            word=word+a+",";
	        }
	    	word=word.substring(0, (word.length()-1));
	        builder.append("\""+word+"\"");
	        builder.append(",freq:\"\",nature:\"\"}");
	    } else if (len == 2 && key.contains("\"dic\"") ) {
	        builder.append(",word:");
	        builder.append(FormatStringUtil.toHttpBody(values[0]));
	        builder.append(",freq:");
	        builder.append(FormatStringUtil.toHttpBody(values[1]));
	        builder.append(",nature:\"\"}");
	    }  else if (len == 3 && key.contains("\"dic\"") ) {
	        builder.append(",word:");
	        builder.append(FormatStringUtil.toHttpBody(values[0]));
	        builder.append(",freq:");
	        builder.append(FormatStringUtil.toHttpBody(values[1]));
	        builder.append(",nature:");
	        builder.append(FormatStringUtil.toHttpBody(values[2]));
	        builder.append("}");
	    }
	    return builder.toString();
    }

    //过滤es查询返回结果的有效信息
    public static String filterQuery4EffectiveData (String query) {
        JSONObject jsonObject = JSON.parseObject(query);
        return filterQuery4EffectiveData(jsonObject);
    }

    //TODO:过滤获得查询后的有效数据
    public static String filterQuery4EffectiveData (JSONObject query) {
        JsonParser parser = new JsonParser(query);
        Object data = parser.keys("hits.hits");
        if ("".equals(data)) {
            return null;
        }
        return null;
    }

}
