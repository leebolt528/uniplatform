package com.bonc.uni.usou.util.jsonParse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author: yzt
 * @description:
 */
public class ResultToJson {

	public static void main(String[] args) {
		String result = "_id, _index, _type, sentence, \n" +
				"2, log, log, lsjfioafd,asjfosajfo,sdajoj, \n" +
				"1, log, log, now中国/n变得/v越来越/a强大/a，/c成为/v地球上/a东方/a的/a一极/n。/c,";
		ResultToJson rs = new ResultToJson();
		System.out.println(rs.resultTOJson(result));

	}

	public JSONObject resultTOJson(String resultSet) {
		String[] rows = resultSet.trim().split("\n");
		String[] heads = rows[0].split(",");
		if (heads.length != 4) {

			JSONObject json = new JSONObject();
			JSONArray list = new JSONArray();
			for (int i = 1; i < rows.length ; i++) {
				JSONObject row = new JSONObject();
				for (int j = 0; j < heads.length - 1; j++) {
					row.put(heads[j], rows[i].split(",")[j]);
				}
				list.add(row);
			}
			json.put("list", list);
			json.put("key", heads);

			return json;
		} else {
			return new JSONObject();
		}
	}
}
