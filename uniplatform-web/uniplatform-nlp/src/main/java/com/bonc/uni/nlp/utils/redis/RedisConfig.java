package com.bonc.uni.nlp.utils.redis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.bonc.uni.nlp.constant.SystemConstant;
import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @ClassName: RedisConfig
 * @Package: com.bonc.uni.nlp.config
 * @Description: redis配置类
 * @author: Chris
 * @date: 2017年12月28日 下午7:53:20
 */
public class RedisConfig {
	/**
	 * redis配置
	 */
	public static String redisDeploy;
	public static String redisStandAloneIp;
	public static String redisStandAlonePort;
	public static String redisDistributedClusters;
	public static String redisPassword = null;
	// redis参数
	public static String redisTimeOut;
	public static String redisConnectionTimeout;
	public static String redisMaxWaitMillis;
	public static String redisMaxRedirections;
	public static String redisPoolMaxTotal;
	public static String redisPoolMaxIdle;
	public static String redisPoolMinIdle;
	public static String redisLockerTtlMillis;
	public static String redisLockerMaxWaitMillis;
	public static String redisLockerPerBlockMillis;
	
	static{
		Properties  properties = new Properties();
		String configPath = null;
		try {
			configPath = PathUtil.getConfigPath() + File.separator + SystemConstant.APPLICATION;
		} catch (PathNotFoundException e) {
			LogManager.Exception(e);
		}
		try(InputStream input = new BufferedInputStream(new FileInputStream(configPath))) {
			properties.load(input);
			/**
			 * redis基本配置
			 */
			redisDeploy= properties.getProperty("bonc.redis.deploy");
			redisStandAloneIp=properties.getProperty("bonc.redis.standalone.ip");
			redisStandAlonePort=properties.getProperty("bonc.redis.standalone.port");
			redisDistributedClusters=properties.getProperty( "bonc.redis.distributed.clusters");
			/**
			 * redis密码
			 */
			String password = properties.getProperty("bonc.redis.password");
			redisPassword = StringUtil.isNotEmpty(password) ? password : redisPassword;
			redisTimeOut=properties.getProperty("bonc.redis.timeout", "2000");
			redisConnectionTimeout=properties.getProperty("bonc.redis.connectionTimeout", "2000");
			redisMaxWaitMillis=properties.getProperty("bonc.redis.maxWaitMillis", "2000");
			redisMaxRedirections=properties.getProperty("bonc.redis.maxRedirections", "5");
			redisPoolMaxTotal=properties.getProperty("bonc.redis.pool.maxTotal", "500");
			redisPoolMaxIdle=properties.getProperty("bonc.redis.pool.maxIdle", "200");
			redisPoolMinIdle=properties.getProperty("bonc.redis.pool.minIdle", "8");
			redisLockerTtlMillis=properties.getProperty("bonc.redis.locker.ttlMillis", "10000");
			redisLockerMaxWaitMillis=properties.getProperty("bonc.redis.locker.maxWaitMillis", "12000");
			redisLockerPerBlockMillis=properties.getProperty("bonc.redis.locker.perBlockMillis", "500");
			
		} catch (FileNotFoundException e) {
			LogManager.Exception("Can't find this properties file : ", e);
		} catch (IOException e) {
			LogManager.Exception(e);
		}
	}
	
}
