package com.bonc.uni.dcci.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 采集器配置文件解析
 * 
 * @author futao 2017年9月6日
 */
public class ConfiureParseUtil {

	/**
	 * 单位缩进字符串。
	 */
	public static String SPACE = "   ";
	public static String TAB = "\t";
	
	//starts.json begin
	public static String START_LIST = "start_list";
	public static String SITE_NAME = "site_name";
	public static String BOARD_NAME = "board_name";
	public static String CATEGORY = "category";
	public static String ENTRY_URL = "entry_url";
	public static String GB2312 = "GB2312";
	
	
	
	//starts.json end

	/**
	 * 返回格式化JSON字符串。
	 * 
	 * @param json
	 *            未格式化的JSON字符串。
	 * @return 格式化的JSON字符串。
	 */
	public static String formatJson(String json) {
		StringBuffer result = new StringBuffer();
		int length = json.length();
		int number = 0;
		char key = 0;
		for (int i = 0; i < length; i++) {
			key = json.charAt(i);
			if ((key == '[') || (key == '{')) {
				if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
					result.append('\n');
					result.append(indent(number, SPACE));
				}
				result.append(key);
				result.append('\n');
				number++;
				result.append(indent(number, SPACE));
				continue;
			}
			if ((key == ']') || (key == '}')) {
				result.append('\n');
				number--;
				result.append(indent(number, SPACE));
				result.append(key);
				if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
					result.append('\n');
				}
				continue;
			}
			if ((key == ',')) {
				result.append(key);
				result.append('\n');
				result.append(indent(number, SPACE));
				continue;
			}
			result.append(key);
		}
		return result.toString();
	}

	/**
	 * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
	 * 
	 * @param number
	 *            缩进次数。
	 * @return 指定缩进次数的字符串。
	 */
	private static String indent(int number, String space) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < number; i++) {
			result.append(space);
		}
		return result.toString();
	}

	/**
	 * 采集器 starts.json格式化
	 * @param json
	 * @return
	 */
	public static String formatStartsJson(String json) {
		StringBuffer result = new StringBuffer();

		int length = json.length();
		int number = 0;
		char key = 0;

		for (int i = 0; i < length; i++) {
			key = json.charAt(i);

			if ((key == '{')) {
				if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
					result.append('\n');
					result.append(indent(number, TAB));
				}

				result.append(key);

				result.append('\n');

				number++;
				result.append(indent(number, TAB));

				continue;
			}

			if ((key == '}')) {
				result.append('\n');

				number--;
				result.append(indent(number, TAB));

				result.append(key);

				if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
					result.append('\n');
				}

				continue;
			}

			if ((key == ',')) {
				result.append(key);
				result.append('\n');
				result.append(indent(number, TAB));
				continue;
			}
			
			if ((key == ':')) {
				result.append(key);
				result.append(TAB);
				continue;
			}

			result.append(key);
		}

		return result.toString();
	}

	public static void main(String args[]) {

		try {
			File file = new File("D://configtest.json");
			String configure = FileUtils.readFileToString(file, "UTF-8");
			JSONObject jsonConfig = JSONObject.fromObject(configure);
			System.out.println("-----------------------------------------------");
			System.out.println(formatStartsJson(jsonConfig.toString()));
			JSONArray jsonArray = jsonConfig.getJSONArray("start_list");
			for(int i=0 ; i < jsonArray.size();i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				System.out.println(jsonObject);
				String jsonStr = jsonObject.toString();
				JSONObject json = JSONObject.fromObject(jsonStr);
				System.out.println(json.getString("entry_url"));
				System.out.println("----------------------------------------------");
			}
			FileUtils.writeStringToFile(new File("D://configtest1111.json"), formatStartsJson(jsonConfig.toString()), "GB2312");
			//System.out.println(configure);
			/*JSONObject jsonConfig = JSONObject.parseObject(configure);
			String strs = JSONObject.toJSONString(jsonConfig, SerializerFeature.PrettyFormat);
			System.out.println(strs);
			System.out.println("-----------------------------------------------");
			System.out.println(formatStartsJson(jsonConfig.toJSONString()));
			JSONArray jsonArray = jsonConfig.getJSONArray("start_list");
			Iterator<Object> jsonIts = jsonArray.iterator();
			while (jsonIts.hasNext()) {
				JSONObject jsonObject = new JSONObject(true);
				jsonObject = (JSONObject) jsonIts.next();
				System.out.println(jsonObject);
			}*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
