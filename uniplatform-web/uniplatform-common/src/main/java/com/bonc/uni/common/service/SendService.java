package com.bonc.uni.common.service;


/**
 * 发送预警接口
 * @author futao
 * 2017年9月1日
 */
public interface SendService {

	/**
	 * 发送邮件
	 * @param 
	 */
	void push(String[] receivers, String subject, String msg);
}
