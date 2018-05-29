package com.bonc.uni.common.util;

/**
 * 返回json封装
 * @author futao
 * 2017年8月30日
 * @param <T>
 */
public class ResponseResult<T> {

	/**
	 * 状态码
	 */
	private int code;
	
	/**
	 * 返回信息
	 */
	private String message;
	
	/**
	 * 返回数据
	 */
	private T data;
	
	/**
	 * 数据大小
	 */
	private long count;
	
	public ResponseResult() {
		super();
	}

	public ResponseResult(int code, String message, T data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public ResponseResult(int code, String message, T data, long count) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
		this.count = count;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
}
