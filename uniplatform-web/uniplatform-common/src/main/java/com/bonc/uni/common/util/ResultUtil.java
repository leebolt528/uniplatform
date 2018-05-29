package com.bonc.uni.common.util;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;


/**
 * 返回结果  工具类
 * @author futao
 * 2017年8月30日
 */
public class ResultUtil {
	
	public static final int SUCCESS = 1;
	public static final int ERROR = 2;
	public static final int FAILE = 3;

	
	
	/**
	 * 添加成功
	 * @return
	 */
	public static String addSuccess() {
		return result(SUCCESS,"添加成功",null,0);
	}
	
	/**
	 * 添加失败
	 * @return
	 */
	public static String addError() {
		return result(ERROR,"添加失败",null,0);
	}
	
	/**
	 * 修改成功
	 * @return
	 */
	public static String updSuccess() {
		return result(SUCCESS,"修改成功",null,0);
	}
	
	/**
	 * 修改失败
	 * @return
	 */
	public static String updError() {
		return result(ERROR,"修改失败",null,0);
	}
	
	/**
	 * 删除成功
	 * @return
	 */
	public static String delSuccess() {
		return result(SUCCESS,"删除成功",null,0);
	}
	
	/**
	 * 删除失败
	 * @return
	 */
	public static String delError() {
		return result(ERROR,"删除失败",null,0);
	}
	
	/**
	 * 获取成功
	 * @return
	 */
	public static String getSuccess(Object data) {
		return result(SUCCESS,"获取成功",data,0);
	}
	
	/**
	 * 获取失败
	 * @return
	 */
	public static String getError() {
		return result(ERROR,"获取失败",null,0);
	}
	
	/**
	 * 集合
	 * @param message
	 * @param data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String resultList(int code,String message,Object data) {
		long count = 0;
		if(null != data) {
			if(data instanceof List) {
				List lists = (List) data;
				count = lists.size();
			}
		}
		return result(code,message,data,count);
	}
	
	/**
	 * 数组
	 * @param message
	 * @param data
	 * @return
	 */
	private static String resultArrays(int code,String message,Object data) {
		long count = 0;
		if(null != data) {
			if(data.getClass().isArray()) {
				Object[] arrays = (Object[]) data;
				count = arrays.length;
			}
		}
		return result(code,message,data,count);
	}
	
	/**
	 * map
	 * @param message
	 * @param data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String resultMap(int code,String message,Object data) {
		long count = 0;
		if(null != data) {
			if(data instanceof Map) {
				Map maps = (Map) data;
				count = maps.size();
			}
		}
		return result(code,message,data,count);
	}
	
	/**
	 * 成功类  包含数组 集合 map
	 * @param message
	 * @param data
	 * @param count
	 * @return
	 */
	public static String success(String message,Object data,long count) {
		return result(SUCCESS,message,data,count);
	}
	
	public static String success(String message,Object data) {
		return result(SUCCESS,message,data,0);
	}
	
	/**
	 * 成功类  
	 * @param message
	 * @param data
	 * @param count
	 * @return
	 */
	public static String successList(String message,Object data) {
		return resultList(SUCCESS,message,data);
	}
	
	/**
	 * 成功类  
	 * @param message
	 * @param data
	 * @param count
	 * @return
	 */
	public static String successMap(String message,Object data) {
		return resultMap(SUCCESS,message,data);
	}
	
	/**
	 * 成功类  
	 * @param message
	 * @param data
	 * @param count
	 * @return
	 */
	public static String successArrays(String message,Object data) {
		return resultArrays(SUCCESS,message,data);
	}
	
	/**错误 包含数组 集合 map
	 * @param message
	 * @param data
	 * @param count
	 * @return
	 */
	public static String error(String message,Object data) {
		return result(ERROR,message,data,0);
	}

	//增加 SerializerFeature.DisableCircularReferenceDetect，解决FastJson中“$ref 循环引用”的问题
	public static String result(int code,String message,Object data,long count) {
		ResponseResult<Object> ss = new ResponseResult<Object>(code,message,data,count);
		return JSON.toJSONString(ss,SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteNullListAsEmpty,SerializerFeature.WriteNullNumberAsZero,SerializerFeature.WriteNullBooleanAsFalse);
	}
	
}
