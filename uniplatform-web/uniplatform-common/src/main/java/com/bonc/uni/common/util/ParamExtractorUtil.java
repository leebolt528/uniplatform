package com.bonc.uni.common.util;

import com.bonc.usdp.odk.common.detector.RegexUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamExtractorUtil {

    /**
     * 从字符串中解析出 Map
     * @param paramStr 参数字符串
     * @param separator ksy 与 value 间的分隔符
     * @param surroundedByQuotes key 和 value 是否被引号包围 (true 被包围)
     * @return 解析结果
     */
    public static Map<String, Object> extractParamToMap(
            String paramStr,
            String separator,
            boolean surroundedByQuotes
    ) {
        Map<String, Object> paramMap = new HashMap<>();
        String regex = "(\\{[^}]*})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(paramStr);
        while (matcher.find()) {
            String keyValue = matcher.group();
            String [] keyValueArray = keyValue.substring(1, keyValue.length() - 1).split(separator);
            String key = keyValueArray[0];
            String value = keyValueArray[1];
            if (surroundedByQuotes) {
                paramMap.put(key.substring(1, key.length() - 1), value.substring(1, value.length() - 1));
            } else {
                paramMap.put(key, value);
            }
        }
        return paramMap;
    }

    /**
     * 从字符串中解析出 Map
     * @param paramStr 参数字符串，例如 <b> [{"key":"value},{"key":"value},...] </b>
     * @return 解析结果
     */
    public static Map<String, Object> extractParamToMap(String paramStr) {
        return extractParamToMap(paramStr, ":", true);
    }

    /**
     * 从字符串中解析出 List
     * @param paramStr 参数字符串 ["4", "5", "6"]
     * @return 解析结果
     */
    public static List<String> extractParamToList(String paramStr) {
        List<String> retValue = new LinkedList<>();
        String regex = "(\"[^\"]*\")";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(paramStr);
        while (matcher.find()) {
            String value = matcher.group();
            String val = value.substring(1, value.length() - 1);
            if (val.length() > 0) {
                retValue.add(val);
            }
        }
        return retValue;
    }

    /**
     * 将 String List 转为 Integer List
     * @param strList String List
     * @return LinkedList<Integer>
     */
    public static List<Integer> convertStringListToIntegerList(List<String> strList) {
        List<Integer> intList = new LinkedList<>();
        for (String str : strList) {
            if (RegexUtil.checkInteger(str)) {
                intList.add(Integer.parseInt(str));
            }
        }
        return intList;
    }

}
