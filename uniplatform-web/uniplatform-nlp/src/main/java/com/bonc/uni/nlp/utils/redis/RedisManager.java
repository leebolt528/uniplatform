package com.bonc.uni.nlp.utils.redis;

import com.bonc.usdp.odk.logmanager.LogManager;
import com.bonc.usdp.odk.redis.IRedisHandler;

/**
 * @Author Chris
 * @Description TODO
 * @Date 2017年12月28日 下午7:53:43
 */
public class RedisManager {

	private static RedisManager instance;

	private String deploy;

	private RedisManager() {
		LogManager.Method("Enter the method : constructor of RedisManager");
		
		com.bonc.usdp.odk.redis.RedisManager redisManager = com.bonc.usdp.odk.redis.RedisManager.getInstance();

		String password = RedisConfig.redisPassword;
		int maxTotal = Integer.parseInt(RedisConfig.redisPoolMaxTotal);
		int maxIdle = Integer.parseInt(RedisConfig.redisPoolMaxIdle);
		int minIdle = Integer.parseInt(RedisConfig.redisPoolMinIdle);
		long maxWaitMillis = Long.parseLong(RedisConfig.redisMaxWaitMillis);
		int connectionTimeout = Integer.parseInt(RedisConfig.redisConnectionTimeout);
		int timeout = Integer.parseInt(RedisConfig.redisTimeOut);
		long ttlMillis = Long.parseLong(RedisConfig.redisLockerTtlMillis);
		long lockerMaxWaitMillis = Long.parseLong(RedisConfig.redisLockerMaxWaitMillis);
		long perBlockMillis = Long.parseLong(RedisConfig.redisLockerPerBlockMillis);

		if (RedisConstant.STANDALONE.equalsIgnoreCase(RedisConfig.redisDeploy)) {
			deploy = RedisConstant.STANDALONE;
			String ip = RedisConfig.redisStandAloneIp;
			int port = Integer.parseInt(RedisConfig.redisStandAlonePort);
			redisManager.initStandaloneHandlerConfig(ip, port, password, maxTotal, maxIdle, minIdle, maxWaitMillis,
					connectionTimeout, timeout);
		} else {
			deploy = RedisConstant.DISTRIBUTED;
			String clusters = RedisConfig.redisDistributedClusters;
			int maxRedirections = Integer.parseInt(RedisConfig.redisMaxRedirections);
			redisManager.initClusterHandlerConfig(clusters, password, maxTotal, maxIdle, minIdle, maxWaitMillis,
					connectionTimeout, timeout, maxRedirections);
		}
		// 初始化redis同步锁设置
		redisManager.initMillisLockerConfig(ttlMillis, lockerMaxWaitMillis, perBlockMillis);
		// 设置redis同步锁key的前缀
		redisManager.setLockerPrefix("bonc.redisLocker.");
		LogManager.Method("Out from the method : constructor of RedisManager");
	}

	public static synchronized RedisManager getInstance() {
		if (instance == null) {
			instance = new RedisManager();
		}
		return instance;
	}

	public IRedisHandler getRedisHandler() {
		IRedisHandler redisHandler;
		if (deploy.equals(RedisConstant.STANDALONE)) {
			redisHandler = com.bonc.usdp.odk.redis.RedisManager.getInstance().getStandaloneHandler();
		} else {
			redisHandler = com.bonc.usdp.odk.redis.RedisManager.getInstance().getClusterHandler();
		}
		return redisHandler;
	}
}
